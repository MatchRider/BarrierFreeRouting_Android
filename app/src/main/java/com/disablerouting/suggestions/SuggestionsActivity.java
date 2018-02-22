package com.disablerouting.suggestions;

import android.location.Location;
import android.os.Bundle;
import com.disablerouting.NewMapBaseActivity;
import com.disablerouting.R;
import com.google.android.gms.maps.model.LatLng;

public class SuggestionsActivity extends NewMapBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getView() {
        return R.layout.activity_route_planner;
    }

    @Override
    protected void onUpdateLocation(Location location) {
        mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());

    }

}
