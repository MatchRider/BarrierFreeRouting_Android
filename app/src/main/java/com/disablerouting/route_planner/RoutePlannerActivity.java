package com.disablerouting.route_planner;

import android.location.Location;
import android.os.Bundle;
import butterknife.ButterKnife;
import com.disablerouting.NewMapBaseActivity;
import com.disablerouting.R;
import com.google.android.gms.maps.model.LatLng;
import org.osmdroid.util.GeoPoint;

public class RoutePlannerActivity extends NewMapBaseActivity implements OnSourceDestinationListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        SourceDestinationFragment sourceDestinationFragment = SourceDestinationFragment.newInstance(this);
        addFragment(R.id.contentContainer,sourceDestinationFragment,"");
    }

    @Override
    protected int getView() {
        return R.layout.activity_route_planner;
    }

    @Override
    protected void onUpdateLocation(Location location) {
        mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onGoClick(GeoPoint geoPointSource, GeoPoint geoPointDestination) {
        showSnackBar("Ready to go", this);
    }

    @Override
    public void plotDataOnMap(String encodedString) {
        //initializeData();
        plotDataOfSourceDestination(encodedString);
    }

    @Override
    public void onBackPress() {
        finish();
    }
}