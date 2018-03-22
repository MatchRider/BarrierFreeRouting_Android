package com.disablerouting.capture_option.view;


import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.base.BaseActivityImpl;
import com.disablerouting.capture_option.manager.SetChangeSetManager;
import com.disablerouting.capture_option.model.Node;
import com.disablerouting.capture_option.model.RequestCreateNode;
import com.disablerouting.capture_option.presenter.CaptureScreenPresenter;
import com.disablerouting.common.AppConstant;
import com.disablerouting.feedback.model.RequestTag;
import com.disablerouting.route_planner.model.FeedBackModel;
import com.disablerouting.success_screen.SuccessActivity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class CaptureActivity extends BaseActivityImpl implements ICaptureView{

    private ExpandableListAdapter mExpandableListAdapter;
    private CaptureScreenPresenter mCaptureScreenPresenter;

    @BindView(R.id.exp_list_view)
    ExpandableListView mExpandableListView;

    private List<String> mListDataHeader;
    private LinkedHashMap<String, List<String>> mListDataChild;
    private int mLastExpandedPosition = -1;
    private View mParentView;

    private FeedBackModel mFeedBackModel;
    List<RequestTag> mRequestTagList = new ArrayList<>();
    private List<String> mListDataHeaderKey;
    private LinkedHashMap<String, List<String>> mListDataChildValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_screen);
        ButterKnife.bind(this);

        mFeedBackModel = getIntent().getParcelableExtra(AppConstant.FEED_BACK_MODEL);
        mCaptureScreenPresenter= new CaptureScreenPresenter(this, new SetChangeSetManager());

        prepareListDataForKeyValue();
        prepareListData();
        onExpandListeners();

    }

    private void callToSetChangeSet(){
        if(mFeedBackModel!=null) {
            RequestCreateNode requestCreateNode = new RequestCreateNode();
            String latitude = String.valueOf(mFeedBackModel.getLatitude());
            String longitude = String.valueOf(mFeedBackModel.getLongitude());
            Node node = new Node(mFeedBackModel.getChangeSetID(), latitude, longitude);

            RequestTag requestTag = new RequestTag("note", "Just a node");
            mRequestTagList.add(requestTag);
            node.setRequestTagList(mRequestTagList);
            requestCreateNode.setNode(node);

            mCaptureScreenPresenter.setChangeSet(requestCreateNode);
        }
    }
    private void onExpandListeners() {
        mExpandableListAdapter = new ExpandableListAdapter(this, mListDataHeader, mListDataChild);
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
                        mListDataChildValue.get(mListDataHeaderKey.get(groupPosition)).get(childPosition));
                mRequestTagList.add(requestTag);

                //Handle click of item slected of child and set to sub subtitle
                mExpandableListAdapter.addSubTitleWhenChildClicked(groupPosition, childPosition, mParentView);
                return false;
            }
        });
    }

    private void prepareListDataForKeyValue() {
        mListDataHeaderKey = new ArrayList<String>();
        mListDataChildValue = new LinkedHashMap<>();

        mListDataHeaderKey.add("surface");
        //mListDataHeaderKey.add("highway");
        //mListDataHeaderKey.add("smoothness");
        mListDataHeaderKey.add("sloped_curb");
        mListDataHeaderKey.add("incline");
        mListDataHeaderKey.add("width");
        mListDataHeaderKey.add("obstacle");

        List<String> surfaceTypeData = new ArrayList<String>();
        surfaceTypeData.add("paved");
        surfaceTypeData.add("asphalt");
        surfaceTypeData.add("concrete_plates");
        surfaceTypeData.add("paving_stones");
        surfaceTypeData.add("cobblestone");
        surfaceTypeData.add("grass_pavers");
        surfaceTypeData.add("gravel");

        /*List<String> trackTypeData = new ArrayList<String>();
        trackTypeData.add("cycleway");
        trackTypeData.add("footway");
        trackTypeData.add("living_street");
        trackTypeData.add("pedestrian");
        trackTypeData.add("cobblestone");

        List<String> smoothnessGradeData = new ArrayList<String>();
        smoothnessGradeData.add("good");
        smoothnessGradeData.add("intermediate");
        smoothnessGradeData.add("bad");
        */

        List<String> maxSlopedCurvedData = new ArrayList<String>();
        maxSlopedCurvedData.add("0");
        maxSlopedCurvedData.add("3");
        maxSlopedCurvedData.add("6");
        maxSlopedCurvedData.add(">6");

        List<String> maxInclineData = new ArrayList<String>();
        maxInclineData.add("-5");
        maxInclineData.add("-4");
        maxInclineData.add("-3");
        maxInclineData.add("-2");
        maxInclineData.add("-1");
        maxInclineData.add("0");
        maxInclineData.add("1");
        maxInclineData.add("2");
        maxInclineData.add("3");
        maxInclineData.add("4");
        maxInclineData.add("5");

        List<String> sideWalkWidthData = new ArrayList<String>();
        sideWalkWidthData.add("<30");
        sideWalkWidthData.add("30-45");
        sideWalkWidthData.add("46-75");
        sideWalkWidthData.add("76-100");
        sideWalkWidthData.add("101-125");
        sideWalkWidthData.add("126-150");
        sideWalkWidthData.add("150-175");
        sideWalkWidthData.add(">176");

        List<String> permanentObstacleData = new ArrayList<String>();
        permanentObstacleData.add("Yes");
        permanentObstacleData.add("No");

        mListDataChildValue.put(mListDataHeaderKey.get(0), surfaceTypeData);
       // mListDataChildValue.put(mListDataHeaderKey.get(0), trackTypeData);
        //mListDataChildValue.put(mListDataHeaderKey.get(2), smoothnessGradeData);
        mListDataChildValue.put(mListDataHeaderKey.get(1), maxSlopedCurvedData);
        mListDataChildValue.put(mListDataHeaderKey.get(2), maxInclineData);
        mListDataChildValue.put(mListDataHeaderKey.get(3), sideWalkWidthData);
        mListDataChildValue.put(mListDataHeaderKey.get(4), permanentObstacleData);

    }
    /*
     * Preparing the list data
     */
    private void prepareListData() {
        mListDataHeader = new ArrayList<String>();
        mListDataChild = new LinkedHashMap<>();

        mListDataHeader.add(getString(R.string.surface_type));
        //mListDataHeader.add("Track Type");
        //mListDataHeader.add("Smoothness Grade");
        mListDataHeader.add(getString(R.string.maximum_sloped));
        mListDataHeader.add(getString(R.string.maximum_incline));
        mListDataHeader.add(getString(R.string.sidewalk_width));
        mListDataHeader.add(getString(R.string.permanent_obstacle));

        List<String> surfaceTypeData = new ArrayList<String>();
        surfaceTypeData.add(getString(R.string.paved));
        surfaceTypeData.add(getString(R.string.asphalt));
        surfaceTypeData.add(getString(R.string.concrete));
        surfaceTypeData.add(getString(R.string.paving_stones));
        surfaceTypeData.add(getString(R.string.cobblestone));
        surfaceTypeData.add(getString(R.string.grass_paver));
        surfaceTypeData.add(getString(R.string.gravel));

        /*List<String> trackTypeData = new ArrayList<String>();
        trackTypeData.add("Cycle Way(Bike path)");
        trackTypeData.add("Footway");
        trackTypeData.add("Living street(Road game)");
        trackTypeData.add("Pedestrian");
        trackTypeData.add("Cobblestone");

        List<String> smoothnessGradeData = new ArrayList<String>();
        smoothnessGradeData.add("Good");
        smoothnessGradeData.add("Intermediate");
        smoothnessGradeData.add("Bad");
        */
        List<String> maxSlopedCurvedData = new ArrayList<String>();
        maxSlopedCurvedData.add("0");
        maxSlopedCurvedData.add("3");
        maxSlopedCurvedData.add("6");
        maxSlopedCurvedData.add(">6");

        List<String> maxInclineData = new ArrayList<String>();
        maxInclineData.add("-5");
        maxInclineData.add("-4");
        maxInclineData.add("-3");
        maxInclineData.add("-2");
        maxInclineData.add("-1");
        maxInclineData.add("0");
        maxInclineData.add("1");
        maxInclineData.add("2");
        maxInclineData.add("3");
        maxInclineData.add("4");
        maxInclineData.add("5");

        List<String> sideWalkWidthData = new ArrayList<String>();
        sideWalkWidthData.add("<30");
        sideWalkWidthData.add("30-45");
        sideWalkWidthData.add("46-75");
        sideWalkWidthData.add("76-100");
        sideWalkWidthData.add("101-125");
        sideWalkWidthData.add("126-150");
        sideWalkWidthData.add("150-175");
        sideWalkWidthData.add(">176");


        List<String> permanentObstacleData = new ArrayList<String>();
        permanentObstacleData.add(getString(R.string.yes));
        permanentObstacleData.add(getString(R.string.no));

        mListDataChild.put(mListDataHeader.get(0), surfaceTypeData);
        //mListDataChild.put(mListDataHeader.get(1), trackTypeData);
        //mListDataChild.put(mListDataHeader.get(2), smoothnessGradeData);
        mListDataChild.put(mListDataHeader.get(1), maxSlopedCurvedData);
        mListDataChild.put(mListDataHeader.get(2), maxInclineData);
        mListDataChild.put(mListDataHeader.get(3), sideWalkWidthData);
        mListDataChild.put(mListDataHeader.get(4), permanentObstacleData);


    }

    @OnClick(R.id.img_back)
    public void onBackClick() {
        finish();
    }

    @OnClick(R.id.btn_finish)
    public void onFinishClick(){
        callToSetChangeSet();
    }

    @Override
    public void showLoader() {
        showProgress();
    }

    @Override
    public void hideLoader() {
        hideProgress();
    }

    @Override
    public void onChangeSetId(String id) {
        hideLoader();
        if(id!=null){
            Toast.makeText(this,getString(R.string.posted_sucuess)+id,Toast.LENGTH_LONG).show();
            launchActivity(this, SuccessActivity.class);
        }
    }

    @Override
    public void onFailureSetChangeSet(String error) {
        hideLoader();
        showSnackBar(error,this);
    }
}
