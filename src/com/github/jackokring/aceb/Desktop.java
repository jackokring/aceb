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
		case R.id.action_home: setCurrent(gc); return true;
		case R.id.action_edit: setCurrent(ta); return true;
		case R.id.action_load: return true;
		case R.id.action_save: return true;
		//rest is settings in super
		default: return super.onOptionsItemSelected(item);
		}
	}

    public DisplayTerminal gc;
    public TextBox ta;
    public MyDialog xit;
    public MyDialog probs;
    public Lister history;
    
    public void OnPause(Bundle b) {
        a.p = true;
    }

    public void destroyApp(boolean unconditional) {
        a.p = false;
        a.destroy = true;
    }
    
    /*

    public void actions(Action c, Keys s) {
        if (c == exitCommand) {
            if(s == gc) {
                ((Video)machine).setCurrent(xit);
            } else if(s == ta) {
                //keep buffer for flipping back to look
                ((Video)machine).setCurrent(gc);
            } else {
                ((Video)machine).setCurrent(ta);
            }
        }
        if (c == inputCommand) {
            //prcess input screen
            if(s == gc) {
                ((Video)machine).setCurrent(ta);
            } else if(s == ta) {
                if(history.append(ta.getString())>16) {
                    history.delete(0);
                }
                a.evaluate(ta.getString());
                ta.setString("");
                ((Video)machine).setCurrent(gc);
            } else {
                int i = history.getSelectedIndex();
                if(i!=-1) ta.setString(history.getString(i));
                setCurrent(ta);
            }
        }
        if(c == brk) a.alloc();//goto start vector
        if(c == rst) {
            a.destroy = true;
            while(!exited) Thread.yield();
            (new Thread(this)).start();
        }
        if(c == past) {
            ((Video)machine).setCurrent(history);
        }
        if(s == xit) {
            destroyApp(false);
            ((Keys)machine).exited();
        }
    }
    
    */

    public Desktop() {
        gc = new DisplayTerminal();
        ta = new TextBox("FORTH Input");
        //TODO: needs override of ok, cancel
        xit = new MyDialog(R.string.xit, R.string.xit_help);
        probs = new MyDialog(R.string.probs, R.string.probs_help);
        history = new Lister("PAST");
        setCurrent(gc);
    }

    /* THE KEY INTERFACE */

    public void exited() {
        System.exit(0);//the last to happen
    }

    /* THE VIDEO INTERFACE */
    private int remove = R.id.content;

    public void setCurrent(Fragment a) {
        FragmentManager fm = this.getSupportFragmentManager();
        fm.beginTransaction().replace(remove, a).commit();
        remove = a.getId();
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
