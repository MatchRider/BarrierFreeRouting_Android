package com.disablerouting.setting;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.base.BaseActivityImpl;
import com.disablerouting.common.AppConstant;
import com.disablerouting.curd_operations.model.ResponseWay;
import com.disablerouting.setting.setting_detail.SettingDetailActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class SettingActivity extends BaseActivityImpl implements SettingAdapterListener {

    @BindView(R.id.rcv_setting)
    RecyclerView mRecyclerView;

    final int OPEN_SETTING_TYPE = 200;
    private SettingAdapter mSettingAdapter;
    private int mPositionClicked = -1;
    private ResponseWay mResponseWayData;
    private HashMap<Integer, String> hashMapWay = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        if(getIntent().hasExtra("WayData")){
            mResponseWayData= getIntent().getParcelableExtra("WayData");
            if(mResponseWayData!=null) {
                getDataFromWay();
            }
        }

        setUpRecyclerView();

    }

    /**
     * Setup recycler view
     */
    private void setUpRecyclerView() {
        mSettingAdapter = new SettingAdapter(prepareListData(), this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mSettingAdapter);
        if(hashMapWay!=null){
            mSettingAdapter.setSelectionMap(hashMapWay);
            mSettingAdapter.notifyDataSetChanged();

        }
    }

    private ArrayList<String> prepareListData() {
        ArrayList<String> stringArrayList = new ArrayList<>();
        stringArrayList.add(getString(R.string.surface_type));
        stringArrayList.add(getString(R.string.maximum_sloped));
        stringArrayList.add(getString(R.string.maximum_incline));
        stringArrayList.add(getString(R.string.sidewalk_width));
        return stringArrayList;
    }

    @Override
    public void OnIconEditViewOnClick(View v, int position) {
        Intent intent = new Intent(this, SettingDetailActivity.class);
        switch (position) {
            case 0:
                intent.putExtra(AppConstant.POSITION_SETTING, position);
                mPositionClicked = position;
                intent.putExtra(AppConstant.SETTING_ITEM_SELECTED_SEND, prepareListData().get(position));
                startActivityForResult(intent, OPEN_SETTING_TYPE);
                break;
            case 1:
                intent.putExtra(AppConstant.POSITION_SETTING, position);
                mPositionClicked = position;
                intent.putExtra(AppConstant.SETTING_ITEM_SELECTED_SEND, prepareListData().get(position));
                startActivityForResult(intent, OPEN_SETTING_TYPE);
                break;
            case 2:
                intent.putExtra(AppConstant.POSITION_SETTING, position);
                mPositionClicked = position;
                intent.putExtra(AppConstant.SETTING_ITEM_SELECTED_SEND, prepareListData().get(position));
                startActivityForResult(intent, OPEN_SETTING_TYPE);
                break;
            case 3:
                intent.putExtra(AppConstant.POSITION_SETTING, position);
                mPositionClicked = position;
                intent.putExtra(AppConstant.SETTING_ITEM_SELECTED_SEND, prepareListData().get(position));
                startActivityForResult(intent, OPEN_SETTING_TYPE);
                break;

        }

    }

    @Override
    public void OnIconCheckBoxOnClick(View v, int position, boolean isChecked) {
        switch (position) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;

        }
    }

    @OnClick(R.id.img_back)
    public void onBackClick() {
        finish();
    }

    @OnClick(R.id.btn_done)
    public void onDoneClick(){
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_SETTING_TYPE) {
            if (resultCode == RESULT_OK) {
                String dataString = data.getStringExtra(AppConstant.SETTING_ITEM_SELECTED_RECIEVE);
                HashMap<Integer, String> hashMap = new HashMap<>();
                hashMap.put(mPositionClicked, dataString);
                mSettingAdapter.setSelectionMap(hashMap);
                mSettingAdapter.notifyDataSetChanged();
            }
        }
    }


    private void getDataFromWay(){
        for (int i=0;i<mResponseWayData.getWayData().size();i++){
            for (int j=0;j<mResponseWayData.getWayData().get(i).getAttributesList().size();j++){
                switch (mResponseWayData.getWayData().get(i).getAttributesList()
                        .get(j).getKey()){

                    case "incline":
                        hashMapWay.put(2,mResponseWayData.getWayData().get(i).getAttributesList().get(j).getValue());
                        break;

                    case "footway":
                        hashMapWay.put(0,mResponseWayData.getWayData().get(i).getAttributesList().get(j).getValue());
                        break;

                    case "highway":
                        hashMapWay.put(1,mResponseWayData.getWayData().get(i).getAttributesList().get(j).getValue());
                        break;

                    case "width":
                        hashMapWay.put(3,mResponseWayData.getWayData().get(i).getAttributesList().get(j).getValue());
                        break;

                    default:

                }
            }
        }
    }
}
