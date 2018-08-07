package com.disablerouting.curd_operations.manager;


import android.support.annotation.NonNull;
import com.disablerouting.api.ErrorResponse;
import com.disablerouting.api.ResponseCallback;
import com.disablerouting.api.ResponseWrapper;
import com.disablerouting.api.RetrofitClient;
import com.disablerouting.curd_operations.model.RequestValidate;
import com.disablerouting.curd_operations.model.ResponseWay;
import retrofit2.Call;

public class ValidateWayManager implements ResponseCallback<ResponseWay> {

    private Call<ResponseWay> mResponseWayCall;
    private IValidateWayResponseReceiver mIValidateWayResponseReceiver;

    public void onValidate(IValidateWayResponseReceiver receiver, RequestValidate requestValidate) {
        this.mIValidateWayResponseReceiver = receiver;
        mResponseWayCall = RetrofitClient.getApiServiceCURD().validate(requestValidate);
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
        if (mIValidateWayResponseReceiver != null) {
            mIValidateWayResponseReceiver.onSuccessValidate(data);
        }
    }

    @Override
    public void onFailure(@NonNull ErrorResponse errorResponse) {
        if (mIValidateWayResponseReceiver != null) {
            mIValidateWayResponseReceiver.onFailureValidate(errorResponse);
        }
    }
}
