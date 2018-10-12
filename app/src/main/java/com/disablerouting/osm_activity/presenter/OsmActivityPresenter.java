package com.disablerouting.osm_activity.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import com.disablerouting.api.ErrorResponse;
import com.disablerouting.osm_activity.manager.OSMManager;

public class OsmActivityPresenter implements IOSMResponseReceiver , IOSMScreenPresenter {

    private IOSMView mIOSMView;
    private OSMManager mOSMManager;
    private Context mContext;


    public OsmActivityPresenter(IOSMView iosmView, OSMManager osmManager, Context context) {
        mIOSMView = iosmView;
        mOSMManager = osmManager;
        mContext = context;
    }


    @Override
    public void onSuccessDirection(String data) {
        if(mIOSMView!=null){
            mIOSMView.onOSMDataReceived(data);
            mIOSMView.hideLoader();
        }

    }

    @Override
    public void onFailureDirection(@NonNull ErrorResponse errorResponse) {
        if(mIOSMView!=null){
            mIOSMView.hideLoader();
            mIOSMView.onFailure(errorResponse.getErrorMessage());
        }
    }

    @Override
    public void getOSM() {
        if(mIOSMView!=null){
            mIOSMView.showLoader();
            mOSMManager.getOSMData(this,mContext);
        }
    }

    @Override
    public void disconnect() {
        if (mOSMManager != null) {
            mOSMManager.cancel();
        }
    }
}
