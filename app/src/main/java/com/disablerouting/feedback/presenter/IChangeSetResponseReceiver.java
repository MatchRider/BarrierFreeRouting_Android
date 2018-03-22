package com.disablerouting.feedback.presenter;

import android.support.annotation.NonNull;
import com.disablerouting.api.ErrorResponse;

public interface IChangeSetResponseReceiver {

    /**
     * Call when api get success
     * @param data
     */
    void onSuccessChangeSet(String data);

    /**
     * Call when api get failure
     * @param errorResponse Server error response
     */
    void onFailureChangeSet(@NonNull ErrorResponse errorResponse);
}