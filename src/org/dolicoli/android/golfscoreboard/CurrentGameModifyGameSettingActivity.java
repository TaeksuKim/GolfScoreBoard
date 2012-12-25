package org.dolicoli.android.golfscoreboard;

import org.dolicoli.android.golfscoreboard.data.settings.GameSetting;
import org.dolicoli.android.golfscoreboard.data.settings.PlayerSetting;
import org.dolicoli.android.golfscoreboard.db.GameSettingDatabaseWorker;
import org.dolicoli.android.golfscoreboard.db.PlayerCacheDatabaseWorker;
import org.dolicoli.android.golfscoreboard.db.PlayerSettingDatabaseWorker;
import org.dolicoli.android.golfscoreboard.fragments.currentgame.ModifyGameSettingFragment;
import org.dolicoli.android.golfscoreboard.fragments.currentgame.ModifyHandicapSettingFragment;
import org.dolicoli.android.golfscoreboard.fragments.currentgame.ModifyHoleFeeSettingFragment;
import org.dolicoli.android.golfscoreboard.fragments.currentgame.ModifyPlayerSettingFragment;
import org.dolicoli.android.golfscoreboard.fragments.currentgame.ModifyRankingFeeSettingFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

public class CurrentGameModifyGameSettingActivity extends FragmentActivity
		implements InputFragmentListener, OnClickListener {

	private static final int PAGE_GAME_SETTING = 0;
	private static final int PAGE_HOLE_FEE_SETTING = 1;
	private static final int PAGE_RANKING_FEE_SETTING = 2;
	private static final int PAGE_PLAYER_SETTING = 3;
	private static final int PAGE_HANDICAP_SETTING = 4;

	private static final int PAGE_FIRST = PAGE_GAME_SETTING;
	private static final int PAGE_LAST = PAGE_HANDICAP_SETTING;

	private int currentPage;
	private ModifyGameSettingFragment gameSettingFragment;
	private ModifyHoleFeeSettingFragment holeFeeSettingFragment;
	private ModifyRankingFeeSettingFragment rankingFeeSettingFragment;
	private ModifyPlayerSettingFragment playerSettingFragment;
	private ModifyHandicapSettingFragment handicapSettingFragment;
	private Button nextButton, confirmButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_current_game_modify_game_setting);

		FragmentManager fragmentManager = getSupportFragmentManager();
		gameSettingFragment = (ModifyGameSettingFragment) fragmentManager
				.findFragmentById(R.id.ModifyGameSettingFragment);
		holeFeeSettingFragment = (ModifyHoleFeeSettingFragment) fragmentManager
				.findFragmentById(R.id.ModifyHoleFeeSettingFragment);
		rankingFeeSettingFragment = (ModifyRankingFeeSettingFragment) fragmentManager
				.findFragmentById(R.id.ModifyRankingFeeSettingFragment);
		playerSettingFragment = (ModifyPlayerSettingFragment) fragmentManager
				.findFragmentById(R.id.ModifyPlayerSettingFragment);
		handicapSettingFragment = (ModifyHandicapSettingFragment) fragmentManager
				.findFragmentById(R.id.ModifyHandicapSettingFragment);

		currentPage = 0;

		findViewById(R.id.BackButton).setOnClickListener(this);
		nextButton = (Button) findViewById(R.id.NextButton);
		nextButton.setOnClickListener(this);
		confirmButton = (Button) findViewById(R.id.ConfirmButton);
		confirmButton.setOnClickListener(this);
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
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		showGameSettingFragment();
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

	@Override
	public void onClick(View view) {
		final int id = view.getId();
		switch (id) {
		case R.id.BackButton:
			if (currentPage > PAGE_FIRST) {
				showFragment(currentPage - 1);
			}
			break;
		case R.id.NextButton:
			if (currentPage < PAGE_LAST) {
				showFragment(currentPage + 1);
			}
			break;
		case R.id.ConfirmButton:
			GameSetting gameSetting = saveGameSetting();
			PlayerSetting playerSetting = savePlayerSetting();
			savePlayerCache(gameSetting, playerSetting);
			setResult(Activity.RESULT_OK);
			finish();
			break;
		case R.id.CancelButton:
			setResult(Activity.RESULT_CANCELED);
			finish();
			break;
		}
	}

	private void showFragment(int page) {
		switch (page) {
		case PAGE_GAME_SETTING:
			showGameSettingFragment();
			return;
		case PAGE_HOLE_FEE_SETTING:
			showHoleFeeSettingFragment();
			return;
		case PAGE_RANKING_FEE_SETTING:
			showRankingFeeSettingFragment();
			return;
		case PAGE_PLAYER_SETTING:
			showPlayerSettingFragment();
			return;
		case PAGE_HANDICAP_SETTING:
			showHandicapSettingFragment();
			return;
		}
	}

	@Override
	public void inputDataChanged() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (playerSettingFragment == null
						|| handicapSettingFragment == null)
					confirmButton.setEnabled(false);

				if (!playerSettingFragment.isAllFieldValid()
						|| !handicapSettingFragment.isAllFieldValid()) {
					confirmButton.setEnabled(false);
				}
				confirmButton.setEnabled(true);
			}
		});
	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.activity_modify_game_cancel)
				.setMessage(
						R.string.activity_modify_game_are_you_sure_to_cancel)
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								setResult(Activity.RESULT_CANCELED);
								finish();
							}

						}).setNegativeButton(android.R.string.no, null).show();
	}

	private void showGameSettingFragment() {
		currentPage = PAGE_GAME_SETTING;
		getActionBar().setTitle(
				R.string.activity_modify_game_title_game_setting);

		gameSettingFragment.getView().setVisibility(View.VISIBLE);
		holeFeeSettingFragment.getView().setVisibility(View.GONE);
		rankingFeeSettingFragment.getView().setVisibility(View.GONE);
		playerSettingFragment.getView().setVisibility(View.GONE);
		handicapSettingFragment.getView().setVisibility(View.GONE);

		findViewById(R.id.ConfirmButton).setVisibility(View.GONE);
		findViewById(R.id.CancelButton).setVisibility(View.VISIBLE);
		findViewById(R.id.BackButton).setVisibility(View.GONE);
		findViewById(R.id.NextButton).setVisibility(View.VISIBLE);

		setNextButtonEnable(true);
	}

	private void showHoleFeeSettingFragment() {
		currentPage = PAGE_HOLE_FEE_SETTING;
		getActionBar().setTitle(
				R.string.activity_modify_game_title_hole_fee_setting);

		holeFeeSettingFragment.setInitialValues(
				gameSettingFragment.getHoleCount(),
				gameSettingFragment.getPlayerCount(),
				gameSettingFragment.getHoleFee());

		gameSettingFragment.getView().setVisibility(View.GONE);
		holeFeeSettingFragment.getView().setVisibility(View.VISIBLE);
		rankingFeeSettingFragment.getView().setVisibility(View.GONE);
		playerSettingFragment.getView().setVisibility(View.GONE);
		handicapSettingFragment.getView().setVisibility(View.GONE);

		findViewById(R.id.ConfirmButton).setVisibility(View.GONE);
		findViewById(R.id.CancelButton).setVisibility(View.GONE);
		findViewById(R.id.BackButton).setVisibility(View.VISIBLE);
		findViewById(R.id.NextButton).setVisibility(View.VISIBLE);

		setNextButtonEnable(true);
	}

	private void showRankingFeeSettingFragment() {
		currentPage = PAGE_RANKING_FEE_SETTING;
		getActionBar().setTitle(
				R.string.activity_modify_game_title_ranking_fee_setting);

		rankingFeeSettingFragment.setInitialValues(
				gameSettingFragment.getPlayerCount(),
				gameSettingFragment.getRankingFee());

		gameSettingFragment.getView().setVisibility(View.GONE);
		holeFeeSettingFragment.getView().setVisibility(View.GONE);
		rankingFeeSettingFragment.getView().setVisibility(View.VISIBLE);
		playerSettingFragment.getView().setVisibility(View.GONE);
		handicapSettingFragment.getView().setVisibility(View.GONE);

		findViewById(R.id.ConfirmButton).setVisibility(View.GONE);
		findViewById(R.id.CancelButton).setVisibility(View.GONE);
		findViewById(R.id.BackButton).setVisibility(View.VISIBLE);
		findViewById(R.id.NextButton).setVisibility(View.VISIBLE);

		setNextButtonEnable(rankingFeeSettingFragment.isEnableToFinish());
	}

	private void showPlayerSettingFragment() {
		currentPage = PAGE_PLAYER_SETTING;
		getActionBar().setTitle(
				R.string.activity_modify_game_title_player_setting);

		gameSettingFragment.getView().setVisibility(View.GONE);
		holeFeeSettingFragment.getView().setVisibility(View.GONE);
		rankingFeeSettingFragment.getView().setVisibility(View.GONE);
		playerSettingFragment.getView().setVisibility(View.VISIBLE);
		handicapSettingFragment.getView().setVisibility(View.GONE);

		findViewById(R.id.ConfirmButton).setVisibility(View.GONE);
		findViewById(R.id.CancelButton).setVisibility(View.GONE);
		findViewById(R.id.BackButton).setVisibility(View.VISIBLE);
		findViewById(R.id.NextButton).setVisibility(View.VISIBLE);

		setNextButtonEnable(playerSettingFragment.isAllFieldValid());
	}

	private void showHandicapSettingFragment() {
		currentPage = PAGE_HANDICAP_SETTING;
		getActionBar().setTitle("ÇÚµðÄ¸ ¼³Á¤");

		int playerCount = gameSettingFragment.getPlayerCount();
		String[] playerNames = new String[playerCount];
		int[] handicaps = new int[playerCount];
		int[] extraScores = new int[playerCount];
		PlayerSettingDatabaseWorker playerSettingWorker = new PlayerSettingDatabaseWorker(
				this);
		PlayerSetting playerSetting = new PlayerSetting();
		playerSettingWorker.getPlayerSetting(playerSetting);
		for (int i = 0; i < playerCount; i++) {
			playerNames[i] = playerSettingFragment.getPlayerName(i);
			handicaps[i] = playerSetting.getHandicap(i);
			extraScores[i] = playerSetting.getExtraScore(i);
		}
		handicapSettingFragment.setInitialValues(playerNames, handicaps,
				extraScores);

		gameSettingFragment.getView().setVisibility(View.GONE);
		holeFeeSettingFragment.getView().setVisibility(View.GONE);
		rankingFeeSettingFragment.getView().setVisibility(View.GONE);
		playerSettingFragment.getView().setVisibility(View.GONE);
		handicapSettingFragment.getView().setVisibility(View.VISIBLE);

		findViewById(R.id.ConfirmButton).setVisibility(View.VISIBLE);
		findViewById(R.id.CancelButton).setVisibility(View.GONE);
		findViewById(R.id.BackButton).setVisibility(View.VISIBLE);
		findViewById(R.id.NextButton).setVisibility(View.GONE);

		setNextButtonEnable(true);
	}

	private GameSetting saveGameSetting() {
		GameSetting setting = new GameSetting();

		setting.setHoleCount(gameSettingFragment.getHoleCount());
		setting.setPlayerCount(gameSettingFragment.getPlayerCount());

		setting.setGameFee(gameSettingFragment.getGameFee());
		setting.setExtraFee(gameSettingFragment.getExtraFee());
		setting.setRankingFee(gameSettingFragment.getRankingFee());

		setting.setHoleFeeForRanking(1,
				holeFeeSettingFragment.getFeeForRanking(1));
		setting.setHoleFeeForRanking(2,
				holeFeeSettingFragment.getFeeForRanking(2));
		setting.setHoleFeeForRanking(3,
				holeFeeSettingFragment.getFeeForRanking(3));
		setting.setHoleFeeForRanking(4,
				holeFeeSettingFragment.getFeeForRanking(4));
		setting.setHoleFeeForRanking(5,
				holeFeeSettingFragment.getFeeForRanking(5));
		setting.setHoleFeeForRanking(6,
				holeFeeSettingFragment.getFeeForRanking(6));

		setting.setRankingFeeForRanking(1,
				rankingFeeSettingFragment.getFeeForRanking(1));
		setting.setRankingFeeForRanking(2,
				rankingFeeSettingFragment.getFeeForRanking(2));
		setting.setRankingFeeForRanking(3,
				rankingFeeSettingFragment.getFeeForRanking(3));
		setting.setRankingFeeForRanking(4,
				rankingFeeSettingFragment.getFeeForRanking(4));
		setting.setRankingFeeForRanking(5,
				rankingFeeSettingFragment.getFeeForRanking(5));
		setting.setRankingFeeForRanking(6,
				rankingFeeSettingFragment.getFeeForRanking(6));

		setting.setDate(rankingFeeSettingFragment.getDateTime());

		GameSettingDatabaseWorker gameSettingWorker = new GameSettingDatabaseWorker(
				this);
		gameSettingWorker.updateGameSetting(setting);

		return setting;
	}

	private PlayerSetting savePlayerSetting() {
		PlayerSetting setting = new PlayerSetting();

		setting.setPlayerName(0, playerSettingFragment.getPlayerName(0));
		setting.setPlayerName(1, playerSettingFragment.getPlayerName(1));
		setting.setPlayerName(2, playerSettingFragment.getPlayerName(2));
		setting.setPlayerName(3, playerSettingFragment.getPlayerName(3));
		setting.setPlayerName(4, playerSettingFragment.getPlayerName(4));
		setting.setPlayerName(5, playerSettingFragment.getPlayerName(5));

		setting.setHandicap(0, handicapSettingFragment.getHandicap(0));
		setting.setHandicap(1, handicapSettingFragment.getHandicap(1));
		setting.setHandicap(2, handicapSettingFragment.getHandicap(2));
		setting.setHandicap(3, handicapSettingFragment.getHandicap(3));
		setting.setHandicap(4, handicapSettingFragment.getHandicap(4));
		setting.setHandicap(5, handicapSettingFragment.getHandicap(5));

		setting.setExtraScore(0, handicapSettingFragment.getExtraScore(0));
		setting.setExtraScore(1, handicapSettingFragment.getExtraScore(1));
		setting.setExtraScore(2, handicapSettingFragment.getExtraScore(2));
		setting.setExtraScore(3, handicapSettingFragment.getExtraScore(3));
		setting.setExtraScore(4, handicapSettingFragment.getExtraScore(4));
		setting.setExtraScore(5, handicapSettingFragment.getExtraScore(5));

		PlayerSettingDatabaseWorker playerSettingWorker = new PlayerSettingDatabaseWorker(
				this);
		playerSettingWorker.updatePlayerSetting(setting);

		return setting;
	}

	private void savePlayerCache(GameSetting gameSetting,
			PlayerSetting playerSetting) {
		int playerCount = gameSetting.getPlayerCount();
		String[] names = new String[playerCount];
		int[] handicaps = new int[playerCount];
		for (int i = 0; i < playerCount; i++) {
			names[i] = playerSetting.getPlayerName(i);
			handicaps[i] = playerSetting.getHandicap(i);
		}

		PlayerCacheDatabaseWorker worker = new PlayerCacheDatabaseWorker(this);
		worker.putPlayer(names, handicaps);
	}

	public void setNextButtonEnable(boolean enabled) {
		if (nextButton == null)
			return;
		nextButton.setEnabled(enabled);
	}
}
