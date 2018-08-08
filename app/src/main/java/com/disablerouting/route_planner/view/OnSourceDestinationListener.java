package com.disablerouting.route_planner.view;


import com.disablerouting.geo_coding.model.Features;
import com.disablerouting.route_planner.model.NodeItem;
import com.disablerouting.route_planner.model.Steps;
import org.osmdroid.util.GeoPoint;

import java.util.List;

public interface OnSourceDestinationListener {

    void plotDataOnMap(List<List<Double>> geoPointList, List<Steps> stepsList);

    void onBackPress();

    void onSourceDestinationSelected(Features featuresSource, Features featuresDestination);

    void onApplyFilter();

    void plotNodesOnMap(List<NodeItem> mNodes);

    void onSwapData();

    void plotMidWayRouteMarker(GeoPoint geoPoint);

    void onSourceClickWhileNavigationRunning();

    void onDestinationClickWhileNavigationRunning();

    void onToggleClickedBanner(boolean isChecked);

}
