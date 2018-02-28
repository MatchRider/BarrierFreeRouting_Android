package com.disablerouting.route_planner.view;

import com.disablerouting.common.ILoader;
import com.disablerouting.geo_coding.model.GeoCodingResponse;
import com.disablerouting.route_planner.model.DirectionsResponse;

public interface ISourceDestinationViewFragment extends ILoader {

    /**
     * To show direction from source to destination
     * @param data
     */
    void onDirectionDataReceived(DirectionsResponse data);

    /**
     * To show relevant error to user
     * @param error Error message
     */
    void onFailureDirection(String error);

    /**
     * To show places
     * @param data
     */
    void onGeoDataDataReceived(GeoCodingResponse data);

    /**
     * To show relevant error to user
     * @param error Error message
     */
    void onFailureGeoCoding(String error);


}