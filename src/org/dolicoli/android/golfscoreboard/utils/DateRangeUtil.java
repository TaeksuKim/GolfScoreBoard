package org.dolicoli.android.golfscoreboard.utils;

import java.util.Calendar;

public class DateRangeUtil {
	public static DateRange getDateRange(int monthRange) {

		Calendar toCalendar = Calendar.getInstance();
		toCalendar.set(Calendar.HOUR_OF_DAY, 23);
		toCalendar.set(Calendar.MINUTE, 59);

		// First day of to-month
		Calendar fromCalendar = Calendar.getInstance();
		fromCalendar.setTimeInMillis(toCalendar.getTimeInMillis());
		int dayOfMonth = fromCalendar.get(Calendar.DAY_OF_MONTH);
		fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
		fromCalendar.set(Calendar.MINUTE, 1);
		fromCalendar.add(Calendar.DAY_OF_MONTH, dayOfMonth * -1 + 1);

		fromCalendar.add(Calendar.MONTH, monthRange * -1);

		return new DateRange(fromCalendar.getTimeInMillis(),
				toCalendar.getTimeInMillis());
	}

	public static DateRange getMonthlyDateRange(int before) {
		Calendar fromCalendar = Calendar.getInstance();

		Calendar toCalendar = Calendar.getInstance();
		toCalendar.set(Calendar.HOUR_OF_DAY, 23);
		toCalendar.set(Calendar.MINUTE, 59);

		// First day of to-month
		fromCalendar.setTimeInMillis(toCalendar.getTimeInMillis());
		fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
		fromCalendar.set(Calendar.MINUTE, 1);
		int dayOfMonth = fromCalendar.get(Calendar.DAY_OF_MONTH);
		fromCalendar.add(Calendar.DAY_OF_MONTH, dayOfMonth * -1 + 1);
		fromCalendar.add(Calendar.MONTH, before * -1);

		if (before > 0) {
			toCalendar.setTimeInMillis(fromCalendar.getTimeInMillis());
			toCalendar.add(Calendar.MONTH, 1);
			toCalendar.set(Calendar.DAY_OF_MONTH, 1);
			toCalendar.set(Calendar.HOUR_OF_DAY, 23);
			toCalendar.set(Calendar.MINUTE, 59);
			toCalendar.add(Calendar.DAY_OF_MONTH, -1);
		}

		return new DateRange(fromCalendar.getTimeInMillis(),
				toCalendar.getTimeInMillis());
	}

	public static class DateRange {
		private long from, to;

		public DateRange(long from, long to) {
			this.from = from;
			this.to = to;
		}

		public long getFrom() {
			return from;
		}

		public long getTo() {
			return to;
		}
	}
}
