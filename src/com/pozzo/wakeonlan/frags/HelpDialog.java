package com.pozzo.wakeonlan.frags;

import com.pozzo.wakeonlan.R;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Will help user with some extra informations.
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-05-04
 */
public class HelpDialog extends DialogFragment {

	public View onCreateView(
			LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().setTitle(R.string.help);

		View contentView = inflater.inflate(R.layout.help_frag, container, false);
		Button bClose = (Button) contentView.findViewById(R.id.bClose);
		bClose.setOnClickListener(onClose);
		return contentView;
	}

	private OnClickListener onClose = new OnClickListener() {
		@Override
		public void onClick(View v) {
			dismiss();
		}
	};

	/**
	 * Following the rules for future needs.
	 */
	public static HelpDialog newInstance() {
		return new HelpDialog();
	}
}
