package com.disablerouting.osm_activity.presenter;

import android.support.annotation.NonNull;
import com.disablerouting.api.ErrorResponse;

public interface IOSMResponseReceiver {

    /**
     * Call when api get success
     * @param data response type
     */
    void onSuccessDirection(String data);

    /**
     * Call when api get failure
     * @param errorResponse Server error response
     */
    void onFailureDirection(@NonNull ErrorResponse errorResponse);
}