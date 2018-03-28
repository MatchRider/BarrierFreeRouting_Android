package com.disablerouting.route_planner.manager;


import android.support.annotation.NonNull;
import com.disablerouting.api.*;
import com.disablerouting.route_planner.model.DirectionsResponse;
import com.disablerouting.route_planner.presenter.IDirectionsResponseReceiver;
import org.json.JSONObject;
import retrofit2.Call;

public class DirectionsManager implements ResponseCallback<DirectionsResponse> {

    private Call<DirectionsResponse> mDirectionsApiCall;
    private IDirectionsResponseReceiver mIDirectionsResponseReceiver;

    public void getDestination(IDirectionsResponseReceiver receiver, String coordinates, String profileType, JSONObject jsonObject) {
        this.mIDirectionsResponseReceiver = receiver;
        String jsonString = null;
        if (jsonObject != null) {
            try {
                jsonString = jsonObject.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            mDirectionsApiCall = RetrofitClient.getApiServiceDirections().getDirections(ApiEndPoint.API_KEY, coordinates, profileType, jsonString);
        } else {
            mDirectionsApiCall = RetrofitClient.getApiService().getDirections(ApiEndPoint.API_KEY, coordinates, profileType);

        }
        if (mDirectionsApiCall != null)
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
        if (mIDirectionsResponseReceiver != null) {
            mIDirectionsResponseReceiver.onSuccessDirection(data);
        }
    }

    @Override
    public void onFailure(@NonNull ErrorResponse errorResponse) {
        if (mIDirectionsResponseReceiver != null) {
            mIDirectionsResponseReceiver.onFailureDirection(errorResponse);
        }
    }
}
