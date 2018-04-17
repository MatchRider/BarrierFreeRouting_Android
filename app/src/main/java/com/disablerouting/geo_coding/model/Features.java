package com.disablerouting.geo_coding.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Features implements Parcelable {

    @JsonProperty("type")
    private String mType;

    @JsonProperty("geometry")
    private Geometry mGeometry ;

    @JsonProperty("properties")
    private Properties mProperties;

    public Features() {
    }

    protected Features(Parcel in) {
        mType = in.readString();
        mGeometry = in.readParcelable(Geometry.class.getClassLoader());
        mProperties = in.readParcelable(Properties.class.getClassLoader());
    }

    public static final Creator<Features> CREATOR = new Creator<Features>() {
        @Override
        public Features createFromParcel(Parcel in) {
            return new Features(in);
        }

        @Override
        public Features[] newArray(int size) {
            return new Features[size];
        }
    };

    public void setType(String type) {
        mType = type;
    }

    public void setGeometry(Geometry geometry) {
        mGeometry = geometry;
    }

    public void setProperties(Properties properties) {
        mProperties = properties;
    }

    public String getType() {
        return mType;
    }

    public Geometry getGeometry() {
        return mGeometry;
    }

    public Properties getProperties() {
        return mProperties;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mType);
        parcel.writeParcelable(mGeometry, i);
        parcel.writeParcelable(mProperties, i);
    }
}
