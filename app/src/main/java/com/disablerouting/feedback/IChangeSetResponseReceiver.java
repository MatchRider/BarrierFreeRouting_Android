package com.disablerouting.feedback;

import android.support.annotation.NonNull;
import com.disablerouting.api.ErrorResponseNew;
import okhttp3.ResponseBody;

public interface IChangeSetResponseReceiver {

    /**
     * Call when api get success
     * @param data
     */
    void onSuccessChangeSet(ResponseBody data);

    /**
     * Call when api get failure
     * @param errorResponse Server error response
     */
    void onFailureChangeSet(@NonNull ErrorResponseNew errorResponse);
}