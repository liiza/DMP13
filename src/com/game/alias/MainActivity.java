package com.game.alias;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	private boolean connectServer(){
		Intent intent = new Intent(this, ConnectServerActivity.class);
		startActivity(intent);
		return true;
	}
	
	private boolean startServer(){
		Intent intent = new Intent(this, StartServerActivity.class);
		startActivity(intent);
		return true;
	}
	 

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    	case R.id.start_server:
	    		startServer();
	    		return true;
	        case R.id.connect_server:
	        	connectServer();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}
