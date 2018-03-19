package com.disablerouting.capture_option;


import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.base.BaseActivityImpl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class CaptureActivity extends BaseActivityImpl {

    private ExpandableListAdapter mExpandableListAdapter;

    @BindView(R.id.exp_list_view)
    ExpandableListView mExpandableListView;

    private List<String> mListDataHeader;
    private LinkedHashMap<String, List<String>> mListDataChild;
    private int mLastExpandedPosition = -1;
    private View mParentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_screen);
        ButterKnife.bind(this);
        prepareListData();
        initializeView();
    }

    private void initializeView() {

        mExpandableListAdapter = new ExpandableListAdapter(this, mListDataHeader, mListDataChild);
        mExpandableListView.setAdapter(mExpandableListAdapter);
        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                mParentView = v;
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
                mExpandableListAdapter.addSubTitleWhenChildClicked(groupPosition, childPosition, mParentView);

                return false;
            }
        });
    }

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        mListDataHeader = new ArrayList<String>();
        mListDataChild = new LinkedHashMap<>();

        mListDataHeader.add("Surface Type");
        mListDataHeader.add("Track Type");
        mListDataHeader.add("Smoothness Grade");
        mListDataHeader.add("Maximum Sloped Curb(cm)");
        mListDataHeader.add("Maximum Incline(%)");
        mListDataHeader.add("Pavement width(cm)");
        mListDataHeader.add("Persistent Obstacle");

        List<String> surfaceTypeData = new ArrayList<String>();
        surfaceTypeData.add("Paved(Paved)");
        surfaceTypeData.add("Asphalt(Paved)");
        surfaceTypeData.add("Concrete(Concrete)");
        surfaceTypeData.add("Paving Stones(Pavers)");
        surfaceTypeData.add("Cobblestone(Cobblestone)");
        surfaceTypeData.add("Grass Paver(Grass Paver)");
        surfaceTypeData.add("Gravel(Gravel)");

        List<String> trackTypeData = new ArrayList<String>();
        trackTypeData.add("Cycle Way(Bike path)");
        trackTypeData.add("Foot way(Walk)");
        trackTypeData.add("Living street(Road game)");
        trackTypeData.add("Pedestrian(Pedestrian)");

        List<String> smoothnessGradeData = new ArrayList<String>();
        smoothnessGradeData.add("Good(Good)");
        smoothnessGradeData.add("Intermediate(Medium)");
        smoothnessGradeData.add("Bad(Bad)");

        List<String> maxSlopedCurvedData = new ArrayList<String>();
        maxSlopedCurvedData.add("0 cm");
        maxSlopedCurvedData.add("3 cm");
        maxSlopedCurvedData.add("6 cm");
        maxSlopedCurvedData.add(">6 cm");

        List<String> maxInclineData = new ArrayList<String>();
        maxInclineData.add("5");
        maxInclineData.add("4");
        maxInclineData.add("3");
        maxInclineData.add("2");
        maxInclineData.add("1");
        maxInclineData.add("0");
        maxInclineData.add("-1");
        maxInclineData.add("-2");
        maxInclineData.add("-3");
        maxInclineData.add("-4");
        maxInclineData.add("-5");

        List<String> pavementWidthData = new ArrayList<String>();
        pavementWidthData.add("<30");
        pavementWidthData.add("30-45");
        pavementWidthData.add("46-75");
        pavementWidthData.add("76-100");
        pavementWidthData.add("101-125");
        pavementWidthData.add("126-150");
        pavementWidthData.add("150-175");
        pavementWidthData.add(">176");


        List<String> persistentObstacleData = new ArrayList<String>();
        persistentObstacleData.add("Yes");
        persistentObstacleData.add("No");

        mListDataChild.put(mListDataHeader.get(0), surfaceTypeData);
        mListDataChild.put(mListDataHeader.get(1), trackTypeData);
        mListDataChild.put(mListDataHeader.get(2), smoothnessGradeData);
        mListDataChild.put(mListDataHeader.get(3), maxSlopedCurvedData);
        mListDataChild.put(mListDataHeader.get(4), maxInclineData);
        mListDataChild.put(mListDataHeader.get(5), pavementWidthData);
        mListDataChild.put(mListDataHeader.get(6), persistentObstacleData);


    }

    @OnClick(R.id.img_back)
    public void onBackClick() {
        finish();
    }

}
