package com.disablerouting.feedback.presenter;


import android.support.annotation.NonNull;
import com.disablerouting.api.ErrorResponseNew;
import com.disablerouting.feedback.view.IFeedbackView;
import com.disablerouting.feedback.manager.CreateChangeSetManager;
import com.disablerouting.feedback.model.RequestCreateChangeSet;
import okhttp3.ResponseBody;

public class FeedBackScreenPresenter implements IFeedbackScreenPresenter,
        IChangeSetResponseReceiver {

    private IFeedbackView mIFeedbackView;
    private CreateChangeSetManager mCreateChangeSetManager;

    public FeedBackScreenPresenter(IFeedbackView iFeedbackView,
                                   CreateChangeSetManager createChangeSetManager ) {
        mIFeedbackView = iFeedbackView;
        mCreateChangeSetManager = createChangeSetManager;
    }

    @Override
    public void createChangeSet(RequestCreateChangeSet createChangeSet) {
        if (mIFeedbackView != null) {
            mIFeedbackView.showLoader();
            mCreateChangeSetManager.getCreateChangeSet(this,createChangeSet);
        }
    }

    @Override
    public void disconnect() {
        if (mCreateChangeSetManager != null) {
            mCreateChangeSetManager.cancel();
        }

    }

    @Override
    public void onSuccessChangeSet(ResponseBody data) {
        if (mIFeedbackView != null) {
            mIFeedbackView.hideLoader();
            mIFeedbackView.onChangeSetId(data);
        }
    }

    @Override
    public void onFailureChangeSet(@NonNull ErrorResponseNew errorResponse) {
        if (mIFeedbackView != null) {
            mIFeedbackView.hideLoader();
            mIFeedbackView.onFailureCreateChangeSet(errorResponse.getErrorMessage());
        }
    }
}
