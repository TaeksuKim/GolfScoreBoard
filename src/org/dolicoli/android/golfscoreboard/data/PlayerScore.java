package org.dolicoli.android.golfscoreboard.data;

public class PlayerScore implements Comparable<PlayerScore> {

	private int playerId;
	private String name;
	private int holeCount;
	private int ranking;
	private int sameRankingCount;

	private int handicap, usedHandicap, extraScore;

	private int originalScore;
	private boolean lastStanding;

	private int holeRankingSum;
	private float avgRanking;
	private float avgOverPar;

	private int originalHoleFee, adjustedHoleFee;
	private int originalRankingFee, adjustedRankingFee;
	private int adjustedTotalFee;

	public PlayerScore(int playerId, String name, int handicap, int extraScore) {
		this.playerId = playerId;
		this.name = name;
		this.holeCount = 0;

		this.handicap = handicap;
		this.usedHandicap = 0;
		this.extraScore = extraScore;

		this.ranking = 0;
		this.originalScore = 0;

		this.avgRanking = 0.0F;

		this.originalHoleFee = 0;
		this.originalRankingFee = 0;
		this.adjustedHoleFee = 0;
		this.adjustedRankingFee = 0;
		this.adjustedTotalFee = 0;
		this.holeRankingSum = 0;
	}

	@Override
	public int compareTo(PlayerScore compare) {
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
		if (extraScore < compare.extraScore)
			return -1;
		if (extraScore > compare.extraScore)
			return 1;
		if (adjustedTotalFee < compare.adjustedTotalFee)
			return -1;
		if (adjustedTotalFee > compare.adjustedTotalFee)
			return 1;
		if (playerId < compare.playerId)
			return -1;
		if (playerId > compare.playerId)
			return 1;
		return 0;
	}

	public int getPlayerId() {
		return playerId;
	}

	public int getOriginalHoleFee() {
		return originalHoleFee;
	}

	public void increaseHoleCount() {
		holeCount++;
	}

	public void increaseScore(int score, int usedHandicap) {
		this.originalScore += score;
		this.usedHandicap += usedHandicap;
	}

	public void decreaseScore(int s) {
		originalScore -= s;
	}

	public void increaseOriginalHoleFee(int fee) {
		originalHoleFee += fee;
	}

	public void setAdjustedHoleFee(int f) {
		adjustedHoleFee = f;
	}

	public int getOriginalScore() {
		return originalScore;
	}

	public int getOriginalScoreInEighteenHole() {
		return originalScore * 18 / holeCount;
	}

	public int getExtraScore() {
		return extraScore;
	}

	public void setExtraScore(int extraScore) {
		this.extraScore = extraScore;
	}

	public int getFinalScore() {
		return originalScore - usedHandicap - extraScore;
	}

	public int getRanking() {
		return ranking;
	}

	public void setRanking(int r) {
		ranking = r;
	}

	public void setLastStanding(boolean s) {
		lastStanding = s;
	}

	public boolean isLastStanding() {
		return lastStanding;
	}

	public void setSameRankingCount(int c) {
		sameRankingCount = c;
	}

	public void increaseSameRankingCount(int c) {
		sameRankingCount += c;
	}

	public int getSameRankingCount() {
		return sameRankingCount;
	}

	public void setOriginalRankingFee(int fee) {
		originalRankingFee = fee;
	}

	public int getOriginalRankingFee() {
		return originalRankingFee;
	}

	public void setAdjustedRankingFee(int fee) {
		adjustedRankingFee = fee;
	}

	public void calculateAvgRanking(int currentHole) {
		if (currentHole < 1) {
			avgRanking = 0F;
		} else {
			avgRanking = (float) holeRankingSum / (float) currentHole;
		}
	}

	public void calculateOverPar(int currentHole) {
		if (currentHole < 1) {
			setAvgOverPar(0F);
		} else {
			setAvgOverPar((float) originalScore / (float) currentHole);
		}
	}

	public void setAvgRanking(float r) {
		avgRanking = r;
	}

	public void addHoleRanking(int ranking) {
		this.holeRankingSum += ranking;
	}

	public void setAvgOverPar(float op) {
		avgOverPar = op;
	}

	public void increaseAdjustedHoleFee(int fee) {
		adjustedHoleFee += fee;
	}

	public int getAdjustedHoleFee() {
		return adjustedHoleFee;
	}

	public void increaseAdjustedRankingFee(int fee) {
		adjustedRankingFee += fee;
	}

	public int getAdjustedRankingFee() {
		return adjustedRankingFee;
	}

	public void decreaseAdjustedRankingFee(int fee) {
		adjustedRankingFee -= fee;
	}

	public void setAdjustedTotalFee(int fee) {
		adjustedTotalFee = fee;
	}

	public int getAdjustedTotalFee() {
		return adjustedTotalFee;
	}

	public String getName() {
		return name;
	}

	public int getHandicap() {
		return handicap;
	}

	public int getUsedHandicap() {
		return usedHandicap;
	}

	public int getRemainHandicap() {
		return handicap - usedHandicap;
	}

	public float getAvgOverPar() {
		return avgOverPar;
	}

	public float getAvgRanking() {
		return avgRanking;
	}
}
