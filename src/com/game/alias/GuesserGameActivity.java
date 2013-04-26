package com.game.alias;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import connection.Connector;
import connection.Packet;
import connection.PacketType;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class GuesserGameActivity extends Activity {

	private String word;
	private Set<String> guesses = new HashSet<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guesser_game);

		TextView upper = (TextView) findViewById(R.id.upper_guess_msg);
		TextView hint = (TextView) findViewById(R.id.hint_message);
		TextView lower = (TextView) findViewById(R.id.lower_guess_msg);
		
		upper.setText("Waiting for start.");
		hint.setVisibility(View.GONE);
		lower.setVisibility(View.GONE);

		// hide button and the guess text field
		View b2 = findViewById(R.id.guess_button);
		b2.setVisibility(View.GONE);
		View t2 = findViewById(R.id.guess);
		t2.setVisibility(View.GONE);

		View button2 = findViewById(R.id.start_new_game_button);
		button2.setVisibility(View.GONE);

		listenStart = true;
		new StartReceiverTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.guesser_game, menu);
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		listenHint = false;
		try {
			Connector.INSTANCE.disconnect();
		} catch (IOException e) {

		}
		finish();
	}

	/**
	 * For the player who is trying to guess the word Called from UI when user
	 * sends the guess.
	 * 
	 * @param view
	 */
	public void sendGuess(View view) {
		// TODO heppu| send guess to opponent

		EditText user_input = (EditText) findViewById(R.id.guess);
		String guess = user_input.getText().toString();
		this.guesses.add(guess);
		
		TextView lower = (TextView) findViewById(R.id.lower_guess_msg);
		TextView hint = (TextView) findViewById(R.id.hint_message);
		hint.setVisibility(View.GONE);
		lower.setVisibility(View.GONE);

		new SendGuessTask().execute(guess);
		// change turn
		if (guess.equals(this.word)) {
			String message = "Correct !! You are master of the universe.";
			TextView textfield = (TextView) findViewById(R.id.upper_guess_msg);
			textfield.setText(message);
			// View button = findViewById(R.id.go_back_to_button);
			// button.setVisibility(View.VISIBLE);
			View button2 = findViewById(R.id.start_new_game_button);
			button2.setVisibility(View.VISIBLE);
			// TODO Send message to other player that the game has ended
		} else {
			waitForHint("Wrong! Wait for hint.");
		}

		// hide button and the hint text field
		View b = findViewById(R.id.guess_button);
		b.setVisibility(View.GONE);
		View t = findViewById(R.id.guess);
		t.setVisibility(View.GONE);

	}

	private void waitForHint(String message) {
		TextView textfield = (TextView) findViewById(R.id.upper_guess_msg);
		textfield.setText(message);
		
		// hide button and the guess text field
		View b2 = findViewById(R.id.guess_button);
		b2.setVisibility(View.GONE);
		View t2 = findViewById(R.id.guess);
		t2.setVisibility(View.GONE);

		View waitingIndicator = findViewById(R.id.waiting_indicator);
		waitingIndicator.setVisibility(View.VISIBLE);

		listenHint = true;
		new HintReceiverTask().execute();
	}

	private Handler handler = new Handler();

	private boolean listenHint;
	private String receivedHint;

	private class HintReceiverTask extends AsyncTask {

		@Override
		protected Object doInBackground(Object... params) {
			while (listenHint)
				try {
					receivedHint = Connector.INSTANCE.listen(PacketType.GUESS)
							.getMessage();
					handler.post(updateHintRunnable);
					listenHint = false;
				} catch (IOException e) {
					e.printStackTrace();
				}
			return null;
		}
	}

	final Runnable updateHintRunnable = new Runnable() {
		public void run() {
			updateHint();
		}
	};

	private void updateHint() {
		View waitingIndicator = findViewById(R.id.waiting_indicator);
		waitingIndicator.setVisibility(View.GONE);

		TextView textfield = (TextView) findViewById(R.id.upper_guess_msg);
		textfield.setText("The hint is: ");
		
		TextView lower = (TextView) findViewById(R.id.lower_guess_msg);
		TextView hint = (TextView) findViewById(R.id.hint_message);
		hint.setVisibility(View.VISIBLE);
		lower.setVisibility(View.VISIBLE);
		hint.setText(receivedHint);
		lower.setText("Send guess");
		
		View b = findViewById(R.id.guess_button);
		b.setVisibility(View.VISIBLE);
		View t = findViewById(R.id.guess);
		t.setVisibility(View.VISIBLE);
	}

	private class SendGuessTask extends AsyncTask {

		@Override
		protected Object doInBackground(Object... params) {
			String guess = (String) params[0];
			try {
				Connector.INSTANCE.send(new Packet(PacketType.GUESS, guess));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

	}

	private boolean listenStart;

	private class StartReceiverTask extends AsyncTask {

		@Override
		protected Object doInBackground(Object... params) {
			while (listenStart)
				try {
					word = Connector.INSTANCE.listen(PacketType.START)
							.getMessage();
					handler.post(waitingForHintRunnable);
					listenStart = false;
				} catch (IOException e) {
					e.printStackTrace();
				}
			return null;
		}
	}

	final Runnable waitingForHintRunnable = new Runnable() {
		public void run() {
			waitForHint("Waiting for hint.");
		}
	};

	public void restartGame(View view) {
		Intent intent = new Intent(this, GameMasterActivity.class);

		startActivity(intent);
		finish();
	}

}
