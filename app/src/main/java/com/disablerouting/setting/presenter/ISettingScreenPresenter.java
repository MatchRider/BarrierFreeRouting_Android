package com.disablerouting.setting.presenter;

import com.disablerouting.curd_operations.model.RequestNodeInfo;
import com.disablerouting.curd_operations.model.RequestWayInfo;

public interface ISettingScreenPresenter {


    /**
     * On Update Request Way
     * @param requestWayInfo request update data
     */
    void onUpdate(RequestWayInfo requestWayInfo);

    /**
     * On Update Request Node
     * @param requestNodeInfo request update data
     */
    void onUpdateNode(RequestNodeInfo requestNodeInfo);

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