package com.github.jackokring.aceb;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jacko
 */

import java.io.IOException;
import java.io.InputStream;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

public class Desktop extends MainActivity {
	
	//default ones
	int viewXML = R.layout.activity_desktop;
	int menuXML = R.menu.desktop;
	
    AceB a = new AceB(this);
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch(id) {
		//TODO: fill in actions
		case R.id.action_home: if(!setCurrent(gc)) reset.show(); return true;
		case R.id.action_edit: if(!setCurrent(ta)) enter.show(); return true;
		case R.id.action_load: load.show();return true;
		//rest is settings in super
		default: return super.onOptionsItemSelected(item);
		}
	}

    public DisplayTerminal gc;
    public TextBox ta;
    public MyDialog xit;
    public MyDialog probs;
    public MyDialog load;
    public MyDialog save;
    public MyDialog reset;
    public MyDialog enter;
    
    public void onBackPressed() {
    	xit.show();
    }
    
    public void onSaveInstanceState(Bundle b) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(b);
        b.putInt("remove", remove);//last screen TODO: more
        ta.save(b);
        a.save(b);
        gc.save(b);
    }
    
    public void onRestoreInstanceState(Bundle b) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(b);
        remove = b.getInt("remove");
        ta.load(b);
        a.load(b);
        gc.load(b);
    }

    //TODO: oncreate vs constructor
    public Desktop() {
        gc = new DisplayTerminal();
        ta = new TextBox();
        //TODO: needs override of ok, cancel
        //Dialogs do not persist, as it is easy to get them again
        xit = new MyDialog(R.string.xit, R.string.xit_help) {
        	public void ok() {
        		finish();
        	}
        };
        probs = new MyDialog(R.string.probs, R.string.probs_help);
        load = new MyDialog(R.string.load, R.string.load_help);
        save = new MyDialog(R.string.save, R.string.save_help);
        reset = new MyDialog(R.string.reset, R.string.reset_help);
        enter = new MyDialog(R.string.enter, R.string.enter_help);
        setCurrent(gc);
        //TODO: vid and pause start...
        a.vidout(1);
    }

    /* THE VIDEO INTERFACE */
    private int remove = R.id.content;

    public boolean setCurrent(Fragment a) {
    	if(a.getId() == remove) return false;
        FragmentManager fm = this.getSupportFragmentManager();
        fm.beginTransaction().replace(remove, a).commit();
        remove = a.getId();
        return true;
    }

    /* THE AUDIO INTERFACE */

    public void beep(int f, int d) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /* THE STORAGE INTERFACE */
    
    InputStream urlStream;
    // a UTF-8 to UTF-16 without suragate handling reader
    public int inurl(int s) {
        try {
            int i;
            if(s != 0) {
                if(urlStream != null) urlStream.close();
                /* urlStream = Connector.openInputStream(asString(s)); */
                openURL(a.asString(s));
            }
            i = (new UTF()).fromUTF(urlStream);
            return i;
        } catch (Exception e) {
        	//TODO: IO error
            return 0;
        }
    }
    
    public void edit(int s) {
        ta.setString(a.asString(s)+ta.getString());
        setCurrent(ta);
    }

    public int openURL(String s) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void play(String s) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}