package com.pozzo.wakeonlan;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.splunk.mint.Mint;

/**
 * This is our APP!
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-05-03
 *
 * TODO show advanced is holding state?
 * TODO Validated timeFrame funcionality.
 */
public class App extends Application {
	private static App appContext;
	private Tracker tracker;

	@Override
	public void onCreate() {
		super.onCreate();
		Mint.initAndStartSession(this, "22c71818");

		appContext = this;
	}

	/**
	 * Not My favorite approach, but I want to make it clean, and our app is simple based on 
	 * 	Activities, so it is not suppose to be a problem.
	 */
	public static App getAppContext() {
		return appContext;
	}

	/**
	 * To track with google analytics.
	 */
	public synchronized Tracker getTracker() {
		if(tracker == null) {
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			tracker = analytics.newTracker(R.xml.global_tracker);
			tracker.enableAdvertisingIdCollection(true);
		}
		return  tracker;
	}
}
