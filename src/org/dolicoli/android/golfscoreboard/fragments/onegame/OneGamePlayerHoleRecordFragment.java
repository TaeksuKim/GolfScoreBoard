package org.dolicoli.android.golfscoreboard.fragments.onegame;

import java.util.Comparator;

import org.dolicoli.android.golfscoreboard.R;
import org.dolicoli.android.golfscoreboard.data.OneGame;
import org.dolicoli.android.golfscoreboard.data.OneHolePlayerScore;
import org.dolicoli.android.golfscoreboard.tasks.CurrentGameQueryTask;
import org.dolicoli.android.golfscoreboard.tasks.HistoryGameSettingWithResultQueryTask;
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
		HistoryGameSettingWithResultQueryTask.TaskListener {

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
	public void onCurrentGameQueryFinished(OneGame gameResult) {
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
			HistoryGameSettingWithResultQueryTask task = new HistoryGameSettingWithResultQueryTask(
					activity, this);
			task.execute(new HistoryGameSettingWithResultQueryTask.QueryParam(
					playDate));
		}
	}

	private void reloadUI(OneGame gameResult) {
		if (getActivity() == null || gameResult == null)
			return;

		adapter.clear();
		int currentHole = gameResult.getCurrentHole();
		for (int holeNumber = 1; holeNumber <= currentHole; holeNumber++) {
			OneHolePlayerScore holePlayerScore = gameResult.getHolePlayerScore(
					holeNumber, playerId);
			if (holePlayerScore != null)
				adapter.add(holePlayerScore);
		}
		adapter.sort();
		adapter.notifyDataSetChanged();
	}

	private static class ScoreListViewHolder {
		TextView holeTextView, feeTextView, rankingTextView;
		TextView sameRankingCountTextView;
		TextView scoreTextView, handicapTextView, finalScoreTextView;
	}

	private static class ScoreListAdapter extends
			ArrayAdapter<OneHolePlayerScore> {

		private ScoreListViewHolder holder;
		private int textViewResourceId;

		public ScoreListAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
			this.textViewResourceId = textViewResourceId;
		}

		public void sort() {
			super.sort(new Comparator<OneHolePlayerScore>() {
				@Override
				public int compare(OneHolePlayerScore lhs,
						OneHolePlayerScore rhs) {
					int leftHoleNumber = lhs.getHoleNumber();
					int rightHoleNumber = rhs.getHoleNumber();

					if (leftHoleNumber < rightHoleNumber)
						return 1;
					if (leftHoleNumber > rightHoleNumber)
						return -1;
					return 0;
				}
			});
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			Context context = getContext();
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) context
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

			OneHolePlayerScore playerScore = getItem(position);
			if (playerScore == null)
				return v;

			holder.holeTextView
					.setText(context
							.getString(
									R.string.fragment_onegame_player_hole_record_hole_number_format,
									playerScore.getHoleNumber(),
									playerScore.getParNumber()));

			UIUtil.setScoreTextView(context, holder.scoreTextView,
					playerScore.getOriginalScore());

			if (playerScore.getUsedHandicap() > 0) {
				holder.handicapTextView.setText("-"
						+ playerScore.getUsedHandicap());

				holder.scoreTextView.setVisibility(View.VISIBLE);
				holder.handicapTextView.setVisibility(View.VISIBLE);
			} else {
				holder.scoreTextView.setVisibility(View.INVISIBLE);
				holder.handicapTextView.setVisibility(View.INVISIBLE);
			}

			UIUtil.setFeeTextView(context, holder.feeTextView,
					playerScore.getFee());

			UIUtil.setRankingTextView(context, holder.rankingTextView,
					playerScore.getRanking());

			if (playerScore.getSameRankingCount() > 1) {
				UIUtil.setPlayerCountTextView(context,
						holder.sameRankingCountTextView,
						playerScore.getSameRankingCount());
				holder.sameRankingCountTextView.setVisibility(View.VISIBLE);
			} else {
				holder.sameRankingCountTextView.setVisibility(View.INVISIBLE);
			}

			UIUtil.setScoreTextView(context, holder.finalScoreTextView,
					playerScore.getFinalScore());

			return v;
		}
	}
}
