package com.disablerouting.curd_operations.manager;


import android.support.annotation.NonNull;
import com.disablerouting.api.ErrorResponse;
import com.disablerouting.curd_operations.model.ResponseWay;

public interface IGetWayResponseReceiver {

    /**
     * Api to get way data
     * @param data way data
     */

    void onSuccessGet(ResponseWay data );

    /**
     * Call when api get failure
     * @param errorResponse Server error response
     */
    void onFailureGet(@NonNull ErrorResponse errorResponse);
}
