package com.disablerouting.login.model;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;


/**
 * Provides app settings persistence.
 * This class is to be used as a context singleton.
 */
public final class SearchPreferences {
    private static final String PREFS_NAME = "ACCESS_SEARCH";
    private static final String ACCESS_USER_SEARCH = "USER_SEARCH_DETAIL";
    private static SearchPreferences sInstance;
    private final SharedPreferences mPreferences;

    /**
     * Protected constructor, for testing purposes only. Use {@link #getInstance(Context)} for external usage.
     *
     * @param mPreferences Shared preference instance.
     */
    private SearchPreferences(SharedPreferences mPreferences) {
        this.mPreferences = mPreferences;
    }

    public void saveUserSearch(UserSearchModel userSearchModel) {
        Gson gson = new Gson();
        String json = gson.toJson(userSearchModel);
        mPreferences.edit().putString(ACCESS_USER_SEARCH, json).apply();
    }
    public UserSearchModel getUserSearch() {
        Gson gson = new Gson();
        String json = mPreferences.getString(ACCESS_USER_SEARCH, null);
        return gson.fromJson(json, UserSearchModel.class);
    }
    /**
     * Get singleton instance.
     *
     * @param context context for lazy initialization.
     * @return Global instance of Settings Store.
     */
    public static SearchPreferences getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new SearchPreferences(context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE));
        }

        return sInstance;
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
