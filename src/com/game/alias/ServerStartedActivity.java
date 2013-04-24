package com.game.alias;

import java.io.IOException;

import connection.Connector;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class ServerStartedActivity extends Activity {

	private String ip;
	private boolean listen = true;

	public final static String SENT_IP = "SENT_IP";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_server_started);

		TextView messageField = (TextView) findViewById(R.id.server_started_message);
		TextView ipField = (TextView) findViewById(R.id.show_ip_text);

		View startGameButton = findViewById(R.id.start_game_button);
		startGameButton.setVisibility(View.GONE);
		View disconnectButton = findViewById(R.id.disconnect_button);
		disconnectButton.setVisibility(View.GONE);
		
		try {
			this.ip = Connector.INSTANCE.getAddress();
			ipField.setText(this.ip);

			Thread t = new Thread(new ConnectionListenerThread());
			t.start();
		} catch (IOException e) {
			View sendIpButton = findViewById(R.id.send_ip_button);
			View listeningIndicator = findViewById(R.id.listening_indicator);
			listeningIndicator.setVisibility(View.GONE);
			sendIpButton.setVisibility(View.GONE);

			messageField.setText("Oops! Something went wrong...");
			ipField.setText("Failed to retrieve IP");
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		listen = false;
		disconnect(null);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		listen = false;
		disconnect(null);
	}
	
	public final static String TURN = "TURN";
	public final static String GUESSER = "GUESSER";

	public boolean startGame(View view) {
		Intent intent = new Intent(this, GameActivity.class);
		// who starts server, must wait the other person to start the game
		intent.putExtra(TURN, false);
		// who starts server is the guesser
		intent.putExtra(GUESSER, true);
		startActivity(intent);
		finish();
		return true;
	}

	public boolean disconnect(View view) {
		try {
			Connector.INSTANCE.disconnect();
		} catch (IOException e) {

		}
		finish();
		return true;
	}

	public boolean sendIp(View view) {
		Intent intent = new Intent(this, SendIPActivity.class);
		intent.putExtra(SENT_IP, ip);
		startActivity(intent);
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.server_started, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void updateUI() {
		TextView messageField = (TextView) findViewById(R.id.server_started_message);
		messageField.setText("Connected!");
		View startGameButton = findViewById(R.id.start_game_button);
		startGameButton.setVisibility(View.VISIBLE);
		View listeningIndicator = (ProgressBar) findViewById(R.id.listening_indicator);
		listeningIndicator.setVisibility(View.GONE);
		View disconnectButton = findViewById(R.id.disconnect_button);
		disconnectButton.setVisibility(View.VISIBLE);
	}

	private final Handler handler = new Handler();

	private class ConnectionListenerThread implements Runnable {

		@Override
		public void run() {
			while (listen)
				try {
					Connector.INSTANCE.awaitConnection();
					handler.post(updateRunnable);
					listen = false;
				} catch (IOException e) {

				}
		}

	}

	final Runnable updateRunnable = new Runnable() {
		public void run() {
			updateUI();
		}
	};

}
