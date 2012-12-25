package org.dolicoli.android.golfscoreboard.fragments.currentgame;

import java.text.DecimalFormat;

import org.dolicoli.android.golfscoreboard.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class NewGameSettingFragment extends Fragment implements
		OnItemSelectedListener, OnClickListener {

	private static final int DEFAULT_GAME_FEE_PER_PERSON_NINE_HOLE = 15000;
	private static final int DEFAULT_GAME_FEE_PER_PERSON_EIGHTEEN_HOLE = 22000;

	private RadioButton nineHoleRadioButton, eighteenHoleRadioButton;
	private Spinner playerCountSpinner;

	private EditText gameFeeEditText, extraFeeEditText, rankingFeeEditText;
	private TextView totalFeeTextView, totalHoleFeeTextView,
			totalRankingFeeTextView;

	private DecimalFormat feeFormat;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(
				R.layout.current_game_new_game_setting_fragment, null);

		feeFormat = new DecimalFormat(getString(R.string.fee_format));

		nineHoleRadioButton = (RadioButton) view
				.findViewById(R.id.NineHoleRadioButton);
		eighteenHoleRadioButton = (RadioButton) view
				.findViewById(R.id.EighteenHoleRadioButton);
		nineHoleRadioButton.setChecked(false);
		eighteenHoleRadioButton.setChecked(true);

		playerCountSpinner = (Spinner) view
				.findViewById(R.id.PlayerCountSpinner);
		SpinnerAdapter playerCountAdapter = new ArrayAdapter<String>(
				view.getContext(),
				android.R.layout.simple_spinner_dropdown_item,
				new String[] {
						getString(R.string.fragment_game_setting_player_count_spinner_2),
						getString(R.string.fragment_game_setting_player_count_spinner_3),
						getString(R.string.fragment_game_setting_player_count_spinner_4),
						getString(R.string.fragment_game_setting_player_count_spinner_5),
						getString(R.string.fragment_game_setting_player_count_spinner_6) });
		playerCountSpinner.setAdapter(playerCountAdapter);

		gameFeeEditText = (EditText) view.findViewById(R.id.GameFeeEditText);
		extraFeeEditText = (EditText) view.findViewById(R.id.ExtraFeeEditText);
		rankingFeeEditText = (EditText) view
				.findViewById(R.id.RankingFeeEditText);

		totalFeeTextView = (TextView) view.findViewById(R.id.TotalFeeTextView);
		totalHoleFeeTextView = (TextView) view
				.findViewById(R.id.TotalHoleFeeTextView);
		totalRankingFeeTextView = (TextView) view
				.findViewById(R.id.TotalRankingFeeTextView);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		nineHoleRadioButton.setChecked(false);
		eighteenHoleRadioButton.setChecked(true);
		playerCountSpinner.setSelection(1);

		gameFeeEditText.setText(String
				.valueOf(DEFAULT_GAME_FEE_PER_PERSON_EIGHTEEN_HOLE * 3));
		extraFeeEditText.setText(String.valueOf(0));
		rankingFeeEditText.setText(String.valueOf(0));

		TextWatcher feeTextWatcher = new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				feeChanged();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int count,
					int after) {
			}
		};
		gameFeeEditText.addTextChangedListener(feeTextWatcher);
		extraFeeEditText.addTextChangedListener(feeTextWatcher);
		rankingFeeEditText.addTextChangedListener(feeTextWatcher);

		nineHoleRadioButton.setOnClickListener(this);
		eighteenHoleRadioButton.setOnClickListener(this);

		playerCountSpinner.setOnItemSelectedListener(this);
		playerCountChanged(3);

		feeChanged();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		playerCountChanged(position + 2);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {
		case R.id.NineHoleRadioButton:
		case R.id.EighteenHoleRadioButton:
			int position = playerCountSpinner.getSelectedItemPosition();
			playerCountChanged(position + 2);
			break;
		}
	}

	private void feeChanged() {
		int totalFee = getGameFee() + getExtraFee();
		int totalRankingFee = getRankingFee();
		totalFeeTextView.setText(feeFormat.format(totalFee));
		totalRankingFeeTextView.setText(feeFormat.format(totalRankingFee));
		totalHoleFeeTextView.setText(feeFormat.format(totalFee
				- totalRankingFee));
	}

	private void playerCountChanged(int playerCount) {
		if (eighteenHoleRadioButton.isChecked()) {
			gameFeeEditText.setText(String
					.valueOf(DEFAULT_GAME_FEE_PER_PERSON_EIGHTEEN_HOLE
							* playerCount));
		} else {
			gameFeeEditText.setText(String
					.valueOf(DEFAULT_GAME_FEE_PER_PERSON_NINE_HOLE
							* playerCount));
		}

		switch (playerCount) {
		case 2:
			// 0, 6000
			rankingFeeEditText.setText(String.valueOf(6000));
			break;
		case 3:
			// 0, 6000, 9000
			rankingFeeEditText.setText(String.valueOf(6000 + 9000));
			break;
		case 4:
			// 0, 6000, 8000, 10000
			rankingFeeEditText.setText(String.valueOf(6000 + 8000 + 10000));
			break;
		case 5:
			// 0, 6000, 8000, 10000, 12000
			rankingFeeEditText.setText(String
					.valueOf(6000 + 8000 + 10000 + 12000));
			break;
		case 6:
			// 0, 6000, 8000, 10000, 12000, 14000
			rankingFeeEditText.setText(String.valueOf(6000 + 8000 + 10000
					+ 12000 + 14000));
			break;
		}
	}

	public int getHoleCount() {
		return nineHoleRadioButton.isChecked() ? 9 : 18;
	}

	public int getPlayerCount() {
		return playerCountSpinner.getSelectedItemPosition() + 2;
	}

	public int getGameFee() {
		try {
			return Integer.parseInt(gameFeeEditText.getText().toString());
		} catch (Throwable t) {
			return 0;
		}
	}

	public int getExtraFee() {
		try {
			return Integer.parseInt(extraFeeEditText.getText().toString());
		} catch (Throwable t) {
			return 0;
		}
	}

	public int getRankingFee() {
		try {
			return Integer.parseInt(rankingFeeEditText.getText().toString());
		} catch (Throwable t) {
			return 0;
		}
	}

	public int getHoleFee() {
		return getGameFee() + getExtraFee() - getRankingFee();
	}
}
