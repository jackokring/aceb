package com.github.jackokring.aceb;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jacko
 */
public class SearchList extends Fragment {
		
	ListView e = (ListView) getActivity().findViewById(R.id.list);

    //TODO: persist before!!
    public synchronized void load(Bundle b) {
    	
    }
    
    public synchronized void save(Bundle b) {
    	
    }
    
    public void search(String s) {
    	
    }
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		container.addView(e);
		return super.onCreateView(inflater, container, savedInstanceState);
	}
}