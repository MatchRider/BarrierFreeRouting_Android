package com.disablerouting.suggestions.view;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.common.AppConstant;
import com.disablerouting.curd_operations.model.RequestGetWay;
import com.disablerouting.feedback.view.FeedbackActivity;
import com.disablerouting.geo_coding.model.Features;
import com.disablerouting.map_base.MapBaseActivity;
import com.disablerouting.route_planner.model.*;
import com.disablerouting.utils.Utility;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polyline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
    private List<Way> mWayListEven= new ArrayList<>();
    private List<Way> mWayListOdd= new ArrayList<>();
    private HashMap<String, Node> mNodeHashMap = new HashMap<>();
    private int mButtonSelected=1;
    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        mSuggestionFragment = SuggestionFragment.newInstance(this);
        addFragment(R.id.contentContainer, mSuggestionFragment, "");
        mBtnGo.setVisibility(View.GONE);
        String data = Utility.readOSMFile(this);
        convertDataIntoModel(data);
    }

    private void convertDataIntoModel(String data) {
        JSONObject jsonObject = Utility.convertXMLtoJSON(data);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
            OSMData OSMData = objectMapper.readValue(jsonObject.toString(), com.disablerouting.route_planner.model.OSMData.class);
            for (int i = 0; i < OSMData.getOSM().getNode().size(); i++) {
                mNodeHashMap.put(OSMData.getOSM().getNode().get(i).getID(), OSMData.getOSM().getNode().get(i));
            }
            for (int i = 0; i < OSMData.getOSM().getWay().size(); i++) {
                if(i%2==0) {
                    mWayListEven.add(OSMData.getOSM().getWay().get(i));
                }else {
                    mWayListOdd.add(OSMData.getOSM().getWay().get(i));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
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
            PlotWayDataTask mPlotWayDataTaskNotValidated = new PlotWayDataTask();
            mPlotWayDataTaskNotValidated.execute();

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
    public void plotDataOnMap(List<List<Double>> encodedString, List<Steps> stepsList) {
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

    @SuppressLint("StaticFieldLeak")
    private class PlotWayDataTask extends AsyncTask<Void, ProgressModel, List<WayCustomModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SuggestionsActivity.this);
            pDialog.setMessage("Please Wait...");
            pDialog.setCancelable(false);
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            } else {
                pDialog.show();
            }
        }

        @Override
        protected void onPostExecute(List<WayCustomModel> aVoid) {
            super.onPostExecute(aVoid);
            setBoundingBox(aVoid.get(0).getGeoPoint().get(0), aVoid.get(aVoid.size() - 1).getGeoPoint().get(0));
            pDialog.dismiss();

        }

        @Override
        protected void onProgressUpdate(ProgressModel... values) {
            ProgressModel model = values[0];
            addPolyLineForWays(model.getGeoPointList(), model.getWayCustomModel() ,model.isValid());

        }

        @Override
        protected List<WayCustomModel> doInBackground(Void... params) {
            try {
                GeoPoint start;
                final List<WayCustomModel> wayCustomModelList = new ArrayList<>();

                if(mButtonSelected==2) {
                    wayCustomModelList.clear();
                    //For Way Data even
                    for (int i = 0; i < mWayListEven.size(); i++) {
                        List<Node> nodeList = new ArrayList<>();
                        List<GeoPoint> geoPointArrayList = new ArrayList<>();
                        final WayCustomModel wayCustomModel = new WayCustomModel();
                        for (int j = 0; j < mWayListEven.get(i).getNdList().size(); j++) {
                            String refNode = mWayListEven.get(i).getNdList().get(j).getRef();
                            nodeList.add(mNodeHashMap.get(refNode));
                            geoPointArrayList.add(new GeoPoint(Double.parseDouble(mNodeHashMap.get(refNode).getLatitude()),
                                    Double.parseDouble(mNodeHashMap.get(refNode).getLongitude())));
                            wayCustomModel.setGeoPoint(geoPointArrayList);

                        }
                        wayCustomModel.setId(mWayListEven.get(i).getID());
                        wayCustomModel.setTag(mWayListEven.get(i).getTagList());
                        wayCustomModel.setNode(nodeList);
                        wayCustomModel.setGeoPoint(geoPointArrayList);
                        wayCustomModelList.add(wayCustomModel);
                        start = null;
                        if (i == 0)
                            start = geoPointArrayList.get(0);

                        final GeoPoint finalStart = start;
                        publishProgress(new ProgressModel(wayCustomModel.getGeoPoint(), finalStart, wayCustomModel,true));

                    }
                }

                if(mButtonSelected==1) {
                    //For Way Data off
                    wayCustomModelList.clear();

                    for (int i = 0; i < mWayListOdd.size(); i++) {
                        List<Node> nodeList = new ArrayList<>();
                        List<GeoPoint> geoPointArrayList = new ArrayList<>();
                        final WayCustomModel wayCustomModel = new WayCustomModel();
                        for (int j = 0; j < mWayListOdd.get(i).getNdList().size(); j++) {
                            String refNode = mWayListOdd.get(i).getNdList().get(j).getRef();
                            nodeList.add(mNodeHashMap.get(refNode));
                            geoPointArrayList.add(new GeoPoint(Double.parseDouble(mNodeHashMap.get(refNode).getLatitude()),
                                    Double.parseDouble(mNodeHashMap.get(refNode).getLongitude())));
                            wayCustomModel.setGeoPoint(geoPointArrayList);

                        }
                        wayCustomModel.setId(mWayListOdd.get(i).getID());
                        wayCustomModel.setTag(mWayListOdd.get(i).getTagList());
                        wayCustomModel.setNode(nodeList);
                        wayCustomModel.setGeoPoint(geoPointArrayList);
                        wayCustomModelList.add(wayCustomModel);
                        start = null;
                        if (i == 0)
                            start = geoPointArrayList.get(0);

                        final GeoPoint finalStart = start;
                        publishProgress(new ProgressModel(wayCustomModel.getGeoPoint(), finalStart, wayCustomModel , false));

                    }
                }
                return wayCustomModelList;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void onTabClicked(int position) {
        clearItemsFromMap();
        addCurrentLocation();
        if(position==1){
            mButtonSelected=1;
            PlotWayDataTask mPlotWayDataTaskNotValidated = new PlotWayDataTask();
            mPlotWayDataTaskNotValidated.execute();
        }else {
            mButtonSelected=2;
            PlotWayDataTask mPlotWayDataTaskValidated = new PlotWayDataTask();
            mPlotWayDataTaskValidated.execute();
        }
    }

    @Override
    public void checkForWay(Polyline polyline, String way) {
        super.checkForWay(polyline, way);
        RequestGetWay requestGetWay = new RequestGetWay();
        requestGetWay.setStringWay(way);
        mSuggestionFragment.getWays(requestGetWay);

    }

}
