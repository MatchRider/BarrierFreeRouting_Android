package com.disablerouting.utils;


import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class Utility {
    /**
     * Hides the soft keyboard
     */
    public static void hideSoftKeyboard(AppCompatActivity activity, View autoCompleteTextView) {
        if (activity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static TranslateAnimation translate(float fromX, float toX, float fromY,
                                        float toY, int ms) {

        TranslateAnimation transAnim = new TranslateAnimation(fromX, toX, fromY, toY);
        transAnim.setDuration(ms);
        return transAnim;
    }



}
