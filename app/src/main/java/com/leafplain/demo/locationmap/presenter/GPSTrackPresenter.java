package com.leafplain.demo.locationmap.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.leafplain.demo.locationmap.interfaces.GPSTrackContract;
import com.leafplain.demo.locationmap.util.location.LocationUpdate;
import com.leafplain.demo.locationmap.util.location.LocationUpdateService;

/**
 * Created by kennethyeh on 2017/3/22.
 */

public class GPSTrackPresenter implements GPSTrackContract.Presenter{

    private static final String TAG     = "GPSTrackPresenter";
    private Context mContext;

    private GPSTrackContract.View mTrackView;
    private LocationReverseServiceReceiver lrsReceiver = new LocationReverseServiceReceiver();

    public GPSTrackPresenter(Context context, GPSTrackContract.View view) {
        mContext            = context;
        this.mTrackView     = view;
    }

    @Override
    public void registerLocationReceiver() {
        IntentFilter filter = new IntentFilter(LocationUpdate.ACTION);
        mContext.registerReceiver(lrsReceiver, filter);
        Log.d(TAG, "registerReceiver");
    }

    @Override
    public void unregisterLocationReceiver() {
        try {
            mContext.unregisterReceiver(lrsReceiver);// avoid memory leak
        } catch (Exception e) {
            Log.w(TAG, "unregisterLRSReceiver:"+e.toString());}
        Log.d(TAG, "unregisterReceiver");
    }

    @Override
    public void startLocationUpdateService() {
        Log.d(TAG, "startLocationUpdateService");
        Intent intent = new Intent();
        intent.setAction(LocationUpdateService.ACTION_NAME);
        intent.setPackage(mContext.getPackageName());
        mContext.startService(intent);
    }

    @Override
    public void stopLocationUpdateService() {
        Log.d(TAG, "stopLocationUpdateService");
        Intent intent = new Intent();
        intent.setAction(LocationUpdateService.ACTION_NAME);
        intent.setPackage(mContext.getPackageName());
        mContext.stopService(intent);
    }

    private class LocationReverseServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            LocationUpdate.LocationInfo locationInfoObj= (LocationUpdate.LocationInfo) intent.getSerializableExtra("LocationInfo");
            Log.d(TAG, "xLng:"+locationInfoObj.xLng);
            Log.d(TAG, "yLat:"+locationInfoObj.yLat);
            Log.i(TAG, "----------");
            mTrackView.onResultLocation(locationInfoObj);
        }
    }
}
