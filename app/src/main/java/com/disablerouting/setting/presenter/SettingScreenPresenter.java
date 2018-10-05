package com.disablerouting.setting.presenter;


import android.support.annotation.NonNull;
import com.disablerouting.api.ErrorResponse;
import com.disablerouting.curd_operations.manager.*;
import com.disablerouting.curd_operations.model.*;
import com.disablerouting.setting.ISettingView;

public class SettingScreenPresenter implements IUpdateWayResponseReceiver, ISettingScreenPresenter ,
        IValidateWayResponseReceiver , IListGetWayResponseReceiver {

    private ISettingView mISettingView;
    private UpdateWayManager mUpdateWayManager;
    private ValidateWayManager mValidateWayManager;
    private ListGetWayManager mListGetWayManager;
    private String mUpdateType;

    public SettingScreenPresenter(ISettingView iSettingView, UpdateWayManager updateWayManager ,
                                  ValidateWayManager validateWayManager, ListGetWayManager listGetWayManager) {
        mISettingView = iSettingView;
        mUpdateWayManager = updateWayManager;
        mValidateWayManager= validateWayManager;
        mListGetWayManager= listGetWayManager;
    }

    @Override
    public void onUpdateWay(RequestWayInfo requestWayInfo,String wayUpdate) {
        if(mISettingView!=null){
            mISettingView.showLoader();
            mUpdateWayManager.onUpdate(this,requestWayInfo);
            mUpdateType=wayUpdate;
        }
    }

    @Override
    public void onUpdateNode(RequestNodeInfo requestNodeInfo,String nodeUpdate) {
        if(mISettingView!=null){
            mISettingView.showLoader();
            mUpdateWayManager.onUpdateNode(this,requestNodeInfo);
            mUpdateType=nodeUpdate;

        }
    }
    @Override
    public void onValidate(RequestWayInfo requestWayInfo) {
        if(mISettingView!=null){
            mISettingView.showLoader();
            mValidateWayManager.onValidate(this,requestWayInfo);
        }
    }

    @Override
    public void getLisData() {
        if(mISettingView !=null){
            mISettingView.showLoader();
            mListGetWayManager.getListWay(this);
        }
    }

    @Override
    public void disconnect() {
        if (mUpdateWayManager != null) {
            mUpdateWayManager.cancel();
        }
        if (mValidateWayManager != null) {
            mValidateWayManager.cancel();
        }
    }


    @Override
    public void onSuccessUpdate(ResponseUpdate data) {
        if(mISettingView!=null){
            mISettingView.onUpdateDataReceived(data,mUpdateType);
            mISettingView.hideLoader();
        }
    }

    @Override
    public void onFailureUpdate(@NonNull ErrorResponse errorResponse) {
        mISettingView.hideLoader();
        if(mISettingView!=null){
            mISettingView.onFailure(errorResponse.getError());
        }
    }

    @Override
    public void onSuccessValidate(ResponseWay data) {
        mISettingView.hideLoader();
        if(mISettingView!=null){

        }
    }

    @Override
    public void onFailureValidate(@NonNull ErrorResponse errorResponse) {
        mISettingView.hideLoader();
        if(mISettingView!=null){
            mISettingView.onFailure(errorResponse.getError());
        }
    }

    @Override
    public void onSuccessGetList(ResponseListWay data) {
        if(mISettingView!=null){
            mISettingView.onListDataSuccess(data);
            mISettingView.hideLoader();
        }
    }

    @Override
    public void onFailureGetList(@NonNull ErrorResponse errorResponse) {
        mISettingView.hideLoader();
        if(mISettingView!=null){
            mISettingView.onFailureListData(errorResponse.getError());
        }
    }
}
