package org.dolicoli.android.golfscoreboard.db;

import java.util.Calendar;
import java.util.Date;

import org.dolicoli.android.golfscoreboard.data.PlayerCache;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class PlayerCacheDatabaseWorker extends AbstractDatabaseWorker {

	private static final String TAG = "PlayerCacheDatabaseWorker";

	private static final String TABLE = "playerCache";

	private static final String COLUMN_NAME = "name";
	private static final String COLUMN_HANDICAP = "handicap";
	private static final String COLUMN_DATE = "dateColumn";

	// COLUMN_NAME + " VARCHAR(25) PRIMARY KEY , " +
	// COLUMN_HANDICAP + " INTEGER , " +
	// COLUMN_DATE + " LONG , " +

	private static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE
			+ " (" + COLUMN_NAME + " VARCHAR(25) PRIMARY KEY , "
			+ COLUMN_HANDICAP + " INTEGER , " + COLUMN_DATE + " LONG " + " );";

	public static void createTable(SQLiteDatabase db) {
		Log.d(TAG, "createTable()");
		db.execSQL(DROP_TABLE_SQL);
		db.execSQL(CREATE_TABLE_SQL);
	}

	private static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS "
			+ TABLE;

	public static void upgradeTable(SQLiteDatabase db, int oldVersion,
			int newVersion, Context context) {
		Log.d(TAG, "upgradeTable(" + oldVersion + ", " + newVersion + ")");
		if (oldVersion < 4) {
			db.execSQL(CREATE_TABLE_SQL);
		}
	}

	public PlayerCacheDatabaseWorker(Context context) {
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

		return result;
	}

	public PlayerCache getCache() throws SQLException {
		open();
		try {
			return getCache(mDb);
		} finally {
			close();
		}
	}

	public static PlayerCache getCache(SQLiteDatabase db) throws SQLException {
		Log.d(TAG, "getCache()");
		Cursor cursor = null;
		try {
			cursor = db.query(true, TABLE, new String[] { COLUMN_NAME,
					COLUMN_HANDICAP }, null, null, null, null, COLUMN_NAME,
					null);

			PlayerCache cache = new PlayerCache();
			if (cursor == null) {
				return cache;
			}

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				int offset = 0;

				String playerName = cursor.getString(offset++);
				int handicap = cursor.getInt(offset++);
				cache.put(playerName, handicap);

				cursor.moveToNext();
			}
			return cache;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public void putPlayer(String name) {
		open();
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_YEAR, -1);
			mDb.delete(TABLE, COLUMN_DATE + " < ? ",
					new String[] { String.valueOf(calendar.getTimeInMillis()) });

			putPlayer(mDb, name);
		} finally {
			close();
		}
	}

	public void putPlayer(String[] names) {
		open();
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_YEAR, -1);
			mDb.delete(TABLE, COLUMN_DATE + " < ? ",
					new String[] { String.valueOf(calendar.getTimeInMillis()) });

			int length = names.length;
			for (int i = 0; i < length; i++) {
				putPlayer(mDb, names[i]);
			}
		} finally {
			close();
		}
	}

	public static void putPlayer(SQLiteDatabase db, String name) {
		Log.d(TAG, "putPlayer()");

		ContentValues args = new ContentValues();

		args.put(COLUMN_NAME, name);
		args.put(COLUMN_HANDICAP, 0);
		args.put(COLUMN_DATE, new Date().getTime());

		db.replace(TABLE, null, args);
	}
}
