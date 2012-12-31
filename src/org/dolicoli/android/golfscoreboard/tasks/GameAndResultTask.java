package org.dolicoli.android.golfscoreboard.tasks;

import java.util.ArrayList;
import java.util.Collections;

import org.dolicoli.android.golfscoreboard.data.GameAndResult;
import org.dolicoli.android.golfscoreboard.db.HistoryGameSettingDatabaseWorker;
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
		ArrayList<GameAndResult> gameAndResults = new ArrayList<GameAndResult>();
		historyGameWorker.getGameSettingsWithResult(dateRanges[0].getFrom(),
				dateRanges[0].getTo(), gameAndResults);

		Collections.sort(gameAndResults);

		GameAndResult[] results = new GameAndResult[gameAndResults.size()];
		gameAndResults.toArray(results);
		return results;
	}

	public static interface GameAndResultTaskListener {
		void onGameAndResultStarted();

		void onGameAndResultFinished(GameAndResult[] results);
	}
}
