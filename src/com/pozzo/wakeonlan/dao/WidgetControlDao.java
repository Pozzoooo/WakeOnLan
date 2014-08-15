package com.pozzo.wakeonlan.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pozzo.wakeonlan.database.ConexaoDBManager;
import com.pozzo.wakeonlan.database.WidgetControlCr;

/**
 * Handle WidgetControl persistence.
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-08-10
 */
public class WidgetControlDao {

	/**
	 * New entry control to be inserted.
	 *  
	 * @param widgetId to be attached.
	 * @param wakeEntryId which should be attached to the widget.
	 */
	public void insert(int widgetId, int ...wakeEntryId) {
		SQLiteDatabase db = new ConexaoDBManager().getDb();
		for(int it : wakeEntryId)
			db.insert(WidgetControlCr.TB_NAME, null, 
					WidgetControlCr.getContentValues(widgetId, it));
	}

	/**
	 * Deletes all widget entries related to the given id.s
	 * 
	 * @param widgetId to be deleted.
	 * @return number of deleted entries.
	 */
	public int delete(int ...widgetId) {
		StringBuilder ids = new StringBuilder();
		ids.append(widgetId[0]);
		for (int i = 1; i < widgetId.length; i++) {
			ids.append(",");
			ids.append(widgetId[i]);
        }

		SQLiteDatabase db = new ConexaoDBManager().getDb();
		return db.delete(WidgetControlCr.TB_NAME, 
				WidgetControlCr.WIDGET_ID + " in (?)", new String[] {ids.toString()});
	}

	/**
	 * Delete all rows on WidgetControl table.
	 * 
	 * @return number of removed entries.
	 */
	public int deleteAll() {
		SQLiteDatabase db = new ConexaoDBManager().getDb();
		return db.delete(WidgetControlCr.TB_NAME, null, null);
	}

	/**
	 * Get all wakeEntries ids attached to the given widget.
	 * 
	 * @param widgetId related.
	 * @return WakeEntry ids.
	 */
	public List<Integer> getWakeEntriesFromWidget(int widgetId) {
		SQLiteDatabase db = new ConexaoDBManager().getDb();
		Cursor cursor = db.query(WidgetControlCr.TB_NAME, new String[] {WidgetControlCr.WAKE_ENTRY}, 
				WidgetControlCr.WIDGET_ID + "=?", new String[] {""+widgetId}, null, null, null);
		List<Integer> ids = new ArrayList<Integer>();

		while(cursor.moveToNext()) {
			ids.add(cursor.getInt(0));
		}

		return ids;
	}
}
