package com.disablerouting.feedback.view;


import android.content.Intent;
import android.os.Bundle;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.base.BaseActivityImpl;
import com.disablerouting.capture_option.view.CaptureActivity;
import com.disablerouting.common.AppConstant;
import com.disablerouting.feedback.manager.CreateChangeSetManager;
import com.disablerouting.feedback.model.RequestCreateChangeSet;
import com.disablerouting.feedback.model.RequestTag;
import com.disablerouting.feedback.presenter.FeedBackScreenPresenter;
import com.disablerouting.route_planner.model.FeedBackModel;

import java.util.ArrayList;
import java.util.List;

public class FeedbackActivity extends BaseActivityImpl implements IFeedbackView {

    private FeedBackScreenPresenter mFeedBackScreenPresenter;
    private String mChangeSetID;
    private FeedBackModel mFeedBackModel;
    private boolean mISStartedFromSuggestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        ButterKnife.bind(this);

        mFeedBackModel = getIntent().getParcelableExtra(AppConstant.FEED_BACK_MODEL);
        mFeedBackScreenPresenter= new FeedBackScreenPresenter(this, new CreateChangeSetManager());
        if(getIntent().hasExtra(AppConstant.STARTED_FROM_SUGGESTION)){
            mISStartedFromSuggestion= getIntent().getBooleanExtra(AppConstant.STARTED_FROM_SUGGESTION,false);
        }
        callToGetChangeSet();
    }

    private void callToGetChangeSet(){
        RequestCreateChangeSet requestCreateChangeSet= new RequestCreateChangeSet();
        List<RequestTag> list = new ArrayList<>();
        RequestTag requestTag = new RequestTag("created_by","JOSM 1.61");
        list.add(requestTag);
        requestTag = new RequestTag("comment","Just adding some streetnames");
        list.add(requestTag);
        requestCreateChangeSet.setRequestTag(list);
        mFeedBackScreenPresenter.createChangeSet(requestCreateChangeSet,this);
    }



    @Override
    public void showLoader() {
        showProgress();
    }

    @Override
    public void hideLoader() {
        hideProgress();
    }

    @Override
    public void onChangeSetId(String stringId) {
        hideLoader();
        if(stringId!=null) {
            mChangeSetID = stringId;
        }
    }

    @Override
    public void onFailureCreateChangeSet(String error) {
        showSnackBar(error,this);
        hideLoader();
    }

    @OnClick(R.id.txv_way_point_or_distance)
    public void redirectToCaptureScreen(){
        Intent intentCaptureActivity= new Intent(this, CaptureActivity.class);
        if(mFeedBackModel!=null && mChangeSetID!=null) {
            mFeedBackModel.setChangeSetID(mChangeSetID);
        }
        intentCaptureActivity.putExtra(AppConstant.FEED_BACK_MODEL,mFeedBackModel);
        intentCaptureActivity.putExtra(AppConstant.STARTED_FROM_SUGGESTION,mISStartedFromSuggestion);
        startActivity(intentCaptureActivity);
    }

    @OnClick(R.id.img_back)
    public void onBackClick() {
        finish();
    }

}
