package org.dolicoli.android.golfscoreboard;

import org.dolicoli.android.golfscoreboard.data.settings.Result;
import org.dolicoli.android.golfscoreboard.db.HistoryGameSettingDatabaseWorker;
import org.dolicoli.android.golfscoreboard.db.PlayerSettingDatabaseWorker;
import org.dolicoli.android.golfscoreboard.db.ResultDatabaseWorker;
import org.dolicoli.android.golfscoreboard.fragments.currentgame.ModifyResultFragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

public class CurrentGameModifyResultActivity extends FragmentActivity implements
		OnClickListener {

	public static final String IK_HOLE_NUMBER = "HOLE_NUMBER";

	private ModifyResultFragment mainFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_current_game_modify_result);

		FragmentManager fragmentManager = getSupportFragmentManager();
		mainFragment = (ModifyResultFragment) fragmentManager
				.findFragmentById(R.id.ModifyResultFragment);

		findViewById(R.id.ConfirmButton).setOnClickListener(this);
		findViewById(R.id.CancelButton).setOnClickListener(this);

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
			setResult(0);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {
		case R.id.ConfirmButton:
			saveResult();
			setResult(1);
			finish();
			break;
		case R.id.CancelButton:
			setResult(0);
			finish();
			break;
		}
	}

	private void saveResult() {
		int hole = mainFragment.getHoleNumber();
		int parNumber = mainFragment.getParNumber();
		Result result = new Result(hole, parNumber);

		for (int playerId = 0; playerId < Constants.MAX_PLAYER_COUNT; playerId++) {
			result.setScore(playerId, mainFragment.getScore(playerId));
			result.setUsedHandicap(playerId, mainFragment.getHandicap(playerId));
		}

		ResultDatabaseWorker resultWorker = new ResultDatabaseWorker(this);
		resultWorker.updateResult(result);

		PlayerSettingDatabaseWorker playerWorker = new PlayerSettingDatabaseWorker(
				this);
		playerWorker.updateUsedHandicap(result);

		HistoryGameSettingDatabaseWorker historyWorker = new HistoryGameSettingDatabaseWorker(
				this);
		historyWorker.addCurrentHistory(false);
	}
}
