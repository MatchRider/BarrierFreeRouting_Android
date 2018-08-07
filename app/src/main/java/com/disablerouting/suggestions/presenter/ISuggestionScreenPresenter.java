package com.disablerouting.suggestions.presenter;


import com.disablerouting.curd_operations.model.RequestGetWay;
import org.json.JSONObject;

public interface ISuggestionScreenPresenter {

    void getDestinationsData(String coordinates, String profile , JSONObject jsonObject);

    void getCoordinatesData(String query, String location, int limit);

    void getGeoCodeDataForward(String query);

    void getGeoCodeDataReverse(double latitude, double longitude);

    void getWays(RequestGetWay requestGetWay);


    void disconnect();

}
