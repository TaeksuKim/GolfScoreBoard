package org.dolicoli.android.golfscoreboard.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DownloadTickDatabaseWorker extends AbstractDatabaseWorker {

	private static final String TAG = "DownloadDatabaseWorker";

	private static final String TABLE = "downloadTick";

	private static final String COLUMN_KEY = "key";
	private static final String COLUMN_TICK = "tick";

	// COLUMN_KEY + " INTEGER PRIMARY KEY , " +
	// COLUMN_TICK + " INTEGER DEFAULT 0, " +

	private static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE
			+ " (" + COLUMN_KEY + " INTEGER PRIMARY KEY , " + COLUMN_TICK
			+ " INTEGER DEFAULT 0 " + " ); ";

	public static void createTable(SQLiteDatabase db) {
		Log.d(TAG, "createTable()");
		db.execSQL(DROP_TABLE_SQL);
		db.execSQL(CREATE_TABLE_SQL);
	}

	private static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS "
			+ TABLE;

	public static void upgradeTable(SQLiteDatabase db, int oldVersion,
			int newVersion, Context context) {
		Log.d(TAG, "upgradeTable()");
		if (oldVersion < 9) {
			db.execSQL(CREATE_TABLE_SQL);
		}
	}

	public DownloadTickDatabaseWorker(Context context) {
		super(context);
	}

	public void reset() {
		Log.d(TAG, "reset()");
	}

	public int cleanUpAllData() {
		Log.d(TAG, "cleanUpAllData()");

		open();

		try {
			return cleanUpAllData(mDb);
		} finally {
			close();
		}
	}

	public static int cleanUpAllData(SQLiteDatabase db) {
		Log.d(TAG, "cleanUpAllData()");
		return db.delete(TABLE, null, null);
	}

	public void updateTick(long tick) {
		Log.d(TAG, "addTick()");

		open();

		try {
			mDb.beginTransaction();

			updateTick(mDb, tick);

			mDb.setTransactionSuccessful();
		} finally {
			mDb.endTransaction();
			close();
		}
	}

	public long getTick() throws SQLException {
		Log.d(TAG, "getTick()");

		open();

		long tick = 0L;
		Cursor cursor = null;
		try {
			cursor = mDb.query(true, TABLE, new String[] { COLUMN_TICK }, null,
					null, null, null, null, null);

			if (cursor != null)
				cursor.moveToFirst();

			if (!cursor.isAfterLast()) {
				int offset = 0;

				tick = cursor.getLong(offset++);

				cursor.moveToNext();
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}

			close();
		}

		return tick;
	}

	private static void updateTick(SQLiteDatabase db, long tick) {
		Log.d(TAG, "updateTick()");

		ContentValues args = new ContentValues();

		args.put(COLUMN_KEY, 1);
		args.put(COLUMN_TICK, tick);

		db.replace(TABLE, null, args);
	}
}
