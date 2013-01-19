package org.dolicoli.android.golfscoreboard.tasks;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.dolicoli.android.golfscoreboard.GolfScoreBoardApplication;
import org.dolicoli.android.golfscoreboard.R;
import org.dolicoli.android.golfscoreboard.data.settings.GameSetting;
import org.dolicoli.android.golfscoreboard.data.settings.PlayerSetting;
import org.dolicoli.android.golfscoreboard.data.settings.Result;
import org.dolicoli.android.golfscoreboard.db.DownloadTickDatabaseWorker;
import org.dolicoli.android.golfscoreboard.db.GameSettingDatabaseWorker;
import org.dolicoli.android.golfscoreboard.db.HistoryGameSettingDatabaseWorker;
import org.dolicoli.android.golfscoreboard.db.PlayerSettingDatabaseWorker;
import org.dolicoli.android.golfscoreboard.db.ResultDatabaseWorker;
import org.dolicoli.android.golfscoreboard.net.HistoryListParser;
import org.dolicoli.android.golfscoreboard.net.HistoryListParser.History;
import org.dolicoli.android.golfscoreboard.net.HttpScraper;
import org.dolicoli.android.golfscoreboard.net.ResponseException;
import org.dolicoli.android.golfscoreboard.tasks.ImportAllTask.ReceiveProgress;
import org.dolicoli.android.golfscoreboard.tasks.ImportAllTask.ReceiveResult;
import org.dolicoli.android.golfscoreboard.utils.DateRangeUtil;
import org.dolicoli.android.golfscoreboard.utils.DateRangeUtil.DateRange;

import android.content.Context;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.util.Log;

public class ImportAllTask extends
		AsyncTask<Void, ReceiveProgress, ReceiveResult> {

	private static final String TAG = "ImportAllTask";

	public static final int CODE_OK = 100;
	public static final int CODE_CANCEL = 101;
	public static final int CODE_CONNECTION_ERROR = 1000;
	public static final int CODE_INVALID_PARAMS = 1001;
	public static final int CODE_NO_DATA = 1002;

	private Context context;
	private TaskListener listener;
	private String host;
	private boolean running;

	public ImportAllTask(Context context, TaskListener listener) {
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
			listener.onImportAllProgressUpdate(values[0]);
		}
	}

	protected void onPreExecute() {
		super.onPreExecute();

		running = true;
		if (listener != null) {
			listener.onImportAllStart();
		}
	}

	@Override
	protected void onPostExecute(ReceiveResult result) {
		super.onPostExecute(result);

		running = false;
		if (listener != null) {
			listener.onImportAllFinished(result);
		}
	}

	private static final String PAGE = "historyList";

	@Override
	protected ReceiveResult doInBackground(Void... args) {
		DateRange dateRange = DateRangeUtil.getDateRange(2);
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHH",
				Locale.getDefault());
		Date fromDate = new Date();
		fromDate.setTime(dateRange.getFrom());
		Date toDate = new Date();
		toDate.setTime(dateRange.getTo());

		String url = host + PAGE + "?from=" + format.format(fromDate) + "&to="
				+ format.format(toDate);

		publishProgress(new ReceiveProgress(
				0,
				0,
				context.getString(R.string.task_threemonthsgamereceive_connecting)));

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

		publishProgress(new ReceiveProgress(0, 0,
				context.getString(R.string.task_threemonthsgamereceive_parsing)));

		try {
			HistoryListParser parser = new HistoryListParser();
			if (!parser.parse(context, contents)) {
				return new ReceiveResult(false, CODE_NO_DATA);
			}

			if (isCancelled()) {
				return new ReceiveResult(true, CODE_CANCEL);
			}

			publishProgress(new ReceiveProgress(
					0,
					0,
					context.getString(R.string.task_threemonthsgamereceive_saving)));

			ArrayList<History> historyList = parser.getHistoryList();
			if (isCancelled()) {
				return new ReceiveResult(true, CODE_CANCEL);
			}

			DownloadTickDatabaseWorker tickWorker = new DownloadTickDatabaseWorker(
					context);
			tickWorker.updateTick(parser.getTick());

			int totalSize = historyList.size();
			int index = 0;
			for (History history : historyList) {
				GameSetting gameSetting = history.getGameSetting();

				publishProgress(new ReceiveProgress(
						index++,
						totalSize,
						context.getString(
								R.string.task_threemonthsgamereceive_saving_format,
								DateUtils
										.formatDateTime(
												context,
												gameSetting.getDate().getTime(),
												DateUtils.FORMAT_SHOW_DATE
														| DateUtils.FORMAT_SHOW_TIME
														| DateUtils.FORMAT_ABBREV_RELATIVE))));

				PlayerSetting playerSetting = history.getPlayerSetting();
				if (isCancelled()) {
					return new ReceiveResult(true, CODE_CANCEL);
				}

				List<Result> results = history.getResults();
				if (isCancelled()) {
					return new ReceiveResult(true, CODE_CANCEL);
				}

				HistoryGameSettingDatabaseWorker historyWorker = new HistoryGameSettingDatabaseWorker(
						context);
				historyWorker.addCurrentHistory(true, gameSetting,
						playerSetting, results);
			}

			if (totalSize > 0) {
				History lastHistory = historyList.get(0);

				GameSettingDatabaseWorker gameSettingWorker = new GameSettingDatabaseWorker(
						context);
				gameSettingWorker.updateGameSetting(lastHistory
						.getGameSetting());
				if (isCancelled()) {
					return new ReceiveResult(true, CODE_CANCEL);
				}

				publishProgress(new ReceiveProgress(7, 10,
						context.getString(R.string.task_gamereceive_saving)));

				PlayerSettingDatabaseWorker playerSettingWorker = new PlayerSettingDatabaseWorker(
						context);
				playerSettingWorker.updatePlayerSetting(lastHistory
						.getPlayerSetting());
				if (isCancelled()) {
					return new ReceiveResult(true, CODE_CANCEL);
				}

				publishProgress(new ReceiveProgress(8, 10,
						context.getString(R.string.task_gamereceive_saving)));

				ResultDatabaseWorker resultWorker = new ResultDatabaseWorker(
						context);
				resultWorker.cleanUpAllData();
				if (isCancelled()) {
					return new ReceiveResult(true, CODE_CANCEL);
				}

				publishProgress(new ReceiveProgress(9, 10,
						context.getString(R.string.task_gamereceive_saving)));

				List<Result> results = lastHistory.getResults();
				for (Result result : results) {
					resultWorker.updateResult(result);
					if (isCancelled()) {
						return new ReceiveResult(true, CODE_CANCEL);
					}
				}
			}

			publishProgress(new ReceiveProgress(10, 10,
					context.getString(R.string.task_gamereceive_finish)));
			return new ReceiveResult(true, CODE_OK);
		} catch (ResponseException e) {
			return new ReceiveResult(false, CODE_CONNECTION_ERROR);
		}
	}

	public boolean isRunning() {
		return running;
	}

	public static interface TaskListener {
		void onImportAllStart();

		void onImportAllProgressUpdate(ReceiveProgress progress);

		void onImportAllFinished(ReceiveResult result);
	}

	public static class ReceiveProgress {
		private String msg;
		private int current, total;

		public ReceiveProgress(int current, int total, String msg) {
			this.current = current;
			this.total = total;
			this.msg = msg;
		}

		public String getMessage() {
			return msg;
		}

		public int getCurrent() {
			return current;
		}

		public int getTotal() {
			return total;
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
}
