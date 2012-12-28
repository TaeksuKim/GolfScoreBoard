package org.dolicoli.android.golfscoreboard.tasks;

import java.util.ArrayList;

import org.dolicoli.android.golfscoreboard.GolfScoreBoardApplication;
import org.dolicoli.android.golfscoreboard.R;
import org.dolicoli.android.golfscoreboard.data.settings.GameSetting;
import org.dolicoli.android.golfscoreboard.data.settings.PlayerSetting;
import org.dolicoli.android.golfscoreboard.data.settings.Result;
import org.dolicoli.android.golfscoreboard.db.GameSettingDatabaseWorker;
import org.dolicoli.android.golfscoreboard.db.PlayerSettingDatabaseWorker;
import org.dolicoli.android.golfscoreboard.db.ResultDatabaseWorker;
import org.dolicoli.android.golfscoreboard.net.GameUploader;
import org.dolicoli.android.golfscoreboard.tasks.ExportCurrentGameTask.ExportProgress;
import org.dolicoli.android.golfscoreboard.tasks.ExportCurrentGameTask.ExportResult;

import android.content.Context;
import android.os.AsyncTask;

public class ExportCurrentGameTask extends
		AsyncTask<Void, ExportProgress, ExportResult> {

	public static final int CODE_OK = 100;
	public static final int CODE_CANCEL = 101;
	public static final int CODE_ERROR = 1000;

	private Context context;
	private String host;
	private TaskListener listener;
	private boolean running;

	public ExportCurrentGameTask(Context context, TaskListener listener) {
		this.context = context;
		this.listener = listener;

		GolfScoreBoardApplication application = (GolfScoreBoardApplication) context
				.getApplicationContext();
		host = application.getWebHost();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		running = true;
		if (listener != null) {
			listener.onExportCurrentGameStarted();
		}
	}

	@Override
	protected void onProgressUpdate(ExportProgress... values) {
		super.onProgressUpdate(values);
		if (values.length < 1)
			return;

		if (listener != null) {
			listener.onExportCurrentGameProgressUpdate(values[0]);
		}
	}

	@Override
	protected void onPostExecute(ExportResult result) {
		super.onPostExecute(result);

		running = false;
		if (listener != null) {
			listener.onExportCurrentGameFinished(result);
		}
	}

	@Override
	protected ExportResult doInBackground(Void... params) {
		if (isCancelled()) {
			return new ExportResult(true, CODE_CANCEL);
		}

		publishProgress(new ExportProgress(0, 10,
				R.string.task_exportcurrentgame_preparing_game_setting));

		GameSettingDatabaseWorker gameSettingWorker = new GameSettingDatabaseWorker(
				context);
		GameSetting gameSetting = new GameSetting();
		gameSettingWorker.getGameSetting(gameSetting);

		if (isCancelled()) {
			return new ExportResult(true, CODE_CANCEL);
		}

		publishProgress(new ExportProgress(2, 10,
				R.string.task_exportcurrentgame_preparing_player_setting));

		PlayerSettingDatabaseWorker playerSettingWorker = new PlayerSettingDatabaseWorker(
				context);
		PlayerSetting playerSetting = new PlayerSetting();
		playerSettingWorker.getPlayerSetting(playerSetting);

		if (isCancelled()) {
			return new ExportResult(true, CODE_CANCEL);
		}

		publishProgress(new ExportProgress(4, 10,
				R.string.task_exportcurrentgame_preparing_result));

		ResultDatabaseWorker resultWorker = new ResultDatabaseWorker(context);
		ArrayList<Result> results = resultWorker.getResults();

		if (isCancelled()) {
			return new ExportResult(true, CODE_CANCEL);
		}

		publishProgress(new ExportProgress(6, 10,
				R.string.task_exportcurrentgame_exporting));

		boolean success = GameUploader.upload(host, gameSetting, playerSetting,
				results);

		publishProgress(new ExportProgress(10, 10,
				R.string.task_exportcurrentgame_finish));

		if (success)
			return new ExportResult();

		return new ExportResult(false, CODE_ERROR);
	}

	public boolean isRunning() {
		return running;
	}

	public static class ExportProgress {
		private int msgId;
		private int current, total;

		public ExportProgress(int current, int total, int msgId) {
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

	public static class ExportResult {

		private boolean success;
		private int errorCode;

		public ExportResult() {
			this(true, CODE_OK);
		}

		public ExportResult(boolean success, int msgId) {
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
		void onExportCurrentGameStarted();

		void onExportCurrentGameProgressUpdate(ExportProgress progress);

		void onExportCurrentGameFinished(ExportResult result);
	}
}
