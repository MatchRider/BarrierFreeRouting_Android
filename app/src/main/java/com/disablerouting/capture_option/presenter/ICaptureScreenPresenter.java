package com.disablerouting.capture_option.presenter;

import com.disablerouting.capture_option.model.RequestCreateNode;

public interface ICaptureScreenPresenter {

    /**
     * Api call to set change set
     * @param requestCreateNode request model of change set
     */
    void setChangeSet(RequestCreateNode requestCreateNode);

    /**
     * Disconnect ongoing calls on network
     */
    void disconnect();
}