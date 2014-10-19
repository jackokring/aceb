package com.github.jackokring.aceb;

public interface OSAdapter {
	
	public char inKey();//gets 1 key if +ve
	public boolean hasKey();//check availability
	public void outKeys(String key);//edit by placing string before current
	
	public void setChar(int x, int y, char c);//draw a character
	public void setRes(int x, int y, char col);//set resolution and background
	
	public void inURL(String url);//get url and place before current for source reading
	public void outURL(String url);//open a web view for media and such
	
	public char inJoy();//get the joystick
	public void outAudio(String music);//play audio
	
	public void onBackPressed();//allow exit
	public void scroll();//scroll display
	
	public void setMachine(String simple);
	public void send(String app, String code);
	
	public void setTick(char milli);
	public char getTicks();
}
