package com.github.jackokring.aceb;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public abstract class MainActivity extends ActionBarActivity implements Video, Audio, Keys, Storage {
	
	//only for common life cycle stuff
	
	public int viewXML = R.layout.activity_desktop;
	public int menuXML = R.menu.desktop;

	private ShareActionProvider mShareActionProvider;

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

// Set up ShareActionProvider's default share intent
    MenuItem shareItem = menu.findItem(R.id.action_share);
    mShareActionProvider = (ShareActionProvider)
            MenuItemCompat.getActionProvider(shareItem);
    mShareActionProvider.setShareIntent(getDefaultIntent());

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

/** Defines a default (dummy) share intent to initialize the action provider.
  * However, as soon as the actual content to be used in the intent
  * is known or changes, you must update the share intent by again calling
  * mShareActionProvider.setShareIntent()
  */
private Intent getDefaultIntent() {
    Intent intent = new Intent(Intent.ACTION_SEND);
	intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
	fileUri = FileProvider.getUriForFile(this,
                            "com.github.jackokring.aceb.fileprovider",
                            memFile);
    intent.setDataAndType(fileUri, getContentResolver().getType(fileUri));
    return intent;
}
}
