package com.disablerouting.route_planner.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.common.AppConstant;
import com.disablerouting.filter.view.FilterActivity;
import com.disablerouting.geo_coding.model.Features;
import com.disablerouting.map_base.MapBaseActivity;
import com.disablerouting.route_planner.model.NodeItem;
import com.disablerouting.route_planner.model.Steps;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoutePlannerActivity extends MapBaseActivity implements OnSourceDestinationListener {

    private SourceDestinationFragment mSourceDestinationFragment;
    private Features mFeaturesSourceAddress;
    private Features mFeaturesDestinationAddress;
    private String mSourceAddress;
    private String mDestinationAddress;
    private JSONObject mJsonObjectFilter=null;

    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, Integer> mHashMapObjectFilterItem = new HashMap<>();
    private List<NodeItem> mNodeItemListFiltered = new ArrayList<>();
    private HashMap<String, Features> mHashMapObjectFilterRoutingVia= new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        mSourceDestinationFragment = SourceDestinationFragment.newInstance(this);
        addFragment(R.id.contentContainer, mSourceDestinationFragment, "");

    }

    @Override
    protected int getView() {
        return R.layout.activity_route_planner;
    }

    @Override
    protected void onUpdateLocation(Location location) {
        mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        GeoPoint geoPoint = new GeoPoint(mCurrentLocation.longitude, mCurrentLocation.latitude);
        mSourceDestinationFragment.onUpdateLocation(geoPoint);
        if (mFeaturesSourceAddress != null && mFeaturesDestinationAddress != null) {
            GeoPoint  geoPointSource = new GeoPoint(mFeaturesSourceAddress.getGeometry().getCoordinates().get(0),
                    mFeaturesSourceAddress.getGeometry().getCoordinates().get(1));

            GeoPoint geoPointDestination = new GeoPoint(mFeaturesDestinationAddress.getGeometry().getCoordinates().get(0),
                    mFeaturesDestinationAddress.getGeometry().getCoordinates().get(1));
            //mSourceDestinationFragment.callForDestination(geoPoint, geoPointSource, geoPointDestination);
        } else {
            plotDataOfSourceDestination(null, mSourceAddress, mDestinationAddress, null);
        }
    }


    @Override
    public void plotDataOnMap(String encodedString, List<Steps> stepsList) {
        if (encodedString != null && stepsList != null) {
            plotDataOfSourceDestination(encodedString, mSourceAddress, mDestinationAddress, stepsList);
        }
    }

    @Override
    public void onBackPress() {
        finish();
    }

    @Override
    public void onSourceDestinationSelected(Features featuresSource, Features featuresDestination) {
        if (featuresSource != null && featuresDestination != null && featuresSource.getProperties() != null
                && featuresDestination.getProperties() != null) {
            mFeaturesSourceAddress = featuresSource;
            mSourceAddress = featuresSource.getProperties().toString();
            mFeaturesDestinationAddress = featuresDestination;
            mDestinationAddress = featuresDestination.getProperties().toString();
        }
    }

    @Override
    public void onApplyFilter() {
        Intent intentFilter = new Intent(this, FilterActivity.class);
        intentFilter.putExtra(AppConstant.IS_FILTER, true);
        intentFilter.putExtra(AppConstant.DATA_FILTER_SELECTED, mHashMapObjectFilterItem);
        intentFilter.putExtra(AppConstant.DATA_FILTER_ROUTING_VIA, mHashMapObjectFilterRoutingVia);
        startActivityForResult(intentFilter, AppConstant.REQUEST_CODE_CAPTURE);
    }

    @Override
    public void plotNodesOnMap(List<NodeItem> mNodes) {
        for (NodeItem nodeItem : mNodes) {
            if (nodeItem.getNodeType() != null && nodeItem.getNodeType().getIdentifier() != null &&
                    nodeItem.getNodeType().getIdentifier().contains(AppConstant.publicTramStop) ||
                    nodeItem.getNodeType().getIdentifier().contains(AppConstant.publicToilets) ||
                    nodeItem.getNodeType().getIdentifier().contains(AppConstant.publicBusStop)) {
                mNodeItemListFiltered.add(nodeItem);
            }
        }
        plotDataOfNodes(mNodeItemListFiltered);
        mNodeItemListFiltered.clear();

    }

    @Override
    public void onSwapData() {
        clearItemsFromMap();
    }

    @Override
    public void plotMidWayRouteMarker(GeoPoint geoPoint) {
        addMidWayMarkers(geoPoint,"Way Point");
    }


    @OnClick(R.id.img_re_center)
    public void reCenter() {
        addCurrentLocation();
    }

    @Override
    public void onFeedBackClick(double longitude, double latitude) {
        if (mSourceDestinationFragment != null) {
            mSourceDestinationFragment.onFeedBackClick(longitude, latitude);
        }
    }

    @OnClick(R.id.btn_go)
    public void goPlotMap() {
        clearItemsFromMap();
        Features features = mHashMapObjectFilterRoutingVia.get(AppConstant.DATA_FILTER_ROUTING_VIA);
        mSourceDestinationFragment.plotRoute(mJsonObjectFilter, features);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppConstant.REQUEST_CODE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                HashMap<String, String> hashMapObjectFilter = (HashMap<String, String>) data.getSerializableExtra(AppConstant.DATA_FILTER);
                mHashMapObjectFilterItem = (HashMap<Integer, Integer>) data.getSerializableExtra(AppConstant.DATA_FILTER_SELECTED);
                mHashMapObjectFilterRoutingVia = (HashMap<String, Features>) data.getSerializableExtra(AppConstant.DATA_FILTER_ROUTING_VIA);

                if (mHashMapObjectFilterItem != null && mHashMapObjectFilterItem.size() != 0) {
                    mJsonObjectFilter= new JSONObject();
                    JSONObject jsonObjectProfileParams = new JSONObject();
                    JSONObject restrictions = new JSONObject();

                    try {
                        for (Map.Entry<String, String> entry : hashMapObjectFilter.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();
                            restrictions.put(key, value);
                        }
                        jsonObjectProfileParams.put("restrictions", restrictions);
                        mJsonObjectFilter.put("profile_params", jsonObjectProfileParams);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    mJsonObjectFilter = null;
                }
            }
        }
    }

}