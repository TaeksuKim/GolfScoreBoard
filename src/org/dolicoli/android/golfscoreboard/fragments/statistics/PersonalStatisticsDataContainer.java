package org.dolicoli.android.golfscoreboard.fragments.statistics;

import java.util.ArrayList;

import org.dolicoli.android.golfscoreboard.data.OneGame;

public interface PersonalStatisticsDataContainer {
	String getPlayerName();

	int getPlayerImageResourceId();

	ArrayList<OneGame> getGameAndResults();
}
