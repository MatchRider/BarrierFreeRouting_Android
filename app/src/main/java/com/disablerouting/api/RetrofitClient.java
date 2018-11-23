package com.disablerouting.api;

import android.content.Context;
import com.disablerouting.login.UserPreferences;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

import java.util.concurrent.TimeUnit;

public class RetrofitClient {
    private static Retrofit sRetrofit;
    private static Retrofit sRetrofitOSMCheck;
    private static Retrofit sRetrofitDirections;
    private static Retrofit sRetrofitWheelChair;
    private static Retrofit sRetrofitCURD;

    private static ApiService sApiService;
    private static ApiService sApiServiceOSMCheck;
    private static ApiService sApiServiceDirections;
    private static ApiService sApiServiceWheelChair;
    private static ApiService sApiServiceCURD;



    /**
     * Initialize retrofit client with base url
     * @return Instance if retrofit
     */
    private static Retrofit getRetrofit() {
        if (sRetrofit == null) {

            final String baseUrl = ApiEndPoint.BASE_URL;
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // set your desired log level
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            final OkHttpClient client = new OkHttpClient.Builder()
                    .followRedirects(true)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(new ApiInterceptor(false))
                   // .addInterceptor(logging)
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

    private static Retrofit getRetrofitForOsmCheck(Context context) {
        if (sRetrofitOSMCheck == null) {

            final String baseUrl = ApiEndPoint.LIVE_BASE_URL_OSM;
            String osm = null;
            if(UserPreferences.getInstance(context)!=null && UserPreferences.getInstance(context).getAccessToken()!=null){
                osm= UserPreferences.getInstance(context).getAccessToken();
            }
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // set your desired log level
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);


            final OkHttpClient client = new OkHttpClient.Builder()
                    .followRedirects(true)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .addInterceptor(new ApiInterceptorOsm(osm))
                  //  .addInterceptor(logging)
                    .build();

            sRetrofitOSMCheck = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(baseUrl)
                    .addConverterFactory(SimpleXmlConverterFactory.create())
                    .build();
        }

        return sRetrofitOSMCheck;
    }
    private static Retrofit getRetrofitDirections() {
        if (sRetrofitDirections == null) {

            final String baseUrl = ApiEndPoint.BASE_URL;
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // set your desired log level
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            final OkHttpClient client = new OkHttpClient.Builder()
                    .followRedirects(true)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(new ApiInterceptor(true))
                    .addInterceptor(logging)
                    .build();

            sRetrofitDirections = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(baseUrl)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();
        }

        return sRetrofitDirections;
    }

    private static Retrofit getRetrofitForWheelChair() {
        if (sRetrofitWheelChair == null) {
            final String baseUrl = ApiEndPoint.BASE_URL_WHEEL_MAP;
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // set your desired log level
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            final OkHttpClient client = new OkHttpClient.Builder()
                    .followRedirects(true)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(new ApiInterceptor(false))
                   // .addInterceptor(logging)
                    .build();

            sRetrofitWheelChair = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(baseUrl)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();
        }

        return sRetrofitWheelChair;
    }

    private static Retrofit getRetrofitCURD() {
        if (sRetrofitCURD == null) {

            final String baseUrl = ApiEndPoint.BASE_URL_SALIL;
           /* HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // set your desired log level
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
*/
            final OkHttpClient client = new OkHttpClient.Builder()
                    .followRedirects(true)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .addInterceptor(new ApiInterceptor(false))
                  //  .addInterceptor(logging)
                    .build();

            sRetrofitCURD = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(baseUrl)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();
        }

        return sRetrofitCURD;
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

    public static ApiService getApiServiceOsmCheck(Context context) {
        if (sApiServiceOSMCheck == null) {
            sApiServiceOSMCheck = getRetrofitForOsmCheck(context).create(ApiService.class);
        }
        return sApiServiceOSMCheck;
    }

    public static ApiService getApiServiceDirections() {
        if (sApiServiceDirections == null) {
            sApiServiceDirections = getRetrofitDirections().create(ApiService.class);
        }
        return sApiServiceDirections;
    }

    public static ApiService getApiServiceWheelChair() {
        if (sApiServiceWheelChair == null) {
            sApiServiceWheelChair = getRetrofitForWheelChair().create(ApiService.class);
        }
        return sApiServiceWheelChair;
    }

    public static ApiService getApiServiceCURD() {
        if (sApiServiceCURD == null) {
            sApiServiceCURD = getRetrofitCURD().create(ApiService.class);
        }
        return sApiServiceCURD;
    }
}
