package com.disablerouting.route_planner.model;


import android.os.Parcel;
import android.os.Parcelable;

public class FeedBackModel implements Parcelable {


    private double mLatitude;

    private double mLongitude;

    private String mChangeSetID;

    public FeedBackModel(double latitude, double longitude) {
        mLatitude = latitude;
        mLongitude = longitude;
    }

    private FeedBackModel(Parcel in) {
        mLatitude = in.readDouble();
        mLongitude = in.readDouble();
        mChangeSetID = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(mLatitude);
        dest.writeDouble(mLongitude);
        dest.writeString(mChangeSetID);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FeedBackModel> CREATOR = new Creator<FeedBackModel>() {
        @Override
        public FeedBackModel createFromParcel(Parcel in) {
            return new FeedBackModel(in);
        }

        @Override
        public FeedBackModel[] newArray(int size) {
            return new FeedBackModel[size];
        }
    };

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public String getChangeSetID() {
        return mChangeSetID;
    }

    public void setChangeSetID(String changeSetID) {
        mChangeSetID = changeSetID;
    }
}
