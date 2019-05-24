package com.disablerouting.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.api.ApiEndPoint;
import com.disablerouting.base.BaseActivityImpl;
import com.disablerouting.common.AppConstant;
import com.disablerouting.utils.Utility;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends BaseActivityImpl implements IAysncTaskOsm {

    private OAuth10aService service;
    private OAuth1RequestToken requestToken;
    private AsyncTaskOsmApi asyncTaskOsmApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_log_in)
    public void OnLoginClick() {
        service = Utility.createOauth10a();
        new fetchRequestToken().execute("");

    }


    /**
     * Api Call To Get user detail
     */
    private void callToGetUserDetails() {
        //String URLGetUserDetail = ApiEndPoint.SANDBOX_BASE_URL_OSM + "user/details";
        String URLGetUserDetail = ApiEndPoint.LIVE_BASE_URL_OSM + "user/details";
        OauthData oauthData = new OauthData(Verb.GET, "", URLGetUserDetail);
        asyncTaskOsmApi = new AsyncTaskOsmApi(this, oauthData, this, true, AppConstant.API_TYPE_GET_USER_DETAIL,true);
        asyncTaskOsmApi.execute("");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        new fetchAccessToken(intent).execute("");
    }

    public void getAccessToken(Intent intent) {
        if (intent != null && intent.getData() != null) {
            try {
                OAuth1AccessToken oAuth1AccessToken = service.getAccessToken(requestToken, intent.getData().getQueryParameter("oauth_verifier"));
                String accessToken = oAuth1AccessToken.getToken(); // Oauth Token
                String accessTokenSecret = oAuth1AccessToken.getTokenSecret(); // Oauth Token Secret
                UserPreferences.getInstance(this).saveToken(accessToken+","+accessTokenSecret);
                this.runOnUiThread(new Runnable() {
                    public void run() {
                        callToGetUserDetails();
                    }
                });
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK,returnIntent);
                finish();

            } catch (IOException | InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSuccessAsyncTask(String responseBody, String API_TYPE) {

    }

    @Override
    public void onFailureAsyncTask(final String errorBody) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(LoginActivity.this, errorBody, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSuccessAsyncTaskForGetWay(String responseBody) {
        if(responseBody!=null) {
            JSONObject jsonObject = Utility.convertXMLtoJSON(responseBody);
            try {
                JSONObject jsonObjectOSM = jsonObject.getJSONObject("osm");
                JSONObject jsonObjectUSer = jsonObjectOSM.getJSONObject("user");
                String id = jsonObjectUSer.optString("id");
                String name = jsonObjectUSer.optString("display_name");
                UserPreferences.getInstance(this).saveUSERID(name+"_"+id);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class fetchRequestToken extends AsyncTask<String, Void, String> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Please Wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                requestToken = service.getRequestToken();
                String authUrl = service.getAuthorizationUrl(requestToken);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl));
                startActivity(intent);

            } catch (IOException | InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return "Executed!";

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.dismiss();

        }
    }
    @SuppressLint("StaticFieldLeak")
    private class fetchAccessToken extends AsyncTask<String, Void, String> {

        private fetchAccessToken(Intent intent) {
            this.mIntent = intent;
        }

        ProgressDialog pDialog;
        Intent mIntent;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Please Wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            getAccessToken(mIntent);
            return "Executed!";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.dismiss();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (asyncTaskOsmApi != null) {
            asyncTaskOsmApi.dismissDialog();
        }
    }
}
