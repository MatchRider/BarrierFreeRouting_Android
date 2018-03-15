package com.disablerouting.api;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

import java.util.concurrent.TimeUnit;

public class RetrofitClient {
    private static Retrofit sRetrofit;
    private static ApiService sApiService;


    /**
     * Initialize retrofit client with base url
     * @return Instance if retrofit
     */
    private static Retrofit getRetrofit() {
        if (sRetrofit == null) {

            final String baseUrl = ApiEndPoint.BASE_URL;

            final OkHttpClient client = new OkHttpClient.Builder()
                    .followRedirects(true)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(new ApiInterceptor())
                    .build();

            sRetrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(baseUrl)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();
        }

        return sRetrofit;
    }

    /**
     * Initialize retrofit client with base url
     * @return Instance if retrofit
     */
    private static Retrofit getRetrofitForOsm() {
        if (sRetrofit == null) {

            final String baseUrl = ApiEndPoint.SANDBOX_BASE_URL_OSM;

            final OkHttpClient client = new OkHttpClient.Builder()
                    .followRedirects(true)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(new ApiInterceptorOsm())
                    .build();

            sRetrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(baseUrl)
                    .addConverterFactory(SimpleXmlConverterFactory.create())
                    .build();
        }

        return sRetrofit;
    }

    private static Retrofit getRetrofitBaseUrl(String url){
        final OkHttpClient client = new OkHttpClient.Builder()
                .followRedirects(true)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        return new Retrofit.Builder()
                .client(client)
                .baseUrl(url)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

    /**
     * Get api retrofit object
     * @return Instance of api service
     */
    public static ApiService getApiService() {
        if (sApiService == null) {
            sApiService = getRetrofit().create(ApiService.class);
        }
        return sApiService;
    }
    /**
     * Get api retrofit object
     * @return Instance of api service
     */
    public static ApiService getApiServiceOsm() {
        if (sApiService == null) {
            sApiService = getRetrofitForOsm().create(ApiService.class);
        }
        return sApiService;
    }

}
