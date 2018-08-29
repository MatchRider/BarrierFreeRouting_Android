package com.disablerouting.route_planner.presenter;


import android.support.annotation.NonNull;
import com.disablerouting.api.ErrorResponse;
import com.disablerouting.curd_operations.manager.GetWayManager;
import com.disablerouting.curd_operations.manager.IGetWayResponseReceiver;
import com.disablerouting.curd_operations.manager.IListGetWayResponseReceiver;
import com.disablerouting.curd_operations.manager.ListGetWayManager;
import com.disablerouting.curd_operations.model.RequestGetWay;
import com.disablerouting.curd_operations.model.ResponseListWay;
import com.disablerouting.curd_operations.model.ResponseWay;

public class RoutePlannerScreenPresenter implements IGetWayResponseReceiver , IRoutePlannerScreenPresenter
, IListGetWayResponseReceiver{

    private IRouteView mIRouteView;
    private GetWayManager mGetWayManager;
    private ListGetWayManager mListGetWayManager;

    public RoutePlannerScreenPresenter(IRouteView IRouteView, GetWayManager getWayManager, ListGetWayManager listGetWayManager) {
        mIRouteView = IRouteView;
        mGetWayManager = getWayManager;
        mListGetWayManager= listGetWayManager;
    }

    @Override
    public void onSuccessGet(ResponseWay data) {
        mIRouteView.hideLoader();
        if(mIRouteView!=null){
            mIRouteView.onWayDataReceived(data);
        }
    }

    @Override
    public void onFailureGet(@NonNull ErrorResponse errorResponse) {
        mIRouteView.hideLoader();
        if(mIRouteView!=null){
            mIRouteView.onFailure(errorResponse.getError());
        }
    }

    @Override
    public void getWays(RequestGetWay requestGetWay) {
        if(mIRouteView!=null){
            mIRouteView.showLoader();
            mGetWayManager.getWAy(this,requestGetWay);
        }

    }

    @Override
    public void getListWays() {
        if(mIRouteView!=null){
            mIRouteView.showLoader();
            mListGetWayManager.getListWay(this);
        }
    }

    @Override
    public void disconnect() {
        if (mGetWayManager != null) {
            mGetWayManager.cancel();
        }
        if (mListGetWayManager != null) {
            mListGetWayManager.cancel();
        }
    }


    @Override
    public void onSuccessGetList(ResponseListWay data) {
        mIRouteView.hideLoader();
        if(mIRouteView!=null){
           mIRouteView.onListWayReceived(data);
        }
    }

    @Override
    public void onFailureGetList(@NonNull ErrorResponse errorResponse) {
        mIRouteView.hideLoader();
        if(mIRouteView!=null){
            mIRouteView.onFailure(errorResponse.getError());
        }
    }
}
