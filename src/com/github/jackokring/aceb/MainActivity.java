package com.github.jackokring.aceb;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public abstract class MainActivity extends ActionBarActivity implements Video, Audio, Keys, Storage {
	
	//only for common life cycle stuff
	
	public int viewXML = R.layout.activity_desktop;
	public int menuXML = R.menu.desktop;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(viewXML);
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(menuXML, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public File getStorage() {
		return Environment.getExternalStoragePublicDirectory(
            	Environment.DIRECTORY_DOCUMENTS);
	}
}
