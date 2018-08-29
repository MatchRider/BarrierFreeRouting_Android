package com.disablerouting.curd_operations.manager;


import android.support.annotation.NonNull;
import com.disablerouting.api.ErrorResponse;
import com.disablerouting.api.ResponseCallback;
import com.disablerouting.api.ResponseWrapper;
import com.disablerouting.api.RetrofitClient;
import com.disablerouting.curd_operations.model.ResponseListWay;
import retrofit2.Call;

public class ListGetWayManager implements ResponseCallback<ResponseListWay> {

    private Call<ResponseListWay> mResponseWayCall;
    private IListGetWayResponseReceiver mIListGetWayResponseReceiver;

    public void getListWay(IListGetWayResponseReceiver receiver) {
        this.mIListGetWayResponseReceiver = receiver;
        mResponseWayCall = RetrofitClient.getApiServiceCURD().getList();
        mResponseWayCall.enqueue(new ResponseWrapper<ResponseListWay>(this));
    }

    /**
     * To cancel all on going calls from network
     */
    public void cancel() {
        if (mResponseWayCall != null && mResponseWayCall.isExecuted()) {
            mResponseWayCall.cancel();
        }
    }

    @Override
    public void onSuccess(@NonNull ResponseListWay data) {
        if (mIListGetWayResponseReceiver != null) {
            mIListGetWayResponseReceiver.onSuccessGetList(data);
        }
    }

    @Override
    public void onFailure(@NonNull ErrorResponse errorResponse) {
        if (mIListGetWayResponseReceiver != null) {
            mIListGetWayResponseReceiver.onFailureGetList(errorResponse);
        }
    }
}
