package org.dolicoli.android.golfscoreboard.fragments.currentgame;

import java.text.DecimalFormat;

import org.dolicoli.android.golfscoreboard.Constants;
import org.dolicoli.android.golfscoreboard.CurrentGameNewGameSettingActivity;
import org.dolicoli.android.golfscoreboard.R;
import org.dolicoli.android.golfscoreboard.utils.FeeCalculator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class NewRankingFeeSettingFragment extends Fragment {

	private int playerCount, totalRankingFee;

	private TextView sumTextView, totalRankingFeeTextView;

	private View[] feeTitleTextViews;
	private EditText[] feeEditTexts;
	private TextView[] recommendTextViews;
	private TextView messageTextView;

	private DecimalFormat feeFormat;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(
				R.layout.current_game_new_ranking_fee_setting_fragment, null);

		feeFormat = new DecimalFormat(getString(R.string.fee_format));

		sumTextView = (TextView) view.findViewById(R.id.SumTextView);
		totalRankingFeeTextView = (TextView) view
				.findViewById(R.id.TotalRankingFeeTextView);
		messageTextView = (TextView) view.findViewById(R.id.MessageTextView);

		View fee1TitleTextView = view.findViewById(R.id.Fee2TitleTextView);
		View fee2TitleTextView = view.findViewById(R.id.Fee2TitleTextView);
		View fee3TitleTextView = view.findViewById(R.id.Fee3TitleTextView);
		View fee4TitleTextView = view.findViewById(R.id.Fee4TitleTextView);
		View fee5TitleTextView = view.findViewById(R.id.Fee5TitleTextView);
		View fee6TitleTextView = view.findViewById(R.id.Fee6TitleTextView);
		feeTitleTextViews = new View[Constants.MAX_PLAYER_COUNT];
		feeTitleTextViews[0] = fee1TitleTextView;
		feeTitleTextViews[1] = fee2TitleTextView;
		feeTitleTextViews[2] = fee3TitleTextView;
		feeTitleTextViews[3] = fee4TitleTextView;
		feeTitleTextViews[4] = fee5TitleTextView;
		feeTitleTextViews[5] = fee6TitleTextView;

		EditText fee1EditText = (EditText) view.findViewById(R.id.Fee1EditText);
		EditText fee2EditText = (EditText) view.findViewById(R.id.Fee2EditText);
		EditText fee3EditText = (EditText) view.findViewById(R.id.Fee3EditText);
		EditText fee4EditText = (EditText) view.findViewById(R.id.Fee4EditText);
		EditText fee5EditText = (EditText) view.findViewById(R.id.Fee5EditText);
		EditText fee6EditText = (EditText) view.findViewById(R.id.Fee6EditText);
		feeEditTexts = new EditText[Constants.MAX_PLAYER_COUNT];
		feeEditTexts[0] = fee1EditText;
		feeEditTexts[1] = fee2EditText;
		feeEditTexts[2] = fee3EditText;
		feeEditTexts[3] = fee4EditText;
		feeEditTexts[4] = fee5EditText;
		feeEditTexts[5] = fee6EditText;

		TextWatcher watcher = new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				feeEditChanged();
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}
		};
		feeEditTexts[0].addTextChangedListener(watcher);
		feeEditTexts[1].addTextChangedListener(watcher);
		feeEditTexts[2].addTextChangedListener(watcher);
		feeEditTexts[3].addTextChangedListener(watcher);
		feeEditTexts[4].addTextChangedListener(watcher);
		feeEditTexts[5].addTextChangedListener(watcher);

		TextView fee1RecommendTextView = (TextView) view
				.findViewById(R.id.Recommend1TextView);
		TextView fee2RecommendTextView = (TextView) view
				.findViewById(R.id.Recommend2TextView);
		TextView fee3RecommendTextView = (TextView) view
				.findViewById(R.id.Recommend3TextView);
		TextView fee4RecommendTextView = (TextView) view
				.findViewById(R.id.Recommend4TextView);
		TextView fee5RecommendTextView = (TextView) view
				.findViewById(R.id.Recommend5TextView);
		TextView fee6RecommendTextView = (TextView) view
				.findViewById(R.id.Recommend6TextView);
		recommendTextViews = new TextView[Constants.MAX_PLAYER_COUNT];
		recommendTextViews[0] = fee1RecommendTextView;
		recommendTextViews[1] = fee2RecommendTextView;
		recommendTextViews[2] = fee3RecommendTextView;
		recommendTextViews[3] = fee4RecommendTextView;
		recommendTextViews[4] = fee5RecommendTextView;
		recommendTextViews[5] = fee6RecommendTextView;

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		for (int i = 1; i <= Constants.MAX_PLAYER_COUNT; i++) {
			feeEditTexts[i - 1].setText(String.valueOf(0));
		}
	}

	public void setInitialValues(int playerCount, int totalRankingFee) {
		this.playerCount = playerCount;
		this.totalRankingFee = totalRankingFee;

		totalRankingFeeTextView.setText(feeFormat.format(totalRankingFee));

		for (int i = 2; i < feeTitleTextViews.length; i++) {
			if (i < playerCount) {
				feeEditTexts[i].setVisibility(View.VISIBLE);
				feeTitleTextViews[i].setVisibility(View.VISIBLE);
				recommendTextViews[i].setVisibility(View.VISIBLE);
			} else {
				feeEditTexts[i].setVisibility(View.GONE);
				feeTitleTextViews[i].setVisibility(View.GONE);
				recommendTextViews[i].setVisibility(View.GONE);
			}
		}

		fillRecommendFees();
		fillFee();
	}

	private void fillRecommendFees() {
		if (totalRankingFee <= 0) {
			recommendTextViews[0].setText(feeFormat.format(0));
			recommendTextViews[1].setText(feeFormat.format(0));
			recommendTextViews[2].setText(feeFormat.format(0));
			recommendTextViews[3].setText(feeFormat.format(0));
			recommendTextViews[4].setText(feeFormat.format(0));
			recommendTextViews[5].setText(feeFormat.format(0));
			return;
		}

		int[] recommendFeesForRanking = new int[Constants.MAX_PLAYER_COUNT];

		switch (playerCount) {
		case 2:
			recommendFeesForRanking[0] = 0;
			recommendFeesForRanking[1] = FeeCalculator
					.toFee(totalRankingFee, 1);
			recommendFeesForRanking[2] = 0;
			recommendFeesForRanking[3] = 0;
			recommendFeesForRanking[4] = 0;
			recommendFeesForRanking[5] = 0;

			break;

		case 3:
			recommendFeesForRanking[0] = 0;
			recommendFeesForRanking[1] = FeeCalculator.toFee(
					totalRankingFee * 2, 2 + 3);
			recommendFeesForRanking[2] = FeeCalculator.toFee(
					totalRankingFee * 3, 2 + 3);
			recommendFeesForRanking[3] = 0;
			recommendFeesForRanking[4] = 0;
			recommendFeesForRanking[5] = 0;
			break;

		case 4:
			recommendFeesForRanking[0] = 0;
			recommendFeesForRanking[1] = FeeCalculator.toFee(
					totalRankingFee * 3, 3 + 4 + 5);
			recommendFeesForRanking[2] = FeeCalculator.toFee(
					totalRankingFee * 4, 3 + 4 + 5);
			recommendFeesForRanking[3] = FeeCalculator.toFee(
					totalRankingFee * 5, 3 + 4 + 5);
			recommendFeesForRanking[4] = 0;
			recommendFeesForRanking[5] = 0;
			break;

		case 5:
			recommendFeesForRanking[0] = 0;
			recommendFeesForRanking[1] = FeeCalculator.toFee(
					totalRankingFee * 3, 3 + 4 + 5 + 6);
			recommendFeesForRanking[2] = FeeCalculator.toFee(
					totalRankingFee * 4, 3 + 4 + 5 + 6);
			recommendFeesForRanking[3] = FeeCalculator.toFee(
					totalRankingFee * 5, 3 + 4 + 5 + 6);
			recommendFeesForRanking[4] = FeeCalculator.toFee(
					totalRankingFee * 6, 3 + 4 + 5 + 6);
			recommendFeesForRanking[5] = 0;
			break;

		case 6:
			recommendFeesForRanking[0] = 0;
			recommendFeesForRanking[1] = FeeCalculator.toFee(
					totalRankingFee * 3, 3 + 4 + 5 + 6 + 7);
			recommendFeesForRanking[2] = FeeCalculator.toFee(
					totalRankingFee * 4, 3 + 4 + 5 + 6 + 7);
			recommendFeesForRanking[3] = FeeCalculator.toFee(
					totalRankingFee * 5, 3 + 4 + 5 + 6 + 7);
			recommendFeesForRanking[4] = FeeCalculator.toFee(
					totalRankingFee * 6, 3 + 4 + 5 + 6 + 7);
			recommendFeesForRanking[5] = FeeCalculator.toFee(
					totalRankingFee * 7, 3 + 4 + 5 + 6 + 7);
			break;

		default:
			recommendTextViews[0].setText(feeFormat.format(0));
			recommendTextViews[1].setText(feeFormat.format(0));
			recommendTextViews[2].setText(feeFormat.format(0));
			recommendTextViews[3].setText(feeFormat.format(0));
			recommendTextViews[4].setText(feeFormat.format(0));
			recommendTextViews[5].setText(feeFormat.format(0));
			return;
		}

		int sum = 0;
		for (int i = 0; i < Constants.MAX_PLAYER_COUNT; i++) {
			sum += recommendFeesForRanking[i];
		}
		int addition = FeeCalculator.toFee(totalRankingFee - sum, 1);
		recommendFeesForRanking[playerCount - 1] += addition;

		for (int i = 0; i < Constants.MAX_PLAYER_COUNT; i++) {
			recommendTextViews[i].setText(feeFormat
					.format(recommendFeesForRanking[i]));
			int roundedFee = Math.round(recommendFeesForRanking[i] / 1000F) * 1000;
			feeEditTexts[i].setText(String.valueOf(roundedFee));
		}
	}

	private void fillFee() {
		int fee = 0;
		for (int i = 1; i <= playerCount; i++) {
			fee += getFeeForRanking(i);
		}

		sumTextView.setText(feeFormat.format(fee));

		CurrentGameNewGameSettingActivity activity = ((CurrentGameNewGameSettingActivity) getActivity());
		if (fee < totalRankingFee) {
			messageTextView.setVisibility(View.VISIBLE);
			activity.setNextButtonEnable(false);
		} else if (fee > totalRankingFee) {
			messageTextView.setVisibility(View.VISIBLE);
			activity.setNextButtonEnable(false);
		} else {
			messageTextView.setVisibility(View.GONE);
			activity.setNextButtonEnable(true);
		}
	}

	public boolean isEnableToFinish() {
		int fee = 0;
		for (int i = 1; i <= playerCount; i++) {
			fee += getFeeForRanking(i);
		}

		if (fee < totalRankingFee) {
			return false;
		} else if (fee > totalRankingFee) {
			return false;
		}

		return true;
	}

	private void feeEditChanged() {
		fillFee();
	}

	public int getFeeForRanking(int ranking) {
		try {
			return Integer.parseInt(feeEditTexts[ranking - 1].getText()
					.toString());
		} catch (Throwable t) {
			return 0;
		}
	}
}
