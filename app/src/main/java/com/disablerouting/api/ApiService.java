package com.disablerouting.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    /***
     * Api call for Directions
     * @param api_key Insert your API Key here.
     * @param coordinates Pipe (|) separated List of longitude,latitude coordinates visited in order.
     *                    i.e 8.34234,48.23424|8.34423,48.26424
     * @param profile Specifies the route profile. Default: driving-car
     * @return Route between two or more profile selected locations
     */
    @GET("directions/")
    Call<Object> getDirections(@Query("api_key") String api_key, @Query("coordinates") String coordinates,
                               @Query("profile") String profile);

}
