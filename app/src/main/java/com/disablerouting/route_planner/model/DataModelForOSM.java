package com.disablerouting.route_planner.model;


import java.util.List;

public class DataModelForOSM {

    List<WayCustomModel> mWayCustomModels;

    public List<WayCustomModel> getWayCustomModels() {
        return mWayCustomModels;
    }

    public void setWayCustomModels(List<WayCustomModel> wayCustomModels) {
        mWayCustomModels = wayCustomModels;
    }
}
