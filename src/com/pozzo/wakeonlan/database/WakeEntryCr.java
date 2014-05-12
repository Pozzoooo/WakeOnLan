package com.pozzo.wakeonlan.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.pozzo.wakeonlan.vo.WakeEntry;

/**
 * We kind of represents our object table here, and make some helpers for manipulation.
 * 
 * @author Luiz Gustavo Pozoz
 * @since 2014-05-03
 */
public class WakeEntryCr {
	public static final String TB_NAME = "wakeEntry";

	public static final String _ID = "_id";
	public static final String MAC_ADDRESS = "mac";
	public static final String NAME = "name";
	public static final String IP = "ip";
	public static final String PORT = "port";

	/**
	 * Create Table SQL.
	 */
	public static final String TB_CREATE = "create table " + TB_NAME + "( " +
			_ID + " integer primary key autoincrement, " +
			MAC_ADDRESS + " varchar unique not null, " +
			NAME + " varchar, " + 	
			IP + " varchar not null, " +
			PORT + " integer not null" +
		");";

	/**
	 * @return ContentValues for given entry.
	 */
	public static ContentValues getContentValues(WakeEntry entry) {
		ContentValues values = new ContentValues();

		if(entry.getId() != 0)
			values.put(_ID, entry.getId());
		values.put(MAC_ADDRESS, entry.getMacAddress());
		values.put(NAME, entry.getName());
		values.put(IP, entry.getIp());
		values.put(PORT, entry.getPort());

		return values;
	}

	/**
	 * @return Object contained on current position of given cursor.
	 */
	public static WakeEntry objectFrom(Cursor cursor) {
		WakeEntry entry = new WakeEntry();

		entry.setId(cursor.getLong(cursor.getColumnIndex(_ID)));
		entry.setMacAddress(cursor.getString(cursor.getColumnIndex(MAC_ADDRESS)));
		entry.setName(cursor.getString(cursor.getColumnIndex(NAME)));
		entry.setIp(cursor.getString(cursor.getColumnIndex(IP)));
		entry.setPort(cursor.getInt(cursor.getColumnIndex(PORT)));

		return entry;
	}
}
