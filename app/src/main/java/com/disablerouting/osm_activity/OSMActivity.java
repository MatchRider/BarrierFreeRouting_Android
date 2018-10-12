package com.disablerouting.osm_activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.disablerouting.R;
import com.disablerouting.base.BaseActivityImpl;
import com.disablerouting.osm_activity.manager.OSMManager;
import com.disablerouting.osm_activity.model.GetOsmData;
import com.disablerouting.osm_activity.presenter.IOSMScreenPresenter;
import com.disablerouting.osm_activity.presenter.IOSMView;
import com.disablerouting.osm_activity.presenter.OsmActivityPresenter;
import com.disablerouting.utils.Utility;

public class OSMActivity extends BaseActivityImpl implements IOSMView {
    IOSMScreenPresenter mIOSMScreenPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_osm);

        mIOSMScreenPresenter = new OsmActivityPresenter(this,new OSMManager(),this);
        mIOSMScreenPresenter.getOSM();
    }

    @Override
    public void onOSMDataReceived(String responseBody) {
        if(responseBody!=null) {
            GetOsmData getOsmData=Utility.convertDataIntoModel(responseBody);
            Log.e("Nodes", String.valueOf(getOsmData.getOSM().getNode().size()));
            Log.e("Ways", String.valueOf(getOsmData.getOSM().getWays().size()));
        }

    }

    @Override
    public void onFailure(String error) {
        Toast.makeText(this,error,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoader() {
        showProgress();
    }

    @Override
    public void hideLoader() {
        hideProgress();
    }
}

