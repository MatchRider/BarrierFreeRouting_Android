package com.disablerouting.curd_operations.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/*
@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
*/
@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeReference implements Parcelable {

    @JsonProperty("OSMNodeId")
    private
    String mOSMNodeId;

    @JsonProperty("APINodeId")
    private
    String mAPINodeId;

    @JsonProperty("Lat")
    private
    String mLat;

    @JsonProperty("Lon")
    private
    String mLon;

    @JsonProperty("Version")
    private
    String mVersion;

    @JsonProperty("Attributes")
    private
    List<Attributes> mAttributes = new ArrayList<>();

    private String mIsForData;

    public NodeReference() {
    }

    protected NodeReference(Parcel in) {
        mOSMNodeId = in.readString();
        mAPINodeId = in.readString();
        mLat = in.readString();
        mLon = in.readString();
        mVersion = in.readString();
        mAttributes = in.createTypedArrayList(Attributes.CREATOR);
        mIsForData = in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mOSMNodeId);
        dest.writeString(mAPINodeId);
        dest.writeString(mLat);
        dest.writeString(mLon);
        dest.writeString(mVersion);
        dest.writeTypedList(mAttributes);
        dest.writeString(mIsForData);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NodeReference> CREATOR = new Creator<NodeReference>() {
        @Override
        public NodeReference createFromParcel(Parcel in) {
            return new NodeReference(in);
        }

        @Override
        public NodeReference[] newArray(int size) {
            return new NodeReference[size];
        }
    };

    public String getAPINodeId() {
        return mAPINodeId;
    }

    public void setAPINodeId(String id) {
        mAPINodeId = id;
    }

    public String getOSMNodeId() {
        return mOSMNodeId; //
    }

    public void setOSMNodeId(String OSMNodeId) {
        mOSMNodeId = OSMNodeId;
    }

    public String getLat() {
        return mLat;
    }

    public void setLat(String lat) {
        mLat = lat;
    }

    public String getLon() {
        return mLon;
    }

    public void setLon(String lon) {
        mLon = lon;
    }

    public String getVersion() {
        return mVersion;
    }

    public void setVersion(String version) {
        mVersion = version;
    }

    public List<Attributes> getAttributes() {
        return mAttributes;
    }

    public void setAttributes(List<Attributes> attributes) {
        mAttributes = attributes;
    }

    public String getIsForData() {
        return mIsForData;
    }

    public void setIsForData(String isForData) {
        mIsForData = isForData;
    }
}
