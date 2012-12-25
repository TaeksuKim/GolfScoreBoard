package org.dolicoli.android.golfscoreboard.db;

import org.dolicoli.android.golfscoreboard.data.settings.PlayerSetting;
import org.dolicoli.android.golfscoreboard.data.settings.Result;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class PlayerSettingDatabaseWorker extends AbstractDatabaseWorker {

	private static final String TAG = "PlayerSettingDatabaseWorker";

	private static final String TABLE = "playerSetting";

	private static final String COLUMN_KEY_INDEX = "keyIndex";

	private static final String COLUMN_NAME_1 = "name1";
	private static final String COLUMN_NAME_2 = "name2";
	private static final String COLUMN_NAME_3 = "name3";
	private static final String COLUMN_NAME_4 = "name4";
	private static final String COLUMN_NAME_5 = "name5";
	private static final String COLUMN_NAME_6 = "name6";

	private static final String COLUMN_HANDICAP_1 = "handicap1";
	private static final String COLUMN_HANDICAP_2 = "handicap2";
	private static final String COLUMN_HANDICAP_3 = "handicap3";
	private static final String COLUMN_HANDICAP_4 = "handicap4";
	private static final String COLUMN_HANDICAP_5 = "handicap5";
	private static final String COLUMN_HANDICAP_6 = "handicap6";

	private static final String COLUMN_EXTRA_SCORE_1 = "extra_score1";
	private static final String COLUMN_EXTRA_SCORE_2 = "extra_score2";
	private static final String COLUMN_EXTRA_SCORE_3 = "extra_score3";
	private static final String COLUMN_EXTRA_SCORE_4 = "extra_score4";
	private static final String COLUMN_EXTRA_SCORE_5 = "extra_score5";
	private static final String COLUMN_EXTRA_SCORE_6 = "extra_score6";

	// COLUMN_KEY_INDEX + " INTEGER PRIMARY KEY , " +
	// COLUMN_NAME_1 + " VARCHAR(25) , " +
	// COLUMN_NAME_2 + " VARCHAR(25) , " +
	// COLUMN_NAME_3 + " VARCHAR(25) , " +
	// COLUMN_NAME_4 + " VARCHAR(25) , " +
	// COLUMN_NAME_5 + " VARCHAR(25) , " +
	// COLUMN_NAME_6 + " VARCHAR(25) , " +
	// COLUMN_HANDICAP_1 + " INTEGER , " +
	// COLUMN_HANDICAP_2 + " INTEGER , " +
	// COLUMN_HANDICAP_3 + " INTEGER , " +
	// COLUMN_HANDICAP_4 + " INTEGER , " +
	// COLUMN_HANDICAP_5 + " INTEGER , " +
	// COLUMN_HANDICAP_6 + " INTEGER , " +
	// COLUMN_EXTRA_SCORE_1 + " INTEGER , " +
	// COLUMN_EXTRA_SCORE_2 + " INTEGER , " +
	// COLUMN_EXTRA_SCORE_3 + " INTEGER , " +
	// COLUMN_EXTRA_SCORE_4 + " INTEGER , " +
	// COLUMN_EXTRA_SCORE_5 + " INTEGER , " +
	// COLUMN_EXTRA_SCORE_6 + " INTEGER , " +

	private static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE
			+ " (" + COLUMN_KEY_INDEX + " INTEGER PRIMARY KEY , "
			+ COLUMN_NAME_1 + " VARCHAR(25) , " + COLUMN_NAME_2
			+ " VARCHAR(25) , " + COLUMN_NAME_3 + " VARCHAR(25) , "
			+ COLUMN_NAME_4 + " VARCHAR(25) , " + COLUMN_NAME_5
			+ " VARCHAR(25) , " + COLUMN_NAME_6 + " VARCHAR(25) , "
			+ COLUMN_HANDICAP_1 + " INTEGER , " + COLUMN_HANDICAP_2
			+ " INTEGER , " + COLUMN_HANDICAP_3 + " INTEGER , "
			+ COLUMN_HANDICAP_4 + " INTEGER , " + COLUMN_HANDICAP_5
			+ " INTEGER , " + COLUMN_HANDICAP_6 + " INTEGER, "
			+ COLUMN_EXTRA_SCORE_1 + " INTEGER , " + COLUMN_EXTRA_SCORE_2
			+ " INTEGER , " + COLUMN_EXTRA_SCORE_3 + " INTEGER , "
			+ COLUMN_EXTRA_SCORE_4 + " INTEGER , " + COLUMN_EXTRA_SCORE_5
			+ " INTEGER , " + COLUMN_EXTRA_SCORE_6 + " INTEGER " + " );";

	public static void createTable(SQLiteDatabase db) {
		Log.d(TAG, "createTable()");
		db.execSQL(DROP_TABLE_SQL);
		db.execSQL(CREATE_TABLE_SQL);

		updatePlayerSetting(db, new PlayerSetting());
	}

	private static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS "
			+ TABLE;

	public static void upgradeTable(SQLiteDatabase db, int oldVersion,
			int newVersion, Context context) {
		Log.d(TAG, "upgradeTable()");

		if (oldVersion >= 7 && oldVersion < 8) {
			String sql = "";
			sql += "CREATE TEMPORARY TABLE ps_backup (";
			sql += "	" + COLUMN_KEY_INDEX + ", ";
			sql += "	" + COLUMN_NAME_1 + ", ";
			sql += "	" + COLUMN_NAME_2 + ", ";
			sql += "	" + COLUMN_NAME_3 + ", ";
			sql += "	" + COLUMN_NAME_4 + ", ";
			sql += "	" + COLUMN_NAME_5 + ", ";
			sql += "	" + COLUMN_NAME_6 + ", ";
			sql += "	" + COLUMN_HANDICAP_1 + ", ";
			sql += "	" + COLUMN_HANDICAP_2 + ", ";
			sql += "	" + COLUMN_HANDICAP_3 + ", ";
			sql += "	" + COLUMN_HANDICAP_4 + ", ";
			sql += "	" + COLUMN_HANDICAP_5 + ", ";
			sql += "	" + COLUMN_HANDICAP_6 + ", ";
			sql += "	" + COLUMN_EXTRA_SCORE_1 + ", ";
			sql += "	" + COLUMN_EXTRA_SCORE_2 + ", ";
			sql += "	" + COLUMN_EXTRA_SCORE_3 + ", ";
			sql += "	" + COLUMN_EXTRA_SCORE_4 + ", ";
			sql += "	" + COLUMN_EXTRA_SCORE_5 + ", ";
			sql += "	" + COLUMN_EXTRA_SCORE_6 + " ";
			sql += "); ";
			db.execSQL(sql);
			sql = "";
			sql += "INSERT INTO ps_backup SELECT ";
			sql += "	" + COLUMN_KEY_INDEX + ", ";
			sql += "	" + COLUMN_NAME_1 + ", ";
			sql += "	" + COLUMN_NAME_2 + ", ";
			sql += "	" + COLUMN_NAME_3 + ", ";
			sql += "	" + COLUMN_NAME_4 + ", ";
			sql += "	" + COLUMN_NAME_5 + ", ";
			sql += "	" + COLUMN_NAME_6 + ", ";
			sql += "	" + COLUMN_HANDICAP_1 + ", ";
			sql += "	" + COLUMN_HANDICAP_2 + ", ";
			sql += "	" + COLUMN_HANDICAP_3 + ", ";
			sql += "	" + COLUMN_HANDICAP_4 + ", ";
			sql += "	" + COLUMN_HANDICAP_5 + ", ";
			sql += "	" + COLUMN_HANDICAP_6 + ", ";
			sql += "	" + 0 + ", ";
			sql += "	" + 0 + ", ";
			sql += "	" + 0 + ", ";
			sql += "	" + 0 + ", ";
			sql += "	" + 0 + ", ";
			sql += "	" + 0 + " ";
			sql += "FROM " + TABLE + "; ";
			db.execSQL(sql);
			sql = "";
			sql += "DROP TABLE " + TABLE + "; ";
			db.execSQL(sql);
			sql = "";
			sql += CREATE_TABLE_SQL;
			db.execSQL(sql);
			sql = "";
			sql += "INSERT INTO " + TABLE + " SELECT ";
			sql += "	" + COLUMN_KEY_INDEX + ", ";
			sql += "	" + COLUMN_NAME_1 + ", ";
			sql += "	" + COLUMN_NAME_2 + ", ";
			sql += "	" + COLUMN_NAME_3 + ", ";
			sql += "	" + COLUMN_NAME_4 + ", ";
			sql += "	" + COLUMN_NAME_5 + ", ";
			sql += "	" + COLUMN_NAME_6 + ", ";
			sql += "	" + COLUMN_HANDICAP_1 + ", ";
			sql += "	" + COLUMN_HANDICAP_2 + ", ";
			sql += "	" + COLUMN_HANDICAP_3 + ", ";
			sql += "	" + COLUMN_HANDICAP_4 + ", ";
			sql += "	" + COLUMN_HANDICAP_5 + ", ";
			sql += "	" + COLUMN_HANDICAP_6 + ", ";
			sql += "	" + COLUMN_EXTRA_SCORE_1 + ", ";
			sql += "	" + COLUMN_EXTRA_SCORE_2 + ", ";
			sql += "	" + COLUMN_EXTRA_SCORE_3 + ", ";
			sql += "	" + COLUMN_EXTRA_SCORE_4 + ", ";
			sql += "	" + COLUMN_EXTRA_SCORE_5 + ", ";
			sql += "	" + COLUMN_EXTRA_SCORE_6 + " ";
			sql += "FROM ps_backup; ";
			sql += "DROP TABLE ps_backup; ";
			db.execSQL(sql);
		}
	}

	public PlayerSettingDatabaseWorker(Context context) {
		super(context);
	}

	public void reset() {
		Log.d(TAG, "reset()");
	}

	public int cleanUpAllData() {
		Log.d(TAG, "cleanUpAllData()");

		open();

		int result = 0;
		try {
			result = mDb.delete(TABLE, null, null);
		} finally {
			close();
		}

		updatePlayerSetting(new PlayerSetting());
		return result;
	}

	public void getPlayerSetting(PlayerSetting setting) throws SQLException {
		open();
		try {
			getPlayerSetting(mDb, setting);
		} finally {
			close();
		}
	}

	public static void getPlayerSetting(SQLiteDatabase db, PlayerSetting setting)
			throws SQLException {
		Log.d(TAG, "getPlayerSetting()");
		Cursor cursor = null;
		try {
			cursor = db.query(true, TABLE, new String[] { COLUMN_NAME_1,
					COLUMN_NAME_2, COLUMN_NAME_3, COLUMN_NAME_4, COLUMN_NAME_5,
					COLUMN_NAME_6, COLUMN_HANDICAP_1, COLUMN_HANDICAP_2,
					COLUMN_HANDICAP_3, COLUMN_HANDICAP_4, COLUMN_HANDICAP_5,
					COLUMN_HANDICAP_6, COLUMN_EXTRA_SCORE_1,
					COLUMN_EXTRA_SCORE_2, COLUMN_EXTRA_SCORE_3,
					COLUMN_EXTRA_SCORE_4, COLUMN_EXTRA_SCORE_5,
					COLUMN_EXTRA_SCORE_6 }, null, null, null, null, null, null);

			if (cursor != null)
				cursor.moveToFirst();

			if (!cursor.isAfterLast()) {
				int offset = 0;

				String playerName1 = cursor.getString(offset++);
				String playerName2 = cursor.getString(offset++);
				String playerName3 = cursor.getString(offset++);
				String playerName4 = cursor.getString(offset++);
				String playerName5 = cursor.getString(offset++);
				String playerName6 = cursor.getString(offset++);
				int handicap1 = cursor.getInt(offset++);
				int handicap2 = cursor.getInt(offset++);
				int handicap3 = cursor.getInt(offset++);
				int handicap4 = cursor.getInt(offset++);
				int handicap5 = cursor.getInt(offset++);
				int handicap6 = cursor.getInt(offset++);
				int extraScore1 = cursor.getInt(offset++);
				int extraScore2 = cursor.getInt(offset++);
				int extraScore3 = cursor.getInt(offset++);
				int extraScore4 = cursor.getInt(offset++);
				int extraScore5 = cursor.getInt(offset++);
				int extraScore6 = cursor.getInt(offset++);

				setting.setPlayerName(0, playerName1);
				setting.setPlayerName(1, playerName2);
				setting.setPlayerName(2, playerName3);
				setting.setPlayerName(3, playerName4);
				setting.setPlayerName(4, playerName5);
				setting.setPlayerName(5, playerName6);
				setting.setHandicap(0, handicap1);
				setting.setHandicap(1, handicap2);
				setting.setHandicap(2, handicap3);
				setting.setHandicap(3, handicap4);
				setting.setHandicap(4, handicap5);
				setting.setHandicap(5, handicap6);
				setting.setExtraScore(0, extraScore1);
				setting.setExtraScore(1, extraScore2);
				setting.setExtraScore(2, extraScore3);
				setting.setExtraScore(3, extraScore4);
				setting.setExtraScore(4, extraScore5);
				setting.setExtraScore(5, extraScore6);

				cursor.moveToNext();
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public void updatePlayerSetting(PlayerSetting setting) {
		open();
		try {
			updatePlayerSetting(mDb, setting);
		} finally {
			close();
		}
	}

	private static void updatePlayerSetting(SQLiteDatabase db,
			PlayerSetting setting) {
		Log.d(TAG, "updatePlayerSetting()");

		ContentValues args = new ContentValues();

		args.put(COLUMN_KEY_INDEX, 0);

		args.put(COLUMN_NAME_1, setting.getPlayerName(0));
		args.put(COLUMN_NAME_2, setting.getPlayerName(1));
		args.put(COLUMN_NAME_3, setting.getPlayerName(2));
		args.put(COLUMN_NAME_4, setting.getPlayerName(3));
		args.put(COLUMN_NAME_5, setting.getPlayerName(4));
		args.put(COLUMN_NAME_6, setting.getPlayerName(5));

		args.put(COLUMN_HANDICAP_1, setting.getHandicap(0));
		args.put(COLUMN_HANDICAP_2, setting.getHandicap(1));
		args.put(COLUMN_HANDICAP_3, setting.getHandicap(2));
		args.put(COLUMN_HANDICAP_4, setting.getHandicap(3));
		args.put(COLUMN_HANDICAP_5, setting.getHandicap(4));
		args.put(COLUMN_HANDICAP_6, setting.getHandicap(5));

		args.put(COLUMN_EXTRA_SCORE_1, setting.getExtraScore(0));
		args.put(COLUMN_EXTRA_SCORE_2, setting.getExtraScore(1));
		args.put(COLUMN_EXTRA_SCORE_3, setting.getExtraScore(2));
		args.put(COLUMN_EXTRA_SCORE_4, setting.getExtraScore(3));
		args.put(COLUMN_EXTRA_SCORE_5, setting.getExtraScore(4));
		args.put(COLUMN_EXTRA_SCORE_6, setting.getExtraScore(5));

		db.replace(TABLE, null, args);
	}

	public void updateUsedHandicap(Result result) {
		open();
		try {
			updateUsedHandicap(mDb, result);
		} finally {
			close();
		}
	}

	public static void updateUsedHandicap(SQLiteDatabase db, Result result) {
		Log.d(TAG, "updateUsedHandicap()");

		PlayerSetting setting = new PlayerSetting();
		getPlayerSetting(db, setting);

		ContentValues args = new ContentValues();

		args.put(COLUMN_KEY_INDEX, 0);

		args.put(COLUMN_NAME_1, setting.getPlayerName(0));
		args.put(COLUMN_NAME_2, setting.getPlayerName(1));
		args.put(COLUMN_NAME_3, setting.getPlayerName(2));
		args.put(COLUMN_NAME_4, setting.getPlayerName(3));
		args.put(COLUMN_NAME_5, setting.getPlayerName(4));
		args.put(COLUMN_NAME_6, setting.getPlayerName(5));

		args.put(COLUMN_HANDICAP_1, setting.getHandicap(0));
		args.put(COLUMN_HANDICAP_2, setting.getHandicap(1));
		args.put(COLUMN_HANDICAP_3, setting.getHandicap(2));
		args.put(COLUMN_HANDICAP_4, setting.getHandicap(3));
		args.put(COLUMN_HANDICAP_5, setting.getHandicap(4));
		args.put(COLUMN_HANDICAP_6, setting.getHandicap(5));

		args.put(COLUMN_EXTRA_SCORE_1, setting.getExtraScore(0));
		args.put(COLUMN_EXTRA_SCORE_2, setting.getExtraScore(1));
		args.put(COLUMN_EXTRA_SCORE_3, setting.getExtraScore(2));
		args.put(COLUMN_EXTRA_SCORE_4, setting.getExtraScore(3));
		args.put(COLUMN_EXTRA_SCORE_5, setting.getExtraScore(4));
		args.put(COLUMN_EXTRA_SCORE_6, setting.getExtraScore(5));

		db.replace(TABLE, null, args);
	}
}
