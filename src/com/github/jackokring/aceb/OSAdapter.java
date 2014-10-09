package com.github.jackokring.aceb;

public interface OSAdapter {
	
	public int inKey();//gets 1 key if +ve
	public void outKeys(String key);//edit by placing string before current
	
	public void setChar(int x, int y, char c);//draw a character
	public void setRes(int x, int y, char col);//set resolution and background
	
	public void inURL(String url);//get url and place before current for source reading
	public void outURL(String url);//open a web view for media and such
	
	public int inJoy();//get the joystick
	public void outAudio(String music);//play audio
	
	public void onBackPressed();//allow exit
}
