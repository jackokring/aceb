package com.github.jackokring.aceb;

public interface Machine extends Runnable {

    //public void load(Bundle b);
    //public void save(Bundle b);
    public void load(char[] f);
    public char[] save();
    public void restart();
    public void end();
    public void reset();
    
    public SearchItem[] search();//return all possible results
    public void resX(char x);//resolution change call backs
    public void resY(char y);
    
}
