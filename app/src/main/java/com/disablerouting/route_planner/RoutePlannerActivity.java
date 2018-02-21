package com.disablerouting.route_planner;

import android.location.Location;
import android.os.Bundle;
import butterknife.ButterKnife;
import com.disablerouting.MapBaseActivity;
import com.disablerouting.R;

public class RoutePlannerActivity extends MapBaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        addFragment(R.id.contentContainer,new SourceDestinationFragment(),"");
    }

    @Override
    protected int getView() {
        return R.layout.activity_route_planner;
    }


    @Override
    public void onSendLocation(Location location) {

    }



}