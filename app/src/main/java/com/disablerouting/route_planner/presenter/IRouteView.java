package com.disablerouting.route_planner.presenter;


import com.disablerouting.common.ILoader;
import com.disablerouting.curd_operations.model.ResponseListWay;

public interface IRouteView extends ILoader {

    void onOSMDataReceived(String responseBody);

    void onListDataReceived(ResponseListWay responseListWay);

    /**
     * To show relevant error to user
     * @param error Error message
     */

    void onFailure(String error);


}
