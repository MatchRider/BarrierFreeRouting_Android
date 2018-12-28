package com.disablerouting.route_planner.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.*;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.disablerouting.R;
import com.disablerouting.common.AppConstant;
import com.disablerouting.common.MessageEvent;
import com.disablerouting.curd_operations.WayDataPreference;
import com.disablerouting.curd_operations.model.*;
import com.disablerouting.filter.view.FilterActivity;
import com.disablerouting.geo_coding.model.Features;
import com.disablerouting.instructions.InstructionsActivity;
import com.disablerouting.login.LoginActivity;
import com.disablerouting.login.UserPreferences;
import com.disablerouting.login.model.SearchPreferences;
import com.disablerouting.login.model.UserSearchModel;
import com.disablerouting.map_base.MapBaseActivity;
import com.disablerouting.route_planner.model.NodeItem;
import com.disablerouting.route_planner.model.ProgressModel;
import com.disablerouting.route_planner.model.Steps;
import com.disablerouting.route_planner.presenter.IRoutePlannerScreenPresenter;
import com.disablerouting.route_planner.presenter.IRouteView;
import com.disablerouting.route_planner.presenter.RoutePlannerScreenPresenter;
import com.disablerouting.service.OsmDataService;
import com.disablerouting.setting.SettingActivity;
import com.disablerouting.utils.Utility;
import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polyline;

import java.util.*;

