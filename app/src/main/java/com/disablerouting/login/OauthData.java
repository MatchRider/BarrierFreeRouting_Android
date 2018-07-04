package com.disablerouting.login;


import com.github.scribejava.core.model.Verb;

public class OauthData {

    private Verb mMethodType;
    private String mRequestBody;
    private String mStringUrl;


    public OauthData(Verb methodType, String requestBody, String url) {
        mMethodType = methodType;
        mRequestBody = requestBody;
        mStringUrl = url;
    }

    public Verb getMethodType() {
        return mMethodType;
    }

    public String getRequestBody() {
        return mRequestBody;
    }

    public String getStringUrl() {
        return mStringUrl;
    }
}
