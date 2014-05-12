package com.pozzo.wakeonlan.business;

import com.pozzo.wakeonlan.dao.WakeEntryDao;
import com.pozzo.wakeonlan.vo.WakeEntry;

/**
 * Business logic for our {@link WakeEntry}.
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-05-03
 */
public class WakeBusiness {

	/**
	 * @param entry to be inserted.
	 */
	public void replace(WakeEntry entry) {
		new WakeEntryDao().replace(entry);
	}

	/**
	 * @return true if there is no entry saved.
	 */
	public boolean isEmpty() {
		return new WakeEntryDao().isEmpty();
	}

	/**
	 * Get a single entry by its unique id.
	 * 
	 * @param id PK.
	 * @return the Entry.
	 */
	public WakeEntry get(long id) {
		return new WakeEntryDao().get(id);
	}

	/**
	 * Delete matching ids.
	 * 
	 * @param ids to be deleted.
	 */
	public void delete(long... ids) {
		new WakeEntryDao().delete(ids);
	}
}
