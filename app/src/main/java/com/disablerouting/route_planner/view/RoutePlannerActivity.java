package com.disablerouting.route_planner.view;

import android.location.Location;
import android.os.Bundle;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.map_base.MapBaseActivity;
import com.disablerouting.route_planner.SourceDestinationFragment;
import com.disablerouting.route_planner.adapter.CustomListAdapter;
import com.google.android.gms.maps.model.LatLng;
import org.osmdroid.util.GeoPoint;

public class RoutePlannerActivity extends MapBaseActivity implements OnSourceDestinationListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        SourceDestinationFragment sourceDestinationFragment = SourceDestinationFragment.newInstance(this);
        addFragment(R.id.contentContainer,sourceDestinationFragment,"");
    }
    private CustomListAdapter mAddressListAdapter;

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

    @Override
    public void onSourceCompleted(String addressModel) {

    }

    @Override
    public void onDestinationCompleted(String addressModel) {

    }

    @Override
    public void onGoSwapView(GeoPoint geoPointSource, GeoPoint geoPointDestination) {
        plotDataOfSourceDestination(null);

    }


    @OnClick(R.id.img_re_center)
    public void reCenter(){
        plotDataOfSourceDestination(null);
    }


}