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

public class Desktop extends MainActivity {
	
	//default ones
	int viewXML = R.layout.activity_desktop;
	int menuXML = R.menu.desktop;
	
    AceB a = new AceB(this);

    public Desktop() {
        //build Frame
    }

    /* THE KEY INTERFACE */

    public void exited() {
        System.exit(0);//the last to happen
    }

    public void actions(Action ac, Keys s) {
        a.actions(ac,s);//divert main messages
    }

    /* THE VIDEO INTERFACE */

    public void setCurrent(Screen a) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Screen getCurrent() {
        return null;//????
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
