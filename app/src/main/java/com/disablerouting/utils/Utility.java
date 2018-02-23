package com.disablerouting.utils;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class Utility {
    /**
     * Hides the soft keyboard
     */
    public static void hideSoftKeyboard(AppCompatActivity activity) {
        if (activity!=null && activity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            assert inputMethodManager != null;
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static TranslateAnimation translate(float fromX, float toX, float fromY,
                                        float toY, int ms) {
        TranslateAnimation transAnim = new TranslateAnimation(fromX, toX, fromY, toY);
        transAnim.setDuration(ms);
        return transAnim;
    }

    public static int calculatePopUpHeight(Context context){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

}
