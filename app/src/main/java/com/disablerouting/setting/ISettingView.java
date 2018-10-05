package com.disablerouting.setting;


import com.disablerouting.common.ILoader;
import com.disablerouting.curd_operations.model.ResponseListWay;
import com.disablerouting.curd_operations.model.ResponseUpdate;

public interface ISettingView extends ILoader {

    /**
     * On Data Receive Update
     * @param responseUpdate response receive
     */
    void onUpdateDataReceived(ResponseUpdate responseUpdate,String updateType);


    void onListDataSuccess(ResponseListWay listWayData);

    /**
     * To show relevant error to user
     * @param error Error message
     */

    void onFailure(String error);


    void onFailureListData(String error);



}
