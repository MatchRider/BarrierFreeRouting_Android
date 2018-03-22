package com.disablerouting.feedback.presenter;


import android.support.annotation.NonNull;
import com.disablerouting.api.ErrorResponse;
import com.disablerouting.feedback.manager.CreateChangeSetManager;
import com.disablerouting.feedback.model.RequestCreateChangeSet;
import com.disablerouting.feedback.view.IFeedbackView;

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
    public void onSuccessChangeSet(String data) {
        if (mIFeedbackView != null) {
            mIFeedbackView.hideLoader();
            mIFeedbackView.onChangeSetId(data);
        }
    }

    @Override
    public void onFailureChangeSet(@NonNull ErrorResponse errorResponse) {
        if (mIFeedbackView != null) {
            mIFeedbackView.hideLoader();
            mIFeedbackView.onFailureCreateChangeSet(errorResponse.getErrorMessage());
        }
    }
}
