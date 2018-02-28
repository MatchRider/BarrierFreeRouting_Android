package com.disablerouting.route_planner.presenter;

public interface ISourceDestinationScreenPresenter {


    void getDestinationsData(String coordinates,String profile);

    void getCoordinatesData(String query, String location, int limit);


    /**
     * Disconnect ongoing calls on network
     */
    void disconnect();
}