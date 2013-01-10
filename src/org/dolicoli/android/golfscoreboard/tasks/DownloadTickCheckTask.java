package org.dolicoli.android.golfscoreboard.tasks;

import org.dolicoli.android.golfscoreboard.GolfScoreBoardApplication;
import org.dolicoli.android.golfscoreboard.db.DownloadTickDatabaseWorker;
import org.dolicoli.android.golfscoreboard.net.DownloadTickParser;
import org.dolicoli.android.golfscoreboard.net.HttpScraper;
import org.dolicoli.android.golfscoreboard.net.ResponseException;
import org.dolicoli.android.golfscoreboard.tasks.DownloadTickCheckTask.DownloadTickCheckResult;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class DownloadTickCheckTask extends
		AsyncTask<Void, Void, DownloadTickCheckResult> {

	private static final String TAG = "DownloadTickCheckTask";

	public static final int CODE_OK = 100;
	public static final int CODE_CANCEL = 101;
	public static final int CODE_CONNECTION_ERROR = 1000;
	public static final int CODE_INVALID_PARAMS = 1001;
	public static final int CODE_NO_DATA = 1002;

	private Context context;
	private DownloadTickCheckListener listener;
	private String host;
	private boolean running;

	public DownloadTickCheckTask(Context context,
			DownloadTickCheckListener listener) {
		this.context = context;
		this.listener = listener;

		GolfScoreBoardApplication application = (GolfScoreBoardApplication) context
				.getApplicationContext();
		host = application.getWebHost();
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
	}

	protected void onPreExecute() {
		super.onPreExecute();

		running = true;
		if (listener != null) {
			listener.onDownloadTickCheckStart();
		}
	}

	@Override
	protected void onPostExecute(DownloadTickCheckResult result) {
		super.onPostExecute(result);

		running = false;
		if (listener != null) {
			listener.onDownloadTickCheckFinished(result);
		}
	}

	private static final String PAGE = "tick";

	@Override
	protected DownloadTickCheckResult doInBackground(Void... args) {

		DownloadTickDatabaseWorker worker = new DownloadTickDatabaseWorker(
				context);
		long tickLocal = worker.getTick();
		if (tickLocal <= 0L) {
			return new DownloadTickCheckResult(true, true);
		}

		String url = host + PAGE;

		String contents = null;
		try {
			contents = HttpScraper.scrap(url, HttpScraper.UTF_8);
		} catch (Exception e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
			return new DownloadTickCheckResult(false);
		}

		if (contents == null || contents.isEmpty()) {
			return new DownloadTickCheckResult(false);
		}

		try {
			DownloadTickParser parser = new DownloadTickParser();
			if (!parser.parse(context, contents)) {
				return new DownloadTickCheckResult(false);
			}

			if (isCancelled()) {
				return new DownloadTickCheckResult(true, false);
			}

			long tickServer = parser.getTick();
			if (tickLocal < tickServer) {
				return new DownloadTickCheckResult(true, true);
			} else {
				return new DownloadTickCheckResult(true, false);
			}
		} catch (ResponseException e) {
			return new DownloadTickCheckResult(false);
		}
	}

	public boolean isRunning() {
		return running;
	}

	public static interface DownloadTickCheckListener {
		void onDownloadTickCheckStart();

		void onDownloadTickCheckFinished(DownloadTickCheckResult result);
	}

	public static class DownloadTickCheckResult {

		private boolean success;
		private boolean shouldRefresh;

		public DownloadTickCheckResult(boolean success) {
			this(success, true);
		}

		public DownloadTickCheckResult(boolean success, boolean refresh) {
			this.success = success;
			this.shouldRefresh = refresh;
		}

		public boolean isSuccess() {
			return success;
		}

		public boolean isShouldRefresh() {
			return shouldRefresh;
		}
	}
}