public class RoutePlannerActivity extends MapBaseActivity implements OnSourceDestinationListener, IRouteView,
        RadioGroup.OnCheckedChangeListener {

    private SourceDestinationFragment mSourceDestinationFragment;
    private Features mFeaturesSourceAddress;
    private Features mFeaturesDestinationAddress;
    private String mSourceAddress;
    private String mDestinationAddress;
    private JSONObject mJsonObjectFilter = new JSONObject();
    ;
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, Integer> mHashMapObjectFilterItem = new HashMap<>();
    private HashMap<String, Features> mHashMapObjectFilterRoutingVia = new HashMap<>();
    private HashMap<String, String> mHashMapObjectFilter;
    private IRoutePlannerScreenPresenter mIRoutePlannerScreenPresenter;
    private int mCoordinatesSize = 0;

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

    @BindView(R.id.img_re_fresh)
    ImageView mImageRefresh;

    @BindView(R.id.contentContainer)
    FrameLayout mFrameLayout;

    private boolean mISMapPlotted = false;
    private boolean mIsUpdateAgain = false;
    private ArrayList<ListWayData> mWayListValidatedData = new ArrayList<>();
    private ArrayList<ListWayData> mWayListNotValidatedData = new ArrayList<>();
    private ArrayList<NodeReference> mNodeListValidatedData = new ArrayList<>();
    private ArrayList<NodeReference> mNodeListNotValidatedData = new ArrayList<>();

    private int mButtonSelected = 1;
    private ProgressDialog pDialog;
    private boolean mISFromSuggestion;
    private boolean mISFromOSM;
    private List<Steps> mStepsList = new ArrayList<>();
    boolean mStepListHasData = false;
    private int mTabSelected = 1;
    private PlotWayDataTask mPlotWayDataTask;
    private boolean mIsResumeExecuted = false;
    private Features features;
    private ListWayData listWayDataUpdate = null;
    private NodeReference nodeReferenceUpdate = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        if (getIntent().hasExtra("FromSuggestion")) {
            mISFromSuggestion = getIntent().getBooleanExtra("FromSuggestion", false);
        }
        if (getIntent().hasExtra("FromOSM")) {
            mISFromOSM = getIntent().getBooleanExtra("FromOSM", false);
        }

        mSourceDestinationFragment = SourceDestinationFragment.newInstance(this);
        addFragment(R.id.contentContainer, mSourceDestinationFragment, "");
        mIRoutePlannerScreenPresenter = new RoutePlannerScreenPresenter(this,
                this);

        if (mISFromSuggestion) {
            showLoader();
            setZoomMap();
            mSourceDestinationFragment.OnFromSuggestion(true);
            mButtonGo.setVisibility(View.GONE);
            mImageRefresh.setVisibility(View.VISIBLE);
            mSwitchCompatToogle.setVisibility(View.GONE);
            mRadioGroup.setVisibility(View.VISIBLE);
            mImageCurrentPin.setVisibility(View.GONE);

        }

        if (mISFromOSM) {
            showLoader();
            setZoomMap();
            mSourceDestinationFragment.OnFromSuggestion(false);
            mButtonGo.setVisibility(View.GONE);
            mImageRefresh.setVisibility(View.VISIBLE);
            mSwitchCompatToogle.setVisibility(View.GONE);
            mRadioGroup.setVisibility(View.VISIBLE);
            mImageCurrentPin.setVisibility(View.GONE);
        }
        mRadioGroup.setOnCheckedChangeListener(this);
        mTabSelected = 3;

        mWayListValidatedData.clear();
        mWayListNotValidatedData.clear();
        mNodeListValidatedData.clear();
        mNodeListNotValidatedData.clear();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Resume", "executed_outside");
        if (!mIsResumeExecuted) {
            if (!OsmDataService.isSyncInProgress) {
                Log.e("Resume", "executed_inside");
                if (mISFromOSM) {
                    if (WayDataPreference.getInstance(this) != null) {

                        mWayListValidatedData = new ArrayList<>(WayDataPreference.getInstance(this).getValidateWayDataOSM());
                        mWayListNotValidatedData = new ArrayList<>(WayDataPreference.getInstance(this).getNotValidatedWayDataOSM());
                        mNodeListValidatedData = new ArrayList<>(WayDataPreference.getInstance(this).getValidateDataNodeOSM());
                        mNodeListNotValidatedData = new ArrayList<>(WayDataPreference.getInstance(this).getNotValidateDataNodeOSM());

                    }
                    if (mWayListValidatedData.size() != 0 || mWayListNotValidatedData.size() != 0 ||
                            mNodeListValidatedData.size() != 0 || mNodeListNotValidatedData.size() != 0) {
                        hideLoader();
                        mSourceDestinationFragment.OnFromSuggestion(false);
                        mIsResumeExecuted = true;
                    }
                    if (mWayListValidatedData.size() == 0 && mWayListNotValidatedData.size() == 0 &&
                            mNodeListValidatedData.size() == 0 && mNodeListNotValidatedData.size() == 0) {
                        showLoader();
                    }
                } else if (mISFromSuggestion) {
                    if (WayDataPreference.getInstance(this) != null) {

                        mWayListValidatedData = new ArrayList<>(WayDataPreference.getInstance(this).getValidateWayData());
                        mWayListNotValidatedData = new ArrayList<>(WayDataPreference.getInstance(this).getNotValidatedWayData());
                        mNodeListValidatedData = new ArrayList<>(WayDataPreference.getInstance(this).getValidateDataNode());
                        mNodeListNotValidatedData = new ArrayList<>(WayDataPreference.getInstance(this).getNotValidateDataNode());

                    }
                    if (mWayListValidatedData.size() != 0 || mWayListNotValidatedData.size() != 0 ||
                            mNodeListValidatedData.size() != 0 || mNodeListNotValidatedData.size() != 0) {
                        hideLoader();
                        mSourceDestinationFragment.OnFromSuggestion(true);
                        mIsResumeExecuted = true;

                    }
                    if (mWayListValidatedData.size() == 0 && mWayListNotValidatedData.size() == 0 &&
                            mNodeListValidatedData.size() == 0 && mNodeListNotValidatedData.size() == 0) {
                        showLoader();
                    }
                }

                if (!mISFromOSM && !mISFromSuggestion) {
                    if (WayDataPreference.getInstance(this) != null) {

                        mWayListValidatedData = new ArrayList<>(WayDataPreference.getInstance(this).getValidateWayData());
                        mWayListNotValidatedData = new ArrayList<>(WayDataPreference.getInstance(this).getNotValidatedWayData());
                        mNodeListValidatedData = new ArrayList<>(WayDataPreference.getInstance(this).getValidateDataNode());
                        mNodeListNotValidatedData = new ArrayList<>(WayDataPreference.getInstance(this).getNotValidateDataNode());
                    }
                    if (mWayListValidatedData.size() != 0 || mWayListNotValidatedData.size() != 0 ||
                            mNodeListValidatedData.size() != 0 || mNodeListNotValidatedData.size() != 0) {

                        hideLoader();
                        mIsResumeExecuted = true;

                    }

                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        Log.e("event", event.getMessage());
        if (mISFromSuggestion) {
            if (event.getMessage().equalsIgnoreCase("LIST_DATA")) {
                if (WayDataPreference.getInstance(this) != null) {
                    mWayListValidatedData.clear();
                    mWayListNotValidatedData.clear();
                    mNodeListValidatedData.clear();
                    mNodeListNotValidatedData.clear();

                    mWayListValidatedData = new ArrayList<>(WayDataPreference.getInstance(this).getValidateWayData());
                    mWayListNotValidatedData = new ArrayList<>(WayDataPreference.getInstance(this).getNotValidatedWayData());
                    mNodeListValidatedData = new ArrayList<>(WayDataPreference.getInstance(this).getValidateDataNode());
                    mNodeListNotValidatedData = new ArrayList<>(WayDataPreference.getInstance(this).getNotValidateDataNode());
                    mIsResumeExecuted = true;

                }
                mSourceDestinationFragment.OnFromSuggestion(true);
                hideLoader();
                if (mImageRefresh.getVisibility() == View.VISIBLE) {
                    Utility.clearAnimationFromView(mImageRefresh);

                }
            }

        }
        if (mISFromOSM) {
            if (event.getMessage().equalsIgnoreCase("OSM_DATA")) {
                mWayListValidatedData.clear();
                mWayListNotValidatedData.clear();
                mNodeListValidatedData.clear();
                mNodeListNotValidatedData.clear();

                if (WayDataPreference.getInstance(this) != null) {
                    mWayListValidatedData = new ArrayList<>(WayDataPreference.getInstance(this).getValidateWayDataOSM());
                    mWayListNotValidatedData = new ArrayList<>(WayDataPreference.getInstance(this).getNotValidatedWayDataOSM());
                    mNodeListValidatedData = new ArrayList<>(WayDataPreference.getInstance(this).getValidateDataNodeOSM());
                    mNodeListNotValidatedData = new ArrayList<>(WayDataPreference.getInstance(this).getNotValidateDataNodeOSM());
                    mIsResumeExecuted = true;

                }
                mSourceDestinationFragment.OnFromSuggestion(false);
                hideLoader();
                if (mImageRefresh.getVisibility() == View.VISIBLE) {
                    Utility.clearAnimationFromView(mImageRefresh);
                }
            }

        }
        if (!mISFromOSM && !mISFromSuggestion) {
            if (event.getMessage().equalsIgnoreCase("LIST_DATA")) {
                if (WayDataPreference.getInstance(this) != null) {
                    mWayListValidatedData.clear();
                    mWayListNotValidatedData.clear();
                    mNodeListValidatedData.clear();
                    mNodeListNotValidatedData.clear();

                    mWayListValidatedData = new ArrayList<>(WayDataPreference.getInstance(this).getValidateWayData());
                    mWayListNotValidatedData = new ArrayList<>(WayDataPreference.getInstance(this).getNotValidatedWayData());
                    mNodeListValidatedData = new ArrayList<>(WayDataPreference.getInstance(this).getValidateDataNode());
                    mNodeListNotValidatedData = new ArrayList<>(WayDataPreference.getInstance(this).getNotValidateDataNode());
                    mIsResumeExecuted = true;
                }
                hideLoader();
                if (mImageRefresh.getVisibility() == View.VISIBLE) {
                    Utility.clearAnimationFromView(mImageRefresh);
                }
            }

        }


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
                if (!mISFromSuggestion && !mISFromOSM) {
                    plotDataOfSourceDestination(null, mSourceAddress, mDestinationAddress, null, true, mCoordinatesSize);
                }

            }
            mIsUpdateAgain = true;
        }
        if (!mISFromSuggestion && !mISFromOSM) {
            addCurrentLocation(0);
        }
    }

    @Override
    public void plotDataOnMap(List<List<Double>> geoPointList, List<Steps> stepsList, int coordinateSize) {
        if (geoPointList != null && stepsList != null) {
            mCoordinatesSize = coordinateSize;
            for (int i = 0; i < stepsList.size(); i++) {
                plotDataOfSourceDestination(geoPointList, mSourceAddress, mDestinationAddress, stepsList, true, coordinateSize);
            }
            if (!mStepListHasData) {
                mStepsList.addAll(stepsList);
                mStepListHasData = true;
            } else {
                Steps steps = new Steps();
                steps.setInstructions(getString(R.string.mid_way));
                steps.setType(14);
                mStepsList.add(steps);
                mStepsList.addAll(stepsList);
            }
        }
    }

    @Override
    public void onBackPress() {
        if (pDialog != null) {
            pDialog.dismiss();
        }
        if (mPlotWayDataTask != null && mPlotWayDataTask.getStatus() == AsyncTask.Status.RUNNING) {
            mPlotWayDataTask.cancel(true);
        }
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
        if (SearchPreferences.getInstance(this) != null && SearchPreferences.getInstance(this).getUserSearch() != null) {
            if (SearchPreferences.getInstance(this).getUserSearch().getHashMapObjectFilterItem() != null) {
                mHashMapObjectFilterItem = SearchPreferences.getInstance(this).getUserSearch().getHashMapObjectFilterItem();
                intentFilter.putExtra(AppConstant.DATA_FILTER_SELECTED, mHashMapObjectFilterItem);
            }
            if (SearchPreferences.getInstance(this).getUserSearch().getHashMapFilterForRouting() != null) {
                mHashMapObjectFilterRoutingVia = SearchPreferences.getInstance(this).getUserSearch().getHashMapFilterForRouting();
                intentFilter.putExtra(AppConstant.DATA_FILTER_ROUTING_VIA, mHashMapObjectFilterRoutingVia);
            }
            if (SearchPreferences.getInstance(this).getUserSearch().getHashMapObjectFilter() != null) {
                mHashMapObjectFilter = SearchPreferences.getInstance(this).getUserSearch().getHashMapObjectFilter();
                intentFilter.putExtra(AppConstant.DATA_FILTER, mHashMapObjectFilter);
            }
        } else {
            intentFilter.putExtra(AppConstant.DATA_FILTER_SELECTED, mHashMapObjectFilterItem);
            intentFilter.putExtra(AppConstant.DATA_FILTER_ROUTING_VIA, mHashMapObjectFilterRoutingVia);
        }
        startActivityForResult(intentFilter, AppConstant.REQUEST_CODE_CAPTURE);
    }

    @Override
    public void plotNodesOnMap(List<NodeItem> mNodes) {
        List<NodeItem> mNodeItemListFiltered = new ArrayList<>();
        for (NodeItem nodeItem : mNodes) {
            assert nodeItem.getNodeType() != null;
            if (nodeItem.getNodeType() != null && (nodeItem.getNodeType() != null && nodeItem.getNodeType().getIdentifier() != null &&
                    nodeItem.getNodeType().getIdentifier().contains(AppConstant.publicTramStop) ||
                    nodeItem.getNodeType().getIdentifier().contains(AppConstant.publicToilets) ||
                    nodeItem.getNodeType().getIdentifier().contains(AppConstant.publicBusStop) ||
                    nodeItem.getNodeType().getIdentifier().contains(AppConstant.publicParking))) {
                mNodeItemListFiltered.add(nodeItem);
            }
        }
        plotDataOfNodes(mNodeItemListFiltered);
        mNodeItemListFiltered.clear();

    }

    @Override
    public void onSwapData() {
        mStepsList.clear();
        clearItemsFromMap();
        mButtonGo.setVisibility(View.VISIBLE);
        mButtonGo.setClickable(true);
        mButtonGo.setText(R.string.go);
        setUserSearchData();
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

        addCurrentLocation(18);
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
        mStepsList.clear();
        mStepListHasData = false;
        mImageCurrentPin.setVisibility(View.GONE);
        setMapCenter(false);
        clearItemsFromMap();

        if (SearchPreferences.getInstance(this) != null && SearchPreferences.getInstance(this).getUserSearch() != null) {
            if (SearchPreferences.getInstance(this).getUserSearch().getHashMapObjectFilterItem() != null) {
                mHashMapObjectFilterItem = SearchPreferences.getInstance(this).getUserSearch().getHashMapObjectFilterItem();
            }
            if (SearchPreferences.getInstance(this).getUserSearch().getHashMapFilterForRouting() != null) {
                mHashMapObjectFilterRoutingVia = SearchPreferences.getInstance(this).getUserSearch().getHashMapFilterForRouting();
                features = mHashMapObjectFilterRoutingVia.get(AppConstant.DATA_FILTER_ROUTING_VIA);
            } else {
                features = mHashMapObjectFilterRoutingVia.get(AppConstant.DATA_FILTER_ROUTING_VIA);
            }
            if (SearchPreferences.getInstance(this).getUserSearch().getHashMapObjectFilter() != null) {
                mHashMapObjectFilter = SearchPreferences.getInstance(this).getUserSearch().getHashMapObjectFilter();
                mJsonObjectFilter = createFilter(mHashMapObjectFilter);
            }
        }
        mSourceDestinationFragment.plotRoute(mJsonObjectFilter, features);
        setUserSearchData();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppConstant.REQUEST_CODE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                mHashMapObjectFilter = (HashMap<String, String>) data.getSerializableExtra(AppConstant.DATA_FILTER);
                mHashMapObjectFilterItem = (HashMap<Integer, Integer>) data.getSerializableExtra(AppConstant.DATA_FILTER_SELECTED);
                mHashMapObjectFilterRoutingVia = (HashMap<String, Features>) data.getSerializableExtra(AppConstant.DATA_FILTER_ROUTING_VIA);
                mJsonObjectFilter = createFilter(mHashMapObjectFilter);
                setUserSearchData();
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
                    if (!mISFromOSM) {
                        if (data != null && data.hasExtra("DATA_WAY")) {
                            listWayDataUpdate = data.getParcelableExtra("DATA_WAY");
                        } else if (data != null && data.hasExtra("DATA_NODE")) {
                            nodeReferenceUpdate = data.getParcelableExtra("DATA_NODE");
                        }
                        showLoader();
                        new SaveData().execute();
                    } else {
                        if (data != null && data.hasExtra("DATA_OSM_WAY")) {
                            listWayDataUpdate = data.getParcelableExtra("DATA_OSM_WAY");
                        } else if (data != null && data.hasExtra("DATA_OSM_NODE")) {
                            nodeReferenceUpdate = data.getParcelableExtra("DATA_OSM_NODE");
                        }
                        showLoader();
                        new SaveData().execute();
                    }
                }
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class SaveData extends AsyncTask<String, Void, Boolean>{

        @Override
        protected Boolean doInBackground(String... params) {
            // your background code here. Don't touch any UI components
            {
                int inDexToRemove = -1;
                if (WayDataPreference.getInstance(getApplicationContext()) != null) {
                    if (!mISFromOSM) {
                        if (listWayDataUpdate != null && listWayDataUpdate.getIsValid() != null) {
                            boolean isValid = Boolean.parseBoolean(listWayDataUpdate.getIsValid());
                            for (int i = 0; i < mWayListNotValidatedData.size(); i++)//Iterate through each item.
                            {
                                if (mWayListNotValidatedData.get(i).getmIndex() ==
                                        listWayDataUpdate.getmIndex()) {
                                    inDexToRemove = i;
                                    break;
                                }

                            }
                            if (isValid) {
                                mWayListNotValidatedData.remove(inDexToRemove);
                                mWayListValidatedData.add(listWayDataUpdate);
                            } else {
                                mWayListNotValidatedData.remove(inDexToRemove);
                                mWayListNotValidatedData.add(listWayDataUpdate);
                            }

                        }
                        if (nodeReferenceUpdate != null && nodeReferenceUpdate.getAttributes() != null &&
                                nodeReferenceUpdate.getAttributes().size() != 0) {
                            boolean isValid = false;
                            for (int i = 0; i < nodeReferenceUpdate.getAttributes().size(); i++) {
                                isValid = nodeReferenceUpdate.getAttributes().get(i).isValid();
                                for (int j = 0; j < mNodeListNotValidatedData.size(); i++) {
                                    if (mNodeListNotValidatedData.get(j).getmIndex() ==
                                            nodeReferenceUpdate.getmIndex()) {
                                        inDexToRemove = j;
                                        break;
                                    }
                                }
                            }
                            if (isValid) {
                                mNodeListNotValidatedData.remove(inDexToRemove);
                                mNodeListValidatedData.add(nodeReferenceUpdate);
                            } else {
                                mNodeListNotValidatedData.remove(inDexToRemove);
                                mNodeListNotValidatedData.add(nodeReferenceUpdate);
                            }

                        }
                        if (WayDataPreference.getInstance(getApplicationContext()) != null) {
                            WayDataPreference.getInstance(getApplicationContext()).saveValidateWayData(mWayListValidatedData);
                            WayDataPreference.getInstance(getApplicationContext()).saveNotValidatedWayData(mWayListNotValidatedData);
                            WayDataPreference.getInstance(getApplicationContext()).saveValidateDataNode(mNodeListValidatedData);
                            WayDataPreference.getInstance(getApplicationContext()).saveNotValidateDataNode(mNodeListNotValidatedData);

                        }
                        return  true;
                    } else {
                        if (listWayDataUpdate != null && listWayDataUpdate.getIsValid() != null) {
                            boolean isValid = Boolean.parseBoolean(listWayDataUpdate.getIsValid());
                            for (int i = 0; i < mWayListNotValidatedData.size(); i++)//Iterate through each item.
                            {
                                if (mWayListNotValidatedData.get(i).getmIndex() ==
                                        listWayDataUpdate.getmIndex()) {
                                    inDexToRemove = i;
                                    break;
                                }

                            }
                            if (isValid) {
                                mWayListNotValidatedData.remove(inDexToRemove);
                                mWayListValidatedData.add(listWayDataUpdate);
                            } else {
                                mWayListNotValidatedData.remove(inDexToRemove);
                                mWayListNotValidatedData.add(listWayDataUpdate);
                            }

                        }
                        if (nodeReferenceUpdate != null && nodeReferenceUpdate.getAttributes() != null &&
                                nodeReferenceUpdate.getAttributes().size() != 0) {
                            boolean isValid = false;
                            for (int i = 0; i < nodeReferenceUpdate.getAttributes().size(); i++) {
                                if (nodeReferenceUpdate.getAttributes().get(i) != null) {
                                    isValid = nodeReferenceUpdate.getAttributes().get(i).isValid();
                                }
                                for (int j = 0; j < mNodeListNotValidatedData.size(); j++) {
                                    if (mNodeListNotValidatedData.get(j).getmIndex() ==
                                            nodeReferenceUpdate.getmIndex()) {
                                        inDexToRemove = j;
                                        break;
                                    }
                                }
                            }
                            if (isValid) {
                                mNodeListNotValidatedData.remove(inDexToRemove);
                                mNodeListValidatedData.add(nodeReferenceUpdate);
                            } else {
                                mNodeListNotValidatedData.remove(inDexToRemove);
                                mNodeListNotValidatedData.add(nodeReferenceUpdate);
                            }
                        }
                        if (WayDataPreference.getInstance(getApplicationContext()) != null) {
                            WayDataPreference.getInstance(getApplicationContext()).saveValidateWayDataOSM(mWayListValidatedData);
                            WayDataPreference.getInstance(getApplicationContext()).saveNotValidatedWayDataOSM(mWayListNotValidatedData);
                            WayDataPreference.getInstance(getApplicationContext()).saveValidateDataNodeOSM(mNodeListValidatedData);
                            WayDataPreference.getInstance(getApplicationContext()).saveNotValidateDataNodeOSM(mNodeListNotValidatedData);
                        }

                        return  true;
                    }
                }
            }
            return null;
        }

        protected void onPostExecute(Boolean result) {
            if(result) {
                if (!mISFromOSM) {
                    onToggleClickedBanner(false);
                    hideLoader();
                } else {
                    onToggleClickedBanner(false);
                    hideLoader();
                }
            }else {
                hideLoader();
            }
            //This is run on the UI thread so you can do as you wish her

        }
    }
    private void setUserSearchData() {
        //Save Data in user Preferences
        if (SearchPreferences.getInstance(this) != null) {
            if (mSourceAddress == null && SearchPreferences.getInstance(this).getUserSearch() != null) {
                mSourceAddress = SearchPreferences.getInstance(this).getUserSearch().getSourceAdd();
            }
            if (mDestinationAddress == null && SearchPreferences.getInstance(this).getUserSearch() != null) {
                mDestinationAddress = SearchPreferences.getInstance(this).getUserSearch().getDestAdd();
            }
            if (mFeaturesSourceAddress == null && SearchPreferences.getInstance(this).getUserSearch() != null) {
                mFeaturesSourceAddress = SearchPreferences.getInstance(this).getUserSearch().getFeaturesSource();
            }
            if (mFeaturesDestinationAddress == null && SearchPreferences.getInstance(this).getUserSearch() != null) {
                mFeaturesDestinationAddress = SearchPreferences.getInstance(this).getUserSearch().getFeaturesDest();
            }
            if (mHashMapObjectFilterRoutingVia == null && SearchPreferences.getInstance(this).getUserSearch() != null) {
                mHashMapObjectFilterRoutingVia = SearchPreferences.getInstance(this).getUserSearch().getHashMapFilterForRouting();
            }
            if (mHashMapObjectFilterItem == null && SearchPreferences.getInstance(this).getUserSearch() != null) {
                mHashMapObjectFilterItem = SearchPreferences.getInstance(this).getUserSearch().getHashMapObjectFilterItem();
            }
            if (mHashMapObjectFilter == null && SearchPreferences.getInstance(this).getUserSearch() != null) {
                mHashMapObjectFilter = SearchPreferences.getInstance(this).getUserSearch().getHashMapObjectFilter();
            }
            GeoPoint geoPointSource = null;
            if (mFeaturesSourceAddress != null && mFeaturesSourceAddress.getGeometry() != null) {
                geoPointSource = new GeoPoint(mFeaturesSourceAddress.getGeometry().getCoordinates().get(0),
                        mFeaturesSourceAddress.getGeometry().getCoordinates().get(1));
            }
            GeoPoint geoPointDestination = null;
            if (mFeaturesDestinationAddress != null && mFeaturesDestinationAddress.getGeometry() != null) {
                geoPointDestination = new GeoPoint(mFeaturesDestinationAddress.getGeometry().getCoordinates().get(0),
                        mFeaturesDestinationAddress.getGeometry().getCoordinates().get(1));
            }
            if (mHashMapObjectFilter != null && mHashMapObjectFilter.size() != 0) {
                mJsonObjectFilter = createFilter(mHashMapObjectFilter);
            }
            if (mHashMapObjectFilterRoutingVia != null && !mHashMapObjectFilterRoutingVia.isEmpty()) {
                features = mHashMapObjectFilterRoutingVia.get(AppConstant.DATA_FILTER_ROUTING_VIA);
            }

            UserSearchModel userSearchModel = new UserSearchModel(mSourceAddress, mDestinationAddress,
                    geoPointSource, geoPointDestination, mFeaturesSourceAddress, mFeaturesDestinationAddress,
                    mHashMapObjectFilterRoutingVia, mHashMapObjectFilterItem, mJsonObjectFilter, mHashMapObjectFilter);
            SearchPreferences.getInstance(this).saveUserSearch(userSearchModel);
        }

    }

    private JSONObject createFilter(HashMap<String, String> hashMapObjectFilter) {
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
        return mJsonObjectFilter;
    }


    @Override
    public void showLoader() {
        //showProgress();
        mProgressBar.setVisibility(View.VISIBLE);
        mFrameLayout.setBackgroundColor(getResources().getColor(R.color.colorLightGray));

    }

    @Override
    public void hideLoader() {
        //hideProgress();
        mProgressBar.setVisibility(View.GONE);
        mFrameLayout.setBackgroundColor(Color.TRANSPARENT);


    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radioButtonWays:
                mTabSelected = 3;
                mRadioButtonWays.setTextColor(getResources().getColor(R.color.colorWhite));
                mRadioButtonNodes.setTextColor(getResources().getColor(R.color.colorPrimary));
                mPlotWayDataTask = new PlotWayDataTask();
                mPlotWayDataTask.execute();
                break;

            case R.id.radioButtonNodes:
                mTabSelected = 4;
                mRadioButtonNodes.setTextColor(getResources().getColor(R.color.colorWhite));
                mRadioButtonWays.setTextColor(getResources().getColor(R.color.colorPrimary));
                mPlotWayDataTask = new PlotWayDataTask();
                mPlotWayDataTask.execute();
                break;

            default:

        }
    }

    private void createListData(ResponseListWay responseWay, boolean b) {
        if (b) {
            mWayListValidatedData = new ArrayList<>();
            mWayListNotValidatedData = new ArrayList<>();
            mNodeListValidatedData = new ArrayList<>();
            mNodeListNotValidatedData = new ArrayList<>();

            for (int i = 0; i < responseWay.getWayData().size(); i++) {
                boolean isValidWay = Boolean.parseBoolean(responseWay.getWayData().get(i).getIsValid());
                if (isValidWay) {
                    mWayListValidatedData.add(responseWay.getWayData().get(i));
                } else {
                    mWayListNotValidatedData.add(responseWay.getWayData().get(i));
                }
                for (int j = 0; j < responseWay.getWayData().get(i).getNodeReference().size(); j++) {
                    if (responseWay.getWayData().get(i).getNodeReference().get(j).getAttributes() != null) {

                        for (int k = 0; k < responseWay.getWayData().get(i).getNodeReference().get(j).getAttributes().size(); k++) {
                            if (!responseWay.getWayData().get(i).getNodeReference().get(j).getAttributes().get(k).isValid()) {
                                mNodeListNotValidatedData.add(responseWay.getWayData().get(i).getNodeReference().get(j));

                            } else {
                                mNodeListValidatedData.add(responseWay.getWayData().get(i).getNodeReference().get(j));

                            }
                        }
                    }
                }
            }
            if (WayDataPreference.getInstance(this) != null) {
                WayDataPreference.getInstance(this).saveValidateWayDataOSM(mWayListValidatedData);
                WayDataPreference.getInstance(this).saveNotValidatedWayDataOSM(mWayListNotValidatedData);
                WayDataPreference.getInstance(this).saveValidateDataNodeOSM(mNodeListValidatedData);
                WayDataPreference.getInstance(this).saveNotValidateDataNodeOSM(mNodeListNotValidatedData);

            }
            onToggleClickedBanner(false);
            hideLoader();
        } else {
            if (responseWay != null && responseWay.getWayData() != null && responseWay.getWayData().size() != 0) {
                if (responseWay.isStatus()) {
                    mWayListValidatedData = new ArrayList<>();
                    mWayListNotValidatedData = new ArrayList<>();
                    mNodeListValidatedData = new ArrayList<>();
                    mNodeListNotValidatedData = new ArrayList<>();

                    for (int i = 0; i < responseWay.getWayData().size(); i++) {
                        boolean isValidWay = Boolean.parseBoolean(responseWay.getWayData().get(i).getIsValid());
                        if (isValidWay) {
                            mWayListValidatedData.add(responseWay.getWayData().get(i));
                        } else {
                            mWayListNotValidatedData.add(responseWay.getWayData().get(i));
                        }


                        for (int j = 0; j < responseWay.getWayData().get(i).getNodeReference().size(); j++) {
                            if (responseWay.getWayData().get(i).getNodeReference().get(j).getAttributes() != null) {
                                for (int k = 0; k < responseWay.getWayData().get(i).getNodeReference().get(j).getAttributes().size(); k++) {

                                    if (!responseWay.getWayData().get(i).getNodeReference().get(j).getAttributes().get(k).isValid()) {
                                        if (!Utility.isListContainId(mNodeListNotValidatedData, responseWay.getWayData().get(i).getNodeReference()
                                                .get(j).getAPINodeId())) {
                                            mNodeListNotValidatedData.add(responseWay.getWayData().get(i).getNodeReference().get(j));

                                        }
                                    } else {
                                        if (!Utility.isListContainId(mNodeListValidatedData, responseWay.getWayData().get(i).getNodeReference()
                                                .get(j).getAPINodeId())) {
                                            mNodeListValidatedData.add(responseWay.getWayData().get(i).getNodeReference().get(j));
                                        }
                                    }

                                }
                            }
                        }
                    }
                    if (WayDataPreference.getInstance(this) != null) {
                        WayDataPreference.getInstance(this).saveValidateWayData(mWayListValidatedData);
                        WayDataPreference.getInstance(this).saveNotValidatedWayData(mWayListNotValidatedData);
                        WayDataPreference.getInstance(this).saveValidateDataNode(mNodeListValidatedData);
                        WayDataPreference.getInstance(this).saveNotValidateDataNode(mNodeListNotValidatedData);

                    }
                } else {
                    if (responseWay.getError() != null && responseWay.getError().get(0) != null &&
                            responseWay.getError().get(0).getMessage() != null) {
                        Toast.makeText(this, responseWay.getError().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                if (WayDataPreference.getInstance(this) != null) {
                    mWayListNotValidatedData = new ArrayList<>(WayDataPreference.getInstance(this).getNotValidatedWayData());
                    mWayListValidatedData = new ArrayList<>(WayDataPreference.getInstance(this).getValidateWayData());
                    mNodeListValidatedData = new ArrayList<>(WayDataPreference.getInstance(this).getValidateDataNode());
                    mNodeListNotValidatedData = new ArrayList<>(WayDataPreference.getInstance(this).getNotValidateDataNode());
                }
                onToggleClickedBanner(false);
                hideLoader();
            } else {
                hideLoader();
            }

        }

    }

    @SuppressLint("StaticFieldLeak")
    private class PlotWayDataTask extends AsyncTask<Void, ProgressModel, CommonModel> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RoutePlannerActivity.this);
            pDialog.setMessage(getResources().getString(R.string.please_wait));
            pDialog.setCancelable(true);
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            } else {
                pDialog.show();
            }
        }

        @Override
        protected void onPostExecute(CommonModel aVoid) {
            super.onPostExecute(aVoid);
            if (aVoid != null) {
                List<ListWayData> listWayData = aVoid.getListWayDataList();
                List<NodeReference> nodeReferenceList = aVoid.getNodeReferenceList();
                if (listWayData != null && listWayData.size() > 0 && listWayData.get(0) != null && listWayData.get(0).getGeoPoints() != null) {
                    if (listWayData.size() > 0 && listWayData.get(0).getGeoPoints().size() > 0 && listWayData.get(listWayData.size() - 1).getGeoPoints().size() != 0) {
                        setBoundingBox(listWayData.get(0).getGeoPoints().get(0),
                                listWayData.get(listWayData.size() - 1).getGeoPoints().get(0));
                    }
                }
                if (nodeReferenceList != null && nodeReferenceList.size() > 0 && nodeReferenceList.get(0) != null) {
                    GeoPoint geoPointStart = new GeoPoint(Double.parseDouble(nodeReferenceList.get(0).getLat()),
                            Double.parseDouble(nodeReferenceList.get(0).getLon()));
                    GeoPoint geoPointEnd = new GeoPoint(Double.parseDouble(nodeReferenceList.get(nodeReferenceList.size() - 1).getLat()),
                            Double.parseDouble(nodeReferenceList.get(nodeReferenceList.size() - 1).getLon()));
                    setBoundingBox(geoPointStart, geoPointEnd);
                }
            }
            pDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(ProgressModel... values) {
            ProgressModel model = values[0];
            if ((mTabSelected == 3 && mButtonSelected == 2) || (mTabSelected == 3 && mButtonSelected == 1)) {
                addPolyLineForWays(model.getListWayData(), model.isValid());
            }
            if ((mTabSelected == 4 && mButtonSelected == 1) || (mTabSelected == 4 && mButtonSelected == 2)) {
                addNodeForWays(model.getNodeReference(), model.isValid());
            }

        }

        @Override
        protected CommonModel doInBackground(Void... params) {
            try {
                clearItemsFromMap();
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
                                List<GeoPoint> geoPointArrayListNotValid = mWayListNotValidatedData.get(i).getGeoPoints();
                                start = null;
                                if (i == 0)
                                    start = geoPointArrayListNotValid.get(0);

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
                        //For Node Data Not validated
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
                        //For Node Data Not validated
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
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        if (pDialog != null) {
            pDialog.dismiss();
        }
        if (mPlotWayDataTask != null && mPlotWayDataTask.getStatus() == AsyncTask.Status.RUNNING) {
            mPlotWayDataTask.cancel(true);
        }
        mIRoutePlannerScreenPresenter.disconnect();
    }

    @Override
    public void onToggleClickedBanner(boolean isChecked) {
        mRadioGroup.setVisibility(View.VISIBLE);
        mImageCurrentPin.setVisibility(View.GONE);
        if (!isChecked) {
            mButtonSelected = 1;
            if (mWayListNotValidatedData.size() > 0) {
                hideLoader();
                mPlotWayDataTask = new PlotWayDataTask();
                mPlotWayDataTask.execute();
            } else {
                clearItemsFromMap();
            }
        } else {
            mButtonSelected = 2;
            if (mWayListValidatedData.size() > 0) {
                hideLoader();
                mPlotWayDataTask = new PlotWayDataTask();
                mPlotWayDataTask.execute();
            } else {
                clearItemsFromMap();
            }
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
                mImageRefresh.setVisibility(View.VISIBLE);
                mImageCurrentPin.setVisibility(View.GONE);
                mImageViewInfo.setVisibility(View.GONE);
                clearItemsFromMap();
                if (mWayListNotValidatedData.size() != 0) {
                    hideLoader();
                    // setZoomMap();
                    mPlotWayDataTask = new PlotWayDataTask();
                    mPlotWayDataTask.execute();
                } else {
                    showLoader();
                }
            } else {
                mRadioGroup.setVisibility(View.GONE);
                mSourceDestinationFragment.onToggleView(false);
                clearItemsFromMap();
                if (mISMapPlotted) {
                    if (mHashMapObjectFilterRoutingVia == null) {
                        if (SearchPreferences.getInstance(this) != null && SearchPreferences.getInstance(this).getUserSearch() != null) {
                            mHashMapObjectFilterRoutingVia = SearchPreferences.getInstance(this).getUserSearch().getHashMapFilterForRouting();
                        }
                    }
                    if (mHashMapObjectFilter == null) {
                        if (SearchPreferences.getInstance(this) != null && SearchPreferences.getInstance(this).getUserSearch() != null) {
                            mHashMapObjectFilter = SearchPreferences.getInstance(this).getUserSearch().getHashMapObjectFilter();
                        }
                    }
                    Features features = mHashMapObjectFilterRoutingVia.get(AppConstant.DATA_FILTER_ROUTING_VIA);
                    if (SearchPreferences.getInstance(this) != null && SearchPreferences.getInstance(this).getUserSearch() != null) {
                        if (mHashMapObjectFilter != null) {
                            mJsonObjectFilter = createFilter(mHashMapObjectFilter);
                        }
                    }
                    mSourceDestinationFragment.plotRoute(mJsonObjectFilter, features);
                }
                mButtonGo.setVisibility(View.VISIBLE);
                mImageRefresh.setVisibility(View.GONE);

                if (mISMapPlotted) {
                    mImageViewInfo.setVisibility(View.GONE);
                } else {
                    mImageCurrentPin.setVisibility(View.VISIBLE);
                }

            }
        }

    }

    @OnClick(R.id.image_info)
    public void onInfo() {
        Intent intent = new Intent(this, InstructionsActivity.class);
        intent.putParcelableArrayListExtra(AppConstant.STEP_DATA, (ArrayList<? extends Parcelable>) mStepsList);
        intent.putExtra(AppConstant.COORDINATE_SIZE, mCoordinatesSize);
        launchActivity(intent);
    }

    @Override
    public void onDragClicked(GeoPoint geoPoint) {
        mSourceDestinationFragment.setDataWhenDragging(geoPoint);

    }

    @Override
    public void onClickField(boolean onStart) {
        if (onStart) {
            if (mButtonGo.getVisibility() == View.VISIBLE) {
                mImageCurrentPin.setVisibility(View.VISIBLE);
                setMapCenter(true);
            } else {
                mImageCurrentPin.setVisibility(View.GONE);
                setMapCenter(false);
            }
        } else {
            mImageCurrentPin.setVisibility(View.GONE);
            setMapCenter(false);
        }
    }

    @OnClick(R.id.img_re_fresh)
    public void onRefreshClick() {
        mWayListValidatedData.clear();
        mWayListNotValidatedData.clear();
        mNodeListValidatedData.clear();
        mNodeListNotValidatedData.clear();

        if (mISFromOSM) {
            if (!OsmDataService.isSyncInProgress) {
                Utility.applyAnimationOnView(this, mImageRefresh);
                startService(Utility.createCallingIntent(this, AppConstant.RUN_OSM));
            }
        }
        if (mISFromSuggestion) {
            if (!OsmDataService.isSyncInProgress) {
                Utility.applyAnimationOnView(this, mImageRefresh);
                startService(Utility.createCallingIntent(this, AppConstant.RUN_LIST));
            }
        }
        if (!mISFromSuggestion && !mISFromOSM) {
            if (!OsmDataService.isSyncInProgress) {
                Utility.applyAnimationOnView(this, mImageRefresh);
                startService(Utility.createCallingIntent(this, AppConstant.RUN_LIST));
            }
        }
    }

}