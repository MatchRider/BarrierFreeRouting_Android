package com.disablerouting.curd_operations.model;


import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseUpdate implements Parcelable {


    @JsonProperty("Status")
    boolean mStatus;

    @JsonProperty("Error")
    String mError;


    public ResponseUpdate() {
    }

    protected ResponseUpdate(Parcel in) {
        mStatus = in.readByte() != 0;
        mError = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (mStatus ? 1 : 0));
        dest.writeString(mError);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ResponseUpdate> CREATOR = new Creator<ResponseUpdate>() {
        @Override
        public ResponseUpdate createFromParcel(Parcel in) {
            return new ResponseUpdate(in);
        }

        @Override
        public ResponseUpdate[] newArray(int size) {
            return new ResponseUpdate[size];
        }
    };

    public boolean isStatus() {
        return mStatus;
    }

    public String getError() {
        return mError;
    }
}
