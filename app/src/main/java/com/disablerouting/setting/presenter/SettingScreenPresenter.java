package com.disablerouting.setting.presenter;


import android.support.annotation.NonNull;
import com.disablerouting.api.ErrorResponse;
import com.disablerouting.curd_operations.manager.IUpdateWayResponseReceiver;
import com.disablerouting.curd_operations.manager.UpdateWayManager;
import com.disablerouting.curd_operations.model.RequestValidate;
import com.disablerouting.curd_operations.model.ResponseUpdate;
import com.disablerouting.setting.ISettingView;

public class SettingScreenPresenter implements IUpdateWayResponseReceiver, ISettingScreenPresenter {

    private ISettingView mISettingView;
    private UpdateWayManager mUpdateWayManager;

    public SettingScreenPresenter(ISettingView iSettingView, UpdateWayManager updateWayManager) {
        mISettingView = iSettingView;
        mUpdateWayManager = updateWayManager;
    }

    @Override
    public void onUpdate(RequestValidate requestValidate) {
        if(mISettingView!=null){
            mISettingView.showLoader();
            mUpdateWayManager.onUpdate(this,requestValidate);
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
        mISettingView.hideLoader();
        if(mISettingView!=null){
            mISettingView.onUpdateDataReceived(data);

        }
    }

    @Override
    public void onFailureUpdate(@NonNull ErrorResponse errorResponse) {
        mISettingView.hideLoader();
        if(mISettingView!=null){
            mISettingView.onFailure(errorResponse.getError());
        }
    }
}
