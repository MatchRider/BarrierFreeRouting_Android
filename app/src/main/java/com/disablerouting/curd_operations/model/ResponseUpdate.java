package com.disablerouting.curd_operations.model;


import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseUpdate implements Parcelable {


    @JsonProperty("Status")
    private
    boolean mStatus;

    @JsonProperty("Error")
    private
    List<Error> mError;


    public ResponseUpdate() {
    }


    protected ResponseUpdate(Parcel in) {
        mStatus = in.readByte() != 0;
        mError = in.createTypedArrayList(Error.CREATOR);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (mStatus ? 1 : 0));
        dest.writeTypedList(mError);
    }

    public boolean isStatus() {
        return mStatus;
    }

    public List<Error> getError() {
        return mError;
    }

    public static Creator<ResponseUpdate> getCREATOR() {
        return CREATOR;
    }
}
