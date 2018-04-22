package com.disablerouting.legal;


import android.os.Bundle;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.base.BaseActivityImpl;

public class LegalActivity extends BaseActivityImpl {

    @BindView(R.id.txv_data_side_menu)
    TextView mTextViewData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_data);
        ButterKnife.bind(this);
        setData();
    }
    private void setData(){
        mTextViewData.setText(getString(R.string.legal_data));
    }

    @OnClick(R.id.img_back)
    public void onBackClick() {
        finish();
    }

}
