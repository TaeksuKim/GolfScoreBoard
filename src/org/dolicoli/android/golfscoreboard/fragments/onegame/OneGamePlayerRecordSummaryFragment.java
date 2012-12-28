package org.dolicoli.android.golfscoreboard.fragments.onegame;

import org.dolicoli.android.golfscoreboard.Constants;
import org.dolicoli.android.golfscoreboard.R;
import org.dolicoli.android.golfscoreboard.data.SingleGameResult;
import org.dolicoli.android.golfscoreboard.data.settings.Result;
import org.dolicoli.android.golfscoreboard.tasks.CurrentGameQueryTask;
import org.dolicoli.android.golfscoreboard.tasks.HistoryQueryTask;
import org.dolicoli.android.golfscoreboard.utils.FeeCalculator;
import org.dolicoli.android.golfscoreboard.utils.UIUtil;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class OneGamePlayerRecordSummaryFragment extends Fragment implements
		OneGamePlayerRecordActivityPage, CurrentGameQueryTask.TaskListener,
		HistoryQueryTask.TaskListener {

	private int playerId;

	private TextView[] rankingTitleTextViews;
	private TextView[] rankingValueTextViews;
	private TextView[] rankingSameValueTextViews;
	private TextView feeZeroValueTextView, feeUnder1000ValueTextView,
			feeUnder1500ValueTextView, feeUnder2000ValueTextView,
			feeUnder2500ValueTextView, feeOver2500ValueTextView;
	private TextView underCondorValueTextView, albatrossValueTextView,
			eagleValueTextView, birdieValueTextView, parValueTextView,
			bogeyValueTextView, doubleBogeyValueTextView,
			tripleBogeyValueTextView, quadrupleBogeyValueTextView,
			overQuintupleBogeyValueTextView;

	private int[] values, sameValues;

	private int primaryTextColor, secondaryTextColor;

	private String playDate;
	private boolean currentGame;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(
				R.layout.onegame_player_record_summary_fragment, null);

		playerId = 0;

		rankingTitleTextViews = new TextView[Constants.MAX_PLAYER_COUNT];
		rankingTitleTextViews[0] = (TextView) view
				.findViewById(R.id.Ranking1TitleTextView);
		rankingTitleTextViews[1] = (TextView) view
				.findViewById(R.id.Ranking2TitleTextView);
		rankingTitleTextViews[2] = (TextView) view
				.findViewById(R.id.Ranking3TitleTextView);
		rankingTitleTextViews[3] = (TextView) view
				.findViewById(R.id.Ranking4TitleTextView);
		rankingTitleTextViews[4] = (TextView) view
				.findViewById(R.id.Ranking5TitleTextView);
		rankingTitleTextViews[5] = (TextView) view
				.findViewById(R.id.Ranking6TitleTextView);

		rankingValueTextViews = new TextView[Constants.MAX_PLAYER_COUNT];
		rankingValueTextViews[0] = (TextView) view
				.findViewById(R.id.Ranking1ValueTextView);
		rankingValueTextViews[1] = (TextView) view
				.findViewById(R.id.Ranking2ValueTextView);
		rankingValueTextViews[2] = (TextView) view
				.findViewById(R.id.Ranking3ValueTextView);
		rankingValueTextViews[3] = (TextView) view
				.findViewById(R.id.Ranking4ValueTextView);
		rankingValueTextViews[4] = (TextView) view
				.findViewById(R.id.Ranking5ValueTextView);
		rankingValueTextViews[5] = (TextView) view
				.findViewById(R.id.Ranking6ValueTextView);

		rankingSameValueTextViews = new TextView[Constants.MAX_PLAYER_COUNT];
		rankingSameValueTextViews[0] = (TextView) view
				.findViewById(R.id.Ranking1SameValueTextView);
		rankingSameValueTextViews[1] = (TextView) view
				.findViewById(R.id.Ranking2SameValueTextView);
		rankingSameValueTextViews[2] = (TextView) view
				.findViewById(R.id.Ranking3SameValueTextView);
		rankingSameValueTextViews[3] = (TextView) view
				.findViewById(R.id.Ranking4SameValueTextView);
		rankingSameValueTextViews[4] = (TextView) view
				.findViewById(R.id.Ranking5SameValueTextView);
		rankingSameValueTextViews[5] = (TextView) view
				.findViewById(R.id.Ranking6SameValueTextView);

		feeZeroValueTextView = (TextView) view
				.findViewById(R.id.FeeZeroValueTextView);
		feeUnder1000ValueTextView = (TextView) view
				.findViewById(R.id.FeeUnder1000ValueTextView);
		feeUnder1500ValueTextView = (TextView) view
				.findViewById(R.id.FeeUnder1500ValueTextView);
		feeUnder2000ValueTextView = (TextView) view
				.findViewById(R.id.FeeUnder2000ValueTextView);
		feeUnder2500ValueTextView = (TextView) view
				.findViewById(R.id.FeeUnder2500ValueTextView);
		feeOver2500ValueTextView = (TextView) view
				.findViewById(R.id.FeeOver2500ValueTextView);

		underCondorValueTextView = (TextView) view
				.findViewById(R.id.UnderCondorValueTextView);
		albatrossValueTextView = (TextView) view
				.findViewById(R.id.AlbatrossValueTextView);
		eagleValueTextView = (TextView) view
				.findViewById(R.id.EagleValueTextView);
		birdieValueTextView = (TextView) view
				.findViewById(R.id.BirdieValueTextView);
		parValueTextView = (TextView) view.findViewById(R.id.ParValueTextView);
		bogeyValueTextView = (TextView) view
				.findViewById(R.id.BogeyValueTextView);
		doubleBogeyValueTextView = (TextView) view
				.findViewById(R.id.DoubleBogeyValueTextView);
		tripleBogeyValueTextView = (TextView) view
				.findViewById(R.id.TripleBogeyValueTextView);
		quadrupleBogeyValueTextView = (TextView) view
				.findViewById(R.id.QuadrupleBogeyValueTextView);
		overQuintupleBogeyValueTextView = (TextView) view
				.findViewById(R.id.OverQuintupleBogeyValueTextView);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		TypedValue tv = new TypedValue();
		getActivity().getTheme().resolveAttribute(R.attr.primaryTextColor, tv,
				true);
		primaryTextColor = getResources().getColor(tv.resourceId);

		tv = new TypedValue();
		getActivity().getTheme().resolveAttribute(R.attr.secondaryTextColor,
				tv, true);
		secondaryTextColor = getResources().getColor(tv.resourceId);

		values = new int[Constants.MAX_PLAYER_COUNT];
		sameValues = new int[Constants.MAX_PLAYER_COUNT];
		for (int i = 0; i < Constants.MAX_PLAYER_COUNT; i++) {
			values[i] = 0;
			sameValues[i] = 0;
		}
	}

	@Override
	public void setPlayerId(boolean currentGame, int playerId, String playDate) {
		this.currentGame = currentGame;
		this.playerId = playerId;
		this.playDate = playDate;
		reload();
	}

	@Override
	public void onGameQueryStarted() {
	}

	@Override
	public void onGameQueryFinished(SingleGameResult gameResult) {
		reloadUI(gameResult);
	}

	private void reload() {
		FragmentActivity activity = getActivity();
		if (activity == null)
			return;

		if (currentGame) {
			CurrentGameQueryTask task = new CurrentGameQueryTask(activity, this);
			task.execute();
		} else {
			HistoryQueryTask task = new HistoryQueryTask(activity, this);
			task.execute(new HistoryQueryTask.QueryParam(playDate));
		}
	}

	private void reloadUI(SingleGameResult gameResult) {
		FragmentActivity activity = getActivity();

		if (activity == null || gameResult == null)
			return;

		int playerCount = gameResult.getPlayerCount();
		if (playerCount <= 2) {
			rankingTitleTextViews[2].setVisibility(View.GONE);
			rankingTitleTextViews[3].setVisibility(View.GONE);
			rankingTitleTextViews[4].setVisibility(View.GONE);
			rankingTitleTextViews[5].setVisibility(View.GONE);

			rankingValueTextViews[2].setVisibility(View.GONE);
			rankingValueTextViews[3].setVisibility(View.GONE);
			rankingValueTextViews[4].setVisibility(View.GONE);
			rankingValueTextViews[5].setVisibility(View.GONE);

			rankingSameValueTextViews[2].setVisibility(View.GONE);
			rankingSameValueTextViews[3].setVisibility(View.GONE);
			rankingSameValueTextViews[4].setVisibility(View.GONE);
			rankingSameValueTextViews[5].setVisibility(View.GONE);
		} else if (playerCount == 3) {
			rankingTitleTextViews[2].setVisibility(View.VISIBLE);
			rankingTitleTextViews[3].setVisibility(View.INVISIBLE);
			rankingTitleTextViews[4].setVisibility(View.GONE);
			rankingTitleTextViews[5].setVisibility(View.GONE);

			rankingValueTextViews[2].setVisibility(View.VISIBLE);
			rankingValueTextViews[3].setVisibility(View.INVISIBLE);
			rankingValueTextViews[4].setVisibility(View.GONE);
			rankingValueTextViews[5].setVisibility(View.GONE);

			rankingSameValueTextViews[2].setVisibility(View.VISIBLE);
			rankingSameValueTextViews[3].setVisibility(View.INVISIBLE);
			rankingSameValueTextViews[4].setVisibility(View.GONE);
			rankingSameValueTextViews[5].setVisibility(View.GONE);
		} else if (playerCount == 4) {
			rankingTitleTextViews[2].setVisibility(View.VISIBLE);
			rankingTitleTextViews[3].setVisibility(View.VISIBLE);
			rankingTitleTextViews[4].setVisibility(View.GONE);
			rankingTitleTextViews[5].setVisibility(View.GONE);

			rankingValueTextViews[2].setVisibility(View.VISIBLE);
			rankingValueTextViews[3].setVisibility(View.VISIBLE);
			rankingValueTextViews[4].setVisibility(View.GONE);
			rankingValueTextViews[5].setVisibility(View.GONE);

			rankingSameValueTextViews[2].setVisibility(View.VISIBLE);
			rankingSameValueTextViews[3].setVisibility(View.VISIBLE);
			rankingSameValueTextViews[4].setVisibility(View.GONE);
			rankingSameValueTextViews[5].setVisibility(View.GONE);
		} else if (playerCount == 5) {
			rankingTitleTextViews[2].setVisibility(View.VISIBLE);
			rankingTitleTextViews[3].setVisibility(View.VISIBLE);
			rankingTitleTextViews[4].setVisibility(View.VISIBLE);
			rankingTitleTextViews[5].setVisibility(View.INVISIBLE);

			rankingValueTextViews[2].setVisibility(View.VISIBLE);
			rankingValueTextViews[3].setVisibility(View.VISIBLE);
			rankingValueTextViews[4].setVisibility(View.VISIBLE);
			rankingValueTextViews[5].setVisibility(View.INVISIBLE);

			rankingSameValueTextViews[2].setVisibility(View.VISIBLE);
			rankingSameValueTextViews[3].setVisibility(View.VISIBLE);
			rankingSameValueTextViews[4].setVisibility(View.VISIBLE);
			rankingSameValueTextViews[5].setVisibility(View.INVISIBLE);
		} else if (playerCount >= 6) {
			rankingTitleTextViews[2].setVisibility(View.VISIBLE);
			rankingTitleTextViews[3].setVisibility(View.VISIBLE);
			rankingTitleTextViews[4].setVisibility(View.VISIBLE);
			rankingTitleTextViews[5].setVisibility(View.VISIBLE);

			rankingValueTextViews[2].setVisibility(View.VISIBLE);
			rankingValueTextViews[3].setVisibility(View.VISIBLE);
			rankingValueTextViews[4].setVisibility(View.VISIBLE);
			rankingValueTextViews[5].setVisibility(View.VISIBLE);

			rankingSameValueTextViews[2].setVisibility(View.VISIBLE);
			rankingSameValueTextViews[3].setVisibility(View.VISIBLE);
			rankingSameValueTextViews[4].setVisibility(View.VISIBLE);
			rankingSameValueTextViews[5].setVisibility(View.VISIBLE);
		}

		for (int i = 0; i < Constants.MAX_PLAYER_COUNT; i++) {
			values[i] = 0;
			sameValues[i] = 0;
		}

		int feeZero = 0;
		int feeUnder1000 = 0;
		int feeUnder1500 = 0;
		int feeUnder2000 = 0;
		int feeUnder2500 = 0;
		int feeOver2500 = 0;

		int underCondor = 0;
		int albatross = 0;
		int eagle = 0;
		int birdie = 0;
		int par = 0;
		int bogey = 0;
		int doubleBogey = 0;
		int tripleBogey = 0;
		int quadrupleBogey = 0;
		int overQuintupleBogey = 0;

		for (Result result : gameResult.getResults()) {
			int ranking = result.getRanking(playerId);
			int sameRankingCount = result.getSameRankingCount(playerId);

			if (sameRankingCount > 1)
				sameValues[ranking - 1]++;
			else
				values[ranking - 1]++;

			int[] fees = FeeCalculator.calculateFee(gameResult, result);
			if (fees[playerId] <= 0) {
				feeZero++;
			} else if (fees[playerId] <= 1000) {
				feeUnder1000++;
			} else if (fees[playerId] <= 1500) {
				feeUnder1500++;
			} else if (fees[playerId] <= 2000) {
				feeUnder2000++;
			} else if (fees[playerId] <= 2500) {
				feeUnder2500++;
			} else {
				feeOver2500++;
			}

			int score = result.getOriginalScore(playerId);
			if (score <= -4) {
				underCondor++;
			} else if (score <= -3) {
				albatross++;
			} else if (score <= -2) {
				eagle++;
			} else if (score <= -1) {
				birdie++;
			} else if (score <= 0) {
				par++;
			} else if (score <= 1) {
				bogey++;
			} else if (score <= 2) {
				doubleBogey++;
			} else if (score <= 3) {
				tripleBogey++;
			} else if (score <= 4) {
				quadrupleBogey++;
			} else {
				overQuintupleBogey++;
			}
		}
		for (int i = 0; i < Constants.MAX_PLAYER_COUNT; i++) {
			rankingValueTextViews[i].setText(UIUtil.formatGameCount(activity,
					values[i]));
			rankingSameValueTextViews[i].setText(UIUtil.formatGameCount(
					activity, sameValues[i]));

			if (values[i] >= 1) {
				rankingValueTextViews[i].setTextColor(primaryTextColor);
			} else {
				rankingValueTextViews[i].setText("-");
				rankingValueTextViews[i].setTextColor(secondaryTextColor);
			}
			if (sameValues[i] >= 1) {
				rankingSameValueTextViews[i].setTextColor(primaryTextColor);
			} else {
				rankingSameValueTextViews[i].setText("-");
				rankingSameValueTextViews[i].setTextColor(secondaryTextColor);
			}
		}

		feeZeroValueTextView.setText(UIUtil.formatGameCount(activity, feeZero));
		if (feeZero > 0) {
			feeZeroValueTextView.setTextColor(primaryTextColor);
		} else {
			feeZeroValueTextView.setText("-");
			feeZeroValueTextView.setTextColor(secondaryTextColor);
		}
		feeUnder1000ValueTextView.setText(UIUtil.formatGameCount(activity,
				feeUnder1000));
		if (feeUnder1000 > 0) {
			feeUnder1000ValueTextView.setTextColor(primaryTextColor);
		} else {
			feeUnder1000ValueTextView.setText("-");
			feeUnder1000ValueTextView.setTextColor(secondaryTextColor);
		}
		feeUnder1500ValueTextView.setText(UIUtil.formatGameCount(activity,
				feeUnder1500));
		if (feeUnder1500 > 0) {
			feeUnder1500ValueTextView.setTextColor(primaryTextColor);
		} else {
			feeUnder1500ValueTextView.setText("-");
			feeUnder1500ValueTextView.setTextColor(secondaryTextColor);
		}
		feeUnder2000ValueTextView.setText(UIUtil.formatGameCount(activity,
				feeUnder2000));
		if (feeUnder2000 > 0) {
			feeUnder2000ValueTextView.setTextColor(primaryTextColor);
		} else {
			feeUnder2000ValueTextView.setText("-");
			feeUnder2000ValueTextView.setTextColor(secondaryTextColor);
		}
		feeUnder2500ValueTextView.setText(UIUtil.formatGameCount(activity,
				feeUnder2500));
		if (feeUnder2500 > 0) {
			feeUnder2500ValueTextView.setTextColor(primaryTextColor);
		} else {
			feeUnder2500ValueTextView.setText("-");
			feeUnder2500ValueTextView.setTextColor(secondaryTextColor);
		}
		feeOver2500ValueTextView.setText(UIUtil.formatGameCount(activity,
				feeOver2500));
		if (feeOver2500 > 0) {
			feeOver2500ValueTextView.setTextColor(primaryTextColor);
		} else {
			feeOver2500ValueTextView.setText("-");
			feeOver2500ValueTextView.setTextColor(secondaryTextColor);
		}

		underCondorValueTextView.setText(UIUtil.formatGameCount(activity,
				underCondor));
		if (underCondor > 0) {
			underCondorValueTextView.setTextColor(primaryTextColor);
		} else {
			underCondorValueTextView.setText("-");
			underCondorValueTextView.setTextColor(secondaryTextColor);
		}
		albatrossValueTextView.setText(UIUtil.formatGameCount(activity,
				albatross));
		if (albatross > 0) {
			albatrossValueTextView.setTextColor(primaryTextColor);
		} else {
			albatrossValueTextView.setText("-");
			albatrossValueTextView.setTextColor(secondaryTextColor);
		}
		eagleValueTextView.setText(UIUtil.formatGameCount(activity, eagle));
		if (eagle > 0) {
			eagleValueTextView.setTextColor(primaryTextColor);
		} else {
			eagleValueTextView.setText("-");
			eagleValueTextView.setTextColor(secondaryTextColor);
		}
		birdieValueTextView.setText(UIUtil.formatGameCount(activity, birdie));
		if (birdie > 0) {
			birdieValueTextView.setTextColor(primaryTextColor);
		} else {
			birdieValueTextView.setText("-");
			birdieValueTextView.setTextColor(secondaryTextColor);
		}
		parValueTextView.setText(UIUtil.formatGameCount(activity, par));
		if (par > 0) {
			parValueTextView.setTextColor(primaryTextColor);
		} else {
			parValueTextView.setText("-");
			parValueTextView.setTextColor(secondaryTextColor);
		}
		bogeyValueTextView.setText(UIUtil.formatGameCount(activity, bogey));
		if (bogey > 0) {
			bogeyValueTextView.setTextColor(primaryTextColor);
		} else {
			bogeyValueTextView.setText("-");
			bogeyValueTextView.setTextColor(secondaryTextColor);
		}
		doubleBogeyValueTextView.setText(UIUtil.formatGameCount(activity,
				doubleBogey));
		if (doubleBogey > 0) {
			doubleBogeyValueTextView.setTextColor(primaryTextColor);
		} else {
			doubleBogeyValueTextView.setText("-");
			doubleBogeyValueTextView.setTextColor(secondaryTextColor);
		}
		tripleBogeyValueTextView.setText(UIUtil.formatGameCount(activity,
				tripleBogey));
		if (tripleBogey > 0) {
			tripleBogeyValueTextView.setTextColor(primaryTextColor);
		} else {
			tripleBogeyValueTextView.setText("-");
			tripleBogeyValueTextView.setTextColor(secondaryTextColor);
		}
		quadrupleBogeyValueTextView.setText(UIUtil.formatGameCount(activity,
				quadrupleBogey));
		if (quadrupleBogey > 0) {
			quadrupleBogeyValueTextView.setTextColor(primaryTextColor);
		} else {
			quadrupleBogeyValueTextView.setText("-");
			quadrupleBogeyValueTextView.setTextColor(secondaryTextColor);
		}
		overQuintupleBogeyValueTextView.setText(UIUtil.formatGameCount(
				activity, overQuintupleBogey));
		if (overQuintupleBogey > 0) {
			overQuintupleBogeyValueTextView.setTextColor(primaryTextColor);
		} else {
			overQuintupleBogeyValueTextView.setText("-");
			overQuintupleBogeyValueTextView.setTextColor(secondaryTextColor);
		}
	}
}
