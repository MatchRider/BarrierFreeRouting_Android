package com.disablerouting.setting.presenter;

import com.disablerouting.curd_operations.model.RequestWayInfo;

public interface ISettingScreenPresenter {


    /**
     * On Update Request
     * @param requestWayInfo request update data
     */
    void onUpdate(RequestWayInfo requestWayInfo);

    /**
     * On validate Request
     * @param requestWayInfo request validate data
     */
    void onValidate(RequestWayInfo requestWayInfo);

    /**
     * Disconnect ongoing calls on network
     */
    void disconnect();
}