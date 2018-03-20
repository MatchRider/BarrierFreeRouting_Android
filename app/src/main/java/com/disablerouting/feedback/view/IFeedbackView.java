package com.disablerouting.feedback.view;

import com.disablerouting.common.ILoader;

public interface IFeedbackView extends ILoader {

    /**
     * get Change Set id
     * @param id
     */
    void onChangeSetId(String id);

    /**
     * To show relevant error to user
     * @param error Error message
     */
    void onFailureCreateChangeSet(String error);



}