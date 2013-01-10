package org.dolicoli.android.golfscoreboard.utils.handicaps;

import java.util.Date;

public interface HandicapCalculator {

	String getName(ResourceContainer context);

	void calculate(String[] playerNames, Iterable<GameResultItem> items);

	float getAvgScore(String playerName);

	int getHandicap(String playerName);

	int getGameCount(String playerName);

	public static interface GameResultItem {
		int getHoleCount();

		int getPlayerCount();

		boolean containsPlayer(String playerName);

		int getRanking(String playerName);

		int getEighteenHoleScore(String playerName);

		int getEighteenHoleFee(String playerName);

		Date getDate();
	}

	public static interface ResourceContainer {
		String getString(int resourceId);
	}
}
