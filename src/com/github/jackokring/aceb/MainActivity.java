package com.github.jackokring.aceb;

import java.io.File;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.ShareActionProvider;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.*;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

public abstract class MainActivity extends ActionBarActivity {
	
	//only for common life cycle stuff
	
	protected int viewXML = R.layout.activity_desktop;
	protected int menuXML = R.menu.desktop;

	protected ShareActionProvider mShareActionProvider;
	
	protected Machine a;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(viewXML);
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(menuXML, menu);

		// Set up ShareActionProvider's default share intent
		MenuItem shareItem = menu.findItem(R.id.action_share);
		mShareActionProvider = (ShareActionProvider)
										MenuItemCompat.getActionProvider(shareItem);
		mShareActionProvider.setShareIntent(getDefaultIntent());
		MenuItem searchItem = menu.findItem(R.id.action_search);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

		return true;
	}
	
    int remove = R.id.content;

    protected boolean setCurrent(Fragment a) {
    	if(a.getId() == remove) return true;
        FragmentManager fm = this.getSupportFragmentManager();
        fm.beginTransaction().replace(remove, a).commit();
        remove = a.getId();
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			this.startActivity(new Intent(this, SettingsActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	protected String getMemFile() {
		return a.getClass().getSimpleName() + getExtension();//a binary image share
	}
	
	protected String getExtension() {
		return getResources().getString(R.string.extension);
	}

	/** Defines a default share intent to initialize the action provider.
	  * However, as soon as the actual content to be used in the intent
	  * is known or changes, you must update the share intent by again calling
	  * mShareActionProvider.setShareIntent()
	  */
	protected Intent getDefaultIntent() {
	    Intent intent = new Intent(Intent.ACTION_SEND);
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		File f = new File(getMemFile());
		if(!f.exists()) {
			findViewById(R.id.action_share).setClickable(false);//hide it
			return null;
		}
		Uri fileUri = FileProvider.getUriForFile(this,
				getResources().getString(R.string.fileprovider), f);
	    intent.setDataAndType(fileUri, getContentResolver().getType(fileUri));
	    findViewById(R.id.action_share).setClickable(true);//show it
	    return intent;
	}
}
