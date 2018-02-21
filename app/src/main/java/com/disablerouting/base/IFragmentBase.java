package com.disablerouting.base;

import android.support.annotation.StringRes;

public interface IFragmentBase {
    
    /**
     * Show progress bar
     */
    void showProgress();
    
    /**
     * Hide progress bar
     */
    void hideProgress();
    
    /**
     * Method to show Toast stringRes message
     *
     * @param message
     */
    void showSnackBar(@StringRes int message);
    
    /**
     * Method to show Toast string message
     *
     * @param message
     */
    void showSnackBar(String message);
}
