package com.disablerouting.success_screen;

import android.content.Intent;
import android.os.Bundle;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.base.BaseActivityImpl;
import com.disablerouting.common.AppConstant;
import com.disablerouting.route_planner.view.RoutePlannerActivity;
import com.disablerouting.suggestions.view.SuggestionsActivity;


public class SuccessActivity extends BaseActivityImpl {

    private boolean mISStartedFromSuggestion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        ButterKnife.bind(this);
        if(getIntent().hasExtra(AppConstant.STARTED_FROM_SUGGESTION)){
            mISStartedFromSuggestion= getIntent().getBooleanExtra(AppConstant.STARTED_FROM_SUGGESTION,false);
        }

    }

    @OnClick(R.id.btn_home)
    public void onBackPress() {
        if(!mISStartedFromSuggestion){
            redirectToHome();
        }else {
            redirectToSuggestion();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(!mISStartedFromSuggestion){
            redirectToHome();
        }else {
            redirectToSuggestion();
        }
    }

    private void redirectToHome() {
        finish();
        Intent goToRoute = new Intent(this, RoutePlannerActivity.class);
        startActivity(goToRoute);

    }

    private void redirectToSuggestion() {
        finish();
        Intent goToSuggestion = new Intent(this, SuggestionsActivity.class);
        startActivity(goToSuggestion);

    }


}
