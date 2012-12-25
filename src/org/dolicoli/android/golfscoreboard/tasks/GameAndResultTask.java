package org.dolicoli.android.golfscoreboard.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.dolicoli.android.golfscoreboard.data.GameAndResult;
import org.dolicoli.android.golfscoreboard.data.settings.GameSetting;
import org.dolicoli.android.golfscoreboard.data.settings.PlayerSetting;
import org.dolicoli.android.golfscoreboard.data.settings.Result;
import org.dolicoli.android.golfscoreboard.db.HistoryGameSettingDatabaseWorker;
import org.dolicoli.android.golfscoreboard.db.HistoryPlayerSettingDatabaseWorker;
import org.dolicoli.android.golfscoreboard.db.HistoryResultDatabaseWorker;
import org.dolicoli.android.golfscoreboard.utils.DateRangeUtil.DateRange;

import android.content.Context;
import android.os.AsyncTask;

public class GameAndResultTask extends
		AsyncTask<DateRange, Void, GameAndResult[]> {

	private static final GameAndResult[] NULL_RESULT = new GameAndResult[0];
	private Context context;
	private GameAndResultTaskListener listener;

	public GameAndResultTask(Context context, GameAndResultTaskListener listener) {
		this.context = context;
		this.listener = listener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (listener != null) {
			listener.onGameAndResultStarted();
		}
	}

	@Override
	protected void onPostExecute(GameAndResult[] results) {
		super.onPostExecute(results);
		if (listener != null) {
			listener.onGameAndResultFinished(results);
		}
	}

	@Override
	protected GameAndResult[] doInBackground(DateRange... dateRanges) {
		if (context == null || dateRanges == null) {
			return NULL_RESULT;
		}

		if (dateRanges.length < 1)
			return NULL_RESULT;

		HistoryGameSettingDatabaseWorker historyGameWorker = new HistoryGameSettingDatabaseWorker(
				context);
		ArrayList<GameSetting> games = new ArrayList<GameSetting>();

		historyGameWorker.getGameSettings(dateRanges[0].getFrom(),
				dateRanges[0].getTo(), games);

		HashMap<String, PlayerSetting> playerSettingMap = new HashMap<String, PlayerSetting>();
		HashMap<String, ArrayList<Result>> resultsMap = new HashMap<String, ArrayList<Result>>();

		HistoryPlayerSettingDatabaseWorker historyPlayerWorker = new HistoryPlayerSettingDatabaseWorker(
				context);
		HistoryResultDatabaseWorker historyResultWorker = new HistoryResultDatabaseWorker(
				context);

		ArrayList<GameAndResult> gameAndResultList = new ArrayList<GameAndResult>();
		for (GameSetting game : games) {
			String playDate = game.getPlayDate();

			PlayerSetting playerSetting = new PlayerSetting();
			historyPlayerWorker.getPlayerSetting(playDate, playerSetting);
			playerSettingMap.put(playDate, playerSetting);

			ArrayList<Result> results = new ArrayList<Result>();
			results = historyResultWorker.getResults(playDate);
			resultsMap.put(playDate, results);

			GameAndResult gameAndResult = new GameAndResult(game,
					playerSetting, results);
			gameAndResultList.add(gameAndResult);
		}

		Collections.sort(gameAndResultList);

		GameAndResult[] results = new GameAndResult[gameAndResultList.size()];
		gameAndResultList.toArray(results);
		return results;
	}

	public static interface GameAndResultTaskListener {
		void onGameAndResultStarted();

		void onGameAndResultFinished(GameAndResult[] results);
	}
}
