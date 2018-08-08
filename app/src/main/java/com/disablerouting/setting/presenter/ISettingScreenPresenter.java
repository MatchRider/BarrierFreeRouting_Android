package com.disablerouting.setting.presenter;

import com.disablerouting.curd_operations.model.RequestValidate;

public interface ISettingScreenPresenter {


    /**
     * On Update Request
     * @param requestValidate request update data
     */
    void onUpdate(RequestValidate requestValidate);

    /**
     * On validate Request
     * @param requestValidate request validate data
     */
    void onValidate(RequestValidate requestValidate);

    /**
     * Disconnect ongoing calls on network
     */
    void disconnect();
}