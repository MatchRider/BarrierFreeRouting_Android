package com.disablerouting.api;

import com.disablerouting.capture_option.model.RequestCreateNode;
import com.disablerouting.curd_operations.model.RequestGetWay;
import com.disablerouting.curd_operations.model.RequestValidate;
import com.disablerouting.curd_operations.model.ResponseUpdate;
import com.disablerouting.curd_operations.model.ResponseWay;
import com.disablerouting.feedback.model.RequestCreateChangeSet;
import com.disablerouting.geo_coding.model.GeoCodingResponse;
import com.disablerouting.route_planner.model.DirectionsResponse;
import com.disablerouting.route_planner.model.NodeResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    /***
     * Api call for Directions
     * @param api_key Insert your API Key here.
     * @param coordinates Pipe (|) separated List of longitude,latitude coordinates visited in order.
     *                    i.e 8.34234,48.23424|8.34423,48.26424
     * @param profile Specifies the route profile. Default: driving-car
     * @param options Specifies the filter data
     * @return Route between two or more profile selected locations
     */
    @GET("directions")
    Call<DirectionsResponse> getDirections(@Query("api_key") String api_key, @Query("coordinates") String coordinates,
                                           @Query("profile") String profile, @Query("options") String options,
                                           @Query("elevation") boolean elevation , @Query("format") String geojson);

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
                                           @Query("profile") String profile,@Query("elevation") boolean elevation,
                                           @Query("format") String geojson);

    /**
     * Api call for getting places
     *
     * @param api_key  Insert your API Key here.
     * @param query    String to search
     * @param limit    max no of data items
     * @param location String of Default: 8.68353,49.412623 long,lat
     * @return return address of places
     */
    @GET("geocoding")
    Call<GeoCodingResponse> getGeoCoding(@Query("api_key") String api_key, @Query("query") String query,
                                         @Query("location") String location, @Query("limit") int limit);


    //Its a Forward Geo Code search
    /**
     *
     * @param api_key api key
     * @param query text string
     * @param latitude latitude
     * @param longitude longitude
     * @param layers layers.
     * @param country iso 3 of country https://en.wikipedia.org/wiki/ISO_3166-1
     * @return
     */
    @GET("geocode/search")
    Call<GeoCodingResponse> getGeoCodeForward(@Query("api_key") String api_key, @Query("text") String query,
                                              @Query("focus.point.lat") double latitude,@Query("focus.point.lon") double longitude,
                                              @Query("layers") String layers,@Query("boundary.country") String country);


    @GET("geocode/reverse")
    Call<GeoCodingResponse> getGeoCodeReverse(@Query("api_key") String api_key, @Query("point.lat") double latitude,
                                              @Query("point.lon") double longitude,@Query("size") int size);

    /**
     * APi call for get changeset id
     *
     * @param requestCreateChangeSet Request model for requesting change set id
     * @return return change set id
     */
    //@Headers({"Content-Type: application/xml; charset=utf-8"})
    @PUT("changeset/create")
    Call<ResponseBody> createChangeSet(@Body RequestCreateChangeSet requestCreateChangeSet);


    /**
     * Api call for creating node
     *
     * @param type              can be of three type (Nodes, Ways and Relations)
     * @param requestCreateNode Request model for create node
     * @return return change set id if successful
     */
    @PUT("{type}/create")
    Call<ResponseBody> setChangeSet(@Path("type") String type, @Body RequestCreateNode requestCreateNode);


    @GET("nodes")
        //@Query("wheelchair") String wheelChair not using
    Call<NodeResponse> getNodes(@Query("api_key") String apiKey, @Query("bbox") String bbox, @Query("per_page") int perPage);


    // Salil's API

    @POST("Get/")
    Call<ResponseWay> getWays(@Body RequestGetWay requestGetWay);

    @POST("Validate")
    Call<ResponseWay> validate(@Body RequestValidate requestValidate);

    @POST("update")
    Call<ResponseUpdate> update(@Body RequestValidate requestValidate);





}
