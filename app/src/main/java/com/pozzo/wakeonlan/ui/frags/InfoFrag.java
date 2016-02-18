package com.pozzo.wakeonlan.ui.frags;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pozzo.wakeonlan.R;
import com.pozzo.wakeonlan.helper.NetworkUtils;

import java.net.InetAddress;
import java.net.SocketException;

/**
 * This is just an informative screen.
 * I like to compare it to something like ifconfig/ ipconfig.
 * 
 * @author Luiz Gustavo Pozzo
 * @since 2014-05-03
 */
public class InfoFrag extends Fragment {
	private TextView lIp;
	private TextView lBroadcast;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View contentView = 
				inflater.inflate(R.layout.fragment_info, container, false);

		lIp = (TextView) contentView.findViewById(R.id.lIp);
		lBroadcast = (TextView) contentView.findViewById(R.id.lBroadcast);

		loadAddress();

		return contentView;
	}

/*
 * TODO
 * 	Criar metodo para poder ir para backgound
 * 	Criar metodo de criacao de fragment
 */

	private void loadAddress() {
		NetworkUtils ipUtils = new NetworkUtils();

		try {
			InetAddress ip = ipUtils.getIPAddress();
			lIp.setText(ip.getHostAddress());
			lBroadcast.setText(ipUtils.getBroadcast(ip).getHostAddress());
		} catch(SocketException e) {
			e.printStackTrace();
			//TODO handle it
		}
	}
}
