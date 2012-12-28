package org.dolicoli.android.golfscoreboard.fragments.onegame;

import java.util.Comparator;

import org.dolicoli.android.golfscoreboard.R;
import org.dolicoli.android.golfscoreboard.data.SingleGameResult;
import org.dolicoli.android.golfscoreboard.data.settings.Result;
import org.dolicoli.android.golfscoreboard.tasks.CurrentGameQueryTask;
import org.dolicoli.android.golfscoreboard.tasks.HistoryQueryTask;
import org.dolicoli.android.golfscoreboard.utils.FeeCalculator;
import org.dolicoli.android.golfscoreboard.utils.UIUtil;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class OneGamePlayerHoleRecordFragment extends ListFragment implements
		OneGamePlayerRecordActivityPage, CurrentGameQueryTask.TaskListener,
		HistoryQueryTask.TaskListener {

	private ScoreListAdapter adapter;

	private int playerId;
	private String playDate;
	private boolean currentGame;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(
				R.layout.onegame_player_hole_record_fragment, null);

		playerId = 0;

		adapter = new ScoreListAdapter(getActivity(),
				R.layout.onegame_player_hole_record_list_item);
		adapter.setNotifyOnChange(false);
		setListAdapter(adapter);

		return view;
	}

	@Override
	public void setPlayerId(boolean currentGame, int playerId, String playDate) {
		this.currentGame = currentGame;
		this.playerId = playerId;
		this.playDate = playDate;
		reload();
	}

	@Override
	public void onCurrentGameQueryStarted() {
	}

	@Override
	public void onCurrentGameQueryFinished(SingleGameResult gameResult) {
		reloadUI(gameResult);
	}

	private void reload() {
		FragmentActivity activity = getActivity();
		if (activity == null)
			return;

		if (currentGame) {
			CurrentGameQueryTask task = new CurrentGameQueryTask(activity, this);
			task.execute();
		} else {
			HistoryQueryTask task = new HistoryQueryTask(activity, this);
			task.execute(new HistoryQueryTask.QueryParam(playDate));
		}
	}

	private void reloadUI(SingleGameResult gameResult) {
		if (getActivity() == null || gameResult == null)
			return;

		adapter.clear();
		for (Result result : gameResult.getResults()) {
			int holeNumber = result.getHoleNumber();
			int parNumber = result.getParNumber();
			int ranking = result.getRanking(playerId);
			int score = result.getOriginalScore(playerId);
			int handicap = result.getUsedHandicap(playerId);
			int sameRankingCount = result.getSameRankingCount(playerId);

			int[] fees = FeeCalculator.calculateFee(gameResult, result);
			int holeFee = fees[playerId];

			adapter.add(new PlayerRecord(holeNumber, parNumber, ranking, score,
					handicap, holeFee, sameRankingCount));
		}
		adapter.sort();
		adapter.notifyDataSetChanged();
	}

	private static class PlayerRecord {
		private int holeNumber, parNumber;

		private int ranking;
		private int score;
		private int finalScore;
		private int handicap;
		private int sameRankingCount;
		private int fee;

		public PlayerRecord(int holeNumber, int parNumber, int ranking,
				int score, int handicap, int fee, int sameRankingCount) {
			this.holeNumber = holeNumber;
			this.parNumber = parNumber;
			this.ranking = ranking;
			this.score = score;
			this.handicap = handicap;
			this.finalScore = (score - handicap);
			this.fee = fee;
			this.sameRankingCount = sameRankingCount;
		}
	}

	private static class ScoreListViewHolder {
		TextView holeTextView, feeTextView, rankingTextView;
		TextView sameRankingCountTextView;
		TextView scoreTextView, handicapTextView, finalScoreTextView;
	}

	private class ScoreListAdapter extends ArrayAdapter<PlayerRecord> {

		private ScoreListViewHolder holder;
		private int textViewResourceId;

		public ScoreListAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
			this.textViewResourceId = textViewResourceId;
		}

		public void sort() {
			super.sort(new Comparator<PlayerRecord>() {
				@Override
				public int compare(PlayerRecord lhs, PlayerRecord rhs) {
					if (lhs.holeNumber < rhs.holeNumber)
						return 1;
					if (lhs.holeNumber > rhs.holeNumber)
						return -1;
					return 0;
				}
			});
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getActivity()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(textViewResourceId, null);
				holder = new ScoreListViewHolder();
				holder.holeTextView = (TextView) v
						.findViewById(R.id.HoleTextView);
				holder.feeTextView = (TextView) v
						.findViewById(R.id.FeeTextView);
				holder.rankingTextView = (TextView) v
						.findViewById(R.id.RankingTextView);
				holder.sameRankingCountTextView = (TextView) v
						.findViewById(R.id.SameRankingCountTextView);
				holder.scoreTextView = (TextView) v
						.findViewById(R.id.ScoreTextView);
				holder.handicapTextView = (TextView) v
						.findViewById(R.id.HandicapTextView);
				holder.finalScoreTextView = (TextView) v
						.findViewById(R.id.FinalScoreTextView);
				v.setTag(holder);
			} else {
				holder = (ScoreListViewHolder) v.getTag();
			}

			int count = getCount();
			if (count < 1)
				return v;

			if (position < 0 || position > count - 1)
				return v;

			PlayerRecord playerScore = getItem(position);
			if (playerScore == null)
				return v;

			holder.holeTextView
					.setText(getString(
							R.string.fragment_onegame_player_hole_record_hole_number_format,
							playerScore.holeNumber, playerScore.parNumber));

			UIUtil.setScoreTextView(getContext(), holder.scoreTextView,
					playerScore.score);

			if (playerScore.handicap > 0) {
				holder.handicapTextView.setText("-" + playerScore.handicap);

				holder.scoreTextView.setVisibility(View.VISIBLE);
				holder.handicapTextView.setVisibility(View.VISIBLE);
			} else {
				holder.scoreTextView.setVisibility(View.INVISIBLE);
				holder.handicapTextView.setVisibility(View.INVISIBLE);
			}

			UIUtil.setFeeTextView(getContext(), holder.feeTextView,
					playerScore.fee);

			UIUtil.setRankingTextView(getContext(), holder.rankingTextView,
					playerScore.ranking);

			if (playerScore.sameRankingCount > 1) {
				holder.sameRankingCountTextView
						.setText(getString(
								R.string.fragment_onegame_player_hole_record_player_count_format,
								playerScore.sameRankingCount));
				holder.sameRankingCountTextView.setVisibility(View.VISIBLE);
			} else {
				holder.sameRankingCountTextView.setVisibility(View.INVISIBLE);
			}

			UIUtil.setScoreTextView(getContext(), holder.finalScoreTextView,
					playerScore.finalScore);

			return v;
		}
	}
}
