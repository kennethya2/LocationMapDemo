package com.leafplain.demo.locationmap.util.location;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.leafplain.demo.locationmap.util.pref.LocationPrefUtil;

import java.io.Serializable;

/**
 * Created by kennethyeh on 2016/12/6.
 */

public class LocationUpdate implements GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener
        , LocationListener {

    private static final String TAG     = "LocationUpdate";
    public static final String ACTION   = "com.leafplain.demo.locationmap.util.location.LocationUpdate";

    public static final String[] LOCATION_FINE_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION
    };


    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static final long INTERVAL = 15000;

    private LocationPrefUtil mLocationPrefUtil= null;

    public LocationUpdate(Context context) {
        mContext = context;

        // Create a new global location parameters object
        mLocationRequest = LocationRequest.create();

        /*
         * Set the update interval
         */
        mLocationRequest.setInterval(INTERVAL);

        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set the interval ceiling to one minute
//        mLocationRequest.setFastestInterval(FAST_INTERVAL);

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mLocationPrefUtil = LocationPrefUtil.getInstance(mContext);
    }

    // LocationHelper called
    public void onStartFusedLocationUpdate() {
        Log.d(TAG, "onStartFusedLocationUpdate");
        /*
         * Connect the client. Don't re-start any requests here;
         * instead, wait for onResume()
         */
        mGoogleApiClient.connect();

    }

    public void onStopFusedLocationUpdate() {
        Log.d(TAG, "onStopFusedLocationUpdate");
        if (mGoogleApiClient.isConnected()) {
            stopPeriodicUpdates();
        }
        mGoogleApiClient.disconnect();
    }

    // GoogleApiClient.ConnectionCallbacks
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startPeriodicUpdates();
    }

    // GoogleApiClient.ConnectionCallbacks
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection has been suspend");
    }

    // GooglePlayServicesClient.OnConnectionFailedListener
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "GoogleApiClient connection has failed");
    }

    /**
     * trace location
     * **/
    public void startPeriodicUpdates() {
        Log.d(TAG, "startPeriodicUpdates");
        int permission = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
        Log.d(TAG, "permission.ACCESS_FINE_LOCATION:"+permission);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.i(TAG, "permissions Denied !");
            return; // return or app will FC
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }
    private void stopPeriodicUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    // LocationListener
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        Log.d(TAG, "currentLocation: "+location.getLatitude()+" "+location.getLongitude());
        Log.i(TAG, "-----");
        setFuseLocation(location);
    }
    private Location currentLocation = null;
    private synchronized void setFuseLocation(Location currentLocation){
        this.currentLocation = currentLocation;

        LocationInfo mLocationInfo = new LocationInfo();
        mLocationInfo.lat = currentLocation.getLatitude();
        mLocationInfo.lng = currentLocation.getLongitude();
        mLocationInfo.yLat = mLocationInfo.lat+"";
        mLocationInfo.xLng = mLocationInfo.lng+"";

        Intent intent = new Intent(ACTION);
        Bundle bundle = new Bundle();
        bundle.putSerializable("LocationInfo",mLocationInfo);
        intent.putExtras(bundle);
        mContext.sendBroadcast(intent);

        mLocationPrefUtil.setLatestLocationInfo(mLocationInfo);
    }
    public synchronized Location getFuseLocation(){
        return currentLocation;
    }



    public static class LocationInfo implements Serializable {
        public double lat;
        public double lng;
        public String xLng        ="";
        public String yLat        ="";
    }

}
