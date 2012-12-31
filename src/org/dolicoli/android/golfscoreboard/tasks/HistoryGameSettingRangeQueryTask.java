package org.dolicoli.android.golfscoreboard.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.dolicoli.android.golfscoreboard.data.GameAndResult;
import org.dolicoli.android.golfscoreboard.db.HistoryGameSettingDatabaseWorker;
import org.dolicoli.android.golfscoreboard.utils.DateRangeUtil.DateRange;

import android.content.Context;
import android.os.AsyncTask;

public class HistoryGameSettingRangeQueryTask extends
		AsyncTask<DateRange, Void, GameAndResult[]> {

	private static final GameAndResult[] NULL_RESULTS = new GameAndResult[0];

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
	protected void onPostExecute(GameAndResult[] results) {
		super.onPostExecute(results);
		if (listener != null) {
			listener.onHistoryGameSettingQueryFinished(results);
		}
	}

	@Override
	protected GameAndResult[] doInBackground(DateRange... params) {
		if (context == null || params == null || params.length < 1) {
			return NULL_RESULTS;
		}

		DateRange dateRange = params[0];

		ArrayList<GameAndResult> results = new ArrayList<GameAndResult>();

		HistoryGameSettingDatabaseWorker historyGameWorker = new HistoryGameSettingDatabaseWorker(
				context);
		historyGameWorker.getGameSettingsWithResult(dateRange.getFrom(),
				dateRange.getTo(), results);

		Collections.sort(results, new Comparator<GameAndResult>() {
			@Override
			public int compare(GameAndResult lhs, GameAndResult rhs) {
				return rhs.getDate().compareTo(lhs.getDate());
			}
		});

		GameAndResult[] array = new GameAndResult[results.size()];
		results.toArray(array);
		return array;
	}

	public static interface TaskListener {
		void onHistoryGameSettingQueryStarted();

		void onHistoryGameSettingQueryFinished(GameAndResult[] gameResults);
	}
}
