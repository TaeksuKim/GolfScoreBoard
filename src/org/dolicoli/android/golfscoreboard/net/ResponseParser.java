package org.dolicoli.android.golfscoreboard.net;

public abstract class ResponseParser {

	protected static long getLong(String value) {
		try {
			return Long.parseLong(value);
		} catch (Throwable t) {
			return 0L;
		}
	}

	protected static int getInt(String value) {
		try {
			return Integer.parseInt(value);
		} catch (Throwable t) {
			return 0;
		}
	}
}
