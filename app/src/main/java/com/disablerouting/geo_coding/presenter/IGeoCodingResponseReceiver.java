package com.disablerouting.geo_coding.presenter;

import android.support.annotation.NonNull;
import com.disablerouting.api.ErrorResponseNew;
import com.disablerouting.geo_coding.model.GeoCodingResponse;

public interface IGeoCodingResponseReceiver {

    /**
     * Call when api get success
     * @param data
     */
    void onSuccessGeoCoding(GeoCodingResponse data);

    /**
     * Call when api get failure
     * @param errorResponse Server error response
     */
    void onFailureGeoCoding(@NonNull ErrorResponseNew errorResponse);
}