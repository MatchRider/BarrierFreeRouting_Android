package com.disablerouting.suggestions.view;


import com.disablerouting.geo_coding.model.Features;
import com.disablerouting.route_planner.model.Steps;

import java.util.List;

public interface OnSuggestionListener {

    void plotDataOnMap(String encodedString, List<Steps> stepsList);

    void onSourceDestinationSelected(Features featuresSource, Features featuresDestination);

    void onBackPress();

    void onGoButtonVisibility(boolean visible);

    void onPlotRouteWhenDestinationSelected();

    void onClearItemsOfMap();

    void onNoClickAddLocation();


}
