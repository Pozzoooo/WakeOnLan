package com.pozzo.wakeonlan.business;

import android.content.Context;
import android.os.AsyncTask;

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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
	 * Asyncrhonous start service if needed.
	 */
	public void requestStartNetworkService(final Context context) {
		new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... params) {
				return getByTrigger("%").size() > 0;
			}

			@Override
			protected void onPostExecute(Boolean shouldStart) {
				if(shouldStart)
					NetworkConnectionReceiver.startListening(true, context);
			}
		}.execute();
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
		try {
			wakeUpCurrentBroadcast(entry);
		} catch(IOException e) {
			//We ignore, as it is supposed to be a plus only
		}
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
		NetworkUtils utils = new NetworkUtils();
		InetAddress myBroad = utils.getMyBroadcast();
		currentBroadcast = myBroad == null ? null : myBroad.getHostAddress();
		//Well we don't sent if it is the same entry or can't get current broadcast.
		if(currentBroadcast != null && !currentBroadcast.equals(entry.getIp()))
			new WakeOnLan().wakeUp(currentBroadcast, entry.getMacAddress(), entry.getPort());
		else
			throw new IOException("Null broadcast!");
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

	/**
	 * This is where we defines our time frames.
	 * Also take a look at @arrays/timeFrames
	 *
	 * @param entry to be setted to.
	 * @param choosenRange The range which will be converted.
	 */
	public void setTimeRange(WakeEntry entry, int choosenRange) {
		Calendar startTime = GregorianCalendar.getInstance();
		startTime.set(Calendar.MINUTE, 0);
		startTime.set(Calendar.SECOND, 0);
		startTime.set(Calendar.MILLISECOND, 0);
		Calendar endTime = GregorianCalendar.getInstance();
		endTime.set(Calendar.MINUTE, 59);
		endTime.set(Calendar.SECOND, 59);
		endTime.set(Calendar.MILLISECOND, 999);
		switch (choosenRange) {
			case 0://all day
			default:
				startTime.set(Calendar.HOUR_OF_DAY, 0);
				endTime.set(Calendar.HOUR_OF_DAY, 23);
				break;
			case 1://morning
				startTime.set(Calendar.HOUR_OF_DAY, 5);
				endTime.set(Calendar.HOUR_OF_DAY, 11);
				break;
			case 2://afternoon
				startTime.set(Calendar.HOUR_OF_DAY, 11);
				endTime.set(Calendar.HOUR_OF_DAY, 19);
				break;
			case 3://evening
				startTime.set(Calendar.HOUR_OF_DAY, 18);
				endTime.set(Calendar.HOUR_OF_DAY, 23);
				break;
			case 4://night
				startTime.set(Calendar.HOUR_OF_DAY, 0);
				endTime.set(Calendar.HOUR_OF_DAY, 8);
				break;
		}
		entry.setStartLimit(startTime.getTimeInMillis());
		entry.setEndLimit(endTime.getTimeInMillis());
	}
}
