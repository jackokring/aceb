package com.github.jackokring.aceb;

import java.util.Random;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.AudioManager;
import android.media.SoundPool;

/* This class represents some interesting coding. The ticks per second sets the tempo at
 * about 1/8th that value used for animation. Many notes can be played, and they are
 * prioritised last submitted. There are four special sounds and 4 control notes plus
 * a ten octave range (0 to 119). You must include control notes to not play all at once.
 * Setting the highest bit in a note clears any older notes when a note is played.
 * The special notes can be looped by using the hold control note if you want such.
 * 
 * The 8 bits above the lower 7 bits (the note) are the split into length (low 4) and
 * volume (high 4). 0 is short(0.5)/low and 11 is long(2)/high. Random jiggle can be
 * applied by using:
 * 
 * 12: White modulation.
 * 13: Pink modulation.
 * 14: Brown modulation.
 * 15: Black modulation.
 * 
 * Control notes use the whole 8 bit length/volume bit field for an integer for the
 * control, described by the control note.
 * 
 * Special notes (with modulation applied to frequency):
 * 
 * Zap: 120.
 * Hyper: 121.
 * Boing: 122.
 * Explode: 123.
 * 
 * Control notes: 
 * 
 * Hold: 124 - Keep the last note in stream playing and wait. (music ticks)
 * Wait: 125 - Wait to play rest of stream. (music ticks)
 * Begin: 126 - Mark the beginning of a repeat. (count of repeats to do)
 * Repeat: 127 - Loop back a number of repeats. (from how many beginnings back in stream)
 * 
 * The code contains some logic to reduce resources if the audio output is flooded with
 * requests to perform miracles. Input devices don't need this kind of handling, but
 * audio output does. The first stage performs a fading, and removes looping, while the
 * second stage is a plain killing of old sound. A machine must check to see if anything is
 * playing, and perhaps reissue some background atmospheric music or such.
 * 
 * For an audio device, this is most logical as it prevents sound insanity. Audio Spock!
 * This gives a durability to the code too.
 */

public class Audio implements Runnable, OnSharedPreferenceChangeListener {
	
	Thread sound = new Thread(this);
	SoundPool pool;
	int ticks;//millis
	int nLen;//number of repeats about
	final int maxChannel = 8;
	int current;
	long lastMilli;
	Random r = new Random();
	boolean dump = true;
	
