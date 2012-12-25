package org.dolicoli.android.golfscoreboard.data;

@SuppressWarnings("unused")
public class PlayerScore implements Comparable<PlayerScore> {

	private int playerId;
	private String name;
	private int ranking;
	private int sameRankingCount;
	private int remainHandicap;
	private int score, originalScore, extraScore;
	private boolean lastStanding;

	private int holeRankingSum;
	private float avgRanking;
	private float avgOverPar;

	private int originalHoleFee, adjustedHoleFee;
	private int originalRankingFee, adjustedRankingFee;
	private int originalTotalFee, adjustedTotalFee;

	public PlayerScore(int playerId, String name, int handicap) {
		this.playerId = playerId;
		this.name = name;
		this.remainHandicap = handicap;

		this.ranking = 0;
		this.score = 0;
		this.originalScore = 0;
		this.extraScore = 0;

		this.avgRanking = 0.0F;

		this.originalHoleFee = 0;
		this.originalRankingFee = 0;
		this.originalTotalFee = 0;
		this.adjustedHoleFee = 0;
		this.adjustedRankingFee = 0;
		this.adjustedTotalFee = 0;
		this.holeRankingSum = 0;
	}

	public void addHoleRanking(int ranking) {
		this.holeRankingSum += ranking;
	}

	@Override
	public int compareTo(PlayerScore compare) {
		if (ranking < compare.ranking)
			return -1;
		if (ranking > compare.ranking)
			return 1;
		if (score < compare.score)
			return -1;
		if (score > compare.score)
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

	public int getOriginalHoleFee() {
		return originalHoleFee;
	}

	public void increaseScore(int s) {
		score += s;
	}

	public void increaseOriginalScore(int s) {
		originalScore += s;
	}

	public void increaseOriginalHoleFee(int f) {
		originalHoleFee += f;
	}

	public void setAdjustedHoleFee(int f) {
		adjustedHoleFee = f;
	}

	public int getScore() {
		return score;
	}

	public int getOriginalScore() {
		return originalScore;
	}

	public int getExtraScore() {
		return extraScore;
	}

	public void setExtraScore(int extraScore) {
		this.extraScore = extraScore;
	}

	public int getFinalScore() {
		return score - extraScore;
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

	public void setAvgRanking(float r) {
		avgRanking = r;
	}

	public float getHoleRankingSum() {
		return holeRankingSum;
	}

	public void setAvgOverPar(float op) {
		avgOverPar = op;
	}

	public void setOriginalTotalFee(int fee) {
		originalTotalFee = fee;
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
}
