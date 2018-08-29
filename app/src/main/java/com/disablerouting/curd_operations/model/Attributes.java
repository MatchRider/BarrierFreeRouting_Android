package com.disablerouting.curd_operations.model;


import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Attributes implements Parcelable {

    @JsonProperty("Key")
    String mKey;

    @JsonProperty("Value")
    String mValue;

    @JsonProperty("IsValid")
    boolean mIsValid;

    public Attributes() {
    }

    protected Attributes(Parcel in) {
        mKey = in.readString();
        mValue = in.readString();
        mIsValid = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mKey);
        dest.writeString(mValue);
        dest.writeByte((byte) (mIsValid ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Attributes> CREATOR = new Creator<Attributes>() {
        @Override
        public Attributes createFromParcel(Parcel in) {
            return new Attributes(in);
        }

        @Override
        public Attributes[] newArray(int size) {
            return new Attributes[size];
        }
    };

    public String getKey() {
        return mKey;
    }

    public String getValue() {
        return mValue;
    }

    public boolean isValid() {
        return mIsValid;
    }
}
