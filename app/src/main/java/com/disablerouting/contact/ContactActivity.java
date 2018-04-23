package com.disablerouting.contact;


import android.os.Bundle;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.base.BaseActivityImpl;
import com.disablerouting.common.AppConstant;
import com.disablerouting.utils.Utility;

public class ContactActivity extends BaseActivityImpl {

    @BindView(R.id.txv_data_side_title)
    TextView mTextViewTitle;

    String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        ButterKnife.bind(this);
        if(getIntent().hasExtra(AppConstant.TITLE_TEXT)) {
            mTitle = getIntent().getStringExtra(AppConstant.TITLE_TEXT);
        }

        setData();
    }
    private void setData(){
        mTextViewTitle.setText(mTitle);
    }

    @OnClick(R.id.img_back)
    public void onBackClick() {
        Utility.hideSoftKeyboard(this);
        finish();
    }

    @OnClick(R.id.btn_send)
    public void onSendClick() {
        Utility.hideSoftKeyboard(this);
        finish();
    }

}
