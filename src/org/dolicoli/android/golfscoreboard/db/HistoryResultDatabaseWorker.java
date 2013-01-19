package org.dolicoli.android.golfscoreboard.db;

import java.util.ArrayList;

import org.dolicoli.android.golfscoreboard.Constants;
import org.dolicoli.android.golfscoreboard.data.settings.Result;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class HistoryResultDatabaseWorker extends AbstractDatabaseWorker {

	private static final String TAG = "HistoryResultDatabaseWorker";

	public static final String TABLE = "historyResult";

	public static final String COLUMN_PLAY_DATE = "playDate";

	public static final String COLUMN_HOLE_NUMBER = "holeNumber";
	public static final String COLUMN_PAR_NUMBER = "parNumber";
	public static final String COLUMN_PLAYER_1_SCORE = "player1Score";
	public static final String COLUMN_PLAYER_2_SCORE = "player2Score";
	public static final String COLUMN_PLAYER_3_SCORE = "player3Score";
	public static final String COLUMN_PLAYER_4_SCORE = "player4Score";
	public static final String COLUMN_PLAYER_5_SCORE = "player5Score";
	public static final String COLUMN_PLAYER_6_SCORE = "player6Score";
	public static final String COLUMN_PLAYER_1_HANDICAP_USED = "player1HandicapUsed";
	public static final String COLUMN_PLAYER_2_HANDICAP_USED = "player2HandicapUsed";
	public static final String COLUMN_PLAYER_3_HANDICAP_USED = "player3HandicapUsed";
	public static final String COLUMN_PLAYER_4_HANDICAP_USED = "player4HandicapUsed";
	public static final String COLUMN_PLAYER_5_HANDICAP_USED = "player5HandicapUsed";
	public static final String COLUMN_PLAYER_6_HANDICAP_USED = "player6HandicapUsed";

	// COLUMN_PLAY_DATE + " VARCHAR(12) , " +
	// COLUMN_HOLE_NUMBER + " INTEGER  , " +
	// COLUMN_PAR_NUMBER + " INTEGER , " +
	// COLUMN_PLAYER_1_SCORE + " INTEGER , " +
	// COLUMN_PLAYER_2_SCORE + " INTEGER , " +
	// COLUMN_PLAYER_3_SCORE + " INTEGER , " +
	// COLUMN_PLAYER_4_SCORE + " INTEGER , " +
	// COLUMN_PLAYER_5_SCORE + " INTEGER , " +
	// COLUMN_PLAYER_6_SCORE + " INTEGER , " +
	// COLUMN_PLAYER_1_HANDICAP_USED + " INTEGER , " +
	// COLUMN_PLAYER_2_HANDICAP_USED + " INTEGER , " +
	// COLUMN_PLAYER_3_HANDICAP_USED + " INTEGER , " +
	// COLUMN_PLAYER_4_HANDICAP_USED + " INTEGER , " +
	// COLUMN_PLAYER_5_HANDICAP_USED + " INTEGER , " +
	// COLUMN_PLAYER_6_HANDICAP_USED + " INTEGER , " +
	// "PRIMARY KEY (" + COLUMN_PLAY_DATE + ", " + COLUMN_HOLE_NUMBER + ") " +

	private static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE
			+ " (" + COLUMN_PLAY_DATE + " VARCHAR(12) , " + COLUMN_HOLE_NUMBER
			+ " INTEGER  , " + COLUMN_PAR_NUMBER + " INTEGER , "
			+ COLUMN_PLAYER_1_SCORE + " INTEGER , " + COLUMN_PLAYER_2_SCORE
			+ " INTEGER , " + COLUMN_PLAYER_3_SCORE + " INTEGER , "
			+ COLUMN_PLAYER_4_SCORE + " INTEGER , " + COLUMN_PLAYER_5_SCORE
			+ " INTEGER , " + COLUMN_PLAYER_6_SCORE + " INTEGER , "
			+ COLUMN_PLAYER_1_HANDICAP_USED + " INTEGER , "
			+ COLUMN_PLAYER_2_HANDICAP_USED + " INTEGER , "
			+ COLUMN_PLAYER_3_HANDICAP_USED + " INTEGER , "
			+ COLUMN_PLAYER_4_HANDICAP_USED + " INTEGER , "
			+ COLUMN_PLAYER_5_HANDICAP_USED + " INTEGER , "
			+ COLUMN_PLAYER_6_HANDICAP_USED + " INTEGER , " + "PRIMARY KEY ("
			+ COLUMN_PLAY_DATE + ", " + COLUMN_HOLE_NUMBER + ") " + " );";

	public static void createTable(SQLiteDatabase db) {
		Log.d(TAG, "createTable()");
		db.execSQL(DROP_TABLE_SQL);
		db.execSQL(CREATE_TABLE_SQL);
	}

	private static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS "
			+ TABLE;

	public static void upgradeTable(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		Log.d(TAG, "upgradeTable(" + oldVersion + ", " + newVersion + ")");
		if (oldVersion < 6) {
			db.execSQL(CREATE_TABLE_SQL);
		}
		if (oldVersion >= 6 && oldVersion < 7) {
			String sql = "";
			sql += "CREATE TEMPORARY TABLE hrs_backup (";
			sql += "	" + COLUMN_PLAY_DATE + ", ";
			sql += "	" + COLUMN_HOLE_NUMBER + ", ";
			sql += "	" + COLUMN_PAR_NUMBER + ", ";
			sql += "	" + COLUMN_PLAYER_1_SCORE + ", ";
			sql += "	" + COLUMN_PLAYER_2_SCORE + ", ";
			sql += "	" + COLUMN_PLAYER_3_SCORE + ", ";
			sql += "	" + COLUMN_PLAYER_4_SCORE + ", ";
			sql += "	" + COLUMN_PLAYER_5_SCORE + ", ";
			sql += "	" + COLUMN_PLAYER_6_SCORE + ", ";
			sql += "	" + COLUMN_PLAYER_1_HANDICAP_USED + ", ";
			sql += "	" + COLUMN_PLAYER_2_HANDICAP_USED + ", ";
			sql += "	" + COLUMN_PLAYER_3_HANDICAP_USED + ", ";
			sql += "	" + COLUMN_PLAYER_4_HANDICAP_USED + ", ";
			sql += "	" + COLUMN_PLAYER_5_HANDICAP_USED + ", ";
			sql += "	" + COLUMN_PLAYER_6_HANDICAP_USED + " ";
			sql += "); ";
			db.execSQL(sql);
			sql = "";
			sql += "INSERT INTO hrs_backup SELECT ";
			sql += "	(" + COLUMN_PLAY_DATE + "||'00'), ";
			sql += "	" + COLUMN_HOLE_NUMBER + ", ";
			sql += "	" + COLUMN_PAR_NUMBER + ", ";
			sql += "	" + COLUMN_PLAYER_1_SCORE + ", ";
			sql += "	" + COLUMN_PLAYER_2_SCORE + ", ";
			sql += "	" + COLUMN_PLAYER_3_SCORE + ", ";
			sql += "	" + COLUMN_PLAYER_4_SCORE + ", ";
			sql += "	" + COLUMN_PLAYER_5_SCORE + ", ";
			sql += "	" + COLUMN_PLAYER_6_SCORE + ", ";
			sql += "	" + COLUMN_PLAYER_1_HANDICAP_USED + ", ";
			sql += "	" + COLUMN_PLAYER_2_HANDICAP_USED + ", ";
			sql += "	" + COLUMN_PLAYER_3_HANDICAP_USED + ", ";
			sql += "	" + COLUMN_PLAYER_4_HANDICAP_USED + ", ";
			sql += "	" + COLUMN_PLAYER_5_HANDICAP_USED + ", ";
			sql += "	" + COLUMN_PLAYER_6_HANDICAP_USED + " ";
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
			sql += "	" + COLUMN_PLAY_DATE + ", ";
			sql += "	" + COLUMN_HOLE_NUMBER + ", ";
			sql += "	" + COLUMN_PAR_NUMBER + ", ";
			sql += "	" + COLUMN_PLAYER_1_SCORE + ", ";
			sql += "	" + COLUMN_PLAYER_2_SCORE + ", ";
			sql += "	" + COLUMN_PLAYER_3_SCORE + ", ";
			sql += "	" + COLUMN_PLAYER_4_SCORE + ", ";
			sql += "	" + COLUMN_PLAYER_5_SCORE + ", ";
			sql += "	" + COLUMN_PLAYER_6_SCORE + ", ";
			sql += "	" + COLUMN_PLAYER_1_HANDICAP_USED + ", ";
			sql += "	" + COLUMN_PLAYER_2_HANDICAP_USED + ", ";
			sql += "	" + COLUMN_PLAYER_3_HANDICAP_USED + ", ";
			sql += "	" + COLUMN_PLAYER_4_HANDICAP_USED + ", ";
			sql += "	" + COLUMN_PLAYER_5_HANDICAP_USED + ", ";
			sql += "	" + COLUMN_PLAYER_6_HANDICAP_USED + " ";
			sql += "FROM hrs_backup; ";
			db.execSQL(sql);
			sql = "";
			sql += "DROP TABLE hrs_backup; ";
			db.execSQL(sql);
		}

	}

	public HistoryResultDatabaseWorker(Context context) {
		super(context);
	}

	public void reset() {
		Log.d(TAG, "reset()");
		cleanUpAllData();
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

	public ArrayList<Result> getResults(String playDate) {
		Log.d(TAG, "getResults()");
		open();
		Cursor cursor = null;
		try {
			cursor = mDb.query(TABLE, new String[] { COLUMN_HOLE_NUMBER,
					COLUMN_PAR_NUMBER, COLUMN_PLAYER_1_SCORE,
					COLUMN_PLAYER_2_SCORE, COLUMN_PLAYER_3_SCORE,
					COLUMN_PLAYER_4_SCORE, COLUMN_PLAYER_5_SCORE,
					COLUMN_PLAYER_6_SCORE, COLUMN_PLAYER_1_HANDICAP_USED,
					COLUMN_PLAYER_2_HANDICAP_USED,
					COLUMN_PLAYER_3_HANDICAP_USED,
					COLUMN_PLAYER_4_HANDICAP_USED,
					COLUMN_PLAYER_5_HANDICAP_USED,
					COLUMN_PLAYER_6_HANDICAP_USED },
					COLUMN_PLAY_DATE + " = ? ", new String[] { playDate },
					null, null, COLUMN_HOLE_NUMBER, null);

			if (cursor != null)
				cursor.moveToFirst();

			ArrayList<Result> list = new ArrayList<Result>();
			while (!cursor.isAfterLast()) {
				int offset = 0;

				int holeNumber = cursor.getInt(offset++);
				int parNumber = cursor.getInt(offset++);
				int player1Score = cursor.getInt(offset++);
				int player2Score = cursor.getInt(offset++);
				int player3Score = cursor.getInt(offset++);
				int player4Score = cursor.getInt(offset++);
				int player5Score = cursor.getInt(offset++);
				int player6Score = cursor.getInt(offset++);
				int player1HandicapUsed = cursor.getInt(offset++);
				int player2HandicapUsed = cursor.getInt(offset++);
				int player3HandicapUsed = cursor.getInt(offset++);
				int player4HandicapUsed = cursor.getInt(offset++);
				int player5HandicapUsed = cursor.getInt(offset++);
				int player6HandicapUsed = cursor.getInt(offset++);

				Result result = new Result(holeNumber, parNumber);
				result.setScore(0, player1Score);
				result.setScore(1, player2Score);
				result.setScore(2, player3Score);
				result.setScore(3, player4Score);
				result.setScore(4, player5Score);
				result.setScore(5, player6Score);
				result.setUsedHandicap(0, player1HandicapUsed);
				result.setUsedHandicap(1, player2HandicapUsed);
				result.setUsedHandicap(2, player3HandicapUsed);
				result.setUsedHandicap(3, player4HandicapUsed);
				result.setUsedHandicap(4, player5HandicapUsed);
				result.setUsedHandicap(5, player6HandicapUsed);
				result.calculate();

				list.add(result);

				cursor.moveToNext();
			}

			return list;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			close();
		}
	}

	public int[] getUsedHandicaps(String playDate) {
		Log.d(TAG, "getUsedHandicaps()");
		open();
		Cursor cursor = null;
		try {
			cursor = mDb.query(TABLE, new String[] {
					"SUM(" + COLUMN_PLAYER_1_HANDICAP_USED + ") ",
					"SUM(" + COLUMN_PLAYER_2_HANDICAP_USED + ") ",
					"SUM(" + COLUMN_PLAYER_3_HANDICAP_USED + ") ",
					"SUM(" + COLUMN_PLAYER_4_HANDICAP_USED + ") ",
					"SUM(" + COLUMN_PLAYER_5_HANDICAP_USED + ") ",
					"SUM(" + COLUMN_PLAYER_6_HANDICAP_USED + ") " },
					COLUMN_PLAY_DATE + " = ? ", new String[] { playDate },
					null, null, null, null);

			if (cursor != null)
				cursor.moveToFirst();

			int[] usedHandicaps = new int[Constants.MAX_PLAYER_COUNT];
			if (!cursor.isAfterLast()) {
				int offset = 0;

				int used1 = cursor.getInt(offset++);
				int used2 = cursor.getInt(offset++);
				int used3 = cursor.getInt(offset++);
				int used4 = cursor.getInt(offset++);
				int used5 = cursor.getInt(offset++);
				int used6 = cursor.getInt(offset++);

				usedHandicaps[0] = used1;
				usedHandicaps[1] = used2;
				usedHandicaps[2] = used3;
				usedHandicaps[3] = used4;
				usedHandicaps[4] = used5;
				usedHandicaps[5] = used6;

				// Log.d(TAG, "1: " + used1 + ", 2:" + used2 + ", 3:" + used3
				// + ", 4:" + used4 + ", 5:" + used5 + ", 6:" + used6);

				cursor.moveToNext();
			}

			return usedHandicaps;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			close();
		}
	}

	static boolean updateResult(SQLiteDatabase db, String playDate,
			Result result) {
		Log.d(TAG, "updateResult()");
		ContentValues args = new ContentValues();

		args.put(COLUMN_PLAY_DATE, playDate);
		args.put(COLUMN_HOLE_NUMBER, result.getHoleNumber());
		args.put(COLUMN_PAR_NUMBER, result.getParNumber());
		args.put(COLUMN_PLAYER_1_SCORE, result.getOriginalScore(0));
		args.put(COLUMN_PLAYER_2_SCORE, result.getOriginalScore(1));
		args.put(COLUMN_PLAYER_3_SCORE, result.getOriginalScore(2));
		args.put(COLUMN_PLAYER_4_SCORE, result.getOriginalScore(3));
		args.put(COLUMN_PLAYER_5_SCORE, result.getOriginalScore(4));
		args.put(COLUMN_PLAYER_6_SCORE, result.getOriginalScore(5));
		args.put(COLUMN_PLAYER_1_HANDICAP_USED, result.getUsedHandicap(0));
		args.put(COLUMN_PLAYER_2_HANDICAP_USED, result.getUsedHandicap(1));
		args.put(COLUMN_PLAYER_3_HANDICAP_USED, result.getUsedHandicap(2));
		args.put(COLUMN_PLAYER_4_HANDICAP_USED, result.getUsedHandicap(3));
		args.put(COLUMN_PLAYER_5_HANDICAP_USED, result.getUsedHandicap(4));
		args.put(COLUMN_PLAYER_6_HANDICAP_USED, result.getUsedHandicap(5));

		long dbResult = db.replace(TABLE, null, args);
		return (dbResult >= 0L);
	}

	static void deleteResult(SQLiteDatabase db, String playDate) {
		Log.d(TAG, "deleteResult()");
		db.delete(TABLE, COLUMN_PLAY_DATE + " = ? ", new String[] { playDate });
	}
}
