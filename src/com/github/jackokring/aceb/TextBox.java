package com.github.jackokring.aceb;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.EditText;
import android.widget.TextView;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jacko
 */
public class TextBox extends Fragment {
		
	EditText e = (EditText) getActivity().findViewById(R.id.input_area);

    //TODO: persist before!!
    public synchronized void load(Bundle b) {
    	setString(b.getString("input"));
    }
    
    public synchronized void save(Bundle b) {
    	b.putString("input", getString(true));
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		container.addView(e);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

    public synchronized void setString(String s) {
    	e.setEnabled(false);
        e.setText(s, TextView.BufferType.EDITABLE);
        e.setEnabled(true);
    }

    public synchronized String getString(boolean en) {
    	e.setEnabled(false);
    	String s = e.getEditableText().toString();
        if(en) e.setEnabled(true);
        return s;
    }
    
    public synchronized String enter() {
    	String s = getString(false);
    	int i = s.indexOf(" ");
    	if(i < 0) i = s.length()-1;
    	setString(s.substring(i+1));
    	return s.substring(0, i+1);
    }
}