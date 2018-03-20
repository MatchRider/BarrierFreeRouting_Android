package com.disablerouting.capture_option.view;

import com.disablerouting.common.ILoader;

public interface ICaptureView extends ILoader {

    /**
     * get Change Set id
     * @param id string
     */
    void onChangeSetId(String id);

    /**
     * To show relevant error to user
     * @param error Error message
     */
    void onFailureSetChangeSet(String error);


}