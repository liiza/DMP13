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

		this.turn = intent.getBooleanExtra(MainActivity.TURN, true);
		this.guesser = intent.getBooleanExtra(MainActivity.GUESSER, false);
		this.gametype = intent.getStringExtra(MainActivity.GAMETYPE);
		 // getAliasWord();
		
		this.game = new Game();
		Set<String> words = MainActivity.getWordsForKeys(Arrays.asList("hard"));
		this.alias_word = this.game.getWord();

		View button2 = findViewById(R.id.start_new_game_button);
		button2.setVisibility(View.GONE);

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
			
			this.game.sendStart(opponent_ip);
		}

		// if the person is the guesser he must wait for the first hint
		else {
			this.opponent_ip = intent.getStringExtra(MainActivity.CLIENT);
			this.own_ip = intent.getStringExtra(MainActivity.SERVER);
			
			String message = "You started a server with ip: " + this.own_ip
					+ " Share your ip with someone so he can join your server.";
			
			TextView textfield = (TextView) findViewById(R.id.server_connected);
			textfield.setTextSize(20);
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
			
			this.game.receiveStart();
			receiveHint();
		}
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
			String message = "Your turn to guess. Wait for the first hint.";

			TextView textfield = (TextView) findViewById(R.id.server_connected);
			textfield.setTextSize(20);
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
		}
	}

	/**
	 * For the player who is trying to guess the word. Called from socket when
	 * user receives the hint.
	 */
	public void receiveHint() {
		// TODO heppu| how do we get here?
		this.turn = true;
		String message = "The hint is: " + this.game.receiveHint();
		TextView textfield = (TextView) findViewById(R.id.server_connected);
		textfield.setText(message);
		View b = findViewById(R.id.guess_button);
		b.setVisibility(View.VISIBLE);
		View t = findViewById(R.id.guess);
		t.setVisibility(View.VISIBLE);
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
			String message = "Your guess wasn't correct. Please wait for next hint.";
			TextView textfield = (TextView) findViewById(R.id.server_connected);
			textfield.setText(message);
			receiveHint();
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
			String message = "Your hint was sent. Now wait for the other player to guess the word.";
			TextView textfield = (TextView) findViewById(R.id.server_connected);
			textfield.setText(message);
			// hide button and the hint text field
			View b = findViewById(R.id.hint_button);
			b.setVisibility(View.GONE);
			View t = findViewById(R.id.hint);
			t.setVisibility(View.GONE);
			this.game.sendHint(hint);
			receiveGuess();
		}
	}

	/**
	 * For the person who knows the word. Called from socket when other playes
	 * has sent a guess
	 */
	public void receiveGuess() {
		// TODO heppu| how do we get here?
		String guess = this.game.receiveGuess();
		this.turn = true;
		String message = "The guess was: " + guess + " It was wrong. Give a new hint";
		TextView textfield = (TextView) findViewById(R.id.server_connected);
		textfield.setText(message);
		View b = findViewById(R.id.hint_button);
		b.setVisibility(View.VISIBLE);
		View t = findViewById(R.id.hint);
		t.setVisibility(View.VISIBLE);
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

}
