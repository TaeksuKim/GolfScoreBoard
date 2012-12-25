package org.dolicoli.android.golfscoreboard;

import android.app.Application;
import android.util.Log;

public class GolfScoreBoardApplication extends Application {

	private static final String TAG = "GolfScoreBoardApplication";

	public static final String PK_THEME = "PK_THEME";

	public static final String PV_THEME_LIGHT = "LIGHT";
	public static final String PV_THEME_DARK = "DARK";

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
