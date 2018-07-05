package com.disablerouting.suggestions.view;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.common.AppConstant;
import com.disablerouting.feedback.view.FeedbackActivity;
import com.disablerouting.geo_coding.model.Features;
import com.disablerouting.map_base.MapBaseActivity;
import com.disablerouting.route_planner.model.FeedBackModel;
import com.disablerouting.route_planner.model.Steps;
import com.google.android.gms.maps.model.LatLng;
import org.osmdroid.util.GeoPoint;

import java.util.List;

public class SuggestionsActivity extends MapBaseActivity implements OnSuggestionListener {

    private SuggestionFragment mSuggestionFragment;
    private Features mFeaturesSourceAddress;
    private Features mFeaturesDestinationAddress;
    private String mSourceAddress;
    private String mDestinationAddress;

    @BindView(R.id.btn_go)
    Button mBtnGo;

    private boolean mIsUpdateAgain = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        mSuggestionFragment = SuggestionFragment.newInstance(this);
        addFragment(R.id.contentContainer, mSuggestionFragment, "");
        mBtnGo.setVisibility(View.GONE);
    }

    @Override
    protected int getView() {
        return R.layout.activity_route_planner;
    }

    @Override
    protected void onUpdateLocation(Location location) {
        if(!mIsUpdateAgain) {

            mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            GeoPoint geoPoint = new GeoPoint(mCurrentLocation.longitude, mCurrentLocation.latitude);
            mSuggestionFragment.onUpdateLocation(geoPoint);
            if (mFeaturesSourceAddress != null && mFeaturesDestinationAddress != null) {
                GeoPoint geoPointSource = new GeoPoint(mFeaturesSourceAddress.getGeometry().getCoordinates().get(0),
                        mFeaturesSourceAddress.getGeometry().getCoordinates().get(1));

                GeoPoint geoPointDestination = new GeoPoint(mFeaturesDestinationAddress.getGeometry().getCoordinates().get(0),
                        mFeaturesDestinationAddress.getGeometry().getCoordinates().get(1));
                //mSourceDestinationFragment.callForDestination(geoPoint, geoPointSource, geoPointDestination);
            } else {
                plotDataOfSourceDestination(null, mSourceAddress, mDestinationAddress, null, false);
            }
            mIsUpdateAgain=true;
        }
    }

    @OnClick(R.id.img_re_center)
    public void reCenter() {
        addCurrentLocation();
    }

    @Override
    public void onFeedBackClick(double longitude, double latitude) {
        //Disable polyline click here
    }

    @Override
    public void onMapPlotted() {

    }

    @Override
    public void plotDataOnMap(String encodedString, List<Steps> stepsList) {
        if (encodedString != null && stepsList != null) {
            plotDataOfSourceDestination(encodedString, mSourceAddress, mDestinationAddress, stepsList, false);
        }
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
    public void onBackPress() {
        finish();
    }

    @Override
    public void onGoButtonVisibility(boolean visible) {
        if (visible) {
            mBtnGo.setVisibility(View.VISIBLE);
        } else {
            mBtnGo.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPlotRouteWhenDestinationSelected() {
        clearItemsFromMap();
        mSuggestionFragment.plotRoute();
    }

    @Override
    public void onClearItemsOfMap() {
        clearItemsFromMap();
    }

    @Override
    public void onNoClickAddLocation() {
        addCurrentLocation();
    }

    @OnClick(R.id.btn_go)
    public void onRedirect() {
        onGoClick(mCurrentLocation.longitude,mCurrentLocation.latitude);
    }

    private void onGoClick(double longitude, double latitude){
        FeedBackModel feedBackModel = new FeedBackModel(latitude, longitude);
        Intent intentFeedback = new Intent(this, FeedbackActivity.class);
        intentFeedback.putExtra(AppConstant.FEED_BACK_MODEL, feedBackModel);
        intentFeedback.putExtra(AppConstant.STARTED_FROM_SUGGESTION,true);
        startActivity(intentFeedback);
    }

}
