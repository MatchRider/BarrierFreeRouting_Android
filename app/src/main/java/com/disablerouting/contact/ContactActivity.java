package com.disablerouting.contact;


import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
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

    @BindView(R.id.edt_name)
    EditText mEdtName;

    @BindView(R.id.edt_email)
    EditText mEdtEmail;

    @BindView(R.id.edt_text)
    TextView mTxvTextData;

    private String mTitle;
    private String NAME, EMAIL, MESSAGE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        ButterKnife.bind(this);
        if (getIntent().hasExtra(AppConstant.TITLE_TEXT)) {
            mTitle = getIntent().getStringExtra(AppConstant.TITLE_TEXT);
        }

        setData();

    }

    private void setData() {
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
        GetData();
        if(validation()) {
            sendEmail();
        }
    }

    private boolean validation() {
        if (NAME.isEmpty()) {
            Toast.makeText(ContactActivity.this, getResources().getString(R.string.validate_name_msg), Toast.LENGTH_SHORT).show();
            return false;
        } else if (EMAIL.isEmpty()) {
            Toast.makeText(ContactActivity.this, getResources().getString(R.string.validate_email_message), Toast.LENGTH_SHORT).show();
            return false;
        } else if (MESSAGE.isEmpty()) {
            Toast.makeText(ContactActivity.this, getResources().getString(R.string.validate_message_enter), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void GetData() {
        NAME = mEdtName.getText().toString();
        EMAIL = mEdtEmail.getText().toString();
        MESSAGE = mTxvTextData.getText().toString();

    }

    protected void sendEmail() {
        String[] TO = {"Buergerservice@Heidelberg.de"};
        //String[] TO = {"kirti.na@daffodilsw.coom"};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        //emailIntent.setDataAndType(Uri.parse("mailto:"), "text/plain");
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.query_subject));
        emailIntent.putExtra(Intent.EXTRA_TEXT, NAME + "\n" + EMAIL + "\n" + MESSAGE);

        try {
            startActivity(Intent.createChooser(emailIntent, getString(R.string.send_mail)));
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ContactActivity.this, R.string.error_contact, Toast.LENGTH_SHORT).show();
        }
    }

}
