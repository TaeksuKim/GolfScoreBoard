package org.dolicoli.android.golfscoreboard.tasks;

import java.util.ArrayList;

import org.dolicoli.android.golfscoreboard.Constants;
import org.dolicoli.android.golfscoreboard.data.OneGame;
import org.dolicoli.android.golfscoreboard.data.settings.GameSetting;
import org.dolicoli.android.golfscoreboard.data.settings.PlayerSetting;
import org.dolicoli.android.golfscoreboard.data.settings.Result;
import org.dolicoli.android.golfscoreboard.db.HistoryGameSettingDatabaseWorker;

import android.content.Context;
import android.os.AsyncTask;

public class HistoryGameSettingWithResultQueryTask
		extends
		AsyncTask<HistoryGameSettingWithResultQueryTask.QueryParam, Void, OneGame> {

	private Context context;
	private TaskListener listener;

	public HistoryGameSettingWithResultQueryTask(Context context,
			TaskListener listener) {
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
	protected void onPostExecute(OneGame result) {
		super.onPostExecute(result);
		if (listener != null) {
			listener.onCurrentGameQueryFinished(result);
		}
	}

	@Override
	protected OneGame doInBackground(QueryParam... params) {
		if (context == null || params == null || params.length < 1) {
			return null;
		}

		String playDate = params[0].playDate;
		int maxHoleNumber = params[0].holeNumber;

		GameSetting gameSetting = new GameSetting();
		PlayerSetting playerSetting = new PlayerSetting();
		ArrayList<Result> results = new ArrayList<Result>();

		HistoryGameSettingDatabaseWorker gameSettingWorker = new HistoryGameSettingDatabaseWorker(
				context);
		gameSettingWorker.getGameSettingWithResult(playDate, gameSetting,
				playerSetting, results, maxHoleNumber);

		return new OneGame(gameSetting, playerSetting, results);
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

		void onCurrentGameQueryFinished(OneGame gameResult);
	}
}
