package com.disablerouting.tutorial;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class TutorialPrefManager {

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private Context mContext;
    private int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "tutorial";
 
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
 
    @SuppressLint("CommitPrefEdits")
    public TutorialPrefManager(Context context) {
        mContext = context;
        mSharedPreferences = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        mEditor = mSharedPreferences.edit();
    }
 
    public void setFirstTimeLaunch(boolean isFirstTime) {
        mEditor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        mEditor.commit();
    }
 
    public boolean isFirstTimeLaunch() {
        return mSharedPreferences.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }
 
}