package com.disablerouting.capture_option.presenter;

import android.support.annotation.NonNull;
import com.disablerouting.api.ErrorResponse;

public interface ISetChangeSetResponseReceiver {

    /**
     * Call when api get success
     * @param data string
     */
    void onSuccessChangeSet(String data);

    /**
     * Call when api get failure
     * @param errorResponse Server error response
     */
    void onFailureChangeSet(@NonNull ErrorResponse errorResponse);
}