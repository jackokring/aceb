package com.github.jackokring.aceb;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jacko
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import android.app.SearchManager;
import android.app.backup.BackupManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;

public class Desktop extends MainActivity implements OSAdapter, OnSharedPreferenceChangeListener {
	
	//default ones
	protected int viewXML = R.layout.activity_desktop;
	protected int menuXML = R.menu.desktop;

	protected void defFile() {
		Thread back = new Thread() {
			public void run() {
				save(getMemFile(), false);//make a dump
		}};
		back.start();//in background do it
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
        load(getMemFile()+".bak", false);
        //setContentView(R.layout.search);
        //TODO: intent handlers

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
          String query = intent.getStringExtra(SearchManager.QUERY);
          //doMySearch(query);
        }
    }
    
    public void load(String name, boolean err) {
    	File f = new File(getFilesDir(), name);
    	StringBuilder buf = new StringBuilder();
    	try {
    		DataInputStream in = new DataInputStream(new FileInputStream(f));
			while(in.available() > 0) {
				buf.append(in.readChar());
			}
			in.close();
			char[] ch = new char[buf.length()];
			buf.toString().getChars(0, buf.length(), ch, 0);
			a.load(ch);
		} catch (Exception e) {
			if(err) probs.show();
		}
    }
    
    public void save(String name, boolean err) {
    	File f = new File(getFilesDir(), name);
    	char[] ch = a.save();
    	try {
    		DataOutputStream out = new DataOutputStream(new FileOutputStream(f));
    		for(int i = 0; i < ch.length; i++)
    			out.writeChar(ch[i]);
			out.close();
		} catch (Exception e) {
			if(err) probs.show();
		}
    	if(!(new File(getMemFile())).exists()) return;//no valid method of constructing file
		mShareActionProvider.setShareIntent(getDefaultIntent());
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
    }
    
    Machine a;
    SharedPreferences sp;
    
    public void finalize() {
    	sp.unregisterOnSharedPreferenceChangeListener(this);
    }

    public Desktop() {
        gc = new DisplayTerminal(this);
        ta = new TextBox();
        ws = new WebShow();
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        onSharedPreferenceChanged(sp, "a");
        sp.registerOnSharedPreferenceChangeListener(this);
        //Dialogs do not persist, as it is easy to get them again
        xit = new MyDialog(R.string.xit, R.string.xit_help) {
        	public void ok() {
        		save(getMemFile()+".bak", false);
        		a.end();//clean up!
        		finish();
        	}
        };
        probs = new MyDialog(R.string.probs, R.string.probs_help) {
        	public void cancel() {
        		throw new RuntimeException();
        	}
        };
        load = new MyDialog(R.string.load, R.string.load_help) {
        	public void ok() {
        		load(getMemFile(), false);
        	}
        };
        save = new MyDialog(R.string.save, R.string.save_help) {
        	public void ok() {
        		save(getMemFile(), false);
        		BackupManager bm = new BackupManager(this.getActivity().getApplicationContext());
        		bm.dataChanged();
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

	boolean run = false;
    String buf = "";
    
    @Override
    public boolean hasKey() {//call after inKey() to check valid
    	return run;
    }
    
	@Override
	public synchronized char inKey() {
		if(!run) {
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
		ta.setString(key+buf+ta.getString(false));
        setCurrent(ta);
	}

	@Override
	public synchronized void inURL(String url) {
		StringBuilder buf = new StringBuilder();
		try {
			URL ht = new URL(url);
			Reader r = new InputStreamReader(ht.openStream());
			while (true) {
				int ch = r.read();
				if (ch < 0) break;
				buf.append((char) ch);
			}
		} catch (Exception e) {
			probs.show();
		}
		buf.append("\n");//chain source
		outKeys(buf.toString());
		enter();
	}

	@Override
	public void outURL(String url) {
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
		a.pause(false);
		m.pause(false);
	}

	@Override
	public char inJoy() {
		return j.get();
	}

	@Override
	public void outAudio(String music) {
		m.set(music);		
	}

	@Override
	public void setChar(int x, int y, char c) {
		gc.setCell(x, y, c);	
	}

	@Override
	public void setRes(int x, int y, char col) {
		gc.getNew(x, y);
		gc.clear(col);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if(sp != sharedPreferences || !key.equals("a")) return;
		int num = sp.getInt("a", 1);
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
		outURL("file:///android_asset/" + a.getClass().getSimpleName() + "/index.html");//intro
		a.reset(true);
	}

	@Override
	public void scroll() {
		gc.scroll();
		setCurrent(gc);//show video out
	}
}