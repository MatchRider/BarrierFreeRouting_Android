package com.disablerouting.route_planner.presenter;


import com.disablerouting.common.ILoader;
import com.disablerouting.curd_operations.model.ResponseListWay;
import com.disablerouting.curd_operations.model.ResponseWay;

public interface IRouteView extends ILoader {

    void onWayDataReceived(ResponseWay responseWay);


    void onListWayReceived(ResponseListWay responseWay);

    /**
     * To show relevant error to user
     * @param error Error message
     */

    void onFailure(String error);


}
