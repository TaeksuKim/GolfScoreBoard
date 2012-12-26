package org.dolicoli.android.golfscoreboard;

import android.app.Application;
import android.util.Log;

public class GolfScoreBoardApplication extends Application {

	private static final String TAG = "GolfScoreBoardApplication";

	private String webHost;

	@Override
	public void onCreate() {
		super.onCreate();

		loadCustomProperties();
		Log.d(TAG, "WEB HOST: " + webHost);
	}

	public String getWebHost() {
		return webHost;
	}

	private void loadCustomProperties() {
		webHost = getString(R.string.web_host);
	}
}
