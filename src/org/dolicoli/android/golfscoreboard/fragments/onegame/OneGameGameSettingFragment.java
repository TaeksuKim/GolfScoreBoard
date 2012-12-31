package org.dolicoli.android.golfscoreboard.fragments.onegame;

import org.dolicoli.android.golfscoreboard.Constants;
import org.dolicoli.android.golfscoreboard.OneGameActivity;
import org.dolicoli.android.golfscoreboard.R;
import org.dolicoli.android.golfscoreboard.data.SingleGameResult;
import org.dolicoli.android.golfscoreboard.tasks.HistoryGameSettingQueryTask;
import org.dolicoli.android.golfscoreboard.utils.UIUtil;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class OneGameGameSettingFragment extends Fragment implements
		OneGameActivityPage, HistoryGameSettingQueryTask.TaskListener {

	@SuppressWarnings("unused")
	private static final String TAG = "OneGameGameSettingFragment";

	private TextView holeCountTextView, playerCountTextView;
	private TextView totalFeeTextView, holeFeeTextView, rankingFeeTextView,
			perHoleFeeTextView;
	private TextView[] holeFeePerRankingTextViews;
	private TextView[] rankingFeePerRankingTextViews;
	private View[] holeFeePerRankingTitleTextViews;
	private View[] rankingFeePerRankingTitleTextViews;

	private String playDate;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.onegame_game_setting_fragment,
				null);

		holeCountTextView = (TextView) view
				.findViewById(R.id.HoleCountTextView);
		playerCountTextView = (TextView) view
				.findViewById(R.id.PlayerCountTextView);

		totalFeeTextView = (TextView) view.findViewById(R.id.TotalFeeTextView);
		holeFeeTextView = (TextView) view.findViewById(R.id.HoleFeeTextView);
		rankingFeeTextView = (TextView) view
				.findViewById(R.id.RankingFeeTextView);
		perHoleFeeTextView = (TextView) view
				.findViewById(R.id.PerHoleFeeTextView);

		holeFeePerRankingTextViews = new TextView[Constants.MAX_PLAYER_COUNT];
		rankingFeePerRankingTextViews = new TextView[Constants.MAX_PLAYER_COUNT];
		holeFeePerRankingTitleTextViews = new View[Constants.MAX_PLAYER_COUNT];
		rankingFeePerRankingTitleTextViews = new View[Constants.MAX_PLAYER_COUNT];

		holeFeePerRankingTextViews[0] = (TextView) view
				.findViewById(R.id.HoleFee1TextView);
		holeFeePerRankingTextViews[1] = (TextView) view
				.findViewById(R.id.HoleFee2TextView);
		holeFeePerRankingTextViews[2] = (TextView) view
				.findViewById(R.id.HoleFee3TextView);
		holeFeePerRankingTextViews[3] = (TextView) view
				.findViewById(R.id.HoleFee4TextView);
		holeFeePerRankingTextViews[4] = (TextView) view
				.findViewById(R.id.HoleFee5TextView);
		holeFeePerRankingTextViews[5] = (TextView) view
				.findViewById(R.id.HoleFee6TextView);

		rankingFeePerRankingTextViews[0] = (TextView) view
				.findViewById(R.id.RankingFee1TextView);
		rankingFeePerRankingTextViews[1] = (TextView) view
				.findViewById(R.id.RankingFee2TextView);
		rankingFeePerRankingTextViews[2] = (TextView) view
				.findViewById(R.id.RankingFee3TextView);
		rankingFeePerRankingTextViews[3] = (TextView) view
				.findViewById(R.id.RankingFee4TextView);
		rankingFeePerRankingTextViews[4] = (TextView) view
				.findViewById(R.id.RankingFee5TextView);
		rankingFeePerRankingTextViews[5] = (TextView) view
				.findViewById(R.id.RankingFee6TextView);

		holeFeePerRankingTitleTextViews[0] = view
				.findViewById(R.id.HoleFee1TitleTextView);
		holeFeePerRankingTitleTextViews[1] = view
				.findViewById(R.id.HoleFee2TitleTextView);
		holeFeePerRankingTitleTextViews[2] = view
				.findViewById(R.id.HoleFee3TitleTextView);
		holeFeePerRankingTitleTextViews[3] = view
				.findViewById(R.id.HoleFee4TitleTextView);
		holeFeePerRankingTitleTextViews[4] = view
				.findViewById(R.id.HoleFee5TitleTextView);
		holeFeePerRankingTitleTextViews[5] = view
				.findViewById(R.id.HoleFee6TitleTextView);

		rankingFeePerRankingTitleTextViews[0] = view
				.findViewById(R.id.RankingFee1TitleTextView);
		rankingFeePerRankingTitleTextViews[1] = view
				.findViewById(R.id.RankingFee2TitleTextView);
		rankingFeePerRankingTitleTextViews[2] = view
				.findViewById(R.id.RankingFee3TitleTextView);
		rankingFeePerRankingTitleTextViews[3] = view
				.findViewById(R.id.RankingFee4TitleTextView);
		rankingFeePerRankingTitleTextViews[4] = view
				.findViewById(R.id.RankingFee5TitleTextView);
		rankingFeePerRankingTitleTextViews[5] = view
				.findViewById(R.id.RankingFee6TitleTextView);

		Intent intent = getActivity().getIntent();
		playDate = intent.getStringExtra(OneGameActivity.IK_PLAY_DATE);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		reload();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	private void reload() {
		if (getActivity() == null || playDate == null)
			return;

		HistoryGameSettingQueryTask task = new HistoryGameSettingQueryTask(
				getActivity(), this);
		task.execute(playDate);
	}

	@Override
	public void setHoleNumber(int holeNumber) {
	}

	@Override
	public void onGameQueryStarted() {
	}

	@Override
	public void onGameQueryFinished(SingleGameResult gameResult) {
		FragmentActivity activity = getActivity();
		if (activity == null)
			return;

		int holeCount = gameResult.getHoleCount();
		int playerCount = gameResult.getPlayerCount();

		holeCountTextView.setText(getString(
				R.string.fragment_one_game_gamesetting_hole_count_format,
				holeCount));
		UIUtil.setPlayerCountTextView(activity, playerCountTextView,
				playerCount);

		int holeFee = gameResult.getHoleFee();
		int rankingFee = gameResult.getRankingFee();
		int perHoleFee = 0;
		for (int i = 0; i < Constants.MAX_PLAYER_COUNT; i++) {
			perHoleFee += gameResult.getHoleFeeForRanking(i);
		}

		totalFeeTextView.setText(UIUtil.formatFee(activity, holeFee
				+ rankingFee));
		holeFeeTextView.setText(UIUtil.formatFee(activity, holeFee));
		rankingFeeTextView.setText(UIUtil.formatFee(activity, rankingFee));
		perHoleFeeTextView.setText(UIUtil.formatFee(activity, perHoleFee));

		for (int i = 0; i < Constants.MAX_PLAYER_COUNT; i++) {
			holeFeePerRankingTextViews[i].setText(UIUtil.formatFee(activity,
					gameResult.getHoleFeeForRanking(i + 1)));
			rankingFeePerRankingTextViews[i].setText(UIUtil.formatFee(activity,
					gameResult.getRankingFeeForRanking(i + 1)));

			int remain = playerCount % 2;
			if (i < playerCount) {
				holeFeePerRankingTextViews[i].setVisibility(View.VISIBLE);
				rankingFeePerRankingTextViews[i].setVisibility(View.VISIBLE);
				holeFeePerRankingTitleTextViews[i].setVisibility(View.VISIBLE);
				rankingFeePerRankingTitleTextViews[i]
						.setVisibility(View.VISIBLE);
			} else if (i < playerCount + remain) {
				holeFeePerRankingTextViews[i].setVisibility(View.INVISIBLE);
				rankingFeePerRankingTextViews[i].setVisibility(View.INVISIBLE);
				holeFeePerRankingTitleTextViews[i]
						.setVisibility(View.INVISIBLE);
				rankingFeePerRankingTitleTextViews[i]
						.setVisibility(View.INVISIBLE);
			} else {
				holeFeePerRankingTextViews[i].setVisibility(View.GONE);
				rankingFeePerRankingTextViews[i].setVisibility(View.GONE);
				holeFeePerRankingTitleTextViews[i].setVisibility(View.GONE);
				rankingFeePerRankingTitleTextViews[i].setVisibility(View.GONE);
			}
		}
	}
}
