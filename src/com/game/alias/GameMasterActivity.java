package com.game.alias;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class GameMasterActivity extends Activity {
	// game types
	public static String NORMAL = "normal";
	public static String LIMITED = "Limited";

	private String opponent_ip;
	// the word to be guessed.
	private String word;
	// gametype
	private String gametype;

	private List<String> guesses = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_master);

		// init the game
		Intent intent = getIntent();
		this.gametype = intent.getStringExtra(MainActivity.GAMETYPE);

		// TODO avainsanat käyttäjältä, lista mahdollisista:
		// MainActivity.KEYWORDS
		this.word = randomizeWord(Arrays.asList("easy"));

		TextView textfield = (TextView) findViewById(R.id.upper_message);
		TextView wordfield = (TextView) findViewById(R.id.word_message);
		TextView hintfield = (TextView) findViewById(R.id.lower_message);

		View button2 = findViewById(R.id.start_new_game_button);
		button2.setVisibility(View.GONE);

		View waitingIndicator = findViewById(R.id.waiting_indicator);
		waitingIndicator.setVisibility(View.GONE);
		if (this.word == null) {
			textfield.setText("Oops! Something went wrong...");
			wordfield.setText("No matching words available.");
			hintfield.setVisibility(View.GONE);
			View b = findViewById(R.id.hint_button);
			b.setVisibility(View.GONE);
			View t = findViewById(R.id.hint);
			t.setVisibility(View.GONE);
		} else {

			textfield.setText("Your word is: ");
			wordfield.setText(this.word);
			hintfield.setText("Send a hint.");

			new StartSenderTask().execute(this.word);
		}
	}

	private String randomizeWord(List<String> keywords) {
		List<String> words = MainActivity.selectedWords;
		if(words.size() == 1){
			return words.toArray(new String[0])[0];
		}
		if (words.size() > 0) {
			int index = new Random(System.currentTimeMillis()).nextInt(words
					.size() - 1);
			return words.toArray(new String[0])[index];
		} else
			return null;
	}

	private void waitForGuess(String message) {
		View waitingIndicator = findViewById(R.id.waiting_indicator);
		waitingIndicator.setVisibility(View.VISIBLE);

		TextView textfield = (TextView) findViewById(R.id.upper_message);
		textfield.setText(message);
		// hide button and the hint text field
		View b = findViewById(R.id.hint_button);
		b.setVisibility(View.GONE);
		View t = findViewById(R.id.hint);
		t.setVisibility(View.GONE);

		TextView lower = (TextView) findViewById(R.id.lower_message);
		lower.setVisibility(View.GONE);

		TextView word = (TextView) findViewById(R.id.word_message);
		word.setVisibility(View.GONE);
		listenGuess = true;
		new GuessReceiverTask().execute();
	}

	@Override
	protected void onPause() {
		super.onPause();
		listenGuess = false;
		try {
			Connector.INSTANCE.disconnect();
		} catch (IOException e) {

		}
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_game, menu);
		return true;
	}

	/**
	 * Called when the game is restarted. The roles are automatically switched.
	 */
	public void restartGame(View view) {
		Intent intent = new Intent(this, GameMasterActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * For the person who knows the word. Called from UI when user sends the
	 * hint.
	 * 
	 * @param view
	 */
	public void sendHint(View view) {
		EditText user_input = (EditText) findViewById(R.id.hint);
		String hint = user_input.getText().toString();

		if (hint.length() > 10
				&& this.gametype.equals(GameMasterActivity.LIMITED)) {
			Toast.makeText(getBaseContext(), "The hint is too long!",
					Toast.LENGTH_SHORT).show();
		} else {
			new SendHintTask().execute(hint);
			waitForGuess("Wait for guess.");
		}
	}

	private class SendHintTask extends AsyncTask {

		@Override
		protected Object doInBackground(Object... params) {
			String hint = (String) params[0];
			try {
				Connector.INSTANCE.send(new Packet(PacketType.HINT, hint));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

	}

	private Handler handler = new Handler();

	private boolean listenGuess;
	private String receivedGuess;

	private class GuessReceiverTask extends AsyncTask {

		@Override
		protected Object doInBackground(Object... params) {
			while (listenGuess)
				try {
					receivedGuess = Connector.INSTANCE.listen(PacketType.GUESS)
							.getMessage();
					handler.post(updateGuessRunnable);
					listenGuess = false;
				} catch (IOException e) {
					e.printStackTrace();
				}
			return null;
		}

	}

	private class StartSenderTask extends AsyncTask {

		@Override
		protected Object doInBackground(Object... params) {
			String word = (String) params[0];
			try {
				Connector.INSTANCE.send(new Packet(PacketType.START, word));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

	}

	private void updateGuess() {
		View waitingIndicator = findViewById(R.id.waiting_indicator);
		waitingIndicator.setVisibility(View.GONE);

		TextView textfield = (TextView) findViewById(R.id.upper_message);
		TextView wordfield = (TextView) findViewById(R.id.word_message);
		TextView hintfield = (TextView) findViewById(R.id.lower_message);

		textfield.setVisibility(View.VISIBLE);
		wordfield.setVisibility(View.VISIBLE);
		hintfield.setVisibility(View.VISIBLE);

		textfield.setText("The guess was: " + receivedGuess);

		if (receivedGuess.equals(this.word)) {

			hintfield.setText("Correct!");

			// button.setVisibility(View.VISIBLE);
			View button2 = findViewById(R.id.start_new_game_button);
			button2.setVisibility(View.VISIBLE);
			// TODO Send message to other player that the game has ended
		} else {

			hintfield.setText("Wrong! Send a new hint.");

			View b = findViewById(R.id.hint_button);
			b.setVisibility(View.VISIBLE);
			View t = findViewById(R.id.hint);
			t.setVisibility(View.VISIBLE);

		}
	}

	final Runnable updateGuessRunnable = new Runnable() {
		public void run() {
			updateGuess();
		}
	};

}
