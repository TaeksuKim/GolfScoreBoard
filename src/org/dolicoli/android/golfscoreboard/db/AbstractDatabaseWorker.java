package org.dolicoli.android.golfscoreboard.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public abstract class AbstractDatabaseWorker {

	protected SQLiteDatabase mDb;

	protected Context context;
	protected DatabaseHelper dbHelper;

	public AbstractDatabaseWorker(Context ctx) {
		this.context = ctx;
	}

	public AbstractDatabaseWorker open() throws SQLException {
		if (context == null)
			return null;

		dbHelper = new DatabaseHelper(context);
		mDb = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}
}
