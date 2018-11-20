package com.disablerouting.route_planner.view;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListPopupWindow;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.base.BaseFragmentImpl;
import com.disablerouting.common.AppConstant;
import com.disablerouting.geo_coding.manager.GeoCodingManager;
import com.disablerouting.geo_coding.model.Features;
import com.disablerouting.geo_coding.model.GeoCodingResponse;
import com.disablerouting.login.UserPreferences;
import com.disablerouting.map_base.OnFeedBackListener;
import com.disablerouting.route_planner.adapter.CustomListAdapter;
import com.disablerouting.route_planner.manager.DirectionsManager;
import com.disablerouting.route_planner.manager.NodeManager;
import com.disablerouting.route_planner.model.DirectionsResponse;
import com.disablerouting.route_planner.model.NodeResponse;
import com.disablerouting.route_planner.presenter.ISourceDestinationScreenPresenter;
import com.disablerouting.route_planner.presenter.SourceDestinationScreenPresenter;
import com.disablerouting.utils.Utility;
import com.disablerouting.widget.CustomAutoCompleteTextView;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.util.List;
import java.util.Objects;

public class SourceDestinationFragment extends BaseFragmentImpl implements ISourceDestinationViewFragment,
        TextView.OnEditorActionListener, AdapterView.OnItemClickListener, OnFeedBackListener  {

    @BindView(R.id.edt_source_add)
    CustomAutoCompleteTextView mEditTextSource;

    @BindView(R.id.edt_dest_add)
    CustomAutoCompleteTextView mEditTextDestination;

    @BindView(R.id.clear_source_address)
    ImageView mSourceAddressClear;

    @BindView(R.id.clear_destination_address)
    ImageView mDestinationAddressClear;

    @BindView(R.id.fetch_current_source_address)
    ImageView mSourceAddressFetch;

    @BindView(R.id.fetch_current_destination_address)
    ImageView mDestinationAddressFetch;

    @BindView(R.id.rel_source_destination)
    RelativeLayout mRelativeLayoutSourceDestination;

    @BindView(R.id.ll_time_distance)
    LinearLayout mLinearLayoutTimeDistance;

    @BindView(R.id.txv_time)
    TextView mTextViewTime;

    @BindView(R.id.txv_km)
    TextView mTextViewKM;

    @BindView(R.id.txv_accent)
    TextView mTextViewAccent;

    @BindView(R.id.txv_decent)
    TextView mTextViewDecent;

    @BindView(R.id.toggle_way_sd)
    Switch mToogleWAY;

    @BindView(R.id.rel_toogle)
    RelativeLayout mRelativeLayoutToogle;

    @BindView(R.id.txv_title)
    TextView mTextViewTitle;

    @BindView(R.id.ll_source)
    LinearLayout mLinearLayoutSource;

    @BindView(R.id.ll_dest)
    LinearLayout mLinearLayoutDestination;

    @BindView(R.id.ll_source_dest)
    LinearLayout mLinearLayoutSourceDestination;

    private static final int SEARCH_TEXT_CHANGED = 1000;
    private String mCurrentLocation = null;

    private ISourceDestinationScreenPresenter mISourceDestinationScreenPresenter;
    private GeoPoint mGeoPointSource;
    private GeoPoint mGeoPointDestination;
    private static OnSourceDestinationListener mOnSourceDestinationListener;

    private CustomListAdapter mAddressListAdapter;
    private List<Features> mFeaturesResultSearch;
    private ListPopupWindow mListPopupWindow;
    private Features mFeaturesSource;
    private Features mFeaturesDestination;
    private boolean mIsTextInputManually = false;
    private JSONObject mJSONObjectFilter;
    private Features mFeaturesRouteVia;
    private boolean mIsFromSuggestion;
    private boolean mIsFromOSM=false;
    private DirectionsResponse mDirectionsResponse=null;

    @SuppressLint("HandlerLeak")
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (!msg.obj.equals("")) {
                if (mListPopupWindow != null && mListPopupWindow.isShowing()) {
                    mListPopupWindow.dismiss();
                }
                if (mEditTextSource.hasFocus() && mEditTextSource != null && !mEditTextSource.getText().toString().equalsIgnoreCase("")) {
                    mISourceDestinationScreenPresenter.getGeoCodeDataForward(mEditTextSource.getText().toString());
                }
                if (mEditTextDestination.hasFocus() && mEditTextDestination != null && !mEditTextDestination.getText().toString().equalsIgnoreCase("")) {
                    mISourceDestinationScreenPresenter.getGeoCodeDataForward(mEditTextDestination.getText().toString());
                }
            }

        }
    };


    public static SourceDestinationFragment newInstance(OnSourceDestinationListener onSourceDestinationListener) {
        mOnSourceDestinationListener = onSourceDestinationListener;
        Bundle args = new Bundle();
        SourceDestinationFragment fragment = new SourceDestinationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @OnClick(R.id.fetch_current_source_address)
    public void fetchCurrentSourceAdd() {
        mIsTextInputManually = false;
        if (mEditTextSource.hasFocus() && mEditTextSource != null && mEditTextSource.getText().toString().equalsIgnoreCase("")) {
           // mISourceDestinationScreenPresenter.getCoordinatesData("", mCurrentLocation, 0);
            if(mCurrentLocation!=null) {
                String[] location = mCurrentLocation.split(",");
                mISourceDestinationScreenPresenter.getGeoCodeDataReverse(Double.parseDouble(location[1]), Double.parseDouble(location[0]));
            }
        }

    }

    @OnClick(R.id.fetch_current_destination_address)
    public void fetchCurrentDestinationAdd() {
        mIsTextInputManually = false;
        handler.removeMessages(SEARCH_TEXT_CHANGED);
        if (mEditTextDestination.hasFocus() && mEditTextDestination != null && mEditTextDestination.getText().toString().equalsIgnoreCase("")) {
           // mISourceDestinationScreenPresenter.getCoordinatesData("", mCurrentLocation, 0);
            if(mCurrentLocation!=null) {
                String[] location = mCurrentLocation.split(",");
                mISourceDestinationScreenPresenter.getGeoCodeDataReverse(Double.parseDouble(location[1]), Double.parseDouble(location[0]));
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mISourceDestinationScreenPresenter = new SourceDestinationScreenPresenter(this,
                new DirectionsManager(), new GeoCodingManager(), new NodeManager());

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_source_destination, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        addFocusChangeListener();
        addListener();
        if(mIsFromSuggestion) {
            mLinearLayoutSourceDestination.setVisibility(View.GONE);
            mRelativeLayoutToogle.setVisibility(View.VISIBLE);
            mOnSourceDestinationListener.onToggleClickedBanner(false);
            mTextViewTitle.setText(getResources().getString(R.string.not_validated));
        }
        else {
            if(mIsFromOSM) {
                mLinearLayoutSourceDestination.setVisibility(View.GONE);
                mRelativeLayoutToogle.setVisibility(View.VISIBLE);
                mToogleWAY.setVisibility(View.INVISIBLE);
                mOnSourceDestinationListener.onToggleClickedBanner(false);
                mTextViewTitle.setText(getResources().getString(R.string.not_validated));
            }
        }
        if(!mIsFromSuggestion){
            if(UserPreferences.getInstance(getContext())!=null && UserPreferences.getInstance(getContext()).getUserSearch()!=null){
                mEditTextSource.setText(UserPreferences.getInstance(getContext()).getUserSearch().getSourceAdd());
                mEditTextDestination.setText(UserPreferences.getInstance(getContext()).getUserSearch().getDestAdd());
                mGeoPointSource=UserPreferences.getInstance(getContext()).getUserSearch().getSourceGeoPoint();
                mGeoPointDestination=UserPreferences.getInstance(getContext()).getUserSearch().getDestGeoPoint();
                mFeaturesSource = UserPreferences.getInstance(getContext()).getUserSearch().getFeaturesSource();
                mFeaturesDestination  = UserPreferences.getInstance(getContext()).getUserSearch().getFeaturesDest();
                //JSONObject jsonObject = UserPreferences.getInstance(getContext()).getUserSearch().getJSONObjectFiter();
                //HashMap<String, Features> mRoutingVia = UserPreferences.getInstance(getContext()).getUserSearch().getHashMapFilterForRouting();
                //Features features = mRoutingVia.get(AppConstant.DATA_FILTER_ROUTING_VIA);
                //plotRoute(jsonObject,features);
            }
        }
    }

    public void addFocusChangeListener() {
        mEditTextSource.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b && mEditTextSource.getText().toString().trim().length() == 0) {
                    mSourceAddressFetch.setVisibility(View.VISIBLE);

                } else {
                    mSourceAddressFetch.setVisibility(View.GONE);
                    mDestinationAddressFetch.setVisibility(View.GONE);
                }
                mOnSourceDestinationListener.onClickField(true); //Adding pin fetch address

            }
        });
        mEditTextDestination.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b && mEditTextDestination.getText().toString().trim().length() == 0) {
                    mDestinationAddressFetch.setVisibility(View.VISIBLE);

                } else {
                    mDestinationAddressFetch.setVisibility(View.GONE);
                    mSourceAddressFetch.setVisibility(View.GONE);

                }
                mOnSourceDestinationListener.onClickField(true); //Adding pin fetch address

            }
        });
    }

    public void callForDestination(GeoPoint geoPointCurrent, GeoPoint geoPointSource, GeoPoint geoPointDestination, JSONObject jsonObject, Features featuresRouteVia) {
        Utility.hideSoftKeyboard((AppCompatActivity) getActivity());
        mGeoPointSource = geoPointSource;
        mGeoPointDestination = geoPointDestination;
        if (mGeoPointSource.getLatitude() != mGeoPointDestination.getLatitude() &&
                mGeoPointSource.getLongitude() != mGeoPointDestination.getLongitude()) {
            if (mEditTextSource != null && !mEditTextSource.getText().toString().isEmpty() &&
                    mEditTextDestination != null && !mEditTextDestination.getText().toString().isEmpty()) {

                String coordinates = null;
                if(featuresRouteVia!=null && featuresRouteVia.getGeometry()!=null && featuresRouteVia.getGeometry().getCoordinates()!=null){
                   GeoPoint geoPointRouteVia = new GeoPoint(featuresRouteVia.getGeometry().getCoordinates().get(0),
                           featuresRouteVia.getGeometry().getCoordinates().get(1));
                    coordinates = mGeoPointSource + "|" +geoPointRouteVia+ "|"+mGeoPointDestination;

                }else {
                    coordinates = mGeoPointSource + "|" + mGeoPointDestination;
                }
                String profileType = AppConstant.PROFILE_WHEEL_CHAIR;
                mISourceDestinationScreenPresenter.getDirectionsData(coordinates, profileType, jsonObject);
            }
            handler.removeMessages(SEARCH_TEXT_CHANGED);
        }
    }


    /**
     * Set Text change listener for edit text
     */
    private void addListener() {
        mEditTextSource.addTextChangedListener(mSourceWatcher);
        mEditTextDestination.addTextChangedListener(mDestWatcher);
    }

    @OnClick(R.id.btn_filter)
    public void onGoFilter() {
        mOnSourceDestinationListener.onApplyFilter();
    }


    public void plotRoute(JSONObject jsonObject , Features featuresRouteVia) {
        if (!mEditTextSource.getText().toString().isEmpty() && !mEditTextDestination.getText().toString().isEmpty()) {
            if (mGeoPointSource != null && mGeoPointDestination != null && mGeoPointSource.getLatitude() != mGeoPointDestination.getLatitude() &&
                    mGeoPointSource.getLongitude() != mGeoPointDestination.getLongitude()) {
                mOnSourceDestinationListener.onSourceDestinationSelected(mFeaturesSource, mFeaturesDestination);
                String bBox = mGeoPointSource.getLatitude() + "," + mGeoPointSource.getLongitude() + "," +
                        mGeoPointDestination.getLatitude() + "," + mGeoPointDestination.getLongitude();

                getNodes(bBox); // API call for set markers of amenity
                mJSONObjectFilter = jsonObject;
                mFeaturesRouteVia = featuresRouteVia;
                callForDestination(null, mGeoPointSource, mGeoPointDestination, mJSONObjectFilter,mFeaturesRouteVia);
            } else {
                showSnackBar(Objects.requireNonNull(getContext()).getResources().getString(R.string.error_source_destination_same));
            }
        }
    }

    @OnClick(R.id.img_back)
    public void onBackClick() {
        hideLoader();
        mOnSourceDestinationListener.onBackPress();
        Utility.hideSoftKeyboard((AppCompatActivity) getActivity());
        clearSourceComplete();
        clearDestinationComplete();
    }

    @OnClick(R.id.clear_source_address)
    public void clearSource() {
        mSourceAddressClear.setVisibility(View.GONE);
        mSourceAddressFetch.setVisibility(View.GONE);
        mEditTextSource.setText("");
        mOnSourceDestinationListener.onSourceClickWhileNavigationRunning();
        mOnSourceDestinationListener.onClickField(true);
    }

    @OnClick(R.id.clear_destination_address)
    public void clearDestination() {
        mDestinationAddressClear.setVisibility(View.GONE);
        mDestinationAddressFetch.setVisibility(View.GONE);
        mEditTextDestination.setText("");
        mOnSourceDestinationListener.onSourceClickWhileNavigationRunning();
        mOnSourceDestinationListener.onClickField(true);
    }

    @Override
    public void showLoader() {
        if (getActivity() != null && !getActivity().isFinishing()) {
            showProgress();
        }
    }

    @Override
    public void hideLoader() {
        hideProgress();
    }

    @Override
    public void onDirectionDataReceived(DirectionsResponse data) {
        if (data != null && data.getFeaturesList() != null && data.getFeaturesList().size() != 0
                && data.getFeaturesList().get(0).getGeometry() != null && data.getFeaturesList().get(0).getProperties().getSegmentList().get(0).getStepsList() != null) {
            mDirectionsResponse=data;
            for (int i = 0; i< data.getFeaturesList().get(0).getProperties().getSegmentList().size(); i++){
                    mOnSourceDestinationListener.plotDataOnMap(data.getFeaturesList().get(0).getGeometry().getCoordinates(),
                            data.getFeaturesList().get(0).getProperties().getSegmentList().get(i).getStepsList(),
                            data.getInfo().getQuery().getCoordinatesList().size());

            }
            if(data.getFeaturesList().get(0).getProperties().getSegmentList().size()>1 && data.getInfo()!=null &&
                    data.getInfo().getQuery()!=null &&
                    data.getInfo().getQuery().getCoordinatesList()!=null &&
                    data.getInfo().getQuery().getCoordinatesList().get(1)!=null) {
                GeoPoint geoPointMid = new GeoPoint(data.getInfo().getQuery().getCoordinatesList().get(1).get(1),
                        data.getInfo().getQuery().getCoordinatesList().get(1).get(0));

                mOnSourceDestinationListener.plotMidWayRouteMarker(geoPointMid);
            }

            mLinearLayoutTimeDistance.setVisibility(View.VISIBLE);
            if (data.getFeaturesList().get(0).getProperties().getSummary() != null) {
                if (data.getFeaturesList().get(0).getProperties().getSummary().get(0).getDuration() != 0) {
                    int time = data.getFeaturesList().get(0).getProperties().getSummary().get(0).getDuration();
                    int hours = time / 3600;
                    int minutes = (time % 3600) / 60;
                    if (hours == 0) {
                        mTextViewTime.setText(new StringBuilder().append(minutes).append(" ").append(getContext().getResources().getString(R.string.min)).toString());
                    } else {
                        mTextViewTime.setText(new StringBuilder().append(hours).append(" ").append(getContext().getResources().getString(R.string.hr)).append(" ").append(minutes).append(getContext().getResources().getString(R.string.min)).toString());
                    }

                } else {
                    mTextViewTime.setText(String.format("%s%s", "--", getContext().getResources().getString(R.string.min)));
                }
                if (data.getFeaturesList().get(0).getProperties().getSummary().get(0).getDistance() != 0) {
                    String distance = String.valueOf(Utility.trimTWoDecimalPlaces(data.getFeaturesList().get(0).getProperties().getSummary().get(0).getDistance() / 1000));
                    mTextViewKM.setText(new StringBuilder().append(distance).append(" ").append(getContext().getResources().getString(R.string.km)).toString());
                } else {
                    mTextViewKM.setText(new StringBuilder().append("--").append(" ").append(getContext().getResources().getString(R.string.km)).toString());
                }

                if(data.getFeaturesList().get(0).getProperties().getSummary().get(0).getAscent()!=0){
                    String ascent = String.valueOf(Utility.trimTWoDecimalPlaces(data.getFeaturesList().get(0).getProperties().getSummary().get(0).getAscent()/100));
                    mTextViewAccent.setText(new StringBuilder().append(ascent).append(" ").append(getContext().getResources().getString(R.string.meter)).toString());

                }else {
                    mTextViewAccent.setText(new StringBuilder().append("--").append(" ").append(getContext().getResources().getString(R.string.meter)).toString());

                }
                if(data.getFeaturesList().get(0).getProperties().getSummary().get(0).getDescent()!=0){
                    String descent = String.valueOf(Utility.trimTWoDecimalPlaces(data.getFeaturesList().get(0).getProperties().getSummary().get(0).getDescent()/100));
                    mTextViewDecent.setText(new StringBuilder().append(descent).append(" ").append(getContext().getResources().getString(R.string.meter)).toString());

                }else {
                    mTextViewDecent.setText(new StringBuilder().append("--").append(" ").append(getContext().getResources().getString(R.string.meter)).toString());

                }
            }
        } else {
            mLinearLayoutTimeDistance.setVisibility(View.GONE);

        }

    }

    @Override
    public void onFailureDirection(String error) {
        Utility.hideSoftKeyboard((AppCompatActivity) getActivity());
        showSnackBar(error);
    }

    @Override
    public void onGeoDataDataReceived(GeoCodingResponse data, boolean isForCurrentLoc) {
        handler.removeMessages(SEARCH_TEXT_CHANGED);
        if (isForCurrentLoc && data != null && data.getFeatures() != null && data.getFeatures().size() == 1) {
            Utility.hideSoftKeyboard((AppCompatActivity) getActivity());
            mFeaturesResultSearch = data.getFeatures();
            if (mEditTextSource.hasFocus()) {
                mEditTextSource.removeTextChangedListener(mSourceWatcher);
                mEditTextSource.setText(mFeaturesResultSearch.get(0).getProperties().toString());
                mGeoPointSource = new GeoPoint(mFeaturesResultSearch.get(0).getGeometry().getCoordinates().get(0),
                        mFeaturesResultSearch.get(0).getGeometry().getCoordinates().get(1));
                mFeaturesSource = mFeaturesResultSearch.get(0);
                mEditTextSource.addTextChangedListener(mSourceWatcher);
                updateEditControls(mEditTextSource, mSourceAddressFetch, mSourceAddressClear);

            } else if (mEditTextDestination.hasFocus()) {
                mEditTextDestination.removeTextChangedListener(mDestWatcher);
                mEditTextDestination.setText(mFeaturesResultSearch.get(0).getProperties().toString());
                mGeoPointDestination = new GeoPoint(mFeaturesResultSearch.get(0).getGeometry().getCoordinates().get(0),
                        mFeaturesResultSearch.get(0).getGeometry().getCoordinates().get(1));
                mFeaturesDestination = mFeaturesResultSearch.get(0);
                mEditTextDestination.addTextChangedListener(mDestWatcher);
                updateEditControls(mEditTextDestination, mDestinationAddressFetch, mDestinationAddressClear);
            }

        } else if (data != null && data.getFeatures() != null && !data.getFeatures().isEmpty()) {
            mFeaturesResultSearch = data.getFeatures();
            mAddressListAdapter = new CustomListAdapter(getContext(), R.layout.address_item_view, data.getFeatures());
            setListPopUp(mRelativeLayoutSourceDestination);
        }
    }

    @Override
    public void onFailureGeoCoding(String error) {
        Utility.hideSoftKeyboard((AppCompatActivity) getActivity());
        if (error.equalsIgnoreCase("No address found.")) {
            showSnackBar(getResources().getString(R.string.no_address_found));
        } else {
            showSnackBar(error);
        }
    }

    public void getNodes(String bBox) {
        mISourceDestinationScreenPresenter.getNodesData(bBox);
    }

    @Override
    public void onNodeDataReceived(NodeResponse data) {
        if (data != null) {
            mOnSourceDestinationListener.plotNodesOnMap(data.getNodes());
        }
    }

    @Override
    public void onFailureNode(String error) {
        Utility.hideSoftKeyboard((AppCompatActivity) getActivity());
        showSnackBar(error);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utility.hideSoftKeyboard((AppCompatActivity) getActivity());
        mISourceDestinationScreenPresenter.disconnect();
    }

    @Override
    public void onPause() {
        super.onPause();
        Utility.hideSoftKeyboard((AppCompatActivity) getActivity());
    }

    public void clearSourceComplete() {
        mEditTextSource.setText("");
        mSourceAddressFetch.setVisibility(View.GONE);
    }

    public void clearDestinationComplete() {
        mEditTextDestination.setText("");
        mDestinationAddressFetch.setVisibility(View.GONE);
    }

    @OnClick(R.id.img_swap)
    public void swapDataOfViews() {
        if (!mEditTextSource.getText().toString().isEmpty() || !mEditTextDestination.getText().toString().isEmpty()) {
            performToggleAddress();
            mOnSourceDestinationListener.onSwapData();
            if (mEditTextSource.getText().toString().isEmpty()) {
                mSourceAddressClear.setVisibility(View.GONE);
                mSourceAddressFetch.setVisibility(View.VISIBLE);
            } else {
                mSourceAddressClear.setVisibility(View.VISIBLE);
                mSourceAddressFetch.setVisibility(View.GONE);

            }
            if (mEditTextDestination.getText().toString().isEmpty()) {
                mDestinationAddressClear.setVisibility(View.GONE);
                mDestinationAddressFetch.setVisibility(View.VISIBLE);
            } else {
                mDestinationAddressClear.setVisibility(View.VISIBLE);
                mDestinationAddressFetch.setVisibility(View.GONE);

            }
        }
    }

    /**
     * Swap address when toggle
     */
    private void performToggleAddress() {
        //Change string data on edit text
        String sourceData = mEditTextSource.getText().toString();
        mEditTextSource.setText((mEditTextDestination.getText().toString()));
        mEditTextDestination.setText(sourceData);

        //Change model data on edit text
        Features featuresSource = mFeaturesSource;
        mFeaturesSource = mFeaturesDestination;
        mFeaturesDestination = featuresSource;

        //Change geo points
        GeoPoint geoPoint = mGeoPointSource;
        mGeoPointSource = mGeoPointDestination;
        mGeoPointDestination = geoPoint;

        plotRoute(mJSONObjectFilter , mFeaturesRouteVia);

    }


    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_DONE) {
            Utility.hideSoftKeyboard((AppCompatActivity) getActivity());
        }

        return false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnSourceDestinationListener = null;
    }

    /**
     * Show data of search result in pop up
     *
     * @param anchor view below which placed  result
     */
    private void setListPopUp(View anchor) {
        mListPopupWindow = new ListPopupWindow(getContext());
        mListPopupWindow.setAnchorView(anchor);
        mListPopupWindow.setAnimationStyle(R.style.popup_window_animation);
        int height = Utility.calculatePopUpHeight(getContext());
        mListPopupWindow.setHeight(height / 2);
        mListPopupWindow.setWidth(ListPopupWindow.MATCH_PARENT);
        mListPopupWindow.setAdapter(mAddressListAdapter);
        mListPopupWindow.setOnItemClickListener(this);
        mListPopupWindow.show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Utility.hideSoftKeyboard((AppCompatActivity) getActivity());
        mListPopupWindow.dismiss();
        if (mEditTextSource.hasFocus()) {
            mEditTextSource.setText(mFeaturesResultSearch.get(i).getProperties().toString());
            mGeoPointSource = new GeoPoint(mFeaturesResultSearch.get(i).getGeometry().getCoordinates().get(0),
                    mFeaturesResultSearch.get(i).getGeometry().getCoordinates().get(1));
            mFeaturesSource = mFeaturesResultSearch.get(i);

        } else if (mEditTextDestination.hasFocus()) {
            mEditTextDestination.setText(mFeaturesResultSearch.get(i).getProperties().toString());
            mGeoPointDestination = new GeoPoint(mFeaturesResultSearch.get(i).getGeometry().getCoordinates().get(0),
                    mFeaturesResultSearch.get(i).getGeometry().getCoordinates().get(1));
            mFeaturesDestination = mFeaturesResultSearch.get(i);

        }
        handler.removeMessages(SEARCH_TEXT_CHANGED);
    }

    /**
     * Get current location
     *
     * @param geoPointCurrent current location geo points
     */
    public void onUpdateLocation(GeoPoint geoPointCurrent) {
        if (geoPointCurrent != null) {
            mCurrentLocation = geoPointCurrent.getLatitude() + "," + geoPointCurrent.getLongitude();
        } else {
            mCurrentLocation = null;
        }
    }

    private TextWatcher mSourceWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable str) {
            updateEditControls(mEditTextSource, mSourceAddressFetch, mSourceAddressClear);
            if (str.toString().length() > 3 && mIsTextInputManually) {
                handler.removeMessages(SEARCH_TEXT_CHANGED);
                handler.sendMessageDelayed(handler.obtainMessage(SEARCH_TEXT_CHANGED, str.toString()), 500);
            } else {
                mIsTextInputManually = true;
                handler.removeMessages(SEARCH_TEXT_CHANGED);
                handler.sendMessageDelayed(handler.obtainMessage(SEARCH_TEXT_CHANGED, ""), 500);
            }

        }
    };

    private TextWatcher mDestWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable str) {
            updateEditControls(mEditTextDestination, mDestinationAddressFetch, mDestinationAddressClear);
            if (str.toString().length() > 3 && mIsTextInputManually) {
                handler.removeMessages(SEARCH_TEXT_CHANGED);
                handler.sendMessageDelayed(handler.obtainMessage(SEARCH_TEXT_CHANGED, str.toString()), 500);
            } else {
                mIsTextInputManually = true;
                handler.removeMessages(SEARCH_TEXT_CHANGED);
                handler.sendMessageDelayed(handler.obtainMessage(SEARCH_TEXT_CHANGED, ""), 500);
            }

        }
    };

    /**
     * Update Controls of edit text
     *
     * @param mTV   TextView
     * @param loc   location fetch image
     * @param clear clear text image
     */
    private void updateEditControls(CustomAutoCompleteTextView mTV, View loc, View clear) {
        if (mTV.hasFocus() && mTV.getText().toString().isEmpty()) {
            clear.setVisibility(View.GONE);
            loc.setVisibility(View.VISIBLE);
        } else if (mTV.hasFocus() && mTV.getText().toString().trim().length() >= 1) {
            loc.setVisibility(View.GONE);
            clear.setVisibility(View.VISIBLE);
        } else {
            loc.setVisibility(View.GONE);
            clear.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFeedBackClick(double longitude, double latitude) {

    }

    @Override
    public void onMapPlotted() {
        //Nothing to do
    }

    @Override
    public void onDragClicked(GeoPoint geoPoint) {

    }


    public void onToggleView(boolean isToggled) {
        if(isToggled){
            mOnSourceDestinationListener.onClickField(false); //Adding pin fetch address
            Utility.collapse(mLinearLayoutSourceDestination);
            if(mLinearLayoutTimeDistance.getVisibility()==View.VISIBLE) {
                Utility.collapse(mLinearLayoutTimeDistance);
            }

            Utility.expand(mRelativeLayoutToogle);

        }else {
            Utility.expand(mLinearLayoutSourceDestination);
            if(mDirectionsResponse!=null) {
                Utility.expand(mLinearLayoutTimeDistance);
            }
            Utility.collapse(mRelativeLayoutToogle);

        }

    }

    @OnClick(R.id.toggle_way_sd)
    public void onTitleToggleClicked(){
        if(!mToogleWAY.isChecked()) {
            mOnSourceDestinationListener.onToggleClickedBanner(false);
            mTextViewTitle.setText(getResources().getString(R.string.not_validated));
        }else {
            mOnSourceDestinationListener.onToggleClickedBanner(true);
            mTextViewTitle.setText(getResources().getString(R.string.validated));

        }

    }

    public void OnFromSuggestion(boolean isFromSuggestion) {
        if(isFromSuggestion) {
            mIsFromSuggestion = true;
            mIsFromOSM=false;
        }else {
            mIsFromOSM = true;
        }
    }

    public void setDataWhenDragging(GeoPoint geoPoint ){
        if(geoPoint!=null){
            mISourceDestinationScreenPresenter.getGeoCodeDataReverse(geoPoint.getLatitude(), geoPoint.getLongitude());
        }

    }

}
