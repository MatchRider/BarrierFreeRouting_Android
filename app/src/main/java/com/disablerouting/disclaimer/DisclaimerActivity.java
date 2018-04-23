package com.disablerouting.disclaimer;


import android.os.Bundle;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.base.BaseActivityImpl;
import com.disablerouting.common.AppConstant;

public class DisclaimerActivity extends BaseActivityImpl {


    @BindView(R.id.txv_data_side_menu)
    TextView mTextViewData;

    @BindView(R.id.txv_data_side_title)
    TextView mTextViewTitle;

    String mTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_data);
        ButterKnife.bind(this);
        if(getIntent().hasExtra(AppConstant.TITLE_TEXT)) {
            mTitle = getIntent().getStringExtra(AppConstant.TITLE_TEXT);
        }

        setData();
    }
    private void setData(){
        mTextViewTitle.setText(mTitle);
        mTextViewData.setText(getString(R.string.coming_soon));
    }

    @OnClick(R.id.img_back)
    public void onBackClick() {
        finish();
    }

}
