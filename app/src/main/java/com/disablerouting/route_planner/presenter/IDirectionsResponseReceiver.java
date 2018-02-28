package com.disablerouting.route_planner.presenter;

import android.support.annotation.NonNull;
import com.disablerouting.api.ErrorResponseNew;
import com.disablerouting.route_planner.model.DirectionsResponse;

public interface IDirectionsResponseReceiver {

    /**
     * Call when api get success
     * @param data
     */
    void onSuccessDirection(DirectionsResponse data );

    /**
     * Call when api get failure
     * @param errorResponse Server error response
     */
    void onFailureDirection(@NonNull ErrorResponseNew errorResponse);
}