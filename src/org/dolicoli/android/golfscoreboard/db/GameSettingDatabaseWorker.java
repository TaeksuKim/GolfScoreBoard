package org.dolicoli.android.golfscoreboard.db;

import java.util.List;

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

public class GameSettingDatabaseWorker extends AbstractDatabaseWorker {

	private static final String TAG = "GameSettingDatabaseWorker";

	private static final String TABLE = "game";

	private static final String COLUMN_KEY_INDEX = "keyIndex";
	private static final String COLUMN_HOLE_COUNT = "holeCount";
	private static final String COLUMN_PLAYER_COUNT = "playerCount";
	private static final String COLUMN_GAME_FEE = "gameFee";
	private static final String COLUMN_EXTRA_FEE = "extraFee";
	private static final String COLUMN_HOLE_FEE_RANKING_1 = "rankingFee1"; // Modify
																			// to
																			// holeFeeForRanking
	private static final String COLUMN_HOLE_FEE_RANKING_2 = "rankingFee2"; // Modify
																			// to
																			// holeFeeForRanking
	private static final String COLUMN_HOLE_FEE_RANKING_3 = "rankingFee3"; // Modify
																			// to
																			// holeFeeForRanking
	private static final String COLUMN_HOLE_FEE_RANKING_4 = "rankingFee4"; // Modify
																			// to
																			// holeFeeForRanking
	private static final String COLUMN_HOLE_FEE_RANKING_5 = "rankingFee5"; // Modify
																			// to
																			// holeFeeForRanking
	private static final String COLUMN_HOLE_FEE_RANKING_6 = "rankingFee6"; // Modify
																			// to
																			// holeFeeForRanking
	private static final String COLUMN_DATE = "dateColumn";
	private static final String COLUMN_RANKING_FEE = "rankingFee";
	private static final String COLUMN_RANKING_FEE_RANKING_1 = "rankingFeeForRanking1";
	private static final String COLUMN_RANKING_FEE_RANKING_2 = "rankingFeeForRanking2";
	private static final String COLUMN_RANKING_FEE_RANKING_3 = "rankingFeeForRanking3";
	private static final String COLUMN_RANKING_FEE_RANKING_4 = "rankingFeeForRanking4";
	private static final String COLUMN_RANKING_FEE_RANKING_5 = "rankingFeeForRanking5";
	private static final String COLUMN_RANKING_FEE_RANKING_6 = "rankingFeeForRanking6";

	// COLUMN_KEY_INDEX + " INTEGER PRIMARY KEY , " +
	// COLUMN_HOLE_COUNT + " INTEGER , " +
	// COLUMN_PLAYER_COUNT + " INTEGER , " +
	// COLUMN_GAME_FEE + " INTEGER , " +
	// COLUMN_EXTRA_FEE + " INTEGER , " +
	// COLUMN_RANKING_FEE_1 + " INTEGER , " +
	// COLUMN_RANKING_FEE_2 + " INTEGER , " +
	// COLUMN_RANKING_FEE_3 + " INTEGER , " +
	// COLUMN_RANKING_FEE_4 + " INTEGER , " +
	// COLUMN_RANKING_FEE_5 + " INTEGER , " +
	// COLUMN_RANKING_FEE_6 + " INTEGER , " +
	// COLUMN_DATE + " LONG , " +
	// COLUMN_RANKING_FEE + " INTEGER DEFAULT 0 , " +
	// COLUMN_RANKING_FEE_RANKING_1 + " INTEGER DEFAULT 0 , " +
	// COLUMN_RANKING_FEE_RANKING_2 + " INTEGER DEFAULT 0 , " +
	// COLUMN_RANKING_FEE_RANKING_3 + " INTEGER DEFAULT 0 , " +
	// COLUMN_RANKING_FEE_RANKING_4 + " INTEGER DEFAULT 0 , " +
	// COLUMN_RANKING_FEE_RANKING_5 + " INTEGER DEFAULT 0 , " +
	// COLUMN_RANKING_FEE_RANKING_6 + " INTEGER DEFAULT 0 , " +

