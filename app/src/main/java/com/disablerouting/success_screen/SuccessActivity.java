package com.disablerouting.success_screen;

import android.content.Intent;
import android.os.Bundle;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.base.BaseActivityImpl;
import com.disablerouting.sidemenu.HomeActivity;


public class SuccessActivity extends BaseActivityImpl {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_home)
    public void onBackPress() {
        redirectToHome();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        redirectToHome();
    }

    private void redirectToHome() {
        finish();
        Intent goToA = new Intent(this, HomeActivity.class);
        goToA.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(goToA);

    }
}
