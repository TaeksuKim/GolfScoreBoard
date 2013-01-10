package org.dolicoli.android.golfscoreboard.fragments.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.dolicoli.android.golfscoreboard.Constants;
import org.dolicoli.android.golfscoreboard.CurrentGameActivity;
import org.dolicoli.android.golfscoreboard.R;
import org.dolicoli.android.golfscoreboard.data.SingleGameResult;
import org.dolicoli.android.golfscoreboard.data.settings.GameSetting;
import org.dolicoli.android.golfscoreboard.data.settings.Result;
import org.dolicoli.android.golfscoreboard.db.GameSettingDatabaseWorker;
import org.dolicoli.android.golfscoreboard.tasks.CurrentGameQueryTask;
import org.dolicoli.android.golfscoreboard.tasks.ImportCurrentGameTask;
import org.dolicoli.android.golfscoreboard.tasks.ImportCurrentGameTask.ImportProgress;
import org.dolicoli.android.golfscoreboard.tasks.ImportCurrentGameTask.ImportResult;
import org.dolicoli.android.golfscoreboard.tasks.ThreeMonthsGameReceiveTask;
import org.dolicoli.android.golfscoreboard.tasks.ThreeMonthsGameReceiveTask.ReceiveProgress;
import org.dolicoli.android.golfscoreboard.tasks.ThreeMonthsGameReceiveTask.ReceiveResult;
import org.dolicoli.android.golfscoreboard.utils.FeeCalculator;
import org.dolicoli.android.golfscoreboard.utils.PlayerUIUtil;
import org.dolicoli.android.golfscoreboard.utils.UIUtil;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateUtils;
import android.util.SparseArray;
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

