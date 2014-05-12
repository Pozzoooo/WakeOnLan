package com.pozzo.wakeonlan.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.pozzo.wakeonlan.R;
import com.pozzo.wakeonlan.business.WakeBusiness;
import com.pozzo.wakeonlan.frags.EntriesListFrag;
import com.pozzo.wakeonlan.frags.HelpDialog;

/**
 * Well, this is our Main Activity =D.
 * Our flow is quite simples, user creates an entry, it is saved on Sqlite and showed here, user 
 * 	can interact by editing and using each entry.
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-05-03
 */
public class MainActivity extends Activity {
	private static final int REQ_ADD = 0x1;
	private boolean needsTutorial = false;

	private EntriesListFrag entryListFrag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		entryListFrag = (EntriesListFrag)
				getFragmentManager().findFragmentById(R.id.fragEntryList);

		checkEmptiness();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * User wants to add a new Entry.
	 */
	public void onAdd(MenuItem item) {
		Intent intent = new Intent(this, AddWakeEntryActivity.class);
		startActivityForResult(intent, REQ_ADD);
	}

	/**
	 * User is claiming for help, lets help him!
	 */
	public void onHelp(MenuItem item) {
		HelpDialog dialog = HelpDialog.newInstance();
		dialog.show(getFragmentManager(), "help");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQ_ADD:
			boolean result = resultCode == RESULT_OK;
			if(result)//Refresh if something new.
				entryListFrag.refresh();
			if(needsTutorial)//We show tutorial on first entry.
				entryListFrag.animTutorial(result);
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
			break;
		}
	}

	/**
	 * Check if we have some entry, if not, we try to make it easy to user.
	 */
	private void checkEmptiness() {
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				SharedPreferences pref = getSharedPreferences("configs", MODE_PRIVATE);
				if(pref.getBoolean("firstTimeMainActivity", true)) {
					if(new WakeBusiness().isEmpty()) {
						needsTutorial = true;
						return true;
					}
				}
				return false;
			}

			protected void onPostExecute(Boolean result) {
				if(result)
					onAdd(null);
			}
		}.execute();
	}
}
