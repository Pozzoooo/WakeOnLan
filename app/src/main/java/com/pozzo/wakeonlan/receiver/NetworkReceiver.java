package com.pozzo.wakeonlan.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.splunk.mint.Mint;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This receiver will be called when network state got changed.
 *
 * @author Luiz Gustavo Pozzo
 * @since 27/08/15.
 */
public class NetworkReceiver extends BroadcastReceiver {
	private static NetworkReceiver instance;
	private HashMap<String, OnConnectionState> observers;

	private NetworkReceiver() {
		observers = new HashMap<>();
	}

	/**
	 * We use a single instance for all network observers.
	 */
	public static NetworkReceiver getInstance(OnConnectionState callback, Context context) {
		synchronized (NetworkReceiver.class) {
			if (instance == null) {
				instance = new NetworkReceiver();
				IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
				context.getApplicationContext().registerReceiver(instance, filter);
			}
			instance.observers.put(context.getClass().getName(), callback);
		}
		return instance;
	}

	/**
	 * Removes observer.
	 */
	public void unregister(Context context) {
		observers.remove(context.getClass().getName());
		synchronized (NetworkReceiver.class) {
			if(observers == null || observers.size() == 0) {
				if(instance != null) {
					context.getApplicationContext().unregisterReceiver(instance);
					instance = null;
				}
			}
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (!action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			Mint.logException(new Exception(
					"NetworkReceiver called wrong!? " + action + " intent: " + intent));
			return;
		}

		if(observers == null || observers.size() == 0) {
			unregister(context);
		} else {
			ArrayList<OnConnectionState> values = new ArrayList<>(observers.values());
			for(OnConnectionState it : values)
				it.onConnectionState(isNetworkAvailable(context));
		}
	}

	/**
	 * Called when network state has benn changed.
	 */
	public interface OnConnectionState {
		void onConnectionState(boolean isConnected);
	}

	/**
	 * @return If network is available.
	 */
	private boolean isNetworkAvailable(Context context) {
		if(context == null)
			return false;

		ConnectivityManager connectivityManager
				= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}
