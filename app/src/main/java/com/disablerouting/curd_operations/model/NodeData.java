package com.disablerouting.curd_operations.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeData implements Parcelable {

    @JsonProperty("Id")
    private
    String mId;

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

    public NodeData() {
    }

    protected NodeData(Parcel in) {
        mId = in.readString();
        mLat = in.readString();
        mLon = in.readString();
        mVersion = in.readString();
        mAttributes = in.createTypedArrayList(Attributes.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mLat);
        dest.writeString(mLon);
        dest.writeString(mVersion);
        dest.writeTypedList(mAttributes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NodeData> CREATOR = new Creator<NodeData>() {
        @Override
        public NodeData createFromParcel(Parcel in) {
            return new NodeData(in);
        }

        @Override
        public NodeData[] newArray(int size) {
            return new NodeData[size];
        }
    };

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
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
}
