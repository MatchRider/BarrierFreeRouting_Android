package com.disablerouting.capture_option.presenter;

import android.content.Context;
import com.disablerouting.capture_option.model.RequestCreateNode;

public interface ICaptureScreenPresenter {

    /**
     * Api call to set change set
     * @param requestCreateNode request model of change set
     */
    void setChangeSet(RequestCreateNode requestCreateNode, Context context);

    /**
     * Disconnect ongoing calls on network
     */
    void disconnect();
}