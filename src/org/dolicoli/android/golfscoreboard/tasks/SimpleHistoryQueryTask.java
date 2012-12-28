package org.dolicoli.android.golfscoreboard.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.dolicoli.android.golfscoreboard.data.SingleGameResult;
import org.dolicoli.android.golfscoreboard.data.settings.GameSetting;
import org.dolicoli.android.golfscoreboard.data.settings.PlayerSetting;
import org.dolicoli.android.golfscoreboard.db.HistoryGameSettingDatabaseWorker;
import org.dolicoli.android.golfscoreboard.db.HistoryPlayerSettingDatabaseWorker;
import org.dolicoli.android.golfscoreboard.utils.DateRangeUtil.DateRange;

import android.content.Context;
import android.os.AsyncTask;

public class SimpleHistoryQueryTask extends
		AsyncTask<DateRange, Void, SingleGameResult[]> {

	private static final SingleGameResult[] NULL_RESULTS = new SingleGameResult[0];

	private Context context;
	private TaskListener listener;

	public SimpleHistoryQueryTask(Context context, TaskListener listener) {
		this.context = context;
		this.listener = listener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (listener != null) {
			listener.onSimpleHistoryQueryStarted();
		}
	}

	@Override
	protected void onPostExecute(SingleGameResult[] results) {
		super.onPostExecute(results);
		if (listener != null) {
			listener.onSimpleHistoryQueryFinished(results);
		}
	}

	@Override
	protected SingleGameResult[] doInBackground(DateRange... params) {
		if (context == null || params == null || params.length < 1) {
			return NULL_RESULTS;
		}

		DateRange dateRange = params[0];

		ArrayList<SingleGameResult> results = new ArrayList<SingleGameResult>();

		ArrayList<GameSetting> gameSettings = new ArrayList<GameSetting>();
		PlayerSetting playerSetting = null;

		HistoryGameSettingDatabaseWorker historyGameWorker = new HistoryGameSettingDatabaseWorker(
				context);
		historyGameWorker.getGameSettings(dateRange.getFrom(),
				dateRange.getTo(), gameSettings);

		HistoryPlayerSettingDatabaseWorker playerWorker = new HistoryPlayerSettingDatabaseWorker(
				context);
		for (GameSetting gameSetting : gameSettings) {
			playerSetting = new PlayerSetting();
			playerWorker.getPlayerSetting(gameSetting.getPlayDate(),
					playerSetting);

			SingleGameResult result = new SingleGameResult();
			result.setGameSetting(gameSetting);
			result.setPlayerSetting(playerSetting);

			results.add(result);
		}
		results.trimToSize();

		Collections.sort(results, new Comparator<SingleGameResult>() {
			@Override
			public int compare(SingleGameResult lhs, SingleGameResult rhs) {
				return rhs.getDate().compareTo(lhs.getDate());
			}
		});

		SingleGameResult[] array = new SingleGameResult[results.size()];
		results.toArray(array);
		return array;
	}

	public static interface TaskListener {
		void onSimpleHistoryQueryStarted();

		void onSimpleHistoryQueryFinished(SingleGameResult[] gameResults);
	}
}
