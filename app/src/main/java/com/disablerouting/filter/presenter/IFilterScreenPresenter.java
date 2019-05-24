package com.disablerouting.filter.presenter;

public interface IFilterScreenPresenter {

    void getCoordinatesData(String query, String location, int limit);

    void getGeoCodeDataForward(String query);

    void getGeoCodeDataReverse(double latitude, double longitude);



    /**
     * Disconnect ongoing calls on network
     */
    void disconnect();
}