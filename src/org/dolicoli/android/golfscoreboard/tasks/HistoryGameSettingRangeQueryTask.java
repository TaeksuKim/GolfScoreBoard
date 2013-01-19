package org.dolicoli.android.golfscoreboard.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.dolicoli.android.golfscoreboard.data.OneGame;
import org.dolicoli.android.golfscoreboard.db.HistoryGameSettingDatabaseWorker;
import org.dolicoli.android.golfscoreboard.utils.DateRangeUtil.DateRange;

import android.content.Context;
import android.os.AsyncTask;

public class HistoryGameSettingRangeQueryTask extends
		AsyncTask<DateRange, Void, OneGame[]> {

	private static final OneGame[] NULL_RESULTS = new OneGame[0];

	private Context context;
	private TaskListener listener;

	public HistoryGameSettingRangeQueryTask(Context context,
			TaskListener listener) {
		this.context = context;
		this.listener = listener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (listener != null) {
			listener.onHistoryGameSettingQueryStarted();
		}
	}

	@Override
	protected void onPostExecute(OneGame[] results) {
		super.onPostExecute(results);
		if (listener != null) {
			listener.onHistoryGameSettingQueryFinished(results);
		}
	}

	@Override
	protected OneGame[] doInBackground(DateRange... params) {
		if (context == null || params == null || params.length < 1) {
			return NULL_RESULTS;
		}

		DateRange dateRange = params[0];

		ArrayList<OneGame> results = new ArrayList<OneGame>();

		HistoryGameSettingDatabaseWorker historyGameWorker = new HistoryGameSettingDatabaseWorker(
				context);
		historyGameWorker.getGameSettingsWithResult(dateRange.getFrom(),
				dateRange.getTo(), results);

		Collections.sort(results, new Comparator<OneGame>() {
			@Override
			public int compare(OneGame lhs, OneGame rhs) {
				return rhs.getDate().compareTo(lhs.getDate());
			}
		});

		OneGame[] array = new OneGame[results.size()];
		results.toArray(array);
		return array;
	}

	public static interface TaskListener {
		void onHistoryGameSettingQueryStarted();

		void onHistoryGameSettingQueryFinished(OneGame[] gameResults);
	}
}
