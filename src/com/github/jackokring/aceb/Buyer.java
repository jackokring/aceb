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
	
	protected ArrayList<SearchItem> list;
	
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
    
    public void list() {
    	HashMap<Machine,Bitmap> map = new HashMap<Machine,Bitmap>(); 
    	list = new ArrayList<SearchItem>();
    	final String pack = this.getClass().getPackage().getName();
    	final String[] machines = getResources().getStringArray(R.array.a);
        for(int i = 0; i < machines.length; i++) {
        	Machine res;
			try {
				res = (Machine) (Class.forName(pack + "." + machines[i]).newInstance());
				map.put(res, getIcon(res));
			} catch (Exception e) {
				i = machines.length;//break later
			}
        	SearchItem[] values = res.buyer();
        	for (int j = 0; j < values.length; ++i) {
        		/* CHECK NOT BOUGHT */
        		
        		/* OR CONSUME */
        		
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
	
	//TODO: ....
	mHelper.launchPurchaseFlow(this, SKU_GAS, 10001,   
			   mPurchaseFinishedListener, "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
	
	mHelper.queryInventoryAsync(mGotInventoryListener);
	
	mHelper.consumeAsync(inventory.getPurchase(SKU_GAS), 
			   mConsumeFinishedListener);
	
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener 
	   = new IabHelper.OnIabPurchaseFinishedListener() {
	   public void onIabPurchaseFinished(IabResult result, Purchase purchase) 
	   {
	      if (result.isFailure()) {
	         Log.d(TAG, "Error purchasing: " + result);
	         return;
	      }      
	      else if (purchase.getSku().equals(SKU_GAS)) {
	         // consume the gas and update the UI
	      }
	      else if (purchase.getSku().equals(SKU_PREMIUM)) {
	         // give user access to premium content and update the UI
	      }
	   }
	};
	
	IabHelper.QueryInventoryFinishedListener mGotInventoryListener 
	   = new IabHelper.QueryInventoryFinishedListener() {
	   public void onQueryInventoryFinished(IabResult result,
	      Inventory inventory) {

	      if (result.isFailure()) {
	        // handle error here
	      }
	      else {
	        // does the user have the premium upgrade?
	        mIsPremium = inventory.hasPurchase(SKU_PREMIUM);        
	        // update UI accordingly
	      }
	   }
	};
	
	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
			   new IabHelper.OnConsumeFinishedListener() {
			   public void onConsumeFinished(Purchase purchase, IabResult result) {
			      if (result.isSuccess()) {
			         // provision the in-app purchase to the user
			         // (for example, credit 50 gold coins to player's character)
			      }
			      else {
			         // handle error
			      }
			   }
			};
	
	@Override
	public void onDestroy() {
	   super.onDestroy();
	   if (mHelper != null) mHelper.dispose();
	   mHelper = null;
	}

}
