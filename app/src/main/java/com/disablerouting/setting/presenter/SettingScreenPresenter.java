package com.disablerouting.setting.presenter;


import android.support.annotation.NonNull;
import com.disablerouting.api.ErrorResponse;
import com.disablerouting.curd_operations.manager.*;
import com.disablerouting.curd_operations.model.*;
import com.disablerouting.setting.ISettingView;

public class SettingScreenPresenter implements IUpdateWayResponseReceiver, ISettingScreenPresenter{

    private ISettingView mISettingView;
    private UpdateWayManager mUpdateWayManager;
    private String mUpdateType;

    public SettingScreenPresenter(ISettingView iSettingView, UpdateWayManager updateWayManager ) {
        mISettingView = iSettingView;
        mUpdateWayManager = updateWayManager;

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
    public void disconnect() {
        if (mUpdateWayManager != null) {
            mUpdateWayManager.cancel();
        }

    }

    @Override
    public void onSuccessUpdate(ResponseUpdate data) {
        if(mISettingView!=null){
            mISettingView.onUpdateDataReceived(data,mUpdateType);
        }
    }

    @Override
    public void onFailureUpdate(@NonNull ErrorResponse errorResponse) {
        if(mISettingView!=null){
            mISettingView.onFailure(errorResponse.getError());
        }
    }


}
