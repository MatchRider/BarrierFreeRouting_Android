package com.disablerouting.route_planner.manager;


import android.support.annotation.NonNull;
import com.disablerouting.api.*;
import com.disablerouting.route_planner.model.NodeResponse;
import com.disablerouting.route_planner.presenter.INodesResponseReceiver;
import retrofit2.Call;

public class NodeManager implements ResponseCallback<NodeResponse> {

    private Call<NodeResponse> mNodeApiCall;
    private INodesResponseReceiver mINodesResponseReceiver;

    public void getNodes(INodesResponseReceiver receiver, String bBox) {
        this.mINodesResponseReceiver = receiver;
        mNodeApiCall = RetrofitClient.getApiServiceWheelChair().getNodes(ApiEndPoint.API_KEY_WHEEL_MAP, bBox, 1000,"yes");
        mNodeApiCall.enqueue(new ResponseWrapper<NodeResponse>(this));
    }

    /**
     * To cancel all on going calls from network
     */
    public void cancel() {
        if (mNodeApiCall != null && mNodeApiCall.isExecuted()) {
            mNodeApiCall.cancel();
        }
    }

    @Override
    public void onSuccess(@NonNull NodeResponse data) {
        if (mINodesResponseReceiver != null) {
            mINodesResponseReceiver.onSuccessNodes(data);
        }
    }

    @Override
    public void onFailure(@NonNull ErrorResponse errorResponse) {
        if (mINodesResponseReceiver != null) {
            mINodesResponseReceiver.onFailureNodes(errorResponse);
        }
    }
}
