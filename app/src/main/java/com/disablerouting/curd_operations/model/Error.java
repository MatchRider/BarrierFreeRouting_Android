package com.disablerouting.curd_operations.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Error implements Parcelable {

    @JsonProperty("Code")
    private
    String mCode;

    @JsonProperty("Message")
    private
    String mMessage;

    @JsonProperty("IsError")
    private
    boolean mIsError;

    protected Error(Parcel in) {
        mCode = in.readString();
        mMessage = in.readString();
        mIsError = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mCode);
        dest.writeString(mMessage);
        dest.writeByte((byte) (mIsError ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Error> CREATOR = new Creator<Error>() {
        @Override
        public Error createFromParcel(Parcel in) {
            return new Error(in);
        }

        @Override
        public Error[] newArray(int size) {
            return new Error[size];
        }
    };

    public String getCode() {
        return mCode;
    }

    public String getMessage() {
        return mMessage;
    }

    public boolean isError() {
        return mIsError;
    }
}
