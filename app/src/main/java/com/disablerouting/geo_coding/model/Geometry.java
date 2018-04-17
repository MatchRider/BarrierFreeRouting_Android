package com.disablerouting.geo_coding.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Geometry implements Parcelable{

    @JsonProperty("type")
    private String mType;

    @JsonProperty("coordinates")
    private List<Double> mCoordinates = null;

    public Geometry() {
    }

    protected Geometry(Parcel in) {
        mType = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Geometry> CREATOR = new Creator<Geometry>() {
        @Override
        public Geometry createFromParcel(Parcel in) {
            return new Geometry(in);
        }

        @Override
        public Geometry[] newArray(int size) {
            return new Geometry[size];
        }
    };

    public String getType() {
        return mType;
    }

    public List<Double> getCoordinates() {
        return mCoordinates;
    }

}
