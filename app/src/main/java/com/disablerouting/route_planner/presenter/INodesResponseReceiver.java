package com.disablerouting.route_planner.presenter;

import android.support.annotation.NonNull;
import com.disablerouting.api.ErrorResponse;
import com.disablerouting.route_planner.model.NodeResponse;

public interface INodesResponseReceiver {

    /**
     * Call when api get success
     * @param data
     */
    void onSuccessNodes(NodeResponse data);

    /**
     * Call when api get failure
     * @param errorResponse Server error response
     */
    void onFailureNodes(@NonNull ErrorResponse errorResponse);
}