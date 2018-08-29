package com.disablerouting.curd_operations.model;


import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseListWay implements Parcelable {


    @JsonProperty("WayData")
    List<ListWayData> mWayData;

    @JsonProperty("Status")
    boolean mStatus;

    @JsonProperty("Error")
    String mError;

    public ResponseListWay() {
    }

    protected ResponseListWay(Parcel in) {
        mWayData = in.createTypedArrayList(ListWayData.CREATOR);
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

    public static final Creator<ResponseListWay> CREATOR = new Creator<ResponseListWay>() {
        @Override
        public ResponseListWay createFromParcel(Parcel in) {
            return new ResponseListWay(in);
        }

        @Override
        public ResponseListWay[] newArray(int size) {
            return new ResponseListWay[size];
        }
    };

    public List<ListWayData> getWayData() {
        return mWayData;
    }

    public boolean isStatus() {
        return mStatus;
    }

    public String getError() {
        return mError;
    }

    public void setWayData(List<ListWayData> wayData) {
        mWayData = wayData;
    }
}
