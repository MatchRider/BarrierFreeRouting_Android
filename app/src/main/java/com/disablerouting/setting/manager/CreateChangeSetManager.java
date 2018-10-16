package com.disablerouting.setting.manager;


import android.content.Context;
import android.support.annotation.NonNull;
import com.disablerouting.api.ErrorResponse;
import com.disablerouting.api.ResponseCallback;
import com.disablerouting.api.ResponseWrapperOsm;
import com.disablerouting.api.RetrofitClient;
import com.disablerouting.setting.model.RequestCreateChangeSet;
import okhttp3.ResponseBody;
import retrofit2.Call;

import java.io.IOException;

public class CreateChangeSetManager implements ResponseCallback<ResponseBody>{

    private Call<ResponseBody> mCreateChangeSet;
    private IChangeSetResponseReceiver mIChangeSetResponseReceiver;

    public void getCreateChangeSet(Context context,IChangeSetResponseReceiver receiver, RequestCreateChangeSet requestCreateChangeSet) {
        this.mIChangeSetResponseReceiver = receiver;
        /*String string="<osm>\n" +
                "   <changeset>\n" +
                "      <tag k=\"created_by\" v=\"JOSM 1.61\"/>\n" +
                "      <tag k=\"comment\" v=\"Just adding some streetnames\"/>\n" +
                "   </changeset>\n" +
                "</osm>";
        RequestBody requestBody= RequestBody.create(MediaType.parse("text/plain"),string);*/

        mCreateChangeSet = RetrofitClient.getApiServiceOsm(context).createChangeSet(requestCreateChangeSet);
        mCreateChangeSet.enqueue(new ResponseWrapperOsm<ResponseBody>(this,context));

       /* String string="<osm><changeset><tag k=\"created_by\" v=\"JOSM 1.61\"/><tag k=\"comment\" v=\"Just adding some streetnames\"/></changeset></osm>";
        OauthData oauthData= new OauthData(Verb.PUT,string,"https://master.apis.dev.openstreetmap.org/api/0.6/changeset/create");
        new AsyncTaskOsmApi(this,oauthData,this).execute("");*/
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
    public void onFailure(@NonNull ErrorResponse errorResponse) {
        if(mIChangeSetResponseReceiver!=null){
            mIChangeSetResponseReceiver.onFailureChangeSet(errorResponse);
        }
    }
}
