package com.disablerouting.route_planner.model;


import android.os.Parcel;
import android.os.Parcelable;
import com.disablerouting.curd_operations.model.Attributes;
import org.osmdroid.util.GeoPoint;

import java.util.List;

public class WayCustomModel implements Parcelable {

    private String mId;
    private String mProjectId;
    private List<GeoPoint> mGeoPoint;
    private String mColor;
    private String mStatus;
    private List<Attributes> mAttributesList;

    public WayCustomModel() {
    }

    protected WayCustomModel(Parcel in) {
        mId = in.readString();
        mProjectId = in.readString();
        mGeoPoint = in.createTypedArrayList(GeoPoint.CREATOR);
        mColor = in.readString();
        mStatus = in.readString();
        mAttributesList = in.createTypedArrayList(Attributes.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mProjectId);
        dest.writeTypedList(mGeoPoint);
        dest.writeString(mColor);
        dest.writeString(mStatus);
        dest.writeTypedList(mAttributesList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WayCustomModel> CREATOR = new Creator<WayCustomModel>() {
        @Override
        public WayCustomModel createFromParcel(Parcel in) {
            return new WayCustomModel(in);
        }

        @Override
        public WayCustomModel[] newArray(int size) {
            return new WayCustomModel[size];
        }
    };

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public List<GeoPoint> getGeoPoint() {
        return mGeoPoint;
    }

    public void setGeoPoint(List<GeoPoint> geoPoint) {
        mGeoPoint = geoPoint;
    }

    public String getColor() {
        return mColor;
    }

    public void setColor(String color) {
        this.mColor = color;
    }


    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public List<Attributes> getAttributesList() {
        return mAttributesList;
    }

    public void setAttributesList(List<Attributes> attributesList) {
        mAttributesList = attributesList;
    }

    public String getProjectId() {
        return mProjectId;
    }

    public void setProjectId(String projectId) {
        mProjectId = projectId;
    }
}
