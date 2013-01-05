package org.dolicoli.android.golfscoreboard.fragments.main;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import org.dolicoli.android.golfscoreboard.Constants;
import org.dolicoli.android.golfscoreboard.HistoryActivity;
import org.dolicoli.android.golfscoreboard.R;
import org.dolicoli.android.golfscoreboard.data.GameAndResult;
import org.dolicoli.android.golfscoreboard.fragments.onegame.OneGameActivityPage;
import org.dolicoli.android.golfscoreboard.tasks.HistoryGameSettingRangeQueryTask;
import org.dolicoli.android.golfscoreboard.tasks.ThreeMonthsGameReceiveTask;
import org.dolicoli.android.golfscoreboard.tasks.ThreeMonthsGameReceiveTask.ReceiveProgress;
import org.dolicoli.android.golfscoreboard.tasks.ThreeMonthsGameReceiveTask.ReceiveResult;
import org.dolicoli.android.golfscoreboard.utils.DateRangeUtil;
import org.dolicoli.android.golfscoreboard.utils.DateRangeUtil.DateRange;
import org.dolicoli.android.golfscoreboard.utils.PlayerUIUtil;
import org.dolicoli.android.golfscoreboard.utils.UIUtil;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AttendCountFragment extends Fragment implements
		OneGameActivityPage, OnClickListener,
		HistoryGameSettingRangeQueryTask.TaskListener,
		ThreeMonthsGameReceiveTask.TaskListener {

	@SuppressWarnings("unused")
	private static final String TAG = "AttendCountFragment";

	private static final String DEFAULT_PLAYER_NAME_PREFIX = "Player";

	private static final int REQ_HISTORY = 0x0001;

	private View noHistoryTextView, mainView;

	private TextView gameCountTextView, totalFeeSumTextView;
	private View[] playerViews, playerTagViews;
	private ImageView[] playerImageViews;
	private TextView[] playerNameTextViews, playerAttendCountTextViews;

	private PlayerAttendCount[] playerAttendCountArray;

	private ProgressDialog progressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.attend_count_fragment, null);

		noHistoryTextView = view.findViewById(R.id.NoHistoryTextView);
		mainView = view.findViewById(R.id.MainView);

		gameCountTextView = (TextView) view
				.findViewById(R.id.GameCountTextView);
		totalFeeSumTextView = (TextView) view
				.findViewById(R.id.TotalFeeSumTextView);

		playerViews = new View[Constants.MAX_PLAYER_COUNT];
		playerViews[0] = view.findViewById(R.id.Player1View);
		playerViews[1] = view.findViewById(R.id.Player2View);
		playerViews[2] = view.findViewById(R.id.Player3View);
		playerViews[3] = view.findViewById(R.id.Player4View);
		playerViews[4] = view.findViewById(R.id.Player5View);
		playerViews[5] = view.findViewById(R.id.Player6View);

		playerTagViews = new View[Constants.MAX_PLAYER_COUNT];
		playerTagViews[0] = view.findViewById(R.id.Player1TagView);
		playerTagViews[1] = view.findViewById(R.id.Player2TagView);
		playerTagViews[2] = view.findViewById(R.id.Player3TagView);
		playerTagViews[3] = view.findViewById(R.id.Player4TagView);
		playerTagViews[4] = view.findViewById(R.id.Player5TagView);
		playerTagViews[5] = view.findViewById(R.id.Player6TagView);

		playerImageViews = new ImageView[Constants.MAX_PLAYER_COUNT];
		playerImageViews[0] = (ImageView) view
				.findViewById(R.id.Player1ImageView);
		playerImageViews[1] = (ImageView) view
				.findViewById(R.id.Player2ImageView);
		playerImageViews[2] = (ImageView) view
				.findViewById(R.id.Player3ImageView);
		playerImageViews[3] = (ImageView) view
				.findViewById(R.id.Player4ImageView);
		playerImageViews[4] = (ImageView) view
				.findViewById(R.id.Player5ImageView);
		playerImageViews[5] = (ImageView) view
				.findViewById(R.id.Player6ImageView);

		playerNameTextViews = new TextView[Constants.MAX_PLAYER_COUNT];
		playerNameTextViews[0] = (TextView) view
				.findViewById(R.id.Player1NameTextView);
		playerNameTextViews[1] = (TextView) view
				.findViewById(R.id.Player2NameTextView);
		playerNameTextViews[2] = (TextView) view
				.findViewById(R.id.Player3NameTextView);
		playerNameTextViews[3] = (TextView) view
				.findViewById(R.id.Player4NameTextView);
		playerNameTextViews[4] = (TextView) view
				.findViewById(R.id.Player5NameTextView);
		playerNameTextViews[5] = (TextView) view
				.findViewById(R.id.Player6NameTextView);

		playerAttendCountTextViews = new TextView[Constants.MAX_PLAYER_COUNT];
		playerAttendCountTextViews[0] = (TextView) view
				.findViewById(R.id.Player1AttendCountTextView);
		playerAttendCountTextViews[1] = (TextView) view
				.findViewById(R.id.Player2AttendCountTextView);
		playerAttendCountTextViews[2] = (TextView) view
				.findViewById(R.id.Player3AttendCountTextView);
		playerAttendCountTextViews[3] = (TextView) view
				.findViewById(R.id.Player4AttendCountTextView);
		playerAttendCountTextViews[4] = (TextView) view
				.findViewById(R.id.Player5AttendCountTextView);
		playerAttendCountTextViews[5] = (TextView) view
				.findViewById(R.id.Player6AttendCountTextView);

		mainView.setOnClickListener(this);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setHasOptionsMenu(true);
		reload(false);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.attend_count, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.ImportThreeMonth:
			importThreeMonthData();
			return true;
		}

		FragmentActivity activity = getActivity();
		if (activity == null) {
			return false;
		}

		return activity.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		if (playerAttendCountArray == null || playerAttendCountArray.length < 1)
			return;

		Intent historyIntent = new Intent(getActivity(), HistoryActivity.class);
		startActivityForResult(historyIntent, REQ_HISTORY);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQ_HISTORY) {
			reload(true);
		}
	}

	@Override
	public void reload(boolean clean) {
		FragmentActivity activity = getActivity();
		if (activity == null || totalFeeSumTextView == null)
			return;

		DateRange dateRange = DateRangeUtil.getDateRange(2);

		HistoryGameSettingRangeQueryTask task = new HistoryGameSettingRangeQueryTask(
				activity, this);
		task.execute(dateRange);
	}

	@Override
	public void setHoleNumber(int holeNumber) {
	}

	@Override
	public void onHistoryGameSettingQueryStarted() {
	}

	@Override
	public void onHistoryGameSettingQueryFinished(GameAndResult[] gameResults) {
		FragmentActivity activity = getActivity();
		if (activity == null || gameResults == null)
			return;

		HashMap<String, PlayerAttendCount> attendCountMap = new HashMap<String, PlayerAttendCount>();

		int gameCount = 0;
		int totalFeeSum = 0;
		for (GameAndResult gameResult : gameResults) {
			totalFeeSum += gameResult.getTotalFee();
			gameCount++;

			int playerCount = gameResult.getPlayerCount();
			for (int playerId = 0; playerId < playerCount; playerId++) {
				String playerName = gameResult.getPlayerName(playerId);
				if (playerName.startsWith(DEFAULT_PLAYER_NAME_PREFIX))
					continue;

				playerName = PlayerUIUtil.toCanonicalName(playerName);
				if (!attendCountMap.containsKey(playerName)) {
					attendCountMap.put(playerName, new PlayerAttendCount(
							playerName));
				}

				PlayerAttendCount attendCount = attendCountMap.get(playerName);
				attendCount.increaseCound();
			}
		}

		Collection<PlayerAttendCount> playerAttendCounts = attendCountMap
				.values();
		playerAttendCountArray = new PlayerAttendCount[playerAttendCounts
				.size()];
		playerAttendCounts.toArray(playerAttendCountArray);
		Arrays.sort(playerAttendCountArray);

		if (playerAttendCountArray.length < 1) {
			noHistoryTextView.setVisibility(View.VISIBLE);
			mainView.setVisibility(View.INVISIBLE);
		} else {
			noHistoryTextView.setVisibility(View.INVISIBLE);
			mainView.setVisibility(View.VISIBLE);

			UIUtil.setGameCountTextView(activity, gameCountTextView, gameCount);
			UIUtil.setFeeTextView(activity, totalFeeSumTextView, totalFeeSum);

			for (int i = 0; i < Constants.MAX_PLAYER_COUNT; i++) {
				int imageResourceId = R.drawable.face_unknown_r;
				int tagColorResourceId = 0x00000000;
				if (i < playerAttendCountArray.length) {
					imageResourceId = PlayerUIUtil
							.getRoundResourceId(playerAttendCountArray[i].name);
					tagColorResourceId = PlayerUIUtil
							.getTagColor(playerAttendCountArray[i].name);
					if (playerAttendCountArray[i].name.length() > 3) {
						playerNameTextViews[i]
								.setText(playerAttendCountArray[i].name
										.subSequence(0, 4));
					} else {
						playerNameTextViews[i]
								.setText(playerAttendCountArray[i].name);
					}
					playerImageViews[i].setImageResource(imageResourceId);
					playerTagViews[i].setBackgroundColor(tagColorResourceId);
					UIUtil.setGameCountTextView(activity,
							playerAttendCountTextViews[i],
							playerAttendCountArray[i].count);

					playerViews[i].setVisibility(View.VISIBLE);
				} else {
					playerViews[i].setVisibility(View.INVISIBLE);
				}
			}
		}
	}

	@Override
	public void onThreeMonthsGameReceiveStart() {
		showProgressDialog();
	}

	@Override
	public void onThreeMonthsGameReceiveProgressUpdate(ReceiveProgress value) {
		if (value == null)
			return;

		int current = value.getCurrent();
		int total = value.getTotal();

		setProgressDialogStatus(current, total, value.getMessage());
	}

	@Override
	public void onThreeMonthsGameReceiveFinished(ReceiveResult result) {
		FragmentActivity activity = getActivity();
		if (activity == null)
			return;

		if (result.isSuccess()) {
			if (!result.isCancel()) {
				Toast.makeText(
						activity,
						R.string.fragment_attendcount_import_three_month_success,
						Toast.LENGTH_LONG).show();
				reload(true);
			}
		} else {
			Toast.makeText(activity,
					R.string.fragment_attendcount_import_three_month_fail,
					Toast.LENGTH_LONG).show();
		}
		hideProgressDialog();
	}

	private void importThreeMonthData() {
		final FragmentActivity activity = getActivity();
		final AttendCountFragment instance = this;

		new AlertDialog.Builder(activity)
				.setTitle(R.string.dialog_import_three_month)
				.setMessage(R.string.dialog_are_you_sure_to_import_three_month)
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								ThreeMonthsGameReceiveTask task = new ThreeMonthsGameReceiveTask(
										activity, instance);
								task.execute();
							}

						}).setNegativeButton(android.R.string.no, null).show();
	}

	private void showProgressDialog() {
		FragmentActivity activity = getActivity();
		if (activity == null)
			return;

		if (progressDialog != null && progressDialog.isShowing()) {
			return;
		}

		progressDialog = new ProgressDialog(activity);
		progressDialog.setTitle(R.string.dialog_import_three_month);
		progressDialog
				.setMessage(getString(R.string.dialog_import_three_month_please_wait));
		progressDialog.setIndeterminate(true);
		progressDialog.setMax(100);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setCancelable(true);
		progressDialog.show();
	}

	private void setProgressDialogStatus(int current, int total,
			final String message) {
		if (progressDialog == null) {
			showProgressDialog();
		} else {
			if (total <= 0) {
				progressDialog.setIndeterminate(true);
			} else {
				progressDialog.setIndeterminate(false);

				progressDialog.setMax(total);
				progressDialog.setProgress(current);
			}
			progressDialog.setMessage(message);
		}
	}

	private void hideProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	private static class PlayerAttendCount implements Serializable,
			Comparable<PlayerAttendCount> {

		private static final long serialVersionUID = 7782414183265330052L;

		private String name;
		private int count;

		public PlayerAttendCount(String name) {
			this.name = name;
			this.count = 0;
		}

		public void increaseCound() {
			count++;
		}

		@Override
		public int compareTo(PlayerAttendCount another) {
			if (count > another.count)
				return -1;
			if (count < another.count)
				return 1;
			return name.compareTo(another.name);
		}
	}
}
