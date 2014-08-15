package com.pozzo.wakeonlan.business;

import java.util.List;

import com.bugsense.trace.BugSenseHandler;
import com.pozzo.wakeonlan.dao.WidgetControlDao;
import com.pozzo.wakeonlan.vo.WakeEntry;

/**
 * Business logic for Widget control.
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-08-10
 */
public class WidgetControlBusiness {

	/**
	 * Insert a new widget with its related entries ids.
	 * 
	 * @param widgetId added.
	 * @param wakeEntryId related to the given widget.
	 */
	public void insert(int widgetId, WakeEntry ...wakeEntries) {
		int[] ids = new int[wakeEntries.length];
		for(int i=0; i<wakeEntries.length; ++i)
			ids[i] = wakeEntries[i].getId();

		new WidgetControlDao().insert(widgetId, ids);
	}

	/**
	 * To remove from our widget database.
	 * 
	 * @param widgetId to be removed.
	 * @return number of rows removed.
	 */
	public int delete(int ...widgetId) {
		if(widgetId == null || widgetId.length == 0) {
			//Handling only for acknowledge behavior
			BugSenseHandler.sendException(new RuntimeException("Sent 0 or null: " + widgetId));
			return 0;//Deleting nothing?
		}
		return new WidgetControlDao().delete(widgetId);
	}

	/**
	 * Delete all rows on WidgetControl table.
	 * 
	 * @return number of removed entries.
	 */
	public int deleteAll() {
		return new WidgetControlDao().deleteAll();
	}

	/**
	 * Get all wakeEntries ids attached to the given widget.
	 * 
	 * @param widgetId related.
	 * @return WakeEntry ids.
	 */
	public List<Integer> getWakeEntriesFromWidget(int widgetId) {
		return new WidgetControlDao().getWakeEntriesFromWidget(widgetId);
	}
}
