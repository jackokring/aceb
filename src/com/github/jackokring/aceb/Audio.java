package com.github.jackokring.aceb;

import java.util.Random;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.AudioManager;
import android.media.SoundPool;

public class Audio implements Runnable, OnSharedPreferenceChangeListener {
	
	Thread sound = new Thread(this);
	SoundPool pool;
	int ticks;//millis
	int nLen;//number of repeats about
	final int maxChannel = 8;
	int current;
	long lastMilli;
	Random r = new Random();
	
	Desktop desk;
	String[] file = {
		"a27_5",
		"a110",
		"a440",
		"a1760",
		"a7040",
		"zap",//wh
		"hyper",//pi
		"boing",//br
		"explode"//bl
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
		while(!pause) {
			if(getMTicks()) {
				tr = head;//reset for play
				current = 0;
				while(tr != null) {
					while(tr != null && tr.at < tr.play.length()) {
						play((short)tr.play.charAt(tr.at++));//short for negation
					}
					if(tr != null && tr.at >= tr.play.length()) {
						Tracker h = head;
						if(h == tr) head = tr.link;
						else {
							while(h.link != tr) h = h.link;
							h.link = h.link.link;//cut
						}
					}
					if(tr != null) tr = tr.link;
					float f = (float)round(r.nextGaussian() / 3 + 1.25);
					tune[120] = f;//white
					tune[121] = (float)round(tune[121] + 0.1 * f);//pink
					tune[122] = (float)round(tune[121] + 0.1 * tune[121]);//brown
					tune[123] = (float)round(tune[121] + 0.1 * tune[122]);//black
				}
				desk.a.playCount((char)current);
			}
			Thread.yield();//wait about
		}
	}
	
	private double round(double f) {
		return Math.max(2, Math.min(0.5, f));
	}

	public void pause(boolean b) {
		if(b) {
			pause = true;
			if(pool != null) pool.autoPause();
		} else {
			if(pool == null) {
				pool = new SoundPool(maxChannel, AudioManager.STREAM_MUSIC, 0);
				if(pool == null) return;
				for(int i = 0; i < file.length; i++) {
					try {
						id[i] = pool.load(desk.getResources().getAssets().openFd(file[i] + ".wav"), 1);
					} catch (Exception e) {
						
					}
				}
			}
			genTable();
			pause = false;
			sound.start();
			pool.autoResume();
		}
	}
	
	public synchronized void clean() {
		pool.release();
		pool = null;
	}

	public synchronized void set(String s) {
		Tracker t = new Tracker(head, s);
		head = t;//queue up
	}
	
	private void play(int note) {
		int len = (note >> 7) & 15;//octave plus 4 random
		int vol = (note >> 11) & 15;//same
		note &= 127;
		if(note > 123) {
			special(note, len, vol);
			if(note < 0) tr.link = null;//cut current tracker list (flush older)
			return;
		}
		if(note < 0) tr.link = null;//cut current tracker list (flush older)
		if(++current > maxChannel) return;
		float volume = tune[107 + vol] / 2;//bound
		float length = Math.min(tune[107 + len] * tune[note] * nLen, 1);
		if(note > 119) length = 1;//single shot four builtins
		tr.loops = ((int)length);
		tr.streamID = pool.play(id[use[note]], volume, volume, 0, tr.loops - 1, tune[note]);
	}
	
	protected class Looper {
		int where;
		Looper link;
		int count;
		
		public Looper(Looper a, int at, int c) {
			link = a;
			where = at;
			count = c;
		}
	}
	
	protected class Tracker {
		Tracker link;
		String play;
		int at;
		Looper reps;
		int waiting = -1;
		int streamID;
		int loops;
		
		public Tracker(Tracker l, String p) {
			link = l;
			play = p;
			at = 0;
		}
	}
	
	Tracker tr;
	Tracker head;
	
	private void special(int note, int len, int vol) {
		//tr is valid
		switch(note) {
		case 124://hold note (no break)
			current++;//prevent fill
			pool.setLoop(tr.streamID, tr.loops * tr.waiting);//maybe???
		case 125://wait mticks
			if(tr.waiting == -1) {
				tr.waiting = len | (vol << 4);//init wait
			}
			if(--tr.waiting == -1) break;//waited
			tr.at--;//keep doing till
			tr = tr.link;//try older
			break;
		case 126://begin count
			Looper l = new Looper(tr.reps, tr.at, len | (vol << 4));
			tr.reps = l;//set up a looping object
			break;
		case 127://end loop back many
			int how = len | (vol << 4);
			Looper m = tr.reps;
			for(int i = 0; i < how; i++) {
				if(m.link == null) break;
				m = m.link;
			}
			if(m.count-- == 0) {
				m.count = (tr.play.charAt(m.where) >> 7) & 255;//restore for looping
				break;//done
			}
			tr.at = m.where;//loop back
			break;
		default:
			break;
		}
	}
	
	private void genTable() {
		for(int i = 0; i < 24; i++) {//2 octaves
			float freq = (float)Math.pow(2, ((float)(i - 12))/12);
			//ten octaves from 0
			for(int j = 0; j < 5; j++) {
				tune[i + j * 24] = freq;
				use[i + j * 24] = (byte)j;
			}
		}
		//four special notes
		for(int i = 0; i < 4; i++) {
			tune[i + 120] = 1;
			use[i + 120] = (byte)(i + 6);
		}
		//last four are meta notes 124, 125, 126, 127
		for(int i = 0; i < 4; i++) {
			tune[i + 124] = 1;
			use[i + 124] = (byte)(-i - 1);
		}
	}
	
	public synchronized void setTick(char num) {
		if(num > 150 || num < 1) onSharedPreferenceChanged(desk.sp,
				"pref_ticks");
		ticks = 1000 / num;
		nLen = ticks / 50;
	}

	public synchronized char getTicks() {
		long now = System.currentTimeMillis();
		long diff = now - lastMilli;
		long many = (diff / ticks);
		lastMilli = now - (diff - many * ticks);
		return (char)many;
	}
	
	private synchronized boolean getMTicks() { //get music ticks
		long now = System.currentTimeMillis();
		long diff = now - lastMilli;
		long many = (diff / ticks * 8);
		return many > 0;
	}

	@Override
	public synchronized void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if(key.equals("pref_ticks")) {
			ticks = 1000 / sharedPreferences.getInt("pref_ticks", 50);
			nLen = ticks / 50;
		}
	}
}
