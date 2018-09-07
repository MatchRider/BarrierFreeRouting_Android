package com.disablerouting.feedback.model;


import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name="nd", strict=false)
public class RequestNode {

    @Attribute(name="ref")
    private String mRef;

    public RequestNode(String ref) {
        mRef = ref;
    }
}
