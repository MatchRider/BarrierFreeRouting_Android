package com.disablerouting.curd_operations;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.disablerouting.curd_operations.model.ListWayData;
import com.disablerouting.curd_operations.model.NodeReference;
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
    private String DataKeyValidatedNode = "ListWayValidatedNode";
    private String DataKeyNotValidatedNode = "ListWayNotValidatedNode";

    private String DataKeyValidatedOSM = "ListWayValidatedOSM";
    private String DataKeyNotValidatedOSM = "ListWayNotValidatedOSM";
    private String DataKeyValidatedNodeOSM = "ListWayValidatedNodeOSM";
    private String DataKeyNotValidatedNodeOSM = "ListWayNotValidatedNodeOSM";

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
        String jsonValidated = gson.toJson(listWayData);
        editor.putString(DataKeyValidated, jsonValidated);
        editor.commit();
    }

    //get data
    public List<ListWayData> getValidateWayData() {
        List<ListWayData> listWayDataList = null;
        if (preferences.contains(DataKeyValidated)) {
            String jsonValidated = preferences.getString(DataKeyValidated, null);
            Gson gson = new Gson();
            ListWayData[] listWayData;
            try {
                if (!TextUtils.isEmpty(jsonValidated)) {
                    listWayData = gson.fromJson(jsonValidated,
                            ListWayData[].class);
                    listWayDataList = Arrays.asList(listWayData);
                } else {
                    listWayDataList = new ArrayList<>();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        } else {
            try {
                return new ArrayList<ListWayData>();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return listWayDataList;
    }
    //add data
    public void saveNotValidatedWayData(List<ListWayData> listWayData) {
        Gson gson = new Gson();
        String jsonNotValidated = gson.toJson(listWayData);
        editor.putString(DataKeyNotValidated, jsonNotValidated);
        editor.commit();
    }

    //get data
    public List<ListWayData> getNotValidatedWayData() {
        List<ListWayData> listWayDataList = null;
        if (preferences.contains(DataKeyNotValidated)) {
            String jsonNotValidated = preferences.getString(DataKeyNotValidated, null);
            Gson gson = new Gson();
            ListWayData[] listWayData;
            try {
                if (!TextUtils.isEmpty(jsonNotValidated)) {
                    listWayData = gson.fromJson(jsonNotValidated,
                            ListWayData[].class);
                    listWayDataList = Arrays.asList(listWayData);
                }else {
                    listWayDataList = new ArrayList<>();
                }  
            }
            catch (Exception e){
                e.printStackTrace();
            }
           
        } else {
            try {
                return new ArrayList<ListWayData>();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return listWayDataList;
    }
    //add data
    public void saveValidateDataNode(List<NodeReference> nodeReferences) {
        Gson gson = new Gson();
        String jsonValidated = gson.toJson(nodeReferences);
        editor.putString(DataKeyValidatedNode, jsonValidated);
        editor.commit();
    }

    //get data
    public List<NodeReference> getValidateDataNode() {
        List<NodeReference> nodeReferenceList = null;
        if (preferences.contains(DataKeyValidatedNode)) {
            String jsonValidated = preferences.getString(DataKeyValidatedNode, null);
            Gson gson = new Gson();
            NodeReference[] nodeReferences;
            try {
                if (!TextUtils.isEmpty(jsonValidated)) {
                    nodeReferences = gson.fromJson(jsonValidated,
                            NodeReference[].class);
                    nodeReferenceList = Arrays.asList(nodeReferences);
                } else {
                    nodeReferenceList = new ArrayList<>();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        } else {
            try {
                return new ArrayList<NodeReference>();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return nodeReferenceList;
    }
    //add data
    public void saveNotValidateDataNode(List<NodeReference> nodeReferences) {
        Gson gson = new Gson();
        String jsonNotValidated = gson.toJson(nodeReferences);
        editor.putString(DataKeyNotValidatedNode, jsonNotValidated);
        editor.commit();
    }

    //get data
    public List<NodeReference> getNotValidateDataNode() {
        List<NodeReference> nodeReferenceList = null;
        if (preferences.contains(DataKeyNotValidatedNode)) {
            String jsonValidated = preferences.getString(DataKeyNotValidatedNode, null);
            Gson gson = new Gson();
            NodeReference[] nodeReferences;
            try {
                if (!TextUtils.isEmpty(jsonValidated)) {
                    nodeReferences = gson.fromJson(jsonValidated,
                            NodeReference[].class);
                    nodeReferenceList = Arrays.asList(nodeReferences);
                } else {
                    nodeReferenceList = new ArrayList<>();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        } else {
            try {
                return new ArrayList<NodeReference>();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return nodeReferenceList;
    }
    //..............................................................
    //add data
    public void saveValidateWayDataOSM(List<ListWayData> listWayData) {
        Gson gson = new Gson();
        String jsonValidated = gson.toJson(listWayData);
        editor.putString(DataKeyValidatedOSM, jsonValidated);
        editor.commit();
    }

    //get data
    public List<ListWayData> getValidateWayDataOSM() {
        List<ListWayData> listWayDataList = null;
        if (preferences.contains(DataKeyValidatedOSM)) {
            String jsonValidated = preferences.getString(DataKeyValidatedOSM, null);
            Gson gson = new Gson();
            ListWayData[] listWayData;
            try {
                if (!TextUtils.isEmpty(jsonValidated)) {
                    listWayData = gson.fromJson(jsonValidated,
                            ListWayData[].class);
                    listWayDataList = Arrays.asList(listWayData);
                } else {
                    listWayDataList = new ArrayList<>();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        } else {
            try {
                return new ArrayList<ListWayData>();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return listWayDataList;
    }
    //add data
    public void saveNotValidatedWayDataOSM(List<ListWayData> listWayData) {
        Gson gson = new Gson();
        String jsonNotValidated = gson.toJson(listWayData);
        editor.putString(DataKeyNotValidatedOSM, jsonNotValidated);
        editor.commit();
    }

    //get data
    public List<ListWayData> getNotValidatedWayDataOSM() {
        List<ListWayData> listWayDataList = null;
        if (preferences.contains(DataKeyNotValidatedOSM)) {
            String jsonNotValidated = preferences.getString(DataKeyNotValidatedOSM, null);
            Gson gson = new Gson();
            ListWayData[] listWayData;
            try {
                if (!TextUtils.isEmpty(jsonNotValidated)) {
                    listWayData = gson.fromJson(jsonNotValidated,
                            ListWayData[].class);
                    listWayDataList = Arrays.asList(listWayData);
                }else {
                    listWayDataList = new ArrayList<>();
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }

        } else {
            try {
                return new ArrayList<ListWayData>();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return listWayDataList;
    }
    //add data
    public void saveValidateDataNodeOSM(List<NodeReference> nodeReferences) {
        Gson gson = new Gson();
        String jsonValidated = gson.toJson(nodeReferences);
        editor.putString(DataKeyValidatedNodeOSM, jsonValidated);
        editor.commit();
    }

    //get data
    public List<NodeReference> getValidateDataNodeOSM() {
        List<NodeReference> nodeReferenceList = null;
        if (preferences.contains(DataKeyValidatedNodeOSM)) {
            String jsonValidated = preferences.getString(DataKeyValidatedNodeOSM, null);
            Gson gson = new Gson();
            NodeReference[] nodeReferences;
            try {
                if (!TextUtils.isEmpty(jsonValidated)) {
                    nodeReferences = gson.fromJson(jsonValidated,
                            NodeReference[].class);
                    nodeReferenceList = Arrays.asList(nodeReferences);
                } else {
                    nodeReferenceList = new ArrayList<>();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        } else {
            try {
                return new ArrayList<NodeReference>();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return nodeReferenceList;
    }
    //add data
    public void saveNotValidateDataNodeOSM(List<NodeReference> nodeReferences) {
        Gson gson = new Gson();
        String jsonNotValidated = gson.toJson(nodeReferences);
        editor.putString(DataKeyNotValidatedNodeOSM, jsonNotValidated);
        editor.commit();
    }

    //get data
    public List<NodeReference> getNotValidateDataNodeOSM() {
        List<NodeReference> nodeReferenceList = null;
        if (preferences.contains(DataKeyNotValidatedNodeOSM)) {
            String jsonValidated = preferences.getString(DataKeyNotValidatedNodeOSM, null);
            Gson gson = new Gson();
            NodeReference[] nodeReferences;
            try {
                if (!TextUtils.isEmpty(jsonValidated)) {
                    nodeReferences = gson.fromJson(jsonValidated,
                            NodeReference[].class);
                    nodeReferenceList = Arrays.asList(nodeReferences);
                } else {
                    nodeReferenceList = new ArrayList<>();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        } else {
            try {
                return new ArrayList<NodeReference>();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return nodeReferenceList;
    }

    //clear data
    public void clearWayDataSharedPrefs() {
        editor.clear();
        editor.commit();
        preferences.edit().clear().apply();

    }
    protected void checkForInitialization() {
        if (preferences == null) {
            throw new IllegalStateException("Initialization is not performed yet.");
        }
    }

}
