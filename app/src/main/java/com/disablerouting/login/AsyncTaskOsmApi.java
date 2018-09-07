package com.disablerouting.login;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import com.disablerouting.utils.Utility;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.oauth.OAuth10aService;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@SuppressLint("StaticFieldLeak")
public class AsyncTaskOsmApi extends AsyncTask<String, Void, String> {

    private Context mContext;
    private OauthData mOauthData;
    private ProgressDialog pDialog;
    private IAysncTaskOsm mIAysncTaskOsm;
    private boolean mIsForGet;
    private String API_TYPE="api_type";


    public AsyncTaskOsmApi(Context context, OauthData oauthData, IAysncTaskOsm aysncTaskOsm,
                           boolean isForGet,String api){
        mContext=context;
        mOauthData=oauthData;
        mIAysncTaskOsm=aysncTaskOsm;
        mIsForGet= isForGet;
        API_TYPE=api;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pDialog = new ProgressDialog(mContext);
        pDialog.setMessage("Please Wait...");
        pDialog.setCancelable(false);
        if(pDialog.isShowing()) {
            pDialog.dismiss();
        }else {
            pDialog.show();
        }
    }

    @Override
    protected String doInBackground(String... params) {

        final OAuth10aService service = Utility.createOauth10a();
        final OAuthRequest request = new OAuthRequest(mOauthData.getMethodType(), mOauthData.getStringUrl());
        request.addHeader("Content-Type", "application/xml;charset=UTF-8");
        request.addHeader("Accept", "application/xml;versions=1");
        if(!mOauthData.getRequestBody().isEmpty()) {
            request.setPayload(mOauthData.getRequestBody());
        }
        String[] tokens=null;
        if(UserPreferences.getInstance(mContext)!=null) {
            tokens = UserPreferences.getInstance(mContext).getAccessToken().split(",", -1);
        }
        assert tokens != null;
        OAuth1AccessToken oAuth1AccessToken = new OAuth1AccessToken(tokens[0],tokens[1]);
        service.signRequest(oAuth1AccessToken, request);
        Response response;
        try {
            response = service.execute(request);
            if(pDialog!=null){
                pDialog.dismiss();
            }
            if(response.isSuccessful()){
                if(mIsForGet){
                    mIAysncTaskOsm.onSuccessAsyncTaskForGetWay(response.getBody());
                }else {
                    mIAysncTaskOsm.onSuccessAsyncTask(response.getBody(),API_TYPE);
                }
            }
            else {
                if(response.getCode()==409){
                    mIAysncTaskOsm.onFailureAsyncTask(response.getBody());
                }else {
                    mIAysncTaskOsm.onFailureAsyncTask(response.getMessage());
                }
            }


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Executed!";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(pDialog!=null) {
            pDialog.dismiss();
        }

    }

    public void dismissDialog(){
        if(pDialog!=null){
            pDialog.dismiss();
        }
    }
}
