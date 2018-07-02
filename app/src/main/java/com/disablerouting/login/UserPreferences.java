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
        String savedToken = mPreferences.getString(ACCESS_TOKEN, null);
        return savedToken;
    }


    public void saveToken(String token) {
        mPreferences.edit().putString(ACCESS_TOKEN, token).apply();
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

}
