package com.disablerouting.api;

import com.disablerouting.curd_operations.model.*;
import com.disablerouting.geo_coding.model.GeoCodingResponse;
import com.disablerouting.route_planner.model.DirectionsResponse;
import com.disablerouting.route_planner.model.NodeResponse;
import com.disablerouting.setting.model.RequestCreateChangeSet;
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
                                           @Query("elevation") boolean elevation , @Query("format") String geojson,
                                           @Query("language") String language);

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
                                           @Query("format") String geojson,@Query("language") String language);

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
     * @return address
     */
    @GET("geocode/search")
    Call<GeoCodingResponse> getGeoCodeForward(@Query("api_key") String api_key, @Query("text") String query,
                                              @Query("boundary.circle.lat") double latitude,@Query("boundary.circle.lon") double longitude,
                                              @Query("layers") String layers,@Query("boundary.country") String country,
                                              @Query("boundary.circle.radius") int radius);

    //Its a Reverse Geo Code search for gtting address of lat and lon
    /**
     *
     * @param api_key api key passed
     * @param latitude longitude
     * @param longitude latitude
     * @param size max size data
     * @return address string
     */
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
     * Api for  plotting nodes over map path
     * @param apiKey api key passed
     * @param bbox bounding box over a lat and lon
     * @param perPage per page result
     * @param wheelchair wheelchair  related data
     * @return nodes
     */
    @GET("nodes")
    Call<NodeResponse> getNodes(@Query("api_key") String apiKey, @Query("bbox") String bbox, @Query("per_page") int perPage,
                                @Query("wheelchair") String wheelchair);


    // Salil's API

    /**
     * Api call for get way
     * @param requestGetWay Request get way bodey
     * @return way
     */
    @POST("Get/")
    Call<ResponseWay> getWays(@Body RequestGetWay requestGetWay);

    /**
     * Api call for validate way
     * @param requestWayInfo Request Way info
     * @return way
     */
    @POST("Validate")
    Call<ResponseWay> validate(@Body RequestWayInfo requestWayInfo);

    /**
     * Api call for update way
     * @param requestWayInfo Request Way info
     * @return way updated
     */
    @POST("update")
    Call<ResponseUpdate> update(@Body RequestWayInfo requestWayInfo);

    /**
     * Api call for update node
     * @param requestNodeInfo Request node info
     * @return node updated
     */
    @POST("updateNode")
    Call<ResponseUpdate> updateNode(@Body RequestNodeInfo requestNodeInfo);

    /**
     * Api call for get list data
     * @return list of ways
     */
    @GET("List")
    Call<ResponseListWay> getList();

}
