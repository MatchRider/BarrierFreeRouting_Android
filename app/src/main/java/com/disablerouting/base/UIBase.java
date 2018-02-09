package com.disablerouting.base;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;

public interface UIBase {

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
    void showSnackBar(@StringRes int message, AppCompatActivity context);
    
    /**
     * Method to show Toast string message
     *
     * @param message
     */
    void showSnackBar(String message, AppCompatActivity context);

    /**
     * Method to launch another activity and finish current one without bundle
     *
     * @param cls Activity class to be redirected
     * @param <T> Current activity context
     */
    <T> void launchActivity(Activity _context, Class<T> cls);
    
    /**
     * Method to launch another activity and finish current one with bundle
     ** @param intent Intent data for passing destination activity
     */
    <T> void launchActivity(Intent intent);



}