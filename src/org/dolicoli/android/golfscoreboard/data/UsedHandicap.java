package org.dolicoli.android.golfscoreboard.data;

import org.dolicoli.android.golfscoreboard.Constants;

public class UsedHandicap {
	private int[] usedHandicaps;

	public UsedHandicap() {
		usedHandicaps = new int[Constants.MAX_PLAYER_COUNT];
		for (int i = 0; i < Constants.MAX_PLAYER_COUNT; i++) {
			usedHandicaps[i] = 0;
		}
	}

	public void setUsedHandicap(int playerId, int handicap) {
		if (playerId < 0 || playerId > Constants.MAX_PLAYER_COUNT - 1)
			return;
		usedHandicaps[playerId] = handicap;
	}

	public int getUsedHandicap(int playerId) {
		if (playerId < 0 || playerId > Constants.MAX_PLAYER_COUNT - 1)
			return 0;
		return usedHandicaps[playerId];
	}
}
