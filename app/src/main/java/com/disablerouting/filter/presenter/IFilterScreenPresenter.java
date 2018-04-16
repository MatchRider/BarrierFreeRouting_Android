package com.disablerouting.filter.presenter;

public interface IFilterScreenPresenter {

    void getCoordinatesData(String query, String location, int limit);

    /**
     * Disconnect ongoing calls on network
     */
    void disconnect();
}