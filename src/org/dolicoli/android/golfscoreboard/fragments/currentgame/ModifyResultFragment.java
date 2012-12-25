package org.dolicoli.android.golfscoreboard.fragments.currentgame;

import org.dolicoli.android.golfscoreboard.Constants;
import org.dolicoli.android.golfscoreboard.CurrentGameModifyResultActivity;
import org.dolicoli.android.golfscoreboard.R;
import org.dolicoli.android.golfscoreboard.data.UsedHandicap;
import org.dolicoli.android.golfscoreboard.data.settings.GameSetting;
import org.dolicoli.android.golfscoreboard.data.settings.PlayerSetting;
import org.dolicoli.android.golfscoreboard.data.settings.Result;
import org.dolicoli.android.golfscoreboard.db.GameSettingDatabaseWorker;
import org.dolicoli.android.golfscoreboard.db.PlayerSettingDatabaseWorker;
import org.dolicoli.android.golfscoreboard.db.ResultDatabaseWorker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

public class ModifyResultFragment extends Fragment implements OnClickListener {

	private RadioButton parThreeRadioButton, parFourRadioButton,
			parFiveRadioButton, parSixRadioButton;

	private TextView holeNumberTextView;
	private TextView[] playerNameTextViews;
	private Spinner[] playerScoreSpinners, playerHandicapSpinners;
	private ScoreSpinnerAdapter[] playerScoreSpinnerAdapters;
	private HandicapSpinnerAdapter[] playerHandicapSpinnerAdapters;

	private int holeNumber;
	private int playerCount;

	private Result oldResult;
	private UsedHandicap usedHandicaps;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(
				R.layout.current_game_modify_result_fragment, null);

		holeNumberTextView = (TextView) view
				.findViewById(R.id.HoleNumberTextView);

		parThreeRadioButton = (RadioButton) view
				.findViewById(R.id.ParThreeRadioButton);
		parFourRadioButton = (RadioButton) view
				.findViewById(R.id.ParFourRadioButton);
		parFiveRadioButton = (RadioButton) view
				.findViewById(R.id.ParFiveRadioButton);
		parSixRadioButton = (RadioButton) view
				.findViewById(R.id.ParSixRadioButton);
		parThreeRadioButton.setChecked(false);
		parFourRadioButton.setChecked(true);
		parFiveRadioButton.setChecked(false);
		parSixRadioButton.setChecked(false);

		Spinner player1ScoreSpinner = (Spinner) view
				.findViewById(R.id.Player1ScoreSpinner);
		ScoreSpinnerAdapter player1ScoreSpinnerAdapter = new ScoreSpinnerAdapter(
				view.getContext(),
				android.R.layout.simple_spinner_dropdown_item);
		player1ScoreSpinner.setAdapter(player1ScoreSpinnerAdapter);

		Spinner player2ScoreSpinner = (Spinner) view
				.findViewById(R.id.Player2ScoreSpinner);
		ScoreSpinnerAdapter player2ScoreSpinnerAdapter = new ScoreSpinnerAdapter(
				view.getContext(),
				android.R.layout.simple_spinner_dropdown_item);
		player2ScoreSpinner.setAdapter(player2ScoreSpinnerAdapter);

		Spinner player3ScoreSpinner = (Spinner) view
				.findViewById(R.id.Player3ScoreSpinner);
		ScoreSpinnerAdapter player3ScoreSpinnerAdapter = new ScoreSpinnerAdapter(
				view.getContext(),
				android.R.layout.simple_spinner_dropdown_item);
		player3ScoreSpinner.setAdapter(player3ScoreSpinnerAdapter);

		Spinner player4ScoreSpinner = (Spinner) view
				.findViewById(R.id.Player4ScoreSpinner);
		ScoreSpinnerAdapter player4ScoreSpinnerAdapter = new ScoreSpinnerAdapter(
				view.getContext(),
				android.R.layout.simple_spinner_dropdown_item);
		player4ScoreSpinner.setAdapter(player4ScoreSpinnerAdapter);

		Spinner player5ScoreSpinner = (Spinner) view
				.findViewById(R.id.Player5ScoreSpinner);
		ScoreSpinnerAdapter player5ScoreSpinnerAdapter = new ScoreSpinnerAdapter(
				view.getContext(),
				android.R.layout.simple_spinner_dropdown_item);
		player5ScoreSpinner.setAdapter(player5ScoreSpinnerAdapter);

		Spinner player6ScoreSpinner = (Spinner) view
				.findViewById(R.id.Player6ScoreSpinner);
		ScoreSpinnerAdapter player6ScoreSpinnerAdapter = new ScoreSpinnerAdapter(
				view.getContext(),
				android.R.layout.simple_spinner_dropdown_item);
		player6ScoreSpinner.setAdapter(player6ScoreSpinnerAdapter);

