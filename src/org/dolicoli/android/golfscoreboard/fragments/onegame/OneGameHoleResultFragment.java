package org.dolicoli.android.golfscoreboard.fragments.onegame;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import org.dolicoli.android.golfscoreboard.Constants;
import org.dolicoli.android.golfscoreboard.OneGameActivity;
import org.dolicoli.android.golfscoreboard.R;
import org.dolicoli.android.golfscoreboard.data.SingleGameResult;
import org.dolicoli.android.golfscoreboard.data.settings.Result;
import org.dolicoli.android.golfscoreboard.tasks.HistoryQueryTask;
import org.dolicoli.android.golfscoreboard.utils.FeeCalculator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class OneGameHoleResultFragment extends ListFragment implements
		OneGameActivityPage, HistoryQueryTask.TaskListener {

	private HoleResultListAdapter adapter;
	private String playDate;

	private int holeNumber;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.onegame_result_fragment, null);

		adapter = new HoleResultListAdapter(getActivity(),
				R.layout.onegame_result_list_item);
		adapter.setNotifyOnChange(false);
		setListAdapter(adapter);

		Intent intent = getActivity().getIntent();
		playDate = intent.getStringExtra(OneGameActivity.IK_PLAY_DATE);

		holeNumber = 0;

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		reload();
	}

	@Override
	public void setHoleNumber(int holeNumber) {
		this.holeNumber = holeNumber;
		reload();
	}

	@Override
	public void onGameQueryStarted() {
	}

	@Override
	public void onGameQueryFinished(SingleGameResult gameResult) {
		reloadUI(gameResult);
	}

	private void reload() {
		if (getActivity() == null || playDate == null)
			return;

		HistoryQueryTask task = new HistoryQueryTask(getActivity(), this);
		task.execute(new HistoryQueryTask.QueryParam(playDate, holeNumber));
	}

	private void reloadUI(SingleGameResult gameResult) {
		FragmentActivity activity = getActivity();
		if (activity == null || gameResult == null)
			return;

		int playerCount = gameResult.getPlayerCount();

		SparseArray<HoleResult> holeResultArray = new SparseArray<HoleResult>();

		int maxHoleNumber = 0;
		for (Result result : gameResult.getResults()) {
			int holeNumber = result.getHoleNumber();
			int parNumber = result.getParNumber();
			if (holeNumber > maxHoleNumber)
				maxHoleNumber = holeNumber;
			HoleResult holeResult = holeResultArray.get(holeNumber, null);
			if (holeResult == null) {
				holeResult = new HoleResult();
				holeResultArray.put(holeNumber, holeResult);
			}
			holeResult.holeNumber = holeNumber;
			holeResult.playerCount = playerCount;
			holeResult.parNumber = parNumber;

			int[] fees = FeeCalculator.calculateFee(gameResult, result);

			for (int playerId = 0; playerId < playerCount; playerId++) {
				int holeFee = fees[playerId];

				int ranking = result.getRanking(playerId);
				RankingItem item = new RankingItem(playerId,
						gameResult.getPlayerName(playerId), ranking,
						result.getFinalScore(playerId),
						result.getOriginalScore(playerId),
						result.getUsedHandicap(playerId));

				item.fee = holeFee;

				holeResult.add(item);
			}

			holeResult.sort();
		}

		adapter.clear();
		for (int i = maxHoleNumber; i >= 1; i--) {
			adapter.add(holeResultArray.get(i));
		}
		adapter.notifyDataSetChanged();
	}

	private static class HoleResult {
		private int holeNumber;
		private int playerCount;
		private int parNumber;

		private ArrayList<RankingItem> items;

		public HoleResult() {
			items = new ArrayList<RankingItem>();
		}

		public void add(RankingItem rankingItem) {
			items.add(rankingItem);
		}

		public RankingItem get(int i) {
			return items.get(i);
		}

		public void sort() {
			Collections.sort(items);

			int prevRanking = 0;
			for (RankingItem item : items) {
				if (prevRanking == item.ranking) {
					item.displayRanking = false;
				} else {
					item.displayRanking = true;
				}
				prevRanking = item.ranking;
			}
		}
	}

	private static class RankingItem implements Comparable<RankingItem> {

		private boolean displayRanking;
		private int playerId;
		private String name;
		private int ranking;
		private int finalScore;
		private int score;
		private int fee;
		private int handicap;

		public RankingItem(int playerId, String name, int ranking,
				int finalScore, int score, int handicap) {
			this.displayRanking = true;
			this.playerId = playerId;
			this.name = name;
			this.ranking = ranking;
			this.handicap = handicap;
			this.finalScore = finalScore;
			this.score = score;
			this.fee = 0;
		}

		@Override
		public int compareTo(RankingItem another) {
			if (ranking < another.ranking)
				return -1;
			if (ranking > another.ranking)
				return 1;
			if (playerId < another.playerId)
				return -1;
			if (playerId > another.playerId)
				return 1;
			return 0;
		}
	}

	private static class HoleResultListViewHolder {
		View[] rankingViews;
		TextView holeNumberTextView;
		TextView[] nameTextViews, rankingTextViews, feeTextViews,
				finalScoreTextViews, scoreTextViews, handicapTextViews;
	}

	private class HoleResultListAdapter extends ArrayAdapter<HoleResult> {

		private HoleResultListViewHolder holder;
		private DecimalFormat feeFormat;
		private int defaultTextColor;
		private int textViewResourceId;

		public HoleResultListAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
			this.textViewResourceId = textViewResourceId;
			TypedValue tv = new TypedValue();
			context.getTheme().resolveAttribute(R.attr.primaryTextColor, tv,
					true);
			defaultTextColor = getResources().getColor(tv.resourceId);
			feeFormat = new DecimalFormat(getString(R.string.fee_format));
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getActivity()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(textViewResourceId, null);
				holder = new HoleResultListViewHolder();

				holder.rankingViews = new View[6];
				holder.rankingViews[0] = v.findViewById(R.id.Ranking1Panel);
				holder.rankingViews[1] = v.findViewById(R.id.Ranking2Panel);
				holder.rankingViews[2] = v.findViewById(R.id.Ranking3Panel);
				holder.rankingViews[3] = v.findViewById(R.id.Ranking4Panel);
				holder.rankingViews[4] = v.findViewById(R.id.Ranking5Panel);
				holder.rankingViews[5] = v.findViewById(R.id.Ranking6Panel);

				holder.holeNumberTextView = (TextView) v
						.findViewById(R.id.HoleNumberTextView);

				holder.rankingTextViews = new TextView[6];
				holder.rankingTextViews[0] = (TextView) v
						.findViewById(R.id.Ranking1TextView);
				holder.rankingTextViews[1] = (TextView) v
						.findViewById(R.id.Ranking2TextView);
				holder.rankingTextViews[2] = (TextView) v
						.findViewById(R.id.Ranking3TextView);
				holder.rankingTextViews[3] = (TextView) v
						.findViewById(R.id.Ranking4TextView);
				holder.rankingTextViews[4] = (TextView) v
						.findViewById(R.id.Ranking5TextView);
				holder.rankingTextViews[5] = (TextView) v
						.findViewById(R.id.Ranking6TextView);

				holder.nameTextViews = new TextView[6];
				holder.nameTextViews[0] = (TextView) v
						.findViewById(R.id.Name1TextView);
				holder.nameTextViews[1] = (TextView) v
						.findViewById(R.id.Name2TextView);
				holder.nameTextViews[2] = (TextView) v
						.findViewById(R.id.Name3TextView);
				holder.nameTextViews[3] = (TextView) v
						.findViewById(R.id.Name4TextView);
				holder.nameTextViews[4] = (TextView) v
						.findViewById(R.id.Name5TextView);
				holder.nameTextViews[5] = (TextView) v
						.findViewById(R.id.Name6TextView);

				holder.feeTextViews = new TextView[6];
				holder.feeTextViews[0] = (TextView) v
						.findViewById(R.id.Fee1TextView);
				holder.feeTextViews[1] = (TextView) v
						.findViewById(R.id.Fee2TextView);
				holder.feeTextViews[2] = (TextView) v
						.findViewById(R.id.Fee3TextView);
				holder.feeTextViews[3] = (TextView) v
						.findViewById(R.id.Fee4TextView);
				holder.feeTextViews[4] = (TextView) v
						.findViewById(R.id.Fee5TextView);
				holder.feeTextViews[5] = (TextView) v
						.findViewById(R.id.Fee6TextView);

				holder.finalScoreTextViews = new TextView[6];
				holder.finalScoreTextViews[0] = (TextView) v
						.findViewById(R.id.FinalScore1TextView);
				holder.finalScoreTextViews[1] = (TextView) v
						.findViewById(R.id.FinalScore2TextView);
				holder.finalScoreTextViews[2] = (TextView) v
						.findViewById(R.id.FinalScore3TextView);
				holder.finalScoreTextViews[3] = (TextView) v
						.findViewById(R.id.FinalScore4TextView);
				holder.finalScoreTextViews[4] = (TextView) v
						.findViewById(R.id.FinalScore5TextView);
				holder.finalScoreTextViews[5] = (TextView) v
						.findViewById(R.id.FinalScore6TextView);

				holder.scoreTextViews = new TextView[6];
				holder.scoreTextViews[0] = (TextView) v
						.findViewById(R.id.Score1TextView);
				holder.scoreTextViews[1] = (TextView) v
						.findViewById(R.id.Score2TextView);
				holder.scoreTextViews[2] = (TextView) v
						.findViewById(R.id.Score3TextView);
				holder.scoreTextViews[3] = (TextView) v
						.findViewById(R.id.Score4TextView);
				holder.scoreTextViews[4] = (TextView) v
						.findViewById(R.id.Score5TextView);
				holder.scoreTextViews[5] = (TextView) v
						.findViewById(R.id.Score6TextView);

				holder.handicapTextViews = new TextView[6];
				holder.handicapTextViews[0] = (TextView) v
						.findViewById(R.id.Handicap1TextView);
				holder.handicapTextViews[1] = (TextView) v
						.findViewById(R.id.Handicap2TextView);
				holder.handicapTextViews[2] = (TextView) v
						.findViewById(R.id.Handicap3TextView);
				holder.handicapTextViews[3] = (TextView) v
						.findViewById(R.id.Handicap4TextView);
				holder.handicapTextViews[4] = (TextView) v
						.findViewById(R.id.Handicap5TextView);
				holder.handicapTextViews[5] = (TextView) v
						.findViewById(R.id.Handicap6TextView);

				v.setTag(holder);
			} else {
				holder = (HoleResultListViewHolder) v.getTag();
			}

			int count = getCount();
			if (count < 1)
				return v;

			if (position < 0 || position > count - 1)
				return v;

			HoleResult holeResult = getItem(position);
			if (holeResult == null)
				return v;

			holder.holeNumberTextView.setText(getString(
					R.string.fragment_hole_result_hole_number_format,
					holeResult.holeNumber, holeResult.parNumber));

			int playerCount = holeResult.playerCount;
			for (int i = 0; i < playerCount; i++) {
				RankingItem item = holeResult.get(i);
				holder.rankingViews[i].setVisibility(View.VISIBLE);

				if (item.displayRanking)
					holder.rankingTextViews[i].setVisibility(View.VISIBLE);
				else
					holder.rankingTextViews[i].setVisibility(View.INVISIBLE);

				holder.rankingTextViews[i].setText(getString(
						R.string.ranking_format, item.ranking));
				holder.nameTextViews[i].setText(item.name);

				if (item.score > 0) {
					holder.scoreTextViews[i].setText(getString(
							R.string.positive_format, item.score));
				} else {
					holder.scoreTextViews[i]
							.setText(String.valueOf(item.score));
				}

				if (item.handicap > 0) {
					holder.handicapTextViews[i].setText("-" + item.handicap);
					holder.handicapTextViews[i].setVisibility(View.VISIBLE);
					holder.scoreTextViews[i].setVisibility(View.VISIBLE);
				} else {
					holder.handicapTextViews[i].setText("-");
					holder.handicapTextViews[i].setVisibility(View.GONE);
					holder.scoreTextViews[i].setVisibility(View.GONE);
				}

				holder.feeTextViews[i].setText(feeFormat.format(item.fee));

				if (item.finalScore > 0) {
					holder.finalScoreTextViews[i].setText(getString(
							R.string.positive_format, item.finalScore));
				} else {
					holder.finalScoreTextViews[i].setText(String
							.valueOf(item.finalScore));
				}

				if (item.finalScore < 0) {
					holder.finalScoreTextViews[i]
							.setTextColor(Constants.UNDER_PAR_TEXT_COLOR);
				} else if (item.finalScore == 0) {
					holder.finalScoreTextViews[i]
							.setTextColor(Constants.EVEN_PAR_TEXT_COLOR);
				} else {
					holder.finalScoreTextViews[i]
							.setTextColor(defaultTextColor);
				}
			}

			for (int i = playerCount; i < Constants.MAX_PLAYER_COUNT; i++) {
				holder.rankingViews[i].setVisibility(View.GONE);
			}

			return v;
		}
	}
}
