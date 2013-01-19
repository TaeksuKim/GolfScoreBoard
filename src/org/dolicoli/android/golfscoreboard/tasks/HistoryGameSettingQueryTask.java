package org.dolicoli.android.golfscoreboard.tasks;

import org.dolicoli.android.golfscoreboard.data.OneGame;
import org.dolicoli.android.golfscoreboard.data.settings.GameSetting;
import org.dolicoli.android.golfscoreboard.data.settings.PlayerSetting;
import org.dolicoli.android.golfscoreboard.db.HistoryGameSettingDatabaseWorker;

import android.content.Context;
import android.os.AsyncTask;

public class HistoryGameSettingQueryTask extends
		AsyncTask<String, Void, OneGame> {

	private Context context;
	private TaskListener listener;

	public HistoryGameSettingQueryTask(Context context, TaskListener listener) {
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
	protected void onPostExecute(OneGame result) {
		super.onPostExecute(result);
		if (listener != null) {
			listener.onGameQueryFinished(result);
		}
	}

	@Override
	protected OneGame doInBackground(String... params) {
		if (context == null || params == null || params.length < 1) {
			return null;
		}

		String playDate = params[0];

		GameSetting gameSetting = new GameSetting();
		PlayerSetting playerSetting = new PlayerSetting();

		HistoryGameSettingDatabaseWorker historyGameWorker = new HistoryGameSettingDatabaseWorker(
				context);
		historyGameWorker.getGameSetting(playDate, gameSetting, playerSetting);

		OneGame result = new OneGame(gameSetting, playerSetting);
		return result;
	}

	public static interface TaskListener {
		void onGameQueryStarted();

		void onGameQueryFinished(OneGame gameResult);
	}
}
