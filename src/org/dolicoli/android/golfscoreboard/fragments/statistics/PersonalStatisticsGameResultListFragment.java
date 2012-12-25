package org.dolicoli.android.golfscoreboard.fragments.statistics;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.dolicoli.android.golfscoreboard.Constants;
import org.dolicoli.android.golfscoreboard.R;
import org.dolicoli.android.golfscoreboard.Reloadable;
import org.dolicoli.android.golfscoreboard.data.GameAndResult;
import org.dolicoli.android.golfscoreboard.data.PlayerScore;
import org.dolicoli.android.golfscoreboard.data.settings.GameSetting;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.format.DateUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PersonalStatisticsGameResultListFragment extends ListFragment
		implements Reloadable {

	private PersonalStatisticsDataContainer dataContainer;

	private GameAndResultListAdapter adapter;

	private DecimalFormat feeFormat;

	private ArrayList<GameAndResult> gameAndResults;
	private String playerName;

	public void setDataContainer(PersonalStatisticsDataContainer container) {
		this.dataContainer = container;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(
				R.layout.personal_statistics_game_result_list_fragment, null);

		adapter = new GameAndResultListAdapter(getActivity(),
				R.layout.personal_statistics_game_result_list_item);
		adapter.setNotifyOnChange(false);
		setListAdapter(adapter);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		feeFormat = new DecimalFormat(getString(R.string.fee_format));

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

		adapter.clear();
		for (GameAndResult gameAndResult : gameAndResults) {
			if (!gameAndResult.containsPlayerScore(playerName))
				continue;

			adapter.add(gameAndResult);
		}
		adapter.notifyDataSetChanged();
	}

	private static class GameAndResultListViewHolder {
		TextView dateTextView, rankingTextView, playerCountTextView,
				scoreTextView, feeTextView;
	}

	private class GameAndResultListAdapter extends ArrayAdapter<GameAndResult> {

		private GameAndResultListViewHolder holder;
		private int defaultTextColor;
		private int textViewResourceId;

		public GameAndResultListAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);

			this.textViewResourceId = textViewResourceId;

			TypedValue tv = new TypedValue();
			context.getTheme().resolveAttribute(R.attr.primaryTextColor, tv,
					true);
			defaultTextColor = getResources().getColor(tv.resourceId);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getActivity()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(textViewResourceId, null);
				holder = new GameAndResultListViewHolder();

				holder.dateTextView = (TextView) v
						.findViewById(R.id.DateTextView);
				holder.rankingTextView = (TextView) v
						.findViewById(R.id.RankingTextView);
				holder.playerCountTextView = (TextView) v
						.findViewById(R.id.PlayerCountTextView);
				holder.scoreTextView = (TextView) v
						.findViewById(R.id.ScoreTextView);
				holder.feeTextView = (TextView) v
						.findViewById(R.id.FeeTextView);

				v.setTag(holder);
			} else {
				holder = (GameAndResultListViewHolder) v.getTag();
			}

			int count = getCount();
			if (count < 1)
				return v;

			if (position < 0 || position > count - 1)
				return v;

			GameAndResult gameAndResult = getItem(position);
			if (gameAndResult == null)
				return v;

			GameSetting gameSetting = gameAndResult.getGameSetting();
			String dateString = DateUtils
					.formatDateTime(getActivity(), gameSetting.getDate()
							.getTime(), DateUtils.FORMAT_SHOW_DATE);
			holder.dateTextView.setText(dateString);

			PlayerScore playerScore = gameAndResult.getPlayerScore(playerName);

			holder.rankingTextView.setText(playerScore.getRanking() + " À§");
			holder.playerCountTextView.setText(String.valueOf(gameSetting
					.getPlayerCount()) + " ¸í");

			int score = playerScore.getScore();
			if (score > 0) {
				holder.scoreTextView.setText("+" + score);
			} else {
				holder.scoreTextView.setText(String.valueOf(score));
			}
			if (score < 0) {
				holder.scoreTextView
						.setTextColor(Constants.UNDER_PAR_TEXT_COLOR);
			} else if (score == 0) {
				holder.scoreTextView
						.setTextColor(Constants.EVEN_PAR_TEXT_COLOR);
			} else {
				holder.scoreTextView.setTextColor(defaultTextColor);
			}

			holder.feeTextView.setText(feeFormat.format(playerScore
					.getAdjustedTotalFee()));

			return v;
		}
	}
}
