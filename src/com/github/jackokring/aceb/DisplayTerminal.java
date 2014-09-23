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
	public int bg = 0x00ffff00;
	ImageView i;
	Context con = getActivity().getApplicationContext();
	 
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
	}
	 
	public void clear() {
		c.drawColor(bg);
		invalidate();
	}
	 
	public void setCell(int x, int y, int ch) {
		int xt = ch / 1024;
		//TODO: process high bits 
		ch %= 1024;
		Paint p = new Paint(bg);
		Rect r = new Rect(x*8, y*12, x*8 + 8, y*12 + 12);
		Rect chr = new Rect((ch%32)*8, (ch/32)*12, (ch%32)*8 + 8, (ch/32)*12 + 12);
		c.drawRect(r, p);//bg
		c.drawBitmap(f, chr, r, null);
		invalidate();
    } 

}
