package com.leafplain.demo.locationmap;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.leafplain.demo.locationmap.interfaces.GPSTrackContract;
import com.leafplain.demo.locationmap.presenter.GPSTrackPresenter;
import com.leafplain.demo.locationmap.util.location.LocationUpdate;
import com.leafplain.demo.locationmap.util.pref.LocationPrefUtil;

/**
 * Created by kennethyeh on 2016/12/6.
 */

public class LocationMapActivity extends AppCompatActivity implements OnMapReadyCallback
        , GPSTrackContract.View
        {

    private static final String TAG     = "LocationMapActivity";
    private GoogleMap mMap;
    private Context mContext;
    public SupportMapFragment mMapFragment ;
    private Marker currentPositionMarker = null;
    private LocationPrefUtil mLocationPrefUtil= null;

    private static final float Zoom_Default             = 16f;
    private static final float Zoom_Unknow_Position     = 7f;
    private static final double Unknow_Lat              = 23.795398; // Unknow LOCATION Default Lat
    private static final double Unknow_Lng              = 121.00256; // Unknow LOCATION Default Lng

    private static final String Position_Title_Default = "目前所在位置";
    private static final String Position_Title_Unknow  = "請稍候";
    private boolean needZoomToDefaultFlag = true;

    private GPSTrackContract.Presenter mGPSTrackPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_map);
        mContext = this;
        mLocationPrefUtil = LocationPrefUtil.getInstance(mContext);
        setView();
        mGPSTrackPresenter = new GPSTrackPresenter(mContext, this);
        mGPSTrackPresenter.startLocationUpdateService();
    }

    private void setView(){
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(LocationMapActivity.this);
    }

    public void stopTrack(View v) {
        showStopDialog();
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we
     * just add a marker near Africa.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        double lat = Unknow_Lat;
        double lng = Unknow_Lng;
        float zoom = Zoom_Unknow_Position;
        String title = Position_Title_Unknow;
        String snippet = "";
        LocationUpdate.LocationInfo mLocationInfo = mLocationPrefUtil.getLatestLocationInfo();
        if(mLocationInfo != null){
            lat = mLocationInfo.lat;
            lng = mLocationInfo.lng;
            zoom = Zoom_Default;
            title = Position_Title_Default;
            snippet = ""+mLocationInfo.xLng+"_"+mLocationInfo.yLat;
            needZoomToDefaultFlag = false;
        }
        LatLng location = new LatLng(lat, lng);
        MarkerOptions markerOpt = new MarkerOptions();
        markerOpt.title(title);
        markerOpt.snippet(snippet);
        markerOpt.position(location);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoom));
        currentPositionMarker = mMap.addMarker(markerOpt);
        currentPositionMarker.showInfoWindow();
    }

    @Override
    public void onResultLocation(Object trackLocation) {
        LocationUpdate.LocationInfo mLocationInfo = (LocationUpdate.LocationInfo) trackLocation;
        try {
            if(mMap !=null && currentPositionMarker!=null){//有目前地點
                try {
                    currentPositionMarker.remove();
                    currentPositionMarker=null;
                    double lat = mLocationInfo.lat;
                    double lng = mLocationInfo.lng;
                    String title = Position_Title_Default;
                    String snippet = ""+mLocationInfo.xLng+"_"+mLocationInfo.yLat;

                    LatLng location = new LatLng(lat, lng);
                    MarkerOptions markerOpt = new MarkerOptions();
                    markerOpt.title(title);
                    markerOpt.snippet(snippet);
                    markerOpt.position(location);
//                    markerOpt.icon(BitmapDescriptorFactory.fromBitmap(positionIconBitmap));
                    currentPositionMarker = mMap.addMarker(markerOpt);
                    currentPositionMarker.showInfoWindow();
                    if(needZoomToDefaultFlag){
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, Zoom_Default));
                        needZoomToDefaultFlag = false;
                    }
                }
                catch (Exception e) {}
            }

        } catch (Exception e) {Log.w(TAG,"updateCurrentPosition Exception:"+e.toString());}
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.d(TAG, "onResume!");
        mGPSTrackPresenter.registerLocationReceiver();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        Log.d(TAG, "onPause!");
        mGPSTrackPresenter.unregisterLocationReceiver();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        showStopDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        mGPSTrackPresenter.stopLocationUpdateService();
    }

    private void showStopDialog(){
        Dialog mDialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("確定停止GPS追蹤？");
        builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                LocationMapActivity.this.finish();
            }
        });
        builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) { dialog.dismiss();
            }
        });
        mDialog = builder.create();
        mDialog.show();
    }

}
