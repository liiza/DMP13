package com.game.alias;

import java.io.IOException;
import java.net.SocketException;

import connection.Connector;
import connection.Packet;
import connection.PacketType;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class ConnectServerActivity extends Activity {

	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connect_server);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.activity_connect_server, menu);
		return true;
	}

	public boolean sendIp(View view) throws IOException {
		// show loading dialog
		EditText user_input = (EditText) findViewById(R.id.edit_message);
		String ip_address = user_input.getText().toString();
		Connector.INSTANCE.connect(ip_address);
		
		progressDialog = ProgressDialog.show(ConnectServerActivity.this, "",
				"Loading...");
		
		listen = true;
		Thread t = new Thread(new ConnectionListenerThread());
		t.start();
		
		return true;
	}

	private final Handler handler = new Handler();
	private boolean listen = true;

	private class ConnectionListenerThread implements Runnable {

		@Override
		public void run() {
			while (listen)
				try {
					Connector.INSTANCE.listen(PacketType.CONNECT).getMessage();
					handler.post(updateRunnable);
					listen = false;
				} catch (IOException e) {

				}
		}

	}

	private void update() {
		progressDialog.dismiss();
		// Do something for the ip
		String alias_word = "kakkulapio";
		String own_ip = MainActivity.getOwnIp();
		RadioButton normalButton = (RadioButton) findViewById(R.id.radio0);
		RadioButton limitedtButton = (RadioButton) findViewById(R.id.radio1);
		String gametype = GameActivity.NORMAL;
		if (limitedtButton.isChecked()) {
			gametype = GameActivity.LIMITED;
		}

		// if everything goes ok, start game. Otherwise stay on page
		Intent intent = new Intent(this, GameActivity.class);
		// who connects server, starts the game
		intent.putExtra(MainActivity.TURN, true);
		// who connects server is the person who knows the word
		intent.putExtra(MainActivity.GUESSER, false);
		// game type
		intent.putExtra(MainActivity.GAMETYPE, gametype);
		startActivity(intent);
	}

	final Runnable updateRunnable = new Runnable() {
		public void run() {
			update();
		}
	};
}
