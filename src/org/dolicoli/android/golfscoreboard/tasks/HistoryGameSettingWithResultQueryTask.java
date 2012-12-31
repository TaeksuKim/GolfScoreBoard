package org.dolicoli.android.golfscoreboard.tasks;

import java.util.ArrayList;

import org.dolicoli.android.golfscoreboard.Constants;
import org.dolicoli.android.golfscoreboard.data.SingleGameResult;
import org.dolicoli.android.golfscoreboard.data.UsedHandicap;
import org.dolicoli.android.golfscoreboard.data.settings.GameSetting;
import org.dolicoli.android.golfscoreboard.data.settings.PlayerSetting;
import org.dolicoli.android.golfscoreboard.data.settings.Result;
import org.dolicoli.android.golfscoreboard.db.HistoryGameSettingDatabaseWorker;
import org.dolicoli.android.golfscoreboard.db.HistoryResultDatabaseWorker;

import android.content.Context;
import android.os.AsyncTask;

public class HistoryGameSettingWithResultQueryTask
		extends
		AsyncTask<HistoryGameSettingWithResultQueryTask.QueryParam, Void, SingleGameResult> {

	private Context context;
	private TaskListener listener;

	public HistoryGameSettingWithResultQueryTask(Context context, TaskListener listener) {
		this.context = context;
		this.listener = listener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (listener != null) {
			listener.onCurrentGameQueryStarted();
		}
	}

	@Override
	protected void onPostExecute(SingleGameResult result) {
		super.onPostExecute(result);
		if (listener != null) {
			listener.onCurrentGameQueryFinished(result);
		}
	}

	@Override
	protected SingleGameResult doInBackground(QueryParam... params) {
		if (context == null || params == null || params.length < 1) {
			return null;
		}

		String playDate = params[0].playDate;
		int maxHoleNumber = params[0].holeNumber;

		GameSetting gameSetting = new GameSetting();
		PlayerSetting playerSetting = new PlayerSetting();
		ArrayList<Result> results = new ArrayList<Result>();
		UsedHandicap usedHandicap = null;

		HistoryGameSettingDatabaseWorker gameSettingWorker = new HistoryGameSettingDatabaseWorker(
				context);
		gameSettingWorker.getGameSettingWithResult(playDate, gameSetting,
				playerSetting, results, maxHoleNumber);

		HistoryResultDatabaseWorker resultWorker = new HistoryResultDatabaseWorker(
				context);
		usedHandicap = resultWorker.getUsedHandicaps(playDate);

		SingleGameResult result = new SingleGameResult();
		result.setGameSetting(gameSetting);
		result.setPlayerSetting(playerSetting);
		result.setResults(results);
		result.setUsedHandicap(usedHandicap);

		return result;
	}

	public static class QueryParam {
		private String playDate;
		private int holeNumber;

		public QueryParam(String playDate) {
			this(playDate, Constants.WHOLE_HOLE);
		}

		public QueryParam(String playDate, int holeNumber) {
			this.playDate = playDate;
			this.holeNumber = holeNumber;
		}
	}

	public static interface TaskListener {
		void onCurrentGameQueryStarted();

		void onCurrentGameQueryFinished(SingleGameResult gameResult);
	}
}
