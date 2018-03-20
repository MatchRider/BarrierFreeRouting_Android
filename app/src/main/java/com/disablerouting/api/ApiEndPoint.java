package com.disablerouting.api;


public interface ApiEndPoint {

    String BASE_URL = "https://api.openrouteservice.org/";
    String APP_CONTENT_TYPE = "Content-Type";
    String API_KEY= "58d904a497c67e00015b45fc2deebb0cd8724d54ba2d7d57c525bf8d";

    String LIVE_BASE_URL_OSM = "https://api.openrouteservice.org/"; // need to change
    String SANDBOX_BASE_URL_OSM = "https://master.apis.dev.openstreetmap.org/api/0.6/";
    String AUTHORIZATION_TAG_OSM = "Authorization";
    String AUTHORIZATION_KEY_OSM= "Basic c2h1YmhhbS5zYWhnYWxAZGFmZm9kaWxzdy5jb206U2h1YmhhbUAwOTEx";

}