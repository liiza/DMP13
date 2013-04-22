package com.game.alias;

import java.io.IOException;
import java.net.SocketException;

import connection.Connector;
import connection.Packet;
import connection.PacketType;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class ConnectServerActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connect_server);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_connect_server, menu);
		return true;
	}

	public boolean sendIp(View view){
		//Do something for the ip
		EditText user_input = (EditText) findViewById(R.id.edit_message);	
		String ip_address = user_input.getText().toString();
		String alias_word = "kakkulapio";
		String own_ip = MainActivity.getOwnIp();
	    RadioButton normalButton = (RadioButton) findViewById(R.id.radio0);
	    RadioButton limitedtButton = (RadioButton) findViewById(R.id.radio1);
	    String gametype = GameActivity.NORMAL;
	    if (limitedtButton.isChecked()){
	    	gametype = GameActivity.LIMITED;
	    }
		
		
		
		
		//if everything goes ok, start game. Otherwise stay on page
		Intent intent = new Intent(this, GameActivity.class);
		intent.putExtra(MainActivity.SERVER, ip_address);
		intent.putExtra(MainActivity.CLIENT, own_ip);
		//who connects server, starts the game
		intent.putExtra(MainActivity.TURN, true);
		//who connects server is the person who knows the word
		intent.putExtra(MainActivity.GUESSER, false);
		//game type
		intent.putExtra(MainActivity.GAMETYPE, gametype);
		startActivity(intent);	
		return true;
	}
}
