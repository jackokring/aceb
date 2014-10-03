package com.github.jackokring.aceb;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.os.Bundle;

public interface Machine {

    public void load(Bundle b);
    public void save(Bundle b);
    public void load(FileInputStream f);
    public void save(FileOutputStream f);
    //TODO: wants and a IO setter?
    public String asString(int s);
    public void reset(boolean build);
    
}
