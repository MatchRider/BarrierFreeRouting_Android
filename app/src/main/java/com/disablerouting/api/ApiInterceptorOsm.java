package com.disablerouting.api;

import android.support.annotation.NonNull;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

class ApiInterceptorOsm implements Interceptor {

    String mStringOSMKey;

    public ApiInterceptorOsm(String osm) {
        mStringOSMKey= osm;
    }

    /**
     * Interceptor that modify/add header for outgoing request
     * @param chain Request chain
     * @return Modified header request
     * @throws IOException Throws IOException
     */
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        final Request originalRequest
                = chain.request();
        final Request requestWithNonAuthHeaders = modifyNonAuthHeaders(originalRequest);

        final Request requestWithAuthAndNonAuthHeaders = modifyAuthHeaders(requestWithNonAuthHeaders);

        return chain.proceed(requestWithAuthAndNonAuthHeaders);
    }

    /**
     * Modify header which want to authorization
     * @param request Request
     * @return Return builder
     */
    private Request modifyAuthHeaders(Request request) {
        if (request != null) {
            Request.Builder builder = request.newBuilder();
            return builder.build();
        }
        return null;
    }

    /**
     * Modify public header
     * @param request Request
     * @return Return builder
     */
    private Request modifyNonAuthHeaders(Request request) {
        if (request != null) {
            Request.Builder builder = request.newBuilder();
            builder.header(ApiEndPoint.AUTHORIZATION_TAG_OSM, mStringOSMKey);
            builder.header(ApiEndPoint.APP_CONTENT_TYPE, "text/plain");

            return builder.build();
        }
        return null;
    }
}