		playerScoreSpinnerAdapters = new ScoreSpinnerAdapter[Constants.MAX_PLAYER_COUNT];
		playerScoreSpinnerAdapters[0] = player1ScoreSpinnerAdapter;
		playerScoreSpinnerAdapters[1] = player2ScoreSpinnerAdapter;
		playerScoreSpinnerAdapters[2] = player3ScoreSpinnerAdapter;
		playerScoreSpinnerAdapters[3] = player4ScoreSpinnerAdapter;
		playerScoreSpinnerAdapters[4] = player5ScoreSpinnerAdapter;
		playerScoreSpinnerAdapters[5] = player6ScoreSpinnerAdapter;

		playerScoreSpinners = new Spinner[Constants.MAX_PLAYER_COUNT];
		playerScoreSpinners[0] = player1ScoreSpinner;
		playerScoreSpinners[1] = player2ScoreSpinner;
		playerScoreSpinners[2] = player3ScoreSpinner;
		playerScoreSpinners[3] = player4ScoreSpinner;
		playerScoreSpinners[4] = player5ScoreSpinner;
		playerScoreSpinners[5] = player6ScoreSpinner;

		Spinner player1HandicapSpinner = (Spinner) view
				.findViewById(R.id.Player1HandicapSpinner);
		HandicapSpinnerAdapter player1HandicapSpinnerAdapter = new HandicapSpinnerAdapter(
				view.getContext(),
				android.R.layout.simple_spinner_dropdown_item);
		player1HandicapSpinner.setAdapter(player1HandicapSpinnerAdapter);

		Spinner player2HandicapSpinner = (Spinner) view
				.findViewById(R.id.Player2HandicapSpinner);
		HandicapSpinnerAdapter player2HandicapSpinnerAdapter = new HandicapSpinnerAdapter(
				view.getContext(),
				android.R.layout.simple_spinner_dropdown_item);
		player2HandicapSpinner.setAdapter(player2HandicapSpinnerAdapter);

		Spinner player3HandicapSpinner = (Spinner) view
				.findViewById(R.id.Player3HandicapSpinner);
		HandicapSpinnerAdapter player3HandicapSpinnerAdapter = new HandicapSpinnerAdapter(
				view.getContext(),
				android.R.layout.simple_spinner_dropdown_item);
		player3HandicapSpinner.setAdapter(player3HandicapSpinnerAdapter);

		Spinner player4HandicapSpinner = (Spinner) view
				.findViewById(R.id.Player4HandicapSpinner);
		HandicapSpinnerAdapter player4HandicapSpinnerAdapter = new HandicapSpinnerAdapter(
				view.getContext(),
				android.R.layout.simple_spinner_dropdown_item);
		player4HandicapSpinner.setAdapter(player4HandicapSpinnerAdapter);

		Spinner player5HandicapSpinner = (Spinner) view
				.findViewById(R.id.Player5HandicapSpinner);
		HandicapSpinnerAdapter player5HandicapSpinnerAdapter = new HandicapSpinnerAdapter(
				view.getContext(),
				android.R.layout.simple_spinner_dropdown_item);
		player5HandicapSpinner.setAdapter(player5HandicapSpinnerAdapter);

		Spinner player6HandicapSpinner = (Spinner) view
				.findViewById(R.id.Player6HandicapSpinner);
		HandicapSpinnerAdapter player6HandicapSpinnerAdapter = new HandicapSpinnerAdapter(
				view.getContext(),
				android.R.layout.simple_spinner_dropdown_item);
		player6HandicapSpinner.setAdapter(player6HandicapSpinnerAdapter);

		playerHandicapSpinnerAdapters = new HandicapSpinnerAdapter[Constants.MAX_PLAYER_COUNT];
		playerHandicapSpinnerAdapters[0] = player1HandicapSpinnerAdapter;
		playerHandicapSpinnerAdapters[1] = player2HandicapSpinnerAdapter;
		playerHandicapSpinnerAdapters[2] = player3HandicapSpinnerAdapter;
		playerHandicapSpinnerAdapters[3] = player4HandicapSpinnerAdapter;
		playerHandicapSpinnerAdapters[4] = player5HandicapSpinnerAdapter;
		playerHandicapSpinnerAdapters[5] = player6HandicapSpinnerAdapter;

