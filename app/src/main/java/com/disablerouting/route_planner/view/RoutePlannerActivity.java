package com.disablerouting.route_planner.view;

import android.location.Location;
import android.os.Bundle;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.geo_coding.model.Features;
import com.disablerouting.map_base.MapBaseActivity;
import com.disablerouting.route_planner.SourceDestinationFragment;
import com.google.android.gms.maps.model.LatLng;
import org.osmdroid.util.GeoPoint;

public class RoutePlannerActivity extends MapBaseActivity implements OnSourceDestinationListener {

    private SourceDestinationFragment mSourceDestinationFragment;
    private Features mFeaturesDestinationAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        mSourceDestinationFragment = SourceDestinationFragment.newInstance(this);
        addFragment(R.id.contentContainer,mSourceDestinationFragment,"");
    }

    @Override
    protected int getView() {
        return R.layout.activity_route_planner;
    }

    @Override
    protected void onUpdateLocation(Location location) {
        mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        GeoPoint geoPoint= new GeoPoint(mCurrentLocation.longitude,mCurrentLocation.latitude);
        GeoPoint geoPointDestination=null;
        if(mFeaturesDestinationAddress!=null) {
            geoPointDestination = new GeoPoint(mFeaturesDestinationAddress.getGeometry().getCoordinates().get(0),
                    mFeaturesDestinationAddress.getGeometry().getCoordinates().get(1));
            mSourceDestinationFragment.callForDestination(geoPoint, geoPointDestination);
        }else {
            plotDataOfSourceDestination(null);
        }
    }

    @Override
    public void onGoClick(GeoPoint geoPointSource, GeoPoint geoPointDestination) {
        showSnackBar("Ready to go", this);
    }

    @Override
    public void plotDataOnMap(String encodedString) {
        plotDataOfSourceDestination(encodedString);
    }

    @Override
    public void onBackPress() {
        finish();
    }

    @Override
    public void onGoSwapView(GeoPoint geoPointSource, GeoPoint geoPointDestination) {
        plotDataOfSourceDestination(null);

    }

    @Override
    public void onSourceDestinationSelected(Features featuresSource, Features featuresDestination) {
       if(featuresDestination!=null){
           mFeaturesDestinationAddress= featuresDestination;
       }
    }

    @OnClick(R.id.img_re_center)
    public void reCenter(){
        plotDataOfSourceDestination(null);
    }


}