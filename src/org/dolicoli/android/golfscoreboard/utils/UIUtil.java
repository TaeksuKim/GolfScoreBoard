package org.dolicoli.android.golfscoreboard.utils;

import java.text.DecimalFormat;

import org.dolicoli.android.golfscoreboard.Constants;
import org.dolicoli.android.golfscoreboard.R;

import android.content.Context;
import android.util.TypedValue;
import android.widget.TextView;

public class UIUtil {
	private static boolean primaryTextColorLoaded = false;
	private static int primaryTextColor = -1;

	private static DecimalFormat feeFormat;
	private static DecimalFormat rankingFormat;
	private static DecimalFormat avgRankingFormat;
	private static DecimalFormat avgScoreFormat;
	private static DecimalFormat gameCountFormat;

	public static String formatFee(Context context, int fee) {
		if (context == null)
			return String.valueOf(fee);

		if (feeFormat == null) {
			feeFormat = new DecimalFormat(
					context.getString(R.string.fee_format));
		}

		return feeFormat.format(fee);
	}

	public static String formatFee(Context context, double fee) {
		return formatFee(context, (int) fee);
	}

	public static void setFeeTextView(Context context, TextView textView,
			int fee) {
		if (context == null || textView == null)
			return;

		String text = formatFee(context, fee);

		textView.setText(text);
	}

	public static void setFeeTextView(Context context, TextView textView,
			double fee) {
		setFeeTextView(context, textView, (int) fee);
	}

	public static String formatRanking(Context context, int ranking) {
		if (context == null)
			return String.valueOf(ranking);

		if (rankingFormat == null) {
			rankingFormat = new DecimalFormat(
					context.getString(R.string.ranking_format1));
		}

		return rankingFormat.format(ranking);
	}

	public static void setRankingTextView(Context context, TextView textView,
			int ranking) {
		if (context == null || textView == null)
			return;

		String text = formatRanking(context, ranking);

		textView.setText(text);
	}

	public static String formatAvgRanking(Context context, double ranking) {
		if (context == null)
			return String.valueOf(ranking);

		if (avgRankingFormat == null) {
			avgRankingFormat = new DecimalFormat(
					context.getString(R.string.avg_ranking_format));
		}

		return avgRankingFormat.format(ranking);
	}

	public static void setAvgRankingTextView(Context context,
			TextView textView, double ranking) {
		if (context == null || textView == null)
			return;

		String text = formatAvgRanking(context, ranking);

		textView.setText(text);
	}

	public static String formatAvgScore(Context context, double score) {
		if (context == null)
			return String.valueOf(score);

		if (avgScoreFormat == null) {
			avgScoreFormat = new DecimalFormat("0.00");
		}

		String text = avgScoreFormat.format(score);
		if (score <= 0.0) {
			return text;
		}
		return "+" + text;
	}

	public static void setAvgScoreTextView(Context context, TextView textView,
			double score) {
		loadPrimaryTextColor(context);
		setAvgScoreTextView(context, textView, primaryTextColor, score);
	}

	public static void setAvgScoreTextView(Context context, TextView textView,
			int defaultTextColor, double score) {
		if (context == null || textView == null)
			return;

		String text = formatAvgScore(context, score);

		textView.setText(text);
		if (score < 0.0) {
			textView.setTextColor(Constants.UNDER_PAR_TEXT_COLOR);
		} else if (score == 0.0) {
			textView.setTextColor(Constants.EVEN_PAR_TEXT_COLOR);
		} else {
			textView.setTextColor(defaultTextColor);
		}
	}

	public static String formatScore(Context context, int score) {
		if (context == null)
			return String.valueOf(score);

		if (score <= 0) {
			return String.valueOf(score);
		}

		return "+" + String.valueOf(score);
	}

	public static void setScoreTextView(Context context, TextView textView,
			int score) {
		loadPrimaryTextColor(context);
		setScoreTextView(context, textView, primaryTextColor, score);
	}

	public static void setScoreTextView(Context context, TextView textView,
			int defaultTextColor, int score) {
		if (context == null || textView == null)
			return;

		String text = formatScore(context, score);

		textView.setText(text);
		if (score < 0) {
			textView.setTextColor(Constants.UNDER_PAR_TEXT_COLOR);
		} else if (score == 0) {
			textView.setTextColor(Constants.EVEN_PAR_TEXT_COLOR);
		} else {
			textView.setTextColor(defaultTextColor);
		}
	}

	public static String formatGameCount(Context context, int count) {
		if (context == null)
			return String.valueOf(count);

		if (gameCountFormat == null) {
			gameCountFormat = new DecimalFormat("###,### ȸ");
		}

		return gameCountFormat.format(count);
	}

	public static void setGameCountTextView(Context context, TextView textView,
			int count) {
		if (context == null || textView == null)
			return;

		String text = formatGameCount(context, count);
		textView.setText(text);
	}

	private static void loadPrimaryTextColor(Context context) {
		if (primaryTextColorLoaded)
			return;

		TypedValue tv = new TypedValue();
		context.getTheme().resolveAttribute(R.attr.primaryTextColor, tv, true);
		primaryTextColor = context.getResources().getColor(tv.resourceId);
		primaryTextColorLoaded = true;
	}
}
