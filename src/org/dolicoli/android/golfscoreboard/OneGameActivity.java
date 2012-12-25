package org.dolicoli.android.golfscoreboard;

import java.util.ArrayList;
import java.util.Date;

import org.dolicoli.android.golfscoreboard.data.settings.GameSetting;
import org.dolicoli.android.golfscoreboard.data.settings.Result;
import org.dolicoli.android.golfscoreboard.db.HistoryResultDatabaseWorker;
import org.dolicoli.android.golfscoreboard.fragments.DummySectionFragment;
import org.dolicoli.android.golfscoreboard.fragments.onegame.OneGameActivityPage;
import org.dolicoli.android.golfscoreboard.fragments.onegame.OneGameGameSettingFragment;
import org.dolicoli.android.golfscoreboard.fragments.onegame.OneGameHoleResultFragment;
import org.dolicoli.android.golfscoreboard.fragments.onegame.OneGameSummaryFragment;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class OneGameActivity extends FragmentActivity implements
		OnNavigationListener, OnClickListener {

	public static final String IK_PLAY_DATE = "PLAY_DATE";
	public static final String IK_DATE = "DATE";

	private static final int TAB_SUMMARY_FRAGMENT = 0;
	private static final int TAB_HOLE_RESULT_FRAGMENT = 1;
	private static final int TAB_GAME_SETTING_FRAGMENT = 2;

	private static final int TAB_COUNT = TAB_GAME_SETTING_FRAGMENT + 1;

	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;

	private ArrayAdapter<Hole> navigationAdapter;

	private Button prevButton, nextButton;
	private int maxHoleNumber = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.activity_onegame);

		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		TextView dateTextView = (TextView) findViewById(R.id.DateTextView);
		prevButton = (Button) findViewById(R.id.PrevButton);
		nextButton = (Button) findViewById(R.id.NextButton);

		prevButton.setOnClickListener(this);
		nextButton.setOnClickListener(this);

		Intent intent = getIntent();
		Date date = (Date) intent.getSerializableExtra(OneGameActivity.IK_DATE);
		if (date != null) {
			String dateString = DateUtils.formatDateTime(this, date.getTime(),
					DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
							| DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_ABBREV_WEEKDAY
							| DateUtils.FORMAT_SHOW_WEEKDAY
							| DateUtils.FORMAT_12HOUR);
			dateTextView.setText(dateString);

			String playDate = GameSetting.toGameIdFormat(date);
			HistoryResultDatabaseWorker resultWorker = new HistoryResultDatabaseWorker(
					this);
			ArrayList<Result> results = resultWorker.getResults(playDate);
			maxHoleNumber = 0;
			for (Result result : results) {
				int holeNumber = result.getHoleNumber();
				if (holeNumber > maxHoleNumber) {
					maxHoleNumber = holeNumber;
				}
			}

			ArrayList<Hole> holeList = new ArrayList<Hole>();
			for (int i = 1; i <= maxHoleNumber; i++) {
				holeList.add(new Hole(i));
			}

			navigationAdapter = new ArrayAdapter<Hole>(this,
					android.R.layout.simple_list_item_1, holeList);
			actionBar.setListNavigationCallbacks(navigationAdapter, this);
			actionBar.setTitle("");
			actionBar.setSelectedNavigationItem(maxHoleNumber - 1);
		} else {
			ArrayList<Hole> holeList = new ArrayList<Hole>();
			holeList.add(new Hole(0));

			navigationAdapter = new ArrayAdapter<Hole>(this,
					android.R.layout.simple_list_item_1, holeList);
			actionBar.setListNavigationCallbacks(navigationAdapter, this);
			actionBar.setTitle("");
		}

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
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		if (mSectionsPagerAdapter == null)
			return false;

		if (itemPosition < 0 || itemPosition > navigationAdapter.getCount() - 1)
			return false;

		Hole hole = navigationAdapter.getItem(itemPosition);
		setHoleNumber(hole.holeNumber);

		setUIState();

		return true;
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
	public void onClick(View view) {
		final int id = view.getId();

		ActionBar actionBar = getActionBar();
		switch (id) {
		case R.id.PrevButton: {
			int selection = actionBar.getSelectedNavigationIndex();
			actionBar.setSelectedNavigationItem(selection - 1);
			return;
		}
		case R.id.NextButton: {
			int selection = actionBar.getSelectedNavigationIndex();
			actionBar.setSelectedNavigationItem(selection + 1);
			return;
		}
		}
	}

	private void setUIState() {
		if (prevButton == null)
			return;

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				int selection = getActionBar().getSelectedNavigationIndex();
				Hole hole = navigationAdapter.getItem(selection);
				if (hole.holeNumber <= 1) {
					prevButton.setEnabled(false);
				} else {
					prevButton.setEnabled(true);
				}
				if (hole.holeNumber >= maxHoleNumber) {
					nextButton.setEnabled(false);
				} else {
					nextButton.setEnabled(true);
				}
			}
		});
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		OneGameSummaryFragment summaryFragment = new OneGameSummaryFragment();
		OneGameHoleResultFragment holeResultFragment = new OneGameHoleResultFragment();
		OneGameGameSettingFragment gameSettingFragment = new OneGameGameSettingFragment();

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			switch (i) {
			case TAB_SUMMARY_FRAGMENT:
				return summaryFragment;
			case TAB_HOLE_RESULT_FRAGMENT:
				return holeResultFragment;
			case TAB_GAME_SETTING_FRAGMENT:
				return gameSettingFragment;
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
				return getString(R.string.activity_history_title_section1)
						.toUpperCase();
			case TAB_HOLE_RESULT_FRAGMENT:
				return getString(R.string.activity_history_title_section2)
						.toUpperCase();
			case TAB_GAME_SETTING_FRAGMENT:
				return getString(R.string.activity_history_title_section3)
						.toUpperCase();
			}
			return null;
		}
	}

	private void setHoleNumber(int holeNumber) {
		if (mSectionsPagerAdapter == null)
			return;

		for (int i = 0; i < TAB_COUNT; i++) {
			OneGameActivityPage fragment = (OneGameActivityPage) mSectionsPagerAdapter
					.getItem(i);
			if (fragment != null) {
				fragment.setHoleNumber(holeNumber);
			}
		}
	}

	private static class Hole {
		int holeNumber;

		public Hole(int holeNumber) {
			this.holeNumber = holeNumber;
		}

		public String toString() {
			if (holeNumber < 1)
				return "-";

			return "Hole " + holeNumber;
		}
	}
}
