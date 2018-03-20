package com.disablerouting.capture_option.presenter;


import android.support.annotation.NonNull;
import com.disablerouting.api.ErrorResponseNew;
import com.disablerouting.capture_option.view.ICaptureView;
import com.disablerouting.capture_option.manager.SetChangeSetManager;
import com.disablerouting.capture_option.model.RequestCreateNode;

public class CaptureScreenPresenter implements ICaptureScreenPresenter , ISetChangeSetResponseReceiver {

    private ICaptureView mICaptureView;
    private SetChangeSetManager mSetChangeSetManager;

    public CaptureScreenPresenter(ICaptureView ICaptureView, SetChangeSetManager setChangeSetManager) {
        mICaptureView = ICaptureView;
        mSetChangeSetManager = setChangeSetManager;
    }

    @Override
    public void setChangeSet(RequestCreateNode requestCreateNode) {
        if (mICaptureView != null) {
            mICaptureView.showLoader();
            mSetChangeSetManager.setChangeSet(this,requestCreateNode);
        }
    }

    @Override
    public void disconnect() {
        if (mICaptureView != null) {
            mSetChangeSetManager.cancel();
        }
    }

    @Override
    public void onSuccessChangeSet(String data) {
        if (mICaptureView != null) {
            mICaptureView.hideLoader();
            mICaptureView.onChangeSetId(data);
        }
    }

    @Override
    public void onFailureChangeSet(@NonNull ErrorResponseNew errorResponse) {
        if (mICaptureView != null) {
            mICaptureView.hideLoader();
            mICaptureView.onFailureSetChangeSet(errorResponse.getErrorMessage());
        }
    }
}
