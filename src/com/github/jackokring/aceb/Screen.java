package com.github.jackokring.aceb;

import android.graphics.Paint.FontMetrics;
import android.support.v4.app.Fragment;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author jacko
 */


public class Screen extends Fragment implements Keys {

    Screen last;
    ActionAssignment head;
    public String message;
    int line;

    public Screen(String s) {
        addOK();
        clear();
        stringToCells(s);
        last = ((Video) AceB.machine).getCurrent();
        ((Video) AceB.machine).setCurrent(this);
    }

    public void stringToCells(String s) {
        message = s;
        repaint();
    }

    public void clear() {
        stringToCells("");
    }

    @Override
    public void paint(Graphics g) {
        g.drawRect(0, 0, getWidth(), getHeight());
        line = 0;
        stringPaint(message,g);
    }

    public void stringPaint(String s, Graphics g) {
        FontMetrics fm = g.getFontMetrics();
        int w = fm.stringWidth(s);
        if(w > getWidth() && s.length() > 3) {
            int i;
            for(i = s.length()/2; i > 0 && s.charAt(i) != ' '; i--);
            if(i == 0) i = s.length()/2;
            line++;
            stringPaint(s.substring(0, i-1), g);
            line++;
            stringPaint(s.substring(i++, s.length()), g);
        } else {
            //centred from top printing
            g.drawString(s, (getWidth()-w)/2, line*fm.getHeight());
        }
    }

    public void addOK() {
        addCommand(new Action("OK", 0));
    }

    public void actions(Action ac, Screen s) {
        if (s == this) {
            ((Video) AceB.machine).setCurrentForget(last, this);
        }
    }

    public void exited() {
        //null, but used in keys interface to exit system
    }

    public synchronized void addCommand(Action a) {
        head = new ActionAssignment(a,this,head);
    }

	@Override
	public void actions(Action ac, Keys s) {
		// TODO Auto-generated method stub
		
	}
}
