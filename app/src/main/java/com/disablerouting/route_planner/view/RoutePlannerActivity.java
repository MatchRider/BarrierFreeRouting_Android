package com.disablerouting.route_planner.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.common.AppConstant;
import com.disablerouting.filter.view.FilterActivity;
import com.disablerouting.geo_coding.model.Features;
import com.disablerouting.map_base.MapBaseActivity;
import com.disablerouting.route_planner.model.*;
import com.disablerouting.utils.Utility;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoutePlannerActivity extends MapBaseActivity implements OnSourceDestinationListener {

    private SourceDestinationFragment mSourceDestinationFragment;
    private Features mFeaturesSourceAddress;
    private Features mFeaturesDestinationAddress;
    private String mSourceAddress;
    private String mDestinationAddress;
    private JSONObject mJsonObjectFilter = null;

    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, Integer> mHashMapObjectFilterItem = new HashMap<>();
    private List<NodeItem> mNodeItemListFiltered = new ArrayList<>();
    private HashMap<String, Features> mHashMapObjectFilterRoutingVia = new HashMap<>();

    @BindView(R.id.btn_go)
    Button mButtonGo;

    @BindView(R.id.toggle)
    SwitchCompat mButtonToggle;

    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    private boolean mISMapPlotted = false;
    private boolean mIsUpdateAgain = false;
    private OSMData mOSMData;
    private HashMap<String, Node> mNodeHashMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        mSourceDestinationFragment = SourceDestinationFragment.newInstance(this);
        addFragment(R.id.contentContainer, mSourceDestinationFragment, "");

        String data = readOSMFile();
        convertDataIntoModel(data);
    }

    @Override
    protected int getView() {
        return R.layout.activity_route_planner;
    }

    @Override
    protected void onUpdateLocation(Location location) {
        if (!mIsUpdateAgain) {
            mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            GeoPoint geoPoint = new GeoPoint(mCurrentLocation.longitude, mCurrentLocation.latitude);
            mSourceDestinationFragment.onUpdateLocation(geoPoint);
            if (mFeaturesSourceAddress != null && mFeaturesDestinationAddress != null) {
                GeoPoint geoPointSource = new GeoPoint(mFeaturesSourceAddress.getGeometry().getCoordinates().get(0),
                        mFeaturesSourceAddress.getGeometry().getCoordinates().get(1));

                GeoPoint geoPointDestination = new GeoPoint(mFeaturesDestinationAddress.getGeometry().getCoordinates().get(0),
                        mFeaturesDestinationAddress.getGeometry().getCoordinates().get(1));
                //mSourceDestinationFragment.callForDestination(geoPoint, geoPointSource, geoPointDestination);
            } else {
                plotDataOfSourceDestination(null, mSourceAddress, mDestinationAddress, null, true);
            }
            mIsUpdateAgain = true;
        }

    }


    @Override
    public void plotDataOnMap(String encodedString, List<Steps> stepsList) {
        if (encodedString != null && stepsList != null) {
            plotDataOfSourceDestination(encodedString, mSourceAddress, mDestinationAddress, stepsList, true);
        }
    }

    @Override
    public void onBackPress() {
        finish();
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
    public void onApplyFilter() {
        Intent intentFilter = new Intent(this, FilterActivity.class);
        intentFilter.putExtra(AppConstant.IS_FILTER, true);
        intentFilter.putExtra(AppConstant.DATA_FILTER_SELECTED, mHashMapObjectFilterItem);
        intentFilter.putExtra(AppConstant.DATA_FILTER_ROUTING_VIA, mHashMapObjectFilterRoutingVia);
        startActivityForResult(intentFilter, AppConstant.REQUEST_CODE_CAPTURE);
    }

    @Override
    public void plotNodesOnMap(List<NodeItem> mNodes) {
        for (NodeItem nodeItem : mNodes) {
            if (nodeItem.getNodeType() != null && nodeItem.getNodeType().getIdentifier() != null &&
                    nodeItem.getNodeType().getIdentifier().contains(AppConstant.publicTramStop) ||
                    nodeItem.getNodeType().getIdentifier().contains(AppConstant.publicToilets) ||
                    nodeItem.getNodeType().getIdentifier().contains(AppConstant.publicBusStop)) {
                mNodeItemListFiltered.add(nodeItem);
            }
        }
        plotDataOfNodes(mNodeItemListFiltered);
        mNodeItemListFiltered.clear();

    }

    @Override
    public void onSwapData() {
        clearItemsFromMap();
        mButtonGo.setVisibility(View.VISIBLE);
        mButtonGo.setClickable(true);
        mButtonGo.setText(R.string.go);

    }

    @Override
    public void plotMidWayRouteMarker(GeoPoint geoPoint) {
        Features value = mHashMapObjectFilterRoutingVia.get(AppConstant.DATA_FILTER_ROUTING_VIA);
        if (value != null) {
            addMidWayMarkers(geoPoint, value.getProperties().toString());
        }
    }

    @Override
    public void onSourceClickWhileNavigationRunning() {
        if (mISMapPlotted) {
            mISMapPlotted = false;
            mButtonGo.setVisibility(View.VISIBLE);
            mButtonGo.setClickable(true);
            mButtonGo.setText(R.string.go);
            stopRunningMarker();
        }
    }

    @Override
    public void onDestinationClickWhileNavigationRunning() {
        if (mISMapPlotted) {
            mISMapPlotted = false;
            mButtonGo.setVisibility(View.VISIBLE);
            mButtonGo.setClickable(true);
            mButtonGo.setText(R.string.go);
            stopRunningMarker();
        }
    }


    @OnClick(R.id.img_re_center)
    public void reCenter() {
        addCurrentLocation();
    }

    @Override
    public void onFeedBackClick(double longitude, double latitude) {
        if (mSourceDestinationFragment != null) {
            mSourceDestinationFragment.onFeedBackClick(longitude, latitude);
        }
    }

    @Override
    public void onMapPlotted() {
        mButtonGo.setText(R.string.start);
        mISMapPlotted = true;

    }

    @OnClick(R.id.btn_go)
    public void goPlotMap() {
        if (mISMapPlotted) {
            mButtonGo.setVisibility(View.GONE);
            mButtonGo.setClickable(false);
            UI_HANDLER.post(updateMarker);
        } else {
            mButtonGo.setVisibility(View.VISIBLE);
            mButtonGo.setClickable(true);
            mButtonGo.setText(R.string.go);
            clearItemsFromMap();
            Features features = mHashMapObjectFilterRoutingVia.get(AppConstant.DATA_FILTER_ROUTING_VIA);
            mSourceDestinationFragment.plotRoute(mJsonObjectFilter, features);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppConstant.REQUEST_CODE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                HashMap<String, String> hashMapObjectFilter = (HashMap<String, String>) data.getSerializableExtra(AppConstant.DATA_FILTER);
                mHashMapObjectFilterItem = (HashMap<Integer, Integer>) data.getSerializableExtra(AppConstant.DATA_FILTER_SELECTED);
                mHashMapObjectFilterRoutingVia = (HashMap<String, Features>) data.getSerializableExtra(AppConstant.DATA_FILTER_ROUTING_VIA);

                if (mHashMapObjectFilterItem != null && mHashMapObjectFilterItem.size() != 0) {
                    mJsonObjectFilter = new JSONObject();
                    JSONObject jsonObjectProfileParams = new JSONObject();
                    JSONObject restrictions = new JSONObject();

                    try {
                        for (Map.Entry<String, String> entry : hashMapObjectFilter.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();
                            restrictions.put(key, value);
                        }
                        jsonObjectProfileParams.put("restrictions", restrictions);
                        mJsonObjectFilter.put("profile_params", jsonObjectProfileParams);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    mJsonObjectFilter = null;
                }
            }
        }
    }

    @OnClick(R.id.toggle)
    public void onToggle() {
        if (mButtonToggle.isChecked()) {
            new MyTask().execute();

        } else {
            clearItemsFromMap();
            Toast.makeText(this, "NormalMap", Toast.LENGTH_SHORT).show();
        }

    }

    private void convertDataIntoModel(String data) {
        JSONObject jsonObject = Utility.convertXMLtoJSON(data);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
            mOSMData = objectMapper.readValue(jsonObject.toString(), OSMData.class);
            for (int i = 0; i < mOSMData.getOSM().getNode().size(); i++) {
                mNodeHashMap.put(mOSMData.getOSM().getNode().get(i).getID(), mOSMData.getOSM().getNode().get(i));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readOSMFile() {
        InputStream input = null;
        try {
            input = getAssets().open("Befahrung_Incline_Matchrider.osm");
            Reader reader = new InputStreamReader(input);
            StringBuilder sb = new StringBuilder();
            char buffer[] = new char[16384];  // read 16k blocks
            int len;
            while ((len = reader.read(buffer)) > 0) {
                sb.append(buffer, 0, len);
            }
            reader.close();
            return sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @SuppressLint("StaticFieldLeak")
    private class MyTask extends AsyncTask<Void, Void, List<WayCustomModel>> {
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RoutePlannerActivity.this);
            pDialog.setMessage("Please Wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected void onPostExecute(List<WayCustomModel> aVoid) {
            super.onPostExecute(aVoid);
            GeoPoint start = null;
            for (int i=0;i<aVoid.size();i++){
                if (i == 0)
                    start = aVoid.get(i).getGeoPoint().get(0);

                final GeoPoint finalStart = start;
                addPolyLineForWays(aVoid.get(i).getGeoPoint(), finalStart);

            }
            pDialog.dismiss();


        }

        @Override
        protected List<WayCustomModel> doInBackground(Void... params) {
            try {
                GeoPoint start = null;
                final List<GeoPoint> geoPointsEnd = new ArrayList<>();
                GeoPoint geoPoint = null;
                final List<WayCustomModel> wayCustomModelList = new ArrayList<>();
                for (int i = 0; i < mOSMData.getOSM().getWay().size(); i++) {
                    final List<GeoPoint> geoPoints = new ArrayList<>();
                    List<Node> nodeList = new ArrayList<>();
                    List<GeoPoint> geoPointArrayList = new ArrayList<>();
                    WayCustomModel wayCustomModel = new WayCustomModel();

                    for (int j = 0; j < mOSMData.getOSM().getWay().get(i).getNdList().size(); j++) {

                        String refNode = mOSMData.getOSM().getWay().get(i).getNdList().get(j).getRef();
                          /*  if (refNode.equalsIgnoreCase(mNodeHashMap.get(refNode).getID())) {
                                geoPoint = new GeoPoint(Double.parseDouble(mNodeHashMap.get(refNode).getLatitude()),
                                        Double.parseDouble(mNodeHashMap.get(refNode).getLongitude()));
                                geoPoints.add(geoPoint);

                            }
                          */

                        nodeList.add(mNodeHashMap.get(refNode));
                        geoPointArrayList.add(new GeoPoint(Double.parseDouble(mNodeHashMap.get(refNode).getLatitude()),
                                Double.parseDouble(mNodeHashMap.get(refNode).getLongitude())));
                        wayCustomModel.setGeoPoint(geoPointArrayList);

                    }
                    wayCustomModel.setId(mOSMData.getOSM().getWay().get(i).getID());
                    wayCustomModel.setTag(mOSMData.getOSM().getWay().get(i).getTagList());
                    wayCustomModel.setNode(nodeList);
                    wayCustomModel.setGeoPoint(geoPointArrayList);
                    wayCustomModelList.add(wayCustomModel);


                    /*if (i == 0)
                        start = geoPoints.get(0);

                    final GeoPoint finalStart = start;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addPolyLineForWays(geoPoints, finalStart);

                        }
                    });
                    geoPointsEnd.add(geoPoint);*/
                }

                /*for (int i=0;i<wayCustomModelList.size();i++){
                    if (i == 0)
                        start = wayCustomModelList.get(i).getGeoPoint().get(0);

                    final GeoPoint finalStart = start;
                    addPolyLineForWays(wayCustomModelList.get(i).getGeoPoint(), finalStart);

                    *//*runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addPolyLineForWays(wayCustomModelList.get(finalI).getGeoPoint(), finalStart);

                        }
                    });*//*
                }*/

                publishProgress();
                return wayCustomModelList;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}