package com.github.jackokring.aceb;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jacko
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.app.backup.BackupManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.WindowManager;

public class Desktop extends MainActivity implements OSAdapter, OnSharedPreferenceChangeListener {
	
	//default ones
	protected int viewXML = R.layout.activity_desktop;
	protected int menuXML = R.menu.desktop;
	
	protected InputStream getFile(String ext) throws IOException {
		File nf;
		String full = getMemFile();
		if(ext != null) full += ext;
		switch(sp.getInt("pref_file", 1)) {
		default:case 1://internal
			nf = new File(getFilesDir(), full);
			break;
		case 2://External
			nf = new File(getExternalFilesDir(null), full);
			break;
		case 3://Public Downloads
			nf = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
					full);
			break;
		case 4://Assets
			return getAssets().open(full);
		}
		return new FileInputStream(nf);
	}
	
	protected OutputStream putFile(String ext) throws IOException {
		File nf;
		String full = getMemFile();
		if(ext != null) full += ext;
		switch(sp.getInt("pref_file", 1)) {
		default:case 1://internal
			nf = new File(getFilesDir(), full);
			break;
		case 2://External
			nf = new File(getExternalFilesDir(null), full);
			break;
		case 3://Public Downloads
			nf = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
					full);
			break;
		case 4://Assets TODO: hall of fame?
			/*
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://www.yoursite.com/script.php");

			try {
			    // Add your data
			    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			    nameValuePairs.add(new BasicNameValuePair("id", "12345"));
			    nameValuePairs.add(new BasicNameValuePair("stringdata", "AndDev is Cool!"));
			    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			    // Execute HTTP Post Request
			    HttpResponse response = httpclient.execute(httppost);

			} catch (ClientProtocolException e) {
			    // TODO Auto-generated catch block
			} catch (IOException e) {
			    // TODO Auto-generated catch block
			} */
			throw new IOException();
		}
		return new FileOutputStream(nf);
	}	
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch(id) {
		case R.id.action_home:
			if(setCurrent(gc)) setCurrent(ws); else setCurrent(gc); return true;
		case R.id.action_edit:
			if(setCurrent(ta)) enter.show(); return true;
		case R.id.action_load:
			load.show(); return true;
		case R.id.action_save:
			save.show(); return true;
		case R.id.action_reset:
			reset.show(); return true;
		//rest is settings in super
		default: return super.onOptionsItemSelected(item);
		}
	}

    DisplayTerminal gc;
    TextBox ta;
    WebShow ws;
    SearchList ls;
    MyDialog xit;
    MyDialog probs;
    MyDialog load;
    MyDialog save;
    MyDialog reset;
    MyDialog enter;
    
    public void onBackPressed() {
    	xit.show();
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onNewIntent(getIntent());

    }
    
    protected void onNewIntent(Intent intent) {
        if(Intent.ACTION_SEND.equals(intent.getAction())) {
        	//a file has been sent
        	Uri u = intent.getData();//gets the filename
        	String s = u.toString();
        	if(s.substring(s.length() - getExtension().length()).equals(getExtension())) {
        		//have a binary
        		loadIntent(u, true);
        	} else if(intent.getType() != null
	        		&& intent.getType().equals(getResources().getString(R.string.mimecode))) {
	        	//some direct code has been sent
	        	s = intent.getStringExtra(Intent.EXTRA_TEXT);
	        	s += "\n";
				outKeys(s);
				enter();
	        } else {
        		//treat as code
        		loadIntent(u, false);
        	}
        }
        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
        	setCurrent(ls);
        	ls.search(intent.getStringExtra(SearchManager.QUERY));
        }
    }
    
    //UTF via readers not data input streams
    
    protected void loadIntent(Uri u, boolean bin) {
    	StringBuilder buf = new StringBuilder();
    	try {
    		Reader in = new InputStreamReader(getContentResolver().openInputStream(u));
    		while (true) {
				int ch = in.read();
				if (ch < 0) break;
				buf.append((char) ch);
			}
			in.close();
			if(!bin) {
				buf.append("\n");
				outKeys(buf.toString());
				enter();
			}
			char[] ch = new char[buf.length()];
			buf.toString().getChars(0, buf.length(), ch, 0);
			a.load(ch);
		} catch (Exception e) {
			//nope.
		}
    }    
    
    public void load(String ext, boolean err) {
    	StringBuilder buf = new StringBuilder();
    	try {
    		Reader in = new InputStreamReader(getFile(ext));
    		while (true) {
				int ch = in.read();
				if (ch < 0) break;
				buf.append((char) ch);
			}
			in.close();
			char[] ch = new char[buf.length()];
			buf.toString().getChars(0, buf.length(), ch, 0);
			a.load(ch);
		} catch (Exception e) {
			if(err) probs.show();
		}
    }
    
    public void save(String ext, boolean err) {
    	char[] ch = a.save();
    	try {
    		Writer out = new OutputStreamWriter(putFile(ext));
    		for(int i = 0; i < ch.length; i++)
    			out.write(ch[i]);
			out.close();
		} catch (Exception e) {
			if(err) probs.show();
		}
    	prefUpdate();
    }
    
    protected void prefUpdate() {
		mShareActionProvider.setShareIntent(getDefaultIntent());
		BackupManager bm = new BackupManager(this);
		bm.dataChanged();
    }
    
    public void onSaveInstanceState(Bundle b) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(b);
        b.putInt("remove", remove);
        b.putString("buf", buf);
        b.putBoolean("run", run);
        ta.save(b);
        b.putCharArray("mem", a.save());
        gc.save(b);
        ws.save(b);
        ls.save(b);
        b.putString("url", urlp);
        b.putBoolean("fetch", fetch);
        b.putBoolean("fetched", fetched);
        b.putString("output", output);
        b.putBoolean("error", error);
        b.putBoolean("js", js);
    }
    
    public void onRestoreInstanceState(Bundle b) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(b);
        remove = b.getInt("remove");
        buf = b.getString("buf");
        run = b.getBoolean("run");
        ta.load(b);
        a.load(b.getCharArray("mem"));
        gc.load(b);
        ws.load(b);
        ls.load(b);
        urlp = b.getString("urlp");
        fetch = b.getBoolean("fetch");
        fetched = b.getBoolean("fetched");
        output = b.getString("output");
        error = b.getBoolean("error");
        js = b.getBoolean("js");
        for(int i = 0; i < frags.length; i++)
        	if(remove == frags[i].getId()) {
        		setCurrent(frags[i]);
        		break;
        	}
        if(fetch) inURL(urlp);//complete URL fetch
    }
    
    SharedPreferences sp;
    
    public void finalize() {
    	sp.unregisterOnSharedPreferenceChangeListener(this);
    }
    
    protected boolean setCurrent(Fragment a) {
    	if(a == gc) getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	else getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	return super.setCurrent(a);
    }
    
    protected boolean js = false;
    
    protected class JavaScriptOS implements OSAdapter {
    	
    	OSAdapter proxy;
    	
    	public JavaScriptOS(OSAdapter a) {
    		proxy = a;
    	}
    	
    	private void installOSBlock() {
    		a.pause(true);
    		while(!a.getSafe()) Thread.yield();
    		js = true;
    	}
    	
    	public synchronized void release() {
    		js = false;
    		a.pause(false);//the restart always lags so always safe
    	}
    	
    	public synchronized void machine(String input) {
    		installOSBlock();
    		proxy.outKeys(input+buf);
    		enter();
    		release();
    	}

		@Override
		public synchronized char inKey() {
			installOSBlock();
			return proxy.inKey();
		}

		@Override
		public synchronized boolean hasKey() {
			installOSBlock();
			return proxy.hasKey();
		}

		@Override
		public synchronized void outKeys(String key) {
			installOSBlock();
			proxy.outKeys(key);
		}

		@Override
		public synchronized void setChar(char x, char y, char c) {
			installOSBlock();
			proxy.setChar(x, y, c);
		}

		@Override
		public synchronized void setRes(char x, char y, char col) {
			installOSBlock();
			proxy.setRes(x, y, col);
		}

		@Override
		public synchronized void inURL(String url) {
			installOSBlock();
			proxy.inURL(url);
		}

		@Override
		public synchronized void outURL(String url) {
			installOSBlock();
			proxy.outURL(url);
		}

		@Override
		public synchronized char inJoy() {
			installOSBlock();
			return proxy.inJoy();
		}

		@Override
		public synchronized void outAudio(String music) {
			installOSBlock();
			proxy.outAudio(music);
		}

		@Override
		public synchronized void scroll() {
			installOSBlock();
			proxy.scroll();
		}

		@Override
		public void setMachine(String simple) {
			//very difficult to do as async, try machine()
			throw new RuntimeException(".setMachine() Not Allowed.");
		}

		@Override
		public synchronized void send(String app, String code) {
			proxy.send(app, code);
		}

		@Override
		public synchronized void setTick(char milli) {
			installOSBlock();
			proxy.setTick(milli);
		}

		@Override
		public synchronized char getTicks() {
			installOSBlock();
			return proxy.getTicks();
		}
    }
    
    protected Fragment[] frags = new Fragment[4];

    @SuppressLint("SetJavaScriptEnabled")
	public Desktop() {
    	frags[0] = gc = new DisplayTerminal(this);
    	frags[1] = ta = new TextBox();
    	frags[2] = ws = new WebShow();
    	frags[3] = ls = new SearchList(this);
    	ws.e.getSettings().setJavaScriptEnabled(true);
    	ws.e.addJavascriptInterface(new JavaScriptOS(this), "OSAdapter");
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        onSharedPreferenceChanged(sp, "a");
        sp.registerOnSharedPreferenceChangeListener(this);
        //Dialogs do not persist, as it is easy to get them again
        xit = new MyDialog(R.string.xit, R.string.xit_help) {
        	public void ok() {
        		save(".bak", false);
        		a.end();//clean up!
        		finish();
        	}
        };
        probs = new MyDialog(R.string.probs, R.string.probs_help) {
        	public void cancel() {
        		error = true;
        	}
        };
        load = new MyDialog(R.string.load, R.string.load_help) {
        	public void ok() {
        		load(null, true);
        	}
        };
        save = new MyDialog(R.string.save, R.string.save_help) {
        	public void ok() {
        		save(null, true);
        	}
        };
        reset = new MyDialog(R.string.reset, R.string.reset_help) {
        	public void ok() {
        		a.reset(true);//rebuild init state
        	}
        };
        enter = new MyDialog(R.string.enter, R.string.enter_help) {
        	public void ok() {
        		enter();
        	}
        };
        sound.start();
    }

    
    synchronized void enter() {
		run = true;
		buf = ta.enter();
		if(buf.equals("")) run = false;//no input
	}
    
    synchronized void enterOutput() {
    	fetch = false;
    	fetched = false;
    	outKeys(output);
    	enter();
    }

	boolean run = false;
    String buf = "";
    String urlp = "";
    boolean fetch = false;
    boolean fetched = false;
    String output = "";
    boolean error = false;
    Thread u;
    
    @Override
    public synchronized boolean hasKey() {//call after inKey() to check valid
    	return run && !fetch;//while not URL getting or has valid
    }
    
	@Override
	public synchronized char inKey() {
		if(error) {
			a.reset(false);
			error = false;
		}
		if(fetched == true) enterOutput();
		if(!hasKey()) {
			gc.cursor(true);
			return 0;//no key
		}
		if(buf.equals("")) {
			enter();
			return inKey();//nest test
		}
		char s = buf.charAt(0);
		buf = buf.substring(0, buf.length());
		return s;
	}

	@Override
	public synchronized void outKeys(String key) {
		ta.out(key+buf);
		buf = "";
        setCurrent(ta);
	}

	@Override
	public synchronized void inURL(String url) {
		u = new Thread(new UrlGetter(url));
		u.start();
	}
	
	protected class UrlGetter implements Runnable {
		
		public UrlGetter(String url) {
			urlp = url;
			fetched = false;
			fetch = true;
		}
		
		public void run() {
			StringBuilder buf = new StringBuilder();
			try {
				URL ht = new URL(urlp);
				Reader r = new InputStreamReader(ht.openStream());
				while (true) {
					int ch = r.read();
					if (ch < 0) break;
					buf.append((char) ch);
				}
			} catch (Exception e) {
				probs.show();
				fetched = false;
				fetch = false;
			}
			buf.append("\n");//chain source
			output = buf.toString();
			fetched = true;
			fetch = false;
		}
	}

	@Override
	public synchronized void outURL(String url) {
		ws.e.loadUrl(url);
		setCurrent(ws);
	}
	
	Joy j = new Joy(this);
	Audio m = new Audio(this);
	Thread sound = new Thread(m);
	
	public void onPause() {
		j.pause(true);
		a.pause(true);
		m.pause(true);
	}
	
	public void onResume() {
		j.pause(false);
		if(!js) a.pause(false);//javascript serving
		m.pause(false);
	}

	@Override
	public synchronized char inJoy() {
		return j.get();
	}

	@Override
	public synchronized void outAudio(String music) {
		m.set(music);		
	}

	@Override
	public synchronized void setChar(char x, char y, char c) {
		gc.setCell(x, y, c);	
	}

	@Override
	public synchronized void setRes(char x, char y, char col) {
		gc.setRes(x, y, col);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		prefUpdate();//backup
		if(sp != sharedPreferences || !key.equals("a")) return;
		int num = sp.getInt("a", 1);
		if(sp.getBoolean("can_use", false) == false) num = -1;
		boolean reset = false;
		if(a != null) {// not first run
			save(".bak", false);//save
		} else reset = true;
		OSAdapter t = this;
		switch(num) {
		case 2:
			a = new Tester(t);
			break;
		case 1:	
		default:
			a = new AceB(t);
			break;
		}
		outURL("file:///android_asset/" + super.getMemFile() + "/index.html");//intro
		a.reset(reset);
		getSupportActionBar().setIcon(new BitmapDrawable(getResources(), getIcon(a)));
		load(".bak", false);
		enter();//maybe some queued input
	}

	@Override
	public synchronized void scroll() {
		gc.scroll();
		setCurrent(gc);//show video out
	}

	@Override
	public void setMachine(String simple) {
		if(sp.getBoolean("can_use", false) == false) return;
		String[] mach = getResources().getStringArray(R.array.a);
		for(int i = 0; i < mach.length; i++) {
			if(mach[i].equals(a.getClass().getSimpleName())) {
				Editor e = sp.edit();
				int[] j = getResources().getIntArray(R.array.a_val);
				e.putInt("a", j[i]);
				a.end();//expect async
				e.commit();
				break;
			}
		}
	}

	@Override
	public void send(String app, String code) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_TEXT, code);
		intent.setType(getResources().getString(R.string.mimecode) + "." + app);
		startActivity(intent);
	}

	@Override
	public void setTick(char milli) {
		m.setTick(milli);
	}

	@Override
	public char getTicks() {
		return m.getTicks();
	}

	public Bitmap getIcon(Machine res) {
		try {
			InputStream i = getAssets().open(res.getClass().getSimpleName() + "/icon.png");
			Bitmap b = BitmapFactory.decodeStream(i);
			i.close();
			return b;
		} catch (IOException e) {
			return ((BitmapDrawable)getResources().getDrawable(R.drawable.ic_launcher)).getBitmap();
		}
	}
}