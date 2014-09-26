package com.github.jackokring.aceb;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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
    public void load(Bundle b) {
    	e.setText(b.getCharSequence("input"));
    }
    
    public void save(Bundle b) {
    	b.putCharSequence("input", e.getEditableText());
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		container.addView(e);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

    public void setString(String s) {
        e.setText(s, TextView.BufferType.EDITABLE);
    }

    public String getString() {
        return e.getEditableText().toString();
    }
}
