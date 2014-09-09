package com.github.jackokring.aceb;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jacko
 */

import uk.co.peopleandroid.desktop.Screen;
import uk.co.peopleandroid.desktop.*;

public interface Video {
    public void setCurrent(Screen a);
    public void setCurrentForget(Screen a, Screen last);
    public Screen getCurrent();
}
