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
        if(AppData.getInstance()!=null && AppData.getInstance().getCurrentLoc()!=null) {
            double latitude= AppData.getInstance().getCurrentLoc().latitude;
            double longitude= AppData.getInstance().getCurrentLoc().longitude;
            String countryISO= "DEU"; // ALPHA 3 CODE GERMANY CODE
            //String countryISO= "IND"; // ALPHA 3 CODE India CODE
            String layers="venue,address";
            int radius=5;
            double static_latitude =  49.4059022; // CENTER
            double static_longitude = 8.6762875;
            mGeoCodingResponseCall = RetrofitClient.getApiService().getGeoCodeForward(ApiEndPoint.API_KEY, queryString,
                    static_latitude,static_longitude,layers,countryISO,radius);
        }
        if(mGeoCodingResponseCall!=null) {
            mGeoCodingResponseCall.enqueue(new ResponseWrapper<GeoCodingResponse>(this));
        }
    }

    public void getGeoCodeReverse(IGeoCodingResponseReceiver receiver, double latitude, double longitude) {
        this.mIGeoCodingResponseReceiver = receiver;
        mGeoCodingResponseCall = RetrofitClient.getApiService().getGeoCodeReverse(ApiEndPoint.API_KEY,latitude,longitude,1);
        if(mGeoCodingResponseCall!=null) {

            mGeoCodingResponseCall.enqueue(new ResponseWrapper<GeoCodingResponse>(this));
        }
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
