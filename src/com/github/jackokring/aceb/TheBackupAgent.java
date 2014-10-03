package com.github.jackokring.aceb;

import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;
import android.app.backup.SharedPreferencesBackupHelper;

//requestRestore() for manual restore ...

 public class TheBackupAgent extends BackupAgentHelper {
	 
	 /*
     // The names of the SharedPreferences groups that the application maintains.  These
     // are the same strings that are passed to getSharedPreferences(String, int).
     static final String PREFS_DISPLAY = "displayprefs";
     static final String PREFS_SCORES = "highscores";

     // An arbitrary string used within the BackupAgentHelper implementation to
     // identify the SharedPreferencesBackupHelper's data.
     static final String MY_PREFS_BACKUP_KEY = "myprefs";
     */
     // The name of the SharedPreferences file
     static final String FILENAME = MainActivity.getMemFile();
     
     // A key to uniquely identify the set of backup data
     static final String FILES_BACKUP_KEY = "myfiles";

     // Simply allocate a helper and install it
     public void onCreate() {
         /* SharedPreferencesBackupHelper helper =
                 new SharedPreferencesBackupHelper(this, PREFS_DISPLAY, PREFS_SCORES);
         addHelper(MY_PREFS_BACKUP_KEY, helper);
	*/

         FileBackupHelper helper2 = new FileBackupHelper(this, FILENAME);
         addHelper(FILES_BACKUP_KEY, helper2);
     }
 }