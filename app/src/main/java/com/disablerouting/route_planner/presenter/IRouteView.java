package com.disablerouting.route_planner.presenter;


import com.disablerouting.common.ILoader;

public interface IRouteView extends ILoader {

    void onOSMDataReceived(String responseBody);

    /**
     * To show relevant error to user
     * @param error Error message
     */

    void onFailure(String error);


}
