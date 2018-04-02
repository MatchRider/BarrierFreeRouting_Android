package com.disablerouting.api;

import com.disablerouting.capture_option.model.RequestCreateNode;
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
                                           @Query("profile") String profile, @Query("options" ) String options);

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


    /**
     * APi call for get changeset id
     * @param requestCreateChangeSet Request model for rrequesting change set id
     * @return return change set id
     */
    //@Headers({"Content-Type: application/xml; charset=utf-8"})
    @PUT("changeset/create")
    Call<ResponseBody> createChangeSet(@Body RequestCreateChangeSet requestCreateChangeSet);


    /**
     * Api call for creating node
     * @param type can be of three type (Nodes, Ways and Relations)
     * @param requestCreateNode Request model for create node
     * @return return change set id if successful
     */
    @PUT("{type}/create")
    Call<ResponseBody> setChangeSet(@Path("type") String type,@Body RequestCreateNode requestCreateNode);


    @GET("nodes") //, @Query("wheelchair") String wheelChair not using
    Call<NodeResponse> getNodes(@Query("api_key") String apiKey,@Query("bbox") String bbox, @Query("per_page") int perPage );

}
