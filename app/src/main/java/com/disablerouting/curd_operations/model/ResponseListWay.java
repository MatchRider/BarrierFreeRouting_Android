package com.disablerouting.curd_operations.model;


import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseListWay implements Parcelable {


    @JsonProperty("WayData")
    private
    List<ListWayData> mWayData;

    @JsonProperty("Status")
    private
    boolean mStatus;

    @JsonProperty("Error")
    private
    List<Error> mError;

    public ResponseListWay() {
    }


    protected ResponseListWay(Parcel in) {
        mWayData = in.createTypedArrayList(ListWayData.CREATOR);
        mStatus = in.readByte() != 0;
        mError = in.createTypedArrayList(Error.CREATOR);
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


    public List<Error> getError() {
        return mError;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mWayData);
        dest.writeByte((byte) (mStatus ? 1 : 0));
        dest.writeTypedList(mError);
    }

    public void setWayData(List<ListWayData> wayData) {
        mWayData = wayData;
    }

    public void setStatus(boolean status) {
        mStatus = status;
    }

    public void setError(List<Error> error) {
        mError = error;
    }
}
