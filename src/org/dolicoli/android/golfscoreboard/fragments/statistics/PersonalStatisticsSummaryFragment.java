package org.dolicoli.android.golfscoreboard.fragments.statistics;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.dolicoli.android.golfscoreboard.R;
import org.dolicoli.android.golfscoreboard.Reloadable;
import org.dolicoli.android.golfscoreboard.data.GameAndResult;
import org.dolicoli.android.golfscoreboard.data.PlayerScore;
import org.dolicoli.android.golfscoreboard.utils.DateRangeUtil;
import org.dolicoli.android.golfscoreboard.utils.DateRangeUtil.DateRange;
import org.dolicoli.android.golfscoreboard.utils.UIUtil;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class PersonalStatisticsSummaryFragment extends Fragment implements
		Reloadable, OnClickListener {

	private PersonalStatisticsDataContainer dataContainer;
	private ArrayList<GameAndResult> gameAndResults;
	private String playerName;

	private View recent5MainView, recent5DetailView;
	private Button recent5ExpandButton;
	private TextView attendCountRecent5TextView;
	private TextView avgRankingRecent5TextView, minRankingRecent5TextView,
			maxRankingRecent5TextView;
	private TextView firstStandingRecent5TextView,
			firstStandingRateRecent5TextView;
	private TextView lastStandingRecent5TextView,
			lastStandingRateRecent5TextView;
	private TextView avgStrokeCountRecent5TextView,
			minStrokeCountRecent5TextView, maxStrokeCountRecent5TextView;
	private TextView avgFeeRecent5TextView, minFeeRecent5TextView,
			maxFeeRecent5TextView, totalFeeRecent5TextView;

	private View thisMonthMainView, thisMonthDetailView;
	private Button thisMonthExpandButton;
	private TextView attendCount1MonthTextView;
	private TextView avgRanking1MonthTextView, minRanking1MonthTextView,
			maxRanking1MonthTextView;
	private TextView firstStanding1MonthTextView,
			firstStandingRate1MonthTextView;
	private TextView lastStanding1MonthTextView,
			lastStandingRate1MonthTextView;
	private TextView avgStrokeCount1MonthTextView,
			minStrokeCount1MonthTextView, maxStrokeCount1MonthTextView;
	private TextView avgFee1MonthTextView, minFee1MonthTextView,
			maxFee1MonthTextView, totalFee1MonthTextView;

	private View lastMonthMainView, lastMonthDetailView;
	private Button lastMonthExpandButton;
	private TextView attendCount2MonthTextView;
	private TextView avgRanking2MonthTextView, minRanking2MonthTextView,
			maxRanking2MonthTextView;
	private TextView firstStanding2MonthTextView,
			firstStandingRate2MonthTextView;
	private TextView lastStanding2MonthTextView,
			lastStandingRate2MonthTextView;
	private TextView avgStrokeCount2MonthTextView,
			minStrokeCount2MonthTextView, maxStrokeCount2MonthTextView;
	private TextView avgFee2MonthTextView, minFee2MonthTextView,
			maxFee2MonthTextView, totalFee2MonthTextView;

	private View recent3MonthMainView, recent3MonthDetailView;
	private Button recent3MonthExpandButton;
	private TextView attendCount3MonthTextView;
	private TextView avgRanking3MonthTextView, minRanking3MonthTextView,
			maxRanking3MonthTextView;
	private TextView firstStanding3MonthTextView,
			firstStandingRate3MonthTextView;
	private TextView lastStanding3MonthTextView,
			lastStandingRate3MonthTextView;
	private TextView avgStrokeCount3MonthTextView,
			minStrokeCount3MonthTextView, maxStrokeCount3MonthTextView;
	private TextView avgFee3MonthTextView, minFee3MonthTextView,
			maxFee3MonthTextView, totalFee3MonthTextView;

	public void setDataContainer(PersonalStatisticsDataContainer container) {
		this.dataContainer = container;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(
				R.layout.personal_statistics_summary_fragment, null);

		View recent5View = view.findViewById(R.id.Recent5SummaryItem);
		recent5View.setOnClickListener(this);

		recent5MainView = recent5View.findViewById(R.id.MainView);
		recent5DetailView = recent5View.findViewById(R.id.DetailView);
		recent5ExpandButton = (Button) recent5View
				.findViewById(R.id.ShowMoreButton);
		attendCountRecent5TextView = (TextView) recent5View
				.findViewById(R.id.AttendCountTextView);
		avgRankingRecent5TextView = (TextView) recent5View
				.findViewById(R.id.AvgRankingTextView);
		minRankingRecent5TextView = (TextView) view
				.findViewById(R.id.MinRankingTextView);
		maxRankingRecent5TextView = (TextView) view
				.findViewById(R.id.MaxRankingTextView);

		firstStandingRecent5TextView = (TextView) recent5View
				.findViewById(R.id.FirstStandTextView);
		firstStandingRateRecent5TextView = (TextView) recent5View
				.findViewById(R.id.FirstStandRateTextView);
		lastStandingRecent5TextView = (TextView) recent5View
				.findViewById(R.id.LastStandTextView);
		lastStandingRateRecent5TextView = (TextView) recent5View
				.findViewById(R.id.LastStandRateTextView);

		avgStrokeCountRecent5TextView = (TextView) recent5View
				.findViewById(R.id.AvgStrokeCountTextView);
		minStrokeCountRecent5TextView = (TextView) recent5View
				.findViewById(R.id.MinStrokeCountTextView);
		maxStrokeCountRecent5TextView = (TextView) recent5View
				.findViewById(R.id.MaxStrokeCountTextView);

		avgFeeRecent5TextView = (TextView) recent5View
				.findViewById(R.id.AvgFeeTextView);
		minFeeRecent5TextView = (TextView) recent5View
				.findViewById(R.id.MinFeeTextView);
		maxFeeRecent5TextView = (TextView) recent5View
				.findViewById(R.id.MaxFeeTextView);
		totalFeeRecent5TextView = (TextView) recent5View
				.findViewById(R.id.TotalFeeTextView);

		View thisMonthView = view.findViewById(R.id.ThisMonthSummaryItem);

		thisMonthMainView = thisMonthView.findViewById(R.id.MainView);
		thisMonthDetailView = thisMonthView.findViewById(R.id.DetailView);
		thisMonthExpandButton = (Button) thisMonthView
				.findViewById(R.id.ShowMoreButton);
		attendCount1MonthTextView = (TextView) thisMonthView
				.findViewById(R.id.AttendCountTextView);
		avgRanking1MonthTextView = (TextView) thisMonthView
				.findViewById(R.id.AvgRankingTextView);
		minRanking1MonthTextView = (TextView) thisMonthView
				.findViewById(R.id.MinRankingTextView);
		maxRanking1MonthTextView = (TextView) thisMonthView
				.findViewById(R.id.MaxRankingTextView);

		firstStanding1MonthTextView = (TextView) thisMonthView
				.findViewById(R.id.FirstStandTextView);
		firstStandingRate1MonthTextView = (TextView) thisMonthView
				.findViewById(R.id.FirstStandRateTextView);
		lastStanding1MonthTextView = (TextView) thisMonthView
				.findViewById(R.id.LastStandTextView);
		lastStandingRate1MonthTextView = (TextView) thisMonthView
				.findViewById(R.id.LastStandRateTextView);

		avgStrokeCount1MonthTextView = (TextView) thisMonthView
				.findViewById(R.id.AvgStrokeCountTextView);
		minStrokeCount1MonthTextView = (TextView) thisMonthView
				.findViewById(R.id.MinStrokeCountTextView);
		maxStrokeCount1MonthTextView = (TextView) thisMonthView
				.findViewById(R.id.MaxStrokeCountTextView);

		avgFee1MonthTextView = (TextView) thisMonthView
				.findViewById(R.id.AvgFeeTextView);
		minFee1MonthTextView = (TextView) thisMonthView
				.findViewById(R.id.MinFeeTextView);
		maxFee1MonthTextView = (TextView) thisMonthView
				.findViewById(R.id.MaxFeeTextView);
		totalFee1MonthTextView = (TextView) thisMonthView
				.findViewById(R.id.TotalFeeTextView);

		View lastMonthView = view.findViewById(R.id.LastMonthSummaryItem);

		lastMonthMainView = lastMonthView.findViewById(R.id.MainView);
		lastMonthDetailView = lastMonthView.findViewById(R.id.DetailView);
		lastMonthExpandButton = (Button) lastMonthView
				.findViewById(R.id.ShowMoreButton);
		attendCount2MonthTextView = (TextView) lastMonthView
				.findViewById(R.id.AttendCountTextView);
		avgRanking2MonthTextView = (TextView) lastMonthView
				.findViewById(R.id.AvgRankingTextView);
		minRanking2MonthTextView = (TextView) lastMonthView
				.findViewById(R.id.MinRankingTextView);
		maxRanking2MonthTextView = (TextView) lastMonthView
				.findViewById(R.id.MaxRankingTextView);

		firstStanding2MonthTextView = (TextView) lastMonthView
				.findViewById(R.id.FirstStandTextView);
		firstStandingRate2MonthTextView = (TextView) lastMonthView
				.findViewById(R.id.FirstStandRateTextView);
		lastStanding2MonthTextView = (TextView) lastMonthView
				.findViewById(R.id.LastStandTextView);
		lastStandingRate2MonthTextView = (TextView) lastMonthView
				.findViewById(R.id.LastStandRateTextView);

		avgStrokeCount2MonthTextView = (TextView) lastMonthView
				.findViewById(R.id.AvgStrokeCountTextView);
		minStrokeCount2MonthTextView = (TextView) lastMonthView
				.findViewById(R.id.MinStrokeCountTextView);
		maxStrokeCount2MonthTextView = (TextView) lastMonthView
				.findViewById(R.id.MaxStrokeCountTextView);

		avgFee2MonthTextView = (TextView) lastMonthView
				.findViewById(R.id.AvgFeeTextView);
		minFee2MonthTextView = (TextView) lastMonthView
				.findViewById(R.id.MinFeeTextView);
		maxFee2MonthTextView = (TextView) lastMonthView
				.findViewById(R.id.MaxFeeTextView);
		totalFee2MonthTextView = (TextView) lastMonthView
				.findViewById(R.id.TotalFeeTextView);

		View recent3MonthView = view.findViewById(R.id.Last3MonthSummaryItem);

		recent3MonthMainView = recent3MonthView.findViewById(R.id.MainView);
		recent3MonthDetailView = recent3MonthView.findViewById(R.id.DetailView);
		recent3MonthExpandButton = (Button) recent3MonthView
				.findViewById(R.id.ShowMoreButton);
		attendCount3MonthTextView = (TextView) recent3MonthView
				.findViewById(R.id.AttendCountTextView);
		avgRanking3MonthTextView = (TextView) recent3MonthView
				.findViewById(R.id.AvgRankingTextView);
		minRanking3MonthTextView = (TextView) recent3MonthView
				.findViewById(R.id.MinRankingTextView);
		maxRanking3MonthTextView = (TextView) recent3MonthView
				.findViewById(R.id.MaxRankingTextView);

		firstStanding3MonthTextView = (TextView) recent3MonthView
				.findViewById(R.id.FirstStandTextView);
		firstStandingRate3MonthTextView = (TextView) recent3MonthView
				.findViewById(R.id.FirstStandRateTextView);
		lastStanding3MonthTextView = (TextView) recent3MonthView
				.findViewById(R.id.LastStandTextView);
		lastStandingRate3MonthTextView = (TextView) recent3MonthView
				.findViewById(R.id.LastStandRateTextView);

		avgStrokeCount3MonthTextView = (TextView) recent3MonthView
				.findViewById(R.id.AvgStrokeCountTextView);
		minStrokeCount3MonthTextView = (TextView) recent3MonthView
				.findViewById(R.id.MinStrokeCountTextView);
		maxStrokeCount3MonthTextView = (TextView) recent3MonthView
				.findViewById(R.id.MaxStrokeCountTextView);

		avgFee3MonthTextView = (TextView) recent3MonthView
				.findViewById(R.id.AvgFeeTextView);
		minFee3MonthTextView = (TextView) recent3MonthView
				.findViewById(R.id.MinFeeTextView);
		maxFee3MonthTextView = (TextView) recent3MonthView
				.findViewById(R.id.MaxFeeTextView);
		totalFee3MonthTextView = (TextView) recent3MonthView
				.findViewById(R.id.TotalFeeTextView);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		recent5MainView.setOnClickListener(this);
		recent5DetailView.setOnClickListener(this);

		thisMonthMainView.setOnClickListener(this);
		thisMonthDetailView.setOnClickListener(this);

		lastMonthMainView.setOnClickListener(this);
		lastMonthDetailView.setOnClickListener(this);

		recent3MonthMainView.setOnClickListener(this);
		recent3MonthDetailView.setOnClickListener(this);

		if (dataContainer != null) {
			gameAndResults = dataContainer.getGameAndResults();
			playerName = dataContainer.getPlayerName();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		reload();
	}

	@Override
	public void reload() {
		if (dataContainer == null)
			return;

		int recent5Count = 0;
		int recent5FirstStandingCount = 0, recent5LastStandingCount = 0;
		int recent5RankingSum = 0, recent5StrokeCountSum = 0, recent5FeeSum = 0;
		int recent5RankingMin = -1, recent5RankingMax = -1;
		int recent5StrokeCountMin = -1, recent5StrokeCountMax = -1;
		int recent5FeeMin = -1, recent5FeeMax = -1;

		int month1Count = 0;
		int month1FirstStandingCount = 0, month1LastStandingCount = 0;
		int month1RankingSum = 0, month1StrokeCountSum = 0, month1FeeSum = 0;
		int month1RankingMin = -1, month1RankingMax = -1;
		int month1StrokeCountMin = -1, month1StrokeCountMax = -1;
		int month1FeeMin = -1, month1FeeMax = -1;

		int month2Count = 0;
		int month2FirstStandingCount = 0, month2LastStandingCount = 0;
		int month2RankingSum = 0, month2StrokeCountSum = 0, month2FeeSum = 0;
		int month2RankingMin = -1, month2RankingMax = -1;
		int month2StrokeCountMin = -1, month2StrokeCountMax = -1;
		int month2FeeMin = -1, month2FeeMax = -1;

		int month3Count = 0;
		int month3FirstStandingCount = 0, month3LastStandingCount = 0;
		int month3RankingSum = 0, month3StrokeCountSum = 0, month3FeeSum = 0;
		int month3RankingMin = -1, month3RankingMax = -1;
		int month3StrokeCountMin = -1, month3StrokeCountMax = -1;
		int month3FeeMin = -1, month3FeeMax = -1;

		DateRange thisMonthRange = DateRangeUtil.getMonthlyDateRange(0);
		DateRange lastMonthRange = DateRangeUtil.getMonthlyDateRange(1);

		for (GameAndResult gameAndResult : gameAndResults) {
			if (!gameAndResult.containsPlayerScore(playerName))
				continue;

			PlayerScore playerScore = gameAndResult.getPlayerScore(playerName);
			int ranking = playerScore.getRanking();
			boolean lastStanding = playerScore.isLastStanding();
			int fee = playerScore.getAdjustedTotalFee();
			int score = playerScore.getScore();

			if (recent5Count < 5) {
				recent5RankingSum += ranking;

				if (ranking == 1) {
					recent5FirstStandingCount++;
				}

				if (lastStanding) {
					recent5LastStandingCount++;
				}

				if (recent5RankingMin < 0 || recent5RankingMin > ranking) {
					recent5RankingMin = ranking;
				}

				if (recent5RankingMax < 0 || recent5RankingMax < ranking) {
					recent5RankingMax = ranking;
				}

				recent5StrokeCountSum += score;

				if (recent5StrokeCountMin < 0 || recent5StrokeCountMin > score) {
					recent5StrokeCountMin = score;
				}

				if (recent5StrokeCountMax < 0 || recent5StrokeCountMax < score) {
					recent5StrokeCountMax = score;
				}

				recent5FeeSum += fee;

				if (recent5FeeMin < 0 || recent5FeeMin > fee) {
					recent5FeeMin = fee;
				}

				if (recent5FeeMax < 0 || recent5FeeMax < fee) {
					recent5FeeMax = fee;
				}

				recent5Count++;
			}

			long date = gameAndResult.getGameSetting().getDate().getTime();
			if (date >= thisMonthRange.getFrom()) {
				month1RankingSum += ranking;

				if (ranking == 1) {
					month1FirstStandingCount++;
					month3FirstStandingCount++;
				}

				if (lastStanding) {
					month1LastStandingCount++;
					month3LastStandingCount++;
				}

				if (month1RankingMin < 0 || month1RankingMin > ranking) {
					month1RankingMin = ranking;
				}

				if (month1RankingMax < 0 || month1RankingMax < ranking) {
					month1RankingMax = ranking;
				}

				month1StrokeCountSum += score;

				if (month1StrokeCountMin < 0 || month1StrokeCountMin > score) {
					month1StrokeCountMin = score;
				}

				if (month1StrokeCountMax < 0 || month1StrokeCountMax < score) {
					month1StrokeCountMax = score;
				}

				month1FeeSum += fee;

				if (month1FeeMin < 0 || month1FeeMin > fee) {
					month1FeeMin = fee;
				}

				if (month1FeeMax < 0 || month1FeeMax < fee) {
					month1FeeMax = fee;
				}
				month1Count++;

				month3RankingSum += ranking;

				if (month3RankingMin < 0 || month3RankingMin > ranking) {
					month3RankingMin = ranking;
				}

				if (month3RankingMax < 0 || month3RankingMax < ranking) {
					month3RankingMax = ranking;
				}

				month3StrokeCountSum += score;

				if (month3StrokeCountMin < 0 || month3StrokeCountMin > score) {
					month3StrokeCountMin = score;
				}

				if (month3StrokeCountMax < 0 || month3StrokeCountMax < score) {
					month3StrokeCountMax = score;
				}

				month3FeeSum += fee;

				if (month3FeeMin < 0 || month3FeeMin > fee) {
					month3FeeMin = fee;
				}

				if (month3FeeMax < 0 || month3FeeMax < fee) {
					month3FeeMax = fee;
				}
				month3Count++;
			} else if (date >= lastMonthRange.getFrom()) {

				month2RankingSum += ranking;

				if (ranking == 1) {
					month2FirstStandingCount++;
					month3FirstStandingCount++;
				}

				if (lastStanding) {
					month2LastStandingCount++;
					month3LastStandingCount++;
				}

				if (month2RankingMin < 0 || month2RankingMin > ranking) {
					month2RankingMin = ranking;
				}

				if (month2RankingMax < 0 || month2RankingMax < ranking) {
					month2RankingMax = ranking;
				}

				month2StrokeCountSum += score;

				if (month2StrokeCountMin < 0 || month2StrokeCountMin > score) {
					month2StrokeCountMin = score;
				}

				if (month2StrokeCountMax < 0 || month2StrokeCountMax < score) {
					month2StrokeCountMax = score;
				}

				month2FeeSum += fee;

				if (month2FeeMin < 0 || month2FeeMin > fee) {
					month2FeeMin = fee;
				}

				if (month2FeeMax < 0 || month2FeeMax < fee) {
					month2FeeMax = fee;
				}

				month2Count++;

				month3RankingSum += ranking;

				if (month3RankingMin < 0 || month3RankingMin > ranking) {
					month3RankingMin = ranking;
				}

				if (month3RankingMax < 0 || month3RankingMax < ranking) {
					month3RankingMax = ranking;
				}

				month3StrokeCountSum += score;

				if (month3StrokeCountMin < 0 || month3StrokeCountMin > score) {
					month3StrokeCountMin = score;
				}

				if (month3StrokeCountMax < 0 || month3StrokeCountMax < score) {
					month3StrokeCountMax = score;
				}

				month3FeeSum += fee;

				if (month3FeeMin < 0 || month3FeeMin > fee) {
					month3FeeMin = fee;
				}

				if (month3FeeMax < 0 || month3FeeMax < fee) {
					month3FeeMax = fee;
				}

				month3Count++;
			} else {

				month3RankingSum += ranking;

				if (ranking == 1) {
					month3FirstStandingCount++;
				}

				if (lastStanding) {
					month3LastStandingCount++;
				}

				if (month3RankingMin < 0 || month3RankingMin > ranking) {
					month3RankingMin = ranking;
				}

				if (month3RankingMax < 0 || month3RankingMax < ranking) {
					month3RankingMax = ranking;
				}

				month3StrokeCountSum += score;

				if (month3StrokeCountMin < 0 || month3StrokeCountMin > score) {
					month3StrokeCountMin = score;
				}

				if (month3StrokeCountMax < 0 || month3StrokeCountMax < score) {
					month3StrokeCountMax = score;
				}

				month3FeeSum += fee;

				if (month3FeeMin < 0 || month3FeeMin > fee) {
					month3FeeMin = fee;
				}

				if (month3FeeMax < 0 || month3FeeMax < fee) {
					month3FeeMax = fee;
				}

				month3Count++;
			}
		}

		fillRecent5Fields(recent5Count, recent5RankingSum, recent5RankingMax,
				recent5RankingMin, recent5FirstStandingCount,
				recent5LastStandingCount, recent5StrokeCountSum,
				recent5StrokeCountMax, recent5StrokeCountMin, recent5FeeSum,
				recent5FeeMax, recent5FeeMin, attendCountRecent5TextView,
				avgRankingRecent5TextView, maxRankingRecent5TextView,
				minRankingRecent5TextView, firstStandingRecent5TextView,
				firstStandingRateRecent5TextView, lastStandingRecent5TextView,
				lastStandingRateRecent5TextView, avgStrokeCountRecent5TextView,
				maxStrokeCountRecent5TextView, minStrokeCountRecent5TextView,
				avgFeeRecent5TextView, maxFeeRecent5TextView,
				minFeeRecent5TextView, totalFeeRecent5TextView);

		fillRecent5Fields(month1Count, month1RankingSum, month1RankingMax,
				month1RankingMin, month1FirstStandingCount,
				month1LastStandingCount, month1StrokeCountSum,
				month1StrokeCountMax, month1StrokeCountMin, month1FeeSum,
				month1FeeMax, month1FeeMin, attendCount1MonthTextView,
				avgRanking1MonthTextView, maxRanking1MonthTextView,
				minRanking1MonthTextView, firstStanding1MonthTextView,
				firstStandingRate1MonthTextView, lastStanding1MonthTextView,
				lastStandingRate1MonthTextView, avgStrokeCount1MonthTextView,
				maxStrokeCount1MonthTextView, minStrokeCount1MonthTextView,
				avgFee1MonthTextView, maxFee1MonthTextView,
				minFee1MonthTextView, totalFee1MonthTextView);

		fillRecent5Fields(month2Count, month2RankingSum, month2RankingMax,
				month2RankingMin, month2FirstStandingCount,
				month2LastStandingCount, month2StrokeCountSum,
				month2StrokeCountMax, month2StrokeCountMin, month2FeeSum,
				month2FeeMax, month2FeeMin, attendCount2MonthTextView,
				avgRanking2MonthTextView, maxRanking2MonthTextView,
				minRanking2MonthTextView, firstStanding2MonthTextView,
				firstStandingRate2MonthTextView, lastStanding2MonthTextView,
				lastStandingRate2MonthTextView, avgStrokeCount2MonthTextView,
				maxStrokeCount2MonthTextView, minStrokeCount2MonthTextView,
				avgFee2MonthTextView, maxFee2MonthTextView,
				minFee2MonthTextView, totalFee2MonthTextView);

		fillRecent5Fields(month3Count, month3RankingSum, month3RankingMax,
				month3RankingMin, month3FirstStandingCount,
				month3LastStandingCount, month3StrokeCountSum,
				month3StrokeCountMax, month3StrokeCountMin, month3FeeSum,
				month3FeeMax, month3FeeMin, attendCount3MonthTextView,
				avgRanking3MonthTextView, maxRanking3MonthTextView,
				minRanking3MonthTextView, firstStanding3MonthTextView,
				firstStandingRate3MonthTextView, lastStanding3MonthTextView,
				lastStandingRate3MonthTextView, avgStrokeCount3MonthTextView,
				maxStrokeCount3MonthTextView, minStrokeCount3MonthTextView,
				avgFee3MonthTextView, maxFee3MonthTextView,
				minFee3MonthTextView, totalFee3MonthTextView);
	}

	private void fillRecent5Fields(int count, int rankingSum, int rankingMax,
			int rankingMin, int firstStandingCount, int lastStandingCount,
			int strokeCountSum, int strokeCountMax, int strokeCountMin,
			int feeSum, int feeMax, int feeMin, TextView attendCountTextView,
			TextView avgRankingTextView, TextView maxRankingTextView,
			TextView minRankingTextView, TextView firstStandingTextView,
			TextView firstStandingRateTextView, TextView lastStandingTextView,
			TextView lastStandingRateTextView, TextView avgStrokeCountTextView,
			TextView maxStrokeCountTextView, TextView minStrokeCountTextView,
			TextView avgFeeTextView, TextView maxFeeTextView,
			TextView minFeeTextView, TextView totalFeeTextView) {

		DecimalFormat rateFormat = new DecimalFormat("0.00 %");
		FragmentActivity activity = getActivity();

		if (count < 1) {
			avgRankingTextView.setText("-");
			avgStrokeCountTextView.setText("-");
			avgFeeTextView.setText("-");
			firstStandingTextView.setText("- ȸ");
			firstStandingRateTextView.setText("- %");
			lastStandingTextView.setText("- ȸ");
			lastStandingRateTextView.setText("- %");
		} else {
			double avgRanking = ((double) rankingSum) / ((double) count);
			UIUtil.setAvgRankingTextView(activity, avgRankingTextView,
					avgRanking);

			double avgStrokeCount = ((double) strokeCountSum)
					/ ((double) count);
			UIUtil.setAvgScoreTextView(activity, avgStrokeCountTextView,
					avgStrokeCount);

			double avgFee = ((double) feeSum) / ((double) count);
			UIUtil.setFeeTextView(activity, avgFeeTextView, avgFee);

			double firstStandingRate = ((double) firstStandingCount)
					/ ((double) count);
			double lastStandingRate = ((double) lastStandingCount)
					/ ((double) count);
			firstStandingTextView.setText(firstStandingCount + " ȸ");
			firstStandingRateTextView.setText(rateFormat
					.format(firstStandingRate));
			lastStandingTextView.setText(lastStandingCount + " ȸ");
			lastStandingRateTextView.setText(rateFormat
					.format(lastStandingRate));
		}

		if (attendCountTextView != null) {
			attendCountTextView.setText(count + " ȸ");
		}

		if (rankingMax < 1) {
			maxRankingTextView.setText("-");
		} else {
			UIUtil.setRankingTextView(activity, maxRankingTextView, rankingMax);
		}
		if (rankingMin < 1) {
			minRankingTextView.setText("-");
		} else {
			UIUtil.setRankingTextView(activity, minRankingTextView, rankingMin);
		}
		if (strokeCountMax < 1) {
			maxStrokeCountTextView.setText("-");
		} else {
			UIUtil.setScoreTextView(activity, maxStrokeCountTextView,
					strokeCountMax);
		}
		if (strokeCountMin < 1) {
			minStrokeCountTextView.setText("-");
		} else {
			UIUtil.setScoreTextView(activity, minStrokeCountTextView,
					strokeCountMin);
		}
		if (feeMax < 1) {
			maxFeeTextView.setText("-");
		} else {
			UIUtil.setFeeTextView(activity, maxFeeTextView, feeMax);
		}
		if (feeMin < 1) {
			minFeeTextView.setText("-");
		} else {
			UIUtil.setFeeTextView(activity, minFeeTextView, feeMin);
		}
		UIUtil.setFeeTextView(activity, totalFeeTextView, feeSum);
	}

	@Override
	public void onClick(View view) {
		if (getActivity() == null || view == null || recent5MainView == null)
			return;

		if (view == recent5MainView || view == recent5DetailView) {
			if (recent5DetailView.getVisibility() == View.VISIBLE) {
				recent5DetailView.setVisibility(View.GONE);
				recent5ExpandButton.setBackgroundResource(R.drawable.ic_expand);
			} else {
				recent5DetailView.setVisibility(View.VISIBLE);
				recent5ExpandButton
						.setBackgroundResource(R.drawable.ic_collapse);
			}
		} else if (view == thisMonthMainView || view == thisMonthDetailView) {
			if (thisMonthDetailView.getVisibility() == View.VISIBLE) {
				thisMonthDetailView.setVisibility(View.GONE);
				thisMonthExpandButton
						.setBackgroundResource(R.drawable.ic_expand);
			} else {
				thisMonthDetailView.setVisibility(View.VISIBLE);
				thisMonthExpandButton
						.setBackgroundResource(R.drawable.ic_collapse);
			}
		} else if (view == lastMonthMainView || view == lastMonthDetailView) {
			if (lastMonthDetailView.getVisibility() == View.VISIBLE) {
				lastMonthDetailView.setVisibility(View.GONE);
				lastMonthExpandButton
						.setBackgroundResource(R.drawable.ic_expand);
			} else {
				lastMonthDetailView.setVisibility(View.VISIBLE);
				lastMonthExpandButton
						.setBackgroundResource(R.drawable.ic_collapse);
			}
		} else if (view == recent3MonthMainView
				|| view == recent3MonthDetailView) {
			if (recent3MonthDetailView.getVisibility() == View.VISIBLE) {
				recent3MonthDetailView.setVisibility(View.GONE);
				recent3MonthExpandButton
						.setBackgroundResource(R.drawable.ic_expand);
			} else {
				recent3MonthDetailView.setVisibility(View.VISIBLE);
				recent3MonthExpandButton
						.setBackgroundResource(R.drawable.ic_collapse);
			}
		}
	}
}
