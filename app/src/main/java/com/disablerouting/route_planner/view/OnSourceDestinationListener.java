package com.disablerouting.route_planner.view;


import com.disablerouting.geo_coding.model.Features;
import com.disablerouting.route_planner.model.NodeItem;
import com.disablerouting.route_planner.model.Steps;

import java.util.List;

public interface OnSourceDestinationListener {

    void plotDataOnMap(String encodedString, List<Steps> stepsList);

    void onBackPress();

    void onSourceDestinationSelected(Features featuresSource, Features featuresDestination);

    void onApplyFilter();

    void plotNodesOnMap(List<NodeItem> mNodes);
}
