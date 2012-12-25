package org.dolicoli.android.golfscoreboard.fragments.history;

import java.util.ArrayList;

import org.dolicoli.android.golfscoreboard.data.GameAndResult;

public interface HistoryDataContainer {

	public static final int INDEX_RECENT_FIVE_GAMES = 0;
	public static final int INDEX_THIS_MONTH = 1;
	public static final int INDEX_LAST_MONTH = 2;
	public static final int INDEX_LAST_THREE_MONTH = 3;

	ArrayList<GameAndResult> getThisMonthGameAndResults();

	ArrayList<GameAndResult> getLastMonthGameAndResults();

	ArrayList<GameAndResult> getAllGameAndResults();
}
