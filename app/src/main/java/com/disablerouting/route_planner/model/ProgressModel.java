package com.disablerouting.route_planner.model;


import com.disablerouting.curd_operations.model.ListWayData;
import com.disablerouting.curd_operations.model.NodeReference;
import org.osmdroid.util.GeoPoint;

public class ProgressModel {

    private GeoPoint start;
    private ListWayData mListWayData;
    private boolean mValid;
    private NodeReference mNodeReference;

    public ProgressModel(GeoPoint startPoint, ListWayData listWayData, boolean valid, NodeReference nodeReference) {
        this.start=startPoint;
        this.mListWayData=listWayData;
        this.mValid = valid;
        this.mNodeReference=nodeReference;
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

    public NodeReference getNodeReference() {
        return mNodeReference;
    }
}
