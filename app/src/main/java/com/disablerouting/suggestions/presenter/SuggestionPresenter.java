package com.disablerouting.suggestions.presenter;


import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.disablerouting.api.ErrorResponse;
import com.disablerouting.geo_coding.manager.GeoCodingManager;
import com.disablerouting.geo_coding.model.GeoCodingResponse;
import com.disablerouting.geo_coding.presenter.IGeoCodingResponseReceiver;
import com.disablerouting.route_planner.manager.DirectionsManager;
import com.disablerouting.route_planner.model.DirectionsResponse;
import com.disablerouting.route_planner.presenter.IDirectionsResponseReceiver;
import com.disablerouting.suggestions.view.ISuggestionFragment;
import org.json.JSONObject;

public class SuggestionPresenter implements ISuggestionScreenPresenter, IDirectionsResponseReceiver,
        IGeoCodingResponseReceiver {

    private ISuggestionFragment mISuggestionFragment;
    private DirectionsManager mDirectionsManager;
    private GeoCodingManager mGeoCodingManager;
    private boolean isForCurrentLoc;

    public SuggestionPresenter(ISuggestionFragment suggestionFragment,
                               DirectionsManager directionsManager ,
                               GeoCodingManager geoCodingManager) {
        mISuggestionFragment = suggestionFragment;
        mDirectionsManager = directionsManager;
        mGeoCodingManager = geoCodingManager;
    }

    @Override
    public void getDestinationsData(String coordinates, String profile , JSONObject jsonObject) {
        if (mISuggestionFragment != null) {
            mISuggestionFragment.showLoader();
            mDirectionsManager.getDestination(this, coordinates, profile , jsonObject);
        }
    }

    @Override
    public void getCoordinatesData(String query, String location,int limit) {
        isForCurrentLoc=!TextUtils.isEmpty(location);
        if (mISuggestionFragment != null) {
            mISuggestionFragment.showLoader();
            mGeoCodingManager.getGeoCoding(this, query, location,limit);
        }
    }

    @Override
    public void getGeoCodeDataForward(String query) {
        isForCurrentLoc=false;
        if (mISuggestionFragment != null) {
            mISuggestionFragment.showLoader();
            mGeoCodingManager.getGeoCodeForward(this, query);
        }
    }

    @Override
    public void getGeoCodeDataReverse(double latitude, double longitude) {
        isForCurrentLoc=true;
        if (mISuggestionFragment != null) {
            mISuggestionFragment.showLoader();
            mGeoCodingManager.getGeoCodeReverse(this, latitude,longitude);
        }
    }

    @Override
    public void onSuccessDirection(DirectionsResponse data) {
        if (mISuggestionFragment != null) {
            mISuggestionFragment.hideLoader();
            mISuggestionFragment.onDirectionDataReceived(data);
        }
    }

    @Override
    public void onFailureDirection(@NonNull ErrorResponse errorResponse) {
        if (mISuggestionFragment != null) {
            mISuggestionFragment.hideLoader();
            mISuggestionFragment.onFailureDirection(errorResponse.getErrorMessage());
        }
    }

    @Override
    public void onSuccessGeoCoding(GeoCodingResponse data) {
        if (mISuggestionFragment != null) {
            mISuggestionFragment.hideLoader();
            mISuggestionFragment.onGeoDataDataReceived(data,isForCurrentLoc);
        }
    }

    @Override
    public void onFailureGeoCoding(@NonNull ErrorResponse errorResponse) {
        if (mISuggestionFragment != null) {
            mISuggestionFragment.hideLoader();
            mISuggestionFragment.onFailureGeoCoding(errorResponse.getErrorMessage());
        }
    }
    @Override
    public void disconnect() {
        if (mDirectionsManager != null) {
            mDirectionsManager.cancel();
        }
        if(mGeoCodingManager!=null){
            mGeoCodingManager.cancel();
        }

    }


}
