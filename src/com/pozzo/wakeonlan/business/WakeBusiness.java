package com.pozzo.wakeonlan.business;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.pozzo.wakeonlan.dao.WakeEntryDao;
import com.pozzo.wakeonlan.helper.WakeOnLan;
import com.pozzo.wakeonlan.receiver.BootReceiver;
import com.pozzo.wakeonlan.receiver.NetworkConnectionListener;
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
	 * @param context to start listening network change if needed.
	 */
	public void replace(WakeEntry entry, Context context) {
		new WakeEntryDao().replace(entry);
		if(entry.getTriggerSsid() != null && entry.getTriggerSsid().length() > 0) {
			context.startService(new Intent(context, NetworkConnectionListener.class));
			BootReceiver.setEnabled(true, context);
		}
	}

	/**
	 * Initialize listener service if needed exist any entry with defined trigger.
	 * 
	 * @param context to start listening network change if needed.
	 */
	public void startNetworkService(Context context) {
		if(getByTrigger("%").size() > 0) {
			context.startService(new Intent(context, NetworkConnectionListener.class));
		}
	}

	/**
	 * Will stop service if not more triggers exists, also may disable boot request.
	 * 
	 * @param context to make it work.
	 */
	public void stopNetworkService(Context context) {
		if(getByTrigger("%").isEmpty()) {
			context.stopService(new Intent(context, NetworkConnectionListener.class));
			BootReceiver.setEnabled(false, context);
		}
	}

	/**
	 * Send a wake up message to given entry.
	 * 
	 * @param entry
	 * @throws IOException
	 */
	public void wakeUp(WakeEntry entry) throws IOException {
		new WakeOnLan().wakeUp(entry.getIp(), entry.getMacAddress(), entry.getPort());
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
	public WakeEntry get(int id) {
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

	/**
	 * Delete matching ids.
	 * 
	 * @param ids to be deleted.
	 */
	public void recover(long... ids) {
		new WakeEntryDao().recover(ids);
	}
}
