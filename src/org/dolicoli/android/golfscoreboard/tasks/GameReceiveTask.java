package org.dolicoli.android.golfscoreboard.tasks;

import java.util.ArrayList;

import org.dolicoli.android.golfscoreboard.GolfScoreBoardApplication;
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
import org.dolicoli.android.golfscoreboard.tasks.GameReceiveTask.ReceiveProgress;
import org.dolicoli.android.golfscoreboard.tasks.GameReceiveTask.ReceiveResult;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class GameReceiveTask extends
		AsyncTask<String, ReceiveProgress, ReceiveResult> {

	private static final String TAG = "GameReceiveTask";

	public static final int CODE_OK = 100;
	public static final int CODE_CANCEL = 101;
	public static final int CODE_CONNECTION_ERROR = 1000;
	public static final int CODE_INVALID_PARAMS = 1001;
	public static final int CODE_NO_DATA = 1002;

	private Context context;
	private GameReceiveTaskListener listener;
	private String host;
	private boolean running;

	public GameReceiveTask(Context context, GameReceiveTaskListener listener) {
		this.context = context;
		this.listener = listener;

		GolfScoreBoardApplication application = (GolfScoreBoardApplication) context
				.getApplicationContext();
		host = application.getWebHost();
	}

	@Override
	protected void onProgressUpdate(ReceiveProgress... values) {
		super.onProgressUpdate(values);
		if (values.length < 1)
			return;

		if (listener != null) {
			listener.onReceiveProgressUpdate(values[0]);
		}
	}

	protected void onPreExecute() {
		super.onPreExecute();

		running = true;
		if (listener != null) {
			listener.onReceiveStart();
		}
	}

	@Override
	protected void onPostExecute(ReceiveResult result) {
		super.onPostExecute(result);

		running = false;
		if (listener != null) {
			listener.onReceiveFinished(result);
		}
	}

	private static final String PAGE = "game";

	@Override
	protected ReceiveResult doInBackground(String... args) {
		if (args == null || args.length < 1) {
			return new ReceiveResult(false, CODE_INVALID_PARAMS);
		}

		String url = host + PAGE + "?gameId=" + args[0];

		publishProgress(new ReceiveProgress("접속 중"));

		String contents = null;
		try {
			contents = HttpScraper.scrap(url, HttpScraper.UTF_8);
		} catch (Exception e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
			return new ReceiveResult(false, CODE_CONNECTION_ERROR);
		}

		if (contents == null || contents.isEmpty()) {
			return new ReceiveResult(false, CODE_NO_DATA);
		}

		publishProgress(new ReceiveProgress("자료를 해석하는 중"));

		try {
			GameParser parser = new GameParser();
			if (!parser.parse(context, contents)) {
				return new ReceiveResult(false, CODE_NO_DATA);
			}

			if (isCancelled()) {
				return new ReceiveResult(true, CODE_CANCEL);
			}

			publishProgress(new ReceiveProgress("게임 설정을 해석하는 중"));

			GameSetting gameSetting = parser.getGameSetting();
			if (isCancelled()) {
				return new ReceiveResult(true, CODE_CANCEL);
			}

			publishProgress(new ReceiveProgress("플레이어 설정을 해석하는 중"));

			PlayerSetting playerSetting = parser.getPlayerSetting();
			if (isCancelled()) {
				return new ReceiveResult(true, CODE_CANCEL);
			}

			publishProgress(new ReceiveProgress("결과를 해석하는 중"));

			ArrayList<Result> results = parser.getResults();
			if (isCancelled()) {
				return new ReceiveResult(true, CODE_CANCEL);
			}

			publishProgress(new ReceiveProgress("결과를 저장하는 중"));

			GameSettingDatabaseWorker gameSettingWorker = new GameSettingDatabaseWorker(
					context);
			gameSettingWorker.updateGameSetting(gameSetting);
			if (isCancelled()) {
				return new ReceiveResult(true, CODE_CANCEL);
			}

			PlayerSettingDatabaseWorker playerSettingWorker = new PlayerSettingDatabaseWorker(
					context);
			playerSettingWorker.updatePlayerSetting(playerSetting);
			if (isCancelled()) {
				return new ReceiveResult(true, CODE_CANCEL);
			}

			ResultDatabaseWorker resultWorker = new ResultDatabaseWorker(
					context);
			resultWorker.cleanUpAllData();
			if (isCancelled()) {
				return new ReceiveResult(true, CODE_CANCEL);
			}

			for (Result result : results) {
				resultWorker.updateResult(result);
				if (isCancelled()) {
					return new ReceiveResult(true, CODE_CANCEL);
				}
			}

			HistoryGameSettingDatabaseWorker historyWorker = new HistoryGameSettingDatabaseWorker(
					context);
			historyWorker.addCurrentHistory(false, gameSetting, playerSetting,
					results);

			publishProgress(new ReceiveProgress("완료"));
			return new ReceiveResult();
		} catch (ResponseException e) {
			return new ReceiveResult(false, CODE_CONNECTION_ERROR);
		}
	}

	public boolean isRunning() {
		return running;
	}

	public static class ReceiveProgress {
		private int msgId;
		private String msg;

		public ReceiveProgress(int msgId) {
			this.msgId = msgId;
			this.msg = null;
		}

		public ReceiveProgress(String msg) {
			this.msgId = 0;
			this.msg = msg;
		}

		public String getMessage() {
			return msg;
		}

		public int getMessageId() {
			return msgId;
		}
	}

	public static class ReceiveResult {

		private boolean success;
		private int errorCode;

		public ReceiveResult() {
			this(true, CODE_OK);
		}

		public ReceiveResult(boolean success, int msgId) {
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

	public static interface GameReceiveTaskListener {
		void onReceiveStart();

		void onReceiveProgressUpdate(ReceiveProgress value);

		void onReceiveFinished(ReceiveResult result);
	}
}
