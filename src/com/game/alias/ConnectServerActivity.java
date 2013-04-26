package com.game.alias;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import connection.Connector;
import connection.Packet;
import connection.PacketType;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

public class ConnectServerActivity extends Activity {

	private ProgressDialog progressDialog;
	private List<CheckBox> checks = new ArrayList<CheckBox>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connect_server);

		LinearLayout layout = (LinearLayout) findViewById(R.id.connect_server_layout);
		for (String key: MainActivity.DIFFICULTIES){
			CheckBox check = new CheckBox(this);
			check.setText(key);
			layout.addView(check);
			checks.add(check);
		}
		
		for (String keyword : MainActivity.KEYWORDS) {
			CheckBox check = new CheckBox(this);
			check.setText(keyword);
			layout.addView(check);
			checks.add(check);
		};
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//No menu
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		listen = false;
		try {
			Connector.INSTANCE.disconnect();
		} catch (IOException e) {

		}
		finish();
	}

	public boolean sendIp(View view) throws IOException {
		
		List<String> keywords = new ArrayList<String>();
		for (CheckBox check : checks) {
			if(check.isChecked()){
				keywords.add(check.getText().toString());
			}
		}
		
		MainActivity.selectedWords = MainActivity.getWordsForKeys(keywords);
		
		// show loading dialog
		EditText user_input = (EditText) findViewById(R.id.edit_message);
		String ip_address = user_input.getText().toString();
		new ConnectTask().execute(ip_address);

		progressDialog = ProgressDialog.show(ConnectServerActivity.this, "",
				"Loading...");

		listen = true;
		new ConnectionListenerTask().execute();

		return true;
	}

	private final Handler handler = new Handler();
	private boolean listen = true;

	private class ConnectTask extends AsyncTask {

		@Override
		protected Object doInBackground(Object... params) {
			String ip_address = (String) params[0];
			try {
				Connector.INSTANCE.connect(ip_address);
			} catch (IOException e) {

			}
			return null;
		}

	}

	private class ConnectionListenerTask extends AsyncTask {

		@Override
		protected Object doInBackground(Object... arg0) {
			while (listen)
				try {
					Connector.INSTANCE.awaitConnection();
					handler.post(updateRunnable);
					listen = false;
				} catch (IOException e) {

				}
			return null;
		}

	}

	private void update() {
		progressDialog.dismiss();
		// Do something for the ip
		RadioButton normalButton = (RadioButton) findViewById(R.id.radio0);
		RadioButton limitedtButton = (RadioButton) findViewById(R.id.radio1);
		String gametype = GameMasterActivity.NORMAL;
		if (limitedtButton.isChecked()) {
			gametype = GameMasterActivity.LIMITED;
		}

		// if everything goes ok, start game. Otherwise stay on page
		Intent intent = new Intent(this, GameMasterActivity.class);
		// who connects server, starts the game
		intent.putExtra(MainActivity.GAMETYPE, gametype);
		startActivity(intent);
	}

	final Runnable updateRunnable = new Runnable() {
		public void run() {
			update();
		}
	};
}
