package com.disablerouting.setting.setting_detail;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
import com.disablerouting.widget.DecimalDigitsInputFilter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingDetailActivity extends BaseActivityImpl implements SettingDetailAdapterListener, TextView.OnEditorActionListener {

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
    private String mValueReceived="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_detail);
        ButterKnife.bind(this);

        if (getIntent().hasExtra(AppConstant.SETTING_ITEM_SELECTED_SEND)) {
            mIsForWAY = getIntent().getBooleanExtra(AppConstant.IS_FOR_WAY, false);
            if(getIntent().hasExtra(AppConstant.VALUE_FOR_EDITOR)){
                mValueReceived = getIntent().getStringExtra(AppConstant.VALUE_FOR_EDITOR);
            }
            String titleToBeSet = getIntent().getStringExtra(AppConstant.SETTING_ITEM_SELECTED_SEND);
            mPositionOfTitle = getIntent().getIntExtra(AppConstant.POSITION_SETTING, -1);
            mTxvTitle.setVisibility(View.VISIBLE);
            mTxvTitle.setText(String.format("%s%s", titleToBeSet + "\n", getString(R.string.please_choose)));
            setUpRecyclerView(mPositionOfTitle);
            mEdtWidth.setOnEditorActionListener(this);
            mEdtWidth.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2)});
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
                    boolean isValueAddSurface =true;
                    for (int i=0 ; i< Utility.prepareListDataMaxInclineKey(this).size();i++){
                        if(Utility.prepareListDataMaxInclineKey(this).get(i).contains(mValueReceived)){
                            isValueAddSurface = false;
                        }
                    }
                    if(isValueAddSurface){
                        mEdtWidth.setText(mValueReceived);
                    }
                    mRelativeLayoutWidth.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    settingDetailAdapter = new SettingDetailAdapter(Utility.prepareListDataSideWalk(this), this, false);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                    mRecyclerView.setAdapter(settingDetailAdapter);

                    boolean isValueAddWidth =true;
                    for (int i=0 ; i< Utility.prepareListDataMaxInclineKey(this).size();i++){
                        if(Utility.prepareListDataMaxInclineKey(this).get(i).contains(mValueReceived)){
                            isValueAddWidth = false;
                        }
                    }
                    if(isValueAddWidth){
                        mEdtWidth.setText(mValueReceived);
                    }
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
            if(mPositionOfTitle==2){
                if(!mEdtWidth.getText().toString().contains("%")) {
                    setDataWhenFilterApplied(mEdtWidth.getText().toString() + "%");
                }else {
                    setDataWhenFilterApplied(mEdtWidth.getText().toString());

                }
            }
            if(mPositionOfTitle==3){
                setDataWhenFilterApplied(Utility.changeCommaToDot(mEdtWidth.getText().toString()));
            }
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

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            onWidthSubmitClick();
            return true;
        }
        return false;
    }

}
