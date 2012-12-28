package org.dolicoli.android.golfscoreboard.fragments.main;

import android.view.MenuItem;

public interface MainFragmentContainer {
	void showModifyGameSettingActivity();

	boolean onOptionsItemSelected(MenuItem item);

	void reload();
}
