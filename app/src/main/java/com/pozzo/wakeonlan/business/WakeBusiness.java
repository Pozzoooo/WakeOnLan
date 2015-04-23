package com.pozzo.wakeonlan.business;

import android.content.Context;

import com.pozzo.wakeonlan.App;
import com.pozzo.wakeonlan.dao.WakeEntryDao;
import com.pozzo.wakeonlan.exception.InvalidMac;
import com.pozzo.wakeonlan.helper.NetworkUtils;
import com.pozzo.wakeonlan.helper.WakeOnLan;
import com.pozzo.wakeonlan.receiver.NetworkConnectionReceiver;
import com.pozzo.wakeonlan.vo.LogObj;
import com.pozzo.wakeonlan.vo.LogObj.Action;
import com.pozzo.wakeonlan.vo.WakeEntry;
import com.splunk.mint.Mint;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;
import java.util.List;

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
        long id = replaceInternal(entry, context);
		if(id > (Integer.MAX_VALUE/2))//Just for precaution
			Mint.logException(new Exception("They have already really big ids"));
		entry.setId(id);

		//Log it for future tracking.
		new LogBusiness().insert(new LogObj(id, Action.replaced));
	}

    /**
     * Inserts a new entry, for internal use (no logs and checks).
     *
     * @param entry to be inserted.
     * @param context to start listening network change if needed.
     */
    private long replaceInternal(WakeEntry entry, Context context) {
        long id = new WakeEntryDao().replace(entry);

        if(entry.getTriggerSsid() != null && entry.getTriggerSsid().length() > 0) {
            NetworkConnectionReceiver.startListening(true, context);
        }
        return id;
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
		markNewSent(entry);

        //We also sent on current broadcast, just to make sure user did not forget that
        wakeUpCurrentBroadcast(entry);
	}

    /**
     * Sent on the current network broadcast.
     * If and only if we can get the network and it is not the same broadcast as the one saved on
     *  entry.
     *
     * @param entry to be sent.
     * @throws IOException Problem to send package.
     * @throws InvalidMac Not valid MAC.
     */
    private void wakeUpCurrentBroadcast(WakeEntry entry) throws IOException, InvalidMac {
        String currentBroadcast;
        try {
            NetworkUtils utils = new NetworkUtils();
            InetAddress myBroad = utils.getMyBroadcast();
            currentBroadcast = myBroad.getHostAddress();
            //Well we don't sent if it is the same entry or can't get current broadcast.
            if(currentBroadcast != null && !currentBroadcast.equals(entry.getIp()))
                new WakeOnLan().wakeUp(currentBroadcast, entry.getMacAddress(), entry.getPort());
        } catch(SocketException e) {
            //Ignore, just don't send
        }
    }

	/**
	 * This just updates with a new count sent for this wol entry.
	 * 
	 * @param entry to be marker with a +1 count.
	 */
	public void markNewSent(WakeEntry entry) {
		entry.setLastWolSentDate(new Date());
		entry.increasCount();
        replaceInternal(entry, App.getAppContext());
	}

	/**
	 * Get all {@link WakeEntry} objects which match givven trigger.
	 * 
	 * @param trigger to be matched.
	 * @return a list of all matched entries.
	 */
	public List<WakeEntry> getByTrigger(String trigger) {
		if(trigger == null)//Just to make sure
			return null;
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
