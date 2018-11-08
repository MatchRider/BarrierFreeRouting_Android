package com.disablerouting.api;


public interface ApiEndPoint {

    String BASE_URL = "https://api.openrouteservice.org/";
    String APP_CONTENT_TYPE = "Content-Type";
    String API_KEY = "58d904a497c67e00015b45fc2deebb0cd8724d54ba2d7d57c525bf8d";
    String AUTHORIZATION_TAG_OSM = "Authorization";
    String AUTHORIZATION_KEY_OSM = "Basic c2h1YmhhbS5zYWhnYWxAZGFmZm9kaWxzdy5jb206U2h1YmhhbUAwOTEx";


    //DEVELOPMENT
   /* String LIVE_BASE_URL_OSM = "https://master.apis.dev.openstreetmap.org/api/0.6/";
    String BASE_URL_OAUTH="https://master.apis.dev.openstreetmap.org/oauth/";
    String CONSUMER_KEY=    "lIHzMdENQdGnwi1TXwh1J6mgtDfTKIv8NjNaX6B9";
    String CONSUMER_SECRET_KEY=  "OWzh43EcXC3Q7wfkZIIg2OQKTPoqr0VcpzR4PRTb";
*/
    //LIVE
    String LIVE_BASE_URL_OSM = "https://api.openstreetmap.org/api/0.6/";
    String BASE_URL_OAUTH = "https://www.openstreetmap.org/oauth/";
    String CONSUMER_KEY = "t46M4A2anQb1O53J6aInLjhWpojjKuprlAg1KSDJ";
    String CONSUMER_SECRET_KEY = "ZKJzfplM9jMtqgOLUfVKQ3O0OYWrJjC4XOFkLkNH";

    String requestTokenUrl = "request_token";
    String authorizeUrl = "authorize";
    String accessTokenUrl = "access_token";
    String OSM_REDIRECT_URI = "disabled-routing://oauth-callback/";

    String BASE_URL_WHEEL_MAP = "https://wheelmap.org/api/";
    String API_KEY_WHEEL_MAP = "b4W1zzy2xnWjwjKQ87Tu";

    String BASE_URL_SALIL = "https://disabledrouteapi.azurewebsites.net/DisableRoute/";


}