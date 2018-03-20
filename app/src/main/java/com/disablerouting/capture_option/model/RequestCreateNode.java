package com.disablerouting.capture_option.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="osm", strict=false)
public class RequestCreateNode {

    public RequestCreateNode() {

    }

    @Element(data = false,name="node", required = true,type = Node.class)
    private Node mNode;


    public void setNode(Node node) {
        mNode = node;
    }
}
