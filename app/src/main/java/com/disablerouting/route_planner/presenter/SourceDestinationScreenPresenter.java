package com.disablerouting.route_planner.presenter;


import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.disablerouting.api.ErrorResponse;
import com.disablerouting.geo_coding.manager.GeoCodingManager;
import com.disablerouting.geo_coding.model.GeoCodingResponse;
import com.disablerouting.geo_coding.presenter.IGeoCodingResponseReceiver;
import com.disablerouting.route_planner.manager.DirectionsManager;
import com.disablerouting.route_planner.model.DirectionsResponse;
import com.disablerouting.route_planner.view.ISourceDestinationViewFragment;
import org.json.JSONObject;

public class SourceDestinationScreenPresenter implements ISourceDestinationScreenPresenter,
        IDirectionsResponseReceiver , IGeoCodingResponseReceiver {

    private ISourceDestinationViewFragment mISourceDestinationViewFragment;
    private DirectionsManager mDirectionsManager;
    private GeoCodingManager mGeoCodingManager;
    private boolean isForCurrentLoc;

    public SourceDestinationScreenPresenter(ISourceDestinationViewFragment directionsViewFragment,
                                            DirectionsManager directionsManager , GeoCodingManager geoCodingManager) {
        mISourceDestinationViewFragment = directionsViewFragment;
        mDirectionsManager = directionsManager;
        mGeoCodingManager = geoCodingManager;
    }

    @Override
    public void getDestinationsData(String coordinates, String profile , JSONObject jsonObject) {
        if (mISourceDestinationViewFragment != null) {
            mISourceDestinationViewFragment.showLoader();
            mDirectionsManager.getDestination(this, coordinates, profile , jsonObject);
        }
    }

    @Override
    public void getCoordinatesData(String query, String location,int limit) {
        isForCurrentLoc=!TextUtils.isEmpty(location);
        if (mISourceDestinationViewFragment != null) {
            mISourceDestinationViewFragment.showLoader();
            mGeoCodingManager.getGeoCoding(this, query, location,limit);
        }
    }

    @Override
    public void onSuccessDirection(DirectionsResponse data) {
        if (mISourceDestinationViewFragment != null) {
            mISourceDestinationViewFragment.hideLoader();
            mISourceDestinationViewFragment.onDirectionDataReceived(data);
        }
    }

    @Override
    public void onFailureDirection(@NonNull ErrorResponse errorResponse) {
        if (mISourceDestinationViewFragment != null) {
            mISourceDestinationViewFragment.hideLoader();
            mISourceDestinationViewFragment.onFailureDirection(errorResponse.getErrorMessage());
        }
    }

    @Override
    public void onSuccessGeoCoding(GeoCodingResponse data) {
        if (mISourceDestinationViewFragment != null) {
            mISourceDestinationViewFragment.hideLoader();
            mISourceDestinationViewFragment.onGeoDataDataReceived(data,isForCurrentLoc);
        }
    }

    @Override
    public void onFailureGeoCoding(@NonNull ErrorResponse errorResponse) {
        if (mISourceDestinationViewFragment != null) {
            mISourceDestinationViewFragment.hideLoader();
            mISourceDestinationViewFragment.onFailureGeoCoding(errorResponse.getErrorMessage());
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
