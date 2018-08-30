package com.disablerouting.home.presenter;

public interface IHomeScreenPresenter {


    void getListWays();

    /**
     * Disconnect ongoing calls on network
     */
    void disconnect();
}