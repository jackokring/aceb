package com.github.jackokring.aceb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
	Context con = getActivity().getApplicationContext();
	Desktop d;//for getting URL

    public SearchList(Desktop desktop) {
		d = desktop;
	}

	//TODO: persist before!!
    public synchronized void load(Bundle b) {
    	
    }
    
    public synchronized void save(Bundle b) {
    	
    }
    
    protected ArrayList<SearchItem> list = new ArrayList<SearchItem>();
    
    public void search(String s) {
    	//TODO: load list
    	for (int i = 0; i < values.length; ++i) {
    		list.add(values[i]);
        }
        final MyArrayAdapter adapter = new MyArrayAdapter(con,
            R.layout.item, list);
        e.setAdapter(adapter);
        e.setOnItemClickListener(new AdapterView.OnItemClickListener() {	
		@Override
		public void onItemClick(AdapterView<?> parent, final View view,
		    int position, long id) {
			//TODO: show stuff
			final SearchItem item = (SearchItem) parent.getItemAtPosition(position);
		    view.animate().setDuration(2000).alpha(0)
		    	.withEndAction(new Runnable() {
		        @Override
		        public void run() {
		        	list.remove(item);
		        	adapter.notifyDataSetChanged();
		        	view.setAlpha(1);
		        }
		    });
		}
        });
    }
    
    private class MyArrayAdapter extends ArrayAdapter<SearchItem> {

        HashMap<SearchItem, Integer> mIdMap = new HashMap<SearchItem, Integer>();
        Context c;

        public MyArrayAdapter(Context context, int textViewResourceId,
            List<SearchItem> objects) {
        	super(context, textViewResourceId, objects);
        	for (int i = 0; i < objects.size(); ++i) {
        		mIdMap.put(objects.get(i), i);
        	}
        	c = context;
        }

        @Override
        public long getItemId(int position) {
        	SearchItem item = getItem(position);
        	return mIdMap.get(item);
        }
        
        @Override
        public View getView(int position, View v, ViewGroup parent) {
        	LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	TextView textView = (TextView) v.findViewById(R.id.label);
        	ImageView imageView = (ImageView) v.findViewById(R.id.icon);
        	textView.setText(getItem(position).toString());
        	// TODO: adapt view v

        	return v;
        }
    }
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
		container.addView(e);
		return super.onCreateView(inflater, container, savedInstanceState);
	}
}