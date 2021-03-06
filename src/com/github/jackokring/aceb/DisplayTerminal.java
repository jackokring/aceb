package com.github.jackokring.aceb;

import android.content.*;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 *
 * @author jacko
 */

public class DisplayTerminal extends Fragment implements OnSharedPreferenceChangeListener {

	char x;
	char y;
	char cx, cy;
	char cc;
	boolean co;
	
	Bitmap b;
	Canvas c;
	Bitmap f;
	char bg = 0x0ff0;
	ImageView i = (ImageView) getActivity().findViewById(R.id.font);
	Desktop desk;
	
	public DisplayTerminal(Desktop d) {
		super();
		desk = d;
	}
	
	/* done in desktop
	public void finalize() {
		sp.unregisterOnSharedPreferenceChangeListener(this);
	} */
	
	Paint p = new Paint(bg);
	Paint ink = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
	float[] filt = {
			-1, 0, 0, 0, 256,//R
			0, -1, 0, 0, 256,//G
			0, 0, -1, 0, 256,//B
			0, 0, 0, 1, 0//A
			//R, G, B, A, O
	};
	ColorFilter inv = new ColorMatrixColorFilter(filt);
	int last = 63;//white from inverse
	
    public synchronized void load(Bundle bu) {
    	x = (char)bu.getInt("x");
    	y = (char)bu.getInt("y");
    	cx = (char)bu.getInt("cx");
    	cy = (char)bu.getInt("cy");
    	cc = (char)bu.getInt("cc");
    	co = bu.getBoolean("co");
    	b = bu.getParcelable("b");
    	bg = (char)bu.getInt("bg");
    	filt = bu.getFloatArray("filt");
    	last = bu.getInt("last");
    	//restore
    	inv = new ColorMatrixColorFilter(filt);//set it
		ink.setColorFilter(inv);
		p = new Paint(bg);
		i.setImageBitmap(b);
		invalidate();
    }
    
    public synchronized void save(Bundle bu) {
    	bu.putInt("x", x);
    	bu.putInt("y", y);
    	bu.putInt("cx", cx);
    	bu.putInt("cy", cy);
    	bu.putInt("cc", cc);
    	bu.putBoolean("co", co);
    	bu.putParcelable("b", b);
    	bu.putInt("bg", bg);
    	bu.putFloatArray("filt", filt);
    	bu.putInt("last", last);
    }
	 
	public synchronized Bitmap getNew(char xs, char ys) {
		if(xs < 16) xs = 16;
		if(ys < 16) ys = 16;//smallest
		b = Bitmap.createBitmap(xs*8, ys*8, Bitmap.Config.ARGB_8888);
		x = xs;
		y = ys;
		c = new Canvas(b);
		char t = bg;
		clear((char)(bg&0x0fff));//no alpha on clear
		bg = t;//restore fade
		return b;
	}
	 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		f = ((BitmapDrawable)desk.getResources().getDrawable(R.drawable.font)).getBitmap();
		container.addView(i);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	public synchronized void invalidate() {
		//i.setImageBitmap(b);
		i.invalidate();
	}
	 
	public synchronized void clear(char i) {
		int a, r, g, b;
		a = ((i >> 12)&15) << 28;
		r = ((i >> 8)&15) << 20;
		g = ((i >> 4)&15) << 12;
		b = (i&15) << 4;
		bg = (char)(a + r + g + b);//also fade blending
		p = new Paint(bg);
		c.drawColor(bg);
		cx = cy = 0;
		invalidate();
	}
	
	public synchronized void setCell(char ch) {
		int xt = ch / 1024;
		if(xt != last) {//new colour
			last = xt;//cache
			setInk(xt);
		}
		ch %= 1024;
		Rect r = new Rect(cx*8, cy*8, cx*8 + 7, cy*8 + 7);
		Rect chr = new Rect((ch%32)*8, (ch/32)*8, (ch%32)*8 + 7, (ch/32)*8 + 7);
		c.drawRect(r, p);//bg
		c.drawBitmap(f, chr, r, ink);
		invalidate();
	}
	 
	public synchronized void setCell(char px, char py, char ch) {
		px %= x;
		py %= y;
		cx = px;
		cy = py;
		cursor(false);//until input
		setCell(ch);
    } 
	
	public synchronized void cursor(boolean on) {
		if(on && (System.currentTimeMillis()&1024) == 0) {
			char t = cc;
			setCell((char)127);
			cc = t;//restore correct
		} else if(co) {
			setCell(cc);
		}
		co = on;//persist
	}
	
	public synchronized void scroll() {
		cursor(false);//before
		Paint pt = new Paint(bg&0xffffff);//no blend
		for(int i = 1; i < y; i++) {
			Rect r = new Rect(0, i * 8 - 8, x * 8 - 1, i * 8 - 1);
			//c.drawRect(r, pt); -- no blends
			c.drawBitmap(b, new Rect(0, i * 8, x * 8 - 1, i * 8 + 7), r, null);
		}
		c.drawRect(new Rect(0, y * 8, x * 8 - 1, y * 8 + 7), pt);
		invalidate();
	}
	
	public synchronized void setInk(int i) {
		int r, g, b;
		r = (i/16)*64;
		g = ((i/4)&3)*64;
		b = (i&3)*64;
		filt[4] = r;
		filt[9] = g;
		filt[14] = b;
		inv = new ColorMatrixColorFilter(filt);//set it
		ink.setColorFilter(inv);
	}
	
	public synchronized void setRes(char x, char y, char col) {
		getNew(x, y);
		i.setImageBitmap(b);
		clear(col);
		desk.resX(x);
		desk.resY(y);
	}

	@Override
	public synchronized void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if(key.equals("pref_screen")) {
			x = y = (char)sharedPreferences.getInt("pref_screen", 32);
			setRes(x, y, bg);
		}
	}
}