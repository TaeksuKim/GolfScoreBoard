package org.dolicoli.android.golfscoreboard;

import org.dolicoli.android.golfscoreboard.utils.DateRangeUtil;
import org.dolicoli.android.golfscoreboard.utils.DateRangeUtil.DateRange;

import android.app.Application;
import android.util.Log;

public class GolfScoreBoardApplication extends Application {

	private static final String TAG = "GolfScoreBoardApplication";

	public static final int MODE_RECENT_FIVE_GAMES = 0;
	public static final int MODE_THIS_MONTH = 1;
	public static final int MODE_LAST_MONTH = 2;
	public static final int MODE_LAST_THREE_MONTHS = 3;

	private DateItem[] navigationItems;
	private String webHost;
	private int navigationMode;

	@Override
	public void onCreate() {
		super.onCreate();

		loadRangeItems();
		loadCustomProperties();
		navigationMode = MODE_RECENT_FIVE_GAMES;

		Log.d(TAG, "WEB HOST: " + webHost);
	}

	public DateItem[] getNavigationItems() {
		return navigationItems;
	}

	public String getWebHost() {
		return webHost;
	}

	public int getNavigationMode() {
		return navigationMode;
	}

	public void setNavigationMode(int mode) {
		this.navigationMode = mode;
	}

	private void loadCustomProperties() {
		webHost = getString(R.string.web_host);
	}

	private void loadRangeItems() {
		navigationItems = new DateItem[4];

		DateRange dummyMonthRange = DateRangeUtil.getDateRange(2);
		navigationItems[0] = new DateItem(dummyMonthRange.getFrom(),
				dummyMonthRange.getTo(), "최근 5 경기");

		DateRange thisMonthRange = DateRangeUtil.getMonthlyDateRange(0);
		navigationItems[1] = new DateItem(thisMonthRange.getFrom(),
				thisMonthRange.getTo(), "이번 달");

		DateRange lastMonthRange = DateRangeUtil.getMonthlyDateRange(1);
		navigationItems[2] = new DateItem(lastMonthRange.getFrom(),
				lastMonthRange.getTo(), "지난 달");

		DateRange lastThreeMonthRange = DateRangeUtil.getDateRange(2);
		navigationItems[3] = new DateItem(lastThreeMonthRange.getFrom(),
				lastThreeMonthRange.getTo(), "최근 3개월");
	}

	public static class DateItem {
		private long from, to;
		private String title;

		public DateItem(long from, long to, String title) {
			this.from = from;
			this.to = to;
			this.title = title;
		}

		public long getFrom() {
			return from;
		}

		public long getTo() {
			return to;
		}

		public String getTitle() {
			return title;
		}

		public DateRange getDateRange() {
			return new DateRange(from, to);
		}

		@Override
		public String toString() {
			return title;
		}
	}
}
