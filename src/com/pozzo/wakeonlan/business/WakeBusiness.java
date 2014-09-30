package com.pozzo.wakeonlan.business;

import java.io.IOException;
import java.util.List;

import android.content.Context;

import com.bugsense.trace.BugSenseHandler;
import com.pozzo.wakeonlan.dao.WakeEntryDao;
import com.pozzo.wakeonlan.exception.InvalidMac;
import com.pozzo.wakeonlan.helper.WakeOnLan;
import com.pozzo.wakeonlan.receiver.NetworkConnectionReceiver;
import com.pozzo.wakeonlan.vo.LogObj;
import com.pozzo.wakeonlan.vo.LogObj.Action;
import com.pozzo.wakeonlan.vo.WakeEntry;

/**
 * Business logic for our {@link WakeEntry}.
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-05-03
 */
public class WakeBusiness {

	/**
	 * It inserts a new entry or replaces if the id already exists.
	 * 
	 * @param entry to be inserted.
	 * @param context to start listening network change if needed.
	 */
	public void replace(WakeEntry entry, Context context) {
		long id = new WakeEntryDao().replace(entry);
		if(id > (Integer.MAX_VALUE/2))//Just for precaution
			BugSenseHandler.sendException(new Exception("They have already really big ids"));
		entry.setId(id);

		if(entry.getTriggerSsid() != null && entry.getTriggerSsid().length() > 0) {
			NetworkConnectionReceiver.startListening(true, context);
		}
		//Log it for future tracking.
		new LogBusiness().insert(new LogObj(id, Action.replaced));
	}

	/**
	 * Initialize listener service if needed exist any entry with defined trigger.
	 * 
	 * @param context to start listening network change if needed.
	 */
	public void startNetworkService(Context context) {
		if(getByTrigger("%").size() > 0) {
			NetworkConnectionReceiver.startListening(true, context);
		}
	}

	/**
	 * Will stop service if not more triggers exists, also may disable boot request.
	 * 
	 * @param context to make it work.
	 */
	public void stopNetworkService(Context context) {
		if(getByTrigger("%").isEmpty()) {
			NetworkConnectionReceiver.startListening(false, context);
		}
	}

	/**
	 * Send a wake up message to given entry.
	 * 
	 * @param entry to be sent.
	 * @param log I want to track this, pleas send something correct.
	 * @throws IOException Problem to send package.
	 * @throws InvalidMac Not valid MAC.
	 */
	public void wakeUp(WakeEntry entry, LogObj log) throws IOException, InvalidMac {
		new WakeOnLan().wakeUp(entry.getIp(), entry.getMacAddress(), entry.getPort());
		new LogBusiness().insert(log);
	}

	/**
	 * Get all {@link WakeEntry} objects which match givven trigger.
	 * 
	 * @param trigger to be matched.
	 * @return a list of all matched entries.
	 */
	public List<WakeEntry> getByTrigger(String trigger) {
		return new WakeEntryDao().getByTrigger(trigger);
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
	 * Put to trash list matching ids.
	 * 
	 * @param ids to be deleted.
	 */
	public void trash(long... ids) {
		new WakeEntryDao().delete(ids);
		//Log it for future tracking.
		for(long it : ids)//Well it may be quite heavy... will it really matter here?
			new LogBusiness().insert(new LogObj(it, Action.trashed));
	}

	/**
	 * Delete matching ids.
	 * 
	 * @param ids to be deleted.
	 */
	public void recover(long... ids) {
		new WakeEntryDao().recover(ids);
	}
}
