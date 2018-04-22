package com.disablerouting.setting;


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
import com.disablerouting.setting.setting_detail.SettingDetailActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class SettingActivity extends BaseActivityImpl implements SettingAdapterListener {

    @BindView(R.id.rcv_setting)
    RecyclerView mRecyclerView;

    final int OPEN_SETTING_TYPE = 200;
    private SettingAdapter mSettingAdapter;
    private int mPositionClicked = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        setUpRecyclerView();
    }

    /**
     * Setup recycler view
     */
    private void setUpRecyclerView() {
        mSettingAdapter = new SettingAdapter(prepareListData(), this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mSettingAdapter);
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
                if (isChecked) {
                    Toast.makeText(this, position + "Verify", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, position + "Unverify", Toast.LENGTH_SHORT).show();
                }
                break;
            case 1:
                if (isChecked) {
                    Toast.makeText(this, position + "Verify", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, position + "Unverify", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if (isChecked) {
                    Toast.makeText(this, position + "Verify", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, position + "Unverify", Toast.LENGTH_SHORT).show();
                }
                break;
            case 3:
                if (isChecked) {
                    Toast.makeText(this, position + "Verify", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, position + "Unverify", Toast.LENGTH_SHORT).show();
                }
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
}
