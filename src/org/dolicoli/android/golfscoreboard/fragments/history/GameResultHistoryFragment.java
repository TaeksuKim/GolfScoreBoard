package org.dolicoli.android.golfscoreboard.fragments.history;

import java.util.ArrayList;
import java.util.Collections;

import org.dolicoli.android.golfscoreboard.Constants;
import org.dolicoli.android.golfscoreboard.HistoryActivity;
import org.dolicoli.android.golfscoreboard.OneGameActivity;
import org.dolicoli.android.golfscoreboard.R;
import org.dolicoli.android.golfscoreboard.data.GameAndResult;
import org.dolicoli.android.golfscoreboard.data.PlayerScore;
import org.dolicoli.android.golfscoreboard.data.settings.GameSetting;
import org.dolicoli.android.golfscoreboard.data.settings.PlayerSetting;
import org.dolicoli.android.golfscoreboard.utils.PlayerUIUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.format.DateUtils;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class GameResultHistoryFragment extends ListFragment implements
		HistoryDataFragment {

	@SuppressWarnings("unused")
	private static final String TAG = "GameResultHistoryFragment";

	private HistoryListAdapter adapter;

	private HistoryDataContainer dataContainer;

	public void setDataContainer(HistoryDataContainer container) {
		this.dataContainer = container;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(
				R.layout.history_game_result_history_fragment, null);

		adapter = new HistoryListAdapter(getActivity(),
				R.layout.history_game_result_history_list_item);
		adapter.setNotifyOnChange(false);
		setListAdapter(adapter);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		final ListView listView = getListView();
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

			@Override
			public void onItemCheckedStateChanged(ActionMode mode,
					int position, long id, boolean checked) {
				int checkCount = listView.getCheckedItemCount();
				if (checkCount > 0)
					mode.setTitle(getString(
							R.string.activity_result_history_item_selected,
							checkCount));
				if (checked) {
					adapter.setNewSelection(position, checked);
				} else {
					adapter.removeSelection(position);
				}
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch (item.getItemId()) {
				case R.id.Delete:
					deleteSelectedItems();
					adapter.clearSelection();
					mode.finish();
					return true;
				default:
					adapter.clearSelection();
					return false;
				}
			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				// Inflate the menu for the CAB
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.context_result_history, menu);
				return true;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				// Here you can make any necessary updates to the activity when
				// the CAB is removed. By default, selected items are
				// deselected/unchecked.
				adapter.clearSelection();
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				// Here you can perform updates to the CAB due to
				// an invalidate() request
				return false;
			}
		});
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		GameAndResult history = adapter.getItem(position);
		Intent intent = new Intent(getActivity(), OneGameActivity.class);
		String playDate = GameSetting.toGameIdFormat(history.getGameSetting()
				.getDate());
		intent.putExtra(OneGameActivity.IK_PLAY_DATE, playDate);
		intent.putExtra(OneGameActivity.IK_DATE, history.getGameSetting()
				.getDate());
		startActivity(intent);
	}

	@Override
	public void reload(int index) {
		if (getActivity() == null || dataContainer == null)
			return;

		ArrayList<GameAndResult> results = null;
		switch (index) {
		case HistoryDataContainer.INDEX_THIS_MONTH:
			results = dataContainer.getThisMonthGameAndResults();
			break;
		case HistoryDataContainer.INDEX_LAST_MONTH:
			results = dataContainer.getLastMonthGameAndResults();
			break;
		case HistoryDataContainer.INDEX_RECENT_FIVE_GAMES:
		case HistoryDataContainer.INDEX_LAST_THREE_MONTH:
			results = dataContainer.getAllGameAndResults();
			break;
		}
		adapter.clear();
		if (results != null) {
			int count = 0;
			for (GameAndResult gameAndResult : results) {
				if (index == HistoryDataContainer.INDEX_RECENT_FIVE_GAMES
						&& count >= 5) {
					break;
				}
				adapter.add(gameAndResult);
				count++;
			}
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	public void hideActionMode() {
		ListView listView = getListView();
		if (listView == null)
			return;

		int size = listView.getCount();
		for (int i = 0; i < size; i++) {
			listView.setItemChecked(i, false);
		}
	}

	private void deleteSelectedItems() {
		ListView listView = getListView();
		int size = listView.getCount();
		SparseBooleanArray positions = getListView().getCheckedItemPositions();

		final ArrayList<String> playDates = new ArrayList<String>();
		for (int i = 0; i < size; i++) {
			if (positions.get(i)) {
				GameAndResult history = adapter.getItem(i);
				playDates.add(history.getGameSetting().getPlayDate());
			}
		}
		((HistoryActivity) getActivity()).deleteHistory(playDates);
	}

	private static class HistoryListViewHolder {
		CheckBox checkBox;

		TextView date1TextView, date2TextView;
		View[] playerTagViews;
		TextView[] playerNameTextViews;
		TextView[] playerScoreTextViews;

		TextView player1NameTextView, player2NameTextView, player3NameTextView,
				player4NameTextView, player5NameTextView, player6NameTextView;
		TextView player1ScoreTextView, player2ScoreTextView,
				player3ScoreTextView, player4ScoreTextView,
				player5ScoreTextView, player6ScoreTextView;

		ImageView firstPlaceImageView, secondPlaceImageView,
				thirdPlaceImageView;
	}

	private class HistoryListAdapter extends ArrayAdapter<GameAndResult> {

		private HistoryListViewHolder holder;
		private int resourceId;
		private int defaultTextColor;

		private SparseBooleanArray selectionMap;
		private boolean contextualMode;
		private int defaultBackground, selectedBackground;

		public HistoryListAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
			this.resourceId = textViewResourceId;
			TypedValue tv = new TypedValue();
			context.getTheme().resolveAttribute(R.attr.primaryTextColor, tv,
					true);
			defaultTextColor = getResources().getColor(tv.resourceId);

			selectionMap = new SparseBooleanArray();
			contextualMode = false;

			defaultBackground = 0x00000000;
			selectedBackground = getActivity().getResources().getColor(
					android.R.color.holo_blue_light);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getActivity()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(resourceId, null);
				holder = new HistoryListViewHolder();

				holder.checkBox = (CheckBox) v.findViewById(R.id.CheckBox);

				holder.date1TextView = (TextView) v
						.findViewById(R.id.Date1TextView);
				holder.date2TextView = (TextView) v
						.findViewById(R.id.Date2TextView);

				holder.playerTagViews = new View[Constants.MAX_PLAYER_COUNT];

				View player1TagView = v.findViewById(R.id.Player1TagView);
				View player2TagView = v.findViewById(R.id.Player2TagView);
				View player3TagView = v.findViewById(R.id.Player3TagView);
				View player4TagView = v.findViewById(R.id.Player4TagView);
				View player5TagView = v.findViewById(R.id.Player5TagView);
				View player6TagView = v.findViewById(R.id.Player6TagView);
				holder.playerTagViews[0] = player1TagView;
				holder.playerTagViews[1] = player2TagView;
				holder.playerTagViews[2] = player3TagView;
				holder.playerTagViews[3] = player4TagView;
				holder.playerTagViews[4] = player5TagView;
				holder.playerTagViews[5] = player6TagView;

				holder.player1NameTextView = (TextView) v
						.findViewById(R.id.Player1NameTextView);
				holder.player2NameTextView = (TextView) v
						.findViewById(R.id.Player2NameTextView);
				holder.player3NameTextView = (TextView) v
						.findViewById(R.id.Player3NameTextView);
				holder.player4NameTextView = (TextView) v
						.findViewById(R.id.Player4NameTextView);
				holder.player5NameTextView = (TextView) v
						.findViewById(R.id.Player5NameTextView);
				holder.player6NameTextView = (TextView) v
						.findViewById(R.id.Player6NameTextView);

				holder.playerNameTextViews = new TextView[Constants.MAX_PLAYER_COUNT];
				holder.playerNameTextViews[0] = holder.player1NameTextView;
				holder.playerNameTextViews[1] = holder.player2NameTextView;
				holder.playerNameTextViews[2] = holder.player3NameTextView;
				holder.playerNameTextViews[3] = holder.player4NameTextView;
				holder.playerNameTextViews[4] = holder.player5NameTextView;
				holder.playerNameTextViews[5] = holder.player6NameTextView;

				holder.player1ScoreTextView = (TextView) v
						.findViewById(R.id.Player1ScoreTextView);
				holder.player2ScoreTextView = (TextView) v
						.findViewById(R.id.Player2ScoreTextView);
				holder.player3ScoreTextView = (TextView) v
						.findViewById(R.id.Player3ScoreTextView);
				holder.player4ScoreTextView = (TextView) v
						.findViewById(R.id.Player4ScoreTextView);
				holder.player5ScoreTextView = (TextView) v
						.findViewById(R.id.Player5ScoreTextView);
				holder.player6ScoreTextView = (TextView) v
						.findViewById(R.id.Player6ScoreTextView);

				holder.playerScoreTextViews = new TextView[Constants.MAX_PLAYER_COUNT];
				holder.playerScoreTextViews[0] = holder.player1ScoreTextView;
				holder.playerScoreTextViews[1] = holder.player2ScoreTextView;
				holder.playerScoreTextViews[2] = holder.player3ScoreTextView;
				holder.playerScoreTextViews[3] = holder.player4ScoreTextView;
				holder.playerScoreTextViews[4] = holder.player5ScoreTextView;
				holder.playerScoreTextViews[5] = holder.player6ScoreTextView;

				holder.firstPlaceImageView = (ImageView) v
						.findViewById(R.id.Player1Image);
				holder.secondPlaceImageView = (ImageView) v
						.findViewById(R.id.Player2Image);
				holder.thirdPlaceImageView = (ImageView) v
						.findViewById(R.id.Player3Image);

				v.setTag(holder);
			} else {
				holder = (HistoryListViewHolder) v.getTag();
			}

			int count = getCount();
			if (count < 1)
				return v;

			if (position < 0 || position > count - 1)
				return v;

			if (contextualMode) {
				if (selectionMap.get(position, false)) {
					v.setBackgroundColor(selectedBackground);
					holder.checkBox.setChecked(true);
				} else {
					v.setBackgroundColor(defaultBackground);
					holder.checkBox.setChecked(false);
				}
				holder.checkBox.setVisibility(View.VISIBLE);
			} else {
				v.setBackgroundColor(defaultBackground);
				holder.checkBox.setVisibility(View.GONE);
			}

			GameAndResult gameAndResult = getItem(position);
			if (gameAndResult == null)
				return v;

			GameSetting gameSetting = gameAndResult.getGameSetting();
			PlayerSetting playerSetting = gameAndResult.getPlayerSetting();

			String date1String = DateUtils.formatDateTime(getActivity(),
					gameSetting.getDate().getTime(), DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_SHOW_YEAR);
			holder.date1TextView.setText(date1String);

			String date2String = DateUtils.formatDateTime(getActivity(),
					gameSetting.getDate().getTime(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_ABBREV_WEEKDAY
							| DateUtils.FORMAT_SHOW_WEEKDAY
							| DateUtils.FORMAT_12HOUR);
			holder.date2TextView.setText(date2String);

			int playerCount = gameSetting.getPlayerCount();
			ArrayList<PlayerScoreItem> playerScoreItems = new ArrayList<PlayerScoreItem>();
			for (int i = 0; i < Constants.MAX_PLAYER_COUNT; i++) {
				String playerName = playerSetting.getPlayerName(i);
				if (i < playerCount) {
					PlayerScore playerScore = gameAndResult
							.getPlayerScore(playerName);
					playerScoreItems.add(new PlayerScoreItem(i, playerName,
							playerScore.getFinalScore(), playerScore
									.getAdjustedTotalFee()));
				} else {
					playerScoreItems.add(new PlayerScoreItem(i, playerName,
							100, 0));
				}
			}
			Collections.sort(playerScoreItems);

			for (int i = 0; i < playerCount; i++) {
				PlayerScoreItem playerScoreItem = playerScoreItems.get(i);

				holder.playerNameTextViews[i].setText(playerScoreItem.name);
				if (playerScoreItem.score > 0) {
					holder.playerScoreTextViews[i].setText("+"
							+ playerScoreItem.score);
				} else {
					holder.playerScoreTextViews[i].setText(String
							.valueOf(playerScoreItem.score));
				}
				if (playerScoreItem.score < 0) {
					holder.playerScoreTextViews[i]
							.setTextColor(Constants.UNDER_PAR_TEXT_COLOR);
				} else if (playerScoreItem.score == 0) {
					holder.playerScoreTextViews[i]
							.setTextColor(Constants.EVEN_PAR_TEXT_COLOR);
				} else {
					holder.playerScoreTextViews[i]
							.setTextColor(defaultTextColor);
				}

				int faceImageResourceId = PlayerUIUtil
						.getRoundResourceId(playerScoreItem.name);
				if (i == 0) {
					holder.firstPlaceImageView
							.setImageResource(faceImageResourceId);
				} else if (i == 1) {
					holder.secondPlaceImageView
							.setImageResource(faceImageResourceId);
				} else if (i == 2) {
					holder.thirdPlaceImageView
							.setImageResource(faceImageResourceId);
				}

				int tagColor = PlayerUIUtil
						.getTagColor(playerScoreItem.name);
				holder.playerTagViews[i].setBackgroundColor(tagColor);
			}

			for (int i = 0; i < Constants.MAX_PLAYER_COUNT; i++) {
				if (i < playerCount) {
					holder.playerTagViews[i].setVisibility(View.VISIBLE);
					holder.playerNameTextViews[i].setVisibility(View.VISIBLE);
					holder.playerScoreTextViews[i].setVisibility(View.VISIBLE);
				} else {
					holder.playerTagViews[i].setVisibility(View.INVISIBLE);
					holder.playerNameTextViews[i].setVisibility(View.INVISIBLE);
					holder.playerScoreTextViews[i]
							.setVisibility(View.INVISIBLE);
				}
			}

			holder.thirdPlaceImageView
					.setVisibility((playerCount < 3) ? View.INVISIBLE
							: View.VISIBLE);

			if (playerCount < 4) {
				holder.playerTagViews[3].setVisibility(View.GONE);
				holder.playerTagViews[4].setVisibility(View.GONE);
				holder.playerTagViews[5].setVisibility(View.GONE);

				holder.playerNameTextViews[3].setVisibility(View.GONE);
				holder.playerNameTextViews[4].setVisibility(View.GONE);
				holder.playerNameTextViews[5].setVisibility(View.GONE);

				holder.playerScoreTextViews[3].setVisibility(View.GONE);
				holder.playerScoreTextViews[4].setVisibility(View.GONE);
				holder.playerScoreTextViews[5].setVisibility(View.GONE);
			}

			return v;
		}

		public void clearSelection() {
			contextualMode = false;
			selectionMap.clear();
			notifyDataSetChanged();
		}

		public void removeSelection(int position) {
			contextualMode = true;
			selectionMap.delete(position);
			notifyDataSetChanged();
		}

		public void setNewSelection(int position, boolean checked) {
			contextualMode = true;
			selectionMap.put(position, checked);
			notifyDataSetChanged();
		}
	}

	private static class PlayerScoreItem implements Comparable<PlayerScoreItem> {
		private int playerId;
		private String name;
		private int score;
		private int fee;

		public PlayerScoreItem(int playerId, String name, int score, int fee) {
			this.playerId = playerId;
			this.name = name;
			this.score = score;
			this.fee = fee;
		}

		@Override
		public int compareTo(PlayerScoreItem compare) {
			if (score < compare.score)
				return -1;
			if (score > compare.score)
				return 1;
			if (fee < compare.fee)
				return -1;
			if (fee > compare.fee)
				return 1;
			if (playerId < compare.playerId)
				return -1;
			if (playerId > compare.playerId)
				return 1;

			return 0;
		}

	}
}
