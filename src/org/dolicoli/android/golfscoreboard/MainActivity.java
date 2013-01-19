package org.dolicoli.android.golfscoreboard;

import org.dolicoli.android.golfscoreboard.db.ResultDatabaseWorker;
import org.dolicoli.android.golfscoreboard.fragments.MenuFragment;
import org.dolicoli.android.golfscoreboard.fragments.main.PlayerRankingFragment;
import org.dolicoli.android.golfscoreboard.tasks.DownloadTickCheckTask;
import org.dolicoli.android.golfscoreboard.tasks.DownloadTickCheckTask.DownloadTickCheckResult;
import org.dolicoli.android.golfscoreboard.tasks.ImportAllTask;
import org.dolicoli.android.golfscoreboard.tasks.ImportAllTask.ReceiveProgress;
import org.dolicoli.android.golfscoreboard.tasks.ImportAllTask.ReceiveResult;
import org.dolicoli.android.golfscoreboard.utils.Reloadable;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class MainActivity extends SlidingFragmentActivity implements
		ImportAllTask.TaskListener,
		DownloadTickCheckTask.DownloadTickCheckListener {

	private View downloadView;
	private ProgressBar progressBar;
	private TextView progressMessageTextView;

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

		setContentView(R.layout.activity_main);

		downloadView = findViewById(R.id.DownloadView);
		progressBar = (ProgressBar) findViewById(R.id.ProgressBar);
		progressMessageTextView = (TextView) findViewById(R.id.ProgressTextView);

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
		case R.id.Settings:
			showSettingActivity();
			return true;
		}
		return super.onOptionsItemSelected(item);
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
								if (contentFragment != null
										&& (contentFragment instanceof Reloadable)) {
									((Reloadable) contentFragment).reload(true);
								}
							}

						}).setNegativeButton(android.R.string.no, null).show();
	}

	private void showSettingActivity() {
		Intent settingsIntent = new Intent(this, SettingsActivity.class);
		startActivity(settingsIntent);
	}

	private void resetDatabase() {
		ResultDatabaseWorker resultWorker = new ResultDatabaseWorker(this);
		resultWorker.reset();
	}

	@Override
	public void onDownloadTickCheckStart() {
		downloadView.setVisibility(View.VISIBLE);
		setProgressMessage(true, 100, 0, "최신 데이터를 확인합니다");
	}

	@Override
	public void onDownloadTickCheckFinished(DownloadTickCheckResult result) {
		downloadView.setVisibility(View.GONE);
		if (!result.isSuccess()) {
			Toast.makeText(this, R.string.activity_main_netshare_import_fail,
					Toast.LENGTH_LONG).show();
		}

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
		setProgressMessage(false, progress.getTotal(), progress.getCurrent(),
				progress.getMessage());
	}

	@Override
	public void onImportAllFinished(ReceiveResult result) {
		if (result.isSuccess()) {
			if (!result.isCancel()) {
				Toast.makeText(this,
						R.string.activity_main_netshare_import_success,
						Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(this, R.string.activity_main_netshare_import_fail,
					Toast.LENGTH_LONG).show();
		}
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
		if (init)
			return;
		
		init = true;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, new PlayerRankingFragment())
				.commit();

	}

	private void setProgressMessage(boolean indeterminate, final int max,
			final int current, final String message) {
		if (progressBar == null || progressMessageTextView == null)
			return;

		if (indeterminate) {
			progressBar.setIndeterminate(true);
		} else {
			progressBar.setIndeterminate(false);
			progressBar.setMax(max);
			progressBar.setProgress(current);
		}
		progressMessageTextView.setText(message);
	}
}
