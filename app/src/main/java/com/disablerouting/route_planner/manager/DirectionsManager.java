package com.disablerouting.route_planner.manager;


import android.support.annotation.NonNull;
import com.disablerouting.api.*;
import com.disablerouting.route_planner.model.DirectionsResponse;
import com.disablerouting.route_planner.presenter.IDirectionsResponseReceiver;
import com.disablerouting.utils.Utility;
import org.json.JSONObject;
import retrofit2.Call;

public class DirectionsManager implements ResponseCallback<DirectionsResponse> {

    private Call<DirectionsResponse> mDirectionsApiCall;
    private IDirectionsResponseReceiver mIDirectionsResponseReceiver;

    public void getDirection(IDirectionsResponseReceiver receiver, String coordinates, String profileType, JSONObject jsonObject) {
        this.mIDirectionsResponseReceiver = receiver;
        String jsonString = null;
        String geoJson = "geojson";
        String language= "";
        if(Utility.getAppLanguage().equalsIgnoreCase("English")){
            language="en";
        }else if(Utility.getAppLanguage().equalsIgnoreCase("Deutsch")) {
            language="de";
        }
        if (jsonObject != null) {
            try {
                jsonString = jsonObject.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            mDirectionsApiCall = RetrofitClient.getApiServiceDirections().getDirections(ApiEndPoint.API_KEY, coordinates, profileType, jsonString,
                    true, geoJson,language);
        } else {
            mDirectionsApiCall = RetrofitClient.getApiService().getDirections(ApiEndPoint.API_KEY, coordinates, profileType,
                    true, geoJson,language);

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
