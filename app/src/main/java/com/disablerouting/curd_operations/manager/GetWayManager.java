package com.disablerouting.curd_operations.manager;


import android.support.annotation.NonNull;
import com.disablerouting.api.ErrorResponse;
import com.disablerouting.api.ResponseCallback;
import com.disablerouting.api.ResponseWrapper;
import com.disablerouting.api.RetrofitClient;
import com.disablerouting.curd_operations.model.RequestGetWay;
import com.disablerouting.curd_operations.model.ResponseWay;
import retrofit2.Call;

public class GetWayManager implements ResponseCallback<ResponseWay> {

    private Call<ResponseWay> mResponseWayCall;
    private IGetWayResponseReceiver mIGetWayResponseReceiver;

    public void getWAy(IGetWayResponseReceiver receiver, RequestGetWay requestGetWay) {
        this.mIGetWayResponseReceiver = receiver;
        mResponseWayCall = RetrofitClient.getApiServiceCURD().getWays(requestGetWay);
        mResponseWayCall.enqueue(new ResponseWrapper<ResponseWay>(this));
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
    public void onSuccess(@NonNull ResponseWay data) {
        if (mIGetWayResponseReceiver != null) {
            mIGetWayResponseReceiver.onSuccessGet(data);
        }
    }

    @Override
    public void onFailure(@NonNull ErrorResponse errorResponse) {
        if (mIGetWayResponseReceiver != null) {
            mIGetWayResponseReceiver.onFailureGet(errorResponse);
        }
    }
}
