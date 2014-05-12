package com.pozzo.wakeonlan.frags;

import java.io.IOException;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pozzo.wakeonlan.R;
import com.pozzo.wakeonlan.activity.AddWakeEntryActivity;
import com.pozzo.wakeonlan.adapters.WakeListAdapter;
import com.pozzo.wakeonlan.business.WakeBusiness;
import com.pozzo.wakeonlan.database.ConexaoDBManager;
import com.pozzo.wakeonlan.database.WakeEntryCr;
import com.pozzo.wakeonlan.helper.WakeOnLan;
import com.pozzo.wakeonlan.vo.WakeEntry;

/**
 * Shows and manage Entry lists.
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-05-03
 * @see WakeListAdapter
 * @see WakeEntry
 */
public class EntriesListFrag extends Fragment {
	private ListView listEntries;
	private WakeListAdapter adapter;
	private ConexaoDBManager conexao;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View contentView = inflater.inflate(R.layout.saved_entries_frag, container, false);

		listEntries = (ListView) contentView.findViewById(R.id.listEntries);

		listEntries.setOnItemClickListener(onItemClick);
		listEntries.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listEntries.setMultiChoiceModeListener(multiChoice);

		return contentView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		conexao = new ConexaoDBManager(activity);
		refresh();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//We maintain the connection during our Activity lifecycle
		conexao.close();
	}

	/**
	 * Make it reafreashable to Activiy.
	 */
	public void refresh() {
		new AsyncTask<Void, Void, Cursor>() {
			@Override
			protected Cursor doInBackground(Void... params) {
				SQLiteDatabase db = conexao.getDb();
				return db.query(WakeEntryCr.TB_NAME, null, null, null, null, null, null);
			}

			@Override
			protected void onPostExecute(Cursor cursor) {
				adapter = new WakeListAdapter(getActivity(), cursor, 0);
				listEntries.setAdapter(adapter);
			}
		}.execute();
	}

	private MultiChoiceModeListener multiChoice = new MultiChoiceModeListener() {
		
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}
		
		@Override
		public void onDestroyActionMode(ActionMode mode) {
		}
		
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	        MenuInflater inflater = mode.getMenuInflater();
	        inflater.inflate(R.menu.single_selection, menu);
	        return true;
		}
		
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.mDelete:
				deleteCheckedItems(listEntries.getCheckedItemIds());
				break;

			case R.id.mEdit:
				//Not supposed to happen when more than one item selected, 
				// but if so, we just pick the first and go on.
				long[] ids = listEntries.getCheckedItemIds();
				edit(ids[0]);
				break;

			default:
				return false;
			}
			mode.finish();
			return true;
		}
		
		@Override
		public void onItemCheckedStateChanged(ActionMode mode, int position,
				long id, boolean checked) {
			//In all new checks we check what to show
			int count = listEntries.getCheckedItemCount();
			MenuItem mEdit = mode.getMenu().findItem(R.id.mEdit);
			mEdit.setVisible(count == 1);
		}
	};

	/**
	 * Remove all checked items on ListView.
	 */
	private void deleteCheckedItems(long... ids) {
		new WakeBusiness().delete(ids);
		refresh();
	}

	/**
	 * Edit a single item.
	 * 
	 * @param itemId to be edited.
	 */
	private void edit(long itemId) {
		WakeBusiness bus = new WakeBusiness();
		WakeEntry entry = bus.get(itemId);
		Intent intent = new Intent(getActivity(), AddWakeEntryActivity.class);
		intent.putExtra(AddWakeEntryActivity.PARAM_WAKE_ENTRY, entry);
		startActivity(intent);
	}

	/**
	 * Interaction with list.
	 */
	private OnItemClickListener onItemClick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			WakeEntry entry = adapter.getItem(position);
			wake(entry);
		}
	};

	/**
	 * Ok, User wants to wake something UP, lets do it!
	 */
	private void wake(final WakeEntry entry) {
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				try {
					new WakeOnLan().wakeUp(entry.getIp(), entry.getMacAddress(), entry.getPort());
				} catch (IOException e) {
					return e.getMessage();
				}
				return null;
			}

			protected void onPostExecute(String result) {
				if(result == null)
					Toast.makeText(getActivity(), R.string.wakeSent, Toast.LENGTH_LONG).show();
				else
					Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
				//TODO fazer verificacao do que da errado, sem internet?
			}
		}.execute();
	}

	/**
	 * Animated tutorial to show function to users.
	 * I preferred to base on times instead of triggers, just to let it cleaner.
	 * This one is not really long, so I let it here inside the Frag, but I suggest to separate, 
	 * 	you can use a <include> on layout and a isolated FrameLayout and even use a state machine
	 * 	and animate with touches.
	 * 
	 * @param hasEntry If it has any entry on main list.
	 */
	public void animTutorial(boolean hasEntry) {
		//Times
		final int animShortTime = 
				Integer.parseInt(getString(R.string.animShort));
		final int readTime = 
				Integer.parseInt(getString(R.string.readTime));
		final float buttonMinSize =
				getResources().getDimension(R.dimen.minButtonSize);
		final Handler uiHandler = new Handler();

		//Load Componnets
		final View contentView = getView();
		final View vgTutorial = contentView.findViewById(R.id.vgTutorial);
		final ImageView iArrow = (ImageView) 
				contentView.findViewById(R.id.iFirstArrow);
		final TextView lWelcome = (TextView) contentView.findViewById(R.id.lWelcome);
		final TextView lTutoMessage = (TextView) 
				contentView.findViewById(R.id.lTutoMessage);

		//Animations - I use animations to beatification
		final Animation animArrowIn = AnimationUtils
				.loadAnimation(getActivity(), R.anim.arrow_in);
		final Animation animAppear = AnimationUtils
				.loadAnimation(getActivity(), R.anim.appear);
		final Animation animDisappear = AnimationUtils
				.loadAnimation(getActivity(), R.anim.disappear);

		//I set to invisible to use theirs default locations, without changes.
		vgTutorial.setVisibility(View.VISIBLE);
		lWelcome.setVisibility(View.INVISIBLE);
		iArrow.setVisibility(View.INVISIBLE);
		lTutoMessage.setVisibility(View.INVISIBLE);

		lWelcome.startAnimation(animAppear);

		Runnable newEntry = new Runnable() {
			public void run() {
				lWelcome.startAnimation(animDisappear);
				lTutoMessage.setText(R.string.tutoCreate);
				lTutoMessage.setAnimation(animAppear);
				iArrow.startAnimation(animArrowIn);
			}
		};

		Runnable cleanLastMessage = new Runnable() {
			public void run() {
				iArrow.setVisibility(View.INVISIBLE);
				lTutoMessage.setAnimation(animDisappear);
			}
		};

		Runnable wakeMessage = new Runnable() {
			public void run() {
				lTutoMessage.setText(R.string.tutoWake);
				lTutoMessage.setAnimation(animAppear);
				RelativeLayout.LayoutParams params = 
						(RelativeLayout.LayoutParams) iArrow.getLayoutParams();
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
				params.addRule(RelativeLayout.CENTER_HORIZONTAL);
				iArrow.requestLayout();
				iArrow.startAnimation(animArrowIn);
			}
		};

		Runnable editWake = new Runnable() {
			public void run() {
				lTutoMessage.setText(R.string.tutoEditWake);
				lTutoMessage.setAnimation(animAppear);
			}
		};

		Runnable help = new Runnable() {
			public void run() {
				lTutoMessage.setText(R.string.tutoHelp);
				lTutoMessage.setAnimation(animAppear);
				RelativeLayout.LayoutParams params = 
						(RelativeLayout.LayoutParams) iArrow.getLayoutParams();
				params.addRule(RelativeLayout.CENTER_HORIZONTAL, 0);
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				params.setMargins(0, 0, (int) (buttonMinSize + buttonMinSize/5 ), 0);
				iArrow.requestLayout();
				iArrow.startAnimation(animArrowIn);
			}
		};

		Runnable hideTutorial = new Runnable() {
			public void run() {
				vgTutorial.setVisibility(View.GONE);
			}
		};

		int playTime = animShortTime * 3;
		uiHandler.postDelayed(newEntry, playTime);
		uiHandler.postDelayed(cleanLastMessage, playTime += readTime);

		if(hasEntry) {
			uiHandler.postDelayed(wakeMessage, playTime += animShortTime);
			uiHandler.postDelayed(editWake, playTime += readTime);
			uiHandler.postDelayed(cleanLastMessage, playTime += readTime);
		}
		uiHandler.postDelayed(help, playTime += animShortTime);

		uiHandler.postDelayed(hideTutorial, playTime += readTime * 3);
	}
}
