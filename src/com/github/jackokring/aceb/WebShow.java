package com.github.jackokring.aceb;

import android.os.Bundle;
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
public class WebShow extends Fragment {
		
	WebView e = (WebView) getActivity().findViewById(R.id.web);

    public void load(Bundle b) {
    	setString(b.getString("web"));
    }
    
    public void save(Bundle b) {
    	b.putString("web", getString());
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		container.addView(e);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

    public void setString(String s) {
        e.loadUrl(s);
    }

    public String getString() {
        return e.getUrl();
    }
}
