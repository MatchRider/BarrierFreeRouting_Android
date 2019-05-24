package com.disablerouting.curd_operations.manager;


import android.support.annotation.NonNull;
import com.disablerouting.api.ErrorResponse;
import com.disablerouting.curd_operations.model.ResponseWay;

public interface IValidateWayResponseReceiver {

    /**
     * Api to get way data
     * @param data way data
     */

    void onSuccessValidate(ResponseWay data);

    /**
     * Call when api get failure
     * @param errorResponse Server error response
     */
    void onFailureValidate(@NonNull ErrorResponse errorResponse);
}
