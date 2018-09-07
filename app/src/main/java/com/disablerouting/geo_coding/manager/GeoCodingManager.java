package com.disablerouting.geo_coding.manager;


import android.support.annotation.NonNull;
import com.disablerouting.api.*;
import com.disablerouting.application.AppData;
import com.disablerouting.geo_coding.model.GeoCodingResponse;
import com.disablerouting.geo_coding.presenter.IGeoCodingResponseReceiver;
import retrofit2.Call;

public class GeoCodingManager implements ResponseCallback<GeoCodingResponse> {

    private Call<GeoCodingResponse> mGeoCodingResponseCall;
    private IGeoCodingResponseReceiver mIGeoCodingResponseReceiver;

    public void getGeoCoding(IGeoCodingResponseReceiver receiver, String queryString,String location,int limit) {
        this.mIGeoCodingResponseReceiver = receiver;
        mGeoCodingResponseCall = RetrofitClient.getApiService().getGeoCoding(ApiEndPoint.API_KEY,queryString,location,limit);
        mGeoCodingResponseCall.enqueue(new ResponseWrapper<GeoCodingResponse>(this));
    }

    public void getGeoCodeForward(IGeoCodingResponseReceiver receiver, String queryString) {
        this.mIGeoCodingResponseReceiver = receiver;
        if(AppData.getNewInstance()!=null && AppData.getNewInstance().getCurrentLoc()!=null) {
            double latitude= AppData.getNewInstance().getCurrentLoc().latitude; //49.4056438,8.5435314
            double longitude= AppData.getNewInstance().getCurrentLoc().longitude;
              String countryISO= "DEU"; // ALPHA 3 CODE GERMANY CODE
            //String countryISO= "IND"; // ALPHA 3 CODE India CODE
            String layers="venue,address";
            mGeoCodingResponseCall = RetrofitClient.getApiService().getGeoCodeForward(ApiEndPoint.API_KEY, queryString,
                    latitude,longitude,layers,countryISO);
        }
        mGeoCodingResponseCall.enqueue(new ResponseWrapper<GeoCodingResponse>(this));
    }

    public void getGeoCodeReverse(IGeoCodingResponseReceiver receiver, double latitude, double longitude) {
        this.mIGeoCodingResponseReceiver = receiver;
        mGeoCodingResponseCall = RetrofitClient.getApiService().getGeoCodeReverse(ApiEndPoint.API_KEY,latitude,longitude,1);
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
