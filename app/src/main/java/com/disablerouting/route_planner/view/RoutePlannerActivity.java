package com.disablerouting.route_planner.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.common.AppConstant;
import com.disablerouting.curd_operations.manager.GetWayManager;
import com.disablerouting.curd_operations.model.RequestGetWay;
import com.disablerouting.curd_operations.model.ResponseWay;
import com.disablerouting.filter.view.FilterActivity;
import com.disablerouting.geo_coding.model.Features;
import com.disablerouting.map_base.MapBaseActivity;
import com.disablerouting.route_planner.model.*;
import com.disablerouting.route_planner.presenter.IRoutePlannerScreenPresenter;
import com.disablerouting.route_planner.presenter.IRouteView;
import com.disablerouting.route_planner.presenter.RoutePlannerScreenPresenter;
import com.disablerouting.setting.SettingActivity;
import com.disablerouting.utils.Utility;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polyline;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoutePlannerActivity extends MapBaseActivity implements OnSourceDestinationListener, IRouteView, RadioGroup.OnCheckedChangeListener {

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

    @BindView(R.id.radioGroup)
    RadioGroup mRadioGroup;

    @BindView(R.id.radioButtonRoutes)
    RadioButton mRadioButtonRoutes;

    @BindView(R.id.radioButtonValidated)
    RadioButton mRadioButtonValidated;

    @BindView(R.id.radioButtonNotValidated)
    RadioButton mRadioButtonNotValidated;

    private int mButtonSelected;
    private ProgressDialog pDialog;


    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    private boolean mISMapPlotted = false;
    private boolean mIsUpdateAgain = false;
    private OSMData mOSMData;
    private HashMap<String, Node> mNodeHashMap = new HashMap<>();
    private IRoutePlannerScreenPresenter mIRoutePlannerScreenPresenter;
    private List<Way> mWayListEven= new ArrayList<>();
    private List<Way> mWayListOdd= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        mSourceDestinationFragment = SourceDestinationFragment.newInstance(this);
        addFragment(R.id.contentContainer, mSourceDestinationFragment, "");
        mIRoutePlannerScreenPresenter = new RoutePlannerScreenPresenter(this, new GetWayManager());
        String data = readOSMFile();
        convertDataIntoModel(data);
        mRadioGroup.setOnCheckedChangeListener(this);
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
    public void plotDataOnMap(List<List<Double>> geoPointList, List<Steps> stepsList) {
        if (geoPointList != null && stepsList != null) {
            plotDataOfSourceDestination(geoPointList, mSourceAddress, mDestinationAddress, stepsList, true);
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
        clearItemsFromMap();
        addCurrentLocation();
        stopRunningMarker();
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



    private void convertDataIntoModel(String data) {
        JSONObject jsonObject = Utility.convertXMLtoJSON(data);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
            mOSMData = objectMapper.readValue(jsonObject.toString(), OSMData.class);
            for (int i = 0; i < mOSMData.getOSM().getNode().size(); i++) {
                mNodeHashMap.put(mOSMData.getOSM().getNode().get(i).getID(), mOSMData.getOSM().getNode().get(i));
            }
            for (int i = 0; i < mOSMData.getOSM().getWay().size(); i++) {
                if(i%2==0) {
                    mWayListEven.add(mOSMData.getOSM().getWay().get(i));
                }else {
                    mWayListOdd.add(mOSMData.getOSM().getWay().get(i));
                }
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

    @Override
    public void showLoader() {
        mProgressBar.setVisibility(View.VISIBLE);

    }

    @Override
    public void hideLoader() {
        mProgressBar.setVisibility(View.GONE);
    }


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.radioButtonRoutes:
                mButtonSelected = 1;
                clearItemsFromMap();
                addCurrentLocation();

                mRadioButtonRoutes.setTextColor(getResources().getColor(R.color.colorWhite));
                mRadioButtonValidated.setTextColor(getResources().getColor(R.color.colorPrimary));
                mRadioButtonNotValidated.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;

            case R.id.radioButtonValidated:
                mButtonSelected = 2;
                mRadioButtonValidated.setTextColor(getResources().getColor(R.color.colorWhite));
                mRadioButtonRoutes.setTextColor(getResources().getColor(R.color.colorPrimary));
                mRadioButtonNotValidated.setTextColor(getResources().getColor(R.color.colorPrimary));

                clearItemsFromMap();
                addCurrentLocation();
                if(mSourceAddress!=null && mDestinationAddress!=null) {
                    PlotWayDataTask mPlotWayDataTaskValidated = new PlotWayDataTask();
                    mPlotWayDataTaskValidated.execute();
                }

                break;

            case R.id.radioButtonNotValidated:
                mButtonSelected = 3;
                mRadioButtonNotValidated.setTextColor(getResources().getColor(R.color.colorWhite));
                mRadioButtonRoutes.setTextColor(getResources().getColor(R.color.colorPrimary));
                mRadioButtonValidated.setTextColor(getResources().getColor(R.color.colorPrimary));

                clearItemsFromMap();
                addCurrentLocation();
                if(mSourceAddress!=null && mDestinationAddress!=null) {
                    PlotWayDataTask mPlotWayDataTaskNotValidated = new PlotWayDataTask();
                    mPlotWayDataTaskNotValidated.execute();
                }
                break;

            default:

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class PlotWayDataTask extends AsyncTask<Void, ProgressModel, List<WayCustomModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RoutePlannerActivity.this);
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
            addPolyLineForWays(model.getGeoPointList(), model.getStart(), model.getWayCustomModel() ,model.isValid());

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

                if(mButtonSelected==3) {
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
    public void checkForWay(Polyline polyline, String way) {
        super.checkForWay(polyline, way);
        RequestGetWay requestGetWay = new RequestGetWay();
        requestGetWay.setStringWay(way);
        mIRoutePlannerScreenPresenter.getWays(requestGetWay);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pDialog != null) {
            pDialog.dismiss();
        }
    }

    @Override
    public void onWayDataReceived(ResponseWay responseWay) {
         Toast.makeText(RoutePlannerActivity.this, "Status is : " + responseWay.isStatus(), Toast.LENGTH_SHORT).show();
         Intent intent= new Intent(this,SettingActivity.class);
         intent.putExtra("WayData", responseWay);
         launchActivity(intent);
    }

    @Override
    public void onFailure(String error) {
        Toast.makeText(RoutePlannerActivity.this, "Status is not found: ", Toast.LENGTH_SHORT).show();
    }


}