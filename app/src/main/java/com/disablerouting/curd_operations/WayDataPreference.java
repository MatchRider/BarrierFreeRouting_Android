package com.disablerouting.curd_operations;

import android.content.Context;
import android.content.SharedPreferences;
import com.disablerouting.curd_operations.model.ListWayData;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WayDataPreference {

    private static final String PREF_NAME = "WayDataPreference";
    private int PRIVATE_MODE = 0;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String DataKeyValidated = "ListWayValidated";
    private String DataKeyNotValidated = "ListWayNotValidated";
    private static WayDataPreference sInstance;

    private WayDataPreference(SharedPreferences mPreferences) {
        this.preferences = mPreferences;
        this.editor = preferences.edit();
    }


    /**
     * Get singleton instance.
     *
     * @param context context for lazy initialization.
     * @return Global instance of Settings Store.
     */
    public static WayDataPreference getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new WayDataPreference(context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE));
        }

        return sInstance;
    }

    //add data
    public void saveValidateWayData(List<ListWayData> listWayData) {
        Gson gson = new Gson();
        String jsonCart = gson.toJson(listWayData);
        editor.putString(DataKeyValidated, jsonCart);
        editor.commit();
    }

    //get data
    public ArrayList<ListWayData> getValidateWayData() {
        List<ListWayData> listWayDataList;
        if (preferences.contains(DataKeyValidated)) {
            String jsonCart = preferences.getString(DataKeyValidated, null);
            Gson gson = new Gson();
            ListWayData[] listWayData = gson.fromJson(jsonCart,
                    ListWayData[].class);

            listWayDataList = Arrays.asList(listWayData);
            listWayDataList = new ArrayList<ListWayData>(listWayDataList);
        } else {
            try {
                return new ArrayList<ListWayData>();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return (ArrayList<ListWayData>) listWayDataList;
    }
    //add data
    public void saveNotValidatedWayData(List<ListWayData> listWayData) {
        Gson gson = new Gson();
        String jsonCart = gson.toJson(listWayData);
        editor.putString(DataKeyNotValidated, jsonCart);
        editor.commit();
    }

    //get data
    public ArrayList<ListWayData> getNotValidatedWayData() {
        List<ListWayData> listWayDataList;
        if (preferences.contains(DataKeyNotValidated)) {
            String jsonCart = preferences.getString(DataKeyNotValidated, null);
            Gson gson = new Gson();
            ListWayData[] listWayData = gson.fromJson(jsonCart,
                    ListWayData[].class);

            listWayDataList = Arrays.asList(listWayData);
            listWayDataList = new ArrayList<ListWayData>(listWayDataList);
        } else {
            try {
                return new ArrayList<ListWayData>();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return (ArrayList<ListWayData>) listWayDataList;
    }

    //clear data
    public void clearWayDataSharedPrefsData() {
        editor.clear();
        editor.commit();
    }
    protected void checkForInitialization() {
        if (preferences == null) {
            throw new IllegalStateException("Initialization is not performed yet.");
        }
    }

}
