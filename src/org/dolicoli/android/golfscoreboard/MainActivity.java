package org.dolicoli.android.golfscoreboard;

import java.util.Locale;

import org.dolicoli.android.golfscoreboard.data.settings.GameSetting;
import org.dolicoli.android.golfscoreboard.db.GameSettingDatabaseWorker;
import org.dolicoli.android.golfscoreboard.db.HistoryGameSettingDatabaseWorker;
import org.dolicoli.android.golfscoreboard.db.ResultDatabaseWorker;
import org.dolicoli.android.golfscoreboard.fragments.DummySectionFragment;
import org.dolicoli.android.golfscoreboard.fragments.main.AttendCountFragment;
import org.dolicoli.android.golfscoreboard.fragments.main.CurrentGameHoleResultFragment;
import org.dolicoli.android.golfscoreboard.fragments.main.CurrentGameSummaryFragment;
import org.dolicoli.android.golfscoreboard.fragments.main.MainFragmentContainer;
import org.dolicoli.android.golfscoreboard.tasks.ExportCurrentGameTask;
import org.dolicoli.android.golfscoreboard.tasks.ExportCurrentGameTask.ExportProgress;
import org.dolicoli.android.golfscoreboard.tasks.ExportCurrentGameTask.ExportResult;
import org.dolicoli.android.golfscoreboard.tasks.ImportGameTask;
import org.dolicoli.android.golfscoreboard.tasks.ImportGameTask.ImportProgress;
import org.dolicoli.android.golfscoreboard.tasks.ImportGameTask.ImportResult;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements
		MainFragmentContainer, ImportGameTask.TaskListener,
		ExportCurrentGameTask.TaskListener, OnClickListener,
		OnPageChangeListener {

	private static final int TAB_CURRENT_GAME_SUMMARY_FRAGMENT = 0;
	private static final int TAB_CURRENG_GAME_HOLE_RESULT_FRAGMENT = 1;
	private static final int TAB_ATTEND_COUNT_FRAGMENT = 2;

	private static final int TAB_COUNT = TAB_ATTEND_COUNT_FRAGMENT + 1;

	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	private Button addResultButton;

	private ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOnPageChangeListener(this);

		addResultButton = (Button) findViewById(R.id.AddResultButton);
		addResultButton.setOnClickListener(this);
		findViewById(R.id.ImportButton).setOnClickListener(this);

		getActionBar().setTitle(R.string.activity_main_title);

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean alwaysTurnOnScreen = preferences.getBoolean(
				getString(R.string.preference_always_turn_on_key), true);
		if (alwaysTurnOnScreen) {
			getWindow()
					.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		} else {
			getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {
		case R.id.AddResultButton:
			showAddResultActivity();
			return;
		case R.id.ImportButton:
			importThisGame();
			return;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.AddResult:
			showAddResultActivity();
			return true;
		case R.id.NewGame:
			showNewGameSettingActivity();
			return true;
		case R.id.ModifyGame:
			showModifyGameSettingActivity();
			return true;
		case R.id.Reset:
			showResetDialog();
			return true;
		case R.id.NetShareSendData:
			showExportDataDialog();
			return true;
		case R.id.NetShareReceiveData:
			importData();
			return true;
		case R.id.Save:
			saveHistory();
			return true;
		case R.id.Settings:
			showSettingActivity();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPageScrollStateChanged(int position) {
	}

	@Override
	public void onPageScrolled(int position, float positionOffest,
			int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
		if (position != TAB_CURRENG_GAME_HOLE_RESULT_FRAGMENT) {
			CurrentGameHoleResultFragment fragment = (CurrentGameHoleResultFragment) mSectionsPagerAdapter
					.getItem(TAB_CURRENG_GAME_HOLE_RESULT_FRAGMENT);
			if (fragment == null || !fragment.isAdded()) {
				return;
			}

			fragment.hideActionMode();
		}
	}

	private static final int REQ_ADD_RESULT = 0x0001;
	private static final int REQ_NEW_GAME = 0x0002;
	private static final int REQ_MODIFY_GAME = 0x0003;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		switch (requestCode) {
		case REQ_NEW_GAME:
			if (resultCode == Activity.RESULT_OK) {
				SharedPreferences preferences = PreferenceManager
						.getDefaultSharedPreferences(this);
				boolean autoUpload = preferences.getBoolean(
						getString(R.string.preference_auto_upload_key), true);
				if (autoUpload) {
					exportData();
				}
			}
			reload();
			return;
		case REQ_MODIFY_GAME:
			if (resultCode == Activity.RESULT_OK) {
				SharedPreferences preferences = PreferenceManager
						.getDefaultSharedPreferences(this);
				boolean autoUpload = preferences.getBoolean(
						getString(R.string.preference_auto_upload_key), true);
				if (autoUpload) {
					exportData();
				}
			}
			reload();
			return;
		case REQ_ADD_RESULT:
			if (resultCode == Activity.RESULT_OK) {
				SharedPreferences preferences = PreferenceManager
						.getDefaultSharedPreferences(this);
				boolean autoUpload = preferences.getBoolean(
						getString(R.string.preference_auto_upload_key), true);
				if (autoUpload) {
					exportData();
				}
			}
			reload();
			return;
		}
	}

	@Override
	public void showModifyGameSettingActivity() {
		Intent intent = new Intent(this,
				CurrentGameModifyGameSettingActivity.class);
		startActivityForResult(intent, REQ_MODIFY_GAME);
	}

	private void showResetDialog() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.dialog_reset)
				.setMessage(R.string.dialog_are_you_sure_to_reset)
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								resetDatabase();
								reload();
							}

						}).setNegativeButton(android.R.string.no, null).show();
	}

	private void showAddResultActivity() {
		CurrentGameSummaryFragment currentScoreFragment = (CurrentGameSummaryFragment) mSectionsPagerAdapter
				.getItem(TAB_CURRENT_GAME_SUMMARY_FRAGMENT);
		if (currentScoreFragment == null) {
			return;
		}

		if (currentScoreFragment.isAllGameFinished()) {
			return;
		}

		Intent intent = new Intent(this, CurrentGameAddResultActivity.class);
		startActivityForResult(intent, REQ_ADD_RESULT);
	}

	private void showNewGameSettingActivity() {
		Intent intent = new Intent(this,
				CurrentGameNewGameSettingActivity.class);
		startActivityForResult(intent, REQ_NEW_GAME);
	}

	private void showExportDataDialog() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.dialog_export_current_game)
				.setMessage(R.string.dialog_are_you_sure_to_export_current_game)
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								exportData();
							}

						}).setNegativeButton(android.R.string.no, null).show();
	}

	private void importData() {
		GameSettingDatabaseWorker gameSettingWorker = new GameSettingDatabaseWorker(
				MainActivity.this);
		GameSetting gameSetting = new GameSetting();
		gameSettingWorker.getGameSetting(gameSetting);

		String currentGameId = GameSetting
				.toGameIdFormat(gameSetting.getDate());

		Intent intent = new Intent(this, NetShareClientActivity.class);
		intent.putExtra(NetShareClientActivity.IK_GAME_ID, currentGameId);
		startActivity(intent);
	}

	private void saveHistory() {
		HistoryGameSettingDatabaseWorker worker = new HistoryGameSettingDatabaseWorker(
				this);
		worker.addCurrentHistory(true);

		reload();
	}

	private void showSettingActivity() {
		Intent settingsIntent = new Intent(this, SettingsActivity.class);
		startActivity(settingsIntent);
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
				Toast.makeText(MainActivity.this,
						R.string.activity_main_netshare_import_success,
						Toast.LENGTH_LONG).show();
				reload();
			}
		} else {
			Toast.makeText(MainActivity.this,
					R.string.activity_main_netshare_import_fail,
					Toast.LENGTH_LONG).show();
		}
		hideProgressDialog();
	}

	@Override
	public void reload() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (mSectionsPagerAdapter == null)
					return;

				int count = mSectionsPagerAdapter.getCount();
				Reloadable fragment = null;
				for (int i = 0; i < count; i++) {
					fragment = (Reloadable) mSectionsPagerAdapter.getItem(i);
					if (fragment != null)
						fragment.reload();
				}
			}
		});
	}

	private void exportData() {
		// UploadDataTask task = new UploadDataTask();
		ExportCurrentGameTask task = new ExportCurrentGameTask(this, this);
		task.execute();
	}

	private void resetDatabase() {
		ResultDatabaseWorker resultWorker = new ResultDatabaseWorker(this);
		resultWorker.reset();
	}

	private void importThisGame() {
		GameSettingDatabaseWorker gameSettingWorker = new GameSettingDatabaseWorker(
				this);
		GameSetting gameSetting = new GameSetting();
		gameSettingWorker.getGameSetting(gameSetting);

		String currentGameId = GameSetting
				.toGameIdFormat(gameSetting.getDate());

		ImportGameTask task = new ImportGameTask(this, this);
		task.execute(currentGameId);
	}

	private void showProgressDialog(int defaultTitleId, int defaultMessageId) {
		if (progressDialog != null && progressDialog.isShowing()) {
			return;
		}
		final Activity activity = this;

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

	private class SectionsPagerAdapter extends FragmentPagerAdapter {
		CurrentGameSummaryFragment currentGameSummaryFragment = new CurrentGameSummaryFragment();
		CurrentGameHoleResultFragment currentGameHoleResultFragment = new CurrentGameHoleResultFragment();
		AttendCountFragment attendCountFragment = new AttendCountFragment();

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case TAB_CURRENT_GAME_SUMMARY_FRAGMENT:
				return currentGameSummaryFragment;
			case TAB_CURRENG_GAME_HOLE_RESULT_FRAGMENT:
				return currentGameHoleResultFragment;
			case TAB_ATTEND_COUNT_FRAGMENT:
				return attendCountFragment;
			}
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			return TAB_COUNT;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case TAB_CURRENT_GAME_SUMMARY_FRAGMENT:
				return getString(
						R.string.activity_main_fragment_current_game_summary)
						.toUpperCase(Locale.getDefault());
			case TAB_CURRENG_GAME_HOLE_RESULT_FRAGMENT:
				return getString(
						R.string.activity_main_fragment_current_game_hole_result)
						.toUpperCase(Locale.getDefault());
			case TAB_ATTEND_COUNT_FRAGMENT:
				return getString(R.string.activity_main_fragment_attend_count)
						.toUpperCase(Locale.getDefault());
			}
			return null;
		}
	}

	@Override
	public void onExportCurrentGameStarted() {
		showProgressDialog(R.string.dialog_export_current_game,
				R.string.dialog_export_current_game_please_wait);
	}

	@Override
	public void onExportCurrentGameProgressUpdate(ExportProgress progress) {
		if (progress == null)
			return;

		int messageId = progress.getMessageId();
		setProgressDialogStatus(R.string.dialog_export_current_game,
				R.string.dialog_export_current_game_please_wait,
				progress.getCurrent(), progress.getTotal(),
				getString(messageId));
	}

	@Override
	public void onExportCurrentGameFinished(ExportResult result) {
		if (result.isSuccess()) {
			Toast.makeText(MainActivity.this,
					R.string.activity_main_netshare_upload_success,
					Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(MainActivity.this,
					R.string.activity_main_netshare_upload_fail,
					Toast.LENGTH_LONG).show();
		}
		hideProgressDialog();
	}
}
