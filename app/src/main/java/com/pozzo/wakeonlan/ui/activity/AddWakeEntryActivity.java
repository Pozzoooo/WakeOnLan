package com.pozzo.wakeonlan.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.pozzo.wakeonlan.App;
import com.pozzo.wakeonlan.R;
import com.pozzo.wakeonlan.business.WakeBusiness;
import com.pozzo.wakeonlan.helper.ItemMenuHelper;
import com.pozzo.wakeonlan.ui.frags.WakeEntryFrag;
import com.pozzo.wakeonlan.vo.WakeEntry;

/**
 * Where our lovely User will input a new Entry.
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-05-03
 */
public class AddWakeEntryActivity extends AppCompatActivity {
	public static final String PARAM_WAKE_ENTRY = "paramWakeEntry";

	private WakeEntryFrag wakeFrag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_wake_entry);

		WakeEntry entry = null;
		Bundle extras = getIntent().getExtras();
		if(extras != null)
			entry = (WakeEntry) extras.getSerializable(PARAM_WAKE_ENTRY);

		wakeFrag = WakeEntryFrag.newWakeEntryFrag(entry);
		getFragmentManager().beginTransaction().add(R.id.fragment_container, wakeFrag).commit();

		ItemMenuHelper.setDoneDiscard(getSupportActionBar(), this);
	}

	@Override
	protected void onStart() {
		super.onStart();

		Tracker tracker = ((App) getApplication()).getTracker();
		tracker.setScreenName(this.getClass().getName());
		tracker.send(new HitBuilders.AppViewBuilder().build());
	}

	/**
	 * Discard input.
	 */
	public void onDiscard(View v) {
		setResult(RESULT_CANCELED);
		finish();
	}

	/**
	 * Confirm input.
	 */
	public void onDone(View v) {
		//We validate, but let user keep saving.
		wakeFrag.validateMac();

		WakeEntry entry = wakeFrag.getWakeEntry();
		new WakeBusiness().replace(entry, this);

		setResult(RESULT_OK);
		finish();
	}
}
