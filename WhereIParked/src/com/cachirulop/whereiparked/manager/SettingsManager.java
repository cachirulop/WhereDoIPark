package com.cachirulop.whereiparked.manager;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.cachirulop.whereiparked.activity.MainActivity;
import com.cachirulop.whereiparked.activity.SettingsActivity;

public class SettingsManager
{
    private static final String KEY_PREF_AUTO_SAVE_POSITION = "pref_autoSavePosition";
    private static final String KEY_PREF_BLUETOOTH_DEVICE_LIST = "pref_bluetoothDeviceList";
    private static final String KEY_PREF_MAP_FILES_PATH = "pref_mapFilesPath";
    
    private static SharedPreferences getSharedPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(ContextManager.getContext ());
    }

    public static String getMapFilesPath () {
        return getSharedPrefs().getString(KEY_PREF_MAP_FILES_PATH, "");
    }
}
