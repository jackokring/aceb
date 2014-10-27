package com.github.jackokring.aceb;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.SoundPool;

public class Audio implements Runnable, OnSharedPreferenceChangeListener {
	
	Thread sound = new Thread(this);
	SoundPool pool;

	public Audio(Desktop desktop) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	public void pause(boolean b) {
		// TODO Auto-generated method stub
		if(b) {
			
		} else {
			if(pool == null) {
				
				sound.start();
			}
		}
	}

	public void set(String s) {
		// TODO Auto-generated method stub
		
	}
	
	public void setTick(char milli) {
		// TODO Auto-generated method stub
		
	}

	public char getTicks() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		
	}

}
