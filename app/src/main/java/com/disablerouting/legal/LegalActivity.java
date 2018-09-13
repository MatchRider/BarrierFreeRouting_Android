package com.disablerouting.legal;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.base.BaseActivityImpl;
import com.disablerouting.common.AppConstant;
import com.disablerouting.utils.Utility;

public class LegalActivity extends BaseActivityImpl {

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
        mTextViewData.setText(getString(R.string.legal_data));
        mTextViewData.setLinkTextColor(getResources().getColor(R.color.colorWhite));
        Utility.makeLinks(mTextViewData, new String[]{"https://www.github.com/MatchRider/Routing"},
                new ClickableSpan[]{
                normalLinkClickSpan1
        });

    }
    ClickableSpan normalLinkClickSpan1 = new ClickableSpan() {
        @Override
        public void onClick(View view) {
            Uri uri = Uri.parse("https://www.github.com/MatchRider/Routing");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

        }

    };

    @OnClick(R.id.img_back)
    public void onBackClick() {
        finish();
    }

}
