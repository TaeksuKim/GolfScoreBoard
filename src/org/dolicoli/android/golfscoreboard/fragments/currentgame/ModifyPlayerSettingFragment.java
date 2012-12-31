package org.dolicoli.android.golfscoreboard.fragments.currentgame;

import java.util.ArrayList;

import org.dolicoli.android.golfscoreboard.Constants;
import org.dolicoli.android.golfscoreboard.CurrentGameModifyGameSettingActivity;
import org.dolicoli.android.golfscoreboard.R;
import org.dolicoli.android.golfscoreboard.data.PlayerCache;
import org.dolicoli.android.golfscoreboard.data.settings.GameSetting;
import org.dolicoli.android.golfscoreboard.data.settings.PlayerSetting;
import org.dolicoli.android.golfscoreboard.db.GameSettingDatabaseWorker;
import org.dolicoli.android.golfscoreboard.db.PlayerCacheDatabaseWorker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class ModifyPlayerSettingFragment extends Fragment implements
		OnClickListener, OnItemSelectedListener {

	private Spinner[] playerNameSpinners;
	private PlayerNameSpinnerAdapter[] playerNameSpinnerAdapters;
	private Button[] newNameButtons;

	private int playerCount;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(
				R.layout.current_game_modify_player_setting_fragment, null);

		Spinner player1NameSpinner = (Spinner) view
				.findViewById(R.id.Player1NameSpinner);
		Spinner player2NameSpinner = (Spinner) view
				.findViewById(R.id.Player2NameSpinner);
		Spinner player3NameSpinner = (Spinner) view
				.findViewById(R.id.Player3NameSpinner);
		Spinner player4NameSpinner = (Spinner) view
				.findViewById(R.id.Player4NameSpinner);
		Spinner player5NameSpinner = (Spinner) view
				.findViewById(R.id.Player5NameSpinner);
		Spinner player6NameSpinner = (Spinner) view
				.findViewById(R.id.Player6NameSpinner);

		playerNameSpinnerAdapters = new PlayerNameSpinnerAdapter[Constants.MAX_PLAYER_COUNT];
		playerNameSpinnerAdapters[0] = new PlayerNameSpinnerAdapter(
				getActivity());
		playerNameSpinnerAdapters[1] = new PlayerNameSpinnerAdapter(
				getActivity());
		playerNameSpinnerAdapters[2] = new PlayerNameSpinnerAdapter(
				getActivity());
		playerNameSpinnerAdapters[3] = new PlayerNameSpinnerAdapter(
				getActivity());
		playerNameSpinnerAdapters[4] = new PlayerNameSpinnerAdapter(
				getActivity());
		playerNameSpinnerAdapters[5] = new PlayerNameSpinnerAdapter(
				getActivity());

		playerNameSpinners = new Spinner[Constants.MAX_PLAYER_COUNT];
		playerNameSpinners[0] = player1NameSpinner;
		playerNameSpinners[1] = player2NameSpinner;
		playerNameSpinners[2] = player3NameSpinner;
		playerNameSpinners[3] = player4NameSpinner;
		playerNameSpinners[4] = player5NameSpinner;
		playerNameSpinners[5] = player6NameSpinner;

		Button player1NewNameButton = (Button) view
				.findViewById(R.id.Player1NewNameButton);
		Button player2NewNameButton = (Button) view
				.findViewById(R.id.Player2NewNameButton);
		Button player3NewNameButton = (Button) view
				.findViewById(R.id.Player3NewNameButton);
		Button player4NewNameButton = (Button) view
				.findViewById(R.id.Player4NewNameButton);
		Button player5NewNameButton = (Button) view
				.findViewById(R.id.Player5NewNameButton);
		Button player6NewNameButton = (Button) view
				.findViewById(R.id.Player6NewNameButton);

		newNameButtons = new Button[Constants.MAX_PLAYER_COUNT];
		newNameButtons[0] = player1NewNameButton;
		newNameButtons[1] = player2NewNameButton;
		newNameButtons[2] = player3NewNameButton;
		newNameButtons[3] = player4NewNameButton;
		newNameButtons[4] = player5NewNameButton;
		newNameButtons[5] = player6NewNameButton;

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		GameSetting gameSetting = new GameSetting();
		PlayerSetting playerSetting = new PlayerSetting();

		GameSettingDatabaseWorker gameSettingWorker = new GameSettingDatabaseWorker(
				getActivity());
		gameSettingWorker.getGameSetting(gameSetting, playerSetting);
		playerCount = gameSetting.getPlayerCount();

		for (int i = 0; i < Constants.MAX_PLAYER_COUNT; i++) {
			playerNameSpinners[i].setAdapter(playerNameSpinnerAdapters[i]);
		}

		for (int i = 0; i < Constants.MAX_PLAYER_COUNT; i++) {
			playerNameSpinners[i].setOnItemSelectedListener(this);
			newNameButtons[i].setOnClickListener(this);
		}

		for (int i = 0; i < playerCount; i++) {
			String playerName = playerSetting.getPlayerName(i);
			Spinner spinner = playerNameSpinners[i];
			int index = findNameSpinnerIndex(spinner, playerName);
			if (index <= 0) {
				addNewPlayerName(spinner, playerName);
			}
		}

		reloadPlayerNameCache();

		for (int i = 0; i < playerCount; i++) {
			String playerName = playerSetting.getPlayerName(i);
			Spinner spinner = playerNameSpinners[i];
			int index = findNameSpinnerIndex(spinner, playerName);
			spinner.setSelection(index);
		}

		for (int i = 0; i < Constants.MAX_PLAYER_COUNT; i++) {
			if (i < playerCount) {
				playerNameSpinners[i].setVisibility(View.VISIBLE);
				newNameButtons[i].setVisibility(View.VISIBLE);
			} else {
				playerNameSpinners[i].setVisibility(View.GONE);
				newNameButtons[i].setVisibility(View.GONE);
			}
		}

		((InputFragmentListener) getActivity()).inputDataChanged();
	}

	private int findNameSpinnerIndex(Spinner spinner, String playerName) {
		int count = spinner.getCount();
		for (int i = 0; i < count; i++) {
			if (((String) spinner.getItemAtPosition(i))
					.equalsIgnoreCase(playerName)) {
				return i;
			}
		}
		return 0;
	}

	public String getPlayerName(int playerId) {
		if (playerId < Constants.MIN_PLAYER_ID
				|| playerId > Constants.MAX_PLAYER_ID) {
			return null;
		}
		return (String) playerNameSpinners[playerId].getSelectedItem();
	}

	public boolean isAllFieldValid() {
		for (int i = 0; i < playerCount; i++) {
			if (playerNameSpinners[i].getSelectedItemPosition() <= 0)
				return false;
		}
		return true;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		inputDataChanged();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {
		case R.id.Player1NewNameButton:
			showInputPlayerNameDialog(playerNameSpinners[0]);
			break;
		case R.id.Player2NewNameButton:
			showInputPlayerNameDialog(playerNameSpinners[1]);
			break;
		case R.id.Player3NewNameButton:
			showInputPlayerNameDialog(playerNameSpinners[2]);
			break;
		case R.id.Player4NewNameButton:
			showInputPlayerNameDialog(playerNameSpinners[3]);
			break;
		case R.id.Player5NewNameButton:
			showInputPlayerNameDialog(playerNameSpinners[4]);
			break;
		case R.id.Player6NewNameButton:
			showInputPlayerNameDialog(playerNameSpinners[5]);
			break;
		}
		inputDataChanged();
	}

	private void showInputPlayerNameDialog(final Spinner spinner) {
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

		alert.setTitle(R.string.dialog_add_name_title);
		alert.setMessage(R.string.dialog_please_insert_name);

		final EditText input = new EditText(getActivity());
		alert.setView(input);

		alert.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String name = input.getText().toString();
						addNewPlayerName(spinner, name);
					}
				});

		alert.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				});

		alert.show();
	}

	private void addNewPlayerName(Spinner spinner, String name) {
		PlayerCacheDatabaseWorker worker = new PlayerCacheDatabaseWorker(
				getActivity());
		worker.putPlayer(name);
		reloadPlayerNameCache();

		int count = spinner.getCount();
		for (int position = 0; position < count; position++) {
			String compare = (String) spinner.getItemAtPosition(position);
			if (name.equalsIgnoreCase(compare)) {
				spinner.setSelection(position);
				break;
			}
		}

		inputDataChanged();
	}

	private void reloadPlayerNameCache() {
		PlayerCacheDatabaseWorker worker = new PlayerCacheDatabaseWorker(
				getActivity());
		PlayerCache cache = worker.getCache();

		String[] cachedNames = cache.getNames();
		final String[] defaultCachedPlayerNames = getResources()
				.getStringArray(R.array.DefaultCachedPlayerNames);
		ArrayList<String> names = new ArrayList<String>();

		names.add(getString(R.string.fragment_game_setting_select_player_name));
		for (String name : defaultCachedPlayerNames) {
			if (names.contains(name))
				continue;
			names.add(name);
		}

		for (String name : cachedNames) {
			if (names.contains(name))
				continue;
			names.add(name);
		}

		for (int i = 0; i < Constants.MAX_PLAYER_COUNT; i++) {
			playerNameSpinnerAdapters[i].clear();
			playerNameSpinnerAdapters[i].addAll(names);
			playerNameSpinnerAdapters[i].notifyDataSetChanged();
		}
	}

	private void inputDataChanged() {
		CurrentGameModifyGameSettingActivity activity = ((CurrentGameModifyGameSettingActivity) getActivity());
		if (activity == null)
			return;

		activity.inputDataChanged();
		activity.setNextButtonEnable(isAllFieldValid());
	}

	private static class PlayerNameSpinnerAdapter extends ArrayAdapter<String> {

		public PlayerNameSpinnerAdapter(Context context) {
			super(context, android.R.layout.simple_list_item_1);
		}

	}
}
