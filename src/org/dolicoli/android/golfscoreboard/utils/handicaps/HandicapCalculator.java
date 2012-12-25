package org.dolicoli.android.golfscoreboard.utils.handicaps;

import java.util.Date;

import android.content.Context;

public interface HandicapCalculator {

	String getName(Context context);

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
}
