package com.disablerouting.feedback;


import android.os.Bundle;
import com.disablerouting.R;
import com.disablerouting.base.BaseActivityImpl;
import com.disablerouting.feedback.manager.CreateChangeSetManager;
import com.disablerouting.feedback.model.RequestCreateChangeSet;
import com.disablerouting.feedback.model.RequestTag;
import com.disablerouting.feedback.presenter.FeedBackScreenPresenter;
import okhttp3.ResponseBody;

import java.util.ArrayList;
import java.util.List;

public class FeedbackActivity extends BaseActivityImpl implements IFeedbackView {

    private FeedBackScreenPresenter mFeedBackScreenPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        mFeedBackScreenPresenter= new FeedBackScreenPresenter(this, new CreateChangeSetManager());
        callToGetChangeSet();
    }

    private void callToGetChangeSet(){
        showLoader();
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
    public void onChangeSetId(ResponseBody id) {
        hideLoader();
    }

    @Override
    public void onFailureCreateChangeSet(String error) {
        hideLoader();
    }
}
