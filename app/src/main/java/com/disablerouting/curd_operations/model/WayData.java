package com.disablerouting.curd_operations.model;


import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WayData implements Parcelable{

    @JsonProperty("Id")
    private
    String mId;

    @JsonProperty("Attributes")
    private
    List<Attributes> mAttributesList;

    public WayData() {
    }


    protected WayData(Parcel in) {
        mId = in.readString();
        mAttributesList = in.createTypedArrayList(Attributes.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeTypedList(mAttributesList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WayData> CREATOR = new Creator<WayData>() {
        @Override
        public WayData createFromParcel(Parcel in) {
            return new WayData(in);
        }

        @Override
        public WayData[] newArray(int size) {
            return new WayData[size];
        }
    };

    public String getId() {
        return mId;
    }

    public List<Attributes> getAttributesList() {
        return mAttributesList;
    }
}
