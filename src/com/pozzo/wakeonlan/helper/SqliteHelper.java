package com.pozzo.wakeonlan.helper;

import com.pozzo.wakeonlan.database.WakeEntryCr;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Sqlite Helper...............
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-05-03
 */
public class SqliteHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static final String DB_NAME = "db.db";

	public SqliteHelper(Context context) {
		super(context, DB_NAME, null, DATABASE_VERSION);
	}

	/**
	 * System called.
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(WakeEntryCr.TB_CREATE);
	}

	/**
	 * System called.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		db.execSQL("DROP TABLE IF EXISTS " + WakeEntryCr.TB_NAME);

		onCreate(db);
	}
}
