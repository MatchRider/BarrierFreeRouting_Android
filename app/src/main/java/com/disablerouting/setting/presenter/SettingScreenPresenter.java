package com.disablerouting.setting.presenter;


import android.support.annotation.NonNull;
import com.disablerouting.api.ErrorResponse;
import com.disablerouting.curd_operations.manager.IUpdateWayResponseReceiver;
import com.disablerouting.curd_operations.manager.IValidateWayResponseReceiver;
import com.disablerouting.curd_operations.manager.UpdateWayManager;
import com.disablerouting.curd_operations.manager.ValidateWayManager;
import com.disablerouting.curd_operations.model.RequestWayInfo;
import com.disablerouting.curd_operations.model.ResponseUpdate;
import com.disablerouting.curd_operations.model.ResponseWay;
import com.disablerouting.setting.ISettingView;

public class SettingScreenPresenter implements IUpdateWayResponseReceiver, ISettingScreenPresenter , IValidateWayResponseReceiver {

    private ISettingView mISettingView;
    private UpdateWayManager mUpdateWayManager;
    private ValidateWayManager mValidateWayManager;

    public SettingScreenPresenter(ISettingView iSettingView, UpdateWayManager updateWayManager , ValidateWayManager validateWayManager) {
        mISettingView = iSettingView;
        mUpdateWayManager = updateWayManager;
        mValidateWayManager= validateWayManager;
    }

    @Override
    public void onUpdate(RequestWayInfo requestWayInfo) {
        if(mISettingView!=null){
            mISettingView.showLoader();
            mUpdateWayManager.onUpdate(this,requestWayInfo);
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
            mISettingView.onUpdateDataReceived(data);
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
            mISettingView.onValidateDataReceived(data);

        }
    }

    @Override
    public void onFailureValidate(@NonNull ErrorResponse errorResponse) {
        mISettingView.hideLoader();
        if(mISettingView!=null){
            mISettingView.onFailure(errorResponse.getError());
        }
    }
}
