package org.dolicoli.android.golfscoreboard;

import org.dolicoli.android.golfscoreboard.data.settings.GameSetting;
import org.dolicoli.android.golfscoreboard.db.GameSettingDatabaseWorker;
import org.dolicoli.android.golfscoreboard.db.ResultDatabaseWorker;
import org.dolicoli.android.golfscoreboard.fragments.MenuFragment;
import org.dolicoli.android.golfscoreboard.fragments.history.NewPlayerRankingFragment;
import org.dolicoli.android.golfscoreboard.fragments.main.MainFragmentContainer;
import org.dolicoli.android.golfscoreboard.tasks.DownloadTickCheckTask;
import org.dolicoli.android.golfscoreboard.tasks.DownloadTickCheckTask.DownloadTickCheckResult;
import org.dolicoli.android.golfscoreboard.tasks.ExportCurrentGameTask;
import org.dolicoli.android.golfscoreboard.tasks.ExportCurrentGameTask.ExportProgress;
import org.dolicoli.android.golfscoreboard.tasks.ExportCurrentGameTask.ExportResult;
import org.dolicoli.android.golfscoreboard.tasks.ImportAllTask;
import org.dolicoli.android.golfscoreboard.tasks.ImportAllTask.ReceiveProgress;
import org.dolicoli.android.golfscoreboard.tasks.ImportAllTask.ReceiveResult;
import org.dolicoli.android.golfscoreboard.tasks.ImportCurrentGameTask;
import org.dolicoli.android.golfscoreboard.tasks.ImportCurrentGameTask.ImportProgress;
import org.dolicoli.android.golfscoreboard.tasks.ImportCurrentGameTask.ImportResult;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class MainActivity extends SlidingFragmentActivity implements
		MainFragmentContainer, ImportCurrentGameTask.TaskListener,
		ExportCurrentGameTask.TaskListener, ImportAllTask.TaskListener,
		DownloadTickCheckTask.DownloadTickCheckListener {

	private ProgressDialog progressDialog;
	private View downloadView;

	@SuppressWarnings("unused")
	private Fragment contentFragment;

	private boolean init;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		init = false;

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		setBehindContentView(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new MenuFragment()).commit();

		// customize the SlidingMenu
		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		setContentView(R.layout.content_frame);

		downloadView = this.findViewById(R.id.DownloadView);

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
	public void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean alwaysDownload = preferences.getBoolean(
				getString(R.string.preference_auto_download_key), true);
		if (alwaysDownload) {
			DownloadTickCheckTask task = new DownloadTickCheckTask(this, this);
			task.execute();
		} else {
			initialize();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
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
		case R.id.Settings:
			showSettingActivity();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private static final int REQ_ADD_RESULT = 0x0001;
	private static final int REQ_NEW_GAME = 0x0002;
	private static final int REQ_MODIFY_GAME = 0x0003;
	private static final int REQ_IMPORT = 0x0004;

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
			reload(true);
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
			reload(true);
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
			reload(false);
			return;
		case REQ_IMPORT:
			if (resultCode == Activity.RESULT_OK) {
				reload(true);
			}
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
								reload(true);
							}

						}).setNegativeButton(android.R.string.no, null).show();
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
		startActivityForResult(intent, REQ_IMPORT);
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
				reload(false);
			}
		} else {
			Toast.makeText(MainActivity.this,
					R.string.activity_main_netshare_import_fail,
					Toast.LENGTH_LONG).show();
		}
		hideProgressDialog();
	}

	@Override
	public void reload(final boolean clean) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
			}
		});
	}

	private void exportData() {
		ExportCurrentGameTask task = new ExportCurrentGameTask(this, this);
		task.execute();
	}

	private void resetDatabase() {
		ResultDatabaseWorker resultWorker = new ResultDatabaseWorker(this);
		resultWorker.reset();
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

	@Override
	public void onDownloadTickCheckStart() {
	}

	@Override
	public void onDownloadTickCheckFinished(DownloadTickCheckResult result) {
		if (result.isSuccess() && result.isShouldRefresh()) {
			ImportAllTask task = new ImportAllTask(this, this);
			task.execute();
		} else {
			initialize();
		}
	}

	@Override
	public void onImportAllStart() {
		downloadView.setVisibility(View.VISIBLE);
	}

	@Override
	public void onImportAllProgressUpdate(ReceiveProgress progress) {
	}

	@Override
	public void onImportAllFinished(ReceiveResult result) {
		downloadView.setVisibility(View.GONE);
		initialize();
	}

	public void switchContent(Fragment fragment) {
		if (!init)
			return;
		contentFragment = fragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment).commit();
		getSlidingMenu().showContent();
	}

	private void initialize() {
		init = true;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, new NewPlayerRankingFragment())
				.commit();

	}
}
