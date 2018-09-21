package com.disablerouting.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.base.BaseActivityImpl;
import com.disablerouting.utils.Utility;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.oauth.OAuth10aService;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends BaseActivityImpl  {

    private OAuth10aService service;
    private OAuth1RequestToken requestToken;

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

                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK,returnIntent);
                finish();

            } catch (IOException | InterruptedException | ExecutionException e) {
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
}
