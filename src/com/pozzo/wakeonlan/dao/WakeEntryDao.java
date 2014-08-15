package com.pozzo.wakeonlan.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	public WakeEntry get(int id) {
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
	 * Look for all {@link WakeEntry} objects saved on database which match the exactly trigger 
	 * 	network.
	 * 
	 * @param trigger to be matched.
	 * @return a list of all matched entries.
	 */
	public List<WakeEntry> getByTrigger(String trigger) {
		List<WakeEntry> entries = new ArrayList<WakeEntry>();
		SQLiteDatabase db = new ConexaoDBManager().getDb();
		Cursor cursor = db.query(WakeEntryCr.TB_NAME, null, WakeEntryCr.TRIGGER_SSID + " like ?", 
				new String[] {trigger}, null, null, null);
		while(cursor.moveToNext()) {
			entries.add(WakeEntryCr.objectFrom(cursor));
		}
		return entries;
	}

	/**
	 * It will recover deleted items which remains on database.
	 * 
	 * @param ids to be recovered.
	 */
	public void recover(long... ids) {
		String idsStr = Arrays.toString(ids);
		idsStr = idsStr.substring(1, idsStr.length()-1);
		SQLiteDatabase db = new ConexaoDBManager().getDb();
		db.execSQL("UPDATE " + WakeEntryCr.TB_NAME 
				+ " SET " + WakeEntryCr.DELETED_DATE + " = NULL "
				+ " WHERE " + WakeEntryCr._ID + " IN (" + idsStr + ")");
	}

	/**
	 * Add a timestamp to field 'deleted_date'.
	 * 
	 * @param ids to be removed.
	 * @return deleted date.
	 */
	public long delete(long... ids) {
		String idsStr = Arrays.toString(ids);
		idsStr = idsStr.substring(1, idsStr.length()-1);
		SQLiteDatabase db = new ConexaoDBManager().getDb();
		long now = System.currentTimeMillis();
		db.execSQL("UPDATE " + WakeEntryCr.TB_NAME 
				+ " SET " + WakeEntryCr.DELETED_DATE + " = " + now
				+ " WHERE " + WakeEntryCr._ID + " IN (" + idsStr + ")");
		return now;
	}

	/**
	 * Deletes all WakeEntry by ids, this operations is IRREVERSIBLE.
	 * 
	 * @param ids to be removed.
	 */
	public void deleteForever(long... ids) {
		String idsStr = Arrays.toString(ids);
		idsStr = idsStr.substring(1, idsStr.length()-1);
		SQLiteDatabase db = new ConexaoDBManager().getDb();
		db.execSQL("DELETE FROM " + WakeEntryCr.TB_NAME + " WHERE " + 
				WakeEntryCr._ID + " IN (" + idsStr + ")");
	}
}
