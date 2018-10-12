package com.disablerouting.route_planner.presenter;

public interface IRoutePlannerScreenPresenter {


    void getOSMData();
    /**
     * Disconnect ongoing calls on network
     */
    void disconnect();
}