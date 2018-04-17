package com.disablerouting.route_planner.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Info {

    @JsonProperty("query")
    private Query mQuery;

    public Query getQuery() {
        return mQuery;
    }
}
