package com.disablerouting.setting;


import com.disablerouting.common.ILoader;
import com.disablerouting.curd_operations.model.ResponseUpdate;
import com.disablerouting.curd_operations.model.ResponseWay;

public interface ISettingView extends ILoader {

    /**
     * On Data Receive Update
     * @param responseUpdate response receive
     */
    void onUpdateDataReceived(ResponseUpdate responseUpdate);

    /**
     * On Data Receive Validate
     * @param responseUpdate response receive
     */
    void onValidateDataReceived(ResponseWay responseUpdate);

    /**
     * To show relevant error to user
     * @param error Error message
     */

    void onFailure(String error);


}