public class CurrentGameFragment extends Fragment implements OnClickListener,
		ThreeMonthsGameReceiveTask.TaskListener,
		CurrentGameQueryTask.TaskListener, ImportCurrentGameTask.TaskListener {

	@SuppressWarnings("unused")
	private static final String TAG = "CurrentGameFragment";
	private static final int REQ_CURRENT_GAME = 0x0001;

	private ProgressDialog progressDialog;
	private SingleGameResult gameResult;

	private View contentView;

	private TextView dateTextView;
	private TextView currentHoleTextView;
	private TextView finalHoleTextView;

	private View[] playerViews;

	private ImageView[] playerImageViews;
	private View[] playerTagViews;
	private TextView[] playerNameTextViews;
	private TextView[] playerScoreTextViews;
	private TextView[] playerTotalFeeTextViews;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle("오늘 게임");

		View view = inflater.inflate(R.layout.current_game, null);

		contentView = view.findViewById(R.id.content_frame);
		dateTextView = (TextView) view.findViewById(R.id.DateTextView);
		currentHoleTextView = (TextView) view
				.findViewById(R.id.CurrentHoleTextView);
		finalHoleTextView = (TextView) view
				.findViewById(R.id.FinalHoleTextView);

		playerViews = new View[Constants.MAX_PLAYER_COUNT];
		playerViews[0] = view.findViewById(R.id.Player1View);
		playerViews[1] = view.findViewById(R.id.Player2View);
		playerViews[2] = view.findViewById(R.id.Player3View);
		playerViews[3] = view.findViewById(R.id.Player4View);
		playerViews[4] = view.findViewById(R.id.Player5View);
		playerViews[5] = view.findViewById(R.id.Player6View);

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

		playerTagViews = new View[Constants.MAX_PLAYER_COUNT];
		playerTagViews[0] = view.findViewById(R.id.Player1TagView);
		playerTagViews[1] = view.findViewById(R.id.Player2TagView);
		playerTagViews[2] = view.findViewById(R.id.Player3TagView);
		playerTagViews[3] = view.findViewById(R.id.Player4TagView);
		playerTagViews[4] = view.findViewById(R.id.Player5TagView);
		playerTagViews[5] = view.findViewById(R.id.Player6TagView);

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

		playerScoreTextViews = new TextView[Constants.MAX_PLAYER_COUNT];
		playerScoreTextViews[0] = (TextView) view
				.findViewById(R.id.Player1ScoreTextView);
		playerScoreTextViews[1] = (TextView) view
				.findViewById(R.id.Player2ScoreTextView);
		playerScoreTextViews[2] = (TextView) view
				.findViewById(R.id.Player3ScoreTextView);
		playerScoreTextViews[3] = (TextView) view
				.findViewById(R.id.Player4ScoreTextView);
		playerScoreTextViews[4] = (TextView) view
				.findViewById(R.id.Player5ScoreTextView);
		playerScoreTextViews[5] = (TextView) view
				.findViewById(R.id.Player6ScoreTextView);

		playerTotalFeeTextViews = new TextView[Constants.MAX_PLAYER_COUNT];
		playerTotalFeeTextViews[0] = (TextView) view
				.findViewById(R.id.Player1TotalFeeTextView);
		playerTotalFeeTextViews[1] = (TextView) view
				.findViewById(R.id.Player2TotalFeeTextView);
		playerTotalFeeTextViews[2] = (TextView) view
				.findViewById(R.id.Player3TotalFeeTextView);
		playerTotalFeeTextViews[3] = (TextView) view
				.findViewById(R.id.Player4TotalFeeTextView);
		playerTotalFeeTextViews[4] = (TextView) view
				.findViewById(R.id.Player5TotalFeeTextView);
		playerTotalFeeTextViews[5] = (TextView) view
				.findViewById(R.id.Player6TotalFeeTextView);

		view.findViewById(R.id.MainView).setOnClickListener(this);
		view.findViewById(R.id.PlayerView).setOnClickListener(this);
		view.findViewById(R.id.ImportButton).setOnClickListener(this);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setHasOptionsMenu(true);
		reloadData();
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
		final int id = v.getId();
		switch (id) {
		case R.id.MainView:
		case R.id.PlayerView:
			Intent historyIntent = new Intent(getActivity(),
					CurrentGameActivity.class);
			startActivityForResult(historyIntent, REQ_CURRENT_GAME);
			return;
		case R.id.ImportButton:
			importCurentGame();
			return;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQ_CURRENT_GAME) {
			reload(true);
		}
	}

	@Override
	public void onThreeMonthsGameReceiveStart() {
		showProgressDialog(R.string.dialog_import_three_month,
				R.string.dialog_import_three_month_please_wait);
	}

	@Override
	public void onThreeMonthsGameReceiveProgressUpdate(ReceiveProgress progress) {
		if (progress == null)
			return;

		int current = progress.getCurrent();
		int total = progress.getTotal();

		setProgressDialogStatus(R.string.dialog_import_three_month,
				R.string.dialog_import_three_month_please_wait, current, total,
				progress.getMessage());
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
				reloadData();
			}
		} else {
			Toast.makeText(activity,
					R.string.fragment_attendcount_import_three_month_fail,
					Toast.LENGTH_LONG).show();
		}
		hideProgressDialog();
	}

	@Override
	public void onImportGameStart() {
		showProgressDialog(R.string.dialog_import_current_game,
				R.string.dialog_import_please_wait);
	}

	@Override
	public void onImportGameProgressUpdate(ImportProgress progress) {
		if (progress == null)
			return;

		int messageId = progress.getMessageId();
		setProgressDialogStatus(R.string.dialog_import_current_game,
				R.string.dialog_import_please_wait, progress.getCurrent(),
				progress.getTotal(), getString(messageId));
	}

	@Override
	public void onImportGameFinished(ImportResult result) {
		if (result.isSuccess()) {
			if (!result.isCancel()) {
				Toast.makeText(getActivity(),
						R.string.activity_main_netshare_import_success,
						Toast.LENGTH_LONG).show();
				reloadData();
			}
		} else {
			Toast.makeText(getActivity(),
					R.string.activity_main_netshare_import_fail,
					Toast.LENGTH_LONG).show();
		}
		hideProgressDialog();
	}

	private void reload(boolean clean) {
		if (getActivity() == null || gameResult == null)
			return;

		int holeCount = gameResult.getHoleCount();

		int playerCount = gameResult.getPlayerCount();
		SparseArray<PlayerScore> playerScoreMap = new SparseArray<PlayerScore>();

		ArrayList<PlayerScore> list = new ArrayList<PlayerScore>();
		for (int playerId = 0; playerId < playerCount; playerId++) {
			PlayerScore playerScore = new PlayerScore(playerId,
					gameResult.getPlayerName(playerId),
					gameResult.getHandicap(playerId),
					gameResult.getHandicap(playerId)
							- gameResult.getUsedHandicap(playerId),
					gameResult.getExtraScore(playerId));
			playerScoreMap.put(playerId, playerScore);
			list.add(playerId, playerScore);
		}

		int currentHole = 0;
		for (Result result : gameResult.getResults()) {
			if (currentHole < result.getHoleNumber())
				currentHole = result.getHoleNumber();

			int[] fees = FeeCalculator.calculateFee(gameResult, result);

			for (int playerId = 0; playerId < playerCount; playerId++) {
				PlayerScore playerScore = list.get(playerId);

				playerScore.score += result.getFinalScore(playerId);
				playerScore.originalHoleFee += fees[playerId];
				playerScore.adjustedHoleFee = playerScore.originalHoleFee;
			}
		}

		for (int playerId = 0; playerId < playerCount; playerId++) {
			PlayerScore playerScore = list.get(playerId);
			playerScore.extraScore = gameResult.getExtraScore(playerId);
			playerScore.originalScore = playerScore.score;
			playerScore.score -= playerScore.extraScore;
		}

		Collections.sort(list);
		calculateRanking(list);
		calculateRankingFee(list, gameResult);
		calculateAvgRanking(currentHole, list);
		calculateOverPar(currentHole, list);

		adjustFee(list, gameResult, currentHole >= gameResult.getHoleCount());
		Collections.sort(list); // Sort again after adjustFee has been set

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

		for (int i = 0; i < Constants.MAX_PLAYER_COUNT; i++) {
			if (i < playerCount) {
				playerViews[i].setVisibility(View.VISIBLE);

				PlayerScore playerScore = list.get(i);
				playerImageViews[i].setImageResource(PlayerUIUtil
						.getRoundResourceId(playerScore.name));
				playerTagViews[i].setBackgroundColor(PlayerUIUtil
						.getTagColor(playerScore.name));
				playerNameTextViews[i].setText(playerScore.name);
				UIUtil.setScoreTextView(getActivity(), playerScoreTextViews[i],
						playerScore.score);
				UIUtil.setFeeTextView(getActivity(),
						playerTotalFeeTextViews[i],
						playerScore.adjustedTotalFee);
			} else {
				playerViews[i].setVisibility(View.GONE);
			}
		}

		contentView.setVisibility(View.VISIBLE);
	}

	private void reloadData() {
		CurrentGameQueryTask task = new CurrentGameQueryTask(getActivity(),
				this);
		task.execute();
	}

	private void importThreeMonthData() {
		final FragmentActivity activity = getActivity();
		final CurrentGameFragment instance = this;

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

	private void showProgressDialog(int defaultTitleId, int defaultMessageId) {
		FragmentActivity activity = getActivity();
		if (activity == null)
			return;

		if (progressDialog != null && progressDialog.isShowing()) {
			return;
		}

		progressDialog = new ProgressDialog(activity);
		progressDialog.setTitle(defaultTitleId);
		progressDialog.setMessage(getString(defaultMessageId));
		progressDialog.setIndeterminate(true);
		progressDialog.setMax(100);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setCancelable(true);
		progressDialog.show();
	}

	private void setProgressDialogStatus(int defaultTitleId,
			int defaultMessageId, final int current, final int total,
			final String message) {
		if (progressDialog == null) {
			showProgressDialog(defaultTitleId, defaultMessageId);
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

	@Override
	public void onCurrentGameQueryStarted() {
	}

	@Override
	public void onCurrentGameQueryFinished(SingleGameResult gameResult) {
		this.gameResult = gameResult;
		reload(false);
	}

	private void calculateRanking(ArrayList<PlayerScore> list) {
		PlayerScore prevPlayerScore = null;
		int ranking = 1;
		for (PlayerScore playerScore : list) {
			if (prevPlayerScore != null
					&& prevPlayerScore.score == playerScore.score) {
				playerScore.ranking = prevPlayerScore.ranking;
			} else {
				playerScore.ranking = ranking;
			}
			ranking++;
			prevPlayerScore = playerScore;
		}

		int size = list.size();
		for (int i = 0; i < size; i++) {
			PlayerScore playerScore = list.get(i);
			playerScore.sameRankingCount = 0;
			for (int j = 0; j < size; j++) {
				PlayerScore compare = list.get(j);
				if (compare.ranking == playerScore.ranking) {
					playerScore.sameRankingCount++;
				}
			}
		}
	}

	private void calculateRankingFee(ArrayList<PlayerScore> list,
			SingleGameResult gameSetting) {
		for (PlayerScore playerScore : list) {
			int sum = 0;
			int sameRankingCount = playerScore.sameRankingCount;

			for (int i = 0; i < sameRankingCount; i++) {
				sum += gameSetting.getRankingFeeForRanking(playerScore.ranking
						+ i);
			}
			playerScore.originalRankingFee = FeeCalculator.ceil(sum,
					sameRankingCount);
			playerScore.adjustedRankingFee = playerScore.originalRankingFee;
		}
	}

	private void calculateAvgRanking(int currentHole,
			ArrayList<PlayerScore> list) {
	}

	private void calculateOverPar(int currentHole, ArrayList<PlayerScore> list) {
	}

	private void adjustFee(ArrayList<PlayerScore> list,
			SingleGameResult gameSetting, boolean isGameFinished) {
		int sumOfHoleFee = 0;
		int sumOfRankingFee = 0;
		for (PlayerScore playerScore : list) {
			sumOfHoleFee += playerScore.originalHoleFee;
			sumOfRankingFee += playerScore.originalRankingFee;

			playerScore.originalTotalFee = playerScore.originalHoleFee
					+ playerScore.originalRankingFee;
		}

		int rankingFee = gameSetting.getRankingFee();
		int holeFee = gameSetting.getHoleFee();

		int remainOfHoleFee = 0;
		if (sumOfHoleFee < holeFee && isGameFinished) {
			int additionalHoleFeePerPlayer = FeeCalculator.ceil(holeFee
					- sumOfHoleFee, gameSetting.getPlayerCount());

			int sum = 0;
			for (PlayerScore playerScore : list) {
				playerScore.adjustedHoleFee += additionalHoleFeePerPlayer;
				sum += playerScore.adjustedHoleFee;
			}
			remainOfHoleFee = sum - holeFee;
		} else if (sumOfHoleFee > holeFee) {
			remainOfHoleFee = sumOfHoleFee - holeFee;
		}

		int remainOfRankingFee = 0;
		if (sumOfRankingFee < rankingFee) {
			int additionalRankingFeePerPlayer = FeeCalculator.ceil(rankingFee
					- sumOfRankingFee, gameSetting.getPlayerCount());

			int sum = 0;
			for (PlayerScore playerScore : list) {
				playerScore.adjustedRankingFee += additionalRankingFeePerPlayer;
				sum += playerScore.adjustedRankingFee;
			}
			remainOfRankingFee = sum - rankingFee;
		} else if (sumOfRankingFee > rankingFee) {
			remainOfRankingFee = sumOfRankingFee - rankingFee;
		}

		PlayerScore lastSinglePlayerScore = null;
		for (PlayerScore playerScore : list) {
			if (playerScore.sameRankingCount <= 1) {
				lastSinglePlayerScore = playerScore;
			}
		}
		if (remainOfHoleFee > 0 || remainOfRankingFee > 0) {
			if (lastSinglePlayerScore != null) {
				lastSinglePlayerScore.adjustedRankingFee -= (remainOfHoleFee + remainOfRankingFee);
			} else {
				// 이 경우 어떻게 해야 할까요?
			}
		}

		sumOfHoleFee = 0;
		sumOfRankingFee = 0;
		for (PlayerScore playerScore : list) {
			sumOfHoleFee += playerScore.adjustedHoleFee;
			sumOfRankingFee += playerScore.adjustedRankingFee;

			playerScore.adjustedTotalFee = playerScore.adjustedHoleFee
					+ playerScore.adjustedRankingFee;
		}
	}

	private void importCurentGame() {
		GameSettingDatabaseWorker gameSettingWorker = new GameSettingDatabaseWorker(
				getActivity());
		GameSetting gameSetting = new GameSetting();
		gameSettingWorker.getGameSetting(gameSetting);

		String currentGameId = GameSetting
				.toGameIdFormat(gameSetting.getDate());

		ImportCurrentGameTask task = new ImportCurrentGameTask(getActivity(),
				this);
		task.execute(currentGameId);
	}

	private static class PlayerScore implements Comparable<PlayerScore> {

		private int playerId;
		private String name;
		private int ranking;
		private int sameRankingCount;

		private int originalScore;
		private int score;
		private int extraScore;

		private int originalHoleFee, adjustedHoleFee;
		private int originalRankingFee, adjustedRankingFee;
		@SuppressWarnings("unused")
		private int originalTotalFee, adjustedTotalFee;

		public PlayerScore(int playerId, String name, int handicap,
				int remainHandicap, int extraScore) {
			this.playerId = playerId;
			this.name = name;
			this.extraScore = extraScore;

			this.ranking = 0;
			this.score = 0;

			this.originalHoleFee = 0;
			this.originalRankingFee = 0;
			this.originalTotalFee = 0;
			this.adjustedHoleFee = 0;
			this.adjustedRankingFee = 0;
			this.adjustedTotalFee = 0;
		}

		@Override
		public int compareTo(PlayerScore compare) {
			if (ranking < compare.ranking)
				return -1;
			if (ranking > compare.ranking)
				return 1;
			if (score < compare.score)
				return -1;
			if (score > compare.score)
				return 1;
			if (originalScore < compare.originalScore)
				return -1;
			if (originalScore > compare.originalScore)
				return 1;
			if (adjustedTotalFee < compare.adjustedTotalFee)
				return -1;
			if (adjustedTotalFee > compare.adjustedTotalFee)
				return 1;
			if (playerId < compare.playerId)
				return -1;
			if (playerId > compare.playerId)
				return 1;
			return 0;
		}
	}
}
