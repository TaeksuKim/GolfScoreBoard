package org.dolicoli.android.golfscoreboard;

import org.dolicoli.android.golfscoreboard.fragments.netshare.NetShareClientFragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;
import android.view.WindowManager;

public class NetShareClientActivity extends FragmentActivity {

	public static final String IK_GAME_ID = "GAME_ID";

	@SuppressWarnings("unused")
	private NetShareClientFragment fragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.activity_netshare_client);

		FragmentManager fragmentManager = getSupportFragmentManager();
		fragment = (NetShareClientFragment) fragmentManager
				.findFragmentById(R.id.NetShareClientFragment);

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
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			setResult(Activity.RESULT_CANCELED);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
