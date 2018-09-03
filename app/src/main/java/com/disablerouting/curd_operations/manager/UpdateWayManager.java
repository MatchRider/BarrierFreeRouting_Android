package com.disablerouting.curd_operations.manager;


import android.support.annotation.NonNull;
import com.disablerouting.api.ErrorResponse;
import com.disablerouting.api.ResponseCallback;
import com.disablerouting.api.ResponseWrapper;
import com.disablerouting.api.RetrofitClient;
import com.disablerouting.curd_operations.model.RequestWayInfo;
import com.disablerouting.curd_operations.model.ResponseUpdate;
import retrofit2.Call;

public class UpdateWayManager implements ResponseCallback<ResponseUpdate> {

    private Call<ResponseUpdate> mResponseUpdateCall;
    private IUpdateWayResponseReceiver mUpdateWayResponseReceiver;

    public void onUpdate(IUpdateWayResponseReceiver receiver, RequestWayInfo requestWayInfo) {
        this.mUpdateWayResponseReceiver = receiver;
        mResponseUpdateCall = RetrofitClient.getApiServiceCURD().update(requestWayInfo);
        mResponseUpdateCall.enqueue(new ResponseWrapper<ResponseUpdate>(this));
    }

    /**
     * To cancel all on going calls from network
     */
    public void cancel() {
        if (mResponseUpdateCall != null && mResponseUpdateCall.isExecuted()) {
            mResponseUpdateCall.cancel();
        }
    }

    @Override
    public void onSuccess(@NonNull ResponseUpdate data) {
        if (mUpdateWayResponseReceiver != null) {
            mUpdateWayResponseReceiver.onSuccessUpdate(data);
        }
    }

    @Override
    public void onFailure(@NonNull ErrorResponse errorResponse) {
        if (mUpdateWayResponseReceiver != null) {
            mUpdateWayResponseReceiver.onFailureUpdate(errorResponse);
        }
    }
}
