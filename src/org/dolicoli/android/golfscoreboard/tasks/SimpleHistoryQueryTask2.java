package org.dolicoli.android.golfscoreboard.tasks;

import org.dolicoli.android.golfscoreboard.data.SingleGameResult;
import org.dolicoli.android.golfscoreboard.data.settings.GameSetting;
import org.dolicoli.android.golfscoreboard.data.settings.PlayerSetting;
import org.dolicoli.android.golfscoreboard.db.HistoryGameSettingDatabaseWorker;
import org.dolicoli.android.golfscoreboard.db.HistoryPlayerSettingDatabaseWorker;

import android.content.Context;
import android.os.AsyncTask;

public class SimpleHistoryQueryTask2 extends
		AsyncTask<String, Void, SingleGameResult> {

	private Context context;
	private TaskListener listener;

	public SimpleHistoryQueryTask2(Context context, TaskListener listener) {
		this.context = context;
		this.listener = listener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (listener != null) {
			listener.onGameQueryStarted();
		}
	}

	@Override
	protected void onPostExecute(SingleGameResult result) {
		super.onPostExecute(result);
		if (listener != null) {
			listener.onGameQueryFinished(result);
		}
	}

	@Override
	protected SingleGameResult doInBackground(String... params) {
		if (context == null || params == null || params.length < 1) {
			return null;
		}

		String playDate = params[0];

		GameSetting gameSetting = new GameSetting();
		PlayerSetting playerSetting = null;

		HistoryGameSettingDatabaseWorker historyGameWorker = new HistoryGameSettingDatabaseWorker(
				context);
		historyGameWorker.getGameSetting(playDate, gameSetting);

		HistoryPlayerSettingDatabaseWorker playerWorker = new HistoryPlayerSettingDatabaseWorker(
				context);
		playerSetting = new PlayerSetting();
		playerWorker.getPlayerSetting(gameSetting.getPlayDate(), playerSetting);

		SingleGameResult result = new SingleGameResult();
		result.setGameSetting(gameSetting);
		result.setPlayerSetting(playerSetting);

		return result;
	}

	public static interface TaskListener {
		void onGameQueryStarted();

		void onGameQueryFinished(SingleGameResult gameResult);
	}
}
