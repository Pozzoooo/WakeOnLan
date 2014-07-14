package com.pozzo.wakeonlan.database;

import java.util.Date;

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
	public static final String TRIGGER_SSID = "trigger";
	public static final String DELETED_DATE = "deleted_date";

	/**
	 * Create Table SQL.
	 */
	public static final String TB_CREATE = "create table " + TB_NAME + "( " +
			_ID + " integer primary key autoincrement, " +
			MAC_ADDRESS + " varchar unique not null, " +
			NAME + " varchar, " + 	
			IP + " varchar not null, " +
			PORT + " integer not null, " +
			TRIGGER_SSID + " varchar, " +
			DELETED_DATE + " bigint" +
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
		values.put(TRIGGER_SSID, entry.getTriggerSsid());

		//Special handle for deleted date
		if(entry.getDeletedDate() != null) {
			values.put(DELETED_DATE, entry.getDeletedDate().getTime());
		} else {
			values.putNull(DELETED_DATE);
		}

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
		entry.setTriggerSsid(cursor.getString(cursor.getColumnIndex(TRIGGER_SSID)));

		//Special handle for deleted date
		int idx = cursor.getColumnIndex(DELETED_DATE);
		if(!cursor.isNull(idx)) {
			entry.setDeletedDate(new Date(cursor.getLong(idx)));
		}

		return entry;
	}
}
