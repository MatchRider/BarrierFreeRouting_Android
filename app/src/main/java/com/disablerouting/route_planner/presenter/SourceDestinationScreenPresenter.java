package com.disablerouting.route_planner.presenter;


import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.disablerouting.api.ErrorResponse;
import com.disablerouting.geo_coding.manager.GeoCodingManager;
import com.disablerouting.geo_coding.model.GeoCodingResponse;
import com.disablerouting.geo_coding.presenter.IGeoCodingResponseReceiver;
import com.disablerouting.route_planner.manager.DirectionsManager;
import com.disablerouting.route_planner.manager.NodeManager;
import com.disablerouting.route_planner.model.DirectionsResponse;
import com.disablerouting.route_planner.model.NodeResponse;
import com.disablerouting.route_planner.view.ISourceDestinationViewFragment;
import org.json.JSONObject;

public class SourceDestinationScreenPresenter implements ISourceDestinationScreenPresenter,
        IDirectionsResponseReceiver , IGeoCodingResponseReceiver , INodesResponseReceiver {

    private ISourceDestinationViewFragment mISourceDestinationViewFragment;
    private DirectionsManager mDirectionsManager;
    private GeoCodingManager mGeoCodingManager;
    private NodeManager mNodeManager;
    private boolean isForCurrentLoc;

    public SourceDestinationScreenPresenter(ISourceDestinationViewFragment directionsViewFragment,
                                            DirectionsManager directionsManager , GeoCodingManager geoCodingManager, NodeManager nodeManager) {
        mISourceDestinationViewFragment = directionsViewFragment;
        mDirectionsManager = directionsManager;
        mGeoCodingManager = geoCodingManager;
        mNodeManager = nodeManager;
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
    public void getGeoCodeDataForward(String query) {
        isForCurrentLoc=false;
        if (mISourceDestinationViewFragment != null) {
            mISourceDestinationViewFragment.showLoader();
            mGeoCodingManager.getGeoCodeForward(this, query);
        }
    }

    @Override
    public void getGeoCodeDataReverse(double latitude, double longitude) {
        isForCurrentLoc=true;
        if (mISourceDestinationViewFragment != null) {
            mISourceDestinationViewFragment.showLoader();
            mGeoCodingManager.getGeoCodeReverse(this, latitude,longitude);
        }
    }

    @Override
    public void getNodesData(String bBox) {
        if (mISourceDestinationViewFragment != null) {
            mISourceDestinationViewFragment.showLoader();
            mNodeManager.getNodes(this, bBox);
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

    @Override
    public void onSuccessNodes(NodeResponse data) {
        if (mISourceDestinationViewFragment != null) {
            mISourceDestinationViewFragment.hideLoader();
            mISourceDestinationViewFragment.onNodeDataReceived(data);
        }
    }

    @Override
    public void onFailureNodes(@NonNull ErrorResponse errorResponse) {
        if (mISourceDestinationViewFragment != null) {
            mISourceDestinationViewFragment.hideLoader();
            mISourceDestinationViewFragment.onFailureNode(errorResponse.getErrorMessage());
        }
    }
}
