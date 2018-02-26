package com.disablerouting.geo_coding.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Properties {

    @JsonProperty("country")
    private String mCountry=" ";

    @JsonProperty("country_code")
    private String mCountry_code=" ";

    @JsonProperty("region")
    private String mRegion=" ";

    @JsonProperty("locality")
    private String mLocality=" ";

    @JsonProperty("street")
    private String mStreet=" ";

    @JsonProperty("name")
    private String mName=" ";

    @JsonProperty("house_number")
    private String mHouseNumber=" ";

    @JsonProperty("place_type")
    private String mPlaceType="";

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
        return mName + mHouseNumber + mStreet + mLocality + mRegion + mCountry ;
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
