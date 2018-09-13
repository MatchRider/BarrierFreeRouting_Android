package com.disablerouting.acknowledement;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.base.BaseActivityImpl;
import com.disablerouting.common.AppConstant;

public class AcknowledgementActivity extends BaseActivityImpl {

    @BindView(R.id.txv_data_side_menu)
    TextView mTextViewData;

    @BindView(R.id.txv_data_side_menu1)
    TextView mTextViewData1;
    @BindView(R.id.txv_data_side_menu2)
    TextView mTextViewData2;
    @BindView(R.id.txv_data_side_menu3)
    TextView mTextViewData3;
    @BindView(R.id.txv_data_side_menu4)
    TextView mTextViewData4;

    @BindView(R.id.txv_data_side_title)
    TextView mTextViewTitle;

    String mTitle;


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
        mTextViewData4.setText(getString(R.string.acknowledgement_data4));
        mTextViewData1.setLinkTextColor(Color.BLUE);
        mTextViewData2.setLinkTextColor(Color.BLUE);
        mTextViewData3.setLinkTextColor(Color.BLUE);
        mTextViewData4.setLinkTextColor(Color.BLUE);
        makeLinks(mTextViewData1, new String[]{"https://www.heidelberg.de"}, new ClickableSpan[]{
                normalLinkClickSpan1
        });
        makeLinks(mTextViewData2, new String[]{"https://openrouteservice.org"}, new ClickableSpan[]{
                normalLinkClickSpan2
        });
        makeLinks(mTextViewData3, new String[]{"https://www.matchrider.de"}, new ClickableSpan[]{
                normalLinkClickSpan3
        });
        makeLinks(mTextViewData4, new String[]{"https://www.wolfert-gmbh.de"}, new ClickableSpan[]{
                normalLinkClickSpan4
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
    ClickableSpan normalLinkClickSpan4 = new ClickableSpan() {
        @Override
        public void onClick(View view) {
            Uri uri = Uri.parse("https://www.wolfert-gmbh.de");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

        }

    };

    @OnClick(R.id.img_back)
    public void onBackClick() {
        finish();
    }

    public void makeLinks(TextView textView, String[] links, ClickableSpan[] clickableSpans) {
        SpannableString spannableString = new SpannableString(textView.getText());
        for (int i = 0; i < links.length; i++) {
            ClickableSpan clickableSpan = clickableSpans[i];
            String link = links[i];

            int startIndexOfLink = textView.getText().toString().indexOf(link);
            spannableString.setSpan(clickableSpan, startIndexOfLink,
                    startIndexOfLink + link.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textView.setHighlightColor(
                Color.TRANSPARENT); // prevent TextView change background when highlight
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(spannableString, TextView.BufferType.SPANNABLE);
    }

}
