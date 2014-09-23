package com.github.jackokring.aceb;

import android.content.Context;
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

public class DisplayTerminal extends Fragment {

	public int x = 32;
	public int y = 32;
	int cx, cy;
	char cc;
	boolean co;
	
	Bitmap b = getNew(x, y);
	Canvas c;
	Bitmap f;
	char bg = 0x0ff0;
	Context con = getActivity().getApplicationContext();
	ImageView i;
	
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
	 
	public Bitmap getNew(int xs, int ys) {
		b = Bitmap.createBitmap(xs*8, ys*12, Bitmap.Config.ARGB_8888);
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
		i = (ImageView) getActivity().findViewById(R.id.font);
		f = ((BitmapDrawable)con.getResources().getDrawable(R.drawable.font)).getBitmap();
		container.addView(i);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	public void invalidate() {
		i.setImageBitmap(b);
		i.invalidate();
	}
	 
	public void clear(char i) {
		int a, r, g, b;
		a = ((i >> 12)&15) << 28;
		r = ((i >> 8)&15) << 20;
		g = ((i >> 4)&15) << 12;
		b = (i&15) << 4;
		bg = (char)(a + r + g + b);//also fade blending
		p = new Paint(bg);
		c.drawColor(bg);
		invalidate();
	}
	 
	public void setCell(int px, int py, char ch) {
		px %= x;
		py %= y;
		cx = px;
		cy = py;
		int xt = ch / 1024;
		if(xt != last) {//new colour
			last = xt;//cache
			setInk(xt);
		}
		ch %= 1024;
		Rect r = new Rect(px*8, py*12, px*8 + 7, py*12 + 11);
		Rect chr = new Rect((ch%32)*8, (ch/32)*12, (ch%32)*8 + 7, (ch/32)*12 + 11);
		c.drawRect(r, p);//bg
		c.drawBitmap(f, chr, r, ink);
		invalidate();
    } 
	
	public void cursor(boolean on) {
		if(on && (System.currentTimeMillis()&1024) == 0) {
			char t = cc;
			setCell(cx, cy, (char)127);
			cc = t;//restore correct
		} else if(co) {
			setCell(cx, cy, cc);
		}
		co = on;//persist
	}
	
	public void setInk(int i) {
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

}