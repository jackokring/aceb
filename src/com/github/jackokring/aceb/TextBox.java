package com.github.jackokring.aceb;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jacko
 */
public class TextBox extends Fragment {???
		
	EditText e = (EditText) getActivity().findViewById(R.id.input_area);

	public TextBox(String s) {
        super(s);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		container.addView(e);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

    public void setString(String s) {
        
    }

    public String getString() {
        return "";
    }
}
