package org.dolicoli.android.golfscoreboard.data;

import org.dolicoli.android.golfscoreboard.utils.DateRangeUtil.DateRange;

public class DateItem {
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
