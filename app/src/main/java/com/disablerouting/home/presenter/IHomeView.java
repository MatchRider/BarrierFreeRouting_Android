package com.disablerouting.home.presenter;


import com.disablerouting.common.ILoader;
import com.disablerouting.curd_operations.model.ResponseListWay;

public interface IHomeView extends ILoader {

    void onListWayReceived(ResponseListWay responseWay);

    /**
     * To show relevant error to user
     * @param error Error message
     */

    void onFailure(String error);


}
