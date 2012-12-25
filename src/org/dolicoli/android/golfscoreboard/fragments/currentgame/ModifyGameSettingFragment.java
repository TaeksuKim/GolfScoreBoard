package org.dolicoli.android.golfscoreboard.fragments.currentgame;

import java.text.DecimalFormat;

import org.dolicoli.android.golfscoreboard.R;
import org.dolicoli.android.golfscoreboard.data.settings.GameSetting;
import org.dolicoli.android.golfscoreboard.db.GameSettingDatabaseWorker;
import org.dolicoli.android.golfscoreboard.db.ResultDatabaseWorker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class ModifyGameSettingFragment extends Fragment implements
		OnClickListener {

	private RadioButton nineHoleRadioButton, eighteenHoleRadioButton;
	private TextView playerCountTextView;

	private EditText gameFeeEditText, extraFeeEditText, rankingFeeEditText;
	private TextView totalFeeTextView, totalHoleFeeTextView,
			totalRankingFeeTextView;

	private DecimalFormat feeFormat;
	private int playerCount;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(
				R.layout.current_game_modify_game_setting_fragment, null);

		feeFormat = new DecimalFormat(getString(R.string.fee_format));

		nineHoleRadioButton = (RadioButton) view
				.findViewById(R.id.NineHoleRadioButton);
		eighteenHoleRadioButton = (RadioButton) view
				.findViewById(R.id.EighteenHoleRadioButton);
		nineHoleRadioButton.setChecked(true);
		eighteenHoleRadioButton.setChecked(false);

		playerCountTextView = (TextView) view
				.findViewById(R.id.PlayerCountTextView);

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

		ResultDatabaseWorker resultWorker = new ResultDatabaseWorker(
				getActivity());
		int maxHoleNumber = resultWorker.getMaxHoleNumber();

		GameSettingDatabaseWorker gameSettingWorker = new GameSettingDatabaseWorker(
				getActivity());
		GameSetting gameSetting = new GameSetting();
		gameSettingWorker.getGameSetting(gameSetting);
		if (gameSetting.getHoleCount() == 18) {
			nineHoleRadioButton.setChecked(false);
			eighteenHoleRadioButton.setChecked(true);

			nineHoleRadioButton.setEnabled(maxHoleNumber <= 9);
			eighteenHoleRadioButton.setEnabled(maxHoleNumber <= 9);
		} else {
			nineHoleRadioButton.setChecked(true);
			eighteenHoleRadioButton.setChecked(false);
		}
		playerCount = gameSetting.getPlayerCount();
		playerCountTextView.setText(playerCount + " Έν");

		gameFeeEditText.setText(String.valueOf(gameSetting.getGameFee()));
		extraFeeEditText.setText(String.valueOf(gameSetting.getExtraFee()));
		rankingFeeEditText.setText(String.valueOf(gameSetting.getRankingFee()));

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

		feeChanged();
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {
		case R.id.NineHoleRadioButton:
		case R.id.EighteenHoleRadioButton:
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

	public int getHoleCount() {
		return nineHoleRadioButton.isChecked() ? 9 : 18;
	}

	public int getPlayerCount() {
		return playerCount;
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
