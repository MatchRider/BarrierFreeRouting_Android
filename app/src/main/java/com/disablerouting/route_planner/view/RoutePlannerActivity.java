package com.disablerouting.route_planner.view;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.capture_option.view.CaptureActivity;
import com.disablerouting.common.AppConstant;
import com.disablerouting.geo_coding.model.Features;
import com.disablerouting.map_base.MapBaseActivity;
import com.disablerouting.route_planner.SourceDestinationFragment;
import com.disablerouting.route_planner.model.Steps;
import com.google.android.gms.maps.model.LatLng;
import org.osmdroid.util.GeoPoint;

import java.util.HashMap;
import java.util.List;

public class RoutePlannerActivity extends MapBaseActivity implements OnSourceDestinationListener {

    private SourceDestinationFragment mSourceDestinationFragment;
    private Features mFeaturesSourceAddress;
    private Features mFeaturesDestinationAddress;
    private String mSourceAddress;
    private String mDestinationAddress;
    private String mEncodedPolyline;
    private List<Steps> mStepsList;
    private HashMap<String, String> mHashMapObjectFilter;

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

        GeoPoint geoPointSource = null;
        GeoPoint geoPointDestination = null;
        if (mFeaturesSourceAddress != null && mFeaturesDestinationAddress != null) {
            geoPointSource = new GeoPoint(mFeaturesSourceAddress.getGeometry().getCoordinates().get(0),
                    mFeaturesSourceAddress.getGeometry().getCoordinates().get(1));

            geoPointDestination = new GeoPoint(mFeaturesDestinationAddress.getGeometry().getCoordinates().get(0),
                    mFeaturesDestinationAddress.getGeometry().getCoordinates().get(1));
            //mSourceDestinationFragment.callForDestination(geoPoint, geoPointSource, geoPointDestination);
        } else {
            plotDataOfSourceDestination(null, mSourceAddress, mDestinationAddress, null);
        }
    }


    @Override
    public void plotDataOnMap(String encodedString, List<Steps> stepsList) {
        if (encodedString != null && stepsList != null) {
            mEncodedPolyline = encodedString;
            mStepsList = stepsList;
            plotDataOfSourceDestination(mEncodedPolyline, mSourceAddress, mDestinationAddress, mStepsList);
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
        Intent intentFilter= new Intent(this,CaptureActivity.class);
        intentFilter.putExtra(AppConstant.IS_FILTER,true);
        startActivityForResult(intentFilter,AppConstant.REQUEST_CODE_CAPTURE);
    }


    @OnClick(R.id.img_re_center)
    public void reCenter() {
        clearItemsFromMap();
        if (mCurrentLocation != null && mSourceAddress != null && mDestinationAddress != null) {
            GeoPoint geoPoint = new GeoPoint(mCurrentLocation.longitude, mCurrentLocation.latitude);
            GeoPoint geoPointSource = null;
            GeoPoint geoPointDestination = null;
            if (mFeaturesSourceAddress != null && mFeaturesDestinationAddress != null) {
                geoPointSource = new GeoPoint(mFeaturesSourceAddress.getGeometry().getCoordinates().get(0),
                        mFeaturesSourceAddress.getGeometry().getCoordinates().get(1));

                geoPointDestination = new GeoPoint(mFeaturesDestinationAddress.getGeometry().getCoordinates().get(0),
                        mFeaturesDestinationAddress.getGeometry().getCoordinates().get(1));
                mSourceDestinationFragment.callForDestination(geoPoint, geoPointSource, geoPointDestination);
            }
        } else {
            plotDataOfSourceDestination(null, mSourceAddress, mDestinationAddress, null);
        }
    }

    @Override
    public void onFeedBackClick(double longitude, double latitude) {
        if(mSourceDestinationFragment!=null) {
            mSourceDestinationFragment.onFeedBackClick(longitude, latitude);
        }
    }

    @OnClick(R.id.btn_go)
    public void goPlotMap(){
        mSourceDestinationFragment.onGoAndPlotMap(mHashMapObjectFilter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppConstant.REQUEST_CODE_CAPTURE) {
            if(resultCode == Activity.RESULT_OK){
                mHashMapObjectFilter = (HashMap<String, String>)data.getSerializableExtra(AppConstant.DATA_FILTER);
            }
        }
    }

}