package com.disablerouting.application;

import android.app.Application;
import android.content.Context;

public class DisableRouteApp extends Application {

    protected Context mContext = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

}
