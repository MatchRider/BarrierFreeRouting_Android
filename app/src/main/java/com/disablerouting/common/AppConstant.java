package com.disablerouting.common;


public interface AppConstant {
    int REQUEST_CODE = 200;
    int SETTING_REQUEST_CODE = 201;
    int REQUEST_CODE_CAPTURE = 202;
    int REQUEST_CODE_LOGIN = 203;
    int REQUEST_CODE_SCREEN = 204;
    int REQUEST_CODE_UPDATE_MAP_DATA = 205;

    String IS_FILTER = "isFilter";
    String DATA_FILTER= "data_filter";
    String DATA_FILTER_SELECTED= "data_filter_selected";
    String DATA_FILTER_ROUTING_VIA= "data_filter_roting_via";


    String publicTramStop = "tram_stop";
    String publicToilets= "toilets";
    String publicBusStop= "bus_stop";
    String publicParking= "parking";

    String PROFILE_DRIVING_CAR="driving-car";
    String PROFILE_WHEEL_CHAIR="wheelchair";

    String POSITION_SETTING="position";
    String TITLE_TEXT="title";
    String SETTING_ITEM_SELECTED_RECIEVE ="selected";
    String SETTING_ITEM_SELECTED_SEND="selected";


    String WAY_DATA="WayData";
    String STEP_DATA="StepData";

     String API_TYPE_CREATE_CHANGE_SET="createChangeSet";
     String API_TYPE_CREATE_PUT_WAY="wayUpdate";

     String KEY_FOOTWAY = "footway"; //footway surface
     String KEY_HIGHWAY = "highway"; //highway kerb
     String KEY_INCLINE = "incline";
     String KEY_WIDTH = "width";
     String KEY_KERB_HEIGHT = "kerb:height";
     String IS_FOR_WAY= "is_for_way";



}
