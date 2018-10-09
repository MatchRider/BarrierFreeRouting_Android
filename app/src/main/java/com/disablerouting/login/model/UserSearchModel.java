package com.disablerouting.login.model;

import com.disablerouting.geo_coding.model.Features;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.util.HashMap;

public class UserSearchModel {

    private String mSourceAdd;
    private String mDestAdd;
    private GeoPoint mSourceGeoPoint;
    private GeoPoint mDestGeoPoint;
    private Features mFeaturesSource;
    private Features mFeaturesDest;
    private HashMap<String, Features> mHashMapFilterForRouting;
    private HashMap<Integer, Integer> mHashMapObjectFilterItem;
    private HashMap<String, String> mHashMapObjectFilter;
    private JSONObject mJSONObjectFiter;


    public UserSearchModel(String sourceAdd, String destAdd, GeoPoint sourceGeoPoint,
                           GeoPoint destGeoPoint, Features featuresSource,Features featuresDest,
                           HashMap<String, Features> hashMapFilterForRouting, HashMap<Integer, Integer> hashMapObjectFilterItem,
                           JSONObject jsonObject,HashMap<String,String> hashMapObjectFilter) {
        mSourceAdd = sourceAdd;
        mDestAdd = destAdd;
        mSourceGeoPoint = sourceGeoPoint;
        mDestGeoPoint = destGeoPoint;
        mFeaturesSource=featuresSource;
        mFeaturesDest=featuresDest;
        mHashMapFilterForRouting = hashMapFilterForRouting;
        mHashMapObjectFilterItem = hashMapObjectFilterItem;
        mJSONObjectFiter = jsonObject;
        mHashMapObjectFilter = hashMapObjectFilter;
    }

    public String getSourceAdd() {
        return mSourceAdd;
    }

    public String getDestAdd() {
        return mDestAdd;
    }

    public GeoPoint getSourceGeoPoint() {
        return mSourceGeoPoint;
    }

    public GeoPoint getDestGeoPoint() {
        return mDestGeoPoint;
    }

    public HashMap<String, Features> getHashMapFilterForRouting() {
        return mHashMapFilterForRouting;
    }

    public HashMap<Integer, Integer> getHashMapObjectFilterItem() {
        return mHashMapObjectFilterItem;
    }

    public JSONObject getJSONObjectFiter() {
        return mJSONObjectFiter;
    }

    public Features getFeaturesSource() {
        return mFeaturesSource;
    }

    public Features getFeaturesDest() {
        return mFeaturesDest;
    }

    public HashMap<String, String> getHashMapObjectFilter() {
        return mHashMapObjectFilter;
    }
}
