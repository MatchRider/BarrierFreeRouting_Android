package com.disablerouting.setting.setting_detail;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.base.BaseActivityImpl;
import com.disablerouting.common.AppConstant;
import com.disablerouting.utils.Utility;

public class SettingDetailActivity extends BaseActivityImpl implements SettingDetailAdapterListener {

    @BindView(R.id.rcv_setting)
    RecyclerView mRecyclerView;


    @BindView(R.id.txv_title_community)
    TextView mTxvTitle;

    @BindView(R.id.rel_width)
    RelativeLayout mRelativeLayoutWidth;

    @BindView(R.id.edt_width_value)
    EditText mEdtWidth;

    private int mPositionOfTitle;
    private boolean mIsForWAY = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_detail);
        ButterKnife.bind(this);

        if (getIntent().hasExtra(AppConstant.SETTING_ITEM_SELECTED_SEND)) {
            mIsForWAY = getIntent().getBooleanExtra(AppConstant.IS_FOR_WAY, false);
            String titleToBeSet = getIntent().getStringExtra(AppConstant.SETTING_ITEM_SELECTED_SEND);
            mPositionOfTitle = getIntent().getIntExtra(AppConstant.POSITION_SETTING, -1);
            mTxvTitle.setVisibility(View.VISIBLE);
            mTxvTitle.setText(String.format("%s%s", titleToBeSet + "\n", getString(R.string.please_choose)));
            setUpRecyclerView(mPositionOfTitle);
        }
    }

    /**
     * Setup recycler view
     */
    private void setUpRecyclerView(int positionOfTitle) {
        SettingDetailAdapter settingDetailAdapter;
        if (mIsForWAY) {
            switch (positionOfTitle) {
                case 0:
                    settingDetailAdapter = new SettingDetailAdapter(Utility.prepareListDataSurface(this), this, true);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                    mRecyclerView.setAdapter(settingDetailAdapter);
                    break;
                case 1:
                    settingDetailAdapter = new SettingDetailAdapter(Utility.prepareListDataMaxSlope(this), this, false);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                    mRecyclerView.setAdapter(settingDetailAdapter);
                    break;
                case 2:
                    settingDetailAdapter = new SettingDetailAdapter(Utility.prepareListDataMaxIncline(this), this, false);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                    mRecyclerView.setAdapter(settingDetailAdapter);
                    break;
                case 3:
                    settingDetailAdapter = new SettingDetailAdapter(Utility.prepareListDataSideWalk(this), this, false);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                    mRecyclerView.setAdapter(settingDetailAdapter);
                    mRelativeLayoutWidth.setVisibility(View.VISIBLE);
                    break;

            }
        } else {
            switch (positionOfTitle) {
                case 0:
                    settingDetailAdapter = new SettingDetailAdapter(Utility.prepareListDataMaxSlope(this), this, false);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                    mRecyclerView.setAdapter(settingDetailAdapter);
                    break;
            }
        }
    }

    @OnClick(R.id.img_back)
    public void onBackClick() {
        finish();
    }

    private void setDataWhenFilterApplied(String valueSelected) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(AppConstant.SETTING_ITEM_SELECTED_RECIEVE, valueSelected);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @OnClick(R.id.img_submit)
    public void onWidthSubmitClick(){
        if(!mEdtWidth.getText().toString().isEmpty()){
            setDataWhenFilterApplied(mEdtWidth.getText().toString());
        }

    }
    @Override
    public void onDetailItemClick(View v, int position) {
        if (mIsForWAY) {
            switch (mPositionOfTitle) {
                case 0:
                    setDataWhenFilterApplied(Utility.prepareListDataSurfaceKey(this).get(position));
                    break;
                case 1:
                    setDataWhenFilterApplied(Utility.prepareListDataMaxSlopeKey(this).get(position));
                    break;
                case 2:
                    setDataWhenFilterApplied(Utility.prepareListDataMaxInclineKey(this).get(position));
                    break;
                case 3:
                    setDataWhenFilterApplied(Utility.prepareListDataSideWalkKey(this).get(position));
                    break;

            }
        } else {
            switch (mPositionOfTitle) {
                case 0:
                    setDataWhenFilterApplied(Utility.prepareListDataMaxSlopeKey(this).get(position));
                    break;
            }
        }
    }
}
