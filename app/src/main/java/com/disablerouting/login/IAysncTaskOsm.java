package com.disablerouting.login;


public interface IAysncTaskOsm {

    void  onSuccessAsyncTask(String responseBody, String API_TYPE);

    void  onFailureAsyncTask(String errorBody);

    void  onSuccessAsyncTaskForGetWay(String responseBody);

}
