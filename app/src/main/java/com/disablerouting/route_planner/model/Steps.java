package com.disablerouting.route_planner.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Steps implements Parcelable {

    @JsonProperty("name")
    private String mName;

    @JsonProperty("way_points")
    private ArrayList<Integer> mDoublesWayPoints;

    @JsonProperty("type")
    private int mType;

    @JsonProperty("instruction")
    private String mInstructions;

    @JsonProperty("distance")
    private double mDistance;

    @JsonProperty("duration")
    private double mDuration;

    public Steps() {
    }

    protected Steps(Parcel in) {
        mName = in.readString();
        mType = in.readInt();
        mInstructions = in.readString();
        mDistance = in.readDouble();
        mDuration = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeInt(mType);
        dest.writeString(mInstructions);
        dest.writeDouble(mDistance);
        dest.writeDouble(mDuration);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Steps> CREATOR = new Creator<Steps>() {
        @Override
        public Steps createFromParcel(Parcel in) {
            return new Steps(in);
        }

        @Override
        public Steps[] newArray(int size) {
            return new Steps[size];
        }
    };

    public String getName() {
        return mName;
    }

    public int getType() {
        return mType;
    }

    public String getInstructions() {
        return mInstructions;
    }

    public double getDistance() {
        return mDistance;
    }

    public double getDuration() {
        return mDuration;
    }

    public ArrayList<Integer> getDoublesWayPoints() {
        return mDoublesWayPoints;
    }

    public void setType(int type) {
        mType = type;
    }

    public void setInstructions(String instructions) {
        mInstructions = instructions;
    }
}
