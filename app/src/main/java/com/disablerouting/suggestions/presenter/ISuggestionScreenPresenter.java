package com.disablerouting.suggestions.presenter;


import org.json.JSONObject;

public interface ISuggestionScreenPresenter {

    void getDestinationsData(String coordinates, String profile , JSONObject jsonObject);

    void getCoordinatesData(String query, String location, int limit);

    void disconnect();

}
