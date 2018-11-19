package com.disablerouting.filter.view;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.application.AppData;
import com.disablerouting.base.BaseActivityImpl;
import com.disablerouting.common.AppConstant;
import com.disablerouting.filter.presenter.FilterScreenPresenter;
import com.disablerouting.filter.presenter.IFilterScreenPresenter;
import com.disablerouting.geo_coding.manager.GeoCodingManager;
import com.disablerouting.geo_coding.model.Features;
import com.disablerouting.geo_coding.model.GeoCodingResponse;
import com.disablerouting.route_planner.adapter.CustomListAdapter;
import com.disablerouting.utils.Utility;
import com.disablerouting.widget.CustomAutoCompleteTextView;

import java.util.*;

public class FilterActivity extends BaseActivityImpl implements IFilterView, AdapterView.OnItemClickListener {

    private FilterExpandableListAdapter mExpandableListAdapter;

    @BindView(R.id.exp_list_view)
    ExpandableListView mExpandableListView;

    @BindView(R.id.edt_routing_via)
    CustomAutoCompleteTextView mEditTextRoutingVia;

    @BindView(R.id.txv_list_sub_title)
    TextView mtxvListSubTitle;

    @BindView(R.id.clear_routing_via)
    ImageView mRoutingViaAddressClear;

    @BindView(R.id.fetch_current_routing_via)
    ImageView mRoutingViaAddressFetch;

    @BindView(R.id.btn_clear)
    Button mBtnClear;

    @BindView(R.id.btn_apply)
    Button mBtnApply;


    private List<String> mListDataHeader;
    private LinkedHashMap<String, List<DataModelExpandableList>> mListDataChild;
    private int mLastExpandedPosition = -1;
    private View mParentView;

    private LinkedHashMap<String, List<DataModelExpandableList>> mListDataChildValue;
    private HashMap<String, String> mHashMapResult = new HashMap<>();
    private List<String> mListDataHeaderKeyForFilter;
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, Integer> mHashMapObjectFilterItem = new HashMap<>();

    private IFilterScreenPresenter mIFilterScreenPresenter;
    private static final int SEARCH_TEXT_CHANGED = 1000;
    private boolean mIsTextInputManually = false;
    private List<Features> mFeaturesResultSearch;
    private CustomListAdapter mAddressListAdapter;
    private String mCurrentLocation = null;
    private android.support.v7.widget.ListPopupWindow mListPopupWindow;
    private HashMap<String, Features> mHashMapResultForRouting = new HashMap<>();


    @SuppressLint("HandlerLeak")
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (!msg.obj.equals("")) {
                if (mListPopupWindow != null && mListPopupWindow.isShowing()) {
                    mListPopupWindow.dismiss();
                }
                if (mEditTextRoutingVia.hasFocus() && mEditTextRoutingVia != null && !mEditTextRoutingVia.getText().toString().equalsIgnoreCase("")) {
                    mIFilterScreenPresenter.getGeoCodeDataForward(mEditTextRoutingVia.getText().toString());
                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_screen);
        ButterKnife.bind(this);

        boolean isFilter = getIntent().getBooleanExtra(AppConstant.IS_FILTER, false);

        if (getIntent().hasExtra(AppConstant.IS_FILTER) && isFilter) {
            if (getIntent().hasExtra(AppConstant.DATA_FILTER_SELECTED)) {
                mHashMapObjectFilterItem = (HashMap<Integer, Integer>) getIntent().getSerializableExtra(AppConstant.DATA_FILTER_SELECTED);
            }
            if(getIntent().hasExtra(AppConstant.DATA_FILTER)){
             mHashMapResult = (HashMap<String, String>) getIntent().getSerializableExtra(AppConstant.DATA_FILTER);
            }
            if (getIntent().hasExtra(AppConstant.DATA_FILTER_ROUTING_VIA)) {
                mHashMapResultForRouting = (HashMap<String, Features>) getIntent().getSerializableExtra(AppConstant.DATA_FILTER_ROUTING_VIA);
                Features value = mHashMapResultForRouting.get(AppConstant.DATA_FILTER_ROUTING_VIA);
                if(value!=null && value.getProperties()!=null){
                    mtxvListSubTitle.setVisibility(View.VISIBLE);
                    mtxvListSubTitle.setText(value.getProperties().toString());
                }
            }else {
                mtxvListSubTitle.setVisibility(View.GONE);
            }
        }
        mIFilterScreenPresenter = new FilterScreenPresenter(this, new GeoCodingManager());

        if (AppData.getNewInstance().getCurrentLoc() != null) {
            mCurrentLocation = AppData.getNewInstance().getCurrentLoc().longitude + "," + AppData.getNewInstance().getCurrentLoc().latitude;
        }
        prepareListDataForKeyValue();
        prepareListData();
        onExpandListeners();
        addFocusChangeListener();
        addListener();

    }

