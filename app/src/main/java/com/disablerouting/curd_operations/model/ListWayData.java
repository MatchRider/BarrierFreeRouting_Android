package com.disablerouting.curd_operations.model;


import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ListWayData implements Parcelable{

    @JsonProperty("Id")
    private
    String mId;

    @JsonProperty("ProjectId")
    private
    String mProjectId;

    @JsonProperty("Coordinates")
    private
    List<ParcelableArrayList> mCoordinates;

    @JsonProperty("NodeReference")
    private
    List<NodeData> mNodeReference;

    @JsonProperty("Color")
    private
    String mColor;

    @JsonProperty("IsValid")
    private
    String mIsValid;

    @JsonProperty("Version")
    private
    String mVersion;

    @JsonProperty("Attributes")
    private
    List<Attributes> mAttributesList;

    public ListWayData() {

    }


    protected ListWayData(Parcel in) {
        mId = in.readString();
        mProjectId = in.readString();
        mCoordinates = in.createTypedArrayList(ParcelableArrayList.CREATOR);
        mNodeReference = in.createTypedArrayList(NodeData.CREATOR);
        mColor = in.readString();
        mIsValid = in.readString();
        mVersion = in.readString();
        mAttributesList = in.createTypedArrayList(Attributes.CREATOR);
    }

    public static final Creator<ListWayData> CREATOR = new Creator<ListWayData>() {
        @Override
        public ListWayData createFromParcel(Parcel in) {
            return new ListWayData(in);
        }

        @Override
        public ListWayData[] newArray(int size) {
            return new ListWayData[size];
        }
    };

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getProjectId() {
        return mProjectId;
    }

    public void setProjectId(String projectId) {
        mProjectId = projectId;
    }

    public List<ParcelableArrayList> getCoordinates() {
        return mCoordinates;
    }

    public void setCoordinates(List<ParcelableArrayList> coordinates) {
        mCoordinates = coordinates;
    }

    public List<NodeData> getNodeReference() {
        return mNodeReference;
    }

    public void setNodeReference(List<NodeData> nodeReference) {
        mNodeReference = nodeReference;
    }

    public String getColor() {
        return mColor;
    }

    public void setColor(String color) {
        mColor = color;
    }

    public String getIsValid() {
        return mIsValid;
    }

    public void setIsValid(String isValid) {
        mIsValid = isValid;
    }


    public String getVersion() {
        return mVersion;
    }

    public void setVersion(String version) {
        mVersion = version;
    }

    public List<Attributes> getAttributesList() {
        return mAttributesList;
    }

    public void setAttributesList(List<Attributes> attributesList) {
        mAttributesList = attributesList;
    }


    public static Creator<ListWayData> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(mId);
        dest.writeString(mProjectId);
        dest.writeTypedList(mCoordinates);
        dest.writeString(mColor);
        dest.writeString(mIsValid);
        dest.writeString(mVersion);
        dest.writeTypedList(mAttributesList);
    }


    public List<GeoPoint> getGeoPoints(){
        ArrayList<GeoPoint> geoPointArrayList = new ArrayList<>();
        if (getCoordinates() != null) {
            for (int j = 0; j < getCoordinates().size(); j++) {
                String lat = getCoordinates().get(j).get(0);
                String lon = getCoordinates().get(j).get(1);
                if (lat != null && lon != null) {
                    geoPointArrayList.add(new GeoPoint(Double.parseDouble(lat), Double.parseDouble((lon))));
                }
            }
        }
        return geoPointArrayList;
    }

}
