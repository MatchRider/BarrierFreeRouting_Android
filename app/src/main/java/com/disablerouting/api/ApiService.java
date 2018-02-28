package com.disablerouting.api;

import com.disablerouting.geo_coding.model.GeoCodingResponse;
import com.disablerouting.route_planner.model.DirectionsResponse;
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
    @GET("directions")
    Call<DirectionsResponse> getDirections(@Query("api_key") String api_key, @Query("coordinates") String coordinates,
                                           @Query("profile") String profile);


    /**
     * Api call for getting places
     * @param api_key Insert your API Key here.
     * @param query String to search
     * @param limit max no of data items
     * @param location String of Default: 8.68353,49.412623 long,lat
     * @return return address of places
     */
    @GET("geocoding")
    Call<GeoCodingResponse> getGeoCoding(@Query("api_key") String api_key, @Query("query") String query,
             @Query("location") String location,@Query("limit") int limit);

}
