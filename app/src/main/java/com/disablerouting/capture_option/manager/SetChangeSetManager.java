package com.disablerouting.capture_option.manager;


import android.support.annotation.NonNull;
import com.disablerouting.api.ErrorResponseNew;
import com.disablerouting.api.ResponseCallback;
import com.disablerouting.api.ResponseWrapperOsm;
import com.disablerouting.api.RetrofitClient;
import com.disablerouting.capture_option.presenter.ISetChangeSetResponseReceiver;
import com.disablerouting.capture_option.model.RequestCreateNode;
import okhttp3.ResponseBody;
import retrofit2.Call;

import java.io.IOException;

public class SetChangeSetManager implements ResponseCallback<ResponseBody>{

    private Call<ResponseBody> mCreateChangeSet;
    private ISetChangeSetResponseReceiver mIChangeSetResponseReceiver;

    public void setChangeSet(ISetChangeSetResponseReceiver receiver, RequestCreateNode requestCreateNode) {
        this.mIChangeSetResponseReceiver = receiver;
        mCreateChangeSet = RetrofitClient.getApiServiceOsm().setChangeSet("node",requestCreateNode);
        mCreateChangeSet.enqueue(new ResponseWrapperOsm<ResponseBody>(this));
    }

    /**
     * To cancel all on going calls from network
     */
    public void cancel() {
        if (mCreateChangeSet != null && mCreateChangeSet.isExecuted()) {
            mCreateChangeSet.cancel();
        }
    }

    @Override
    public void onSuccess(@NonNull ResponseBody data) {
        if(mIChangeSetResponseReceiver!=null){
            try {
                mIChangeSetResponseReceiver.onSuccessChangeSet(data.string());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFailure(@NonNull ErrorResponseNew errorResponse) {
        if(mIChangeSetResponseReceiver!=null){
            mIChangeSetResponseReceiver.onFailureChangeSet(errorResponse);
        }
    }
}
