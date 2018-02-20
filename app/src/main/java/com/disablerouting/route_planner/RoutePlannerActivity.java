package com.disablerouting.route_planner;

import android.location.Location;
import android.os.Bundle;
import com.disablerouting.MapBaseActivity;
import com.disablerouting.R;

public class RoutePlannerActivity extends MapBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getView() {
        return R.layout.activity_route_planner;
    }


    @Override
    public void onSendLocation(Location location) {

    }
}