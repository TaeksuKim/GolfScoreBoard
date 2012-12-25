package org.dolicoli.android.golfscoreboard;

import java.util.ArrayList;
import java.util.List;

import org.dolicoli.android.golfscoreboard.data.GameAndResult;
import org.dolicoli.android.golfscoreboard.db.HistoryGameSettingDatabaseWorker;
import org.dolicoli.android.golfscoreboard.fragments.DummySectionFragment;
import org.dolicoli.android.golfscoreboard.fragments.history.GameResultHistoryFragment;
import org.dolicoli.android.golfscoreboard.fragments.history.HistoryDataContainer;
import org.dolicoli.android.golfscoreboard.fragments.history.HistoryDataFragment;
import org.dolicoli.android.golfscoreboard.fragments.history.PlayerRankingFragment;
import org.dolicoli.android.golfscoreboard.tasks.GameAndResultTask;
import org.dolicoli.android.golfscoreboard.tasks.GameAndResultTask.GameAndResultTaskListener;
import org.dolicoli.android.golfscoreboard.utils.DateRangeUtil;
import org.dolicoli.android.golfscoreboard.utils.DateRangeUtil.DateRange;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

public class HistoryActivity extends FragmentActivity implements
		OnPageChangeListener, HistoryDataContainer, GameAndResultTaskListener,
		OnItemSelectedListener, OnNavigationListener {

	private static final int TAB_GAME_RESULT_HISTORY_FRAGMENT = 0;
	private static final int TAB_PLAYER_RANKING_FRAGMENT = 1;

	private static final int TAB_COUNT = TAB_PLAYER_RANKING_FRAGMENT + 1;

	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;

	private ArrayAdapter<DateItem> navigationAdapter;
	private ProgressBar progressBar;

	private ArrayList<GameAndResult> thisMonthResultList;
	private ArrayList<GameAndResult> lastMonthResultList;
	private ArrayList<GameAndResult> allResultList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		setContentView(R.layout.activity_history);

		thisMonthResultList = new ArrayList<GameAndResult>();
		lastMonthResultList = new ArrayList<GameAndResult>();
		allResultList = new ArrayList<GameAndResult>();

		mSectionsPagerAdapter = new SectionsPagerAdapter(this,
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOnPageChangeListener(this);

		navigationAdapter = new ArrayAdapter<DateItem>(this,
				android.R.layout.simple_list_item_1, getDateRangeItems());
		actionBar.setListNavigationCallbacks(navigationAdapter, this);
		actionBar.setTitle("");

		progressBar = (ProgressBar) findViewById(R.id.ProgressBar);

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
	public void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		reloadData();
	}

	public void reloadData() {
		GameAndResultTask task = new GameAndResultTask(this, this);
		task.execute(DateRangeUtil.getDateRange(3));

		reload();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_history, menu);
		return true;
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		GameResultHistoryFragment gameResultHistoryFragment = new GameResultHistoryFragment();
		PlayerRankingFragment playerRankingFragment = new PlayerRankingFragment();

		public SectionsPagerAdapter(HistoryDataContainer container,
				FragmentManager fm) {
			super(fm);

			gameResultHistoryFragment.setDataContainer(container);
			playerRankingFragment.setDataContainer(container);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case TAB_GAME_RESULT_HISTORY_FRAGMENT:
				return gameResultHistoryFragment;
			case TAB_PLAYER_RANKING_FRAGMENT:
				return playerRankingFragment;
			}
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
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
			case TAB_GAME_RESULT_HISTORY_FRAGMENT:
				return getString(R.string.title_section3).toUpperCase();
			case TAB_PLAYER_RANKING_FRAGMENT:
				return "통계".toUpperCase();
			}
			return null;
		}
	}

	@Override
	public void onPageScrollStateChanged(int position) {
	}

	@Override
	public void onPageScrolled(int position, float positionOffest,
			int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
		HistoryDataFragment fragment = null;
		int count = mSectionsPagerAdapter.getCount();
		for (int i = 0; i < count; i++) {
			fragment = (HistoryDataFragment) mSectionsPagerAdapter.getItem(i);
			if (fragment != null)
				fragment.hideActionMode();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.Settings:
			Intent settingsIntent = new Intent(this, SettingsActivity.class);
			startActivity(settingsIntent);
			return true;
		case android.R.id.home:
			setResult(Activity.RESULT_CANCELED);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private boolean reload() {
		int selectedIndex = getActionBar().getSelectedNavigationIndex();
		if (selectedIndex < 0
				|| selectedIndex > navigationAdapter.getCount() - 1)
			return false;

		HistoryDataFragment fragment = null;
		for (int i = 0; i < TAB_COUNT; i++) {
			fragment = (HistoryDataFragment) mSectionsPagerAdapter.getItem(i);
			if (fragment != null)
				fragment.reload(selectedIndex);
		}

		return true;
	}

	private ArrayList<DateItem> getDateRangeItems() {
		ArrayList<DateItem> items = new ArrayList<DateItem>();

		DateRange dummyMonthRange = DateRangeUtil.getMonthlyDateRange(0);
		items.add(new DateItem(dummyMonthRange.getFrom(), dummyMonthRange
				.getTo(), "최근 5 경기"));

		DateRange thisMonthRange = DateRangeUtil.getMonthlyDateRange(0);
		items.add(new DateItem(thisMonthRange.getFrom(),
				thisMonthRange.getTo(), "이번 달"));

		DateRange lastMonthRange = DateRangeUtil.getMonthlyDateRange(1);
		items.add(new DateItem(lastMonthRange.getFrom(),
				lastMonthRange.getTo(), "지난 달"));

		DateRange lastThreeMonthRange = DateRangeUtil.getDateRange(3);
		items.add(new DateItem(lastThreeMonthRange.getFrom(),
				lastThreeMonthRange.getTo(), "최근 3개월"));

		return items;
	}

	private static class DateItem {
		@SuppressWarnings("unused")
		private long from, to;
		private String title;

		public DateItem(long from, long to, String title) {
			this.from = from;
			this.to = to;
			this.title = title;
		}

		@Override
		public String toString() {
			return title;
		}
	}

	@Override
	public ArrayList<GameAndResult> getThisMonthGameAndResults() {
		return thisMonthResultList;
	}

	@Override
	public ArrayList<GameAndResult> getLastMonthGameAndResults() {
		return lastMonthResultList;
	}

	@Override
	public ArrayList<GameAndResult> getAllGameAndResults() {
		return allResultList;
	}

	@Override
	public void onGameAndResultStarted() {
		if (progressBar != null)
			progressBar.setVisibility(View.VISIBLE);
	}

	@Override
	public void onGameAndResultFinished(GameAndResult[] results) {
		DateRange thisMonthRange = DateRangeUtil.getMonthlyDateRange(0);
		DateRange lastMonthRange = DateRangeUtil.getMonthlyDateRange(1);
		thisMonthResultList.clear();
		lastMonthResultList.clear();
		allResultList.clear();
		for (GameAndResult result : results) {
			long gameDateTime = result.getGameSetting().getDate().getTime();
			if (thisMonthRange.getFrom() <= gameDateTime
					&& thisMonthRange.getTo() >= gameDateTime) {
				thisMonthResultList.add(result);
			} else if (lastMonthRange.getFrom() <= gameDateTime
					&& lastMonthRange.getTo() >= gameDateTime) {
				lastMonthResultList.add(result);
			}

			allResultList.add(result);
		}

		if (progressBar != null)
			progressBar.setVisibility(View.GONE);

		if (mSectionsPagerAdapter == null)
			return;

		int selectedIndex = getActionBar().getSelectedNavigationIndex();
		if (selectedIndex < 0
				|| selectedIndex > navigationAdapter.getCount() - 1)
			selectedIndex = 0;

		int count = mSectionsPagerAdapter.getCount();
		HistoryDataFragment fragment = null;
		for (int i = 0; i < count; i++) {
			fragment = (HistoryDataFragment) mSectionsPagerAdapter.getItem(i);
			if (fragment != null)
				fragment.reload(selectedIndex);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		reload();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		if (itemPosition < 0 || itemPosition > navigationAdapter.getCount() - 1)
			return false;

		return reload();
	}

	public void deleteHistory(final List<String> playDates) {
		final HistoryGameSettingDatabaseWorker gameSettingWorker = new HistoryGameSettingDatabaseWorker(
				this);

		final int selectionCount = playDates.size();
		if (selectionCount > 0) {
			new AlertDialog.Builder(this)
					.setTitle(R.string.activity_result_history_delete)
					.setMessage(
							getString(
									R.string.activity_result_history_are_you_sure_to_delete,
									selectionCount))
					.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									gameSettingWorker.clearHistories(playDates);
									reloadData();
								}

							}).setNegativeButton(android.R.string.no, null)
					.show();
		}
	}
}
