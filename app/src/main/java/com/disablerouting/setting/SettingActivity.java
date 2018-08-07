package com.disablerouting.setting;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.base.BaseActivityImpl;
import com.disablerouting.common.AppConstant;
import com.disablerouting.curd_operations.manager.UpdateWayManager;
import com.disablerouting.curd_operations.manager.ValidateWayManager;
import com.disablerouting.curd_operations.model.*;
import com.disablerouting.setting.presenter.ISettingScreenPresenter;
import com.disablerouting.setting.presenter.SettingScreenPresenter;
import com.disablerouting.setting.setting_detail.SettingDetailActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SettingActivity extends BaseActivityImpl implements SettingAdapterListener , ISettingView {

    @BindView(R.id.rcv_setting)
    RecyclerView mRecyclerView;

    final int OPEN_SETTING_TYPE = 200;
    private SettingAdapter mSettingAdapter;
    private int mPositionClicked = -1;
    private ResponseWay mResponseWayData;
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, String> mHashMapWay = new HashMap<>();
    private ISettingScreenPresenter mISettingScreenPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        mISettingScreenPresenter= new SettingScreenPresenter(this,new UpdateWayManager(), new ValidateWayManager());
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
        if(mHashMapWay !=null){
            mSettingAdapter.setSelectionMap(mHashMapWay);
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
        if(mResponseWayData!=null){
            onUpdateWay();
        }else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_SETTING_TYPE) {
            if (resultCode == RESULT_OK) {
                String dataString = data.getStringExtra(AppConstant.SETTING_ITEM_SELECTED_RECIEVE);
                mHashMapWay.put(mPositionClicked, dataString);
                mSettingAdapter.setSelectionMap(mHashMapWay);
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
                        mHashMapWay.put(2,mResponseWayData.getWayData().get(i).getAttributesList().get(j).getValue());
                        break;

                    case "footway":
                        mHashMapWay.put(0,mResponseWayData.getWayData().get(i).getAttributesList().get(j).getValue());
                        break;

                    case "highway":
                        mHashMapWay.put(1,mResponseWayData.getWayData().get(i).getAttributesList().get(j).getValue());
                        break;

                    case "width":
                        mHashMapWay.put(3,mResponseWayData.getWayData().get(i).getAttributesList().get(j).getValue());
                        break;

                    default:

                }
            }
        }
    }



    private void onUpdateWay(){
        WayDataValidate wayDataValidate= new WayDataValidate();
        wayDataValidate.setId(mResponseWayData.getWayData().get(0).getId());
        AttributesValidate attributesValidate= new AttributesValidate();
        if(mHashMapWay.get(0)!=null && !mHashMapWay.get(0).isEmpty()) {
            attributesValidate.setFootWay(mHashMapWay.get(0));
        }
        if(mHashMapWay.get(1)!=null && !mHashMapWay.get(1).isEmpty()) {
            attributesValidate.setHighWay(mHashMapWay.get(1));
        }

        if(mHashMapWay.get(2)!=null && !mHashMapWay.get(2).isEmpty()) {
            attributesValidate.setIncline(mHashMapWay.get(2));
        }

        if(mHashMapWay.get(3)!=null && !mHashMapWay.get(3).isEmpty()) {
            attributesValidate.setHWidth(mHashMapWay.get(3));
        }
        wayDataValidate.setAttributesValidate(attributesValidate);
        List<WayDataValidate> listRequestValidate= new ArrayList<>();
        listRequestValidate.add(wayDataValidate);
        RequestValidate requestValidate= new RequestValidate();
        requestValidate.setWayDataValidates(listRequestValidate);
        mISettingScreenPresenter.onUpdate(requestValidate);
    }



    private void onValidateWay(){
        WayDataValidate wayDataValidate= new WayDataValidate();
        wayDataValidate.setId(mResponseWayData.getWayData().get(0).getId());
        AttributesValidate attributesValidate= new AttributesValidate();
        if(mHashMapWay.get(0)!=null && !mHashMapWay.get(0).isEmpty()) {
            attributesValidate.setFootWay(mHashMapWay.get(0));
        }
        if(mHashMapWay.get(1)!=null && !mHashMapWay.get(1).isEmpty()) {
            attributesValidate.setHighWay(mHashMapWay.get(1));
        }

        if(mHashMapWay.get(2)!=null && !mHashMapWay.get(2).isEmpty()) {
            attributesValidate.setIncline(mHashMapWay.get(2));
        }

        if(mHashMapWay.get(3)!=null && !mHashMapWay.get(3).isEmpty()) {
            attributesValidate.setHWidth(mHashMapWay.get(3));
        }
        wayDataValidate.setAttributesValidate(attributesValidate);
        List<WayDataValidate> listRequestValidate= new ArrayList<>();
        listRequestValidate.add(wayDataValidate);
        RequestValidate requestValidate= new RequestValidate();
        requestValidate.setWayDataValidates(listRequestValidate);
        mISettingScreenPresenter.onValidate(requestValidate);
    }
    @Override
    public void onUpdateDataReceived(ResponseUpdate responseUpdate) {
        Toast.makeText(SettingActivity.this, R.string.updated_info, Toast.LENGTH_SHORT).show();
        onValidateWay();
    }

    @Override
    public void onValidateDataReceived(ResponseWay responseUpdate) {
        Toast.makeText(SettingActivity.this, R.string.validated_way_info, Toast.LENGTH_SHORT).show();
        finish();

    }

    @Override
    public void onFailure(String error) {
        Toast.makeText(SettingActivity.this, error, Toast.LENGTH_SHORT).show();

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
