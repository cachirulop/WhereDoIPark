package com.cachirulop.whereiparked.manager;

import com.cachirulop.whereiparked.R;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;

public class SettingsManager
{
    public static final String KEY_PREF_AUTO_SAVE_POSITION = "pref_autoSavePosition";
    public static final String KEY_PREF_BLUETOOTH_DEVICE_LIST = "pref_bluetoothDeviceList";
    public static final String KEY_PREF_MAP_FILES_PATH = "pref_mapFilesPath";
    public static final String KEY_PREF_MAP_MODE = "pref_mapMode";
    
    public enum MapModeType {
    	AUTO,
    	ONLINE,
    	OFFLINE
    }
    
    private static SharedPreferences getSharedPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(ContextManager.getContext ());
    }

    public static String getMapFilesPath () {
        return getSharedPrefs().getString(KEY_PREF_MAP_FILES_PATH, "");
    }
    
    public static MapModeType getMapMode () {
        return MapModeType.values() [getMapModeInt()];
    }
    
    public static String getMapModeDesc () {
        return ContextManager.getStringFromArray(R.array.pref_mapModeText, getMapModeInt());
    }
    
    public static int getMapModeInt ()
    {
    	String index;
    	
        index = getSharedPrefs().getString(KEY_PREF_MAP_MODE, "0");
        
        return Integer.parseInt(index);
    }
    

}
