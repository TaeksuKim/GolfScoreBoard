package org.dolicoli.android.golfscoreboard.db;

import org.dolicoli.android.golfscoreboard.data.settings.GameSetting;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
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

	public void getGameSetting(GameSetting setting) throws SQLException {
		Log.d(TAG, "getGameSetting()");

		open();

		Cursor cursor = null;
		try {
			cursor = mDb.query(true, TABLE,
					new String[] { COLUMN_HOLE_COUNT, COLUMN_PLAYER_COUNT,
							COLUMN_GAME_FEE, COLUMN_EXTRA_FEE,
							COLUMN_HOLE_FEE_RANKING_1,
							COLUMN_HOLE_FEE_RANKING_2,
							COLUMN_HOLE_FEE_RANKING_3,
							COLUMN_HOLE_FEE_RANKING_4,
							COLUMN_HOLE_FEE_RANKING_5,
							COLUMN_HOLE_FEE_RANKING_6, COLUMN_DATE,
							COLUMN_RANKING_FEE, COLUMN_RANKING_FEE_RANKING_1,
							COLUMN_RANKING_FEE_RANKING_2,
							COLUMN_RANKING_FEE_RANKING_3,
							COLUMN_RANKING_FEE_RANKING_4,
							COLUMN_RANKING_FEE_RANKING_5,
							COLUMN_RANKING_FEE_RANKING_6, }, null, null, null,
					null, null, null);

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
				holeFeeRanking1 = cursor.getInt(offset++);
				holeFeeRanking2 = cursor.getInt(offset++);
				holeFeeRanking3 = cursor.getInt(offset++);
				holeFeeRanking4 = cursor.getInt(offset++);
				holeFeeRanking5 = cursor.getInt(offset++);
				holeFeeRanking6 = cursor.getInt(offset++);
				date = cursor.getLong(offset++);
				rankingFee = cursor.getInt(offset++);
				rankingFeeRanking1 = cursor.getInt(offset++);
				rankingFeeRanking2 = cursor.getInt(offset++);
				rankingFeeRanking3 = cursor.getInt(offset++);
				rankingFeeRanking4 = cursor.getInt(offset++);
				rankingFeeRanking5 = cursor.getInt(offset++);
				rankingFeeRanking6 = cursor.getInt(offset++);

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

				// Log.d(TAG, "Hole count: " + setting.getHoleCount());
				// Log.d(TAG, "Player count: " + setting.getPlayerCount());
				// Log.d(TAG, "Game fee: " + setting.getGameFee());
				// Log.d(TAG, "Extra fee: " + setting.getExtraFee());
				// Log.d(TAG, "Ranking fee: " + setting.getRankingFee());
				// Log.d(TAG, "Hole fee (1): " + setting.getHoleFee(1));
				// Log.d(TAG, "Hole fee (2): " + setting.getHoleFee(2));
				// Log.d(TAG, "Hole fee (3): " + setting.getHoleFee(3));
				// Log.d(TAG, "Hole fee (4): " + setting.getHoleFee(4));
				// Log.d(TAG, "Hole fee (5): " + setting.getHoleFee(5));
				// Log.d(TAG, "Hole fee (6): " + setting.getHoleFee(6));
				// Log.d(TAG, "Ranking fee (1): " + setting.getRankingFee(1));
				// Log.d(TAG, "Ranking fee (2): " + setting.getRankingFee(2));
				// Log.d(TAG, "Ranking fee (3): " + setting.getRankingFee(3));
				// Log.d(TAG, "Ranking fee (4): " + setting.getRankingFee(4));
				// Log.d(TAG, "Ranking fee (5): " + setting.getRankingFee(5));
				// Log.d(TAG, "Ranking fee (6): " + setting.getRankingFee(6));

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
		// Log.d(TAG, "Hole count: " + setting.getHoleCount());
		// Log.d(TAG, "Player count: " + setting.getPlayerCount());
		// Log.d(TAG, "Game fee: " + setting.getGameFee());
		// Log.d(TAG, "Extra fee: " + setting.getExtraFee());
		// Log.d(TAG, "Ranking fee: " + setting.getRankingFee());
		// Log.d(TAG, "Hole fee (1): " + setting.getHoleFee(1));
		// Log.d(TAG, "Hole fee (2): " + setting.getHoleFee(2));
		// Log.d(TAG, "Hole fee (3): " + setting.getHoleFee(3));
		// Log.d(TAG, "Hole fee (4): " + setting.getHoleFee(4));
		// Log.d(TAG, "Hole fee (5): " + setting.getHoleFee(5));
		// Log.d(TAG, "Hole fee (6): " + setting.getHoleFee(6));
		// Log.d(TAG, "Ranking fee (1): " + setting.getRankingFee(1));
		// Log.d(TAG, "Ranking fee (2): " + setting.getRankingFee(2));
		// Log.d(TAG, "Ranking fee (3): " + setting.getRankingFee(3));
		// Log.d(TAG, "Ranking fee (4): " + setting.getRankingFee(4));
		// Log.d(TAG, "Ranking fee (5): " + setting.getRankingFee(5));
		// Log.d(TAG, "Ranking fee (6): " + setting.getRankingFee(6));

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
