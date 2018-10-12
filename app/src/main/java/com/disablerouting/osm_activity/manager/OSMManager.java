package com.disablerouting.osm_activity.manager;


import android.content.Context;
import android.support.annotation.NonNull;
import com.disablerouting.api.*;
import com.disablerouting.osm_activity.presenter.IOSMResponseReceiver;
import okhttp3.ResponseBody;
import retrofit2.Call;

import java.io.IOException;

public class OSMManager implements ResponseCallback<ResponseBody> {

    private Call<ResponseBody> mOsmApiCall;
    private IOSMResponseReceiver mIOSMResponseReceiver;

    public void getOSMData(IOSMResponseReceiver receiver, Context context) {
        this.mIOSMResponseReceiver = receiver;
        mOsmApiCall = RetrofitClient.getApiServiceOsm(context).downloadFileWithDynamicUrlAsync("8.662997,49.405089,8.697251,49.416175");
        if (mOsmApiCall != null)
            mOsmApiCall.enqueue(new ResponseWrapperOsm<ResponseBody>(this));

    }

    /**
     * To cancel all on going calls from network
     */
    public void cancel() {
        if (mOsmApiCall != null && mOsmApiCall.isExecuted()) {
            mOsmApiCall.cancel();
        }
    }

    @Override
    public void onSuccess(@NonNull ResponseBody data) {
        if(mIOSMResponseReceiver!=null){
            try {
                String dataXML = data.string();
                mIOSMResponseReceiver.onSuccessDirection(dataXML);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onFailure(@NonNull ErrorResponse errorResponse) {
        if (mIOSMResponseReceiver != null) {
            mIOSMResponseReceiver.onFailureDirection(errorResponse);
        }
    }

}
