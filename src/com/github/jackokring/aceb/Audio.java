package com.github.jackokring.aceb;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.SoundPool;

public class Audio implements Runnable, OnSharedPreferenceChangeListener {
	
	Thread sound = new Thread(this);
	SoundPool pool;
	int ticks;
	Desktop desk;

	public Audio(Desktop desktop) {
		desk = desktop;
	}
	
	private boolean pause = true;

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(!pause) {
			
		}
	}

	public void pause(boolean b) {
		// TODO Auto-generated method stub
		if(b) {
			pause = true;
			pool.autoPause();
		} else {
			if(pool == null) {
				pool = new SoundPool(x, x, x);
			}
			pause = false;
			sound.start();
			pool.autoResume();
		}
	}

	public void set(String s) {
		// TODO Auto-generated method stub
		
	}
	
	public void setTick(char num) {
		if(num > 150 || num < 1) onSharedPreferenceChanged(desk.sp,
				"pref_ticks");
		ticks = 1000 / num;
	}

	public char getTicks() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if(key.equals("pref_ticks")) {
			ticks = 1000 / sharedPreferences.getInt("pref_ticks", 50);
		}
	}
}
