package com.disablerouting.route_planner;

import android.location.Location;
import android.os.Bundle;
import butterknife.ButterKnife;
import com.disablerouting.MapBaseActivity;
import com.disablerouting.R;
import org.osmdroid.util.GeoPoint;

public class RoutePlannerActivity extends MapBaseActivity implements OnSourceDestinationListener {


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
    public void onSendLocation(Location location) {

    }


    @Override
    public void onGoClick(GeoPoint geoPointSource, GeoPoint geoPointDestination) {
        showSnackBar("Ready to go", this);
        //TODO api call when clicked
        initializeData();
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