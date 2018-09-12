package com.disablerouting.home.presenter;


import android.support.annotation.NonNull;
import com.disablerouting.api.ErrorResponse;
import com.disablerouting.curd_operations.manager.IListGetWayResponseReceiver;
import com.disablerouting.curd_operations.manager.ListGetWayManager;
import com.disablerouting.curd_operations.model.ResponseListWay;

public class HomeScreenPresenter implements IHomeScreenPresenter, IListGetWayResponseReceiver{

    private IHomeView mIHomeView;
    private ListGetWayManager mListGetWayManager;

    public HomeScreenPresenter(IHomeView IHomeView, ListGetWayManager listGetWayManager) {
        mIHomeView = IHomeView;
        mListGetWayManager= listGetWayManager;
    }

    @Override
    public void getListWays() {
        if(mIHomeView !=null){
           // mIHomeView.showLoader();
            mListGetWayManager.getListWay(this);
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
}
