package com.ftech.journalapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
 
    //mode
    int PRIVATE_MODE = 0;
 
    // file name
    private static final String PREF_NAME = "INTRO_DEMO";
    private static final String IS_FIRST_TIME = "IsFirstTime";
 
    public Prefs(Context context,int mode) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, mode);
        editor = pref.edit();
    }
 
    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME, isFirstTime);
        editor.commit();
    }
 
    public boolean isNotFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME, false);
    }
 
}