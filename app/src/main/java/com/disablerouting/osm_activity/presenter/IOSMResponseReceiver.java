package com.disablerouting.osm_activity.presenter;

import android.support.annotation.NonNull;
import com.disablerouting.api.ErrorResponse;

public interface IOSMResponseReceiver {

    /**
     * Call when api get success
     * @param data response type
     */
    void onSuccessOSM(String data);

    /**
     * Call when api get failure
     * @param errorResponse Server error response
     */
    void onFailureOSM(@NonNull ErrorResponse errorResponse);
}