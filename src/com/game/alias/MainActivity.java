package com.game.alias;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import connection.Connector;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.view.LayoutInflater;
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
	public static String GAMETYPE = "GAMETYPE";

	private final static Keyword NO_KEY = new Keyword("NO_KEY", false);

	private static final Map<String, List<Keyword>> WORDS = new HashMap<String, List<Keyword>>();
	private static final Set<String> ABSOLUTES = new HashSet<String>();
	private static final Set<String> KEYWORDS = new HashSet<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		readWords();
		// setContentView(R.layout.activity_main);
		String[] elements = { "Connect Server", "Start Server" };

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
		} else if (element == "Start Server") {
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
		try {
			return Connector.INSTANCE.getAddress();
		} catch (IOException e) {
			e.printStackTrace();
			return "Failed to retrieve IP";
		}
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

	private void readWords() {
		AssetManager assetManager = getAssets();
		try {
			String[] files = assetManager.list("");
			for (String fileName : files) {
				if (fileName.endsWith(".alias")) {
					InputStream in = assetManager.open(fileName);
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(in, "UTF-8"));
					String line;
					while ((line = reader.readLine()) != null) {
						
						if(line.startsWith("//"))
							continue;
						
						String[] split = line.split(":");

						if (split.length == 0)
							continue;

						String words;
						String[] keywords = split[0].split(",");
						if (split.length == 1) {
							words = split[0];

							for (String word : words.split(",")) {
								word = word.trim();
								if (word.length() == 0)
									continue;
								if (!WORDS.keySet().contains(word))
									WORDS.put(word, new ArrayList<Keyword>());
							}
						} else {
							words = split[1];

							for (String word : words.split(",")) {
								word = word.trim();

								if (word.length() == 0)
									continue;

								if (WORDS.keySet().contains(word)
										&& WORDS.get(word).size() != 0)
									continue;

								WORDS.put(word, new ArrayList<Keyword>());

								for (String keyword : keywords) {

									keyword = keyword.trim();

									boolean absolute = keyword.startsWith("[")
											&& keyword.endsWith("]");
									
									if (absolute) {
										keyword = keyword.substring(1,
												keyword.length() - 1);
										ABSOLUTES.add(keyword);
									} 
									Keyword key = new Keyword(keyword, false);
									KEYWORDS.add(keyword);

									if (keyword.length() == 0) {
										continue;
									}

									WORDS.get(word).add(key);
								}
							}
						}
					}
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public static Set<String> getWordsForKeys(List<String> keywords) {
		Set<String> selected = new HashSet<String>();

		for (String word : WORDS.keySet()) {
			boolean match = true;
			
			for (String absolute : ABSOLUTES) {
				if(WORDS.get(word).contains(new Keyword(absolute, false)) && !keywords.contains(absolute)){
					match = false;
				}
			}
			
			if(!match)
				continue;
			
			for (String keyword : keywords) {
				if (!WORDS.get(word).contains(new Keyword(keyword, false))) {
					match = false;
				}
			}
			
			if(!match)
				continue;
			
			selected.add(word);
		}
		return selected;
	}

	public static class Keyword {

		private String key;
		private boolean absolute;

		public Keyword(String key, boolean absolute) {
			this.key = key;
			this.absolute = absolute;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Keyword other = (Keyword) obj;
			if (key == null) {
				if (other.key != null)
					return false;
			} else if (!key.equals(other.key))
				return false;
			return true;
		}

	}
}
