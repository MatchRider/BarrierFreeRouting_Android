package com.disablerouting.setting.setting_detail;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.base.BaseActivityImpl;
import com.disablerouting.common.AppConstant;

import java.util.ArrayList;

public class SettingDetailActivity extends BaseActivityImpl implements SettingDetailAdapterListener {

    @BindView(R.id.rcv_setting)
    RecyclerView mRecyclerView;


    @BindView(R.id.txv_title_community)
    TextView mTxvTitle;

    private int mPositionOfTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_detail);
        ButterKnife.bind(this);

        if (getIntent().hasExtra(AppConstant.SETTING_ITEM_SELECTED_SEND)) {
            String titleToBeSet = getIntent().getStringExtra(AppConstant.SETTING_ITEM_SELECTED_SEND);
            mPositionOfTitle = getIntent().getIntExtra(AppConstant.POSITION_SETTING, -1);
            mTxvTitle.setVisibility(View.VISIBLE);
            mTxvTitle.setText(String.format("%s%s", titleToBeSet+"\n", getString(R.string.please_choose)));
        }
        setUpRecyclerView(mPositionOfTitle);
    }

    /**
     * Setup recycler view
     */
    private void setUpRecyclerView(int positionOfTitle) {
        SettingDetailAdapter settingDetailAdapter;
        switch (positionOfTitle) {
            case 0:
                settingDetailAdapter = new SettingDetailAdapter(prepareListDataSurface(), this, true);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                mRecyclerView.setAdapter(settingDetailAdapter);
                break;
            case 1:
                settingDetailAdapter = new SettingDetailAdapter(prepareListDataMaxSlope(), this,false);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                mRecyclerView.setAdapter(settingDetailAdapter);
                break;
            case 2:
                settingDetailAdapter = new SettingDetailAdapter(prepareListDataMaxIncline(), this,false);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                mRecyclerView.setAdapter(settingDetailAdapter);
                break;
            case 3:
                settingDetailAdapter = new SettingDetailAdapter(prepareListDataSideWalk(), this,false);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                mRecyclerView.setAdapter(settingDetailAdapter);
                break;

        }
    }

    private ArrayList<String> prepareListDataSurface() {
        ArrayList<String> stringArrayList = new ArrayList<>();
        stringArrayList.add(getString(R.string.asphalt));
        stringArrayList.add(getString(R.string.concrete));
        stringArrayList.add(getString(R.string.paving_stones));
        stringArrayList.add(getString(R.string.cobblestone));
        stringArrayList.add(getString(R.string.compacted));
        //stringArrayList.add(getString(R.string.grass_paver));
        //stringArrayList.add(getString(R.string.gravel));
        return stringArrayList;
    }

    private ArrayList<String> prepareListDataMaxSlope() {
        ArrayList<String> stringArrayList = new ArrayList<>();
        stringArrayList.add(getString(R.string.zero));
        stringArrayList.add(getString(R.string.string_one_point_two));
        stringArrayList.add(getString(R.string.string_two_point_four));
        stringArrayList.add(getString(R.string.string_two_point_four_greater));
        return stringArrayList;
    }
    private ArrayList<String> prepareValueListDataMaxSlope() {
        ArrayList<String> stringArrayList = new ArrayList<>();
        stringArrayList.add(getString(R.string.zero));
        stringArrayList.add(getString(R.string.value_string_one_point_two));
        stringArrayList.add(getString(R.string.value_string_two_point_four));
        stringArrayList.add(getString(R.string.value_string_two_point_four_greater));
        return stringArrayList;
    }
    private ArrayList<String> prepareListDataMaxIncline() {
        ArrayList<String> stringArrayList = new ArrayList<>();
        stringArrayList.add(getString(R.string.zero));
        stringArrayList.add(getString(R.string.up_to_three));
        stringArrayList.add(getString(R.string.up_to_six));
        stringArrayList.add(getString(R.string.up_to_ten));
        stringArrayList.add(getString(R.string.greater_ten));
        return stringArrayList;
    }

    private ArrayList<String> prepareListDataSideWalk() {
        ArrayList<String> stringArrayList = new ArrayList<>();
        stringArrayList.add(getString(R.string.string_less_width));
        stringArrayList.add(getString(R.string.string_greater_width));
        return stringArrayList;
    }


    @OnClick(R.id.img_back)
    public void onBackClick() {
        finish();
    }

    private void setDataWhenFilterApplied(String valueSelected, int position) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(AppConstant.SETTING_ITEM_SELECTED_RECIEVE, valueSelected);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onDetailItemClick(View v, int position) {
        switch (mPositionOfTitle){
            case 0:
                setDataWhenFilterApplied(prepareListDataSurface().get(position), position);
                break;
            case 1:
                setDataWhenFilterApplied(prepareListDataMaxSlope().get(position), position);
                break;
            case 2:
                setDataWhenFilterApplied(prepareListDataMaxIncline().get(position), position);
                break;
            case 3:
                setDataWhenFilterApplied(prepareListDataSideWalk().get(position), position);
                break;

        }
    }
}
