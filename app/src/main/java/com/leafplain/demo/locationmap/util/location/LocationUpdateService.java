package com.leafplain.demo.locationmap.util.location;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by kennethyeh on 2016/12/7.
 */

public class LocationUpdateService extends Service {
    public static final String TAG              = "LocationUpdateService";

    public static final String ACTION_NAME      = "com.leafplain.demo.locationmap.util.location.LocationUpdateService";
    private FusedLocationUpdate mFusedLocationUpdate = null;

    public void onCreate(){
        super.onCreate();
        Log.d(TAG, "onCreate");
        goTraceLocation();
    }

    /**
     * Get Current Location
     * **/
    private void goTraceLocation(){
        mFusedLocationUpdate = new FusedLocationUpdate(getApplicationContext());
        mFusedLocationUpdate.onStartFusedLocationUpdate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return START_REDELIVER_INTENT;
//        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        mFusedLocationUpdate.onStopFusedLocationUpdate();
    }
}
