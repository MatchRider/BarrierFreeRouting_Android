package com.disablerouting.feedback.view;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.base.BaseActivityImpl;
import com.disablerouting.capture_option.CaptureActivity;
import com.disablerouting.common.AppConstant;
import com.disablerouting.feedback.manager.CreateChangeSetManager;
import com.disablerouting.feedback.model.RequestCreateChangeSet;
import com.disablerouting.feedback.model.RequestTag;
import com.disablerouting.feedback.presenter.FeedBackScreenPresenter;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FeedbackActivity extends BaseActivityImpl implements IFeedbackView {

    private FeedBackScreenPresenter mFeedBackScreenPresenter;
    private String mChangeSetID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        ButterKnife.bind(this);

        mFeedBackScreenPresenter= new FeedBackScreenPresenter(this, new CreateChangeSetManager());
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
        mFeedBackScreenPresenter.createChangeSet(requestCreateChangeSet);
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
    public void onChangeSetId(ResponseBody responseBody) {
        hideLoader();
        if(responseBody!=null) {
            try {
                mChangeSetID = responseBody.string();
                Toast.makeText(this, responseBody.string(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFailureCreateChangeSet(String error) {
        hideLoader();
    }

    @OnClick(R.id.txv_way_point_or_distance)
    public void redirectToCaptureScreen(){
        Intent intentCaptureActivity= new Intent(this, CaptureActivity.class);
        intentCaptureActivity.putExtra(AppConstant.CHANGE_SET_ID,mChangeSetID);
        startActivity(intentCaptureActivity);
    }

    @OnClick(R.id.img_back)
    public void onBackClick() {
        finish();
    }



}
