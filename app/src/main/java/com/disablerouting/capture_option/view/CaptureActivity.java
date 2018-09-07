package com.disablerouting.capture_option.view;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.base.BaseActivityImpl;
import com.disablerouting.capture_option.manager.SetChangeSetManager;
import com.disablerouting.capture_option.model.DataModelExpandableList;
import com.disablerouting.capture_option.model.Node;
import com.disablerouting.capture_option.model.RequestCreateNode;
import com.disablerouting.capture_option.presenter.CaptureScreenPresenter;
import com.disablerouting.common.AppConstant;
import com.disablerouting.feedback.model.RequestTag;
import com.disablerouting.login.AsyncTaskOsmApi;
import com.disablerouting.login.IAysncTaskOsm;
import com.disablerouting.login.OauthData;
import com.disablerouting.route_planner.model.FeedBackModel;
import com.disablerouting.success_screen.SuccessActivity;
import com.github.scribejava.core.model.Verb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class CaptureActivity extends BaseActivityImpl implements ICaptureView, IAysncTaskOsm{

    private ExpandableListAdapter mExpandableListAdapter;
    private CaptureScreenPresenter mCaptureScreenPresenter;

    @BindView(R.id.exp_list_view)
    ExpandableListView mExpandableListView;

    @BindView(R.id.btn_finish)
    Button mBtnFinish;

    private List<String> mListDataHeader;
    private LinkedHashMap<String, List<DataModelExpandableList>> mListDataChild;
    private int mLastExpandedPosition = -1;
    private View mParentView;

    private FeedBackModel mFeedBackModel;
    List<RequestTag> mRequestTagList = new ArrayList<>();
    private List<String> mListDataHeaderKey;
    private LinkedHashMap<String, List<DataModelExpandableList>> mListDataChildValue;
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, Integer> mHashMapObjectFilterItem = new HashMap<>();
    private boolean mISStartedFromSuggestion;
    private String mURL="https://master.apis.dev.openstreetmap.org/api/0.6/node/create";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_screen);
        ButterKnife.bind(this);

        mFeedBackModel = getIntent().getParcelableExtra(AppConstant.FEED_BACK_MODEL);
        mCaptureScreenPresenter= new CaptureScreenPresenter(this, new SetChangeSetManager());
        if(getIntent().hasExtra(AppConstant.STARTED_FROM_SUGGESTION)){
            mISStartedFromSuggestion= getIntent().getBooleanExtra(AppConstant.STARTED_FROM_SUGGESTION,false);
        }
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

            String stringBuilder = "<osm><node changeset=" + "\""+String.valueOf(mFeedBackModel.getChangeSetID()) +"\""+ " "+
                    "lat=" + "\""+String.valueOf(mFeedBackModel.getLatitude())+"\"" +" "+
                    "lon=" +"\""+ String.valueOf(mFeedBackModel.getLongitude()) +"\""+ ">" +
                    "<tag k=\"note\" v=\"Just a node\"/></node></osm>";


            String string="<osm>\n" +
                    " <node changeset=\"112100\" lat=\"28.584220243018713\" lon=\"77.13020324707031\">\n" +
                    "   <tag k=\"note\" v=\"Just a node\"/>\n" +
                    " </node>\n" +
                    "</osm>";
           // mCaptureScreenPresenter.setChangeSet(requestCreateNode, this);

            OauthData oauthData= new OauthData(Verb.PUT, stringBuilder,mURL);
            new AsyncTaskOsmApi(CaptureActivity.this,oauthData,this,false, AppConstant.API_TYPE_CREATE_CHANGE_SET).execute("");
        }
    }
    private void onExpandListeners() {
        mExpandableListAdapter = new ExpandableListAdapter(this, mListDataHeader, mListDataChild);

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

                //Handle click of item selected of child and set to sub subtitle
                mExpandableListAdapter.addSubTitleWhenChildClicked(groupPosition, childPosition, mParentView);
                return false;
            }
        });
    }

    private void prepareListDataForKeyValue() {
        mListDataHeaderKey = new ArrayList<String>();
        mListDataChildValue = new LinkedHashMap<>();

        mListDataHeaderKey.add("surface");
        mListDataHeaderKey.add("sloped_curb");
        mListDataHeaderKey.add("incline");
        mListDataHeaderKey.add("width");
        mListDataHeaderKey.add("obstacle");

        List<DataModelExpandableList> surfaceTypeData = new ArrayList<DataModelExpandableList>();
        surfaceTypeData.add(new DataModelExpandableList("paved"));
        surfaceTypeData.add(new DataModelExpandableList("asphalt"));
        surfaceTypeData.add(new DataModelExpandableList("concrete_plates"));
        surfaceTypeData.add(new DataModelExpandableList("paving_stones"));
        surfaceTypeData.add(new DataModelExpandableList("cobblestone"));
        surfaceTypeData.add(new DataModelExpandableList("grass_pavers"));
        surfaceTypeData.add(new DataModelExpandableList("gravel"));

        /*surfaceTypeData.add("paved","");
        surfaceTypeData.add("asphalt");
        surfaceTypeData.add("concrete_plates");
        surfaceTypeData.add("paving_stones");
        surfaceTypeData.add("cobblestone");
        surfaceTypeData.add("grass_pavers");
        surfaceTypeData.add("gravel");
*/
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

        List<DataModelExpandableList> maxSlopedCurvedData = new ArrayList<DataModelExpandableList>();
        maxSlopedCurvedData.add(new DataModelExpandableList("0"));
        maxSlopedCurvedData.add(new DataModelExpandableList(getString(R.string.string_one_point_two)));
        maxSlopedCurvedData.add(new DataModelExpandableList(getString(R.string.string_two_point_four)));
        maxSlopedCurvedData.add(new DataModelExpandableList(getString(R.string.string_two_point_four_greater)));

        List<DataModelExpandableList> maxInclineData = new ArrayList<DataModelExpandableList>();
        maxInclineData.add(new DataModelExpandableList("0"));
        maxInclineData.add(new DataModelExpandableList(getString(R.string.up_to_three)));
        maxInclineData.add(new DataModelExpandableList(getString(R.string.up_to_six)));
        maxInclineData.add(new DataModelExpandableList(getString(R.string.up_to_ten)));
        maxInclineData.add(new DataModelExpandableList("> 10"));

        List<DataModelExpandableList> sideWalkWidthData = new ArrayList<DataModelExpandableList>();
        sideWalkWidthData.add(new DataModelExpandableList("< 90"));
        sideWalkWidthData.add(new DataModelExpandableList("90-120"));
        sideWalkWidthData.add(new DataModelExpandableList("> 120"));

        List<DataModelExpandableList> permanentObstacleData = new ArrayList<DataModelExpandableList>();
        permanentObstacleData.add(new DataModelExpandableList(getString(R.string.yes)));
        permanentObstacleData.add(new DataModelExpandableList(getString(R.string.no)));

        mListDataChildValue.put(mListDataHeaderKey.get(0), surfaceTypeData);
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
        mListDataHeader.add(getString(R.string.maximum_sloped));
        mListDataHeader.add(getString(R.string.maximum_incline));
        mListDataHeader.add(getString(R.string.sidewalk_width));
        mListDataHeader.add(getString(R.string.permanent_obstacle));

        List<DataModelExpandableList> surfaceTypeData = new ArrayList<DataModelExpandableList>();
        surfaceTypeData.add(new DataModelExpandableList(getResources().getString(R.string.asphalt)));
        surfaceTypeData.add(new DataModelExpandableList(getResources().getString(R.string.concrete)));
        surfaceTypeData.add(new DataModelExpandableList(getResources().getString(R.string.paving_stones)));
        surfaceTypeData.add(new DataModelExpandableList(getResources().getString(R.string.cobblestone)));
        surfaceTypeData.add(new DataModelExpandableList(getResources().getString(R.string.grass_paver)));
        surfaceTypeData.add(new DataModelExpandableList(getResources().getString(R.string.gravel)));

        List<DataModelExpandableList> maxSlopedCurvedData = new ArrayList<DataModelExpandableList>();
        maxSlopedCurvedData.add(new DataModelExpandableList("0"));
        maxSlopedCurvedData.add(new DataModelExpandableList(getString(R.string.string_one_point_two)));
        maxSlopedCurvedData.add(new DataModelExpandableList(getString(R.string.string_two_point_four)));
        maxSlopedCurvedData.add(new DataModelExpandableList(getString(R.string.string_two_point_four_greater)));

        List<DataModelExpandableList> maxInclineData = new ArrayList<DataModelExpandableList>();
        maxInclineData.add(new DataModelExpandableList("0"));
        maxInclineData.add(new DataModelExpandableList(getString(R.string.up_to_three)));
        maxInclineData.add(new DataModelExpandableList(getString(R.string.up_to_six)));
        maxInclineData.add(new DataModelExpandableList(getString(R.string.up_to_ten)));
        maxInclineData.add(new DataModelExpandableList("> 10"));

        List<DataModelExpandableList> sideWalkWidthData = new ArrayList<DataModelExpandableList>();
        sideWalkWidthData.add(new DataModelExpandableList("< 90"));
        sideWalkWidthData.add(new DataModelExpandableList("90-120"));
        sideWalkWidthData.add(new DataModelExpandableList("> 120"));

        List<DataModelExpandableList> permanentObstacleData = new ArrayList<DataModelExpandableList>();
        permanentObstacleData.add(new DataModelExpandableList(getString(R.string.yes)));
        permanentObstacleData.add(new DataModelExpandableList(getString(R.string.no)));

        mListDataChild.put(mListDataHeader.get(0), surfaceTypeData);
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
            Intent intent= new Intent(this, SuccessActivity.class);
            intent.putExtra(AppConstant.STARTED_FROM_SUGGESTION,mISStartedFromSuggestion);
            startActivity(intent);
        }
    }

    @Override
    public void onFailureSetChangeSet(String error) {
        hideLoader();
        showSnackBar(error,this);
    }


    @Override
    public void onSuccessAsyncTask(final String responseBody, String API_TYPE) {

        this.runOnUiThread(new Runnable() {
            public void run() {
                if(responseBody!=null){
                    Toast.makeText(CaptureActivity.this,getString(R.string.posted_sucuess)+responseBody,Toast.LENGTH_LONG).show();
                    Intent intent= new Intent(CaptureActivity.this, SuccessActivity.class);
                    intent.putExtra(AppConstant.STARTED_FROM_SUGGESTION,mISStartedFromSuggestion);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public void onFailureAsyncTask(final String errorBody) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(CaptureActivity.this, errorBody, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSuccessAsyncTaskForGetWay(String responseBody) {

    }
}
