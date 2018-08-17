package com.disablerouting.geo_coding.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Properties implements Parcelable {

    @JsonProperty("country")
    private String mCountry="";

    @JsonProperty("country_code")
    private String mCountry_code="";

    @JsonProperty("region")
    private String mRegion="";

    @JsonProperty("locality")
    private String mLocality="";

    @JsonProperty("street")
    private String mStreet="";

    @JsonProperty("name")
    private String mName="";

    @JsonProperty("house_number")
    private String mHouseNumber="";

    @JsonProperty("place_type")
    private String mPlaceType="";

    public Properties() {
    }

    protected Properties(Parcel in) {
        mCountry = in.readString();
        mCountry_code = in.readString();
        mRegion = in.readString();
        mLocality = in.readString();
        mStreet = in.readString();
        mName = in.readString();
        mHouseNumber = in.readString();
        mPlaceType = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mCountry);
        dest.writeString(mCountry_code);
        dest.writeString(mRegion);
        dest.writeString(mLocality);
        dest.writeString(mStreet);
        dest.writeString(mName);
        dest.writeString(mHouseNumber);
        dest.writeString(mPlaceType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Properties> CREATOR = new Creator<Properties>() {
        @Override
        public Properties createFromParcel(Parcel in) {
            return new Properties(in);
        }

        @Override
        public Properties[] newArray(int size) {
            return new Properties[size];
        }
    };

    public void setCountry(String country) {
        mCountry = country;
    }

    public void setCountry_code(String country_code) {
        mCountry_code = country_code;
    }

    public void setRegion(String region) {
        mRegion = region;
    }

    public void setLocality(String locality) {
        mLocality = locality;
    }

    public void setStreet(String street) {
        mStreet = street;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setHouseNumber(String houseNumber) {
        mHouseNumber = houseNumber;
    }

    public void setPlaceType(String placeType) {
        mPlaceType = placeType;
    }

    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        if(mName!=null && !mName.isEmpty()) {
            strBuilder.append(mName);}
        if(mHouseNumber!=null && !mHouseNumber.isEmpty()) {
            strBuilder.append(" "+mHouseNumber);}
        if(mStreet!=null && !mStreet.isEmpty()) {
            strBuilder.append(" "+mStreet);}
        if(mLocality!=null && !mLocality.isEmpty()) {
            strBuilder.append(" "+mLocality);}
        if(mRegion!=null && !mRegion.isEmpty()) {
            strBuilder.append(" "+mRegion);
        }if(mCountry!=null && !mCountry.isEmpty()) {
            strBuilder.append(" "+mCountry);
        }
       // return mName + mHouseNumber + mStreet + mLocality + mRegion + mCountry ;
        return strBuilder.toString();
    }

    public String getCountry() {
        return mCountry;
    }

    public String getCountry_code() {
        return mCountry_code;
    }

    public String getRegion() {
        return mRegion;
    }

    public String getLocality() {
        return mLocality;
    }

    public String getStreet() {
        return mStreet;
    }

    public String getName() {
        return mName;
    }

    public String getHouseNumber() {
        return mHouseNumber;
    }

    public String getPlaceType() {
        return mPlaceType;
    }
}
