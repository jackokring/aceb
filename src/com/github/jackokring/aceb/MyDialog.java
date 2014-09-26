package com.github.jackokring.aceb;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.DialogFragment;

public class MyDialog extends DialogFragment {
	
	int res;
	int help;
	
	public MyDialog(int what, int how) {
		super();
		res = what;
		help = how;
	}
	
	public void ok() {
		
	}
	
	private void proxy(boolean t) {
		if(t) ok(); else cancel();
	}
	
	public void help() {
		(new MyDialog(help, R.string.help_text_generic) {
			public void ok() {
				proxy(true);
			}
			public void cancel() {
				proxy(false);
			}
		}).show();
	}
	
	public void show() {
		show(getFragmentManager(), "Dialog");
	}
	
	public void cancel() {
		
	}
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(res)
            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       ok();
                   }
            })
            .setNeutralButton(R.string.help, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       help();
                   }
            })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                	public void onClick(DialogInterface dialog, int id) {
                		cancel();
                	}
        });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}