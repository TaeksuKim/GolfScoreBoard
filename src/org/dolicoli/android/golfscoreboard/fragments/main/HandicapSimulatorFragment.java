package org.dolicoli.android.golfscoreboard.fragments.main;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import org.dolicoli.android.golfscoreboard.R;
import org.dolicoli.android.golfscoreboard.data.OneGame;
import org.dolicoli.android.golfscoreboard.tasks.GameAndResultTask;
import org.dolicoli.android.golfscoreboard.tasks.GameAndResultTask.GameAndResultTaskListener;
import org.dolicoli.android.golfscoreboard.utils.DateRangeUtil;
import org.dolicoli.android.golfscoreboard.utils.DateRangeUtil.DateRange;
import org.dolicoli.android.golfscoreboard.utils.PlayerUIUtil;
import org.dolicoli.android.golfscoreboard.utils.Reloadable;
import org.dolicoli.android.golfscoreboard.utils.handicaps.Ecco1Calculator;
import org.dolicoli.android.golfscoreboard.utils.handicaps.HandicapCalculator;
import org.dolicoli.android.golfscoreboard.utils.handicaps.HandicapCalculator.GameResultItem;
import org.dolicoli.android.golfscoreboard.utils.handicaps.HandicapCalculator.ResourceContainer;
import org.dolicoli.android.golfscoreboard.utils.handicaps.LaterBetterCalculator;
import org.dolicoli.android.golfscoreboard.utils.handicaps.MoyaCalculator;
import org.dolicoli.android.golfscoreboard.utils.handicaps.SimpleHandicapCalculator;
import org.dolicoli.android.golfscoreboard.utils.handicaps.ThreeMonthsHandicapCalculator;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class HandicapSimulatorFragment extends ListFragment implements
		Reloadable, GameAndResultTaskListener, OnItemSelectedListener {

	private ProgressBar progressBar;
	private Spinner handicapCalculatorSpinner;

	private ArrayList<OneGame> gameAndResults;
	private PlayerHandicapListAdapter adapter;
	private HandicapCalculator[] calculators;
	private String[] canonicalPlayerNames;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final FragmentActivity activity = getActivity();

		ActionBar actionBar = activity.getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle("ÇÚµðÄ¸ °è»ê");

		View view = inflater
				.inflate(R.layout.handicap_simulator_fragment, null);

		progressBar = (ProgressBar) view.findViewById(R.id.ProgressBar);

		ResourceContainer resourceContainer = new ResourceContainer() {
			@Override
			public String getString(int resourceId) {
				return activity.getString(resourceId);
			}
		};
		calculators = new HandicapCalculator[] {
				new SimpleHandicapCalculator(), new Ecco1Calculator(),
				new LaterBetterCalculator(), new MoyaCalculator(),
				new ThreeMonthsHandicapCalculator() };
		String[] calculatorNames = new String[calculators.length];
		for (int i = 0; i < calculators.length; i++) {
			calculatorNames[i] = calculators[i].getName(resourceContainer);
		}

		final SpinnerAdapter handicapCalculatorSpinnerAdapter = new ArrayAdapter<String>(
				activity, android.R.layout.simple_spinner_dropdown_item,
				calculatorNames);
		handicapCalculatorSpinner = (Spinner) view
				.findViewById(R.id.HandicapCalculatorSpinner);
		handicapCalculatorSpinner.setAdapter(handicapCalculatorSpinnerAdapter);
		handicapCalculatorSpinner.setOnItemSelectedListener(this);

		adapter = new PlayerHandicapListAdapter(getActivity(),
				R.layout.handicap_simulator_list_item);
		adapter.setNotifyOnChange(false);
		setListAdapter(adapter);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setHasOptionsMenu(true);
		
		gameAndResults = new ArrayList<OneGame>();
		canonicalPlayerNames = new String[0];

		DateRange dateRange = DateRangeUtil.getDateRange(3);
		GameAndResultTask task = new GameAndResultTask(getActivity(), this);
		task.execute(dateRange);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.main_handicap_simulator, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		FragmentActivity activity = getActivity();
		if (activity == null) {
			return false;
		}

		return activity.onOptionsItemSelected(item);
	}

	@Override
	public void reload(boolean clean) {
		if (getActivity() == null)
			return;

		if (canonicalPlayerNames == null || canonicalPlayerNames.length < 1)
			return;

		HandicapCalculator calculator = calculators[handicapCalculatorSpinner
				.getSelectedItemPosition()];

		ArrayList<GameResultItem> items = new ArrayList<GameResultItem>();
		items.addAll(gameAndResults);
		calculator.calculate(canonicalPlayerNames, items);

		int playerCount = canonicalPlayerNames.length;
		adapter.clear();
		for (int i = 0; i < playerCount; i++) {
			String playerName = canonicalPlayerNames[i];
			int handicap = calculator.getHandicap(playerName);
			if (handicap < 0)
				handicap = 0;

			int gameCount = calculator.getGameCount(playerName);
			if (gameCount < 0)
				gameCount = 0;

			float avgScore = calculator.getAvgScore(playerName);
			if (avgScore < 0F)
				avgScore = 0F;

			PlayerHandicapInfo info = new PlayerHandicapInfo(playerName,
					gameCount, handicap, avgScore);
			adapter.add(info);
		}
		adapter.sort(new Comparator<PlayerHandicapInfo>() {
			@Override
			public int compare(PlayerHandicapInfo lhs, PlayerHandicapInfo rhs) {
				return lhs.compareTo(rhs);
			}
		});
		adapter.notifyDataSetChanged();
	}

	private static class PlayerHandicapListViewHolder {
		View tagView;
		ImageView playerImageView;
		TextView playerNameTextView, attendCountTextView, avgScoreTextView,
				handicapTextView;
	}

	private static class PlayerHandicapListAdapter extends
			ArrayAdapter<PlayerHandicapInfo> implements OnClickListener {

		private PlayerHandicapListViewHolder holder;
		private int textViewResourceId;
		private DecimalFormat format;

		public PlayerHandicapListAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);

			this.textViewResourceId = textViewResourceId;
			this.format = new DecimalFormat("##.00");
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(textViewResourceId, null);
				holder = new PlayerHandicapListViewHolder();

				holder.tagView = v.findViewById(R.id.PlayerTagView);
				holder.playerImageView = (ImageView) v
						.findViewById(R.id.PlayerImageView);
				holder.playerNameTextView = (TextView) v
						.findViewById(R.id.PlayerNameTextView);
				holder.attendCountTextView = (TextView) v
						.findViewById(R.id.AttendCountTextView);
				holder.handicapTextView = (TextView) v
						.findViewById(R.id.HandicapTextView);
				holder.avgScoreTextView = (TextView) v
						.findViewById(R.id.AvgScoreTextView);

				v.setTag(holder);
			} else {
				holder = (PlayerHandicapListViewHolder) v.getTag();
			}

			int count = getCount();
			if (count < 1)
				return v;

			if (position < 0 || position > count - 1)
				return v;

			PlayerHandicapInfo handicapInfo = getItem(position);
			if (handicapInfo == null)
				return v;

			holder.playerNameTextView.setText(handicapInfo.name);
			holder.tagView.setBackgroundColor(PlayerUIUtil
					.getTagColor(handicapInfo.name));
			holder.playerImageView.setImageResource(PlayerUIUtil
					.getRoundResourceId(handicapInfo.name));
			holder.handicapTextView.setText(String
					.valueOf(handicapInfo.handicap));
			holder.attendCountTextView.setText(String
					.valueOf(handicapInfo.gameCount));
			holder.avgScoreTextView.setText(format
					.format(handicapInfo.avgScore));

			return v;
		}

		@Override
		public void onClick(View v) {
			if (!(v instanceof Button))
				return;

			Button button = (Button) v;
			View more = (View) button.getTag();
			if (more.getVisibility() == View.VISIBLE) {
				more.setVisibility(View.GONE);
				button.setBackgroundResource(R.drawable.ic_expand);
			} else {
				more.setVisibility(View.VISIBLE);
				button.setBackgroundResource(R.drawable.ic_collapse);
			}
		}
	}

	private static class PlayerHandicapInfo implements
			Comparable<PlayerHandicapInfo> {
		private String name;
		private int gameCount = 0;
		private int handicap = 0;
		private float avgScore = 0;

		private PlayerHandicapInfo(String name, int gameCount, int handicap,
				float avgScore) {
			this.name = name;
			this.gameCount = gameCount;
			this.handicap = handicap;
			this.avgScore = avgScore;
		}

		@Override
		public int compareTo(PlayerHandicapInfo compare) {
			if (handicap < compare.handicap)
				return -1;
			if (handicap > compare.handicap)
				return 1;
			if (avgScore < compare.avgScore)
				return -1;
			if (avgScore > compare.avgScore)
				return 1;
			return name.compareTo(compare.name);
		}
	}

	@Override
	public void onGameAndResultStarted() {
		if (progressBar != null) {
			progressBar.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onGameAndResultFinished(OneGame[] results) {
		gameAndResults.clear();
		HashSet<String> playerNameSet = new HashSet<String>();
		for (OneGame result : results) {
			int playerCount = result.getPlayerCount();
			for (int i = 0; i < playerCount; i++) {
				playerNameSet.add(PlayerUIUtil.toCanonicalName(result
						.getPlayerName(i)));
			}
			gameAndResults.add(result);
		}
		Collections.sort(gameAndResults);

		canonicalPlayerNames = new String[playerNameSet.size()];
		playerNameSet.toArray(canonicalPlayerNames);

		reload(true);

		if (progressBar != null) {
			progressBar.setVisibility(View.GONE);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		reload(true);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}
}
