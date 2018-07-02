package com.disablerouting.login;


import com.disablerouting.api.ApiEndPoint;
import com.github.scribejava.core.builder.api.DefaultApi10a;

public class OSMApi extends DefaultApi10a {

    protected OSMApi() {
    }

    private static class InstanceHolder {
        private static final OSMApi INSTANCE = new OSMApi();
    }

    public static OSMApi instance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public String getRequestTokenEndpoint() {
        return ApiEndPoint.BASE_URL_OAUTH+ApiEndPoint.requestTokenUrl;
    }

    @Override
    public String getAccessTokenEndpoint() {
        return ApiEndPoint.BASE_URL_OAUTH+ApiEndPoint.accessTokenUrl;
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return ApiEndPoint.BASE_URL_OAUTH+ApiEndPoint.authorizeUrl;
    }
}
