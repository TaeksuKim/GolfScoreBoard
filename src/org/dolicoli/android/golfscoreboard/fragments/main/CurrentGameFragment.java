package org.dolicoli.android.golfscoreboard.fragments.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.dolicoli.android.golfscoreboard.Constants;
import org.dolicoli.android.golfscoreboard.CurrentGameActivity;
import org.dolicoli.android.golfscoreboard.NetShareClientActivity;
import org.dolicoli.android.golfscoreboard.R;
import org.dolicoli.android.golfscoreboard.data.OneGame;
import org.dolicoli.android.golfscoreboard.data.PlayerScore;
import org.dolicoli.android.golfscoreboard.data.settings.GameSetting;
import org.dolicoli.android.golfscoreboard.db.GameSettingDatabaseWorker;
import org.dolicoli.android.golfscoreboard.tasks.CurrentGameQueryTask;
import org.dolicoli.android.golfscoreboard.tasks.ImportCurrentGameTask;
import org.dolicoli.android.golfscoreboard.tasks.ImportCurrentGameTask.ImportProgress;
import org.dolicoli.android.golfscoreboard.tasks.ImportCurrentGameTask.ImportResult;
import org.dolicoli.android.golfscoreboard.tasks.ThreeMonthsGameReceiveTask;
import org.dolicoli.android.golfscoreboard.tasks.ThreeMonthsGameReceiveTask.ReceiveProgress;
import org.dolicoli.android.golfscoreboard.tasks.ThreeMonthsGameReceiveTask.ReceiveResult;
import org.dolicoli.android.golfscoreboard.utils.Reloadable;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class CurrentGameFragment extends Fragment implements OnClickListener,
		Reloadable, ThreeMonthsGameReceiveTask.TaskListener,
		CurrentGameQueryTask.TaskListener, ImportCurrentGameTask.TaskListener {

	@SuppressWarnings("unused")
	private static final String TAG = "CurrentGameFragment";

	private static final int REQ_CURRENT_GAME = 0x0001;
	private static final int REQ_IMPORT = 0x0004;

	private ProgressDialog progressDialog;
	private OneGame gameResult;

	private View contentView;

	private TextView dateTextView;
	private TextView currentHoleTextView;
	private TextView finalHoleTextView;

	private PlayerSummaryView[] playerViews;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle("ø¿¥√ ∞‘¿”");

		View view = inflater.inflate(R.layout.main_current_game, null);

		contentView = view.findViewById(R.id.content_frame);
		dateTextView = (TextView) view.findViewById(R.id.DateTextView);
		currentHoleTextView = (TextView) view
				.findViewById(R.id.CurrentHoleTextView);
		finalHoleTextView = (TextView) view
				.findViewById(R.id.FinalHoleTextView);

		playerViews = new PlayerSummaryView[Constants.MAX_PLAYER_COUNT];
		playerViews[0] = new PlayerSummaryView(
				view.findViewById(R.id.Player1View));
		playerViews[1] = new PlayerSummaryView(
				view.findViewById(R.id.Player2View));
		playerViews[2] = new PlayerSummaryView(
				view.findViewById(R.id.Player3View));
		playerViews[3] = new PlayerSummaryView(
				view.findViewById(R.id.Player4View));
		playerViews[4] = new PlayerSummaryView(
				view.findViewById(R.id.Player5View));
		playerViews[5] = new PlayerSummaryView(
				view.findViewById(R.id.Player6View));

		view.findViewById(R.id.MainView).setOnClickListener(this);
		view.findViewById(R.id.PlayerView).setOnClickListener(this);

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
		inflater.inflate(R.menu.main_current_game, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.ImportThreeMonth:
			importGame();
			return true;
		case R.id.ImportThisGame:
			importCurentGame();
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
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case REQ_CURRENT_GAME:
			reloadData();
			return;
		case REQ_IMPORT:
			if (resultCode == Activity.RESULT_OK) {
				reloadData();
			}
			return;
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

	@Override
	public void reload(boolean clean) {
		if (getActivity() == null || gameResult == null)
			return;

		finalHoleTextView.setText(String.valueOf(gameResult.getHoleCount()));

		currentHoleTextView.setText(getString(
				R.string.fragment_onegamesummary_current_hole_format,
				gameResult.getCurrentHole()));

		int playerCount = gameResult.getPlayerCount();
		ArrayList<PlayerScore> list = new ArrayList<PlayerScore>();
		for (int playerId = 0; playerId < playerCount; playerId++) {
			PlayerScore playerScore = gameResult.getPlayerScore(playerId);
			list.add(playerScore);
		}

		Collections.sort(list);

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

		for (int i = 0; i < Constants.MAX_PLAYER_COUNT; i++) {
			if (i < playerCount) {
				PlayerScore playerScore = list.get(i);

				playerViews[i].setVisibility(View.VISIBLE);
				playerViews[i].setValue(getActivity(), playerScore.getName(),
						playerScore.getRanking(), playerScore.getFinalScore(),
						playerScore.getAdjustedTotalFee());
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
	public void onCurrentGameQueryFinished(OneGame gameResult) {
		this.gameResult = gameResult;
		reload(false);
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

	private void importGame() {
		GameSettingDatabaseWorker gameSettingWorker = new GameSettingDatabaseWorker(
				getActivity());
		GameSetting gameSetting = new GameSetting();
		gameSettingWorker.getGameSetting(gameSetting);

		String currentGameId = GameSetting
				.toGameIdFormat(gameSetting.getDate());

		Intent intent = new Intent(getActivity(), NetShareClientActivity.class);
		intent.putExtra(NetShareClientActivity.IK_GAME_ID, currentGameId);
		startActivityForResult(intent, REQ_IMPORT);
	}
}