	private static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE
			+ " (" + COLUMN_KEY_INDEX + " INTEGER PRIMARY KEY , "
			+ COLUMN_HOLE_COUNT + " INTEGER , " + COLUMN_PLAYER_COUNT
			+ " INTEGER , " + COLUMN_GAME_FEE + " INTEGER , "
			+ COLUMN_EXTRA_FEE + " INTEGER , " + COLUMN_HOLE_FEE_RANKING_1
			+ " INTEGER , " + COLUMN_HOLE_FEE_RANKING_2 + " INTEGER , "
			+ COLUMN_HOLE_FEE_RANKING_3 + " INTEGER , "
			+ COLUMN_HOLE_FEE_RANKING_4 + " INTEGER , "
			+ COLUMN_HOLE_FEE_RANKING_5 + " INTEGER , "
			+ COLUMN_HOLE_FEE_RANKING_6 + " INTEGER , " + COLUMN_DATE
			+ " LONG , " + COLUMN_RANKING_FEE + " INTEGER DEFAULT 0, "
			+ COLUMN_RANKING_FEE_RANKING_1 + " INTEGER DEFAULT 0, "
			+ COLUMN_RANKING_FEE_RANKING_2 + " INTEGER DEFAULT 0, "
			+ COLUMN_RANKING_FEE_RANKING_3 + " INTEGER DEFAULT 0, "
			+ COLUMN_RANKING_FEE_RANKING_4 + " INTEGER DEFAULT 0, "
			+ COLUMN_RANKING_FEE_RANKING_5 + " INTEGER DEFAULT 0, "
			+ COLUMN_RANKING_FEE_RANKING_6 + " INTEGER DEFAULT 0  " + " );";

	public static void createTable(SQLiteDatabase db) {
		Log.d(TAG, "createTable()");
		db.execSQL(DROP_TABLE_SQL);
		db.execSQL(CREATE_TABLE_SQL);

		updateGameSetting(db, new GameSetting());
	}

	private static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS "
			+ TABLE;

	public static void upgradeTable(SQLiteDatabase db, int oldVersion,
			int newVersion, Context context) {
		Log.d(TAG, "upgradeTable()");
		if (oldVersion < 5) {
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
	}

	public GameSettingDatabaseWorker(Context context) {
		super(context);
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

		GameSetting setting = new GameSetting();
		updateGameSetting(setting);
		return result;
	}

	public void reset() {
		Log.d(TAG, "reset()");
	}

	public void getGameSetting(GameSetting gameSetting) throws SQLException {
		getGameSetting(gameSetting, null);
	}

	public void getGameSetting(GameSetting gameSetting,
			PlayerSetting playerSetting) throws SQLException {
		Log.d(TAG, "getGameSetting()");

		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(TABLE + " G " + " LEFT JOIN "
				+ PlayerSettingDatabaseWorker.TABLE + " P ");

		String sql = queryBuilder.buildQuery(new String[] {
				"G." + COLUMN_HOLE_COUNT, "G." + COLUMN_PLAYER_COUNT,
				"G." + COLUMN_GAME_FEE, "G." + COLUMN_EXTRA_FEE,
				"G." + COLUMN_RANKING_FEE, "G." + COLUMN_DATE,

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

				"P." + PlayerSettingDatabaseWorker.COLUMN_NAME_1,
				"P." + PlayerSettingDatabaseWorker.COLUMN_NAME_2,
				"P." + PlayerSettingDatabaseWorker.COLUMN_NAME_3,
				"P." + PlayerSettingDatabaseWorker.COLUMN_NAME_4,
				"P." + PlayerSettingDatabaseWorker.COLUMN_NAME_5,
				"P." + PlayerSettingDatabaseWorker.COLUMN_NAME_6,
				"P." + PlayerSettingDatabaseWorker.COLUMN_HANDICAP_1,
				"P." + PlayerSettingDatabaseWorker.COLUMN_HANDICAP_2,
				"P." + PlayerSettingDatabaseWorker.COLUMN_HANDICAP_3,
				"P." + PlayerSettingDatabaseWorker.COLUMN_HANDICAP_4,
				"P." + PlayerSettingDatabaseWorker.COLUMN_HANDICAP_5,
				"P." + PlayerSettingDatabaseWorker.COLUMN_HANDICAP_6,
				"P." + PlayerSettingDatabaseWorker.COLUMN_EXTRA_SCORE_1,
				"P." + PlayerSettingDatabaseWorker.COLUMN_EXTRA_SCORE_2,
				"P." + PlayerSettingDatabaseWorker.COLUMN_EXTRA_SCORE_3,
				"P." + PlayerSettingDatabaseWorker.COLUMN_EXTRA_SCORE_4,
				"P." + PlayerSettingDatabaseWorker.COLUMN_EXTRA_SCORE_5,
				"P." + PlayerSettingDatabaseWorker.COLUMN_EXTRA_SCORE_6, },

		null, null, null, null, null);

		open();

		Cursor cursor = null;
		try {
			cursor = mDb.rawQuery(sql, new String[0]);

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

				if (gameSetting != null) {
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
				}

				if (playerSetting != null) {
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
				}

				cursor.moveToNext();
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}

			close();
		}
	}

