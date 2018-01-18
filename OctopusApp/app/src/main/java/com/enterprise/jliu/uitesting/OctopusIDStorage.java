package com.enterprise.jliu.uitesting;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by JenniferLiu on 3/12/2017.
 */

public class OctopusIDStorage {
    private static final String PREF_OCTOPUS_ID = "Octopus_Id";
    private static final String DEFAULT_OCTOPUS_ID = "000000000";
    private static final String TAG = "OctopusIDStorage";
    private static String sOctopus = null;
    private static final Object sOctopusLock = new Object();

    public static void SetOctopus(Context c, String s){
        synchronized (sOctopusLock){
            Log.i(TAG, "Setting Octopus: " + s);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
            prefs.edit().putString(PREF_OCTOPUS_ID, s);
            sOctopus = s;
        }
    }

    public static String GetAccount(Context c){
        synchronized (sOctopusLock){
            if (sOctopus == null){
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
                String octopus = prefs.getString(PREF_OCTOPUS_ID, DEFAULT_OCTOPUS_ID);
                sOctopus = octopus;
            }
            return sOctopus;
        }
    }

}
