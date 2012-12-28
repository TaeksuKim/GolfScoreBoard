package org.dolicoli.android.golfscoreboard.tasks;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.dolicoli.android.golfscoreboard.GolfScoreBoardApplication;
import org.dolicoli.android.golfscoreboard.data.settings.GameSetting;
import org.dolicoli.android.golfscoreboard.data.settings.PlayerSetting;
import org.dolicoli.android.golfscoreboard.data.settings.Result;
import org.dolicoli.android.golfscoreboard.db.HistoryGameSettingDatabaseWorker;
import org.dolicoli.android.golfscoreboard.net.HistoryListParser;
import org.dolicoli.android.golfscoreboard.net.HistoryListParser.History;
import org.dolicoli.android.golfscoreboard.net.HttpScraper;
import org.dolicoli.android.golfscoreboard.net.ResponseException;
import org.dolicoli.android.golfscoreboard.tasks.GameReceiveTask.GameReceiveTaskListener;
import org.dolicoli.android.golfscoreboard.tasks.GameReceiveTask.ReceiveProgress;
import org.dolicoli.android.golfscoreboard.tasks.GameReceiveTask.ReceiveResult;
import org.dolicoli.android.golfscoreboard.utils.DateRangeUtil;
import org.dolicoli.android.golfscoreboard.utils.DateRangeUtil.DateRange;

import android.content.Context;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.util.Log;

public class ThreeMonthsGameReceiveTask extends
		AsyncTask<String, ReceiveProgress, ReceiveResult> {

	private static final String TAG = "ThreeMonthsGameReceiveTask";

	public static final int CODE_OK = 100;
	public static final int CODE_CANCEL = 101;
	public static final int CODE_CONNECTION_ERROR = 1000;
	public static final int CODE_INVALID_PARAMS = 1001;
	public static final int CODE_NO_DATA = 1002;

	private Context context;
	private GameReceiveTaskListener listener;
	private String host;
	private boolean running;

	public ThreeMonthsGameReceiveTask(Context context,
			GameReceiveTaskListener listener) {
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

	private static final String PAGE = "historyList";

	@Override
	protected ReceiveResult doInBackground(String... args) {
		DateRange dateRange = DateRangeUtil.getDateRange(2);
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHH",
				Locale.getDefault());
		Date fromDate = new Date();
		fromDate.setTime(dateRange.getFrom());
		Date toDate = new Date();
		toDate.setTime(dateRange.getTo());

		String url = host + PAGE + "?from=" + format.format(fromDate) + "&to="
				+ format.format(toDate);

		// publishProgress(new ReceiveProgress(0 /* Requesting... */));
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
			HistoryListParser parser = new HistoryListParser();
			if (!parser.parse(context, contents)) {
				return new ReceiveResult(false, CODE_NO_DATA);
			}

			if (isCancelled()) {
				return new ReceiveResult(true, CODE_CANCEL);
			}

			publishProgress(new ReceiveProgress("자료를 저장하는 중"));

			ArrayList<History> historyList = parser.getHistoryList();
			if (isCancelled()) {
				return new ReceiveResult(true, CODE_CANCEL);
			}

			for (History history : historyList) {
				GameSetting gameSetting = history.getGameSetting();

				publishProgress(new ReceiveProgress("자료를 저장하는 중\n"
						+ DateUtils.formatDateTime(context, gameSetting
								.getDate().getTime(),
								DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_ABBREV_RELATIVE)));

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
			return new ReceiveResult();
		} catch (ResponseException e) {
			return new ReceiveResult(false, CODE_CONNECTION_ERROR);
		}
	}

	public boolean isRunning() {
		return running;
	}
}
