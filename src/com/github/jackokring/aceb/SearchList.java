package com.github.jackokring.aceb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
	String search = "";

    public SearchList(Desktop desktop) {
		d = desktop;
	}

    public synchronized void load(Bundle b) {
    	search = b.getString("search");
    	if(d.remove == this.getId()) search(search);
    }
    
    public synchronized void save(Bundle b) {
    	b.putString("search", search);
    }
    
    protected ArrayList<SearchItem> list;
    
    public void search(String s) {
    	search = s;
    	HashMap<Machine,Bitmap> map = new HashMap<Machine,Bitmap>(); 
    	list = new ArrayList<SearchItem>();
    	final String pack = this.getClass().getPackage().getName();
    	final String[] machines = getResources().getStringArray(R.array.a);
        for(int i = 0; i < machines.length; i++) {
        	Machine res;
			try {
				res = (Machine) (Class.forName(pack + "." + machines[i]).newInstance());
				map.put(res, d.getIcon(res));
			} catch (Exception e) {
				res = d.a;
				i = machines.length;//break later
			}
        	SearchItem[] values = res.search();
        	for (int j = 0; j < values.length; ++i) {
        		int k = values[i].toString().indexOf(s);
        		if(k < 0 || k >= values[i].toString().length()) continue; 
    			list.add(values[i]);
    			values[i].setMachine(res);
        	}
        }
        final MyArrayAdapter adapter = new MyArrayAdapter(con,
            R.layout.item, list, map);
        e.setAdapter(adapter);
        e.setOnItemClickListener(new AdapterView.OnItemClickListener() {	
			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
			    int position, long id) {
				SearchItem si = (SearchItem)parent.getAdapter().getItem(position);
				String file = Uri.encode(si.toString());
				String mach = si.getMachine().getClass().getSimpleName();
				d.outURL("file:///android_asset/" + mach + "/search/" + file + ".html");
			}
        });
    }
    
    private class MyArrayAdapter extends ArrayAdapter<SearchItem> {
        HashMap<Machine,Bitmap> m;

        public MyArrayAdapter(Context context, int textViewResourceId,
            List<SearchItem> objects, HashMap<Machine,Bitmap> map) {
        	super(context, textViewResourceId, objects);
        	m = map;
        }
        
        @Override
        public View getView(int position, View v, ViewGroup parent) {
        	TextView textView = (TextView) v.findViewById(R.id.word);
        	ImageView imageView = (ImageView) v.findViewById(R.id.image);
        	textView.setText(getItem(position).toString());
        	imageView.setImageBitmap(m.get(getItem(position).getMachine()));
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