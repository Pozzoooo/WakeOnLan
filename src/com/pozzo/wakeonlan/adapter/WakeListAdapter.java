package com.pozzo.wakeonlan.adapter;

import com.pozzo.wakeonlan.R;
import com.pozzo.wakeonlan.database.WakeEntryCr;
import com.pozzo.wakeonlan.vo.WakeEntry;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * How we will show our entry list.
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-05-03
 */
public class WakeListAdapter extends CursorAdapter {
	private final String PORT_SEPARATOR = ":";
	private LayoutInflater inflater;

	public WakeListAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View line = inflater.inflate(R.layout.wake_entry, parent, false);
		return line;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		WakeEntry entry = WakeEntryCr.objectFrom(cursor);

		TextView lAddr = (TextView) view.findViewById(R.id.lAddr);
		TextView lMacAddr = (TextView) view.findViewById(R.id.lMacAddr);
		TextView lName = (TextView) view.findViewById(R.id.lName);

		lAddr.setText(entry.getIp() + PORT_SEPARATOR + entry.getPort());
		lMacAddr.setText(entry.getMacAddress());
		lName.setText(entry.getName());
	}

	@Override
	public WakeEntry getItem(int position) {
		Cursor cursor = (Cursor) super.getItem(position);
		if(cursor == null)
			return null;

		return WakeEntryCr.objectFrom(cursor);
	}
}
