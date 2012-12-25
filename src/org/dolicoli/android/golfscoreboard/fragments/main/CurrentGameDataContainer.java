package org.dolicoli.android.golfscoreboard.fragments.main;

import org.dolicoli.android.golfscoreboard.Reloadable;

public interface CurrentGameDataContainer extends Reloadable {
	void modifyGame();

	void addResult();

	void newGame();

	void showResetDialog();

	void showExportDataDialog();

	void importData();

	void saveHistory();

	void showSettingActivity();

	void importThreeMonthData();
}
