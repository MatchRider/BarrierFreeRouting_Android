package com.disablerouting.route_planner.presenter;


import android.content.Context;
import android.support.annotation.NonNull;
import com.disablerouting.api.ErrorResponse;
import com.disablerouting.osm_activity.manager.OSMManager;
import com.disablerouting.osm_activity.presenter.IOSMResponseReceiver;

public class RoutePlannerScreenPresenter implements IOSMResponseReceiver, IRoutePlannerScreenPresenter {

    private IRouteView mIRouteView;
    private OSMManager mOSMManager;
    private Context mContext;


    public RoutePlannerScreenPresenter(IRouteView IRouteView,OSMManager osmManager,Context context) {
        mIRouteView = IRouteView;
        mOSMManager=osmManager;
        mContext=context;
    }


    @Override
    public void getOSMData() {
        if(mIRouteView!=null){
            mIRouteView.showLoader();
            mOSMManager.getOSMData(this,mContext);
        }
    }

    @Override
    public void disconnect() {
        if (mOSMManager != null) {
            mOSMManager.cancel();
        }

    }

    @Override
    public void onSuccessDirection(String data) {
        if(mIRouteView!=null){
            mIRouteView.onOSMDataReceived(data);
            mIRouteView.hideLoader();
        }

    }

    @Override
    public void onFailureDirection(@NonNull ErrorResponse errorResponse) {
        if(mIRouteView!=null){
            mIRouteView.hideLoader();
            mIRouteView.onFailure(errorResponse.getErrorMessage());
        }
    }


}
