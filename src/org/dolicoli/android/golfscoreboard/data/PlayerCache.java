package org.dolicoli.android.golfscoreboard.data;

import java.util.Arrays;
import java.util.HashSet;

public class PlayerCache {
	private HashSet<String> playerNameSet;

	public PlayerCache() {
		playerNameSet = new HashSet<String>();
	}

	public void put(String name, int handicap) {
		playerNameSet.add(name);
	}

	public int size() {
		return playerNameSet.size();
	}

	public String[] getNames() {
		int size = playerNameSet.size();
		if (size < 1)
			return new String[0];

		String[] array = new String[size];
		playerNameSet.toArray(array);
		Arrays.sort(array);
		return array;
	}
}