		playerHandicapSpinners = new Spinner[Constants.MAX_PLAYER_COUNT];
		playerHandicapSpinners[0] = player1HandicapSpinner;
		playerHandicapSpinners[1] = player2HandicapSpinner;
		playerHandicapSpinners[2] = player3HandicapSpinner;
		playerHandicapSpinners[3] = player4HandicapSpinner;
		playerHandicapSpinners[4] = player5HandicapSpinner;
		playerHandicapSpinners[5] = player6HandicapSpinner;

		TextView player1NameTextView = (TextView) view
				.findViewById(R.id.Player1NameTextView);
		TextView player2NameTextView = (TextView) view
				.findViewById(R.id.Player2NameTextView);
		TextView player3NameTextView = (TextView) view
				.findViewById(R.id.Player3NameTextView);
		TextView player4NameTextView = (TextView) view
				.findViewById(R.id.Player4NameTextView);
		TextView player5NameTextView = (TextView) view
				.findViewById(R.id.Player5NameTextView);
		TextView player6NameTextView = (TextView) view
				.findViewById(R.id.Player6NameTextView);

		playerNameTextViews = new TextView[Constants.MAX_PLAYER_COUNT];
		playerNameTextViews[0] = player1NameTextView;
		playerNameTextViews[1] = player2NameTextView;
		playerNameTextViews[2] = player3NameTextView;
		playerNameTextViews[3] = player4NameTextView;
		playerNameTextViews[4] = player5NameTextView;
		playerNameTextViews[5] = player6NameTextView;

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		GameSettingDatabaseWorker gameSettingWorker = new GameSettingDatabaseWorker(
				getActivity());
		GameSetting gameSetting = new GameSetting();
		gameSettingWorker.getGameSetting(gameSetting);

		playerCount = gameSetting.getPlayerCount();

		for (int playerId = 0; playerId < Constants.MAX_PLAYER_COUNT; playerId++) {
			if (playerId < playerCount) {
				playerNameTextViews[playerId].setVisibility(View.VISIBLE);
				playerScoreSpinners[playerId].setVisibility(View.VISIBLE);
				playerHandicapSpinners[playerId].setVisibility(View.VISIBLE);
			} else {
				playerNameTextViews[playerId].setVisibility(View.GONE);
				playerScoreSpinners[playerId].setVisibility(View.GONE);
				playerHandicapSpinners[playerId].setVisibility(View.GONE);
			}
		}

		parThreeRadioButton.setOnClickListener(this);
		parFourRadioButton.setOnClickListener(this);
		parFiveRadioButton.setOnClickListener(this);
		parSixRadioButton.setOnClickListener(this);

