package com.disablerouting.application;

import com.google.android.gms.maps.model.LatLng;

public final class AppData {

    private static AppData sInstance;
    private LatLng mCurrentLoc;


    public static AppData getInstance() {
        if (sInstance == null) {
            sInstance = new AppData();
        }
        return sInstance;
    }


    public LatLng getCurrentLoc() {
        return mCurrentLoc;
    }

    public void setCurrentLoc(LatLng currentLoc) {
        mCurrentLoc = currentLoc;
    }


}
