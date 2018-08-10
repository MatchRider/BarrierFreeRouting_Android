package com.disablerouting.curd_operations.model;


import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseWay implements Parcelable {


    @JsonProperty("WayData")
    List<WayData> mWayData;

    @JsonProperty("Status")
    boolean mStatus;

    @JsonProperty("Error")
    String mError;

    public ResponseWay() {
    }

    protected ResponseWay(Parcel in) {
        mWayData = in.createTypedArrayList(WayData.CREATOR);
        mStatus = in.readByte() != 0;
        mError = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mWayData);
        dest.writeByte((byte) (mStatus ? 1 : 0));
        dest.writeString(mError);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ResponseWay> CREATOR = new Creator<ResponseWay>() {
        @Override
        public ResponseWay createFromParcel(Parcel in) {
            return new ResponseWay(in);
        }

        @Override
        public ResponseWay[] newArray(int size) {
            return new ResponseWay[size];
        }
    };

    public List<WayData> getWayData() {
        return mWayData;
    }

    public boolean isStatus() {
        return mStatus;
    }

    public String getError() {
        return mError;
    }

    public void setWayData(List<WayData> wayData) {
        mWayData = wayData;
    }
}
