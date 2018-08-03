package com.disablerouting.setting.presenter;

import com.disablerouting.curd_operations.model.RequestValidate;

public interface ISettingScreenPresenter {


    void onUpdate(RequestValidate requestValidate);


    /**
     * Disconnect ongoing calls on network
     */
    void disconnect();
}