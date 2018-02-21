package com.disablerouting.route_planner.presenter;


import android.support.annotation.NonNull;
import com.disablerouting.api.ErrorResponse;
import com.disablerouting.route_planner.manager.DirectionsManager;
import com.disablerouting.route_planner.model.DirectionsResponse;
import com.disablerouting.route_planner.view.IDirectionsViewFragment;

public class DirectionsPresenter implements IDirectionsScreenPresenter, IDirectionsResponseReceiver {


    private IDirectionsViewFragment mIDirectionsViewFragment;
    private DirectionsManager mDirectionsManager;

    public DirectionsPresenter(IDirectionsViewFragment directionsViewFragment, DirectionsManager directionsManager) {
        mIDirectionsViewFragment = directionsViewFragment;
        mDirectionsManager = directionsManager;
    }
    @Override
    public void getDestinationsData(String coordinates,String profile) {
        mIDirectionsViewFragment.showLoader();
        mDirectionsManager.getDestination(this,coordinates,profile);
    }

    @Override
    public void onSuccess(DirectionsResponse data) {
        mIDirectionsViewFragment.hideLoader();
        mIDirectionsViewFragment.onDirectionDataReceived(data);
    }

    @Override
    public void onFailure(@NonNull ErrorResponse errorResponse) {
        mIDirectionsViewFragment.hideLoader();
        mIDirectionsViewFragment.onFailure(errorResponse.getErrorMessage());
    }
    @Override
    public void disconnect() {
        if (mDirectionsManager != null) {
            mDirectionsManager.cancel();
        }
    }

}
