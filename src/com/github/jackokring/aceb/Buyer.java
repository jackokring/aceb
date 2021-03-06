package com.github.jackokring.aceb;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.android.vending.*;
import android.app.*;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Buyer extends Activity {
	
	protected static final String TAG = "buy";
	IabHelper mHelper;
	ListView e = (ListView) this.findViewById(R.id.buyer);
	Context con = this.getApplicationContext();
	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(con);

	@Override
	public void onCreate(Bundle savedInstanceState) {
	   // ...
	   String base64EncodedPublicKey = getResources().getString(R.string.rsa_key);

	   // compute your public key and store it in base64EncodedPublicKey
	   mHelper = new IabHelper(this, base64EncodedPublicKey);
	   
	   mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
		   public void onIabSetupFinished(IabResult result) {
		      if (!result.isSuccess()) {
		         // Oh noes, there was a problem.
		         Log.d(TAG, "Problem setting up In-app Billing: " + result);
		      }
		         // Hooray, IAB is fully set up!
		   }
		});
	}
	
	protected ArrayList<BoughtItem> list;
	
	public Bitmap getIcon(Machine res) {
		try {
			InputStream i = getAssets().open(res.getClass().getSimpleName() + "/icon.png");
			Bitmap b = BitmapFactory.decodeStream(i);
			i.close();
			return b;
		} catch (IOException e) {
			return ((BitmapDrawable)getResources().getDrawable(R.drawable.ic_launcher)).getBitmap();
		}
	}
	
	private Inventory stuff;
    
    public void list() {
    	HashMap<Machine,Bitmap> map = new HashMap<Machine,Bitmap>(); 
    	list = new ArrayList<BoughtItem>();
    	final String pack = this.getClass().getPackage().getName();
    	final String[] machines = getResources().getStringArray(R.array.a);
    	mHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
    		   public void onQueryInventoryFinished(IabResult result,
    		      Inventory inventory) {
    		      if (result.isFailure()) {
    		        //TODO: handle error here
    		      } else {
    		        stuff = inventory;        
    		      }
    		   }
    		});
        for(int i = 0; i < machines.length; i++) {
        	Machine res = null;
			try {
				res = (Machine) (Class.forName(pack + "." + machines[i]).newInstance());
				map.put(res, getIcon(res));
			} catch (Exception e) {
				i = machines.length;//break later
			}
        	BoughtItem[] values = res.buyer();
        	for (int j = 0; j < values.length; ++i) {
        		/* CHECK NOT BOUGHT */
        		
        		/* OR CONSUME */
        		if(values[j].notOwned) mHelper.consumeAsync(purchase, new IabHelper.OnConsumeFinishedListener() {
        						   public void onConsumeFinished(Purchase purchase, IabResult result) {
        						      if (result.isSuccess()) {
        						         // provision the in-app purchase to the user
        						         // (for example, credit 50 gold coins to player's character)
        						      } else {
        						         // handle error
        						      }
        						   }
        						});
    			list.add(values[i]);
    			values[i].setMachine(res);
        	}
        }
        final MyArrayAdapter adapter = new MyArrayAdapter(con,
            R.layout.buy_item, list, map);
        	e.setAdapter(adapter);
        	e.setOnItemClickListener(new AdapterView.OnItemClickListener() {	
			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
			    int position, long id) {
				SearchItem si = (SearchItem)parent.getAdapter().getItem(position);
				String file = Uri.encode(si.toString());
				String mach = si.getMachine().getClass().getSimpleName();
				/* BUY!!!! */
				//TODO:
				mHelper.launchPurchaseFlow(this, SKU_GAS, 10001,   
						new IabHelper.OnIabPurchaseFinishedListener() {
						   public void onIabPurchaseFinished(IabResult result, Purchase purchase) 
						   {
						      if (result.isFailure() || !purchase.getSku().equals(SKU_GAS)) {
						         Log.d(TAG, "Error purchasing: " + result);
						         return;
						      }      
						      //TODO: purchase
						   }
						}, "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
			}
        });
    }
    
    private class MyArrayAdapter extends ArrayAdapter<BoughtItem> {
        HashMap<Machine,Bitmap> m;

        public MyArrayAdapter(Context context, int textViewResourceId,
            List<BoughtItem> objects, HashMap<Machine,Bitmap> map) {
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
	public void onDestroy() {
	   super.onDestroy();
	   if (mHelper != null) mHelper.dispose();
	   mHelper = null;
	}

}
