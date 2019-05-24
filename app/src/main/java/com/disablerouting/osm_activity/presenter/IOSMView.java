package com.disablerouting.osm_activity.presenter;


import com.disablerouting.common.ILoader;

public interface IOSMView extends ILoader {

    void onOSMDataReceived(String responseBody);

    /**
     * To show relevant error to user
     * @param error Error message
     */

    void onFailure(String error);


}