	Desktop desk;
	String[] file = {
		"a27_5",
		"a110",
		"a440",
		"a1760",
		"a7040",
		"zap",//white noise
		"hyper",//pink noise
		"boing",//brown noise
		"explode",//black noise
		"Kick",
		"Eight08",
		"Snare",
		"Horn",
		"HiHat",
		"Shaker",
		"Clap2",
		"Clap1",
		"Ting"
	};
	int[] id = new int[file.length];
	float[] tune = new float[128];//a 0.5 to 2 tuning value, and length multiplier
	byte[] use = new byte[128];//an index into id
	byte[] drm = new byte[128];//an index into id

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
					synchronized(tr.play) {//lock a playing instance from changes
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
					}//extend lock to allow zero length change
					if(tr != null) tr = tr.link;
					/* boredom reduction circuitry
					 * handle with care !! */ 
					int f = r.nextInt();//24 bit
					byte c = -12;
					for(int i = 0; i < 23; i++) {// 0 mean binomial mix
						if((f & 1) == 0) c++;
						f >>= 1;
					}
					use[124] = (byte)(c * 11);//white
					use[125] = (byte)(use[125] + use[124] >>> 4);//pink 
					use[126] = (byte)(use[126] + use[125] >>> 4);//brown
					use[127] = (byte)(use[127] + use[126] >>> 4);//black
					for(int i = 0; i < 4; i++) {
						tune[120 + i] = tune[use[124 + i] * (12 / 128) + 12];//musically nice
					}
				}
				if(stress) desk.playCount((char)-1);
				else desk.playCount((char)current);//running
			}
			Thread.yield();//wait about
		}
	}

	public void pause(boolean b) {
		if(b) {
			pause = true;
			if(pool != null) pool.autoPause();
		} else {
			desk.playCount((char)-1);//initializing
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
			dump = false;
			pool.autoResume();
		}
	}
	
	public synchronized void clean() {
		pool.release();
		pool = null;
	}

	public synchronized void set(float x, float y, S s, boolean drums) {
		if(dump) return;//initialisation dump
		float fade = (float)(y / 2 + 0.5);
		float l = (1 - x) * fade;
		float r = x * fade;
		Tracker t = new Tracker(head, s, l, r, drums);
		head = t;//queue up
	}
	
	private void play(int note) {
		int len = (note >> 7) & 15;//octave plus 4 random
		int vol = (note >> 11) & 15;//same
		boolean flush = (note < 0);
		note &= 127;
		if(note > 123) {
			special(note, len, vol);
			if(flush) tr.link = null;//cut current tracker list (flush older)
			return;
		}
		float le = tr.le;
		float ri = tr.ri;
		current++;//notes playing
		//sensibility actions
		if(flush || current > 3 * maxChannel) tr.link = null;//cut current tracker list (flush older)
		if(current > 2 * maxChannel) {
			//fade
			tr.reps = null;//don't loop
			if((tr.le *= 0.98) < 0.05 && (tr.ri *= 0.98) < 0.05) tr.at = tr.play.length();//kill
		}
		if(current > maxChannel) return;
		//play note
		float volume = tune[107 + vol] / 2;//bound
		float length = Math.min(tune[107 + len] * tune[note] * nLen * pllIndex, 1);
		if(note > 119 || tr.drums) length = 1;//single shot four built-ins
		tr.loops = ((int)length) - 1;
		int used = id[use[note]];
		if(tr.drums) {
			used = id[drm[note]];
		}
		tr.streamID = pool.play(used, volume * le, volume * ri, 0, (int)(tr.loops * lenMul()), tune[note]);
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
		S play;
		int at;
		Looper reps;
		int waiting = -1;
		int streamID;
		int loops;
		float le, ri;
		boolean drums;
		
		public Tracker(Tracker l, S p, float left, float right, boolean dr) {
			link = l;
			play = p;
			at = 0;
			le = left;
			ri = right;
			drums = dr;
		}
	}
	
	Tracker tr;
	Tracker head;
	
	private float lenMul() {//to stop drony notes
		if(pllIndex > 255) return 16;
		return pllIndex * irt[pllIndex]; 
	}
	
	private void special(int note, int len, int vol) {
		//tr is valid
		switch(note) {
		case 124://hold note (no break)
			current++;//prevent fill
			if(tr.at > 0) {
				//allow LFO stylee
				int note2 = tr.at - 1;
				int len2 = (note2 >> 7) & 15;//octave plus 4 random
				int vol2 = (note2 >> 11) & 15;//same
				note2 &= 127;
				float volume = tune[107 + vol2] / 2;//bound
				float length = Math.min(tune[107 + len2] * tune[note2] * nLen * pllIndex, 1);
				if(note2 > 119 || tr.drums) length = 1;//single shot four built-ins
				tr.loops = ((int)length) - 1;
				pool.setRate(tr.streamID, tune[note2]);//freq mod
				pool.setVolume(tr.streamID, volume * tr.le, volume * tr.ri);//volume mod
			}
			pool.setLoop(tr.streamID, (int)(tr.loops * tr.waiting * lenMul()));//!!
		case 125://wait mticks
			if(tr.waiting == -1) {
				tr.waiting = len | (vol << 4);//init wait
			}
			if(--tr.waiting == -1) break;//waited
			tr.at--;//keep doing till
			tr = tr.link;//try older
			break;
		case 126://begin count
			Looper l = new Looper(tr.reps, tr.at, len | (vol << 4));//loop back to as ++ before
			tr.reps = l;//set up a looping object
			break;
		case 127://end loop back many
			int how = (len | (vol << 4)) - 1;
			if(tr.reps == null) break;
			Looper m = tr.reps;
			for(int i = 0; i < how; i++) {
				if(m.link == null) break;
				m = m.link;
			}
			if(m == null) break;
			if(m.count-- <= 0) {//avoid annoying infinity loop and allow funny nestings
				m.count = (tr.play.charAt(m.where) >> 7) & 255;//restore for looping
				break;//done
			}
			tr.at = m.where;//loop back
			tr.reps = m;//have to unwind stack of loopers except this
			break;
		default:
			break;
		}
	}
	
	public float[] irt = new float[256];
	
	private void genTable() {
		for(int i = 0; i < 24; i++) {//2 octaves
			float freq = (float)Math.pow(2, ((float)(i - 12))/12);
			//ten octaves from 0
			for(int j = 0; j < 5; j++) {
				tune[i + j * 24] = freq;
				use[i + j * 24] = (byte)j;
				drm[i + j * 24] = (byte)(j + 9);
			}
		}
		//four special notes
		for(int i = 0; i < 8; i++) {
			tune[i + 120] = 1;
			use[i + 120] = (byte)(i + 6);
			drm[i + 120] = (byte)(i + 15);
		}
		//last four are meta notes 124, 125, 126, 127
		//gen inverse root
		for(int i = 0; i < 256; i++) {
			irt[i] = (float)(1 / Math.sqrt(i));
		}
	}
	
	public synchronized void setTick(char num) {
		if(num > 150 || num < 1) onSharedPreferenceChanged(desk.sp,
				"pref_ticks");
		ticks = 1000 / num;
		nLen = ticks / 50;
	}
	
	private boolean stress = false;  

	public synchronized char getTicks() {
		long now = System.currentTimeMillis();
		long diff = now - lastMilli;
		long many = (diff / ticks);
		lastMilli = now - (diff - many * ticks);
		stress = (many > 1);
		if(master) {
			many = (char)(-many);
			master = false;//kick back audio sync for matching
		}
		return (char)many;
	}
	
	private int pllIndex = 0;
	private long lastMilli2;
	private int syncRel = 0;
	
	private byte[] smooth = {
			//len_mul is copy of sync_over
			//pll, skip
			16, 1,//16
			1, 0,//1
			2, 3,//3
			4, 2,//4
			4, 1,//5
			8, 4,//6
			8, 3,//7
			8, 2,//8
			8, 1,//9
			16, 8,//10
			16, 7,//11
			16, 6,//12
			16, 5,//13
			16, 4,//14
			16, 3,//15
			16, 2,//16
	};
	
	private boolean master;
	
	private synchronized boolean getMTicks() { //get music ticks
		long now = System.currentTimeMillis();
		long diff = now - lastMilli2;
		long many = (diff / (ticks * 8 * pllIndex));
		lastMilli2 = now - (diff - many * ticks * 8 * pllIndex);
		//obvious twerk
		master = false;
		if(many < 1) return false;
		master = true;
		many &= 15;
		boolean play = true;
		syncRel += (pllIndex - 1) * many;//beats out
		if(!stress) {
			if(many == 1 && (syncRel & 15) == 0) pllIndex = 1;//ok
		} else {
			if(many > 1) {
				play = many == 1;
				lastMilli2 += smooth[(int)many * 2 + 1] * ticks * 8 * pllIndex;
				pllIndex *= smooth[(int)many * 2];//PLL
				syncRel -= pllIndex - 1;//the adversive resheduled note before the sync
			}
		}
		//return synced play
		return play;
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
