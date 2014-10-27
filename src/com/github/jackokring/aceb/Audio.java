package com.github.jackokring.aceb;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.AudioManager;
import android.media.SoundPool;

public class Audio implements Runnable, OnSharedPreferenceChangeListener {
	
	Thread sound = new Thread(this);
	SoundPool pool;
	int ticks;
	final int second = 26;//number of repeats about
	Desktop desk;
	String[] file = {
		"a27_5",
		"a110",
		"a440",
		"a1760",
		"a7040",
		"boing",
		"explode",
		"hyper",
		"zap"
	};
	int[] id = new int[file.length];
	float[] tune = new float[128];//a 0.5 to 2 tuning value, and length multiplier
	byte[] use = new byte[128];//an index into id

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
		if(b) {
			pause = true;
			if(pool != null) pool.autoPause();
		} else {
			if(pool == null) {
				pool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
				if(pool == null) return;
				for(int i = 0; i < file.length; i++) {
					try {
						id[i] = pool.load(desk.getResources().getAssets().openFd(file[i] + ".wav"), 1);
					} catch (Exception e) {
						
					}
				}
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
