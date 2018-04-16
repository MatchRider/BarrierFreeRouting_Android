package com.disablerouting.filter.view;

import com.disablerouting.common.ILoader;
import com.disablerouting.geo_coding.model.GeoCodingResponse;

public interface IFilterView extends ILoader {

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


}