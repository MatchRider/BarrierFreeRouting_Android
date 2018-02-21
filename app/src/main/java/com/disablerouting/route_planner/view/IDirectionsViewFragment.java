package com.disablerouting.route_planner.view;

import android.support.annotation.StringRes;
import com.disablerouting.common.ILoader;
import com.disablerouting.route_planner.model.DirectionsResponse;

public interface IDirectionsViewFragment extends ILoader {

    /**
     * To show list  of trip taken by user
     * @param data
     */
    void onDirectionDataReceived(DirectionsResponse data);

    /**
     * To show relevant error to user
     * @param error Error message
     */
    void onFailure(@StringRes int error);
}