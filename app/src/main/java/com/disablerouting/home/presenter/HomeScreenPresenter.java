package com.disablerouting.home.presenter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import com.disablerouting.api.ErrorResponse;
import com.disablerouting.curd_operations.manager.IListGetWayResponseReceiver;
import com.disablerouting.curd_operations.manager.ListGetWayManager;
import com.disablerouting.curd_operations.model.ResponseListWay;
import com.disablerouting.osm_activity.manager.OSMManager;
import com.disablerouting.osm_activity.presenter.IOSMResponseReceiver;

import java.util.Calendar;
import java.util.Date;

public class HomeScreenPresenter implements IHomeScreenPresenter, IListGetWayResponseReceiver,
        IOSMResponseReceiver {

    private IHomeView mIHomeView;
    private ListGetWayManager mListGetWayManager;
    private OSMManager mOSMManager;
    private Context mContext;


    public HomeScreenPresenter(IHomeView IHomeView, ListGetWayManager listGetWayManager,OSMManager osmManager,Context context) {
        mIHomeView = IHomeView;
        mListGetWayManager= listGetWayManager;
        mOSMManager= osmManager;
        mContext=context;
    }

    @Override
    public void getListWays() {
        if(mIHomeView !=null){
           // mIHomeView.showLoader();
            mListGetWayManager.getListWay(this);
        }
    }

    @Override
    public void getOSMData() {
        Date currentTime = Calendar.getInstance().getTime();
        Log.e("Time Start", String.valueOf(currentTime));
        if(mIHomeView!=null){
           // mIHomeView.showLoader();
            mOSMManager.getOSMData(this,mContext);
        }
    }

    @Override
    public void disconnect() {
        if (mListGetWayManager != null) {
            mListGetWayManager.cancel();
        }
    }


    @Override
    public void onSuccessGetList(ResponseListWay data) {
       // mIHomeView.hideLoader();
        if(mIHomeView !=null){
           mIHomeView.onListWayReceived(data);
        }
    }

    @Override
    public void onFailureGetList(@NonNull ErrorResponse errorResponse) {
       // mIHomeView.hideLoader();
        if(mIHomeView !=null){
            mIHomeView.onFailure(errorResponse.getError());
        }
    }

    @Override
    public void onSuccessOSM(String data) {
        if(mIHomeView!=null){
            mIHomeView.onOSMDataReceived(data);
           // mIHomeView.hideLoader();
        }

    }

    @Override
    public void onFailureOSM(@NonNull ErrorResponse errorResponse) {
        if(mIHomeView!=null){
          //  mIHomeView.hideLoader();
            mIHomeView.onFailure(errorResponse.getErrorMessage());
        }
    }

}
