package com.disablerouting.route_planner.model;


import com.disablerouting.curd_operations.model.ListWayData;
import org.osmdroid.util.GeoPoint;

public class ProgressModel {

    private GeoPoint start;
    private ListWayData mListWayData;
    private boolean mValid;

    public ProgressModel(GeoPoint startPoint, ListWayData listWayData, boolean valid) {
        this.start=startPoint;
        this.mListWayData=listWayData;
        this.mValid = valid;
    }

    public GeoPoint getStart() {
        return start;
    }

    public ListWayData getListWayData() {
        return mListWayData;
    }
    public boolean isValid() {
        return mValid;
    }
}
