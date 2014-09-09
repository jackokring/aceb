package com.github.jackokring.aceb;


import uk.co.peopleandroid.aceb.Keys;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jacko
 */
import java.awt.event.*;
import java.awt.*;
import uk.co.peopleandroid.aceb.*;

public class Action extends Button implements ActionListener {

    public void actionPerformed(ActionEvent e) {
        ActionAssignment aa = ActionAssignment.que;
        Keys k = Desktop.me.getCurrent();
        while(aa!=null) { //the message polling
            if(aa.a == this && aa.s == k) aa.s.actions(this, k);
            aa = aa.link;
        }
    }

    public Action(String s, int pri) {
        setLabel(s);
        addActionListener(this);
    }

    public static void noActions(Keys k) {

    }
}
