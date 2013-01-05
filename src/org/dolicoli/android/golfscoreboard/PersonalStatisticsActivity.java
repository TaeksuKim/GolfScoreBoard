package org.dolicoli.android.golfscoreboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import org.dolicoli.android.golfscoreboard.data.GameAndResult;
import org.dolicoli.android.golfscoreboard.fragments.DummySectionFragment;
import org.dolicoli.android.golfscoreboard.fragments.statistics.PersonalStatisticsDataContainer;
import org.dolicoli.android.golfscoreboard.fragments.statistics.PersonalStatisticsGameResultListFragment;
import org.dolicoli.android.golfscoreboard.fragments.statistics.PersonalStatisticsSummaryFragment;
import org.dolicoli.android.golfscoreboard.tasks.GameAndResultTask;
import org.dolicoli.android.golfscoreboard.tasks.GameAndResultTask.GameAndResultTaskListener;
import org.dolicoli.android.golfscoreboard.utils.DateRangeUtil;
import org.dolicoli.android.golfscoreboard.utils.DateRangeUtil.DateRange;
import org.dolicoli.android.golfscoreboard.utils.PlayerUIUtil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class PersonalStatisticsActivity extends FragmentActivity implements
		PersonalStatisticsDataContainer, GameAndResultTaskListener {

	@SuppressWarnings("unused")
	private static final String TAG = "PersonalStatisticsActivity";

	private static final int TAB_SUMMARY_FRAGMENT = 0;
	private static final int TAB_GAME_RESULT_LIST_FRAGMENT = 1;

	private static final int TAB_COUNT = TAB_GAME_RESULT_LIST_FRAGMENT + 1;

	public static final String IK_PLAYER_NAME = "PLAYER_NAME";

	private String playerName;
	private int playerImageResourceId;
	private ArrayList<GameAndResult> gameAndResults;

	private ImageView playerImageView;
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	private ProgressBar progressBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.activity_personal_statistics);

		playerImageView = (ImageView) findViewById(R.id.PlayerImageView);
		progressBar = (ProgressBar) findViewById(R.id.ProgressBar);

		mSectionsPagerAdapter = new SectionsPagerAdapter(this,
				getSupportFragmentManager());

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		gameAndResults = new ArrayList<GameAndResult>();

		DateRange dateRange = DateRangeUtil.getDateRange(2);
		GameAndResultTask task = new GameAndResultTask(this, this);
		task.execute(dateRange);

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
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		Intent intent = getIntent();
		if (intent != null) {
			playerName = intent
					.getStringExtra(PersonalStatisticsActivity.IK_PLAYER_NAME);
			playerImageResourceId = PlayerUIUtil.getResourceId(playerName);
			playerImageView.setImageResource(playerImageResourceId);

			getActionBar().setTitle(playerName);
		}
	}

	@Override
	public String getPlayerName() {
		return playerName;
	}

	@Override
	public int getPlayerImageResourceId() {
		return playerImageResourceId;
	}

	@Override
	public ArrayList<GameAndResult> getGameAndResults() {
		return gameAndResults;
	}

	@Override
	public void onGameAndResultStarted() {
		if (progressBar != null) {
			progressBar.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onGameAndResultFinished(GameAndResult[] results) {
		gameAndResults.clear();
		for (GameAndResult result : results) {
			gameAndResults.add(result);
		}
		Collections.sort(gameAndResults);

		fireDataChange();
		if (progressBar != null) {
			progressBar.setVisibility(View.GONE);
		}
	}

	private void fireDataChange() {
		int itemCount = mSectionsPagerAdapter.getCount();
		for (int i = 0; i < itemCount; i++) {
			Fragment item = mSectionsPagerAdapter.getItem(i);
			if (item != null && (item instanceof Reloadable))
				((Reloadable) item).reload(false);
		}
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		PersonalStatisticsSummaryFragment summaryFragment = new PersonalStatisticsSummaryFragment();
		PersonalStatisticsGameResultListFragment gameResultListFragment = new PersonalStatisticsGameResultListFragment();

		public SectionsPagerAdapter(PersonalStatisticsDataContainer container,
				FragmentManager fm) {
			super(fm);

			summaryFragment.setDataContainer(container);
			gameResultListFragment.setDataContainer(container);
		}

		@Override
		public Fragment getItem(int i) {
			switch (i) {
			case TAB_SUMMARY_FRAGMENT:
				return summaryFragment;
			case TAB_GAME_RESULT_LIST_FRAGMENT:
				return gameResultListFragment;
			}
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, i + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			return TAB_COUNT;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case TAB_SUMMARY_FRAGMENT:
				return getString(
						R.string.activity_personal_statistics_fragment_summary)
						.toUpperCase(Locale.US);
			case TAB_GAME_RESULT_LIST_FRAGMENT:
				return getString(
						R.string.activity_personal_statistics_fragment_game_result_list)
						.toUpperCase(Locale.US);
			}
			return null;
		}
	}
}
