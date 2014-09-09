/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.github.jackokring.aceb;

/**
 *
 * @author jacko
 */
import uk.co.peopleandroid.desktop.*;

public class ActionAssignment {

    public static ActionAssignment que;
    public ActionAssignment link;

    public Action a;
    public Keys s;
    public ActionAssignment aa;

    public ActionAssignment(Action ac, Keys i, ActionAssignment ab) {
        a = ac;
        s = i;
        aa = ab;
        link = que;
        que = this;
    }
}
