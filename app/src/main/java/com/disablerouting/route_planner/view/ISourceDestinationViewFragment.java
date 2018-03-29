package com.disablerouting.route_planner.view;

import com.disablerouting.common.ILoader;
import com.disablerouting.geo_coding.model.GeoCodingResponse;
import com.disablerouting.route_planner.model.DirectionsResponse;
import com.disablerouting.route_planner.model.NodeResponse;

public interface ISourceDestinationViewFragment extends ILoader {

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


    /**
     * TO show amenity on map
     * @param data node response
     */
    void onNodeDataReceived(NodeResponse data);

    /**
     * To show relevant error to user
     * @param error Error message
     */

    void onFailureNode(String error);


}