		loadData();
	}

	private void loadData() {

		PlayerSettingDatabaseWorker playerSettingWorker = new PlayerSettingDatabaseWorker(
				getActivity());
		PlayerSetting playerSetting = new PlayerSetting();
		playerSettingWorker.getPlayerSetting(playerSetting);

		Intent intent = getActivity().getIntent();
		holeNumber = intent.getIntExtra(
				CurrentGameModifyResultActivity.IK_HOLE_NUMBER, 1);

		ResultDatabaseWorker resultWorker = new ResultDatabaseWorker(
				getActivity());
		usedHandicaps = resultWorker.getUsedHandicaps(holeNumber);
		oldResult = resultWorker.getResult(holeNumber);

		for (int playerId = 0; playerId < Constants.MAX_PLAYER_COUNT; playerId++) {
			playerNameTextViews[playerId].setText(playerSetting
					.getPlayerName(playerId));

			fillHandicapSpinner(playerSetting, playerId);
		}

		holeNumberTextView
				.setText(getString(
						R.string.fragment_modify_result_hole_number_format,
						holeNumber));

		switch (oldResult.getParNumber()) {
		case 3:
			parThreeRadioButton.setChecked(true);
			parFourRadioButton.setChecked(false);
			parFiveRadioButton.setChecked(false);
			parSixRadioButton.setChecked(false);
			break;
		case 4:
			parThreeRadioButton.setChecked(false);
			parFourRadioButton.setChecked(true);
			parFiveRadioButton.setChecked(false);
			parSixRadioButton.setChecked(false);
			break;
		case 5:
			parThreeRadioButton.setChecked(false);
			parFourRadioButton.setChecked(false);
			parFiveRadioButton.setChecked(true);
			parSixRadioButton.setChecked(false);
			break;
		case 6:
			parThreeRadioButton.setChecked(false);
			parFourRadioButton.setChecked(false);
			parFiveRadioButton.setChecked(false);
			parSixRadioButton.setChecked(true);
			break;
		}

		for (int playerId = 0; playerId < Constants.MAX_PLAYER_COUNT; playerId++) {
			fillScoreSpinner(playerScoreSpinners[playerId],
					playerScoreSpinnerAdapters[playerId]);
		}

		for (int playerId = 0; playerId < Constants.MAX_PLAYER_COUNT; playerId++) {
			int score = oldResult.getOriginalScore(playerId);
			int handicap = oldResult.getUsedHandicap(playerId);

			Spinner scoreSpinner = playerScoreSpinners[playerId];
			ScoreSpinnerAdapter scoreSpinnerAdapter = playerScoreSpinnerAdapters[playerId];

			int scoreSpinnerSize = scoreSpinnerAdapter.getCount();
			for (int poosition = 0; poosition < scoreSpinnerSize; poosition++) {
				if (scoreSpinnerAdapter.getItem(poosition).score == score) {
					scoreSpinner.setSelection(poosition);
					break;
				}
			}

			Spinner handicapSpinner = playerHandicapSpinners[playerId];
			HandicapSpinnerAdapter handicapSpinnerAdapter = playerHandicapSpinnerAdapters[playerId];

			int handicapSpinnerSize = handicapSpinnerAdapter.getCount();
			for (int poosition = 0; poosition < handicapSpinnerSize; poosition++) {
				if (handicapSpinnerAdapter.getItem(poosition).handicap == handicap) {
					handicapSpinner.setSelection(poosition);
					break;
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {
		case R.id.ParThreeRadioButton:
		case R.id.ParFourRadioButton:
		case R.id.ParFiveRadioButton:
		case R.id.ParSixRadioButton:
			for (int playerId = 0; playerId < Constants.MAX_PLAYER_COUNT; playerId++) {
				fillScoreSpinner(playerScoreSpinners[playerId],
						playerScoreSpinnerAdapters[playerId]);
			}
			break;
		}
	}

	private void fillScoreSpinner(Spinner spinner, ScoreSpinnerAdapter adapter) {
		adapter.clear();
		if (parThreeRadioButton.isChecked()) {
			adapter.add(new ScoreSpinnerItem(-2,
					getString(R.string.score_format_par_3_minus_2)));
			adapter.add(new ScoreSpinnerItem(-1,
					getString(R.string.score_format_par_3_minus_1)));
			adapter.add(new ScoreSpinnerItem(0,
					getString(R.string.score_format_par_3_even)));
			adapter.add(new ScoreSpinnerItem(1,
					getString(R.string.score_format_par_3_plus_1)));
			adapter.add(new ScoreSpinnerItem(2,
					getString(R.string.score_format_par_3_plus_2)));
			adapter.add(new ScoreSpinnerItem(3,
					getString(R.string.score_format_par_3_plus_3)));
		} else if (parFiveRadioButton.isChecked()) {
			adapter.add(new ScoreSpinnerItem(-4,
					getString(R.string.score_format_par_5_minus_4)));
			adapter.add(new ScoreSpinnerItem(-3,
					getString(R.string.score_format_par_5_minus_3)));
			adapter.add(new ScoreSpinnerItem(-2,
					getString(R.string.score_format_par_5_minus_2)));
			adapter.add(new ScoreSpinnerItem(-1,
					getString(R.string.score_format_par_5_minus_1)));
			adapter.add(new ScoreSpinnerItem(0,
					getString(R.string.score_format_par_5_even)));
			adapter.add(new ScoreSpinnerItem(1,
					getString(R.string.score_format_par_5_plus_1)));
			adapter.add(new ScoreSpinnerItem(2,
					getString(R.string.score_format_par_5_plus_2)));
			adapter.add(new ScoreSpinnerItem(3,
					getString(R.string.score_format_par_5_plus_3)));
			adapter.add(new ScoreSpinnerItem(4,
					getString(R.string.score_format_par_5_plus_4)));
			adapter.add(new ScoreSpinnerItem(5,
					getString(R.string.score_format_par_5_plus_5)));
		} else if (parSixRadioButton.isChecked()) {
			adapter.add(new ScoreSpinnerItem(-5,
					getString(R.string.score_format_par_6_minus_5)));
			adapter.add(new ScoreSpinnerItem(-4,
					getString(R.string.score_format_par_6_minus_4)));
			adapter.add(new ScoreSpinnerItem(-3,
					getString(R.string.score_format_par_6_minus_3)));
			adapter.add(new ScoreSpinnerItem(-2,
					getString(R.string.score_format_par_6_minus_2)));
			adapter.add(new ScoreSpinnerItem(-1,
					getString(R.string.score_format_par_6_minus_1)));
			adapter.add(new ScoreSpinnerItem(0,
					getString(R.string.score_format_par_6_even)));
			adapter.add(new ScoreSpinnerItem(1,
					getString(R.string.score_format_par_6_plus_1)));
			adapter.add(new ScoreSpinnerItem(2,
					getString(R.string.score_format_par_6_plus_2)));
			adapter.add(new ScoreSpinnerItem(3,
					getString(R.string.score_format_par_6_plus_3)));
			adapter.add(new ScoreSpinnerItem(4,
					getString(R.string.score_format_par_6_plus_4)));
			adapter.add(new ScoreSpinnerItem(5,
					getString(R.string.score_format_par_6_plus_5)));
			adapter.add(new ScoreSpinnerItem(6,
					getString(R.string.score_format_par_6_plus_6)));
		} else {
			adapter.add(new ScoreSpinnerItem(-3,
					getString(R.string.score_format_par_4_minus_3)));
			adapter.add(new ScoreSpinnerItem(-2,
					getString(R.string.score_format_par_4_minus_2)));
			adapter.add(new ScoreSpinnerItem(-1,
					getString(R.string.score_format_par_4_minus_1)));
			adapter.add(new ScoreSpinnerItem(0,
					getString(R.string.score_format_par_4_even)));
			adapter.add(new ScoreSpinnerItem(1,
					getString(R.string.score_format_par_4_plus_1)));
			adapter.add(new ScoreSpinnerItem(2,
					getString(R.string.score_format_par_4_plus_2)));
			adapter.add(new ScoreSpinnerItem(3,
					getString(R.string.score_format_par_4_plus_3)));
			adapter.add(new ScoreSpinnerItem(4,
					getString(R.string.score_format_par_4_plus_4)));
		}
		adapter.notifyDataSetChanged();

		if (parThreeRadioButton.isChecked()) {
			spinner.setSelection(2);
		} else if (parFiveRadioButton.isChecked()) {
			spinner.setSelection(4);
		} else if (parSixRadioButton.isChecked()) {
			spinner.setSelection(5);
		} else {
			spinner.setSelection(3);
		}
	}

	private void fillHandicapSpinner(PlayerSetting playerSetting, int playerId) {
		int remainedHandicap = playerSetting.getHandicap(playerId)
				- usedHandicaps.getUsedHandicap(playerId);

		HandicapSpinnerAdapter adapter = playerHandicapSpinnerAdapters[playerId];
		adapter.clear();
		if (remainedHandicap > 0) {
			for (int i = 0; i <= remainedHandicap; i++) {
				adapter.add(new HandicapSpinnerItem(i));
			}
			playerHandicapSpinners[playerId].setEnabled(true);
		} else {
			playerHandicapSpinners[playerId].setEnabled(false);
		}
		adapter.notifyDataSetChanged();
	}

	private static class ScoreSpinnerAdapter extends
			ArrayAdapter<ScoreSpinnerItem> {

		public ScoreSpinnerAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}
	}

	private static class ScoreSpinnerItem {
		private int score;
		private String title;

		public ScoreSpinnerItem(int score, String title) {
			this.score = score;
			this.title = title;
		}

		@Override
		public String toString() {
			return title;
		}
	}

	private static class HandicapSpinnerAdapter extends
			ArrayAdapter<HandicapSpinnerItem> {

		public HandicapSpinnerAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}
	}

	private static class HandicapSpinnerItem {
		private int handicap;
		private String title;

		public HandicapSpinnerItem(int handicap) {
			this.handicap = handicap;
			this.title = (handicap == 0) ? "0" : "-" + handicap;
		}

		@Override
		public String toString() {
			return title;
		}
	}

	public int getHoleNumber() {
		return holeNumber;
	}

	public int getParNumber() {
		if (parThreeRadioButton.isChecked())
			return 3;
		else if (parFourRadioButton.isChecked())
			return 4;
		else if (parFiveRadioButton.isChecked())
			return 5;
		else if (parSixRadioButton.isChecked())
			return 6;
		return 4;
	}

	public int getScore(int playerId) {
		if (playerId < 0 || playerId > playerCount - 1)
			return 100;

		ScoreSpinnerItem scoreItem = null;

		scoreItem = (ScoreSpinnerItem) playerScoreSpinners[playerId]
				.getSelectedItem();
		if (scoreItem == null)
			return 0;
		return scoreItem.score;
	}

	public int getHandicap(int playerId) {
		if (playerId < 0 || playerId > playerCount - 1)
			return 0;

		HandicapSpinnerItem handicapItem = null;

		handicapItem = (HandicapSpinnerItem) playerHandicapSpinners[playerId]
				.getSelectedItem();
		if (handicapItem == null)
			return 0;
		return handicapItem.handicap;
	}
}
