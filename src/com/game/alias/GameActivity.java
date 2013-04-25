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

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class GameActivity extends Activity {
	// game types
	public static String NORMAL = "normal";
	public static String LIMITED = "Limited";

	private String opponent_ip;
	private String own_ip;

	private boolean turn;
	// whether you are the guessing the word or giving hints
	private boolean guesser;

	// the word to be guessed.
	private String alias_word;
	// gametype
	private String gametype;

	private List<String> guesses = new ArrayList<String>();

	private Game game;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		// init the game
		Intent intent = getIntent();

		this.turn = intent.getBooleanExtra(ServerStartedActivity.TURN, true);
		this.guesser = intent.getBooleanExtra(ServerStartedActivity.GUESSER,
				false);
		this.gametype = intent.getStringExtra(MainActivity.GAMETYPE);
		// getAliasWord();

		this.game = new Game();

		// TODO avainsanat käyttäjältä, lista mahdollisista:
		// MainActivity.KEYWORDS
		Set<String> words = MainActivity.getWordsForKeys(Arrays.asList("easy"));
		int index = new Random(System.currentTimeMillis())
				.nextInt(words.size() - 1);

		this.alias_word = words.toArray(new String[0])[index];
		this.game.setWord(alias_word);

		View button2 = findViewById(R.id.start_new_game_button);
		button2.setVisibility(View.GONE);

		View waitingIndicator = findViewById(R.id.waiting_indicator);
		waitingIndicator.setVisibility(View.GONE);

		// if the player is not quesser he starts the game by giving first hint
		if (!this.guesser) {
			this.opponent_ip = intent.getStringExtra(MainActivity.SERVER);
			this.own_ip = intent.getStringExtra(MainActivity.CLIENT);

			String message = "You sent game request to server "
					+ this.opponent_ip + " The word is " + this.alias_word
					+ ". Give the first hint.";

			TextView textfield = (TextView) findViewById(R.id.server_connected);
			textfield.setTextSize(20);
			textfield.setText(message);
			// hide button and the guess text field
			View b = findViewById(R.id.guess_button);
			b.setVisibility(View.GONE);
			View t = findViewById(R.id.guess);
			t.setVisibility(View.GONE);
		}

		// if the person is the guesser he must wait for the first hint
		else {
			
			this.alias_word = intent.getStringExtra(ServerStartedActivity.WORD);
			this.game.setWord(alias_word);
			
			this.opponent_ip = intent.getStringExtra(MainActivity.CLIENT);
			this.own_ip = intent.getStringExtra(MainActivity.SERVER);

			waitForHint("Waiting for hint.");
		}
	}

	

	private void waitForHint(String message) {
		TextView textfield = (TextView) findViewById(R.id.server_connected);
		textfield.setText(message);
		// hide button and the hint text field
		View b = findViewById(R.id.hint_button);
		b.setVisibility(View.GONE);
		View t = findViewById(R.id.hint);
		t.setVisibility(View.GONE);
		// hide button and the guess text field
		View b2 = findViewById(R.id.guess_button);
		b2.setVisibility(View.GONE);
		View t2 = findViewById(R.id.guess);
		t2.setVisibility(View.GONE);

		View waitingIndicator = findViewById(R.id.waiting_indicator);
		waitingIndicator.setVisibility(View.VISIBLE);

		Thread hintThread = new Thread(new HintReceiverThread());
		listenHint = true;
		hintThread.start();
	}

	private void waitForGuess(String message) {
		View waitingIndicator = findViewById(R.id.waiting_indicator);
		waitingIndicator.setVisibility(View.VISIBLE);

		TextView textfield = (TextView) findViewById(R.id.server_connected);
		textfield.setText(message);
		// hide button and the hint text field
		View b = findViewById(R.id.hint_button);
		b.setVisibility(View.GONE);
		View t = findViewById(R.id.hint);
		t.setVisibility(View.GONE);

		Thread guessThread = new Thread(new GuessReceiverThread());
		listenGuess = true;
		guessThread.start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		listenHint = false;
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
	 * Returns a random word to be used in Alias game
	 * 
	 * @return
	 */
	private String getAliasWord() {
		Random rand = new Random();
		int index = rand.nextInt(10);
		String[] words = { "dress", "fang", "field", "flag", "flower", "fog",
				"game", "heat", "hill", "home" };
		return words[index];
	}

	/**
	 * Called when the game is restarted. The roles are automatically switched.
	 */
	public void restartGame(View view) {

		View button2 = findViewById(R.id.start_new_game_button);
		button2.setVisibility(View.GONE);
		this.alias_word = getAliasWord();
		this.guesses.clear();

		// switch roles
		this.guesser = !this.guesser;

		// if the player is not guesser he starts the game by giving first hint
		if (!this.guesser) {

			this.turn = true;
			String message = "Your turn to give hints. " + "The word is "
					+ this.alias_word + ". Give the first hint.";

			TextView textfield = (TextView) findViewById(R.id.server_connected);
			textfield.setTextSize(20);
			textfield.setText(message);
			// hide button and the guess text field
			View b = findViewById(R.id.guess_button);
			b.setVisibility(View.GONE);
			View t = findViewById(R.id.guess);
			t.setVisibility(View.GONE);
			// hide button and the hint text field
			View b2 = findViewById(R.id.hint_button);
			b2.setVisibility(View.VISIBLE);
			View t2 = findViewById(R.id.hint);
			t2.setVisibility(View.VISIBLE);
		}

		// if the person is the guesser he must wait for the first hint
		else {
			this.turn = false;

			waitForHint("Your turn to guess. Wait for the first hint.");
		}
	}

	/**
	 * For the player who is trying to guess the word. Called from socket when
	 * user receives the hint.
	 */
	public void receiveHint() {

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

		this.game.sendGuess(guess);
		// change turn
		this.turn = false;
		if (guess.equals(this.alias_word)) {
			String message = "Correct !! You are master of the universe.";
			TextView textfield = (TextView) findViewById(R.id.server_connected);
			textfield.setText(message);
			// View button = findViewById(R.id.go_back_to_button);
			// button.setVisibility(View.VISIBLE);
			View button2 = findViewById(R.id.start_new_game_button);
			button2.setVisibility(View.VISIBLE);
			// TODO Send message to other player that the game has ended
		} else {
			waitForHint("Your guess wasn't correct. Please wait for next hint.");
		}

		// hide button and the hint text field
		View b = findViewById(R.id.guess_button);
		b.setVisibility(View.GONE);
		View t = findViewById(R.id.guess);
		t.setVisibility(View.GONE);

	}

	/**
	 * For the person who knows the word. Called from UI when user sends the
	 * hint.
	 * 
	 * @param view
	 */
	public void sendHint(View view) {
		// TODO heppu| send hint to opponent
		EditText user_input = (EditText) findViewById(R.id.hint);
		String hint = user_input.getText().toString();

		if (hint.length() > 10 && this.gametype.equals(GameActivity.LIMITED)) {
			String message = "Your hint is too long for limited mode. Give a new hint";
			TextView textfield = (TextView) findViewById(R.id.server_connected);
			textfield.setText(message);
		} else {

			// change turn and change text
			this.turn = false;
			this.game.sendHint(hint);
			waitForGuess("Your hint was sent. Now wait for the other player to guess the word.");

		}
	}

	/**
	 * For the person who knows the word. Called from socket when other playes
	 * has sent a guess
	 */
	public void receiveGuess() {
		// TODO heppu| how do we get here?
		String guess = this.game.receiveGuess();

	}

	/**
	 * This just for the dummy game version
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.dummy_reply:
			receiveGuess();
			return true;
		case R.id.dummy_hint:
			receiveHint();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private Handler handler = new Handler();

	private boolean listenGuess;
	private String receivedGuess;

	private class GuessReceiverThread implements Runnable {

		@Override
		public void run() {

			while (listenGuess)
				try {
					receivedGuess = Connector.INSTANCE.listen(PacketType.GUESS)
							.getMessage();
					handler.post(updateGuessRunnable);
					listenGuess = false;
				} catch (IOException e) {
					e.printStackTrace();
				}

		}
	}

	private boolean listenHint;
	private String receivedHint;

	private class HintReceiverThread implements Runnable {
		@Override
		public void run() {

			while (listenHint)
				try {
					receivedHint = Connector.INSTANCE.listen(PacketType.GUESS)
							.getMessage();
					handler.post(updateHintRunnable);
					listenHint = false;
				} catch (IOException e) {
					e.printStackTrace();
				}

		}
	}

	private void updateGuess() {
		View waitingIndicator = findViewById(R.id.waiting_indicator);
		waitingIndicator.setVisibility(View.GONE);

		this.turn = true;
		String message = "The guess was: " + receivedGuess
				+ " It was wrong. Give a new hint";
		TextView textfield = (TextView) findViewById(R.id.server_connected);
		textfield.setText(message);
		View b = findViewById(R.id.hint_button);
		b.setVisibility(View.VISIBLE);
		View t = findViewById(R.id.hint);
		t.setVisibility(View.VISIBLE);
	}

	private void updateHint() {
		View waitingIndicator = findViewById(R.id.waiting_indicator);
		waitingIndicator.setVisibility(View.GONE);

		this.turn = true;
		String message = "The hint is: " + receivedHint;
		TextView textfield = (TextView) findViewById(R.id.server_connected);
		textfield.setText(message);
		View b = findViewById(R.id.guess_button);
		b.setVisibility(View.VISIBLE);
		View t = findViewById(R.id.guess);
		t.setVisibility(View.VISIBLE);
	}

	final Runnable updateGuessRunnable = new Runnable() {
		public void run() {
			updateGuess();
		}
	};

	final Runnable updateHintRunnable = new Runnable() {
		public void run() {
			updateHint();
		}
	};

}
