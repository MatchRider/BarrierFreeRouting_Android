package com.disablerouting.map_base;


import org.osmdroid.util.GeoPoint;

public interface OnFeedBackListener {

    void onFeedBackClick(double longitude, double latitude);

    void onMapPlotted();

    void onDragClicked(GeoPoint geoPoint);
}
