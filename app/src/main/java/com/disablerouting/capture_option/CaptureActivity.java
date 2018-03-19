package com.disablerouting.capture_option;


import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import butterknife.BindView;
import butterknife.ButterKnife;
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
        initializeView();
    }

    private void initializeView() {
        prepareListData();
        mExpandableListAdapter = new ExpandableListAdapter(this, mListDataHeader, mListDataChild);
        mExpandableListView.setAdapter(mExpandableListAdapter);

        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
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

        mListDataChild.put(mListDataHeader.get(0), surfaceTypeData);
        mListDataChild.put(mListDataHeader.get(1), trackTypeData);
        mListDataChild.put(mListDataHeader.get(2), smoothnessGradeData);

    }

}
