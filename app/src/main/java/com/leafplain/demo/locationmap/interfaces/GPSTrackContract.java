package com.leafplain.demo.locationmap.interfaces;

/**
 * Created by kennethyeh on 2017/3/22.
 */

public interface GPSTrackContract {

    interface View<T>{
        void onResultLocation(T trackLocation);
    }

    interface Presenter {
        void registerLocationReceiver();
        void unregisterLocationReceiver();
        void startLocationUpdateService();
        void stopLocationUpdateService();
    }
}
