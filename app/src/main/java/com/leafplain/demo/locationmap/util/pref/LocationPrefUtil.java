package com.leafplain.demo.locationmap.util.pref;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.leafplain.demo.locationmap.util.location.FusedLocationUpdate;

import java.lang.reflect.Type;

/**
 * Created by kennethyeh on 2016/12/6.
 */

public class LocationPrefUtil {

    private String TAG = "LocationPrefUtil";

    private SharedPreferences prefs                     = null;
    private static String PREFS_NAME 	                = "PrefLocationInfo";
    private static final String PREFS_KEY_LOCATION      = "LocationInfo";

    private static final Type TYPE_LOCATION = new TypeToken<FusedLocationUpdate.LocationInfo>() {}.getType();

    private Context mContext = null;
    public static synchronized LocationPrefUtil getInstance(Context context){
        return new LocationPrefUtil(context);
    }

    private LocationPrefUtil(Context context){
        mContext    = context;
        prefs       = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public synchronized void setLatestLocationInfo(FusedLocationUpdate.LocationInfo locationInfo){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREFS_KEY_LOCATION, new Gson().toJson(locationInfo));
        editor.commit();
    }

    public synchronized FusedLocationUpdate.LocationInfo getLatestLocationInfo(){
        FusedLocationUpdate.LocationInfo locationInfo = new Gson().fromJson(prefs.getString(PREFS_KEY_LOCATION, null), TYPE_LOCATION);
        return locationInfo;
    }
}
