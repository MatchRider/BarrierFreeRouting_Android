package com.disablerouting.route_planner.presenter;


import android.content.Context;
import android.support.annotation.NonNull;
import com.disablerouting.api.ErrorResponse;
import com.disablerouting.curd_operations.manager.IListGetWayResponseReceiver;
import com.disablerouting.curd_operations.manager.ListGetWayManager;
import com.disablerouting.curd_operations.model.ResponseListWay;
import com.disablerouting.osm_activity.manager.OSMManager;
import com.disablerouting.osm_activity.presenter.IOSMResponseReceiver;

public class RoutePlannerScreenPresenter implements IOSMResponseReceiver, IRoutePlannerScreenPresenter
        , IListGetWayResponseReceiver {

    private IRouteView mIRouteView;
    private ListGetWayManager mListGetWayManager;
    private OSMManager mOSMManager;
    private Context mContext;


    public RoutePlannerScreenPresenter(IRouteView IRouteView,OSMManager osmManager,
                                       ListGetWayManager listGetWayManager,Context context) {
        mIRouteView = IRouteView;
        mOSMManager=osmManager;
        mListGetWayManager=listGetWayManager;
        mContext=context;
    }


    @Override
    public void getOSMData() {
        if(mIRouteView!=null){
            //mIRouteView.showLoader();
            mOSMManager.getOSMData(this,mContext);
        }
    }

    @Override
    public void getListData() {
        if(mIRouteView!=null){
            mIRouteView.showLoader();
            mListGetWayManager.getListWay(this);
        }
    }


    @Override
    public void disconnect() {
        if (mOSMManager != null) {
            mOSMManager.cancel();
        }if(mListGetWayManager!=null){
            mListGetWayManager.cancel();
        }

    }

    @Override
    public void onSuccessOSM(String data) {
        if(mIRouteView!=null){
            mIRouteView.onOSMDataReceived(data);
           // mIRouteView.hideLoader();
        }

    }

    @Override
    public void onFailureOSM(@NonNull ErrorResponse errorResponse) {
        if(mIRouteView!=null){
           // mIRouteView.hideLoader();
            mIRouteView.onFailure(errorResponse.getErrorMessage());
        }
    }


    @Override
    public void onSuccessGetList(ResponseListWay responseListWay) {
        if(mIRouteView!=null){
            mIRouteView.onListDataReceived(responseListWay);
           // mIRouteView.hideLoader();
        }
    }

    @Override
    public void onFailureGetList(@NonNull ErrorResponse errorResponse) {
        if(mIRouteView!=null){
            mIRouteView.hideLoader();
            mIRouteView.onFailure(errorResponse.getErrorMessage());
        }
    }
}
