package com.disablerouting.feedback.model;


import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name="tag", strict=false)
public class RequestTag {

    @Attribute(name="k")
    private String mK;

    @Attribute(name="v")
    private String mV;

    public RequestTag(String k, String v) {
        mK = k;
        mV = v;
    }

    public String getK() {
        return mK;
    }

    public void setK(String k) {
        mK = k;
    }

    public String getV() {
        return mV;
    }

    public void setV(String v) {
        mV = v;
    }
}
