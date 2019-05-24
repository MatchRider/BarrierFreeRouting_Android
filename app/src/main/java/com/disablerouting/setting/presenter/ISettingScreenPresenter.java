package com.disablerouting.setting.presenter;

import com.disablerouting.curd_operations.model.RequestNodeInfo;
import com.disablerouting.curd_operations.model.RequestWayInfo;

public interface ISettingScreenPresenter {


    /**
     * On Update Request Way
     * @param requestWayInfo request update data
     */
    void onUpdateWay(RequestWayInfo requestWayInfo, String isForWay);

    /**
     * On Update Request Node
     * @param requestNodeInfo request update data
     */
    void onUpdateNode(RequestNodeInfo requestNodeInfo,String isForNode);

    /**
     * Disconnect ongoing calls on network
     */
    void disconnect();
}