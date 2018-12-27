package com.disablerouting.application;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class DisableRouteApp extends MultiDexApplication {

    protected Context mContext = null;

   @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mContext = getApplicationContext();
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mContext = getApplicationContext();

    }

}
