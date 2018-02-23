package com.disablerouting.geo_coding.manager;


import android.support.annotation.NonNull;
import com.disablerouting.api.*;
import com.disablerouting.geo_coding.presenter.IGeoCodingResponseReceiver;
import com.disablerouting.geo_coding.model.GeoCodingResponse;
import retrofit2.Call;

public class GeoCodingManager implements ResponseCallback<GeoCodingResponse> {

    private Call<GeoCodingResponse> mGeoCodingResponseCall;
    private IGeoCodingResponseReceiver mIGeoCodingResponseReceiver;

    public void getGeoCoding(IGeoCodingResponseReceiver receiver, String queryString) {
        this.mIGeoCodingResponseReceiver = receiver;
        mGeoCodingResponseCall = RetrofitClient.getApiService().getGeoCoding(ApiEndPoint.API_KEY,queryString);
        mGeoCodingResponseCall.enqueue(new ResponseWrapper<GeoCodingResponse>(this));
    }

    /**
     * To cancel all on going calls from network
     */
    public void cancel() {
        if (mGeoCodingResponseCall != null && mGeoCodingResponseCall.isExecuted()) {
            mGeoCodingResponseCall.cancel();
        }
    }

    @Override
    public void onSuccess(@NonNull GeoCodingResponse data) {
        if(mIGeoCodingResponseReceiver!=null){
            mIGeoCodingResponseReceiver.onSuccessGeoCoding(data);
        }
    }

    @Override
    public void onFailure(@NonNull ErrorResponse errorResponse) {
        if(mIGeoCodingResponseReceiver!=null){
            mIGeoCodingResponseReceiver.onFailureGeoCoding(errorResponse);
        }
    }
}
