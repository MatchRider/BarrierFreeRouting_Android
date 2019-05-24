package com.disablerouting.route_planner.presenter;


import android.content.Context;
import android.support.annotation.NonNull;
import com.disablerouting.api.ErrorResponse;
import com.disablerouting.curd_operations.manager.IListGetWayResponseReceiver;
import com.disablerouting.curd_operations.manager.ListGetWayManager;
import com.disablerouting.curd_operations.model.ResponseListWay;
import com.disablerouting.osm_activity.manager.OSMManager;
import com.disablerouting.osm_activity.presenter.IOSMResponseReceiver;

public class RoutePlannerScreenPresenter implements IRoutePlannerScreenPresenter
{

    private IRouteView mIRouteView;
    private Context mContext;


    public RoutePlannerScreenPresenter(IRouteView IRouteView,Context context) {
        mIRouteView = IRouteView;
        mContext=context;
    }


    @Override
    public void disconnect() {

    }
}
