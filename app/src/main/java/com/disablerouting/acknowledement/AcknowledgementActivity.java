package com.disablerouting.acknowledement;


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

public class AcknowledgementActivity extends BaseActivityImpl {

    @BindView(R.id.txv_data_side_menu)
    TextView mTextViewData;

    @BindView(R.id.txv_data_side_menu1)
    TextView mTextViewData1;
    @BindView(R.id.txv_data_side_menu2)
    TextView mTextViewData2;
    @BindView(R.id.txv_data_side_menu3)
    TextView mTextViewData3;
    @BindView(R.id.txv_data_side_title)
    TextView mTextViewTitle;

    private String mTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acknowledgement_data);
        ButterKnife.bind(this);
        if (getIntent().hasExtra(AppConstant.TITLE_TEXT)) {
            mTitle = getIntent().getStringExtra(AppConstant.TITLE_TEXT);
        }
        setData();
    }

    private void setData() {
        mTextViewTitle.setText(mTitle);
        mTextViewData.setText(getString(R.string.acknowledgement_data));

        mTextViewData1.setText(getString(R.string.acknowledgement_data1));
        mTextViewData2.setText(getString(R.string.acknowledgement_data2));
        mTextViewData3.setText(getString(R.string.acknowledgement_data3));
        mTextViewData1.setLinkTextColor(getResources().getColor(R.color.colorWhite));
        mTextViewData2.setLinkTextColor(getResources().getColor(R.color.colorWhite));
        mTextViewData3.setLinkTextColor(getResources().getColor(R.color.colorWhite));
        Utility.makeLinks(mTextViewData1, new String[]{"https://www.heidelberg.de"}, new ClickableSpan[]{
                normalLinkClickSpan1
        });
        Utility.makeLinks(mTextViewData2, new String[]{"https://openrouteservice.org"}, new ClickableSpan[]{
                normalLinkClickSpan2
        });
        Utility.makeLinks(mTextViewData3, new String[]{"https://www.matchrider.de"}, new ClickableSpan[]{
                normalLinkClickSpan3
        });

    }

    ClickableSpan normalLinkClickSpan1 = new ClickableSpan() {
        @Override
        public void onClick(View view) {
            Uri uri = Uri.parse("https://www.heidelberg.de");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

        }

    };
    ClickableSpan normalLinkClickSpan2 = new ClickableSpan() {
        @Override
        public void onClick(View view) {
            Uri uri = Uri.parse("https://openrouteservice.org");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

        }

    };
    ClickableSpan normalLinkClickSpan3 = new ClickableSpan() {
        @Override
        public void onClick(View view) {
            Uri uri = Uri.parse("https://www.matchrider.de");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

        }

    };

    @OnClick(R.id.img_back)
    public void onBackClick() {
        finish();
    }


}
