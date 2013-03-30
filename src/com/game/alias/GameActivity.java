package com.game.alias;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TextView;

public class GameActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		Intent intent = getIntent();
		//TODO if we start a game from Start Server Activity? How can we now that?
		String message = intent.getStringExtra(ConnectServerActivity.EXTRA_MESSAGE);
		String startmsg= "You sent game request to server ";
		String endmsg = ". Wait for the other player to accept your request and pass you the first hint";
		message = startmsg.concat(message).concat(endmsg);
		
		// Create the text view
	    TextView textView = new TextView(this);
	    textView.setTextSize(40);
	    textView.setText(message);

	    // Set the text view as the activity layout
	    setContentView(textView);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_game, menu);
		return true;
	}

}
