package org.dolicoli.android.golfscoreboard.fragments.onegame;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import org.dolicoli.android.golfscoreboard.Constants;
import org.dolicoli.android.golfscoreboard.OneGamePlayerRecordActivity;
import org.dolicoli.android.golfscoreboard.PersonalStatisticsActivity;
import org.dolicoli.android.golfscoreboard.R;
import org.dolicoli.android.golfscoreboard.data.OneGame;
import org.dolicoli.android.golfscoreboard.data.PlayerScore;
import org.dolicoli.android.golfscoreboard.tasks.CurrentGameQueryTask;
import org.dolicoli.android.golfscoreboard.tasks.HistoryGameSettingWithResultQueryTask;
import org.dolicoli.android.golfscoreboard.utils.PlayerUIUtil;
import org.dolicoli.android.golfscoreboard.utils.UIUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.format.DateUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class OneGameSummaryFragment extends ListFragment implements
		OneGameActivityPage, CurrentGameQueryTask.TaskListener,
		HistoryGameSettingWithResultQueryTask.TaskListener {

	private View statusView;
	private TextView currentHoleTextView, finalHoleTextView, dateTextView;
	private ScoreListAdapter adapter;

	private int holeCount;
	private int currentHole;
	private OneGame gameResult;

	private int mode;
	private String playDate;
	private int holeNumber;

	private HashMap<String, Boolean> expandMap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.mode = MODE_CURRENT;
		this.playDate = "";
		this.holeNumber = Constants.WHOLE_HOLE;

		Bundle bundle = null;
		if (savedInstanceState != null) {
			bundle = savedInstanceState;
		} else {
			bundle = getArguments();
		}
		if (bundle != null) {
			int mode = bundle.getInt(BK_MODE);
			if (mode == MODE_CURRENT || mode == MODE_HISTORY)
				this.mode = mode;

			String playDate = bundle.getString(BK_PLAY_DATE, "");
			if (!playDate.equals("")) {
				this.playDate = playDate;
			}

			int holeNumber = bundle.getInt(BK_HOLE_NUMBER);
			if (holeNumber > 0 && holeNumber <= 18)
				this.holeNumber = holeNumber;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.onegame_summary_fragment, null);

		statusView = view.findViewById(R.id.StatusView);
		currentHoleTextView = (TextView) view
				.findViewById(R.id.CurrentHoleTextView);
		finalHoleTextView = (TextView) view
				.findViewById(R.id.FinalHoleTextView);
		dateTextView = (TextView) view.findViewById(R.id.DateTextView);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		adapter = new ScoreListAdapter(getActivity(),
				R.layout.onegame_summary_list_item);
		adapter.setNotifyOnChange(false);
		setListAdapter(adapter);

		if (mode == MODE_HISTORY) {
			statusView.setVisibility(View.GONE);
		} else {
			setHasOptionsMenu(true);
			statusView.setVisibility(View.VISIBLE);
		}
		reload(false);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		if (mode == MODE_CURRENT) {
			inflater.inflate(R.menu.current_game, menu);
		}
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		if (mode == MODE_CURRENT) {
			MenuItem item = menu.findItem(R.id.AddResult);

			if (isAllGameFinished()) {
				item.setEnabled(false);
			} else {
				item.setEnabled(true);
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Activity activity = getActivity();
		return activity.onOptionsItemSelected(item);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		PlayerScoreLocal playerScore = adapter.getItem(position);
		int playerId = playerScore.playerScore.getPlayerId();
		Intent intent = new Intent(getActivity(),
				OneGamePlayerRecordActivity.class);
		if (mode == MODE_CURRENT) {
			intent.putExtra(OneGamePlayerRecordActivity.IK_CURRENT, true);
			intent.putExtra(OneGamePlayerRecordActivity.IK_PLAYER_ID, playerId);
		} else if (mode == MODE_HISTORY) {
			intent.putExtra(OneGamePlayerRecordActivity.IK_CURRENT, false);
			intent.putExtra(OneGamePlayerRecordActivity.IK_PLAY_DATE, playDate);
			intent.putExtra(OneGamePlayerRecordActivity.IK_PLAYER_ID, playerId);
		}
		startActivity(intent);
	}

	@Override
	public void reload(boolean clean) {
		if (getActivity() == null)
			return;

		if (clean && expandMap != null) {
			expandMap.clear();
			expandMap = null;
		}

		if (mode == MODE_CURRENT) {
			CurrentGameQueryTask task = new CurrentGameQueryTask(getActivity(),
					this);
			task.execute();
		} else if (mode == MODE_HISTORY) {
			HistoryGameSettingWithResultQueryTask task = new HistoryGameSettingWithResultQueryTask(
					getActivity(), this);
			task.execute(new HistoryGameSettingWithResultQueryTask.QueryParam(
					playDate, holeNumber));
		}
	}

	@Override
	public void onCurrentGameQueryStarted() {
	}

	@Override
	public void onCurrentGameQueryFinished(OneGame gameResult) {
		this.gameResult = gameResult;
		reloadUI();
	}

	@Override
	public void setHoleNumber(int holeNumber) {
		this.holeNumber = holeNumber;

		reload(false);
	}

	private void reloadUI() {
		if (getActivity() == null || gameResult == null)
			return;

		holeCount = gameResult.getHoleCount();

		int playerCount = gameResult.getPlayerCount();
		SparseArray<PlayerScoreLocal> playerScoreMap = new SparseArray<PlayerScoreLocal>();

		ArrayList<PlayerScoreLocal> list = new ArrayList<PlayerScoreLocal>();
		for (int playerId = 0; playerId < playerCount; playerId++) {
			PlayerScoreLocal playerScore = new PlayerScoreLocal(
					gameResult.getPlayerScore(playerId));
			playerScoreMap.put(playerId, playerScore);
			list.add(playerId, playerScore);
		}

		currentHole = gameResult.getCurrentHole();

		Date date = gameResult.getDate();
		if (date != null) {
			String dateString = DateUtils.formatDateTime(getActivity(),
					date.getTime(), DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_SHOW_YEAR
							| DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_ABBREV_WEEKDAY
							| DateUtils.FORMAT_SHOW_WEEKDAY
							| DateUtils.FORMAT_12HOUR);
			dateTextView.setText(dateString);
		} else {
			dateTextView.setText(R.string.blank);
		}
		currentHoleTextView.setText(getString(
				R.string.fragment_onegamesummary_current_hole_format,
				currentHole));
		finalHoleTextView.setText(String.valueOf(holeCount));

		if (expandMap == null) {
			expandMap = new HashMap<String, Boolean>();
		}
		adapter.clear();
		for (PlayerScoreLocal playerScore : list) {
			if (expandMap.containsKey(playerScore.playerScore.getName())) {
				playerScore.expand = expandMap.get(playerScore.playerScore
						.getName());
			} else {
				playerScore.expand = false;
			}
			adapter.add(playerScore);
		}
		adapter.sort(new Comparator<PlayerScoreLocal>() {
			@Override
			public int compare(PlayerScoreLocal lhs, PlayerScoreLocal rhs) {
				return lhs.compareTo(rhs);
			}
		});
		adapter.notifyDataSetChanged();

		getActivity().invalidateOptionsMenu();
	}

	public boolean isAllGameFinished() {
		return (currentHole >= holeCount);
	}

	private static class PlayerScoreLocal implements
			Comparable<PlayerScoreLocal> {

		private boolean expand;
		private PlayerScore playerScore;

		public PlayerScoreLocal(PlayerScore playerScore) {
			this.expand = false;
			this.playerScore = playerScore;
		}

		@Override
		public int compareTo(PlayerScoreLocal compare) {
			return playerScore.compareTo(compare.playerScore);
		}
	}

	private static class ScoreListViewHolder {
		TextView largeNameTextView, largeTotalFeeTextView, largeScoreTextView;
		TextView mediumNameTextView, mediumTotalFeeTextView,
				mediumScoreTextView;
		TextView totalOriginalScoreTextView, totalHandicapTextView,
				totalExtraScoreTextView;
		TextView holeFeeTextView, rankingFeeTextView;
		View handicapView;
		TextView handicapTextView, remainHandicapTextView;
		TextView avgRankingTextView;
		TextView avgOverParTextView;

		ImageView largePlayerImageView, mediumPlayerImageView;
		Button largeShowMoreButton, mediumShowMoreButton;
		Button personalStatisticsButton;

		View largeTagView, mediumTagView, largeView, mediumView, detailView;
	}

	private class ScoreListAdapter extends ArrayAdapter<PlayerScoreLocal>
			implements OnClickListener {

		private ScoreListViewHolder holder;
		private int textViewResourceId;

		public ScoreListAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
			this.textViewResourceId = textViewResourceId;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getActivity()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(textViewResourceId, null);
				holder = new ScoreListViewHolder();

				holder.largeTagView = v.findViewById(R.id.LargeTagView);
				holder.largeView = v.findViewById(R.id.LargeView);
				holder.largePlayerImageView = (ImageView) v
						.findViewById(R.id.LargeImage);
				holder.largeNameTextView = (TextView) v
						.findViewById(R.id.LargeNameTextView);
				holder.largeTotalFeeTextView = (TextView) v
						.findViewById(R.id.LargeTotalFeeTextView);
				holder.largeScoreTextView = (TextView) v
						.findViewById(R.id.LargeScoreTextView);
				holder.largeShowMoreButton = (Button) v
						.findViewById(R.id.LargeShowMoreButton);

				holder.mediumTagView = v.findViewById(R.id.MediumTagView);
				holder.mediumView = v.findViewById(R.id.MediumView);
				holder.mediumPlayerImageView = (ImageView) v
						.findViewById(R.id.MediumImage);
				holder.mediumNameTextView = (TextView) v
						.findViewById(R.id.MediumNameTextView);
				holder.mediumTotalFeeTextView = (TextView) v
						.findViewById(R.id.MediumTotalFeeTextView);
				holder.mediumScoreTextView = (TextView) v
						.findViewById(R.id.MediumScoreTextView);
				holder.mediumShowMoreButton = (Button) v
						.findViewById(R.id.MediumShowMoreButton);

				holder.totalOriginalScoreTextView = (TextView) v
						.findViewById(R.id.TotalOriginalScoreTextView);
				holder.totalHandicapTextView = (TextView) v
						.findViewById(R.id.TotalHandicapTextView);
				holder.totalExtraScoreTextView = (TextView) v
						.findViewById(R.id.TotalExtraScoreTextView);

				holder.holeFeeTextView = (TextView) v
						.findViewById(R.id.HoleFeeTextView);
				holder.rankingFeeTextView = (TextView) v
						.findViewById(R.id.RankingFeeTextView);

				holder.avgRankingTextView = (TextView) v
						.findViewById(R.id.AvgRankingTextView);
				holder.avgOverParTextView = (TextView) v
						.findViewById(R.id.AvgOverParTextView);
				holder.handicapView = v.findViewById(R.id.HandicapView);
				holder.handicapTextView = (TextView) v
						.findViewById(R.id.HandicapTextView);
				holder.remainHandicapTextView = (TextView) v
						.findViewById(R.id.RemainHandicapTextView);

				holder.detailView = v.findViewById(R.id.DetailView);

				holder.personalStatisticsButton = (Button) v
						.findViewById(R.id.PersonalStatisticsButton);

				v.setTag(holder);
			} else {
				holder = (ScoreListViewHolder) v.getTag();
			}

			int count = getCount();
			if (count < 1)
				return v;

			if (position < 0 || position > count - 1)
				return v;

			PlayerScoreLocal playerScore = getItem(position);
			if (playerScore == null)
				return v;

			Button showMoreButton;
			ImageView playerImageView;
			TextView nameTextView, totalFeeTextView, scoreTextView;
			View tagView;
			if (playerScore.playerScore.getRanking() <= 1) {
				tagView = holder.largeTagView;

				playerImageView = holder.largePlayerImageView;
				showMoreButton = holder.largeShowMoreButton;
				nameTextView = holder.largeNameTextView;
				totalFeeTextView = holder.largeTotalFeeTextView;
				scoreTextView = holder.largeScoreTextView;

				holder.largeView.setVisibility(View.VISIBLE);
				holder.mediumView.setVisibility(View.GONE);
			} else {
				tagView = holder.mediumTagView;

				playerImageView = holder.mediumPlayerImageView;
				showMoreButton = holder.mediumShowMoreButton;
				nameTextView = holder.mediumNameTextView;
				totalFeeTextView = holder.mediumTotalFeeTextView;
				scoreTextView = holder.mediumScoreTextView;

				holder.largeView.setVisibility(View.GONE);
				holder.mediumView.setVisibility(View.VISIBLE);
			}

			String playerName = playerScore.playerScore.getName();

			tagView.setBackgroundColor(PlayerUIUtil.getTagColor(playerName));

			showMoreButton.setOnClickListener(this);
			showMoreButton.setTag(playerScore);

			holder.personalStatisticsButton.setOnClickListener(this);
			holder.personalStatisticsButton.setTag(playerName);

			playerImageView.setImageResource(PlayerUIUtil
					.getRoundResourceId(playerName));
			nameTextView.setText(playerName);

			totalFeeTextView.setText(UIUtil.formatFee(getContext(),
					playerScore.playerScore.getAdjustedTotalFee()));
			holder.holeFeeTextView.setText(UIUtil.formatFee(getContext(),
					playerScore.playerScore.getAdjustedHoleFee()));
			holder.rankingFeeTextView.setText(UIUtil.formatFee(getContext(),
					playerScore.playerScore.getAdjustedTotalFee()
							- playerScore.playerScore.getAdjustedHoleFee()));

			UIUtil.setScoreTextView(getContext(), scoreTextView,
					playerScore.playerScore.getFinalScore());

			int handicap = playerScore.playerScore.getHandicap();
			if (handicap > 0) {
				holder.handicapTextView.setText(String
						.valueOf(playerScore.playerScore.getUsedHandicap()));
				holder.remainHandicapTextView.setText(String
						.valueOf(playerScore.playerScore.getRemainHandicap()));

				holder.handicapView.setVisibility(View.VISIBLE);
			} else {
				holder.handicapView.setVisibility(View.GONE);
			}

			UIUtil.setScoreTextView(getContext(),
					holder.totalOriginalScoreTextView,
					playerScore.playerScore.getOriginalScore());
			holder.totalHandicapTextView.setText(String
					.valueOf(playerScore.playerScore.getUsedHandicap()));
			holder.totalExtraScoreTextView.setText(String
					.valueOf(playerScore.playerScore.getExtraScore()));

			UIUtil.setAvgScoreTextView(getContext(), holder.avgOverParTextView,
					playerScore.playerScore.getAvgOverPar());

			if (playerScore.playerScore.getAvgRanking() > 0.0) {
				holder.avgRankingTextView.setText(UIUtil.formatAvgRanking(
						getContext(), playerScore.playerScore.getAvgRanking()));
				holder.avgRankingTextView.setVisibility(View.VISIBLE);
			} else {
				holder.avgRankingTextView.setVisibility(View.INVISIBLE);
			}

			if (playerScore.expand) {
				holder.detailView.setVisibility(View.VISIBLE);
				showMoreButton.setBackgroundResource(R.drawable.ic_collapse);
			} else {
				holder.detailView.setVisibility(View.GONE);
				showMoreButton.setBackgroundResource(R.drawable.ic_expand);
			}

			return v;
		}

		@Override
		public void onClick(View v) {
			if (!(v instanceof Button))
				return;

			final int id = v.getId();
			if (id == R.id.PersonalStatisticsButton) {
				String playerName = (String) v.getTag();

				Intent intent = new Intent(getActivity(),
						PersonalStatisticsActivity.class);
				intent.putExtra(PersonalStatisticsActivity.IK_PLAYER_NAME,
						PlayerUIUtil.toCanonicalName(playerName));
				startActivity(intent);
			} else if (id == R.id.LargeShowMoreButton
					|| id == R.id.MediumShowMoreButton) {
				Button button = (Button) v;
				PlayerScoreLocal playerScore = (PlayerScoreLocal) button
						.getTag();
				playerScore.expand = !playerScore.expand;
				if (expandMap != null)
					expandMap.put(playerScore.playerScore.getName(),
							playerScore.expand);
				notifyDataSetChanged();
			}
		}
	}
}
