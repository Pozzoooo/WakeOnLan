package com.pozzo.wakeonlan.receiver;

import java.io.IOException;
import java.util.List;

import com.bugsense.trace.BugSenseHandler;
import com.pozzo.wakeonlan.business.WakeBusiness;
import com.pozzo.wakeonlan.vo.WakeEntry;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

/**
 * This a service which will handle broadcast life cycle and maintain it alive in background.
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-06-11
 */
public class NetworkConnectionListener extends Service {
	private BroadcastReceiver receiver;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		startListening();
		return START_STICKY;
	}

	/**
	 * Begins listening any network change.
	 */
	public void startListening() {
		if(receiver == null) {
			receiver = new Receiver();
			registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		}
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * Get the current connected network name.
	 * 
	 * @param ctx
	 * @return
	 */
	public static String getNetworkSsid(Context ctx) {
		WifiManager wifiMgr = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
		String networkSsid = wifiInfo.getSSID();
		//If it is a valid UTF8 name we handle it
		if(networkSsid != null && networkSsid.endsWith("\"") 
				&& networkSsid.startsWith("\"")) {
			networkSsid = networkSsid.substring(1, networkSsid.length()-1);
			return networkSsid;
		}
		return null;
	}

	/**
	 * Network Broadcast Receiver, which will tell me when is the right moment to act.
	 * 
	 * @author Luiz Gustavo Pozzo
	 * @since 2014-06-09
	 */
	private class Receiver extends BroadcastReceiver {
		private String networkSsid;

		@Override
		public void onReceive(final Context ctx, Intent intent) {
			boolean noConnectivity = 
				intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
	
			if(!noConnectivity) {
				networkSsid = getNetworkSsid(ctx);

				Thread background = new Thread() {
					public void run() {
						WakeBusiness wakeBus = new WakeBusiness();
						List<WakeEntry> entries = wakeBus.getByTrigger(networkSsid);
						for(WakeEntry it : entries) {
							try {
								wakeBus.wakeUp(it);
							} catch (IOException e) {
								Log.e("IO", it.getName() + " " + e.getMessage());
								BugSenseHandler.sendException(e);
							}
						}
					}
				};
				background.setName("AutoWakingUp");
				background.setDaemon(false);
				background.start();
			}
		}
	}
}
