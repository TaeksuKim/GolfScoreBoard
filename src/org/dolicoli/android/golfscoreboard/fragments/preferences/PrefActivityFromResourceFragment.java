package org.dolicoli.android.golfscoreboard.fragments.preferences;

import org.dolicoli.android.golfscoreboard.GolfScoreBoardApplication;
import org.dolicoli.android.golfscoreboard.R;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

public class PrefActivityFromResourceFragment extends PreferenceFragment
		implements OnSharedPreferenceChangeListener {

	private SharedPreferences mainPreference;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		addPreferencesFromResource(R.xml.settings);

		mainPreference = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		mainPreference.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onDestroy() {
		if (mainPreference != null)
			mainPreference.registerOnSharedPreferenceChangeListener(this);
		super.onDestroy();
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {

		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(GolfScoreBoardApplication.PK_THEME)) {
		}
	}
}