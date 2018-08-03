package com.disablerouting.curd_operations.manager;


import android.support.annotation.NonNull;
import com.disablerouting.api.ErrorResponse;
import com.disablerouting.curd_operations.model.ResponseUpdate;

public interface IUpdateWayResponseReceiver {

    /**
     * Api to get way data
     * @param data way data
     */

    void onSuccessUpdate(ResponseUpdate data);

    /**
     * Call when api get failure
     * @param errorResponse Server error response
     */
    void onFailureUpdate(@NonNull ErrorResponse errorResponse);
}
