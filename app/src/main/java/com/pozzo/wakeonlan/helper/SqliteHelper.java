package com.pozzo.wakeonlan.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pozzo.wakeonlan.database.LogCr;
import com.pozzo.wakeonlan.database.WakeEntryCr;
import com.pozzo.wakeonlan.database.WidgetControlCr;
import com.splunk.mint.Mint;

/**
 * Sqlite Helper...............
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-05-03
 */
public class SqliteHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 7;
	private static final String DB_NAME = "db.db";

	public SqliteHelper(Context context) {
		super(context, DB_NAME, null, DATABASE_VERSION);
	}

	/**
	 * System called.
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.execSQL(WakeEntryCr.TB_CREATE);
			db.execSQL(WidgetControlCr.TB_CREATE);
			db.execSQL(LogCr.TB_CREATE);
		} catch(RuntimeException e) {
			//Some devices seems to call it more than once, I need to check why!
			Mint.logException(e);
		}
	}

	/**
	 * System called.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		switch (oldVersion) {
		case 1:
            //Full creation at step 5... maintain for historic only?
//			db.execSQL("ALTER TABLE " + WakeEntryCr.TB_NAME + " ADD COLUMN "
//					+ WakeEntryCr.DELETED_DATE + " bigint;");
		case 2:
			db.execSQL(WidgetControlCr.TB_CREATE);
		case 3:
            //Full creation at step 5... maintain for historic only?
//			db.execSQL("ALTER TABLE " + WakeEntryCr.TB_NAME + " ADD COLUMN "
//					+ WakeEntryCr.LAST_WOL_SENT_DATE + " bigint;");
			db.execSQL(LogCr.TB_CREATE);
		case 4:
            //Full creation at step 5... maintain for historic only?
//			db.execSQL("ALTER TABLE " + WakeEntryCr.TB_NAME + " ADD COLUMN "
//					+ WakeEntryCr.WOL_COUNT + " integer;");
        case 5:
            //Remove unique MAC field (user may want to have internal and external entries)
            db.execSQL("ALTER TABLE " + WakeEntryCr.TB_NAME + " RENAME TO tempWake");
            db.execSQL(WakeEntryCr.TB_CREATE);
            db.execSQL("INSERT INTO " + WakeEntryCr.TB_NAME + " SELECT * FROM tempWake");
            db.execSQL("DROP TABLE tempWake");
		case 6:
			//Add new fields
			db.execSQL("ALTER TABLE " + WakeEntryCr.TB_NAME + " ADD COLUMN "
					+ WakeEntryCr.TIME_RANGE_INDEX + " integer;");
			db.execSQL("ALTER TABLE " + WakeEntryCr.TB_NAME + " ADD COLUMN "
					+ WakeEntryCr.START_LIMIT + " integer;");
			db.execSQL("ALTER TABLE " + WakeEntryCr.TB_NAME + " ADD COLUMN "
					+ WakeEntryCr.END_LIMIT + " integer;");
		default:
			break;
		}
	}
}
