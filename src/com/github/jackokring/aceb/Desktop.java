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
    public Lister history;
    
    public void OnPause(Bundle b) {
        a.p = true;
    }

    public void destroyApp(boolean unconditional) {
        a.p = false;
        a.destroy = true;
    }
    
    public void onBackPressed() {
    	xit.show();
    }

    //TODO: oncreate vs constructor
    public Desktop() {
        gc = new DisplayTerminal();
        ta = new TextBox();
        //TODO: needs override of ok, cancel
        xit = new MyDialog(R.string.xit, R.string.xit_help);
        probs = new MyDialog(R.string.probs, R.string.probs_help);
        load = new MyDialog(R.string.load, R.string.load_help);
        save = new MyDialog(R.string.save, R.string.save_help);
        reset = new MyDialog(R.string.reset, R.string.reset_help);
        enter = new MyDialog(R.string.enter, R.string.enter_help);
        history = new Lister("PAST");
        setCurrent(gc);
        //TODO: vid and pause start...
        a.vidout(1);
    }

    /* THE KEY INTERFACE */

    public void exited() {
        System.exit(0);//the last to happen
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

    public int openURL(String s) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void play(String s) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
