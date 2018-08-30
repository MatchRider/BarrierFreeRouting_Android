package com.disablerouting.route_planner.presenter;

import com.disablerouting.curd_operations.model.RequestGetWay;

public interface IRoutePlannerScreenPresenter {


    void getWays(RequestGetWay requestGetWay);

    /**
     * Disconnect ongoing calls on network
     */
    void disconnect();
}