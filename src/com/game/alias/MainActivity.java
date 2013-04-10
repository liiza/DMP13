package com.game.alias;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ListActivity {
	public final static String SERVER = "SERVER";
	public final static String ALIAS_WORD = "ALIAS_WORD";
	public final static String CLIENT = "OWN_IP";
	public final static String TURN = "TURN";
	public final static String GUESSER = "GUESSER";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		String[] elements = { "Connect Server", "Start Server", "Quit" };

		// Create an ArrayAdapter
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, elements);
		this.setListAdapter(arrayAdapter);
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Object o = this.getListAdapter().getItem(position);
		String element = o.toString();
		if (element == "Connect Server") {
			connectServer();
		}
		else if (element == "Start Server") {
			startServer();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private boolean connectServer() {
		Intent intent = new Intent(this, ConnectServerActivity.class);
		startActivity(intent);
		return true;

	}

	public static String getOwnIp() {
		// TODO heppu| how to get own ip
		return "dummyownip";
	}

	private boolean startServer() {
		Intent intent = new Intent(this, GameActivity.class);
		String own_ip_address = getOwnIp();
		intent.putExtra(SERVER, own_ip_address);
		// who starts server, must wait the other person to start the game
		intent.putExtra(TURN, false);
		// who starts server is the guesser
		intent.putExtra(GUESSER, true);
		startActivity(intent);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.connect_server:
			connectServer();
			return true;
		case R.id.start_server:
			startServer();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