	public void getGameSetting(GameSetting gameSetting,
			PlayerSetting playerSetting, List<Result> results)
			throws SQLException {
		Log.d(TAG, "getGameSetting()");

		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(TABLE + " G " + " LEFT JOIN "
				+ PlayerSettingDatabaseWorker.TABLE + " P " + " LEFT JOIN "
				+ ResultDatabaseWorker.TABLE + " R ");

		String sql = queryBuilder.buildQuery(new String[] {
				"G." + COLUMN_HOLE_COUNT, "G." + COLUMN_PLAYER_COUNT,
				"G." + COLUMN_GAME_FEE, "G." + COLUMN_EXTRA_FEE,
				"G." + COLUMN_RANKING_FEE, "G." + COLUMN_DATE,

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

				"P." + PlayerSettingDatabaseWorker.COLUMN_NAME_1,
				"P." + PlayerSettingDatabaseWorker.COLUMN_NAME_2,
				"P." + PlayerSettingDatabaseWorker.COLUMN_NAME_3,
				"P." + PlayerSettingDatabaseWorker.COLUMN_NAME_4,
				"P." + PlayerSettingDatabaseWorker.COLUMN_NAME_5,
				"P." + PlayerSettingDatabaseWorker.COLUMN_NAME_6,
				"P." + PlayerSettingDatabaseWorker.COLUMN_HANDICAP_1,
				"P." + PlayerSettingDatabaseWorker.COLUMN_HANDICAP_2,
				"P." + PlayerSettingDatabaseWorker.COLUMN_HANDICAP_3,
				"P." + PlayerSettingDatabaseWorker.COLUMN_HANDICAP_4,
				"P." + PlayerSettingDatabaseWorker.COLUMN_HANDICAP_5,
				"P." + PlayerSettingDatabaseWorker.COLUMN_HANDICAP_6,
				"P." + PlayerSettingDatabaseWorker.COLUMN_EXTRA_SCORE_1,
				"P." + PlayerSettingDatabaseWorker.COLUMN_EXTRA_SCORE_2,
				"P." + PlayerSettingDatabaseWorker.COLUMN_EXTRA_SCORE_3,
				"P." + PlayerSettingDatabaseWorker.COLUMN_EXTRA_SCORE_4,
				"P." + PlayerSettingDatabaseWorker.COLUMN_EXTRA_SCORE_5,
				"P." + PlayerSettingDatabaseWorker.COLUMN_EXTRA_SCORE_6,

				"R." + ResultDatabaseWorker.COLUMN_HOLE_NUMBER,
				"R." + ResultDatabaseWorker.COLUMN_PAR_NUMBER,
				"R." + ResultDatabaseWorker.COLUMN_PLAYER_1_SCORE,
				"R." + ResultDatabaseWorker.COLUMN_PLAYER_2_SCORE,
				"R." + ResultDatabaseWorker.COLUMN_PLAYER_3_SCORE,
				"R." + ResultDatabaseWorker.COLUMN_PLAYER_4_SCORE,
				"R." + ResultDatabaseWorker.COLUMN_PLAYER_5_SCORE,
				"R." + ResultDatabaseWorker.COLUMN_PLAYER_6_SCORE,
				"R." + ResultDatabaseWorker.COLUMN_PLAYER_1_HANDICAP_USED,
				"R." + ResultDatabaseWorker.COLUMN_PLAYER_2_HANDICAP_USED,
				"R." + ResultDatabaseWorker.COLUMN_PLAYER_3_HANDICAP_USED,
				"R." + ResultDatabaseWorker.COLUMN_PLAYER_4_HANDICAP_USED,
				"R." + ResultDatabaseWorker.COLUMN_PLAYER_5_HANDICAP_USED,
				"R." + ResultDatabaseWorker.COLUMN_PLAYER_6_HANDICAP_USED, },

		null, null, null, "R." + ResultDatabaseWorker.COLUMN_HOLE_NUMBER, null);

		open();

		Cursor cursor = null;
		try {
			cursor = mDb.rawQuery(sql, new String[0]);

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
							.getColumnIndex(ResultDatabaseWorker.COLUMN_HOLE_NUMBER);
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

	public void updateGameSetting(GameSetting setting) {
		open();
		try {
			updateGameSetting(mDb, setting);
		} finally {
			close();
		}
	}

	private static void updateGameSetting(SQLiteDatabase db, GameSetting setting) {
		Log.d(TAG, "updateGameSetting()");

		ContentValues args = new ContentValues();

		args.put(COLUMN_KEY_INDEX, 0);
		args.put(COLUMN_HOLE_COUNT, setting.getHoleCount());
		args.put(COLUMN_PLAYER_COUNT, setting.getPlayerCount());
		args.put(COLUMN_GAME_FEE, setting.getGameFee());
		args.put(COLUMN_EXTRA_FEE, setting.getExtraFee());
		args.put(COLUMN_RANKING_FEE, setting.getRankingFee());
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
		args.put(COLUMN_DATE, setting.getDate().getTime());

		db.replace(TABLE, null, args);
	}
}
