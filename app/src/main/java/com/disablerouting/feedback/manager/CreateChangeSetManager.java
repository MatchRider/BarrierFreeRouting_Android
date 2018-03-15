package com.disablerouting.feedback.manager;


import android.support.annotation.NonNull;
import com.disablerouting.api.ErrorResponseNew;
import com.disablerouting.api.ResponseCallback;
import com.disablerouting.api.ResponseWrapperOsm;
import com.disablerouting.api.RetrofitClient;
import com.disablerouting.feedback.IChangeSetResponseReceiver;
import com.disablerouting.feedback.model.RequestCreateChangeSet;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class CreateChangeSetManager implements ResponseCallback<ResponseBody>{

    private Call<ResponseBody> mCreateChangeSet;
    private IChangeSetResponseReceiver mIChangeSetResponseReceiver;

    public void getCreateChangeSet(IChangeSetResponseReceiver receiver, RequestCreateChangeSet requestCreateChangeSet) {
        this.mIChangeSetResponseReceiver = receiver;
        /*String string="<osm>\n" +
                "   <changeset>\n" +
                "      <tag k=\"created_by\" v=\"JOSM 1.61\"/>\n" +
                "      <tag k=\"comment\" v=\"Just adding some streetnames\"/>\n" +
                "   </changeset>\n" +
                "</osm>";
        RequestBody requestBody= RequestBody.create(MediaType.parse("text/plain"),string);*/

        mCreateChangeSet = RetrofitClient.getApiServiceOsm().createChangeSet(requestCreateChangeSet);
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
            mIChangeSetResponseReceiver.onSuccessChangeSet(data);
        }
    }

    @Override
    public void onFailure(@NonNull ErrorResponseNew errorResponse) {
        if(mIChangeSetResponseReceiver!=null){
            mIChangeSetResponseReceiver.onFailureChangeSet(errorResponse);
        }
    }
}
