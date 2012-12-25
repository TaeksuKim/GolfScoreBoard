package org.dolicoli.android.golfscoreboard.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dolicoli.android.golfscoreboard.data.settings.GameSetting;
import org.dolicoli.android.golfscoreboard.data.settings.PlayerSetting;
import org.dolicoli.android.golfscoreboard.data.settings.Result;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class HistoryGameSettingDatabaseWorker extends AbstractDatabaseWorker {

	private static final String TAG = "HistoryGameSettingDatabaseWorker";

	private static final String TABLE = "historyGame";

	private static final String COLUMN_PLAY_DATE = "playDate";

	private static final String COLUMN_HOLE_COUNT = "holeCount";
	private static final String COLUMN_PLAYER_COUNT = "playerCount";
	private static final String COLUMN_GAME_FEE = "gameFee";
	private static final String COLUMN_EXTRA_FEE = "extraFee";
	private static final String COLUMN_RANKING_FEE = "rankingFee";

	private static final String COLUMN_DATE = "dateColumn";

	private static final String COLUMN_HOLE_FEE_RANKING_1 = "holeFeeForRanking1";
	private static final String COLUMN_HOLE_FEE_RANKING_2 = "holeFeeForRanking2";
	private static final String COLUMN_HOLE_FEE_RANKING_3 = "holeFeeForRanking3";
	private static final String COLUMN_HOLE_FEE_RANKING_4 = "holeFeeForRanking4";
	private static final String COLUMN_HOLE_FEE_RANKING_5 = "holeFeeForRanking5";
	private static final String COLUMN_HOLE_FEE_RANKING_6 = "holeFeeForRanking6";

	private static final String COLUMN_RANKING_FEE_RANKING_1 = "rankingFeeForRanking1";
	private static final String COLUMN_RANKING_FEE_RANKING_2 = "rankingFeeForRanking2";
	private static final String COLUMN_RANKING_FEE_RANKING_3 = "rankingFeeForRanking3";
	private static final String COLUMN_RANKING_FEE_RANKING_4 = "rankingFeeForRanking4";
	private static final String COLUMN_RANKING_FEE_RANKING_5 = "rankingFeeForRanking5";
	private static final String COLUMN_RANKING_FEE_RANKING_6 = "rankingFeeForRanking6";

	// COLUMN_PLAY_DATE + " VARCHAR(12) PRIMARY KEY , " +
	// COLUMN_HOLE_COUNT + " INTEGER , " +
	// COLUMN_PLAYER_COUNT + " INTEGER , " +
	// COLUMN_GAME_FEE + " INTEGER DEFAULT 0 , " +
	// COLUMN_EXTRA_FEE + " INTEGER DEFAULT 0 , " +
	// COLUMN_RANKING_FEE + " INTEGER DEFAULT 0 , " +
	// COLUMN_DATE + " LONG , " +
	// COLUMN_HOLE_FEE_RANKING_1 + " INTEGER , " +
	// COLUMN_HOLE_FEE_RANKING_2 + " INTEGER , " +
	// COLUMN_HOLE_FEE_RANKING_3 + " INTEGER , " +
	// COLUMN_HOLE_FEE_RANKING_4 + " INTEGER , " +
	// COLUMN_HOLE_FEE_RANKING_5 + " INTEGER , " +
	// COLUMN_HOLE_FEE_RANKING_6 + " INTEGER , " +
	// COLUMN_RANKING_FEE_RANKING_1 + " INTEGER DEFAULT 0 , " +
	// COLUMN_RANKING_FEE_RANKING_2 + " INTEGER DEFAULT 0 , " +
	// COLUMN_RANKING_FEE_RANKING_3 + " INTEGER DEFAULT 0 , " +
	// COLUMN_RANKING_FEE_RANKING_4 + " INTEGER DEFAULT 0 , " +
	// COLUMN_RANKING_FEE_RANKING_5 + " INTEGER DEFAULT 0 , " +
	// COLUMN_RANKING_FEE_RANKING_6 + " INTEGER DEFAULT 0 , " +

	private static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE
			+ " (" + COLUMN_PLAY_DATE + " VARCHAR(12) PRIMARY KEY , "
			+ COLUMN_HOLE_COUNT + " INTEGER , " + COLUMN_PLAYER_COUNT
			+ " INTEGER , " + COLUMN_GAME_FEE + " INTEGER DEFAULT 0 , "
			+ COLUMN_EXTRA_FEE + " INTEGER DEFAULT 0 , " + COLUMN_RANKING_FEE
			+ " INTEGER DEFAULT 0, " + COLUMN_DATE + " LONG , "
			+ COLUMN_HOLE_FEE_RANKING_1 + " INTEGER , "
			+ COLUMN_HOLE_FEE_RANKING_2 + " INTEGER , "
			+ COLUMN_HOLE_FEE_RANKING_3 + " INTEGER , "
			+ COLUMN_HOLE_FEE_RANKING_4 + " INTEGER , "
			+ COLUMN_HOLE_FEE_RANKING_5 + " INTEGER , "
			+ COLUMN_HOLE_FEE_RANKING_6 + " INTEGER , "
			+ COLUMN_RANKING_FEE_RANKING_1 + " INTEGER DEFAULT 0, "
			+ COLUMN_RANKING_FEE_RANKING_2 + " INTEGER DEFAULT 0, "
			+ COLUMN_RANKING_FEE_RANKING_3 + " INTEGER DEFAULT 0, "
			+ COLUMN_RANKING_FEE_RANKING_4 + " INTEGER DEFAULT 0, "
			+ COLUMN_RANKING_FEE_RANKING_5 + " INTEGER DEFAULT 0, "
			+ COLUMN_RANKING_FEE_RANKING_6 + " INTEGER DEFAULT 0  " + " ); ";

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
		if (oldVersion < 5) {
			db.execSQL(CREATE_TABLE_SQL);
		} else if (oldVersion < 6) {
			String sql = "ALTER TABLE " + TABLE + " ADD COLUMN "
					+ COLUMN_RANKING_FEE + " DEFAULT 0";
			db.execSQL(sql);

			String[] rankingFeeRankingColumnNames = {
					COLUMN_RANKING_FEE_RANKING_1, COLUMN_RANKING_FEE_RANKING_2,
					COLUMN_RANKING_FEE_RANKING_3, COLUMN_RANKING_FEE_RANKING_4,
					COLUMN_RANKING_FEE_RANKING_5, COLUMN_RANKING_FEE_RANKING_6 };
			for (String column : rankingFeeRankingColumnNames) {
				sql = "ALTER TABLE " + TABLE + " ADD COLUMN " + column
						+ " DEFAULT 0";
				db.execSQL(sql);
			}
		}

		if (oldVersion < 7) {
			String sql = "";
			sql += "CREATE TEMPORARY TABLE hgs_backup (";
			sql += "	" + COLUMN_PLAY_DATE + ", ";
			sql += "	" + COLUMN_HOLE_COUNT + ", ";
			sql += "	" + COLUMN_PLAYER_COUNT + ", ";
			sql += "	" + COLUMN_GAME_FEE + ", ";
			sql += "	" + COLUMN_EXTRA_FEE + ", ";
			sql += "	" + COLUMN_RANKING_FEE + ", ";
			sql += "	" + COLUMN_DATE + ", ";
			sql += "	" + COLUMN_HOLE_FEE_RANKING_1 + ", ";
			sql += "	" + COLUMN_HOLE_FEE_RANKING_2 + ", ";
			sql += "	" + COLUMN_HOLE_FEE_RANKING_3 + ", ";
			sql += "	" + COLUMN_HOLE_FEE_RANKING_4 + ", ";
			sql += "	" + COLUMN_HOLE_FEE_RANKING_5 + ", ";
			sql += "	" + COLUMN_HOLE_FEE_RANKING_6 + ", ";
			sql += "	" + COLUMN_RANKING_FEE_RANKING_1 + ", ";
			sql += "	" + COLUMN_RANKING_FEE_RANKING_2 + ", ";
			sql += "	" + COLUMN_RANKING_FEE_RANKING_3 + ", ";
			sql += "	" + COLUMN_RANKING_FEE_RANKING_4 + ", ";
			sql += "	" + COLUMN_RANKING_FEE_RANKING_5 + ", ";
			sql += "	" + COLUMN_RANKING_FEE_RANKING_6 + " ";
			sql += "); ";
			db.execSQL(sql);
			sql = "";
			sql += "INSERT INTO hgs_backup SELECT ";
			sql += "	(" + COLUMN_PLAY_DATE + "||'00'), ";
			sql += "	" + COLUMN_HOLE_COUNT + ", ";
			sql += "	" + COLUMN_PLAYER_COUNT + ", ";
			sql += "	" + COLUMN_GAME_FEE + ", ";
			sql += "	" + COLUMN_EXTRA_FEE + ", ";
			sql += "	" + COLUMN_RANKING_FEE + ", ";
			sql += "	" + COLUMN_DATE + ", ";
			sql += "	" + COLUMN_HOLE_FEE_RANKING_1 + ", ";
			sql += "	" + COLUMN_HOLE_FEE_RANKING_2 + ", ";
			sql += "	" + COLUMN_HOLE_FEE_RANKING_3 + ", ";
			sql += "	" + COLUMN_HOLE_FEE_RANKING_4 + ", ";
			sql += "	" + COLUMN_HOLE_FEE_RANKING_5 + ", ";
			sql += "	" + COLUMN_HOLE_FEE_RANKING_6 + ", ";
			sql += "	" + COLUMN_RANKING_FEE_RANKING_1 + ", ";
			sql += "	" + COLUMN_RANKING_FEE_RANKING_2 + ", ";
			sql += "	" + COLUMN_RANKING_FEE_RANKING_3 + ", ";
			sql += "	" + COLUMN_RANKING_FEE_RANKING_4 + ", ";
			sql += "	" + COLUMN_RANKING_FEE_RANKING_5 + ", ";
			sql += "	" + COLUMN_RANKING_FEE_RANKING_6 + " ";
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
			sql += "	" + COLUMN_PLAY_DATE + " , ";
			sql += "	" + COLUMN_HOLE_COUNT + ", ";
			sql += "	" + COLUMN_PLAYER_COUNT + ", ";
			sql += "	" + COLUMN_GAME_FEE + ", ";
			sql += "	" + COLUMN_EXTRA_FEE + ", ";
			sql += "	" + COLUMN_RANKING_FEE + ", ";
			sql += "	" + COLUMN_DATE + ", ";
			sql += "	" + COLUMN_HOLE_FEE_RANKING_1 + ", ";
			sql += "	" + COLUMN_HOLE_FEE_RANKING_2 + ", ";
			sql += "	" + COLUMN_HOLE_FEE_RANKING_3 + ", ";
			sql += "	" + COLUMN_HOLE_FEE_RANKING_4 + ", ";
			sql += "	" + COLUMN_HOLE_FEE_RANKING_5 + ", ";
			sql += "	" + COLUMN_HOLE_FEE_RANKING_6 + ", ";
			sql += "	" + COLUMN_RANKING_FEE_RANKING_1 + ", ";
			sql += "	" + COLUMN_RANKING_FEE_RANKING_2 + ", ";
			sql += "	" + COLUMN_RANKING_FEE_RANKING_3 + ", ";
			sql += "	" + COLUMN_RANKING_FEE_RANKING_4 + ", ";
			sql += "	" + COLUMN_RANKING_FEE_RANKING_5 + ", ";
			sql += "	" + COLUMN_RANKING_FEE_RANKING_6 + " ";
			sql += "FROM hgs_backup; ";
			sql += "DROP TABLE hgs_backup; ";
			db.execSQL(sql);
		}
	}

	public HistoryGameSettingDatabaseWorker(Context context) {
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

	public void clearHistory(String playDate) {
		Log.d(TAG, "clearHistory()");

		open();

		try {
			mDb.beginTransaction();
			HistoryResultDatabaseWorker.deleteResult(mDb, playDate);
			HistoryPlayerSettingDatabaseWorker.deletePlayerSetting(mDb,
					playDate);
			deleteGameSetting(mDb, playDate);
			mDb.setTransactionSuccessful();
		} finally {
			mDb.endTransaction();
			close();
		}
	}

	public void clearHistories(List<String> playDates) {
		Log.d(TAG, "clearHistories()");

		if (playDates == null || playDates.size() < 1)
			return;

		open();

		try {
			mDb.beginTransaction();
			for (String playDate : playDates) {
				HistoryResultDatabaseWorker.deleteResult(mDb, playDate);
				HistoryPlayerSettingDatabaseWorker.deletePlayerSetting(mDb,
						playDate);
				deleteGameSetting(mDb, playDate);
			}
			mDb.setTransactionSuccessful();
		} finally {
			mDb.endTransaction();
			close();
		}
	}

	public void addCurrentHistory(boolean forceWrite) {
		addCurrentHistory(forceWrite, null, null, null);
	}

	public void addCurrentHistory(boolean forceWrite, GameSetting gameSetting,
			PlayerSetting playerSetting, List<Result> results) {
		if (gameSetting == null) {
			GameSettingDatabaseWorker gameSettingWorker = new GameSettingDatabaseWorker(
					context);
			gameSetting = new GameSetting();
			gameSettingWorker.getGameSetting(gameSetting);
		}

		if (playerSetting == null) {
			PlayerSettingDatabaseWorker playerSettingWorker = new PlayerSettingDatabaseWorker(
					context);
			playerSetting = new PlayerSetting();
			playerSettingWorker.getPlayerSetting(playerSetting);
		}

		if (results == null) {
			ResultDatabaseWorker resultWorker = new ResultDatabaseWorker(
					context);
			results = resultWorker.getResults();
		}

		if (!forceWrite) {
			int holeCount = gameSetting.getHoleCount();

			int lastHoleNumber = 0;
			for (Result result : results) {
				int holeNumber = result.getHoleNumber();
				if (lastHoleNumber < holeNumber) {
					lastHoleNumber = holeNumber;
				}
			}

			if (lastHoleNumber < holeCount)
				return;
		}

		addHistory(gameSetting, playerSetting, results);
	}

	private void addHistory(GameSetting gameSetting,
			PlayerSetting playerSetting, Iterable<Result> results) {
		Log.d(TAG, "clearHistory()");

		open();

		try {
			mDb.beginTransaction();

			Date date = gameSetting.getDate();
			String playDate = GameSetting.toGameIdFormat(date);

			updateGameSetting(mDb, playDate, gameSetting);

			HistoryPlayerSettingDatabaseWorker.updatePlayerSetting(mDb,
					playDate, playerSetting);

			HistoryResultDatabaseWorker.deleteResult(mDb, playDate);
			for (Result result : results) {
				HistoryResultDatabaseWorker.updateResult(mDb, playDate, result);
			}

			mDb.setTransactionSuccessful();
		} finally {
			mDb.endTransaction();
			close();
		}
	}

	public void getGameSetting(String playDate, GameSetting setting)
			throws SQLException {
		Log.d(TAG, "getGameSetting()");

		open();

		Cursor cursor = null;
		try {
			cursor = mDb.query(true, TABLE, new String[] { COLUMN_HOLE_COUNT,
					COLUMN_PLAYER_COUNT, COLUMN_GAME_FEE, COLUMN_EXTRA_FEE,
					COLUMN_RANKING_FEE, COLUMN_DATE, COLUMN_HOLE_FEE_RANKING_1,
					COLUMN_HOLE_FEE_RANKING_2, COLUMN_HOLE_FEE_RANKING_3,
					COLUMN_HOLE_FEE_RANKING_4, COLUMN_HOLE_FEE_RANKING_5,
					COLUMN_HOLE_FEE_RANKING_6, COLUMN_RANKING_FEE_RANKING_1,
					COLUMN_RANKING_FEE_RANKING_2, COLUMN_RANKING_FEE_RANKING_3,
					COLUMN_RANKING_FEE_RANKING_4, COLUMN_RANKING_FEE_RANKING_5,
					COLUMN_RANKING_FEE_RANKING_6, },
					COLUMN_PLAY_DATE + " = ? ", new String[] { playDate },
					null, null, null, null);

			if (cursor != null)
				cursor.moveToFirst();

			int playerCount = 4;
			int holeCount = 9;
			int gameFee = 0;
			int extraFee = 0;
			int rankingFee = 0;
			int rankingFeeRanking1 = 0;
			int rankingFeeRanking2 = 0;
			int rankingFeeRanking3 = 0;
			int rankingFeeRanking4 = 0;
			int rankingFeeRanking5 = 0;
			int rankingFeeRanking6 = 0;
			int holeFeeRanking1 = 0;
			int holeFeeRanking2 = 0;
			int holeFeeRanking3 = 0;
			int holeFeeRanking4 = 0;
			int holeFeeRanking5 = 0;
			int holeFeeRanking6 = 0;
			long date = 0L;

			if (!cursor.isAfterLast()) {
				int offset = 0;

				holeCount = cursor.getInt(offset++);
				playerCount = cursor.getInt(offset++);
				gameFee = cursor.getInt(offset++);
				extraFee = cursor.getInt(offset++);
				rankingFee = cursor.getInt(offset++);
				date = cursor.getLong(offset++);

				holeFeeRanking1 = cursor.getInt(offset++);
				holeFeeRanking2 = cursor.getInt(offset++);
				holeFeeRanking3 = cursor.getInt(offset++);
				holeFeeRanking4 = cursor.getInt(offset++);
				holeFeeRanking5 = cursor.getInt(offset++);
				holeFeeRanking6 = cursor.getInt(offset++);

				rankingFeeRanking1 = cursor.getInt(offset++);
				rankingFeeRanking2 = cursor.getInt(offset++);
				rankingFeeRanking3 = cursor.getInt(offset++);
				rankingFeeRanking4 = cursor.getInt(offset++);
				rankingFeeRanking5 = cursor.getInt(offset++);
				rankingFeeRanking6 = cursor.getInt(offset++);

				setting.setPlayDate(playDate);
				setting.setHoleCount(holeCount);
				setting.setPlayerCount(playerCount);
				setting.setGameFee(gameFee);
				setting.setExtraFee(extraFee);
				setting.setRankingFee(rankingFee);
				setting.setHoleFeeForRanking(1, holeFeeRanking1);
				setting.setHoleFeeForRanking(2, holeFeeRanking2);
				setting.setHoleFeeForRanking(3, holeFeeRanking3);
				setting.setHoleFeeForRanking(4, holeFeeRanking4);
				setting.setHoleFeeForRanking(5, holeFeeRanking5);
				setting.setHoleFeeForRanking(6, holeFeeRanking6);
				setting.setRankingFeeForRanking(1, rankingFeeRanking1);
				setting.setRankingFeeForRanking(2, rankingFeeRanking2);
				setting.setRankingFeeForRanking(3, rankingFeeRanking3);
				setting.setRankingFeeForRanking(4, rankingFeeRanking4);
				setting.setRankingFeeForRanking(5, rankingFeeRanking5);
				setting.setRankingFeeForRanking(6, rankingFeeRanking6);
				setting.setDate(date);

				cursor.moveToNext();
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}

			close();
		}
	}

	public void getGameSettings(long from, long to, ArrayList<GameSetting> games)
			throws SQLException {
		Log.d(TAG, "getGameSettings()");

		open();

		Cursor cursor = null;
		try {
			cursor = mDb.query(true, TABLE,
					new String[] { COLUMN_PLAY_DATE, COLUMN_HOLE_COUNT,
							COLUMN_PLAYER_COUNT, COLUMN_GAME_FEE,
							COLUMN_EXTRA_FEE, COLUMN_RANKING_FEE, COLUMN_DATE,
							COLUMN_HOLE_FEE_RANKING_1,
							COLUMN_HOLE_FEE_RANKING_2,
							COLUMN_HOLE_FEE_RANKING_3,
							COLUMN_HOLE_FEE_RANKING_4,
							COLUMN_HOLE_FEE_RANKING_5,
							COLUMN_HOLE_FEE_RANKING_6,
							COLUMN_RANKING_FEE_RANKING_1,
							COLUMN_RANKING_FEE_RANKING_2,
							COLUMN_RANKING_FEE_RANKING_3,
							COLUMN_RANKING_FEE_RANKING_4,
							COLUMN_RANKING_FEE_RANKING_5,
							COLUMN_RANKING_FEE_RANKING_6, }, COLUMN_DATE
							+ " BETWEEN ? AND ? ",
					new String[] { String.valueOf(from), String.valueOf(to) },
					null, null, null, null);

			if (cursor != null)
				cursor.moveToFirst();

			String playDate = null;
			int playerCount = 4;
			int holeCount = 9;
			int gameFee = 0;
			int extraFee = 0;
			int rankingFee = 0;
			int rankingFeeRanking1 = 0;
			int rankingFeeRanking2 = 0;
			int rankingFeeRanking3 = 0;
			int rankingFeeRanking4 = 0;
			int rankingFeeRanking5 = 0;
			int rankingFeeRanking6 = 0;
			int holeFeeRanking1 = 0;
			int holeFeeRanking2 = 0;
			int holeFeeRanking3 = 0;
			int holeFeeRanking4 = 0;
			int holeFeeRanking5 = 0;
			int holeFeeRanking6 = 0;
			long date = 0L;

			while (!cursor.isAfterLast()) {
				int offset = 0;

				playDate = cursor.getString(offset++);

				holeCount = cursor.getInt(offset++);
				playerCount = cursor.getInt(offset++);
				gameFee = cursor.getInt(offset++);
				extraFee = cursor.getInt(offset++);
				rankingFee = cursor.getInt(offset++);
				date = cursor.getLong(offset++);

				holeFeeRanking1 = cursor.getInt(offset++);
				holeFeeRanking2 = cursor.getInt(offset++);
				holeFeeRanking3 = cursor.getInt(offset++);
				holeFeeRanking4 = cursor.getInt(offset++);
				holeFeeRanking5 = cursor.getInt(offset++);
				holeFeeRanking6 = cursor.getInt(offset++);

				rankingFeeRanking1 = cursor.getInt(offset++);
				rankingFeeRanking2 = cursor.getInt(offset++);
				rankingFeeRanking3 = cursor.getInt(offset++);
				rankingFeeRanking4 = cursor.getInt(offset++);
				rankingFeeRanking5 = cursor.getInt(offset++);
				rankingFeeRanking6 = cursor.getInt(offset++);

				GameSetting setting = new GameSetting();

				setting.setPlayDate(playDate);
				setting.setHoleCount(holeCount);
				setting.setPlayerCount(playerCount);
				setting.setGameFee(gameFee);
				setting.setExtraFee(extraFee);
				setting.setRankingFee(rankingFee);
				setting.setHoleFeeForRanking(1, holeFeeRanking1);
				setting.setHoleFeeForRanking(2, holeFeeRanking2);
				setting.setHoleFeeForRanking(3, holeFeeRanking3);
				setting.setHoleFeeForRanking(4, holeFeeRanking4);
				setting.setHoleFeeForRanking(5, holeFeeRanking5);
				setting.setHoleFeeForRanking(6, holeFeeRanking6);
				setting.setRankingFeeForRanking(1, rankingFeeRanking1);
				setting.setRankingFeeForRanking(2, rankingFeeRanking2);
				setting.setRankingFeeForRanking(3, rankingFeeRanking3);
				setting.setRankingFeeForRanking(4, rankingFeeRanking4);
				setting.setRankingFeeForRanking(5, rankingFeeRanking5);
				setting.setRankingFeeForRanking(6, rankingFeeRanking6);
				setting.setDate(date);

				games.add(setting);

				cursor.moveToNext();
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}

			close();
		}
	}

	private static void updateGameSetting(SQLiteDatabase db, String playDate,
			GameSetting setting) {
		Log.d(TAG, "updateGameSetting()");

		ContentValues args = new ContentValues();

		args.put(COLUMN_PLAY_DATE, playDate);
		args.put(COLUMN_HOLE_COUNT, setting.getHoleCount());
		args.put(COLUMN_PLAYER_COUNT, setting.getPlayerCount());
		args.put(COLUMN_GAME_FEE, setting.getGameFee());
		args.put(COLUMN_EXTRA_FEE, setting.getExtraFee());
		args.put(COLUMN_RANKING_FEE, setting.getRankingFee());
		args.put(COLUMN_DATE, setting.getDate().getTime());

		args.put(COLUMN_HOLE_FEE_RANKING_1, setting.getHoleFeeForRanking(1));
		args.put(COLUMN_HOLE_FEE_RANKING_2, setting.getHoleFeeForRanking(2));
		args.put(COLUMN_HOLE_FEE_RANKING_3, setting.getHoleFeeForRanking(3));
		args.put(COLUMN_HOLE_FEE_RANKING_4, setting.getHoleFeeForRanking(4));
		args.put(COLUMN_HOLE_FEE_RANKING_5, setting.getHoleFeeForRanking(5));
		args.put(COLUMN_HOLE_FEE_RANKING_6, setting.getHoleFeeForRanking(6));
		args.put(COLUMN_RANKING_FEE_RANKING_1,
				setting.getRankingFeeForRanking(1));
		args.put(COLUMN_RANKING_FEE_RANKING_2,
				setting.getRankingFeeForRanking(2));
		args.put(COLUMN_RANKING_FEE_RANKING_3,
				setting.getRankingFeeForRanking(3));
		args.put(COLUMN_RANKING_FEE_RANKING_4,
				setting.getRankingFeeForRanking(4));
		args.put(COLUMN_RANKING_FEE_RANKING_5,
				setting.getRankingFeeForRanking(5));
		args.put(COLUMN_RANKING_FEE_RANKING_6,
				setting.getRankingFeeForRanking(6));

		db.replace(TABLE, null, args);
	}

	private static void deleteGameSetting(SQLiteDatabase db, String playDate) {
		Log.d(TAG, "deleteGameSetting()");
		db.delete(TABLE, COLUMN_PLAY_DATE + " = ? ", new String[] { playDate });
	}
}
