package org.dolicoli.android.golfscoreboard.tasks;

import java.util.ArrayList;

import org.dolicoli.android.golfscoreboard.GolfScoreBoardApplication;
import org.dolicoli.android.golfscoreboard.R;
import org.dolicoli.android.golfscoreboard.data.settings.GameSetting;
import org.dolicoli.android.golfscoreboard.data.settings.PlayerSetting;
import org.dolicoli.android.golfscoreboard.data.settings.Result;
import org.dolicoli.android.golfscoreboard.db.GameSettingDatabaseWorker;
import org.dolicoli.android.golfscoreboard.db.HistoryGameSettingDatabaseWorker;
import org.dolicoli.android.golfscoreboard.db.PlayerSettingDatabaseWorker;
import org.dolicoli.android.golfscoreboard.db.ResultDatabaseWorker;
import org.dolicoli.android.golfscoreboard.net.GameParser;
import org.dolicoli.android.golfscoreboard.net.HttpScraper;
import org.dolicoli.android.golfscoreboard.net.ResponseException;
import org.dolicoli.android.golfscoreboard.tasks.ImportGameTask.ImportProgress;
import org.dolicoli.android.golfscoreboard.tasks.ImportGameTask.ImportResult;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class ImportGameTask extends
		AsyncTask<String, ImportProgress, ImportResult> {

	private static final String TAG = "ImportGameTask";

	public static final int CODE_OK = 100;
	public static final int CODE_CANCEL = 101;
	public static final int CODE_CONNECTION_ERROR = 1000;
	public static final int CODE_INVALID_PARAMS = 1001;
	public static final int CODE_NO_DATA = 1002;

	private Context context;
	private TaskListener listener;
	private String host;
	private boolean running;

	public ImportGameTask(Context context, TaskListener listener) {
		this.context = context;
		this.listener = listener;

		GolfScoreBoardApplication application = (GolfScoreBoardApplication) context
				.getApplicationContext();
		host = application.getWebHost();
	}

	@Override
	protected void onProgressUpdate(ImportProgress... values) {
		super.onProgressUpdate(values);
		if (values.length < 1)
			return;

		if (listener != null) {
			listener.onImportGameProgressUpdate(values[0]);
		}
	}

	protected void onPreExecute() {
		super.onPreExecute();

		running = true;
		if (listener != null) {
			listener.onImportGameStart();
		}
	}

	@Override
	protected void onPostExecute(ImportResult result) {
		super.onPostExecute(result);

		running = false;
		if (listener != null) {
			listener.onImportGameFinished(result);
		}
	}

	private static final String PAGE = "game";

	@Override
	protected ImportResult doInBackground(String... args) {
		if (args == null || args.length < 1) {
			return new ImportResult(false, CODE_INVALID_PARAMS);
		}

		String url = host + PAGE + "?gameId=" + args[0];

		publishProgress(new ImportProgress(0, 10,
				R.string.task_gamereceive_connecting));

		String contents = null;
		try {
			contents = HttpScraper.scrap(url, HttpScraper.UTF_8);
		} catch (Exception e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
			return new ImportResult(false, CODE_CONNECTION_ERROR);
		}

		if (contents == null || contents.isEmpty()) {
			return new ImportResult(false, CODE_NO_DATA);
		}

		publishProgress(new ImportProgress(2, 10,
				R.string.task_gamereceive_parsing));

		try {
			GameParser parser = new GameParser();
			if (!parser.parse(context, contents)) {
				return new ImportResult(false, CODE_NO_DATA);
			}

			if (isCancelled()) {
				return new ImportResult(true, CODE_CANCEL);
			}

			publishProgress(new ImportProgress(3, 10,
					R.string.task_gamereceive_parsing_game_setting));

			GameSetting gameSetting = parser.getGameSetting();
			if (isCancelled()) {
				return new ImportResult(true, CODE_CANCEL);
			}

			publishProgress(new ImportProgress(4, 10,
					R.string.task_gamereceive_parsing_player_setting));

			PlayerSetting playerSetting = parser.getPlayerSetting();
			if (isCancelled()) {
				return new ImportResult(true, CODE_CANCEL);
			}

			publishProgress(new ImportProgress(5, 10,
					R.string.task_gamereceive_parsing_result));

			ArrayList<Result> results = parser.getResults();
			if (isCancelled()) {
				return new ImportResult(true, CODE_CANCEL);
			}

			publishProgress(new ImportProgress(6, 10,
					R.string.task_gamereceive_saving));

			GameSettingDatabaseWorker gameSettingWorker = new GameSettingDatabaseWorker(
					context);
			gameSettingWorker.updateGameSetting(gameSetting);
			if (isCancelled()) {
				return new ImportResult(true, CODE_CANCEL);
			}

			publishProgress(new ImportProgress(7, 10,
					R.string.task_gamereceive_saving));

			PlayerSettingDatabaseWorker playerSettingWorker = new PlayerSettingDatabaseWorker(
					context);
			playerSettingWorker.updatePlayerSetting(playerSetting);
			if (isCancelled()) {
				return new ImportResult(true, CODE_CANCEL);
			}

			publishProgress(new ImportProgress(8, 10,
					R.string.task_gamereceive_saving));

			ResultDatabaseWorker resultWorker = new ResultDatabaseWorker(
					context);
			resultWorker.cleanUpAllData();
			if (isCancelled()) {
				return new ImportResult(true, CODE_CANCEL);
			}

			for (Result result : results) {
				resultWorker.updateResult(result);
				if (isCancelled()) {
					return new ImportResult(true, CODE_CANCEL);
				}
			}

			publishProgress(new ImportProgress(9, 10,
					R.string.task_gamereceive_saving));

			HistoryGameSettingDatabaseWorker historyWorker = new HistoryGameSettingDatabaseWorker(
					context);
			historyWorker.addCurrentHistory(false, gameSetting, playerSetting,
					results);

			publishProgress(new ImportProgress(10, 10,
					R.string.task_gamereceive_finish));
			return new ImportResult();
		} catch (ResponseException e) {
			return new ImportResult(false, CODE_CONNECTION_ERROR);
		}
	}

	public boolean isRunning() {
		return running;
	}

	public static class ImportProgress {
		private int msgId;
		private int current, total;

		public ImportProgress(int current, int total, int msgId) {
			this.current = current;
			this.total = total;
			this.msgId = msgId;
		}

		public int getMessageId() {
			return msgId;
		}

		public int getCurrent() {
			return current;
		}

		public int getTotal() {
			return total;
		}
	}

	public static class ImportResult {

		private boolean success;
		private int errorCode;

		public ImportResult() {
			this(true, CODE_OK);
		}

		public ImportResult(boolean success, int msgId) {
			this.success = success;
			this.errorCode = msgId;
		}

		public boolean isSuccess() {
			return success;
		}

		public int getErrorCode() {
			return errorCode;
		}

		public boolean isCancel() {
			return (errorCode == CODE_CANCEL);
		}
	}

	public static interface TaskListener {
		void onImportGameStart();

		void onImportGameProgressUpdate(ImportProgress progress);

		void onImportGameFinished(ImportResult result);
	}
}
