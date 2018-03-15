package com.disablerouting.feedback;

import com.disablerouting.common.ILoader;
import okhttp3.ResponseBody;

public interface IFeedbackView extends ILoader {

    /**
     * get Change Set id
     * @param id
     */
    void onChangeSetId(ResponseBody id);

    /**
     * To show relevant error to user
     * @param error Error message
     */
    void onFailureCreateChangeSet(String error);



}