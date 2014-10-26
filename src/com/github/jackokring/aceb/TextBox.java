package com.github.jackokring.aceb;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
import android.view.*;
import android.widget.EditText;

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

    public synchronized void load(Bundle b) {
    	e = (EditText)b.getParcelable("input");
    }
    
    public synchronized void save(Bundle b) {
    	b.putParcelable("input", (Parcelable)e);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		container.addView(e);
		return super.onCreateView(inflater, container, savedInstanceState);
	}
    
    public synchronized String enter() {
    	e.setEnabled(false);
    	SpannableStringBuilder k = (SpannableStringBuilder) e.getText();
    	String s = k.toString();
    	int i = s.indexOf(" ");
    	if(i < 0) i = s.length()-1;
    	k.delete(0, i);
    	e.setEnabled(true);
    	e.invalidate();
    	return s.substring(0, i);
    }

	public synchronized void out(String s) {
		e.setEnabled(false);
    	SpannableStringBuilder k = (SpannableStringBuilder) e.getText();
    	k.insert(0, s);
    	e.setEnabled(true);
    	e.invalidate();
	}
	
	public synchronized void postfix(String s) {
		e.setEnabled(false);
    	SpannableStringBuilder k = (SpannableStringBuilder) e.getText();
    	k.insert(k.length(), s);//place at end
    	e.setEnabled(true);
    	e.invalidate();
	}
}