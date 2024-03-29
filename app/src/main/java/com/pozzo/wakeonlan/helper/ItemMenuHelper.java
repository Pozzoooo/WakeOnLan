package com.pozzo.wakeonlan.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pozzo.wakeonlan.R;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Build some specific ItemMenu programatically.
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-05-03
 */
public class ItemMenuHelper {

	/**
	 * Two option, done or discard, nothing more.
	 * 
	 * @param actionBar which will be used to show this options.
	 * @param ctx Context.
	 */
	public static void setDoneDiscard(ActionBar actionBar, Context ctx) {
        actionBar.setDisplayOptions(
				ActionBar.DISPLAY_SHOW_CUSTOM,
				ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
						| ActionBar.DISPLAY_SHOW_TITLE);
		View view = ((LayoutInflater) ctx
					.getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE))
                		.inflate(R.layout.menu_form, null);
		actionBar.setCustomView(view, new ActionBar.LayoutParams(
		        ViewGroup.LayoutParams.MATCH_PARENT,
		        ViewGroup.LayoutParams.MATCH_PARENT));
	}
}
