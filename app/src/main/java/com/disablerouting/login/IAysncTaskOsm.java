package com.disablerouting.login;


public interface IAysncTaskOsm {

    void  onSuccessAsyncTask(String responseBody);

    void  onFailureAsyncTask(String errorBody);
}