    @OnClick(R.id.fetch_current_routing_via)
    public void fetchCurrentRoutingAdd() {
        mIsTextInputManually = false;
        if (mEditTextRoutingVia.hasFocus() && mEditTextRoutingVia != null && mEditTextRoutingVia.getText().toString().equalsIgnoreCase("")) {
          //  mIFilterScreenPresenter.getCoordinatesData("", mCurrentLocation, 0);
            if(mCurrentLocation!=null) {
                String[] location = mCurrentLocation.split(",");
                mIFilterScreenPresenter.getGeoCodeDataReverse(Double.parseDouble(location[1]),Double.parseDouble(location[0]));
            }
        }

    }

    @OnClick(R.id.clear_routing_via)
    public void clearRoutingVia() {
        mRoutingViaAddressClear.setVisibility(View.GONE);
        mRoutingViaAddressFetch.setVisibility(View.GONE);
        mtxvListSubTitle.setVisibility(View.GONE);
        mtxvListSubTitle.setText("");
        mEditTextRoutingVia.setText("");
    }

    public void addFocusChangeListener() {
        mEditTextRoutingVia.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b && mEditTextRoutingVia.getText().toString().trim().length() == 0) {
                    mRoutingViaAddressFetch.setVisibility(View.VISIBLE);
                } else {
                    mRoutingViaAddressFetch.setVisibility(View.GONE);
                }
            }
        });
    }

    private void addListener() {
        mEditTextRoutingVia.addTextChangedListener(mRoutingViaWatcher);
    }

    private TextWatcher mRoutingViaWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable str) {
            updateEditControls(mEditTextRoutingVia, mRoutingViaAddressFetch, mRoutingViaAddressClear);
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



    private void onExpandListeners() {
        mExpandableListAdapter = new FilterExpandableListAdapter(this, mListDataHeader, mListDataChild);

        mExpandableListAdapter.setSelectionMap(mHashMapObjectFilterItem);

        mExpandableListView.setAdapter(mExpandableListAdapter);
        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                mParentView = v;
                mExpandableListView.setSelection(groupPosition);

                return false;
            }
        });

        mExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (mLastExpandedPosition != -1 && groupPosition != mLastExpandedPosition) {
                    mExpandableListView.collapseGroup(mLastExpandedPosition);
                }
                mLastExpandedPosition = groupPosition;

            }
        });

        mExpandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {

            }
        });

        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                mHashMapResult.put(mListDataHeaderKeyForFilter.get(groupPosition), mListDataChildValue.get(mListDataHeaderKeyForFilter.get(groupPosition)).get(childPosition).getValue());

                //Handle click of item selected of child and set to sub subtitle
                mExpandableListAdapter.addSubTitleWhenChildClicked(groupPosition, childPosition, mParentView);
                return false;
            }
        });
    }

    private void prepareListDataForKeyValue() {
        mListDataHeaderKeyForFilter = new ArrayList<>();
        mListDataChildValue = new LinkedHashMap<>();

        mListDataHeaderKeyForFilter.add("surface_type"); //"surface_type"
        mListDataHeaderKeyForFilter.add("maximum_sloped_kerb");
        mListDataHeaderKeyForFilter.add("maximum_incline");
        mListDataHeaderKeyForFilter.add("minimum_width");

        List<DataModelExpandableList> surfaceTypeData = new ArrayList<>();
        surfaceTypeData.add(new DataModelExpandableList("concrete"));
        surfaceTypeData.add(new DataModelExpandableList(getString(R.string.concrete_key)));
        surfaceTypeData.add(new DataModelExpandableList(getString(R.string.paving_stones_key)));
        surfaceTypeData.add(new DataModelExpandableList(getString(R.string.cobbleston_key)));
        surfaceTypeData.add(new DataModelExpandableList(""));

        List<DataModelExpandableList> maxSlopedCurvedData = new ArrayList<>();
        maxSlopedCurvedData.add(new DataModelExpandableList(getString(R.string.zero)));
        maxSlopedCurvedData.add(new DataModelExpandableList(getString(R.string.string_point_three)));
        maxSlopedCurvedData.add(new DataModelExpandableList(getString(R.string.string_point_six)));
        maxSlopedCurvedData.add(new DataModelExpandableList(getString(R.string.any)));

        List<DataModelExpandableList> maxInclineData = new ArrayList<>();
        maxInclineData.add(new DataModelExpandableList("0.00"));
        maxInclineData.add(new DataModelExpandableList("0.03"));
        maxInclineData.add(new DataModelExpandableList("0.06"));
        maxInclineData.add(new DataModelExpandableList("0.10"));
        maxInclineData.add(new DataModelExpandableList(""));

        List<DataModelExpandableList> sideWalkWidthData = new ArrayList<>();
        sideWalkWidthData.add(new DataModelExpandableList(""));
        sideWalkWidthData.add(new DataModelExpandableList(getString(R.string.value_string_less_width)));


        mListDataChildValue.put(mListDataHeaderKeyForFilter.get(0), surfaceTypeData);
        mListDataChildValue.put(mListDataHeaderKeyForFilter.get(1), maxSlopedCurvedData);
        mListDataChildValue.put(mListDataHeaderKeyForFilter.get(2), maxInclineData);
        mListDataChildValue.put(mListDataHeaderKeyForFilter.get(3), sideWalkWidthData);

    }

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        mListDataHeader = new ArrayList<>();
        mListDataChild = new LinkedHashMap<>();

        mListDataHeader.add(getString(R.string.surface_type));
        mListDataHeader.add(getString(R.string.maximum_sloped_filter));
        mListDataHeader.add(getString(R.string.maximum_incline));
        mListDataHeader.add(getString(R.string.sidewalk_width));


        List<DataModelExpandableList> surfaceTypeData = new ArrayList<>();
        surfaceTypeData.add(new DataModelExpandableList(getResources().getString(R.string.asphalt_filter)));
        surfaceTypeData.add(new DataModelExpandableList(getResources().getString(R.string.paving_stones_filter)));
        surfaceTypeData.add(new DataModelExpandableList(getResources().getString(R.string.cobblestone_filter)));
        surfaceTypeData.add(new DataModelExpandableList(getResources().getString(R.string.compacted_filter)));
        surfaceTypeData.add(new DataModelExpandableList(getResources().getString(R.string.all_filter)));

        List<DataModelExpandableList> maxSlopedCurvedData = new ArrayList<DataModelExpandableList>();
        maxSlopedCurvedData.add(new DataModelExpandableList(getString(R.string.zero_curb)));
        maxSlopedCurvedData.add(new DataModelExpandableList(getString(R.string.string_three)));
        maxSlopedCurvedData.add(new DataModelExpandableList(getString(R.string.string_six)));
        maxSlopedCurvedData.add(new DataModelExpandableList(getString(R.string.string_greater_six)));

        List<DataModelExpandableList> maxInclineData = new ArrayList<>();
        maxInclineData.add(new DataModelExpandableList(getString(R.string.zero_curb_inlcline)));
        maxInclineData.add(new DataModelExpandableList(getString(R.string.up_to_three)));
        maxInclineData.add(new DataModelExpandableList(getString(R.string.up_to_six)));
        maxInclineData.add(new DataModelExpandableList(getString(R.string.up_to_ten)));
        maxInclineData.add(new DataModelExpandableList(getString(R.string.any_show)));

        List<DataModelExpandableList> sideWalkWidthData = new ArrayList<>();
        sideWalkWidthData.add(new DataModelExpandableList(getString(R.string.ninty_less)));
        sideWalkWidthData.add(new DataModelExpandableList(getString(R.string.ninty_greater)));


        mListDataChild.put(mListDataHeader.get(0), surfaceTypeData);
        mListDataChild.put(mListDataHeader.get(1), maxSlopedCurvedData);
        mListDataChild.put(mListDataHeader.get(2), maxInclineData);
        mListDataChild.put(mListDataHeader.get(3), sideWalkWidthData);
    }

    @OnClick(R.id.img_back)
    public void onBackClick() {
        finish();
    }

    @OnClick(R.id.btn_clear)
    public void onClickClear() {
        mExpandableListAdapter.removeSubTitlesWhenClearClicked();
        mHashMapResult = new HashMap<>();
        mtxvListSubTitle.setVisibility(View.GONE);
        mtxvListSubTitle.setText("");
        mHashMapResultForRouting= new HashMap<>();
        setDataWhenFilterApplied();
    }

    @OnClick(R.id.btn_apply)
    public void onClickApply() {
        setDataWhenFilterApplied();
    }

    private void setDataWhenFilterApplied() {
        Intent returnIntent = new Intent();
        for (Map.Entry<String, String> values : mHashMapResult.entrySet()) {
            if(values.getKey().equalsIgnoreCase("surface_type") && values.getValue().equalsIgnoreCase("")){
                mHashMapResult.remove("surface_type");
            }
            if(values.getKey().equalsIgnoreCase("maximum_incline") && values.getValue().equalsIgnoreCase("")){
                mHashMapResult.remove("maximum_incline");
            }
            if(values.getKey().equalsIgnoreCase("minimum_width") && values.getValue().equalsIgnoreCase("")){
                mHashMapResult.remove("minimum_width");
            }


        }
        returnIntent.putExtra(AppConstant.DATA_FILTER, mHashMapResult);
        returnIntent.putExtra(AppConstant.DATA_FILTER_ROUTING_VIA, mHashMapResultForRouting);
        returnIntent.putExtra(AppConstant.DATA_FILTER_SELECTED, mExpandableListAdapter.getSelectionMap());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onGeoDataDataReceived(GeoCodingResponse data, boolean isForCurrentLoc) {
        handler.removeMessages(SEARCH_TEXT_CHANGED);
        if (isForCurrentLoc && data != null && data.getFeatures() != null && data.getFeatures().size() == 1) {
            Utility.hideSoftKeyboard(this);
            mFeaturesResultSearch = data.getFeatures();
            if (mEditTextRoutingVia.hasFocus()) {
                mEditTextRoutingVia.removeTextChangedListener(mRoutingViaWatcher);
                mEditTextRoutingVia.setText(mFeaturesResultSearch.get(0).getProperties().toString());
                mtxvListSubTitle.setVisibility(View.VISIBLE);
                mtxvListSubTitle.setText(mFeaturesResultSearch.get(0).getProperties().toString());
                mHashMapResultForRouting.put(AppConstant.DATA_FILTER_ROUTING_VIA,mFeaturesResultSearch.get(0));
                mEditTextRoutingVia.addTextChangedListener(mRoutingViaWatcher);
                updateEditControls(mEditTextRoutingVia, mRoutingViaAddressFetch, mRoutingViaAddressClear);

            }

        } else if (data != null && data.getFeatures() != null && !data.getFeatures().isEmpty()) {
            mFeaturesResultSearch = data.getFeatures();
            mAddressListAdapter = new CustomListAdapter(this, R.layout.address_item_view, data.getFeatures());
            Utility.hideSoftKeyboard(this);
            setListPopUp(mEditTextRoutingVia);
        }
    }

    @Override
    public void onFailureGeoCoding(String error) {
        Utility.hideSoftKeyboard(this);
        if (error.equalsIgnoreCase("No address found.")) {
            showSnackBar(getResources().getString(R.string.no_address_found), this);
        } else {
            showSnackBar(error, this);
        }
    }

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

    private void setListPopUp(View anchor) {
        mListPopupWindow = new android.support.v7.widget.ListPopupWindow(this);
        mListPopupWindow.setAnchorView(anchor);
        mListPopupWindow.setAnimationStyle(R.style.popup_window_animation);
        int height = Utility.calculatePopUpHeight(this);
        mListPopupWindow.setHeight(height / 4);
        mListPopupWindow.setWidth(android.support.v7.widget.ListPopupWindow.MATCH_PARENT);
        mListPopupWindow.setAdapter(mAddressListAdapter);
        mListPopupWindow.setOnItemClickListener(this);
        mListPopupWindow.show();
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Utility.hideSoftKeyboard(this);
        mListPopupWindow.dismiss();
        if (mEditTextRoutingVia.hasFocus()) {
            mEditTextRoutingVia.setText(mFeaturesResultSearch.get(i).getProperties().toString());
            mtxvListSubTitle.setVisibility(View.VISIBLE);
            mtxvListSubTitle.setText(mFeaturesResultSearch.get(i).getProperties().toString());
            mHashMapResultForRouting.put(AppConstant.DATA_FILTER_ROUTING_VIA,mFeaturesResultSearch.get(i));
        }
        handler.removeMessages(SEARCH_TEXT_CHANGED);
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
