package org.dolicoli.android.golfscoreboard.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.dolicoli.android.golfscoreboard.Constants;
import org.dolicoli.android.golfscoreboard.data.OneGame;
import org.dolicoli.android.golfscoreboard.data.settings.GameSetting;
import org.dolicoli.android.golfscoreboard.data.settings.PlayerSetting;
import org.dolicoli.android.golfscoreboard.data.settings.Result;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
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
		GameSettingDatabaseWorker gameSettingWorker = new GameSettingDatabaseWorker(
				context);
		if (gameSetting == null) {
			gameSetting = new GameSetting();
			playerSetting = new PlayerSetting();
			results = new ArrayList<Result>();
			gameSettingWorker.getGameSetting(gameSetting, playerSetting,
					results);
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
		Log.d(TAG, "addHistory()");

		open();

		try {
			mDb.beginTransaction();

			Date date = gameSetting.getDate();
			String playDate = GameSetting.toGameIdFormat(date);

			updateGameSetting(mDb, playDate, gameSetting);

			HistoryPlayerSettingDatabaseWorker.updatePlayerSetting(mDb,
					playDate, playerSetting);

			int playerCount = gameSetting.getPlayerCount();
			for (int i = 0; i < playerCount; i++) {
				String playerName = playerSetting.getPlayerName(i);
				PlayerCacheDatabaseWorker.putPlayer(mDb, playerName);
			}

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

	public void getGameSetting(String playDate, GameSetting gameSetting,
			PlayerSetting playerSetting) throws SQLException {
		Log.d(TAG, "getGameSetting()");

		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(TABLE + " G " + " LEFT JOIN "
				+ HistoryPlayerSettingDatabaseWorker.TABLE + " P " + " ON "
				+ "G." + COLUMN_PLAY_DATE + " = " + "P."
				+ HistoryPlayerSettingDatabaseWorker.COLUMN_PLAY_DATE);

		String sql = queryBuilder
				.buildQuery(
						new String[] {
								"G." + COLUMN_HOLE_COUNT,
								"G." + COLUMN_PLAYER_COUNT,
								"G." + COLUMN_GAME_FEE,
								"G." + COLUMN_EXTRA_FEE,
								"G." + COLUMN_RANKING_FEE,
								"G." + COLUMN_DATE,
								"G." + COLUMN_HOLE_FEE_RANKING_1,
								"G." + COLUMN_HOLE_FEE_RANKING_2,
								"G." + COLUMN_HOLE_FEE_RANKING_3,
								"G." + COLUMN_HOLE_FEE_RANKING_4,
								"G." + COLUMN_HOLE_FEE_RANKING_5,
								"G." + COLUMN_HOLE_FEE_RANKING_6,
								"G." + COLUMN_RANKING_FEE_RANKING_1,
								"G." + COLUMN_RANKING_FEE_RANKING_2,
								"G." + COLUMN_RANKING_FEE_RANKING_3,
								"G." + COLUMN_RANKING_FEE_RANKING_4,
								"G." + COLUMN_RANKING_FEE_RANKING_5,
								"G." + COLUMN_RANKING_FEE_RANKING_6,

								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_NAME_1,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_NAME_2,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_NAME_3,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_NAME_4,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_NAME_5,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_NAME_6,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_HANDICAP_1,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_HANDICAP_2,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_HANDICAP_3,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_HANDICAP_4,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_HANDICAP_5,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_HANDICAP_6,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_EXTRA_SCORE_1,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_EXTRA_SCORE_2,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_EXTRA_SCORE_3,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_EXTRA_SCORE_4,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_EXTRA_SCORE_5,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_EXTRA_SCORE_6, },

						"G." + COLUMN_PLAY_DATE + " = ? ", null, null, null,
						null);

		open();

		Cursor cursor = null;
		try {
			cursor = mDb.rawQuery(sql, new String[] { playDate });

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

			String playerName1 = "";
			String playerName2 = "";
			String playerName3 = "";
			String playerName4 = "";
			String playerName5 = "";
			String playerName6 = "";

			int handicap1 = 0;
			int handicap2 = 0;
			int handicap3 = 0;
			int handicap4 = 0;
			int handicap5 = 0;
			int handicap6 = 0;

			int extraScore1 = 0;
			int extraScore2 = 0;
			int extraScore3 = 0;
			int extraScore4 = 0;
			int extraScore5 = 0;
			int extraScore6 = 0;

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

				playerName1 = cursor.getString(offset++);
				playerName2 = cursor.getString(offset++);
				playerName3 = cursor.getString(offset++);
				playerName4 = cursor.getString(offset++);
				playerName5 = cursor.getString(offset++);
				playerName6 = cursor.getString(offset++);

				handicap1 = cursor.getInt(offset++);
				handicap2 = cursor.getInt(offset++);
				handicap3 = cursor.getInt(offset++);
				handicap4 = cursor.getInt(offset++);
				handicap5 = cursor.getInt(offset++);
				handicap6 = cursor.getInt(offset++);

				extraScore1 = cursor.getInt(offset++);
				extraScore2 = cursor.getInt(offset++);
				extraScore3 = cursor.getInt(offset++);
				extraScore4 = cursor.getInt(offset++);
				extraScore5 = cursor.getInt(offset++);
				extraScore6 = cursor.getInt(offset++);

				gameSetting.setPlayDate(playDate);
				gameSetting.setHoleCount(holeCount);
				gameSetting.setPlayerCount(playerCount);
				gameSetting.setGameFee(gameFee);
				gameSetting.setExtraFee(extraFee);
				gameSetting.setRankingFee(rankingFee);
				gameSetting.setHoleFeeForRanking(1, holeFeeRanking1);
				gameSetting.setHoleFeeForRanking(2, holeFeeRanking2);
				gameSetting.setHoleFeeForRanking(3, holeFeeRanking3);
				gameSetting.setHoleFeeForRanking(4, holeFeeRanking4);
				gameSetting.setHoleFeeForRanking(5, holeFeeRanking5);
				gameSetting.setHoleFeeForRanking(6, holeFeeRanking6);
				gameSetting.setRankingFeeForRanking(1, rankingFeeRanking1);
				gameSetting.setRankingFeeForRanking(2, rankingFeeRanking2);
				gameSetting.setRankingFeeForRanking(3, rankingFeeRanking3);
				gameSetting.setRankingFeeForRanking(4, rankingFeeRanking4);
				gameSetting.setRankingFeeForRanking(5, rankingFeeRanking5);
				gameSetting.setRankingFeeForRanking(6, rankingFeeRanking6);
				gameSetting.setDate(date);

				playerSetting.setPlayDate(playDate);
				playerSetting.setPlayerName(0, playerName1);
				playerSetting.setPlayerName(1, playerName2);
				playerSetting.setPlayerName(2, playerName3);
				playerSetting.setPlayerName(3, playerName4);
				playerSetting.setPlayerName(4, playerName5);
				playerSetting.setPlayerName(5, playerName6);
				playerSetting.setHandicap(0, handicap1);
				playerSetting.setHandicap(1, handicap2);
				playerSetting.setHandicap(2, handicap3);
				playerSetting.setHandicap(3, handicap4);
				playerSetting.setHandicap(4, handicap5);
				playerSetting.setHandicap(5, handicap6);
				playerSetting.setExtraScore(0, extraScore1);
				playerSetting.setExtraScore(1, extraScore2);
				playerSetting.setExtraScore(2, extraScore3);
				playerSetting.setExtraScore(3, extraScore4);
				playerSetting.setExtraScore(4, extraScore5);
				playerSetting.setExtraScore(5, extraScore6);

				cursor.moveToNext();
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}

			close();
		}
	}

	public void getGameSettingWithResult(String playDate,
			GameSetting gameSetting, PlayerSetting playerSetting,
			List<Result> results, int maxHoleNumber) throws SQLException {
		Log.d(TAG, "getGameSettingWithResult()");

		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(TABLE + " G " + " LEFT JOIN "
				+ HistoryPlayerSettingDatabaseWorker.TABLE + " P " + " ON "
				+ "G." + COLUMN_PLAY_DATE + " = " + "P."
				+ HistoryPlayerSettingDatabaseWorker.COLUMN_PLAY_DATE
				+ " LEFT JOIN " + HistoryResultDatabaseWorker.TABLE + " R "
				+ " ON " + "G." + COLUMN_PLAY_DATE + " = " + "R."
				+ HistoryResultDatabaseWorker.COLUMN_PLAY_DATE);

		String holeWhereClause = "";
		if (maxHoleNumber != Constants.WHOLE_HOLE) {
			holeWhereClause = " AND R."
					+ HistoryResultDatabaseWorker.COLUMN_HOLE_NUMBER + " <= ? ";
		}

		String sql = queryBuilder
				.buildQuery(
						new String[] {
								"G." + COLUMN_HOLE_COUNT,
								"G." + COLUMN_PLAYER_COUNT,
								"G." + COLUMN_GAME_FEE,
								"G." + COLUMN_EXTRA_FEE,
								"G." + COLUMN_RANKING_FEE,
								"G." + COLUMN_DATE,
								"G." + COLUMN_HOLE_FEE_RANKING_1,
								"G." + COLUMN_HOLE_FEE_RANKING_2,
								"G." + COLUMN_HOLE_FEE_RANKING_3,
								"G." + COLUMN_HOLE_FEE_RANKING_4,
								"G." + COLUMN_HOLE_FEE_RANKING_5,
								"G." + COLUMN_HOLE_FEE_RANKING_6,
								"G." + COLUMN_RANKING_FEE_RANKING_1,
								"G." + COLUMN_RANKING_FEE_RANKING_2,
								"G." + COLUMN_RANKING_FEE_RANKING_3,
								"G." + COLUMN_RANKING_FEE_RANKING_4,
								"G." + COLUMN_RANKING_FEE_RANKING_5,
								"G." + COLUMN_RANKING_FEE_RANKING_6,

								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_NAME_1,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_NAME_2,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_NAME_3,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_NAME_4,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_NAME_5,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_NAME_6,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_HANDICAP_1,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_HANDICAP_2,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_HANDICAP_3,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_HANDICAP_4,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_HANDICAP_5,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_HANDICAP_6,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_EXTRA_SCORE_1,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_EXTRA_SCORE_2,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_EXTRA_SCORE_3,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_EXTRA_SCORE_4,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_EXTRA_SCORE_5,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_EXTRA_SCORE_6,

								"R."
										+ HistoryResultDatabaseWorker.COLUMN_HOLE_NUMBER,
								"R."
										+ HistoryResultDatabaseWorker.COLUMN_PAR_NUMBER,
								"R."
										+ HistoryResultDatabaseWorker.COLUMN_PLAYER_1_SCORE,
								"R."
										+ HistoryResultDatabaseWorker.COLUMN_PLAYER_2_SCORE,
								"R."
										+ HistoryResultDatabaseWorker.COLUMN_PLAYER_3_SCORE,
								"R."
										+ HistoryResultDatabaseWorker.COLUMN_PLAYER_4_SCORE,
								"R."
										+ HistoryResultDatabaseWorker.COLUMN_PLAYER_5_SCORE,
								"R."
										+ HistoryResultDatabaseWorker.COLUMN_PLAYER_6_SCORE,
								"R."
										+ HistoryResultDatabaseWorker.COLUMN_PLAYER_1_HANDICAP_USED,
								"R."
										+ HistoryResultDatabaseWorker.COLUMN_PLAYER_2_HANDICAP_USED,
								"R."
										+ HistoryResultDatabaseWorker.COLUMN_PLAYER_3_HANDICAP_USED,
								"R."
										+ HistoryResultDatabaseWorker.COLUMN_PLAYER_4_HANDICAP_USED,
								"R."
										+ HistoryResultDatabaseWorker.COLUMN_PLAYER_5_HANDICAP_USED,
								"R."
										+ HistoryResultDatabaseWorker.COLUMN_PLAYER_6_HANDICAP_USED, },

						"G." + COLUMN_PLAY_DATE + " = ? " + holeWhereClause,
						null, null,
						"R." + HistoryResultDatabaseWorker.COLUMN_HOLE_NUMBER,
						null);

		open();

		Cursor cursor = null;
		try {

			if (maxHoleNumber != Constants.WHOLE_HOLE) {
				cursor = mDb
						.rawQuery(
								sql,
								new String[] { playDate,
										String.valueOf(maxHoleNumber) });
			} else {
				cursor = mDb.rawQuery(sql, new String[] { playDate });
			}

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

			String playerName1 = "";
			String playerName2 = "";
			String playerName3 = "";
			String playerName4 = "";
			String playerName5 = "";
			String playerName6 = "";

			int handicap1 = 0;
			int handicap2 = 0;
			int handicap3 = 0;
			int handicap4 = 0;
			int handicap5 = 0;
			int handicap6 = 0;

			int extraScore1 = 0;
			int extraScore2 = 0;
			int extraScore3 = 0;
			int extraScore4 = 0;
			int extraScore5 = 0;
			int extraScore6 = 0;

			int holeNumber = 0;
			int parNumber = 0;
			int player1Score = 0;
			int player2Score = 0;
			int player3Score = 0;
			int player4Score = 0;
			int player5Score = 0;
			int player6Score = 0;
			int player1HandicapUsed = 0;
			int player2HandicapUsed = 0;
			int player3HandicapUsed = 0;
			int player4HandicapUsed = 0;
			int player5HandicapUsed = 0;
			int player6HandicapUsed = 0;

			while (!cursor.isAfterLast()) {
				int offset = 0;

				if (cursor.isFirst()) {
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

					playerName1 = cursor.getString(offset++);
					playerName2 = cursor.getString(offset++);
					playerName3 = cursor.getString(offset++);
					playerName4 = cursor.getString(offset++);
					playerName5 = cursor.getString(offset++);
					playerName6 = cursor.getString(offset++);

					handicap1 = cursor.getInt(offset++);
					handicap2 = cursor.getInt(offset++);
					handicap3 = cursor.getInt(offset++);
					handicap4 = cursor.getInt(offset++);
					handicap5 = cursor.getInt(offset++);
					handicap6 = cursor.getInt(offset++);

					extraScore1 = cursor.getInt(offset++);
					extraScore2 = cursor.getInt(offset++);
					extraScore3 = cursor.getInt(offset++);
					extraScore4 = cursor.getInt(offset++);
					extraScore5 = cursor.getInt(offset++);
					extraScore6 = cursor.getInt(offset++);

					gameSetting.setPlayDate(playDate);
					gameSetting.setHoleCount(holeCount);
					gameSetting.setPlayerCount(playerCount);
					gameSetting.setGameFee(gameFee);
					gameSetting.setExtraFee(extraFee);
					gameSetting.setRankingFee(rankingFee);
					gameSetting.setHoleFeeForRanking(1, holeFeeRanking1);
					gameSetting.setHoleFeeForRanking(2, holeFeeRanking2);
					gameSetting.setHoleFeeForRanking(3, holeFeeRanking3);
					gameSetting.setHoleFeeForRanking(4, holeFeeRanking4);
					gameSetting.setHoleFeeForRanking(5, holeFeeRanking5);
					gameSetting.setHoleFeeForRanking(6, holeFeeRanking6);
					gameSetting.setRankingFeeForRanking(1, rankingFeeRanking1);
					gameSetting.setRankingFeeForRanking(2, rankingFeeRanking2);
					gameSetting.setRankingFeeForRanking(3, rankingFeeRanking3);
					gameSetting.setRankingFeeForRanking(4, rankingFeeRanking4);
					gameSetting.setRankingFeeForRanking(5, rankingFeeRanking5);
					gameSetting.setRankingFeeForRanking(6, rankingFeeRanking6);
					gameSetting.setDate(date);

					playerSetting.setPlayDate(playDate);
					playerSetting.setPlayerName(0, playerName1);
					playerSetting.setPlayerName(1, playerName2);
					playerSetting.setPlayerName(2, playerName3);
					playerSetting.setPlayerName(3, playerName4);
					playerSetting.setPlayerName(4, playerName5);
					playerSetting.setPlayerName(5, playerName6);
					playerSetting.setHandicap(0, handicap1);
					playerSetting.setHandicap(1, handicap2);
					playerSetting.setHandicap(2, handicap3);
					playerSetting.setHandicap(3, handicap4);
					playerSetting.setHandicap(4, handicap5);
					playerSetting.setHandicap(5, handicap6);
					playerSetting.setExtraScore(0, extraScore1);
					playerSetting.setExtraScore(1, extraScore2);
					playerSetting.setExtraScore(2, extraScore3);
					playerSetting.setExtraScore(3, extraScore4);
					playerSetting.setExtraScore(4, extraScore5);
					playerSetting.setExtraScore(5, extraScore6);
				} else {
					offset = cursor
							.getColumnIndex(HistoryResultDatabaseWorker.COLUMN_HOLE_NUMBER);
				}

				holeNumber = cursor.getInt(offset++);
				parNumber = cursor.getInt(offset++);
				player1Score = cursor.getInt(offset++);
				player2Score = cursor.getInt(offset++);
				player3Score = cursor.getInt(offset++);
				player4Score = cursor.getInt(offset++);
				player5Score = cursor.getInt(offset++);
				player6Score = cursor.getInt(offset++);
				player1HandicapUsed = cursor.getInt(offset++);
				player2HandicapUsed = cursor.getInt(offset++);
				player3HandicapUsed = cursor.getInt(offset++);
				player4HandicapUsed = cursor.getInt(offset++);
				player5HandicapUsed = cursor.getInt(offset++);
				player6HandicapUsed = cursor.getInt(offset++);

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

				results.add(result);

				cursor.moveToNext();
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}

			close();
		}
	}

	private class Wrapper implements Comparable<Wrapper> {
		public String playDate;
		public GameSetting gameSetting;
		public PlayerSetting playerSetting;
		public ArrayList<Result> results;

		Wrapper(String playDate) {
			this.playDate = playDate;
			results = new ArrayList<Result>();
		}

		@Override
		public int compareTo(Wrapper another) {
			return playDate.compareTo(another.playDate);
		}
	}

	public void getGameSettingsWithResult(long from, long to,
			List<OneGame> gameAndResults) throws SQLException {
		Log.d(TAG, "getGameSettingsWithResult()");

		HashMap<String, Wrapper> map = new HashMap<String, Wrapper>();

		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(TABLE + " G " + " LEFT JOIN "
				+ HistoryPlayerSettingDatabaseWorker.TABLE + " P " + " ON "
				+ "G." + COLUMN_PLAY_DATE + " = " + "P."
				+ HistoryPlayerSettingDatabaseWorker.COLUMN_PLAY_DATE
				+ " LEFT JOIN " + HistoryResultDatabaseWorker.TABLE + " R "
				+ " ON " + "G." + COLUMN_PLAY_DATE + " = " + "R."
				+ HistoryResultDatabaseWorker.COLUMN_PLAY_DATE);

		String sql = queryBuilder
				.buildQuery(
						new String[] {
								"G." + COLUMN_PLAY_DATE,
								"G." + COLUMN_HOLE_COUNT,
								"G." + COLUMN_PLAYER_COUNT,
								"G." + COLUMN_GAME_FEE,
								"G." + COLUMN_EXTRA_FEE,
								"G." + COLUMN_RANKING_FEE,
								"G." + COLUMN_DATE,
								"G." + COLUMN_HOLE_FEE_RANKING_1,
								"G." + COLUMN_HOLE_FEE_RANKING_2,
								"G." + COLUMN_HOLE_FEE_RANKING_3,
								"G." + COLUMN_HOLE_FEE_RANKING_4,
								"G." + COLUMN_HOLE_FEE_RANKING_5,
								"G." + COLUMN_HOLE_FEE_RANKING_6,
								"G." + COLUMN_RANKING_FEE_RANKING_1,
								"G." + COLUMN_RANKING_FEE_RANKING_2,
								"G." + COLUMN_RANKING_FEE_RANKING_3,
								"G." + COLUMN_RANKING_FEE_RANKING_4,
								"G." + COLUMN_RANKING_FEE_RANKING_5,
								"G." + COLUMN_RANKING_FEE_RANKING_6,

								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_NAME_1,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_NAME_2,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_NAME_3,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_NAME_4,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_NAME_5,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_NAME_6,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_HANDICAP_1,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_HANDICAP_2,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_HANDICAP_3,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_HANDICAP_4,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_HANDICAP_5,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_HANDICAP_6,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_EXTRA_SCORE_1,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_EXTRA_SCORE_2,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_EXTRA_SCORE_3,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_EXTRA_SCORE_4,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_EXTRA_SCORE_5,
								"P."
										+ HistoryPlayerSettingDatabaseWorker.COLUMN_EXTRA_SCORE_6,

								"R."
										+ HistoryResultDatabaseWorker.COLUMN_HOLE_NUMBER,
								"R."
										+ HistoryResultDatabaseWorker.COLUMN_PAR_NUMBER,
								"R."
										+ HistoryResultDatabaseWorker.COLUMN_PLAYER_1_SCORE,
								"R."
										+ HistoryResultDatabaseWorker.COLUMN_PLAYER_2_SCORE,
								"R."
										+ HistoryResultDatabaseWorker.COLUMN_PLAYER_3_SCORE,
								"R."
										+ HistoryResultDatabaseWorker.COLUMN_PLAYER_4_SCORE,
								"R."
										+ HistoryResultDatabaseWorker.COLUMN_PLAYER_5_SCORE,
								"R."
										+ HistoryResultDatabaseWorker.COLUMN_PLAYER_6_SCORE,
								"R."
										+ HistoryResultDatabaseWorker.COLUMN_PLAYER_1_HANDICAP_USED,
								"R."
										+ HistoryResultDatabaseWorker.COLUMN_PLAYER_2_HANDICAP_USED,
								"R."
										+ HistoryResultDatabaseWorker.COLUMN_PLAYER_3_HANDICAP_USED,
								"R."
										+ HistoryResultDatabaseWorker.COLUMN_PLAYER_4_HANDICAP_USED,
								"R."
										+ HistoryResultDatabaseWorker.COLUMN_PLAYER_5_HANDICAP_USED,
								"R."
										+ HistoryResultDatabaseWorker.COLUMN_PLAYER_6_HANDICAP_USED, },

						"G." + COLUMN_DATE + " BETWEEN ? AND ? ",
						null,
						null,
						"G."
								+ COLUMN_DATE
								+ " , R."
								+ HistoryResultDatabaseWorker.COLUMN_HOLE_NUMBER,
						null);

		open();

		Cursor cursor = null;
		try {

			cursor = mDb.rawQuery(sql, new String[] { String.valueOf(from),
					String.valueOf(to) });

			if (cursor != null)
				cursor.moveToFirst();

			String playDate = "";
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

			String playerName1 = "";
			String playerName2 = "";
			String playerName3 = "";
			String playerName4 = "";
			String playerName5 = "";
			String playerName6 = "";

			int handicap1 = 0;
			int handicap2 = 0;
			int handicap3 = 0;
			int handicap4 = 0;
			int handicap5 = 0;
			int handicap6 = 0;

			int extraScore1 = 0;
			int extraScore2 = 0;
			int extraScore3 = 0;
			int extraScore4 = 0;
			int extraScore5 = 0;
			int extraScore6 = 0;

			int holeNumber = 0;
			int parNumber = 0;
			int player1Score = 0;
			int player2Score = 0;
			int player3Score = 0;
			int player4Score = 0;
			int player5Score = 0;
			int player6Score = 0;
			int player1HandicapUsed = 0;
			int player2HandicapUsed = 0;
			int player3HandicapUsed = 0;
			int player4HandicapUsed = 0;
			int player5HandicapUsed = 0;
			int player6HandicapUsed = 0;

			Wrapper wrapper = null;
			while (!cursor.isAfterLast()) {
				int offset = 0;

				boolean newWrapper = false;
				playDate = cursor.getString(offset++);
				if (!map.containsKey(playDate)) {
					map.put(playDate, new Wrapper(playDate));
					newWrapper = true;
				}
				wrapper = map.get(playDate);

				if (newWrapper) {
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

					playerName1 = cursor.getString(offset++);
					playerName2 = cursor.getString(offset++);
					playerName3 = cursor.getString(offset++);
					playerName4 = cursor.getString(offset++);
					playerName5 = cursor.getString(offset++);
					playerName6 = cursor.getString(offset++);

					handicap1 = cursor.getInt(offset++);
					handicap2 = cursor.getInt(offset++);
					handicap3 = cursor.getInt(offset++);
					handicap4 = cursor.getInt(offset++);
					handicap5 = cursor.getInt(offset++);
					handicap6 = cursor.getInt(offset++);

					extraScore1 = cursor.getInt(offset++);
					extraScore2 = cursor.getInt(offset++);
					extraScore3 = cursor.getInt(offset++);
					extraScore4 = cursor.getInt(offset++);
					extraScore5 = cursor.getInt(offset++);
					extraScore6 = cursor.getInt(offset++);

					wrapper.gameSetting = new GameSetting();
					wrapper.gameSetting.setPlayDate(playDate);
					wrapper.gameSetting.setHoleCount(holeCount);
					wrapper.gameSetting.setPlayerCount(playerCount);
					wrapper.gameSetting.setGameFee(gameFee);
					wrapper.gameSetting.setExtraFee(extraFee);
					wrapper.gameSetting.setRankingFee(rankingFee);
					wrapper.gameSetting
							.setHoleFeeForRanking(1, holeFeeRanking1);
					wrapper.gameSetting
							.setHoleFeeForRanking(2, holeFeeRanking2);
					wrapper.gameSetting
							.setHoleFeeForRanking(3, holeFeeRanking3);
					wrapper.gameSetting
							.setHoleFeeForRanking(4, holeFeeRanking4);
					wrapper.gameSetting
							.setHoleFeeForRanking(5, holeFeeRanking5);
					wrapper.gameSetting
							.setHoleFeeForRanking(6, holeFeeRanking6);
					wrapper.gameSetting.setRankingFeeForRanking(1,
							rankingFeeRanking1);
					wrapper.gameSetting.setRankingFeeForRanking(2,
							rankingFeeRanking2);
					wrapper.gameSetting.setRankingFeeForRanking(3,
							rankingFeeRanking3);
					wrapper.gameSetting.setRankingFeeForRanking(4,
							rankingFeeRanking4);
					wrapper.gameSetting.setRankingFeeForRanking(5,
							rankingFeeRanking5);
					wrapper.gameSetting.setRankingFeeForRanking(6,
							rankingFeeRanking6);
					wrapper.gameSetting.setDate(date);

					wrapper.playerSetting = new PlayerSetting();
					wrapper.playerSetting.setPlayDate(playDate);
					wrapper.playerSetting.setPlayerName(0, playerName1);
					wrapper.playerSetting.setPlayerName(1, playerName2);
					wrapper.playerSetting.setPlayerName(2, playerName3);
					wrapper.playerSetting.setPlayerName(3, playerName4);
					wrapper.playerSetting.setPlayerName(4, playerName5);
					wrapper.playerSetting.setPlayerName(5, playerName6);
					wrapper.playerSetting.setHandicap(0, handicap1);
					wrapper.playerSetting.setHandicap(1, handicap2);
					wrapper.playerSetting.setHandicap(2, handicap3);
					wrapper.playerSetting.setHandicap(3, handicap4);
					wrapper.playerSetting.setHandicap(4, handicap5);
					wrapper.playerSetting.setHandicap(5, handicap6);
					wrapper.playerSetting.setExtraScore(0, extraScore1);
					wrapper.playerSetting.setExtraScore(1, extraScore2);
					wrapper.playerSetting.setExtraScore(2, extraScore3);
					wrapper.playerSetting.setExtraScore(3, extraScore4);
					wrapper.playerSetting.setExtraScore(4, extraScore5);
					wrapper.playerSetting.setExtraScore(5, extraScore6);
				} else {
					offset = cursor
							.getColumnIndex(HistoryResultDatabaseWorker.COLUMN_HOLE_NUMBER);
				}

				holeNumber = cursor.getInt(offset++);
				parNumber = cursor.getInt(offset++);
				player1Score = cursor.getInt(offset++);
				player2Score = cursor.getInt(offset++);
				player3Score = cursor.getInt(offset++);
				player4Score = cursor.getInt(offset++);
				player5Score = cursor.getInt(offset++);
				player6Score = cursor.getInt(offset++);
				player1HandicapUsed = cursor.getInt(offset++);
				player2HandicapUsed = cursor.getInt(offset++);
				player3HandicapUsed = cursor.getInt(offset++);
				player4HandicapUsed = cursor.getInt(offset++);
				player5HandicapUsed = cursor.getInt(offset++);
				player6HandicapUsed = cursor.getInt(offset++);

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

				wrapper.results.add(result);

				cursor.moveToNext();
			}

			Collection<Wrapper> values = map.values();
			Wrapper[] array = new Wrapper[values.size()];
			values.toArray(array);
			Arrays.sort(array);
			for (Wrapper w : array) {
				gameAndResults.add(new OneGame(w.gameSetting, w.playerSetting,
						w.results));
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
