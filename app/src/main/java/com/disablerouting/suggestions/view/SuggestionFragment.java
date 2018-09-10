package com.disablerouting.suggestions.view;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.disablerouting.curd_operations.manager.GetWayManager;
import com.disablerouting.curd_operations.model.RequestGetWay;
import com.disablerouting.curd_operations.model.ResponseWay;
import com.disablerouting.geo_coding.manager.GeoCodingManager;
import com.disablerouting.geo_coding.model.Features;
import com.disablerouting.geo_coding.model.GeoCodingResponse;
import com.disablerouting.route_planner.adapter.CustomListAdapter;
import com.disablerouting.route_planner.manager.DirectionsManager;
import com.disablerouting.route_planner.model.DirectionsResponse;
import com.disablerouting.setting.SettingActivity;
import com.disablerouting.suggestions.presenter.ISuggestionScreenPresenter;
import com.disablerouting.suggestions.presenter.SuggestionPresenter;
import com.disablerouting.utils.Utility;
import com.disablerouting.widget.CustomAutoCompleteTextView;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.util.List;

public class SuggestionFragment extends BaseFragmentImpl implements ISuggestionFragment,
        AdapterView.OnItemClickListener ,TextView.OnEditorActionListener , RadioGroup.OnCheckedChangeListener{

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

    @BindView(R.id.ll_result)
    LinearLayout mLinearLayoutResult;

    @BindView(R.id.ll_source)
    LinearLayout mLinearLayoutSource;

    @BindView(R.id.ll_dest)
    LinearLayout mLinearLayoutDest;

    @BindView(R.id.txv_sub_title)
    TextView mSubTitle;

    @BindView(R.id.txv_sub_title_no)
    Button mSubTitleNo;

    @BindView(R.id.txv_sub_title_yes)
    Button mSubTitleYes;

    @BindView(R.id.radioGroup)
    RadioGroup mRadioGroup;

    @BindView(R.id.radioButtonValidated)
    RadioButton mRadioButtonValidated;

    @BindView(R.id.radioButtonNotValidated)
    RadioButton mRadioButtonNotValidated;

    private int mButtonSelected;

    private static final int SEARCH_TEXT_CHANGED = 1000;
    private String mCurrentLocation = null;
    private static OnSuggestionListener mSuggestionListener;
    private GeoPoint mGeoPointSource;
    private GeoPoint mGeoPointDestination;
    private Features mFeaturesSource;
    private Features mFeaturesDestination;
    private CustomListAdapter mAddressListAdapter;
    private List<Features> mFeaturesResultSearch;
    private ListPopupWindow mListPopupWindow;
    private boolean mIsTextInputManually = false;
    private ISuggestionScreenPresenter mISuggestionScreenPresenter;

    public static SuggestionFragment newInstance(OnSuggestionListener onSuggestionListener) {
        mSuggestionListener = onSuggestionListener;
        Bundle args = new Bundle();
        SuggestionFragment fragment = new SuggestionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("HandlerLeak")
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (!msg.obj.equals("")) {
                if (mListPopupWindow != null && mListPopupWindow.isShowing()) {
                    mListPopupWindow.dismiss();
                }
                if (mEditTextSource.hasFocus() && !mEditTextSource.getText().toString().equalsIgnoreCase("")) {
                  // mISuggestionScreenPresenter.getCoordinatesData(mEditTextSource.getText().toString(), "", 10);
                mISuggestionScreenPresenter.getGeoCodeDataForward(mEditTextSource.getText().toString());
                }
                if (mEditTextDestination.hasFocus() && !mEditTextDestination.getText().toString().equalsIgnoreCase("")) {
                   // mISuggestionScreenPresenter.getCoordinatesData(mEditTextDestination.getText().toString(), "", 10);
                    mISuggestionScreenPresenter.getGeoCodeDataForward(mEditTextDestination.getText().toString());

                }
            }

        }
    };

    @OnClick(R.id.fetch_current_source_address)
    public void fetchCurrentSourceAdd() {
        mIsTextInputManually = false;
        if (mEditTextSource.hasFocus() && mEditTextSource.getText().toString().equalsIgnoreCase("")) {
          //  mISuggestionScreenPresenter.getCoordinatesData("", mCurrentLocation, 0);
            if(mCurrentLocation!=null) {
                String[] location = mCurrentLocation.split(",");
                mISuggestionScreenPresenter.getGeoCodeDataReverse(Double.parseDouble(location[1]), Double.parseDouble(location[0]));
            }
        }

    }

    @OnClick(R.id.fetch_current_destination_address)
    public void fetchCurrentDestinationAdd() {
        mIsTextInputManually = false;
        handler.removeMessages(SEARCH_TEXT_CHANGED);
        if (mEditTextDestination.hasFocus() && mEditTextDestination.getText().toString().equalsIgnoreCase("")) {
           //mISuggestionScreenPresenter.getCoordinatesData("", mCurrentLocation, 0);
            if(mCurrentLocation!=null) {
                String[] location = mCurrentLocation.split(",");
                mISuggestionScreenPresenter.getGeoCodeDataReverse(Double.parseDouble(location[1]), Double.parseDouble(location[0]));
            }
        }
    }
    @OnClick(R.id.clear_source_address)
    public void clearSource() {
        mSourceAddressClear.setVisibility(View.GONE);
        mSourceAddressFetch.setVisibility(View.GONE);
        mEditTextSource.setText("");
        mEditTextDestination.setText("");
        mSubTitle.setText(R.string.tell_us_where_you_are);
        mSubTitleNo.setVisibility(View.GONE);
        mSubTitleYes.setVisibility(View.GONE);
        mLinearLayoutDest.setVisibility(View.GONE);
        mSuggestionListener.onGoButtonVisibility(false);
        mSuggestionListener.onClearItemsOfMap();

    }

    @OnClick(R.id.clear_destination_address)
    public void clearDestination() {
        mDestinationAddressClear.setVisibility(View.GONE);
        mDestinationAddressFetch.setVisibility(View.GONE);
        mEditTextDestination.setText("");

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mISuggestionScreenPresenter = new SuggestionPresenter(this, new DirectionsManager(), new GeoCodingManager(), new GetWayManager());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_suggestion, container, false);
    }
    @Override
    public void showLoader() {
        if (getActivity() != null && !getActivity().isFinishing()) {
            showProgress();
        }
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        addFocusChangeListener();
        addListener();
        mRadioGroup.setOnCheckedChangeListener(this);
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
            }
        });
    }
    /**
     * Set Text change listener for edit text
     */
    private void addListener() {
        mEditTextSource.addTextChangedListener(mSourceWatcher);
        mEditTextDestination.addTextChangedListener(mDestWatcher);
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
            if(mLinearLayoutDest.getVisibility()==View.GONE){
                mSubTitle.setText(R.string.give_feedback_title);
                mSubTitleNo.setVisibility(View.VISIBLE);
                mSubTitleYes.setVisibility(View.VISIBLE);
            }
            mGeoPointSource = new GeoPoint(mFeaturesResultSearch.get(i).getGeometry().getCoordinates().get(0),
                    mFeaturesResultSearch.get(i).getGeometry().getCoordinates().get(1));
            mFeaturesSource = mFeaturesResultSearch.get(0);

        } else if (mEditTextDestination.hasFocus()) {
            mEditTextDestination.setText(mFeaturesResultSearch.get(i).getProperties().toString());

            mGeoPointDestination = new GeoPoint(mFeaturesResultSearch.get(i).getGeometry().getCoordinates().get(0),
                    mFeaturesResultSearch.get(i).getGeometry().getCoordinates().get(1));
            mFeaturesDestination = mFeaturesResultSearch.get(0);
            if(!mEditTextDestination.getText().toString().isEmpty()) {
                mSuggestionListener.onPlotRouteWhenDestinationSelected();
                mSuggestionListener.onGoButtonVisibility(true);
            }else {
                mSuggestionListener.onGoButtonVisibility(false);
            }

        }
        handler.removeMessages(SEARCH_TEXT_CHANGED);
    }

    @Override
    public void hideLoader() {
        hideProgress();
    }

    @OnClick(R.id.img_back)
    public void onBackClick() {
        hideLoader();
        mSuggestionListener.onBackPress();
        Utility.hideSoftKeyboard((AppCompatActivity) getActivity());
        mEditTextSource.setText("");
        mEditTextDestination.setText("");
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
        mSuggestionListener = null;
    }

    public void callForDestination(GeoPoint geoPointCurrent, GeoPoint geoPointSource, GeoPoint geoPointDestination, JSONObject jsonObject, Features featuresRouteVia) {
        Utility.hideSoftKeyboard((AppCompatActivity) getActivity());
        mGeoPointSource = geoPointSource;
        mGeoPointDestination = geoPointDestination;
        if (mGeoPointSource.getLatitude() != mGeoPointDestination.getLatitude() &&
                mGeoPointSource.getLongitude() != mGeoPointDestination.getLongitude()) {
            if (!mEditTextSource.getText().toString().isEmpty() && !mEditTextDestination.getText().toString().isEmpty()) {
                String coordinates = mGeoPointSource + "|" + mGeoPointDestination;
                String profileType = AppConstant.PROFILE_WHEEL_CHAIR;
                mISuggestionScreenPresenter.getDestinationsData(coordinates, profileType, jsonObject);
            }
            handler.removeMessages(SEARCH_TEXT_CHANGED);
        }
    }

    @Override
    public void onDirectionDataReceived(DirectionsResponse data) {
        if (data != null && data.getFeaturesList() != null && data.getFeaturesList().size() != 0
                && data.getFeaturesList().get(0).getGeometry() != null && data.getFeaturesList().get(0).getProperties().getSegmentList().get(0).getStepsList() != null) {
            for (int i = 0; i < data.getFeaturesList().get(0).getProperties().getSegmentList().size(); i++) {
                mSuggestionListener.plotDataOnMap(data.getFeaturesList().get(0).getGeometry().getCoordinates(),
                        data.getFeaturesList().get(0).getProperties().getSegmentList().get(i).getStepsList());
            }
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
                if(mLinearLayoutDest.getVisibility()==View.GONE){
                    mSubTitle.setText(R.string.give_feedback_title);
                    mSubTitleNo.setVisibility(View.VISIBLE);
                    mSubTitleYes.setVisibility(View.VISIBLE);
                }
                mGeoPointSource = new GeoPoint(mFeaturesResultSearch.get(0).getGeometry().getCoordinates().get(0),
                        mFeaturesResultSearch.get(0).getGeometry().getCoordinates().get(1));
                mFeaturesSource = mFeaturesResultSearch.get(0);
                mEditTextSource.addTextChangedListener(mSourceWatcher);
                updateEditControls(mEditTextSource, mSourceAddressFetch, mSourceAddressClear);

            } else if (mEditTextDestination.hasFocus()) {
                mEditTextDestination.removeTextChangedListener(mDestWatcher);
                mEditTextDestination.setText(mFeaturesResultSearch.get(0).getProperties().toString());
                if(!mEditTextDestination.getText().toString().isEmpty()) {
                    mSuggestionListener.onPlotRouteWhenDestinationSelected();
                    mSuggestionListener.onGoButtonVisibility(true);
                }else {
                    mSuggestionListener.onGoButtonVisibility(false);
                }

                mGeoPointDestination = new GeoPoint(mFeaturesResultSearch.get(0).getGeometry().getCoordinates().get(0),
                        mFeaturesResultSearch.get(0).getGeometry().getCoordinates().get(1));
                mFeaturesDestination = mFeaturesResultSearch.get(0);
                mEditTextDestination.addTextChangedListener(mDestWatcher);
                updateEditControls(mEditTextDestination, mDestinationAddressFetch, mDestinationAddressClear);
            }

        } else if (data != null && data.getFeatures() != null && !data.getFeatures().isEmpty()) {
            mFeaturesResultSearch = data.getFeatures();
            mAddressListAdapter = new CustomListAdapter(getContext(), R.layout.address_item_view, data.getFeatures());
            setListPopUp(mLinearLayoutResult);
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

    @Override
    public void getWays(RequestGetWay requestGetWay) {
        mISuggestionScreenPresenter.getWays(requestGetWay);
    }

    @Override
    public void onWayDataReceived(ResponseWay responseWay) {
        Toast.makeText(getContext(), "Status is : " + responseWay.isStatus(), Toast.LENGTH_SHORT).show();
        Intent intent= new Intent(getContext(),SettingActivity.class);
        intent.putExtra(AppConstant.WAY_DATA, responseWay);
        startActivity(intent);
    }

    @Override
    public void onFailure(String error) {
        Toast.makeText(getContext(), "Status is not found: ", Toast.LENGTH_SHORT).show();
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

    @OnClick(R.id.txv_sub_title_no)
    public void onNOClick(){
        mSubTitle.setText(R.string.tell_us_where_you_are);
        mSubTitleNo.setVisibility(View.GONE);
        mSubTitleYes.setVisibility(View.GONE);
        mLinearLayoutDest.setVisibility(View.GONE);
        mSuggestionListener.onGoButtonVisibility(true);
        mSuggestionListener.onNoClickAddLocation();

    }


    @OnClick(R.id.txv_sub_title_yes)
    public void  onYESClick(){
        mSubTitleNo.setVisibility(View.GONE);
        mSubTitleYes.setVisibility(View.GONE);
        mLinearLayoutDest.setVisibility(View.VISIBLE);
        if(!mEditTextDestination.getText().toString().isEmpty()) {
            mSuggestionListener.onGoButtonVisibility(true);
        }else {
            mSuggestionListener.onGoButtonVisibility(false);
        }

    }
    public void plotRoute() {
        if (!mEditTextSource.getText().toString().isEmpty() && !mEditTextDestination.getText().toString().isEmpty()) {
            if (mGeoPointSource != null && mGeoPointDestination != null && mGeoPointSource.getLatitude() != mGeoPointDestination.getLatitude() &&
                    mGeoPointSource.getLongitude() != mGeoPointDestination.getLongitude()) {
                mSuggestionListener.onSourceDestinationSelected(mFeaturesSource, mFeaturesDestination);
                callForDestination(null, mGeoPointSource, mGeoPointDestination, null,null);
            } else {
                showSnackBar(getContext().getResources().getString(R.string.error_source_destination_same));
            }
        }
    }


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.radioButtonNotValidated:
                mButtonSelected = 1;
                mRadioButtonNotValidated.setTextColor(getResources().getColor(R.color.colorPrimary));
                mRadioButtonValidated.setTextColor(getResources().getColor(R.color.colorWhite));
               // mSuggestionListener.onTabClicked(mButtonSelected);

                break;

            case R.id.radioButtonValidated:
                mButtonSelected = 2;
                mRadioButtonValidated.setTextColor(getResources().getColor(R.color.colorPrimary));
                mRadioButtonNotValidated.setTextColor(getResources().getColor(R.color.colorWhite));
               // mSuggestionListener.onTabClicked(mButtonSelected);
                break;

            default:

        }
    }

    @OnClick(R.id.img_back_press)
    public void backPress(){
        mSuggestionListener.onBackPress();
    }


}
