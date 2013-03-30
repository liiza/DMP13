package com.game.alias;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ConnectServerActivity extends Activity {
	public final static String EXTRA_MESSAGE = "com.game.alias.MESSAGE";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connect_server);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_connect_server, menu);
		return true;
	}

	public boolean sendIp(View view){
		//Do something for the ip
		//TODO heppu connect to server user gives
		EditText user_input = (EditText) findViewById(R.id.edit_message);
		String ip_address = user_input.getText().toString();
		
		//if everything goes ok, start game. Otherwise stay on page
		Intent intent = new Intent(this, GameActivity.class);
		intent.putExtra(EXTRA_MESSAGE, ip_address);
		startActivity(intent);
		return true;
	}
}
