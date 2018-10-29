package com.disablerouting.route_planner.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
import com.disablerouting.curd_operations.WayDataPreference;
import com.disablerouting.curd_operations.manager.ListGetWayManager;
import com.disablerouting.curd_operations.model.*;
import com.disablerouting.filter.view.FilterActivity;
import com.disablerouting.geo_coding.model.Features;
import com.disablerouting.instructions.InstructionsActivity;
import com.disablerouting.login.LoginActivity;
import com.disablerouting.login.UserPreferences;
import com.disablerouting.login.model.UserSearchModel;
import com.disablerouting.map_base.MapBaseActivity;
import com.disablerouting.osm_activity.manager.OSMManager;
import com.disablerouting.osm_activity.model.GetOsmData;
import com.disablerouting.route_planner.model.NodeItem;
import com.disablerouting.route_planner.model.ProgressModel;
import com.disablerouting.route_planner.model.Steps;
import com.disablerouting.route_planner.presenter.IRoutePlannerScreenPresenter;
import com.disablerouting.route_planner.presenter.IRouteView;
import com.disablerouting.route_planner.presenter.RoutePlannerScreenPresenter;
import com.disablerouting.setting.SettingActivity;
import com.disablerouting.utils.Utility;
import com.google.android.gms.maps.model.LatLng;
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
    private List<NodeItem> mNodeItemListFiltered = new ArrayList<>();
    private HashMap<String, Features> mHashMapObjectFilterRoutingVia = new HashMap<>();
    private HashMap<String, String> mHashMapObjectFilter;
    private IRoutePlannerScreenPresenter mIRoutePlannerScreenPresenter;


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

    private int mButtonSelected = 1;
    private ProgressDialog pDialog;
    private boolean mISFromSuggestion;
    private boolean mISFromOSM;
    private List<Steps> mStepsList = new ArrayList<>();
    boolean mStepListHasData = false;
    private int mTabSelected = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        if (getIntent().hasExtra("FromSuggestion")) {
            mISFromSuggestion = getIntent().getBooleanExtra("FromSuggestion", false);
        }
        if (getIntent().hasExtra("FromOSM")) {
            mISFromOSM = getIntent().getBooleanExtra("FromOSM", false);
        }
        if (mISFromOSM) {
            if (WayDataPreference.getInstance(this) != null) {
                mWayListValidatedData = WayDataPreference.getInstance(this).getValidateWayDataOSM();
                mWayListNotValidatedData = WayDataPreference.getInstance(this).getNotValidatedWayDataOSM();
                mNodeListValidatedData = WayDataPreference.getInstance(this).getValidateDataNodeOSM();
                mNodeListNotValidatedData = WayDataPreference.getInstance(this).getNotValidateDataNodeOSM();

            }
        } else {
            if (WayDataPreference.getInstance(this) != null) {
                mWayListValidatedData = WayDataPreference.getInstance(this).getValidateWayData();
                mWayListNotValidatedData = WayDataPreference.getInstance(this).getNotValidatedWayData();
                mNodeListValidatedData = WayDataPreference.getInstance(this).getValidateDataNode();
                mNodeListNotValidatedData = WayDataPreference.getInstance(this).getNotValidateDataNode();

            }
        }
        mSourceDestinationFragment = SourceDestinationFragment.newInstance(this);
        addFragment(R.id.contentContainer, mSourceDestinationFragment, "");
        mIRoutePlannerScreenPresenter = new RoutePlannerScreenPresenter(this,
                new OSMManager(), new ListGetWayManager(),this);

        if (mISFromSuggestion) {
            addCurrentLocation(18);
            mSourceDestinationFragment.OnFromSuggestion(true);
            mButtonGo.setVisibility(View.GONE);
            mSwitchCompatToogle.setVisibility(View.GONE);
            mRadioGroup.setVisibility(View.VISIBLE);
            mImageCurrentPin.setVisibility(View.GONE);
        }

        if (mISFromOSM) {
            addCurrentLocation(18);
            mSourceDestinationFragment.OnFromSuggestion(false);
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
                if (!mISFromSuggestion && !mISFromOSM) {
                    plotDataOfSourceDestination(null, mSourceAddress, mDestinationAddress, null, true);
                }

            }
            mIsUpdateAgain = true;
        }
        if (!mISFromSuggestion && !mISFromOSM) {
            addCurrentLocation(0);
        }
    }

    @Override
    public void plotDataOnMap(List<List<Double>> geoPointList, List<Steps> stepsList) {
        if (geoPointList != null && stepsList != null) {
            for (int i = 0; i < stepsList.size(); i++) {
                plotDataOfSourceDestination(geoPointList, mSourceAddress, mDestinationAddress, stepsList, true);
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
        if (UserPreferences.getInstance(this) != null && UserPreferences.getInstance(this).getUserSearch() != null) {
            if (UserPreferences.getInstance(this).getUserSearch().getHashMapObjectFilterItem() != null) {
                mHashMapObjectFilterItem = UserPreferences.getInstance(this).getUserSearch().getHashMapObjectFilterItem();
                intentFilter.putExtra(AppConstant.DATA_FILTER_SELECTED, mHashMapObjectFilterItem);
            }
            if (UserPreferences.getInstance(this).getUserSearch().getHashMapFilterForRouting() != null) {
                mHashMapObjectFilterRoutingVia = UserPreferences.getInstance(this).getUserSearch().getHashMapFilterForRouting();
                intentFilter.putExtra(AppConstant.DATA_FILTER_ROUTING_VIA, mHashMapObjectFilterRoutingVia);
            }
            if (UserPreferences.getInstance(this).getUserSearch().getHashMapObjectFilter() != null) {
                mHashMapObjectFilter = UserPreferences.getInstance(this).getUserSearch().getHashMapObjectFilter();
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
        getMapCenter(false);
        clearItemsFromMap();
        Features features = mHashMapObjectFilterRoutingVia.get(AppConstant.DATA_FILTER_ROUTING_VIA);
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

               /* if (mHashMapObjectFilterItem != null && mHashMapObjectFilterItem.size() != 0) {
                    mJsonObjectFilter = createFilter(mHashMapObjectFilter);
                } else {
                    mJsonObjectFilter = null;
                }*/
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
                        showLoader();
                        mIRoutePlannerScreenPresenter.getListData();

                    } else {
                        showLoader();
                        mIRoutePlannerScreenPresenter.getOSMData();
                    }
                }
            }
        }
    }

    private void setUserSearchData() {
        //Save Data in user Preferences
        if (UserPreferences.getInstance(this) != null && UserPreferences.getInstance(this).getAccessToken() != null) {
            if (mSourceAddress == null && UserPreferences.getInstance(this).getUserSearch() != null) {
                mSourceAddress = UserPreferences.getInstance(this).getUserSearch().getSourceAdd();
            }
            if (mDestinationAddress == null && UserPreferences.getInstance(this).getUserSearch() != null) {
                mDestinationAddress = UserPreferences.getInstance(this).getUserSearch().getDestAdd();
            }
            if (mFeaturesSourceAddress == null && UserPreferences.getInstance(this).getUserSearch() != null) {
                mFeaturesSourceAddress = UserPreferences.getInstance(this).getUserSearch().getFeaturesSource();
            }
            if (mFeaturesDestinationAddress == null && UserPreferences.getInstance(this).getUserSearch() != null) {
                mFeaturesDestinationAddress = UserPreferences.getInstance(this).getUserSearch().getFeaturesDest();
            }
            if (mHashMapObjectFilterRoutingVia == null && UserPreferences.getInstance(this).getUserSearch() != null) {
                mHashMapObjectFilterRoutingVia = UserPreferences.getInstance(this).getUserSearch().getHashMapFilterForRouting();
            }
            if (mHashMapObjectFilterItem == null && UserPreferences.getInstance(this).getUserSearch() != null) {
                mHashMapObjectFilterItem = UserPreferences.getInstance(this).getUserSearch().getHashMapObjectFilterItem();
            }
            if (mHashMapObjectFilter == null && UserPreferences.getInstance(this).getUserSearch() != null) {
                mHashMapObjectFilter = UserPreferences.getInstance(this).getUserSearch().getHashMapObjectFilter();
            }
            GeoPoint geoPointSource = new GeoPoint(mFeaturesSourceAddress.getGeometry().getCoordinates().get(0),
                    mFeaturesSourceAddress.getGeometry().getCoordinates().get(1));
            GeoPoint geoPointDestination = new GeoPoint(mFeaturesDestinationAddress.getGeometry().getCoordinates().get(0),
                    mFeaturesDestinationAddress.getGeometry().getCoordinates().get(1));
            if (mHashMapObjectFilter != null && mHashMapObjectFilter.size() != 0) {
                mJsonObjectFilter = createFilter(mHashMapObjectFilter);
            }
            UserSearchModel userSearchModel = new UserSearchModel(mSourceAddress, mDestinationAddress,
                    geoPointSource, geoPointDestination, mFeaturesSourceAddress, mFeaturesDestinationAddress,
                    mHashMapObjectFilterRoutingVia, mHashMapObjectFilterItem, mJsonObjectFilter, mHashMapObjectFilter);
            UserPreferences.getInstance(this).saveUserSearch(userSearchModel);
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

    }

    @Override
    public void hideLoader() {
      //hideProgress();
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radioButtonWays:
                mTabSelected = 3;
                mRadioButtonWays.setTextColor(getResources().getColor(R.color.colorWhite));
                mRadioButtonNodes.setTextColor(getResources().getColor(R.color.colorPrimary));
                PlotWayDataTask mPlotWayDataTaskWays = new PlotWayDataTask();
                mPlotWayDataTaskWays.execute();
                break;

            case R.id.radioButtonNodes:
                mTabSelected = 4;
                mRadioButtonNodes.setTextColor(getResources().getColor(R.color.colorWhite));
                mRadioButtonWays.setTextColor(getResources().getColor(R.color.colorPrimary));
                PlotWayDataTask mPlotWayDataTaskNodes = new PlotWayDataTask();
                mPlotWayDataTaskNodes.execute();
                break;

            default:

        }
    }

    @Override
    public void onOSMDataReceived(String responseBody) {
        if (responseBody != null) {
            GetOsmData getOsmData = Utility.convertDataIntoModel(responseBody);
            Log.e("Nodes", String.valueOf(getOsmData.getOSM().getNode().size()));
            Log.e("Ways", String.valueOf(getOsmData.getOSM().getWays().size()));

            List<NodeReference> nodeReferenceList = new ArrayList<>();
            NodeReference nodeReference = null;
            for (int i = 0; i < getOsmData.getOSM().getNode().size(); i++) {
                nodeReference = new NodeReference();

                nodeReference.setOSMNodeId(getOsmData.getOSM().getNode().get(i).getID());
                nodeReference.setLat(getOsmData.getOSM().getNode().get(i).getLatitude());
                nodeReference.setLon(getOsmData.getOSM().getNode().get(i).getLongitude());
                nodeReference.setVersion(getOsmData.getOSM().getNode().get(i).getVersion());
                nodeReference.setIsForData(AppConstant.OSM_DATA);


                List<Attributes> attributesList = new ArrayList<>();
                Attributes attributes = null;
                if (getOsmData.getOSM().getNode().get(i).getTag() != null &&
                        getOsmData.getOSM().getNode().get(i).getTag().size() != 0) {
                    for (int k = 0; k < getOsmData.getOSM().getNode().get(i).getTag().size(); k++) {
                        attributes = new Attributes();
                        attributes.setKey(getOsmData.getOSM().getNode().get(i).getTag().get(k).getK());
                        attributes.setValue(getOsmData.getOSM().getNode().get(i).getTag().get(k).getV());
                        attributes.setValid(false);
                        attributesList.add(attributes);
                        nodeReference.setAttributes(attributesList);
                    }
                }
                nodeReferenceList.add(nodeReference);
            }

            List<ListWayData> listWayDataListCreated = new ArrayList<>();
            ListWayData listWayData = null;


            for (int i = 0; i < getOsmData.getOSM().getWays().size(); i++) {
                listWayData = new ListWayData();

                listWayData.setOSMWayId(getOsmData.getOSM().getWays().get(i).getID());
                listWayData.setVersion(getOsmData.getOSM().getWays().get(i).getVersion());
                listWayData.setIsValid("false");
                listWayData.setColor(Utility.randomColor());
                listWayData.setIsForData(AppConstant.OSM_DATA);
                ParcelableArrayList stringListCoordinates;

                List<NodeReference> nodeReferencesWay = new ArrayList<>();
                List<ParcelableArrayList> coordinatesList = new LinkedList<>();

                for (int j = 0; getOsmData.getOSM().getWays().get(i).getNdList() != null &&
                        getOsmData.getOSM().getWays().get(i).getNdList().size() != 0 &&
                        j < getOsmData.getOSM().getWays().get(i).getNdList().size(); j++) {

                    for (int k = 0; k < nodeReferenceList.size(); k++) {
                        if (getOsmData.getOSM().getWays().get(i).getNdList().get(j).getRef()
                                .equalsIgnoreCase(nodeReferenceList.get(k).getOSMNodeId())) {

                            nodeReferencesWay.add(nodeReferenceList.get(k));
                            stringListCoordinates = new ParcelableArrayList();
                            stringListCoordinates.add(0, nodeReferenceList.get(k).getLat());
                            stringListCoordinates.add(1, nodeReferenceList.get(k).getLon());
                            coordinatesList.add(stringListCoordinates);
                            break;
                        }
                    }

                }
                listWayData.setCoordinates(coordinatesList);
                listWayData.setNodeReference(nodeReferencesWay);

                List<Attributes> attributesArrayListWay = new ArrayList<>();
                for (int j = 0; getOsmData.getOSM().getWays().get(i).getTagList() != null &&
                        getOsmData.getOSM().getWays().get(i).getTagList().size() != 0 &&
                        j < getOsmData.getOSM().getWays().get(i).getTagList().size(); j++) {
                    Attributes attributesWay = new Attributes();
                    attributesWay.setKey(getOsmData.getOSM().getWays().get(i).getTagList().get(j).getK());
                    attributesWay.setValue(getOsmData.getOSM().getWays().get(i).getTagList().get(j).getV());
                    attributesWay.setValid(false);
                    attributesArrayListWay.add(attributesWay);
                }
                listWayData.setAttributesList(attributesArrayListWay);
                listWayDataListCreated.add(listWayData);
            }

            Log.e("List", String.valueOf(listWayDataListCreated.size()));

            ResponseListWay responseListWay = new ResponseListWay();
            responseListWay.setWayData(listWayDataListCreated);
            if (listWayDataListCreated.size() > 0) {
                responseListWay.setStatus(true);
            } else {
                responseListWay.setStatus(false);
            }
            createListData(responseListWay, true);
        }
    }

    @Override
    public void onListDataReceived(ResponseListWay responseWay) {
        if (responseWay != null) {
            if (responseWay.isStatus()) {
                mWayListValidatedData = new ArrayList<>();
                mWayListNotValidatedData= new ArrayList<>();
                mNodeListValidatedData= new ArrayList<>();
                mNodeListNotValidatedData= new ArrayList<>();

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
            mWayListNotValidatedData = WayDataPreference.getInstance(this).getNotValidatedWayData();
            mWayListValidatedData = WayDataPreference.getInstance(this).getValidateWayData();
            mNodeListValidatedData = WayDataPreference.getInstance(this).getValidateDataNode();
            mNodeListNotValidatedData = WayDataPreference.getInstance(this).getNotValidateDataNode();
            onToggleClickedBanner(false);
        }

    }

    private void createListData(ResponseListWay responseWay, boolean b) {
        if (b) {
            mWayListValidatedData = new ArrayList<>();
            mWayListNotValidatedData= new ArrayList<>();
            mNodeListValidatedData= new ArrayList<>();
            mNodeListNotValidatedData= new ArrayList<>();

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

        }
    }

    @Override
    public void onFailure(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        hideLoader();
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
            if (aVoid != null) {
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
        super.onDestroy();
        if (pDialog != null) {
            pDialog.dismiss();
        }
    }

    @Override
    public void onToggleClickedBanner(boolean isChecked) {
//        clearItemsFromMap();
        mRadioGroup.setVisibility(View.VISIBLE);
        mImageCurrentPin.setVisibility(View.GONE);
        if (!isChecked) {
            mButtonSelected = 1;
            // setZoomMap();
            PlotWayDataTask mPlotWayDataTaskNotValidated = new PlotWayDataTask();
            mPlotWayDataTaskNotValidated.execute();
        } else {
            mButtonSelected = 2;
            //setZoomMap();
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
                mImageCurrentPin.setVisibility(View.GONE);
                mImageViewInfo.setVisibility(View.GONE);
                // clearItemsFromMap();
                if (mNodeListNotValidatedData.size() != 0) {
                    hideLoader();
                    // setZoomMap();
                    PlotWayDataTask mPlotWayDataTaskNotValidated = new PlotWayDataTask();
                    mPlotWayDataTaskNotValidated.execute();
                } else {
                    showLoader();
                }
            } else {
                mRadioGroup.setVisibility(View.GONE);
                mSourceDestinationFragment.onToggleView(false);
                clearItemsFromMap();
                if (mISMapPlotted) {
                    if (mHashMapObjectFilterRoutingVia == null) {
                        if (UserPreferences.getInstance(this) != null && UserPreferences.getInstance(this).getUserSearch() != null) {
                            mHashMapObjectFilterRoutingVia = UserPreferences.getInstance(this).getUserSearch().getHashMapFilterForRouting();
                        }
                    }
                    if (mHashMapObjectFilter == null) {
                        if (UserPreferences.getInstance(this) != null && UserPreferences.getInstance(this).getUserSearch() != null) {
                            mHashMapObjectFilter = UserPreferences.getInstance(this).getUserSearch().getHashMapObjectFilter();
                        }
                    }
                    Features features = mHashMapObjectFilterRoutingVia.get(AppConstant.DATA_FILTER_ROUTING_VIA);
                    if (UserPreferences.getInstance(this) != null && UserPreferences.getInstance(this).getUserSearch() != null) {
                        if (mHashMapObjectFilter != null) {
                            mJsonObjectFilter = createFilter(mHashMapObjectFilter);
                        }
                    }
                    mSourceDestinationFragment.plotRoute(mJsonObjectFilter, features);
                }
                mButtonGo.setVisibility(View.VISIBLE);
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
                getMapCenter(true);
            } else {
                mImageCurrentPin.setVisibility(View.GONE);
                getMapCenter(false);
            }
        } else {
            mImageCurrentPin.setVisibility(View.GONE);
            getMapCenter(false);
        }
    }


}