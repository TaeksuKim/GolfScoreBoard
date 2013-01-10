package org.dolicoli.android.golfscoreboard.fragments.history;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import org.dolicoli.android.golfscoreboard.GolfScoreBoardApplication;
import org.dolicoli.android.golfscoreboard.PersonalStatisticsActivity;
import org.dolicoli.android.golfscoreboard.R;
import org.dolicoli.android.golfscoreboard.data.GameAndResult;
import org.dolicoli.android.golfscoreboard.data.settings.PlayerSetting;
import org.dolicoli.android.golfscoreboard.utils.PlayerUIUtil;
import org.dolicoli.android.golfscoreboard.utils.TagProgressBar;
import org.dolicoli.android.golfscoreboard.utils.UIUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class NewPlayerRankingFragment extends NewHistoryItemListFragment {

	private PlayerInfoListAdapter adapter;

	@Override
	protected View inflate(LayoutInflater inflater) {
		return inflater.inflate(R.layout.new_history_player_ranking_fragment,
				null);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);

		adapter = new PlayerInfoListAdapter(getActivity(),
				R.layout.new_history_player_ranking_list_item);
		adapter.setNotifyOnChange(false);
		setListAdapter(adapter);

		return view;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		PlayerInfo playerInfo = adapter.getItem(position);
		if (playerInfo == null)
			return;

		String playerName = playerInfo.name;
		Intent intent = new Intent(getActivity(),
				PersonalStatisticsActivity.class);
		intent.putExtra(PersonalStatisticsActivity.IK_PLAYER_NAME, playerName);
		startActivity(intent);
	}

	@Override
	protected void reload() {
		if (getActivity() == null)
			return;

		if (resultList == null || resultList.size() < 1) {
			noResultTextView.setVisibility(View.VISIBLE);

			adapter.clear();
			adapter.notifyDataSetChanged();
		} else {
			noResultTextView.setVisibility(View.GONE);

			HashMap<String, PlayerInfo> playerInfoMap = new HashMap<String, PlayerInfo>();
			if (getCurrentMode() == GolfScoreBoardApplication.MODE_RECENT_FIVE_GAMES) {
				HashMap<String, Integer> playerGameCountMap = new HashMap<String, Integer>();
				for (GameAndResult gameAndResult : resultList) {
					int holeCount = gameAndResult.getHoleCount();
					int playerCount = gameAndResult.getGameSetting()
							.getPlayerCount();
					PlayerSetting playerSettings = gameAndResult
							.getPlayerSetting();
					for (int playerId = 0; playerId < playerCount; playerId++) {
						String playerName = playerSettings
								.getPlayerName(playerId);
						playerName = PlayerUIUtil.toCanonicalName(playerName);
						if (playerName.startsWith("Player"))
							continue;

						if (!playerGameCountMap.containsKey(playerName)) {
							playerGameCountMap.put(playerName, 0);
						}

						int playerGameCount = playerGameCountMap
								.get(playerName);
						if (playerGameCount >= 5)
							continue;

						if (!playerInfoMap.containsKey(playerName)) {
							playerInfoMap.put(playerName, new PlayerInfo(
									playerName));
						}
						PlayerInfo playerInfo = playerInfoMap.get(playerName);
						int originalScore = gameAndResult.getPlayerScore(
								playerName).getOriginalScore();
						if (holeCount < 18) {
							playerInfo.increaseScore(originalScore * 2);
						} else {
							playerInfo.increaseScore(originalScore);
						}

						playerGameCountMap.put(playerName, playerGameCount + 1);
					}
				}
			} else {
				for (GameAndResult gameAndResult : resultList) {
					int holeCount = gameAndResult.getHoleCount();
					int playerCount = gameAndResult.getGameSetting()
							.getPlayerCount();
					PlayerSetting playerSettings = gameAndResult
							.getPlayerSetting();
					for (int playerId = 0; playerId < playerCount; playerId++) {
						String playerName = playerSettings
								.getPlayerName(playerId);
						playerName = PlayerUIUtil.toCanonicalName(playerName);
						if (playerName.startsWith("Player"))
							continue;

						if (!playerInfoMap.containsKey(playerName)) {
							playerInfoMap.put(playerName, new PlayerInfo(
									playerName));
						}
						PlayerInfo playerInfo = playerInfoMap.get(playerName);
						int originalScore = gameAndResult.getPlayerScore(
								playerName).getOriginalScore();
						if (holeCount < 18) {
							playerInfo.increaseScore(originalScore * 2);
						} else {
							playerInfo.increaseScore(originalScore);
						}
					}
				}
			}

			Collection<PlayerInfo> values = playerInfoMap.values();
			PlayerInfo[] playerInfos = new PlayerInfo[values.size()];
			values.toArray(playerInfos);
			for (PlayerInfo playerInfo : playerInfos) {
				playerInfo.calculate();
			}
			Arrays.sort(playerInfos);

			adapter.clear();
			for (PlayerInfo info : playerInfos) {
				adapter.add(info);
			}
			adapter.notifyDataSetChanged();
		}
	}

	private static class ScoreListViewHolder {
		View tagView;
		TagProgressBar attendCountProgressBar;
		ImageView playerImageView;
		TextView playerNameTextView, attendCountTextView;
	}

	private class PlayerInfoListAdapter extends ArrayAdapter<PlayerInfo>
			implements OnClickListener {

		private ScoreListViewHolder holder;
		private int textViewResourceId;

		public PlayerInfoListAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);

			this.textViewResourceId = textViewResourceId;
			TypedValue tv = new TypedValue();
			context.getTheme().resolveAttribute(R.attr.primaryTextColor, tv,
					true);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getActivity()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(textViewResourceId, null);
				holder = new ScoreListViewHolder();

				holder.tagView = v.findViewById(R.id.PlayerTagView);
				holder.attendCountProgressBar = (TagProgressBar) v
						.findViewById(R.id.AttendCountProgressBar);
				holder.playerImageView = (ImageView) v
						.findViewById(R.id.PlayerImage);
				holder.playerNameTextView = (TextView) v
						.findViewById(R.id.PlayerNameTextView);
				holder.attendCountTextView = (TextView) v
						.findViewById(R.id.AttendCountTextView);

				v.setTag(holder);
			} else {
				holder = (ScoreListViewHolder) v.getTag();
			}

			int count = getCount();
			if (count < 1)
				return v;

			if (position < 0 || position > count - 1)
				return v;

			PlayerInfo playerInfo = getItem(position);
			if (playerInfo == null)
				return v;

			holder.attendCountProgressBar.setMax(100);
			double rate = (140.0 - ((double) playerInfo.avgOfScore - 16.0) * 5.0) / 280.0 + 0.3;
			if (rate < 0.0) {
				rate = 0.0;
			}
			holder.attendCountProgressBar.setPos((int) (rate * 100.0));

			holder.tagView.setBackgroundColor(PlayerUIUtil
					.getTagColor(playerInfo.name));
			holder.attendCountProgressBar.setColor(PlayerUIUtil
					.getTagColor(playerInfo.name));
			holder.playerImageView.setImageResource(PlayerUIUtil
					.getRoundResourceId(playerInfo.name));
			holder.playerNameTextView.setText(playerInfo.name);

			DecimalFormat overParScoreCountFormat = new DecimalFormat("+0.00");
			DecimalFormat underParScoreCountFormat = new DecimalFormat("0.00");

			String attendText = UIUtil.formatGameCount(getActivity(),
					playerInfo.attend);
			if (playerInfo.avgOfScore > 0) {
				holder.attendCountTextView.setText(overParScoreCountFormat
						.format(playerInfo.avgOfScore)
						+ " ("
						+ attendText
						+ ")");
			} else {
				holder.attendCountTextView.setText(underParScoreCountFormat
						.format(playerInfo.avgOfScore)
						+ " ("
						+ attendText
						+ ")");
			}

			return v;
		}

		@Override
		public void onClick(View v) {
			if (!(v instanceof Button))
				return;

			Button button = (Button) v;
			View more = (View) button.getTag();
			if (more.getVisibility() == View.VISIBLE) {
				more.setVisibility(View.GONE);
				button.setBackgroundResource(R.drawable.ic_expand);
			} else {
				more.setVisibility(View.VISIBLE);
				button.setBackgroundResource(R.drawable.ic_collapse);
			}
		}
	}

	private static class PlayerInfo implements Comparable<PlayerInfo> {
		private int attend = 0;
		private int sumOfScore = 0;
		private float avgOfScore = 0;
		private String name;

		private PlayerInfo(String name) {
			this.name = name;
			this.sumOfScore = 0;
			this.attend = 0;
		}

		public void increaseScore(int score) {
			this.sumOfScore += score;
			this.attend++;
		}

		public void calculate() {
			if (attend < 1) {
				avgOfScore = 0F;
			} else {
				avgOfScore = (float) sumOfScore / (float) attend;
			}
		}

		@Override
		public int compareTo(PlayerInfo compare) {
			if (attend >= 5 && compare.attend < 5)
				return -1;
			if (attend < 5 && compare.attend >= 5)
				return 1;
			if (avgOfScore < compare.avgOfScore)
				return -1;
			if (avgOfScore > compare.avgOfScore)
				return 1;
			if (attend > compare.attend)
				return -1;
			if (attend < compare.attend)
				return 1;
			return name.compareTo(compare.name);
		}
	}
}
