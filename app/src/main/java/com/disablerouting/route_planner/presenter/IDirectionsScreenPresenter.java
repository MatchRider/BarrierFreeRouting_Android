package com.disablerouting.route_planner.presenter;

public interface IDirectionsScreenPresenter {


    void getDestinationsData(String coordinates,String profile);

    /**
     * Disconnect ongoing calls on network
     */
    void disconnect();
}