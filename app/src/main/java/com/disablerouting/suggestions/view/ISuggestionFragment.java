package com.disablerouting.suggestions.view;


import com.disablerouting.common.ILoader;
import com.disablerouting.curd_operations.model.RequestGetWay;
import com.disablerouting.curd_operations.model.ResponseWay;
import com.disablerouting.geo_coding.model.GeoCodingResponse;
import com.disablerouting.route_planner.model.DirectionsResponse;

public interface ISuggestionFragment extends ILoader {

    /**
     * To show direction from source to destination
     * @param data result of coordinates
     */
    void onDirectionDataReceived(DirectionsResponse data);

    /**
     * To show relevant error to user
     * @param error Error message
     */
    void onFailureDirection(String error);

    /**
     * To show places
     * @param data response
     */
    void onGeoDataDataReceived(GeoCodingResponse data, boolean isForCurrentLoc);

    /**
     * To show relevant error to user
     * @param error Error message
     */
    void onFailureGeoCoding(String error);


    void getWays(RequestGetWay requestGetWay);

    void onWayDataReceived(ResponseWay responseWay);

    /**
     * To show relevant error to user
     * @param error Error message
     */

    void onFailure(String error);


}
