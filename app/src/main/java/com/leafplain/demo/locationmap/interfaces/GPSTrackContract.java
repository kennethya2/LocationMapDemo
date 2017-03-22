package com.leafplain.demo.locationmap.interfaces;

/**
 * Created by kennethyeh on 2017/3/22.
 */

public interface GPSTrackContract {

    interface View {
        void onResultLocation(Object trackLocation);
    }

    interface Presenter {
        void registerLocationReceiver();
        void unregisterLocationReceiver();
        void startLocationUpdateService();
        void stopLocationUpdateService();
    }
}
