package org.dolicoli.android.golfscoreboard.tasks;

import java.util.ArrayList;

import org.dolicoli.android.golfscoreboard.data.OneGame;
import org.dolicoli.android.golfscoreboard.data.settings.GameSetting;
import org.dolicoli.android.golfscoreboard.data.settings.PlayerSetting;
import org.dolicoli.android.golfscoreboard.data.settings.Result;
import org.dolicoli.android.golfscoreboard.db.GameSettingDatabaseWorker;

import android.content.Context;
import android.os.AsyncTask;

public class CurrentGameQueryTask extends AsyncTask<Void, Void, OneGame> {

	private Context context;
	private TaskListener listener;

	public CurrentGameQueryTask(Context context, TaskListener listener) {
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
	protected OneGame doInBackground(Void... params) {
		if (context == null) {
			return null;
		}

		GameSetting gameSetting = new GameSetting();
		PlayerSetting playerSetting = new PlayerSetting();
		ArrayList<Result> results = new ArrayList<Result>();

		GameSettingDatabaseWorker gameSettingWorker = new GameSettingDatabaseWorker(
				context);
		gameSettingWorker.getGameSetting(gameSetting, playerSetting, results);

		return new OneGame(gameSetting, playerSetting, results);
	}

	public static interface TaskListener {
		void onCurrentGameQueryStarted();

		void onCurrentGameQueryFinished(OneGame gameResult);
	}
}
