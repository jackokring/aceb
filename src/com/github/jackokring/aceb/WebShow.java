package com.github.jackokring.aceb;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.*;
import android.webkit.WebView;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jacko
 */
@SuppressLint("SetJavaScriptEnabled")
public class WebShow extends Fragment {
		
	protected WebView e = (WebView) getActivity().findViewById(R.id.web);
	JavaScriptOS jsref;
	Desktop d;
	
    public void load(Bundle b) {
    	e = b.getParcelable("web");
    }
    
    public void save(Bundle b) {
    	b.putParcelable("web", (Parcelable)e);
    }
	
    protected class JavaScriptOS implements OSAdapter {
    	
    	private Desktop proxy;
    	public int xpos, ypos, playing;
    	
    	JavaScriptOS(Desktop a) {
    		proxy = a;
    	}
    	
    	private synchronized void installOSBlock() {
    		proxy.a.end();
    		proxy.js = true;
    	}
    	
    	public synchronized void release() {
    		proxy.js = false;
    		proxy.lock();
    	}
    	
    	public synchronized void machine(String input) {
    		installOSBlock();
    		proxy.outKeys(input+proxy.buf);
    		proxy.enter();
    		release();
    	}

		@Override
		public synchronized char inKey() {
			installOSBlock();
			return proxy.inKey();
		}

		@Override
		public synchronized boolean hasKey() {
			installOSBlock();
			return proxy.hasKey();
		}

		@Override
		public synchronized void outKeys(String key) {
			installOSBlock();
			proxy.outKeys(key);
		}

		@Override
		public synchronized void setChar(char x, char y, char c) {
			installOSBlock();
			proxy.setChar(x, y, c);
		}

		@Override
		public synchronized void setRes(char x, char y, char col) {
			installOSBlock();
			proxy.setRes(x, y, col);
		}

		@Override
		public synchronized void inURL(String url) {
			installOSBlock();
			proxy.inURL(url);
		}

		@Override
		public synchronized void outURL(String url) {
			installOSBlock();
			proxy.outURL(url);
		}

		@Override
		public synchronized char inJoy() {
			installOSBlock();
			return proxy.inJoy();
		}

		@Override
		public synchronized void outAudio(char x, char y, S music, boolean drums) {
			installOSBlock();
			proxy.outAudio(x, y, music, drums);
		}

		@Override
		public synchronized void scroll() {
			installOSBlock();
			proxy.scroll();
		}

		@Override
		public void setMachine(String simple) {
			//very difficult to do as async, try machine()
			throw new RuntimeException(".setMachine() Not Allowed.");
		}

		@Override
		public synchronized void send(String app, String code) {
			proxy.send(app, code);
		}

		@Override
		public synchronized void setTick(char milli) {
			installOSBlock();
			proxy.setTick(milli);
		}

		@Override
		public synchronized char getTicks() {
			installOSBlock();
			return proxy.getTicks();
		}

		@Override
		public void notify(String s) {
			installOSBlock();
			proxy.notify(s);
		}
		
		public void outAudio(char x, char y, String music, boolean drums) {
			outAudio(x, y, new S(music), drums);//for convenience
		}
    }
    
    public WebShow(Desktop os) {
    	d = os;
    	//enable js
    	e.getSettings().setJavaScriptEnabled(true);
    	e.addJavascriptInterface(jsref = new JavaScriptOS(os), "OSAdapter");
    	e.addJavascriptInterface(new S(""), "AudioBuffer");
    	e.addJavascriptInterface(new LFO("", ""), "LFO");
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		container.addView(e);
		return super.onCreateView(inflater, container, savedInstanceState);
	}
}
