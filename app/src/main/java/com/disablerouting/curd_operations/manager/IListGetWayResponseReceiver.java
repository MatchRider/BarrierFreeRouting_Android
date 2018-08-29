package com.disablerouting.curd_operations.manager;


import android.support.annotation.NonNull;
import com.disablerouting.api.ErrorResponse;
import com.disablerouting.curd_operations.model.ResponseListWay;

public interface IListGetWayResponseReceiver {

    /**
     * Api to get way data
     * @param data way data
     */

    void onSuccessGetList(ResponseListWay data);

    /**
     * Call when api get failure
     * @param errorResponse Server error response
     */
    void onFailureGetList(@NonNull ErrorResponse errorResponse);
}
