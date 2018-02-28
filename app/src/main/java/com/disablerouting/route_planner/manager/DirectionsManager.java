package com.disablerouting.route_planner.manager;


import android.support.annotation.NonNull;
import com.disablerouting.api.*;
import com.disablerouting.route_planner.model.DirectionsResponse;
import com.disablerouting.route_planner.presenter.IDirectionsResponseReceiver;
import retrofit2.Call;

public class DirectionsManager implements ResponseCallback<DirectionsResponse>{

    private Call<DirectionsResponse> mDirectionsApiCall;
    private IDirectionsResponseReceiver mIDirectionsResponseReceiver;

    public void getDestination(IDirectionsResponseReceiver receiver, String coordinates, String profileType) {
        this.mIDirectionsResponseReceiver = receiver;
        mDirectionsApiCall = RetrofitClient.getApiService().getDirections(ApiEndPoint.API_KEY,coordinates, profileType);
        mDirectionsApiCall.enqueue(new ResponseWrapper<DirectionsResponse>(this));
    }

    /**
     * To cancel all on going calls from network
     */
    public void cancel() {
        if (mDirectionsApiCall != null && mDirectionsApiCall.isExecuted()) {
            mDirectionsApiCall.cancel();
        }
    }

    @Override
    public void onSuccess(@NonNull DirectionsResponse data) {
        if(mIDirectionsResponseReceiver!=null){
            mIDirectionsResponseReceiver.onSuccessDirection(data);
        }
    }

    @Override
    public void onFailure(@NonNull ErrorResponseNew errorResponse) {
        if(mIDirectionsResponseReceiver!=null){
            mIDirectionsResponseReceiver.onFailureDirection(errorResponse);
        }
    }
}
