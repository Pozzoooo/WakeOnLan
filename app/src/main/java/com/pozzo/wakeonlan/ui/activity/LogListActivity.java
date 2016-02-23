package com.pozzo.wakeonlan.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.pozzo.wakeonlan.App;
import com.pozzo.wakeonlan.R;
import com.pozzo.wakeonlan.business.LogBusiness;
import com.pozzo.wakeonlan.ui.frags.LogListFrag;

/**
 * Will hold log fragments.
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-07-09
 */
public class LogListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
	private LogListFrag logListFrag;
	private SearchView searchView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log);

		ActionBar actionBar = getSupportActionBar();
		if(actionBar != null)
			actionBar.setDisplayHomeAsUpEnabled(true);

		logListFrag = (LogListFrag) getSupportFragmentManager().findFragmentById(R.id.fragLogList);
	}

	@Override
	protected void onStart() {
		super.onStart();

		Tracker tracker = ((App) getApplication()).getTracker();
		tracker.setScreenName(this.getClass().getName());
		tracker.send(new HitBuilders.AppViewBuilder().build());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.log_list, menu);

        searchView = (SearchView) menu.findItem(R.id.mActionSearch).getActionView();
        searchView.setQueryHint("");
        searchView.setOnQueryTextListener(this);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Users wants to clear log list.
	 */
	public void onClear(MenuItem menu) {
		new LogBusiness().clear();
		logListFrag.refresh();
	}

    @Override
    public boolean onSearchRequested() {
    	if(searchView != null) {
    		searchView.setIconified(false);
    		searchView.requestFocus();
    	}
    	return super.onSearchRequested();
    }

	@Override
	public boolean onQueryTextSubmit(String query) {
		return logListFrag.onQueryTextSubmit(query);
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		return logListFrag.onQueryTextChange(newText);
	}
}
