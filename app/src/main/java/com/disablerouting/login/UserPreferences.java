package com.disablerouting.login;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Provides app settings persistence.
 * This class is to be used as a context singleton.
 */
public final class UserPreferences {
    private static final String PREFS_NAME = "ACCESS_TOKEN_OSM";
    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    private static final String ACCESS_USER_DETAIL = "USER_DETAIL";
    private static UserPreferences sInstance;
    private final SharedPreferences mPreferences;

    /**
     * Protected constructor, for testing purposes only. Use {@link #getInstance(Context)} for external usage.
     *
     * @param mPreferences Shared preference instance.
     */
    private UserPreferences(SharedPreferences mPreferences) {
        this.mPreferences = mPreferences;
    }


    public String getAccessToken() {
        return mPreferences.getString(ACCESS_TOKEN, null);
    }


    public void saveToken(String token) {
        mPreferences.edit().putString(ACCESS_TOKEN, token).apply();
    }

    public void saveUSERID(String token) {
        mPreferences.edit().putString(ACCESS_USER_DETAIL, token).apply();
    }
    public String getUserDetail() {
        return mPreferences.getString(ACCESS_USER_DETAIL, null);
    }
    /**
     * Get singleton instance.
     *
     * @param context context for lazy initialization.
     * @return Global instance of Settings Store.
     */
    public static UserPreferences getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new UserPreferences(context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE));
        }

        return sInstance;
    }

    public boolean isUserLoggedIn() {
        return mPreferences!=null && mPreferences.getString(ACCESS_TOKEN,null) != null
                && mPreferences.getString(ACCESS_USER_DETAIL,null)!=null;
    }
    /**
     * {@inheritDoc}
     */
    public void destroySession() throws IllegalStateException {
        checkForInitialization();
        mPreferences.edit().clear().apply();
    }

    /*
     * Throws IllegalStateException if initialization has not been performed.
     */
    protected void checkForInitialization() {
        if (mPreferences == null) {
            throw new IllegalStateException("Initialization is not performed yet.");
        }
    }

}
