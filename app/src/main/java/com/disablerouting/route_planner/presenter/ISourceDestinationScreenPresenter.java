package com.disablerouting.route_planner.presenter;

import org.json.JSONObject;

public interface ISourceDestinationScreenPresenter {

    void getDirectionsData(String coordinates, String profile , JSONObject jsonObject);

    void getCoordinatesData(String query, String location, int limit);

    void getGeoCodeDataForward(String query);

    void getGeoCodeDataReverse(double latitude, double longitude);

    void getNodesData(String bBox);


    /**
     * Disconnect ongoing calls on network
     */
    void disconnect();
}