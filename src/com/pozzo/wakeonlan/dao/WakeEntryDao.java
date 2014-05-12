package com.pozzo.wakeonlan.dao;

import java.util.Arrays;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pozzo.wakeonlan.database.ConexaoDBManager;
import com.pozzo.wakeonlan.database.WakeEntryCr;
import com.pozzo.wakeonlan.vo.WakeEntry;

/**
 * Low level manipulation for {@link WakeEntry}.
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-05-03
 */
public class WakeEntryDao {

	/**
	 * @param entry to be persisted.
	 */
	public void replace(WakeEntry entry) {
		SQLiteDatabase db = new ConexaoDBManager().getDb();
		db.replace(WakeEntryCr.TB_NAME, null, WakeEntryCr.getContentValues(entry));
	}

	/**
	 * @return If it has any persisted entry.
	 */
	public boolean isEmpty() {
		SQLiteDatabase db = new ConexaoDBManager().getDb();
		Cursor cursor = db.rawQuery(
				"SELECT count(*) FROM " + WakeEntryCr.TB_NAME + " LIMIT 1", null);
		cursor.moveToFirst();
		return cursor.getInt(0) == 0;
	}

	/**
	 * Get a single entry by its unique id.
	 * 
	 * @param id PK.
	 * @return the Entry.
	 */
	public WakeEntry get(long id) {
		WakeEntry entry = null;
		SQLiteDatabase db = new ConexaoDBManager().getDb();
		Cursor cursor = db.query(WakeEntryCr.TB_NAME, null, WakeEntryCr._ID + "=?", 
				new String[] {""+id}, null, null, null);
		if(cursor.moveToNext()) {
			entry = WakeEntryCr.objectFrom(cursor);
		}
		return entry;
	}

	/**
	 * Deletes all WakeEntry by ids.
	 * 
	 * @param ids to be removed.
	 */
	public void delete(long... ids) {
		String idsStr = Arrays.toString(ids);
		idsStr = idsStr.substring(1, idsStr.length()-1);
		SQLiteDatabase db = new ConexaoDBManager().getDb();
		db.execSQL("DELETE FROM " + WakeEntryCr.TB_NAME + " WHERE " + 
				WakeEntryCr._ID + " IN (" + idsStr + ")");
		
	}
}
