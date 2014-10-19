package com.github.jackokring.aceb;

import java.io.File;

import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;
import android.app.backup.SharedPreferencesBackupHelper;

//requestRestore() for manual restore ...

 public class TheBackupAgent extends BackupAgentHelper {
     
     // An arbitrary string used within the BackupAgentHelper implementation to
     // identify the SharedPreferencesBackupHelper's data.
     static final String MY_PREFS_BACKUP_KEY = "myprefs";

     // Simply allocate a helper and install it
     public void onCreate() {
         SharedPreferencesBackupHelper helper =
                 new SharedPreferencesBackupHelper(this);
         addHelper(MY_PREFS_BACKUP_KEY, helper);
         FileBackupHelper helpf;
         String[] files = getResources().getStringArray(R.array.a);
         for(int i = 0; i < files.length; i++) {
        	 String name = files[i] + getResources().getString(R.string.extension);
        	 if((new File(name)).exists()) {
        		 helpf = new FileBackupHelper(this, name);
        		 addHelper(files[i], helpf);
        	 }
         }
     }
 }