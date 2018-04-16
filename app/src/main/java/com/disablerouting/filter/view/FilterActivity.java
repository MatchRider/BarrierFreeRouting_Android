package com.disablerouting.filter.view;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.base.BaseActivityImpl;
import com.disablerouting.capture_option.model.DataModelExpandableList;
import com.disablerouting.common.AppConstant;
import com.disablerouting.feedback.model.RequestTag;
import com.disablerouting.filter.presenter.FilterScreenPresenter;
import com.disablerouting.geo_coding.manager.GeoCodingManager;
import com.disablerouting.geo_coding.model.GeoCodingResponse;
import com.disablerouting.widget.CustomAutoCompleteTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class FilterActivity extends BaseActivityImpl implements IFilterView {

    private FilterExpandableListAdapter mExpandableListAdapter;

    @BindView(R.id.exp_list_view)
    ExpandableListView mExpandableListView;

    @BindView(R.id.edt_routing_via)
    CustomAutoCompleteTextView mEditTextRoutingVia;

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

    List<RequestTag> mRequestTagList = new ArrayList<>();
    private List<String> mListDataHeaderKey;
    private LinkedHashMap<String, List<DataModelExpandableList>> mListDataChildValue;
    HashMap<String, String> hashMapResult= new HashMap<>();
    private List<String> mListDataHeaderKeyForFilter;
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, Integer> mHashMapObjectFilterItem = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_screen);
        ButterKnife.bind(this);

        boolean isFilter = getIntent().getBooleanExtra(AppConstant.IS_FILTER, false);

        if(getIntent().hasExtra(AppConstant.IS_FILTER) && isFilter){
            mBtnClear.setVisibility(View.VISIBLE);
            mBtnApply.setVisibility(View.VISIBLE);
            if (getIntent().hasExtra(AppConstant.DATA_FILTER_SELECTED)){
                mHashMapObjectFilterItem = (HashMap<Integer, Integer>)getIntent().getSerializableExtra(AppConstant.DATA_FILTER_SELECTED);
            }
        }
        FilterScreenPresenter filterScreenPresenter = new FilterScreenPresenter(this, new GeoCodingManager());

        prepareListDataForKeyValue();
        prepareListData();
        onExpandListeners();

    }

    private void callForRoutingVia(){
      //  mFilterScreenPresenter.getCoordinatesData();
    }

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

                 //Add data for API call
                RequestTag requestTag = new RequestTag(mListDataHeaderKey.get(groupPosition),
                        mListDataChildValue.get(mListDataHeaderKey.get(groupPosition)).get(childPosition).getValue());

                mRequestTagList.add(requestTag);

                hashMapResult.put(mListDataHeaderKeyForFilter.get(groupPosition),mListDataChildValue.get(mListDataHeaderKey.get(groupPosition)).get(childPosition).getValue());

                //Handle click of item selected of child and set to sub subtitle
                mExpandableListAdapter.addSubTitleWhenChildClicked(groupPosition, childPosition, mParentView);
                return false;
            }
        });
    }

    private void prepareListDataForKeyValue() {
        mListDataHeaderKey = new ArrayList<String>();
        mListDataHeaderKeyForFilter = new ArrayList<String>();
        mListDataChildValue = new LinkedHashMap<>();

        mListDataHeaderKey.add("surface");
        mListDataHeaderKey.add("sloped_curb");
        mListDataHeaderKey.add("incline");
        mListDataHeaderKey.add("width");

        mListDataHeaderKeyForFilter.add("surface_type");
        mListDataHeaderKeyForFilter.add("maximum_sloped_curb");
        mListDataHeaderKeyForFilter.add("maximum_incline");
        mListDataHeaderKeyForFilter.add("width");


        List<DataModelExpandableList> surfaceTypeData = new ArrayList<DataModelExpandableList>();
        surfaceTypeData.add(new DataModelExpandableList("paved"));
        surfaceTypeData.add(new DataModelExpandableList("asphalt"));
        surfaceTypeData.add(new DataModelExpandableList("concrete_plates"));
        surfaceTypeData.add(new DataModelExpandableList("paving_stones"));
        surfaceTypeData.add(new DataModelExpandableList("cobblestone"));
        surfaceTypeData.add(new DataModelExpandableList("grass_pavers"));
        surfaceTypeData.add(new DataModelExpandableList("gravel"));

        List<DataModelExpandableList> maxSlopedCurvedData = new ArrayList<DataModelExpandableList>();
        maxSlopedCurvedData.add(new DataModelExpandableList("0"));
        maxSlopedCurvedData.add(new DataModelExpandableList("3"));
        maxSlopedCurvedData.add(new DataModelExpandableList("6"));
        maxSlopedCurvedData.add(new DataModelExpandableList(">6"));

        List<DataModelExpandableList> maxInclineData = new ArrayList<DataModelExpandableList>();
        maxInclineData.add(new DataModelExpandableList("-5"));
        maxInclineData.add(new DataModelExpandableList("-4"));
        maxInclineData.add(new DataModelExpandableList("-3"));
        maxInclineData.add(new DataModelExpandableList("-2"));
        maxInclineData.add(new DataModelExpandableList("-1"));
        maxInclineData.add(new DataModelExpandableList("0"));
        maxInclineData.add(new DataModelExpandableList("1"));
        maxInclineData.add(new DataModelExpandableList("2"));
        maxInclineData.add(new DataModelExpandableList("3"));
        maxInclineData.add(new DataModelExpandableList("4"));
        maxInclineData.add(new DataModelExpandableList("5"));

        List<DataModelExpandableList> sideWalkWidthData = new ArrayList<DataModelExpandableList>();
        sideWalkWidthData.add(new DataModelExpandableList("<30"));
        sideWalkWidthData.add(new DataModelExpandableList("30-45"));
        sideWalkWidthData.add(new DataModelExpandableList("46-75"));
        sideWalkWidthData.add(new DataModelExpandableList("76-100"));
        sideWalkWidthData.add(new DataModelExpandableList("101-125"));
        sideWalkWidthData.add(new DataModelExpandableList("126-150"));
        sideWalkWidthData.add(new DataModelExpandableList("150-175"));
        sideWalkWidthData.add(new DataModelExpandableList(">176"));

        mListDataChildValue.put(mListDataHeaderKey.get(0), surfaceTypeData);
        mListDataChildValue.put(mListDataHeaderKey.get(1), maxSlopedCurvedData);
        mListDataChildValue.put(mListDataHeaderKey.get(2), maxInclineData);
        mListDataChildValue.put(mListDataHeaderKey.get(3), sideWalkWidthData);

    }
    /*
     * Preparing the list data
     */
    private void prepareListData() {
        mListDataHeader = new ArrayList<String>();
        mListDataChild = new LinkedHashMap<>();

        mListDataHeader.add(getString(R.string.surface_type));
        mListDataHeader.add(getString(R.string.maximum_sloped));
        mListDataHeader.add(getString(R.string.maximum_incline));
        mListDataHeader.add(getString(R.string.walk_way_width));

        List<DataModelExpandableList> surfaceTypeData = new ArrayList<DataModelExpandableList>();
        surfaceTypeData.add(new DataModelExpandableList(getResources().getString(R.string.asphalt_filter)));
        surfaceTypeData.add(new DataModelExpandableList(getResources().getString(R.string.paving_stones_filter)));
        surfaceTypeData.add(new DataModelExpandableList(getResources().getString(R.string.cobblestone_filter)));
        surfaceTypeData.add(new DataModelExpandableList(getResources().getString(R.string.grass_paver_filter)));
        surfaceTypeData.add(new DataModelExpandableList(getResources().getString(R.string.all_filter)));

        List<DataModelExpandableList> maxSlopedCurvedData = new ArrayList<DataModelExpandableList>();
        maxSlopedCurvedData.add(new DataModelExpandableList("0"));
        maxSlopedCurvedData.add(new DataModelExpandableList("1.2"));
        maxSlopedCurvedData.add(new DataModelExpandableList("2.4"));
        maxSlopedCurvedData.add(new DataModelExpandableList(">2.4"));

        List<DataModelExpandableList> maxInclineData = new ArrayList<DataModelExpandableList>();
        maxInclineData.add(new DataModelExpandableList("0"));
        maxInclineData.add(new DataModelExpandableList(getString(R.string.up_to_three)));
        maxInclineData.add(new DataModelExpandableList(getString(R.string.up_to_six)));
        maxInclineData.add(new DataModelExpandableList(getString(R.string.up_to_ten)));
        maxInclineData.add(new DataModelExpandableList("< 10"));

        List<DataModelExpandableList> sideWalkWidthData = new ArrayList<DataModelExpandableList>();
        sideWalkWidthData.add(new DataModelExpandableList("< 35.4"));
        sideWalkWidthData.add(new DataModelExpandableList("> 35.4"));


        mListDataChild.put(mListDataHeader.get(0), surfaceTypeData);
        mListDataChild.put(mListDataHeader.get(1), maxSlopedCurvedData);
        mListDataChild.put(mListDataHeader.get(2), maxInclineData);
        mListDataChild.put(mListDataHeader.get(3), sideWalkWidthData);
    }

    @OnClick(R.id.img_back)
    public void onBackClick() {
        finish();
    }

    @Override
    public void showLoader() {
        showProgress();
    }

    @Override
    public void hideLoader() {
        hideProgress();
    }


    @OnClick(R.id.btn_clear)
    public void onClickClear(){
        mExpandableListAdapter.removeSubTitlesWhenClearClicked();
        hashMapResult = new HashMap<>();
        setDataWhenFilterApplied();
    }

    @OnClick(R.id.btn_apply)
    public void onClickApply(){
       setDataWhenFilterApplied();
    }

    private void setDataWhenFilterApplied(){
        Intent returnIntent = new Intent();
        returnIntent.putExtra(AppConstant.DATA_FILTER, hashMapResult);
        returnIntent.putExtra(AppConstant.DATA_FILTER_SELECTED, mExpandableListAdapter.getSelectionMap());
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    @Override
    public void onGeoDataDataReceived(GeoCodingResponse data, boolean isForCurrentLoc) {

    }

    @Override
    public void onFailureGeoCoding(String error) {

    }
}
