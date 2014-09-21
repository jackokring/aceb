package com.github.jackokring.aceb;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.DialogFragment;

public class MyDialog extends DialogFragment {
	
	int res;
	
	public MyDialog(int what) {
		super();
		res = what;
	}
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(res)
            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // FIRE ZE MISSILES!
                   }
            })
            .setNeutralButton(R.string.help, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                   }
            })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
        });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}