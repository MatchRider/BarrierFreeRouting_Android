package com.disablerouting.feedback.presenter;

import android.content.Context;
import com.disablerouting.feedback.model.RequestCreateChangeSet;

public interface IFeedbackScreenPresenter {

    /**
     * Api call to create change set
     * @param createChangeSet request model of changeset
     */
    void createChangeSet(RequestCreateChangeSet createChangeSet,Context context);

    /**
     * Disconnect ongoing calls on network
     */
    void disconnect();
}