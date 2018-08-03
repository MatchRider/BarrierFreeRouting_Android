package com.disablerouting.setting;


import com.disablerouting.common.ILoader;
import com.disablerouting.curd_operations.model.ResponseUpdate;

public interface ISettingView extends ILoader {

    void onUpdateDataReceived(ResponseUpdate responseUpdate);

    /**
     * To show relevant error to user
     * @param error Error message
     */

    void onFailure(String error);


}
