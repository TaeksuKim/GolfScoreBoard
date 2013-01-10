package org.dolicoli.android.golfscoreboard.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String TAG = "org.dolicoli.android.golfscoreboard.db.DatabaseHelper";

	private static final String DATABASE_NAME = "datum.db";
	private static final int DATABASE_VERSION = 9;

	private Context context;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "Creating db from version " + DATABASE_VERSION);

		GameSettingDatabaseWorker.createTable(db);
		PlayerSettingDatabaseWorker.createTable(db);
		ResultDatabaseWorker.createTable(db);
		PlayerCacheDatabaseWorker.createTable(db);
		HistoryGameSettingDatabaseWorker.createTable(db);
		HistoryPlayerSettingDatabaseWorker.createTable(db);
		HistoryResultDatabaseWorker.createTable(db);
		DownloadTickDatabaseWorker.createTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "Upgrading db from version" + oldVersion + " to"
				+ newVersion + ", which will destroy all old data");

		GameSettingDatabaseWorker.upgradeTable(db, oldVersion, newVersion,
				context);
		PlayerSettingDatabaseWorker.upgradeTable(db, oldVersion, newVersion,
				context);
		ResultDatabaseWorker.upgradeTable(db, oldVersion, newVersion);
		PlayerCacheDatabaseWorker.upgradeTable(db, oldVersion, newVersion,
				context);
		HistoryGameSettingDatabaseWorker.upgradeTable(db, oldVersion,
				newVersion, context);
		HistoryPlayerSettingDatabaseWorker.upgradeTable(db, oldVersion,
				newVersion, context);
		HistoryResultDatabaseWorker.upgradeTable(db, oldVersion, newVersion);
		DownloadTickDatabaseWorker.upgradeTable(db, oldVersion, newVersion,
				context);
	}

	public static void cleanUpAllData(Context context) {
		{
			GameSettingDatabaseWorker worker = new GameSettingDatabaseWorker(
					context);
			worker.open();
			worker.cleanUpAllData();
			worker.close();
		}
		{
			PlayerSettingDatabaseWorker worker = new PlayerSettingDatabaseWorker(
					context);
			worker.open();
			worker.cleanUpAllData();
			worker.close();
		}
		{
			ResultDatabaseWorker worker = new ResultDatabaseWorker(context);
			worker.open();
			worker.cleanUpAllData();
			worker.close();
		}
		{
			PlayerCacheDatabaseWorker worker = new PlayerCacheDatabaseWorker(
					context);
			worker.open();
			worker.cleanUpAllData();
			worker.close();
		}
		{
			HistoryGameSettingDatabaseWorker worker = new HistoryGameSettingDatabaseWorker(
					context);
			worker.open();
			worker.cleanUpAllData();
			worker.close();
		}
		{
			HistoryPlayerSettingDatabaseWorker worker = new HistoryPlayerSettingDatabaseWorker(
					context);
			worker.open();
			worker.cleanUpAllData();
			worker.close();
		}
		{
			HistoryResultDatabaseWorker worker = new HistoryResultDatabaseWorker(
					context);
			worker.open();
			worker.cleanUpAllData();
			worker.close();
		}
		{
			DownloadTickDatabaseWorker worker = new DownloadTickDatabaseWorker(
					context);
			worker.open();
			worker.cleanUpAllData();
			worker.close();
		}
	}
}
