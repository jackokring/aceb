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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import android.app.SearchManager;
import android.app.backup.BackupManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public class Desktop extends MainActivity implements OSAdapter {
	
	//default ones
	int viewXML = R.layout.activity_desktop;
	int menuXML = R.menu.desktop;
	
	public void defFile() {
		a.reset(true);//initial state
		save(getMemFile(), false);//make a dump
	}
	
    Machine a = new AceB(this);
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch(id) {
		//TODO: fill in actions
		case R.id.action_home: if(setCurrent(gc)) setCurrent(ws); else setCurrent(gc); return true;
		case R.id.action_edit: if(setCurrent(ta)) enter.show(); return true;
		case R.id.action_load: load.show(); return true;
		case R.id.action_save: save.show(); return true;
		case R.id.action_reset: reset.show(); return true;
		//rest is settings in super TODO: reset
		default: return super.onOptionsItemSelected(item);
		}
	}

    public DisplayTerminal gc;
    public TextBox ta;
    public WebShow ws;
    public MyDialog xit;
    public MyDialog probs;
    public MyDialog load;
    public MyDialog save;
    public MyDialog reset;
    public MyDialog enter;
    
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
    	try {
    		File f = new File(getFilesDir(), name);
			a.load(new FileInputStream(f));
		} catch (FileNotFoundException e) {
			if(err) probs.show();
		}
    }
    
    public void save(String name, boolean err) {
    	try {
    		File f = new File(getFilesDir(), name);
			a.save(new FileOutputStream(f));
		} catch (FileNotFoundException e) {
			if(err) probs.show();
		}
    }
    
    public void onSaveInstanceState(Bundle b) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(b);
        b.putInt("remove", remove);
        b.putString("buf", buf);
        b.putBoolean("run", run);
        ta.save(b);
        a.save(b);
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
        a.load(b);
        gc.load(b);
        ws.load(b);
    }

    //TODO: oncreate vs constructor
    public Desktop() {
        gc = new DisplayTerminal();
        ta = new TextBox();
        ws = new WebShow();
        //TODO: needs override of ok, cancel
        //Dialogs do not persist, as it is easy to get them again
        xit = new MyDialog(R.string.xit, R.string.xit_help) {
        	public void ok() {
        		save(getMemFile()+".bak", false);
        		finish();
        	}
        };
        probs = new MyDialog(R.string.probs, R.string.probs_help) {
        	public void cancel() {
        		throw new RuntimeException();//TODO: catch
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
        		enter();//rebuild init state
        	}
        };
        setCurrent(gc);
    }
    
    synchronized void enter() {
		run = true;
		buf = ta.enter();
	}

	boolean run = false;
    String buf = "";
    
	@Override
	public synchronized int inKey() {
		if(!run) {
			gc.cursor(true);
			return -1;//no key
		}
		if(buf.equals("")) {
			buf = ta.enter();
			if(buf.equals("")) {
				run = false;//end of input
				return -1;//no key
			}
		}
		int s = buf.charAt(0);
		buf = buf.substring(0, buf.length());
		return s;
	}

	@Override
	public synchronized void outKeys(String key) {
		ta.setString(key+ta.getString(false));
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
			//TODO:
		}
		buf.append(32);//chain source
		outKeys(buf.toString());
		enter();
	}

	@Override
	public void outURL(String url) {
		ws.e.loadUrl(url);
		setCurrent(ws);
	}

	@Override
	public int inJoy() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void outAudio(String music) {
		// TODO Auto-generated method stub
		
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
}