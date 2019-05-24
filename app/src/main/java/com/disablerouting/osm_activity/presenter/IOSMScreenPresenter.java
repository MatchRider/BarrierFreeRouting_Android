package com.disablerouting.osm_activity.presenter;

public interface IOSMScreenPresenter {


    void getOSM();

    /**
     * Disconnect ongoing calls on network
     */
    void disconnect();
}