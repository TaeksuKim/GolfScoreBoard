package org.dolicoli.android.golfscoreboard.data;

public class OneHolePlayerScore implements Comparable<OneHolePlayerScore> {

	private int holeNumber, parNumber;
	private int playerId;
	private String name;

	private int ranking, sameRankingCount;
	private int originalScore, usedHandicap;
	private int fee;

	public OneHolePlayerScore(int holeNumber, int parNumber, int playerId,
			String name, int originalScore, int usedHandicap, int ranking,
			int sameRankingCount, int fee) {
		this.parNumber = parNumber;
		this.holeNumber = holeNumber;
		this.playerId = playerId;
		this.name = name;

		this.ranking = ranking;
		this.sameRankingCount = sameRankingCount;

		this.originalScore = originalScore;
		this.usedHandicap = usedHandicap;

		this.fee = fee;
	}

	@Override
	public int compareTo(OneHolePlayerScore compare) {
		if (ranking < compare.ranking)
			return -1;
		if (ranking > compare.ranking)
			return 1;
		if (getFinalScore() < compare.getFinalScore())
			return -1;
		if (getFinalScore() > compare.getFinalScore())
			return 1;
		if (originalScore < compare.originalScore)
			return -1;
		if (originalScore > compare.originalScore)
			return 1;
		if (fee > compare.fee)
			return -1;
		if (fee < compare.fee)
			return 1;
		return name.compareTo(compare.name);
	}

	public int getHoleNumber() {
		return holeNumber;
	}

	public int getParNumber() {
		return parNumber;
	}

	public int getPlayerId() {
		return playerId;
	}

	public String getName() {
		return name;
	}

	public int getRanking() {
		return ranking;
	}

	public int getSameRankingCount() {
		return sameRankingCount;
	}

	public int getOriginalScore() {
		return originalScore;
	}

	public int getUsedHandicap() {
		return usedHandicap;
	}

	public int getFinalScore() {
		return originalScore - usedHandicap;
	}

	public int getFee() {
		return fee;
	}
}
