package com.disablerouting.route_planner.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.*;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.common.AppConstant;
import com.disablerouting.curd_operations.WayDataPreference;
import com.disablerouting.curd_operations.manager.GetWayManager;
import com.disablerouting.curd_operations.model.CommonModel;
import com.disablerouting.curd_operations.model.ListWayData;
import com.disablerouting.curd_operations.model.NodeReference;
import com.disablerouting.curd_operations.model.ResponseWay;
import com.disablerouting.filter.view.FilterActivity;
import com.disablerouting.geo_coding.model.Features;
import com.disablerouting.instructions.InstructionsActivity;
import com.disablerouting.login.LoginActivity;
import com.disablerouting.login.UserPreferences;
import com.disablerouting.map_base.MapBaseActivity;
import com.disablerouting.route_planner.model.NodeItem;
import com.disablerouting.route_planner.model.ProgressModel;
import com.disablerouting.route_planner.model.Steps;
import com.disablerouting.route_planner.presenter.IRoutePlannerScreenPresenter;
import com.disablerouting.route_planner.presenter.IRouteView;
import com.disablerouting.route_planner.presenter.RoutePlannerScreenPresenter;
import com.disablerouting.setting.SettingActivity;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoutePlannerActivity extends MapBaseActivity implements OnSourceDestinationListener, IRouteView,
        RadioGroup.OnCheckedChangeListener {

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

    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    @BindView(R.id.toggle_way)
    Switch mSwitchCompatToogle;

    @BindView(R.id.image_info)
    ImageView mImageViewInfo;

    @BindView(R.id.radioGroup)
    RadioGroup mRadioGroup;

    @BindView(R.id.radioButtonWays)
    RadioButton mRadioButtonWays;

    @BindView(R.id.radioButtonNodes)
    RadioButton mRadioButtonNodes;

    @BindView(R.id.img_current_pin)
    ImageView mImageCurrentPin;


    private boolean mISMapPlotted = false;
    private boolean mIsUpdateAgain = false;
    private List<ListWayData> mWayListValidatedData = new ArrayList<>();
    private List<ListWayData> mWayListNotValidatedData = new ArrayList<>();
    private List<NodeReference> mNodeListValidatedData = new ArrayList<>();
    private List<NodeReference> mNodeListNotValidatedData = new ArrayList<>();
    private HashMap<Integer, List<Object>> mIntegerListHashMap = new HashMap<>();

    private int mButtonSelected = 1;
    private ProgressDialog pDialog;
    private boolean mISFromSuggestion;
    private List<Steps> mStepsList = new ArrayList<>();
    private int mTabSelected = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        if (getIntent().hasExtra("FromSuggestion")) {
            mISFromSuggestion = getIntent().getBooleanExtra("FromSuggestion", false);
        }
        if (WayDataPreference.getInstance(this) != null) {
            mWayListValidatedData = WayDataPreference.getInstance(this).getValidateWayData();
            mWayListNotValidatedData = WayDataPreference.getInstance(this).getNotValidatedWayData();
            mNodeListValidatedData = WayDataPreference.getInstance(this).getValidateDataNode();
            mNodeListNotValidatedData = WayDataPreference.getInstance(this).getNotValidateDataNode();

        }
        mSourceDestinationFragment = SourceDestinationFragment.newInstance(this);
        addFragment(R.id.contentContainer, mSourceDestinationFragment, "");
        IRoutePlannerScreenPresenter IRoutePlannerScreenPresenter = new RoutePlannerScreenPresenter(this, new GetWayManager());
        if (mISFromSuggestion) {
            addCurrentPosition();
            mSourceDestinationFragment.OnFromSuggestion();
            mButtonGo.setVisibility(View.GONE);
            mSwitchCompatToogle.setVisibility(View.GONE);
            mRadioGroup.setVisibility(View.VISIBLE);
            mImageCurrentPin.setVisibility(View.GONE);
        }

        mRadioGroup.setOnCheckedChangeListener(this);
        mTabSelected = 3;
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
                if (!mISFromSuggestion) {
                    plotDataOfSourceDestination(null, mSourceAddress, mDestinationAddress, null, true);
                }
            }
            mIsUpdateAgain = true;
        }

    }

    public void addCurrentPosition() {
        addCurrentLocation();
    }

    @Override
    public void plotDataOnMap(List<List<Double>> geoPointList, List<Steps> stepsList) {
        if (geoPointList != null && stepsList != null) {
            mStepsList = stepsList;
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
                    nodeItem.getNodeType().getIdentifier().contains(AppConstant.publicBusStop) ||
                    nodeItem.getNodeType().getIdentifier().contains(AppConstant.publicParking)) {
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
        }
    }

    @Override
    public void onDestinationClickWhileNavigationRunning() {
        if (mISMapPlotted) {
            mISMapPlotted = false;
            mButtonGo.setVisibility(View.VISIBLE);
            mButtonGo.setClickable(true);
            mButtonGo.setText(R.string.go);
        }
    }


    @OnClick(R.id.img_re_center)
    public void reCenter() {
        // clearItemsFromMap();
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
        mISMapPlotted = true;
        if (mISMapPlotted) {
            mImageViewInfo.setVisibility(View.VISIBLE);
        } else {
            mImageViewInfo.setVisibility(View.GONE);
        }

    }




    @OnClick(R.id.btn_go)
    public void goPlotMap() {
        // UI_HANDLER.post(updateMarker);
        mImageCurrentPin.setVisibility(View.GONE);
        clearItemsFromMap();
        Features features = mHashMapObjectFilterRoutingVia.get(AppConstant.DATA_FILTER_ROUTING_VIA);
        mSourceDestinationFragment.plotRoute(mJsonObjectFilter, features);

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
        if (requestCode == AppConstant.REQUEST_CODE_LOGIN) {
            if (resultCode == Activity.RESULT_OK) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
            }
        }
        if (requestCode == AppConstant.REQUEST_CODE_UPDATE_MAP_DATA) {
            if (resultCode == Activity.RESULT_OK) {
                if (WayDataPreference.getInstance(this) != null) {
                    mWayListNotValidatedData = new ArrayList<>(WayDataPreference.getInstance(this).getNotValidatedWayData());
                    mWayListValidatedData = new ArrayList<>(WayDataPreference.getInstance(this).getValidateWayData());
                    mNodeListValidatedData = new ArrayList<>(WayDataPreference.getInstance(this).getValidateDataNode());
                    mNodeListNotValidatedData = new ArrayList<>(WayDataPreference.getInstance(this).getNotValidateDataNode());
                    onToggleClickedBanner(false);
                }
            }
        }
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
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        clearItemsFromMap();

        switch (checkedId) {
            case R.id.radioButtonWays:
                mTabSelected = 3;
                mRadioButtonWays.setTextColor(getResources().getColor(R.color.colorPrimary));
                mRadioButtonNodes.setTextColor(getResources().getColor(R.color.colorWhite));
                PlotWayDataTask mPlotWayDataTaskWays = new PlotWayDataTask();
                mPlotWayDataTaskWays.execute();

                break;

            case R.id.radioButtonNodes:
                mTabSelected = 4;

                mRadioButtonNodes.setTextColor(getResources().getColor(R.color.colorPrimary));
                mRadioButtonWays.setTextColor(getResources().getColor(R.color.colorWhite));

                PlotWayDataTask mPlotWayDataTaskNodes = new PlotWayDataTask();
                mPlotWayDataTaskNodes.execute();


                break;

            default:

        }
    }


    @SuppressLint("StaticFieldLeak")
    private class PlotWayDataTask extends AsyncTask<Void, ProgressModel, CommonModel> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RoutePlannerActivity.this);
            pDialog.setMessage(getResources().getString(R.string.please_wait));
            pDialog.setCancelable(false);
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            } else {
                pDialog.show();
            }
        }

        @Override
        protected void onPostExecute(CommonModel aVoid) {
            super.onPostExecute(aVoid);
            List<ListWayData> listWayData = aVoid.getListWayDataList();
            List<NodeReference> nodeReferenceList = aVoid.getNodeReferenceList();
            if (listWayData != null && listWayData.size() > 0) {
                setBoundingBox(listWayData.get(0).getGeoPoints().get(0), listWayData.get(listWayData.size() - 1).getGeoPoints().get(0));
            }
            if (nodeReferenceList != null && nodeReferenceList.size() > 0) {
                GeoPoint geoPointStart = new GeoPoint(Double.parseDouble(nodeReferenceList.get(0).getLat()),
                        Double.parseDouble(nodeReferenceList.get(0).getLon()));
                GeoPoint geoPointEnd = new GeoPoint(Double.parseDouble(nodeReferenceList.get(nodeReferenceList.size() - 1).getLat()),
                        Double.parseDouble(nodeReferenceList.get(nodeReferenceList.size() - 1).getLon()));
                setBoundingBox(geoPointStart, geoPointEnd);
            }

            pDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(ProgressModel... values) {
            ProgressModel model = values[0];
            if ((mTabSelected == 3 && mButtonSelected == 1) || (mTabSelected == 3 && mButtonSelected == 2)) {
                addPolyLineForWays(model.getListWayData(), model.isValid());
            }
            if ((mTabSelected == 4 && mButtonSelected == 1) || (mTabSelected == 4 && mButtonSelected == 2)) {
                addNodeForWays(model.getNodeReference(), model.isValid());
            }

        }

        @Override
        protected CommonModel doInBackground(Void... params) {
            try {

                CommonModel commonModel = new CommonModel();
                GeoPoint start;
                List<ListWayData> listWayData = new ArrayList<>();
                List<NodeReference> nodeReferenceList = new ArrayList<>();
                if (mTabSelected == 3) {
                    if (mButtonSelected == 1) {
                        //For Way Data Not validated
                        listWayData.clear();
                        listWayData = mWayListNotValidatedData;
                        if (mWayListNotValidatedData.size() != 0) {
                            for (int i = 0; i < mWayListNotValidatedData.size(); i++) {
                                List<GeoPoint> geoPointArrayList = mWayListNotValidatedData.get(i).getGeoPoints();
                                start = null;
                                if (i == 0)
                                    start = geoPointArrayList.get(0);

                                final GeoPoint finalStart = start;
                                publishProgress(new ProgressModel(finalStart,
                                        mWayListNotValidatedData.get(i), false, null));

                            }
                        }
                    }
                    if (mButtonSelected == 2) {
                        //For Way Data Validated
                        listWayData.clear();
                        listWayData = mWayListValidatedData;
                        if (mWayListValidatedData.size() != 0) {
                            for (int i = 0; i < mWayListValidatedData.size(); i++) {
                                List<GeoPoint> geoPointArrayList = mWayListValidatedData.get(i).getGeoPoints();
                                start = null;
                                if (i == 0)
                                    start = geoPointArrayList.get(0);

                                final GeoPoint finalStart = start;
                                publishProgress(new ProgressModel(finalStart,
                                        mWayListValidatedData.get(i), true, null));

                            }
                        }
                    }
                }
                if (mTabSelected == 4) {
                    if (mButtonSelected == 1) {
                        //For Way Data Not validated
                        nodeReferenceList.clear();
                        nodeReferenceList = mNodeListNotValidatedData;
                        if (mNodeListNotValidatedData.size() != 0) {
                            for (int i = 0; i < mNodeListNotValidatedData.size(); i++) {

                                publishProgress(new ProgressModel(null,
                                        null, false, mNodeListNotValidatedData.get(i)));

                            }
                        }
                    }
                    if (mButtonSelected == 2) {
                        //For Way Data Not validated
                        nodeReferenceList.clear();
                        nodeReferenceList = mNodeListValidatedData;
                        if (mNodeListValidatedData.size() != 0) {
                            for (int i = 0; i < mNodeListValidatedData.size(); i++) {

                                publishProgress(new ProgressModel(null,
                                        null, true, mNodeListValidatedData.get(i)));

                            }
                        }
                    }


                }

                commonModel.setListWayDataList(listWayData);
                commonModel.setNodeReferenceList(nodeReferenceList);
                return commonModel;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public void checkForWay(Polyline polyline, ListWayData way, boolean valid, boolean isForWay, NodeReference nodeReference) {
        if (isForWay) {
            Intent intent = new Intent(this, SettingActivity.class);
            intent.putExtra(AppConstant.WAY_DATA, way);
            intent.putExtra(AppConstant.IS_FOR_WAY, true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, AppConstant.REQUEST_CODE_UPDATE_MAP_DATA);
        } else {
            Intent intent = new Intent(this, SettingActivity.class);
            intent.putExtra(AppConstant.WAY_DATA, nodeReference);
            intent.putExtra(AppConstant.IS_FOR_WAY, false);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, AppConstant.REQUEST_CODE_UPDATE_MAP_DATA);
        }
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
        Intent intent = new Intent(this, SettingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra(AppConstant.WAY_DATA, responseWay);
        launchActivity(intent);
    }


    @Override
    public void onFailure(String error) {
        Toast.makeText(RoutePlannerActivity.this, "Status is not found: ", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onToggleClickedBanner(boolean isChecked) {
        clearItemsFromMap();
        mRadioGroup.setVisibility(View.VISIBLE);
        if (!isChecked) {
            mButtonSelected = 1;
            setZoomMap();
            PlotWayDataTask mPlotWayDataTaskNotValidated = new PlotWayDataTask();
            mPlotWayDataTaskNotValidated.execute();
        } else {
            mButtonSelected = 2;
            setZoomMap();
            PlotWayDataTask mPlotWayDataTaskValidated = new PlotWayDataTask();
            mPlotWayDataTaskValidated.execute();
        }

    }



    @OnClick(R.id.toggle_way)
    public void toggleViews() {
        if (UserPreferences.getInstance(this).getAccessToken() == null) {
            Intent intentLogin = new Intent(this, LoginActivity.class);
            startActivityForResult(intentLogin, AppConstant.REQUEST_CODE_LOGIN);
            mSwitchCompatToogle.setChecked(false);

        } else {
            if (mSwitchCompatToogle.isChecked()) {

                mRadioGroup.setVisibility(View.VISIBLE);
                mSourceDestinationFragment.onToggleView(true);
                mButtonGo.setVisibility(View.GONE);
                mImageViewInfo.setVisibility(View.GONE);
                clearItemsFromMap();
                if (mNodeListNotValidatedData.size() != 0) {
                    hideLoader();
                    setZoomMap();
                    PlotWayDataTask mPlotWayDataTaskNotValidated = new PlotWayDataTask();
                    mPlotWayDataTaskNotValidated.execute();
                } else {
                    showLoader();
                }
            } else {
                mRadioGroup.setVisibility(View.GONE);
                mSourceDestinationFragment.onToggleView(false);
                clearItemsFromMap();
                Features features = mHashMapObjectFilterRoutingVia.get(AppConstant.DATA_FILTER_ROUTING_VIA);
                mSourceDestinationFragment.plotRoute(mJsonObjectFilter, features);
                mButtonGo.setVisibility(View.VISIBLE);

                if (mISMapPlotted) {
                    mImageViewInfo.setVisibility(View.VISIBLE);
                }

            }
        }

    }

    @OnClick(R.id.image_info)
    public void onInfo() {
        Intent intent = new Intent(this, InstructionsActivity.class);
        intent.putParcelableArrayListExtra(AppConstant.STEP_DATA, (ArrayList<? extends Parcelable>) mStepsList);
        launchActivity(intent);
    }

    @Override
    public void onDragClicked(GeoPoint geoPoint) {
        mSourceDestinationFragment.setDataWhenDragging(geoPoint);

    }

    @Override
    public void onClickField(boolean onStart) {
        if(onStart){
            mImageCurrentPin.setVisibility(View.VISIBLE);
            getMapCenter();

        }else {
            mImageCurrentPin.setVisibility(View.GONE);

        }
    }


}