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
import android.widget.ImageView.ScaleType;

/**
 *
 * @author jacko
 */

public class DisplayTerminal extends Fragment {

	public int x = 32;
	public int y = 32;
	Bitmap b = getNew(x, y);
	Canvas c;
	Bitmap f = ((BitmapDrawable) BitmapDrawable.createFromPath("font.png")).getBitmap();
	int bg = 0x00ffff00;
	ImageView i;
	Context con = getActivity().getApplicationContext();
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
		i = new ImageView(con);
		i.setScaleType(ScaleType.FIT_XY);
		return b;
	}
	 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		container.addView(i);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	public void invalidate() {
		i.setImageBitmap(b);
		i.invalidate();
	}
	 
	public void clear() {
		c.drawColor(bg);
		invalidate();
	}
	
	public void setBg(int b) {
		bg = b;
	}
	 
	public void setCell(int x, int y, char ch) {
		int xt = ch / 1024;
		if(xt != last) {//new colour
			last = xt;//cache
			setInk(xt);
		}
		ch %= 1024;
		Rect r = new Rect(x*8, y*12, x*8 + 8, y*12 + 12);
		Rect chr = new Rect((ch%32)*8, (ch/32)*12, (ch%32)*8 + 8, (ch/32)*12 + 12);
		c.drawRect(r, p);//bg
		c.drawBitmap(f, chr, r, ink);
		invalidate();
    } 
	
	public void setInk(int i) {
		int r, g, b;
		r = (i%16)*64;
		g = ((i%4)&3)*64;
		b = (i&3)*64;
		filt[4] = r;
		filt[9] = g;
		filt[14] = b;
		inv = new ColorMatrixColorFilter(filt);//set it
		ink.setColorFilter(inv);
	}

}
