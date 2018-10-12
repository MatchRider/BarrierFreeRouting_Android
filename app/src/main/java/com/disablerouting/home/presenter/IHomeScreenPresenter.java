package com.disablerouting.home.presenter;

public interface IHomeScreenPresenter {


    void getListWays();

    void getOSMData();

    /**
     * Disconnect ongoing calls on network
     */
    void disconnect();
}