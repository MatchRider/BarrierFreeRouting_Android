package com.disablerouting.setting;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.disablerouting.BuildConfig;
import com.disablerouting.R;
import com.disablerouting.api.ApiEndPoint;
import com.disablerouting.base.BaseActivityImpl;
import com.disablerouting.common.AppConstant;
import com.disablerouting.curd_operations.manager.UpdateWayManager;
import com.disablerouting.curd_operations.model.*;
import com.disablerouting.login.AsyncTaskOsmApi;
import com.disablerouting.login.IAysncTaskOsm;
import com.disablerouting.login.OauthData;
import com.disablerouting.login.UserPreferences;
import com.disablerouting.setting.model.SettingModel;
import com.disablerouting.setting.presenter.ISettingScreenPresenter;
import com.disablerouting.setting.presenter.SettingScreenPresenter;
import com.disablerouting.setting.setting_detail.SettingDetailActivity;
import com.disablerouting.utils.Utility;
import com.github.scribejava.core.model.Verb;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class SettingActivity extends BaseActivityImpl implements SettingAdapterListener, ISettingView,
        IAysncTaskOsm {

    @BindView(R.id.rcv_setting)
    RecyclerView mRecyclerView;

    @BindView(R.id.rel_progress_bar)
    RelativeLayout mRelativeLayoutProgress;

    @BindView(R.id.txv_sidewalk)
    TextView mTxvSideWalk;

    final int OPEN_SETTING_TYPE = 200;
    private SettingAdapter mSettingAdapter;
    private ListWayData mListWayData;
    private ListWayData mListDataSEND;
    private NodeReference mNodeRefSEND;

    private NodeReference mNodeReference;
    private boolean mIsValidScreen = false;

    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, Attributes> mHashMapWay = new HashMap<>();
    private ISettingScreenPresenter mISettingScreenPresenter;
    private String mChangeSetID = null;
    private String mVersionNumber;
    private String mUpdateVersionNumber;
    private String mWayID;
    private String mNodeID;
    private AsyncTaskOsmApi asyncTaskOsmApi;
    private int mPositionClicked = -1;
    private boolean mIsForWAY = false;
    private boolean isValidFORCall;
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, String> mNodeIdsCreated = new HashMap<>();
    private String mWayIdNew = null;
    private Integer mNodeRefIndex = -1;
    private boolean mWayIdAvailable = false;
    private Handler mHandler = new Handler();
    private Integer mNodeUpdate = 0;
    private boolean mISFromOSM = false;
    private String mStringChoosedSideWalk = "";
    private String mApiEndPoint = ApiEndPoint.LIVE_BASE_URL_OSM; //ApiEndPoint.SANDBOX_BASE_URL_OSM;
    private JSONObject mJSONObjectOSM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        if (Utility.isOnline(this)) {
            showLoader();
            callToGetChangeSet();
        }
        mISettingScreenPresenter = new SettingScreenPresenter(this, new UpdateWayManager());
        if (getIntent().hasExtra(AppConstant.WAY_DATA)) {
            mIsForWAY = getIntent().getBooleanExtra(AppConstant.IS_FOR_WAY, false);
            if (mIsForWAY) {
                mListWayData = getIntent().getParcelableExtra(AppConstant.WAY_DATA);
                mListDataSEND = mListWayData;
                if (mListWayData != null) {
                    mWayID = mListWayData.getOSMWayId();
                    mIsValidScreen = Boolean.parseBoolean(mListWayData.getIsValid());
                    if (mListWayData.getIsForData() != null) {
                        mISFromOSM = !mListWayData.getIsForData().isEmpty() && mListWayData.getIsForData().equalsIgnoreCase(AppConstant.OSM_DATA);
                    }

                    if (mISFromOSM) {
                        for (int i = 0; i < mListWayData.getAttributesList().size(); i++) {
                            switch (mListWayData.getAttributesList().get(i).getKey()) {
                                case AppConstant.KEY_SIDEWALK:
                                    switch (mListWayData.getAttributesList().get(i).getValue()) {
                                        case "left":
                                            mStringChoosedSideWalk = "left";
                                            mTxvSideWalk.setText(getResources().getString(R.string.left));
                                            break;
                                        case "right":
                                            mStringChoosedSideWalk = "right";
                                            mTxvSideWalk.setText(getResources().getString(R.string.right));
                                            break;
                                        case "both":
                                            mStringChoosedSideWalk = "both";
                                            mTxvSideWalk.setText(getResources().getString(R.string.both));
                                            break;
                                    }
                            }
                        }
                        if (!mStringChoosedSideWalk.isEmpty()) {
                            mTxvSideWalk.setVisibility(View.VISIBLE);

                        } else {
                            mTxvSideWalk.setVisibility(View.GONE);
                        }
                    } else {
                        mTxvSideWalk.setVisibility(View.GONE);
                    }

                    getDataFromWay();
                    setUpRecyclerView();
                }
            } else {
                mNodeReference = getIntent().getParcelableExtra(AppConstant.WAY_DATA);
                mNodeRefSEND = mNodeReference;
                if (mNodeReference != null) {
                    isValidFORCall = mNodeReference.getAttributes().get(0).isValid();
                    mNodeID = mNodeReference.getOSMNodeId();
                    if (mNodeReference.getIsForData() != null) {
                        mISFromOSM = !mNodeReference.getIsForData().isEmpty() && mNodeReference.getIsForData().equalsIgnoreCase(AppConstant.OSM_DATA);
                    }

                    mTxvSideWalk.setVisibility(View.GONE);
                    getDataFromWay();
                    setUpRecyclerView();

                }
            }
        }
    }

    /**
     * Api Call To CREATE CHANGE SET
     */
    private void callToGetChangeSet() {
        showLoader();
        String versionName = BuildConfig.VERSION_NAME;
        String date = AppConstant.Date;

        String string = "<osm><changeset><tag k=\"created_by\" v=\" Barrierefrei Projekt\"/><tag k=\"comment\" v=\"Android:" + versionName + date + "\"/></changeset></osm>";
        String URLChangeSet = mApiEndPoint + "changeset/create";
        OauthData oauthData = new OauthData(Verb.PUT, string, URLChangeSet);
        asyncTaskOsmApi = new AsyncTaskOsmApi(SettingActivity.this, oauthData, this,
                false, AppConstant.API_TYPE_CREATE_CHANGE_SET, false);
        asyncTaskOsmApi.execute("");
        Log.e("API", URLChangeSet);
    }

    /**
     * Api Call To GET WAY VERSION NUMBER
     */
    private void callToGetVersions() {
        if (mIsForWAY) {
            GetWayVersion();
        } else {
            GetNodeVersion();
        }

    }

    private void GetNodeVersion() {
        showLoader();
        String URLNodeGet = mApiEndPoint + "node/" + mNodeID;
        OauthData oauthData = new OauthData(Verb.GET, "", URLNodeGet);
        asyncTaskOsmApi = new AsyncTaskOsmApi(SettingActivity.this, oauthData,
                this, true, "", false);
        asyncTaskOsmApi.execute("");
        Log.e("API", URLNodeGet);
    }

    private void GetWayVersion() {
        showLoader();
        String URLWayGet = mApiEndPoint + "way/" + mWayID;
        OauthData oauthData = new OauthData(Verb.GET, "", URLWayGet);
        asyncTaskOsmApi = new AsyncTaskOsmApi(SettingActivity.this, oauthData, this,
                true, "", false);
        asyncTaskOsmApi.execute("");
        Log.e("API_Get_Way", URLWayGet);

    }


    /**
     * Api Call To UPDATE WAY DATA ON OSM SERVER
     */
    private void callToUpdateWayDataOnOSMServer() {
        if (mIsForWAY) {
            try {
                onUpdateWAYonOSMServer();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            onUpdateNODEonOSMServer();
        }
    }

    private void onUpdateNODEonOSMServer() {
        showLoader();
        StringBuilder tags = new StringBuilder();
        for (Map.Entry<Integer, Attributes> pair : mHashMapWay.entrySet()) {
            Attributes attributes = pair.getValue();
            if (attributes != null && attributes.getValue() != null) {
                if (!mISFromOSM) {
                    if (attributes.isValid()) {
                        tags.append("<tag k=\"" + attributes.getKey() + "\" v=\"" + Utility.covertValueRequiredWhenSend(this, attributes.getKey(), attributes.getValue()) + "\"/>\n");

                    }
                } else {
                    tags.append("<tag k=\"" + attributes.getKey() + "\" v=\"" + Utility.covertValueRequiredWhenSend(this, attributes.getKey(), attributes.getValue()) + "\"/>\n");
                    for (int i = 0; i < mNodeRefSEND.getAttributes().size(); i++) {
                        mNodeRefSEND.getAttributes().get(i).setValue(Utility.covertValueRequiredWhenSend(this, attributes.getKey(), attributes.getValue()));
                    }
                }
            }
        }

        mNodeRefSEND.setVersion(mVersionNumber);
        StringBuilder nodes = new StringBuilder();
        if (mNodeReference.getOSMNodeId() != null && !mNodeReference.getOSMNodeId().isEmpty()) {
            nodes.append("<nd ref=\"" + mNodeReference.getOSMNodeId() + "\"/>\n");
        }
        String requestString = "<osm>\n" +
                " <node  id=\"" + mNodeID + "\" changeset=\"" + mChangeSetID + "\" version=\"" + mVersionNumber + "\"  lat=\"" + mNodeReference.getLat() + "\" lon=\"" + mNodeReference.getLon() + "\" >\n" +
                nodes +
                tags +
                " </node>\n" +
                "</osm>";

        String URLNodePUT = mApiEndPoint + "node/" + mNodeID;
        OauthData oauthData = new OauthData(Verb.PUT, requestString, URLNodePUT);
        asyncTaskOsmApi = new AsyncTaskOsmApi(SettingActivity.this, oauthData,
                this, false, AppConstant.API_TYPE_UPDATE_WAY_OR_NODE, false);
        asyncTaskOsmApi.execute("");
        Log.e("API_node_put_osm", URLNodePUT);

    }

    private void onUpdateWAYonOSMServer() throws JSONException {
        showLoader();
        StringBuilder tags = new StringBuilder();
        if (!mISFromOSM) {
            for (Map.Entry<Integer, Attributes> pair : mHashMapWay.entrySet()) {
                Attributes attributes = pair.getValue();
                assert attributes != null;
                if (attributes.getValue() != null && !attributes.getValue().isEmpty()) {
                    if (attributes.isValid()) {
                        tags.append("<tag k=\"" + attributes.getKey() + "\" v=\"" + Utility.covertValueRequiredWhenSend(this, attributes.getKey(), attributes.getValue()) + "\"/>\n");
                    }

                }
            }
        } else {
            HashMap<String, String> hashMapTags = new HashMap<>();
            List<Attributes> attributesListSend = new ArrayList<>();
            for (int i = 0; i < mListWayData.getAttributesList().size(); i++) {
                hashMapTags.put(mListWayData.getAttributesList().get(i).getKey(),
                        mListWayData.getAttributesList().get(i).getValue());
            }

            for (Map.Entry<Integer, Attributes> pair : mHashMapWay.entrySet()) {
                Attributes attributes = pair.getValue();
                if (attributes != null && attributes.getValue() != null) {
                    if (mISFromOSM) {
                        if (!mStringChoosedSideWalk.isEmpty()) {
                            if (attributes.getKey().equalsIgnoreCase(AppConstant.KEY_SIDEWALK + ":" + mStringChoosedSideWalk + ":" + AppConstant.KEY_SURFACE) && !attributes.getValue().isEmpty()) {
                                hashMapTags.put(AppConstant.KEY_SIDEWALK + ":" + mStringChoosedSideWalk + ":" + AppConstant.KEY_SURFACE,
                                        Utility.covertValueRequiredWhenSend(this, attributes.getKey(), attributes.getValue()));
                            } else {
                                if (attributes.getKey().equalsIgnoreCase(AppConstant.KEY_SURFACE) && !attributes.getValue().isEmpty()) {
                                    hashMapTags.put(AppConstant.KEY_SIDEWALK + ":" + mStringChoosedSideWalk + ":" + AppConstant.KEY_SURFACE,
                                            Utility.covertValueRequiredWhenSend(this, attributes.getKey(), attributes.getValue()));
                                }
                            }
                            if (attributes.getKey().equalsIgnoreCase(AppConstant.KEY_SIDEWALK + ":" + mStringChoosedSideWalk + ":" + AppConstant.KEY_WIDTH) && !attributes.getValue().isEmpty()) {
                                hashMapTags.put(AppConstant.KEY_SIDEWALK + ":" + mStringChoosedSideWalk + ":" + AppConstant.KEY_WIDTH,
                                        Utility.covertValueRequiredWhenSend(this, attributes.getKey(), attributes.getValue()));
                            } else {
                                if (attributes.getKey().equalsIgnoreCase(AppConstant.KEY_WIDTH) && !attributes.getValue().isEmpty()) {
                                    hashMapTags.put(AppConstant.KEY_SIDEWALK + ":" + mStringChoosedSideWalk + ":" + AppConstant.KEY_WIDTH,
                                            Utility.covertValueRequiredWhenSend(this, attributes.getKey(), attributes.getValue()));
                                }
                            }
                            if (attributes.getKey().equalsIgnoreCase(AppConstant.KEY_INCLINE) && !attributes.getValue().isEmpty()) {
                                hashMapTags.put(AppConstant.KEY_INCLINE,
                                        Utility.covertValueRequiredWhenSend(this, attributes.getKey(), attributes.getValue()));

                            } else {
                                if (attributes.getKey().equalsIgnoreCase(AppConstant.KEY_INCLINE) && !attributes.getValue().isEmpty()) {
                                    hashMapTags.put(AppConstant.KEY_INCLINE,
                                            Utility.covertValueRequiredWhenSend(this, attributes.getKey(), attributes.getValue()));

                                }
                            }
                        } else {
                            if (attributes.getKey().equalsIgnoreCase(AppConstant.KEY_SURFACE) && !attributes.getValue().isEmpty()) {
                                hashMapTags.put(AppConstant.KEY_SURFACE,
                                        Utility.covertValueRequiredWhenSend(this, attributes.getKey(), attributes.getValue()));
                            }
                            if (attributes.getKey().equalsIgnoreCase(AppConstant.KEY_INCLINE) && !attributes.getValue().isEmpty()) {
                                hashMapTags.put(AppConstant.KEY_INCLINE,
                                        Utility.covertValueRequiredWhenSend(this, attributes.getKey(), attributes.getValue()));

                            }
                            if (attributes.getKey().equalsIgnoreCase(AppConstant.KEY_WIDTH) && !attributes.getValue().isEmpty()) {
                                hashMapTags.put(AppConstant.KEY_WIDTH,
                                        Utility.covertValueRequiredWhenSend(this, attributes.getKey(), attributes.getValue()));
                            }
                        }
                    } else {
                        if (!mStringChoosedSideWalk.isEmpty()) {
                            if (attributes.getKey().equalsIgnoreCase(AppConstant.KEY_SURFACE) && !attributes.getValue().isEmpty()) {
                                hashMapTags.put(AppConstant.KEY_SIDEWALK + ":" + mStringChoosedSideWalk + ":" + AppConstant.KEY_SURFACE,
                                        attributes.getValue());
                            }
                            if (attributes.getKey().equalsIgnoreCase(AppConstant.KEY_INCLINE) && !attributes.getValue().isEmpty()) {
                                hashMapTags.put(AppConstant.KEY_INCLINE,
                                        attributes.getValue());

                            }
                            if (attributes.getKey().equalsIgnoreCase(AppConstant.KEY_WIDTH) && !attributes.getValue().isEmpty()) {
                                hashMapTags.put(AppConstant.KEY_SIDEWALK + ":" + mStringChoosedSideWalk + ":" + AppConstant.KEY_WIDTH,
                                        attributes.getValue());
                            }
                        } else {
                            if (attributes.getKey().equalsIgnoreCase(AppConstant.KEY_SURFACE) && !attributes.getValue().isEmpty()) {
                                hashMapTags.put(AppConstant.KEY_SURFACE,
                                        Utility.covertValueRequiredWhenSend(this, attributes.getKey(), attributes.getValue()));
                            }
                            if (attributes.getKey().equalsIgnoreCase(AppConstant.KEY_INCLINE) && !attributes.getValue().isEmpty()) {
                                hashMapTags.put(AppConstant.KEY_INCLINE,
                                        Utility.covertValueRequiredWhenSend(this, attributes.getKey(), attributes.getValue()));

                            }
                            if (attributes.getKey().equalsIgnoreCase(AppConstant.KEY_WIDTH) && !attributes.getValue().isEmpty()) {
                                hashMapTags.put(AppConstant.KEY_WIDTH,
                                        Utility.covertValueRequiredWhenSend(this, attributes.getKey(), attributes.getValue()));
                            }
                        }
                    }


                }
            }

            for (Map.Entry<String, String> pair : hashMapTags.entrySet()) {
                tags.append("<tag k=\"" + pair.getKey() + "\" v=\"" + pair.getValue() + "\"/>\n");

                Attributes attributes = new Attributes();
                attributes.setKey(pair.getKey());
                attributes.setValue(pair.getValue());
                attributesListSend.add(attributes);

            }


            if (mISFromOSM) {
                mListDataSEND.setAttributesList(attributesListSend);
                mListDataSEND.setVersion(mVersionNumber);

            }

        }

        StringBuilder nodes = new StringBuilder();
        if (mJSONObjectOSM != null && mJSONObjectOSM.getJSONObject("way") != null &&
                mJSONObjectOSM.getJSONObject("way").getJSONArray("nd") != null
                && mJSONObjectOSM.getJSONObject("way").getJSONArray("nd").length() != 0) {
            for (int i = 0; i < mJSONObjectOSM.getJSONObject("way").getJSONArray("nd").length(); i++) {

                String ref = mJSONObjectOSM.getJSONObject("way").getJSONArray("nd")
                        .getJSONObject(i).getString("ref");
                nodes.append("<nd ref=\"" + ref + "\"/>\n");
            }
        }

        String requestString = "<osm>\n" +
                " <way  id=\"" + mWayID + "\" changeset=\"" + mChangeSetID + "\" version=\"" + mVersionNumber + "\" >\n" +
                nodes +
                tags +
                " </way>\n" +
                "</osm>";
        String URLWayPUT = mApiEndPoint + "way/" + mWayID;
        OauthData oauthData = new OauthData(Verb.PUT, requestString, URLWayPUT);
        asyncTaskOsmApi = new AsyncTaskOsmApi(SettingActivity.this, oauthData,
                this, false, AppConstant.API_TYPE_UPDATE_WAY_OR_NODE, false);
        asyncTaskOsmApi.execute("");
        Log.e("API_way_put_osm", URLWayPUT);

    }

    /**
     * Setup recycler view
     */
    private void setUpRecyclerView() {
        if (mIsForWAY) {
            mSettingAdapter = new SettingAdapter(this, prepareListDataWay(), this, mISFromOSM, mIsValidScreen);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.setAdapter(mSettingAdapter);
            if (mHashMapWay != null) {
                mSettingAdapter.setSelectionMap(mHashMapWay, false);
                mSettingAdapter.notifyDataSetChanged();

            }
        } else {
            mSettingAdapter = new SettingAdapter(this, prepareListDataNode(), this, mISFromOSM, false);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.setAdapter(mSettingAdapter);
            if (mHashMapWay != null) {
                mSettingAdapter.setSelectionMap(mHashMapWay, false);
                mSettingAdapter.notifyDataSetChanged();

            }
        }
    }

    /**
     * Prepare items of attributes
     *
     * @return list
     */
    private ArrayList<SettingModel> prepareListDataWay() {
        ArrayList<SettingModel> modelArrayList = new ArrayList<>();
        SettingModel settingModel;
        settingModel = new SettingModel(0, getString(R.string.surface_type));
        modelArrayList.add(settingModel);
        settingModel = new SettingModel(2, getString(R.string.maximum_incline));
        modelArrayList.add(settingModel);
        settingModel = new SettingModel(3, getString(R.string.sidewalk_width_m));
        modelArrayList.add(settingModel);
        return modelArrayList;
    }

    private ArrayList<SettingModel> prepareListDataNode() {
        ArrayList<SettingModel> modelArrayList = new ArrayList<>();
        SettingModel settingModel;
        settingModel = new SettingModel(0, getString(R.string.maximum_sloped_kerb_m));
        modelArrayList.add(settingModel);
        return modelArrayList;

    }

    @OnClick(R.id.img_back)
    public void onBackClick() {
        finish();
    }

    @OnClick(R.id.btn_finish)
    public void onFinishClick() {
        showLoader();
        if (mIsForWAY) {
            if (mListWayData != null) {
                boolean isValid = Boolean.parseBoolean(mListWayData.getIsValid());
                if (!isValid) {
                    if (Utility.isOnline(this)) {
                        if (mChangeSetID != null) {
                            mRelativeLayoutProgress.setVisibility(View.VISIBLE);
                            if(!mISFromOSM){
                                if(CheckAllValues()){
                                    if (!mListWayData.getOSMWayId().isEmpty()) {
                                        callToGetVersions();
                                    } else {
                                        mWayIdAvailable = false;
                                        checkNodes();
                                    }

                                }else {
                                    hideLoader();
                                    Toast.makeText(this,R.string.message_to_proceed,Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                if (!mListWayData.getOSMWayId().isEmpty()) {
                                    callToGetVersions();
                                } else {
                                    mWayIdAvailable = false;
                                    checkNodes();
                                }
                            }


                        }
                    }
                } else {
                    hideLoader();
                    finish();
                }
            }
        } else {
            if (!isValidFORCall) {
                if (Utility.isOnline(this)) {
                    if (mChangeSetID != null) {
                        mRelativeLayoutProgress.setVisibility(View.VISIBLE);
                        if(!mISFromOSM){
                            if(CheckAllValues()){
                                if (!mNodeReference.getOSMNodeId().isEmpty()) {
                                    callToGetVersions();
                                } else {
                                    mWayIdAvailable = false;
                                    callToCreateNode(mNodeReference);
                                }

                            }else {
                                hideLoader();
                                Toast.makeText(this,R.string.message_to_proceed,Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            if (!mNodeReference.getOSMNodeId().isEmpty()) {
                                callToGetVersions();
                            } else {
                                mWayIdAvailable = false;
                                callToCreateNode(mNodeReference);
                            }
                        }

                    }
                }
            } else {
                hideLoader();
                finish();
            }
        }

    }

    public boolean CheckAllValues() {
        boolean shouldProceed = false;
        if(mIsForWAY) {
            boolean checkSurface = false;
            boolean checkIncline = false;
            boolean checkWidth = false;
            if (mHashMapWay != null && mHashMapWay.size() != 0) {
                for (Map.Entry<Integer, Attributes> pair : mHashMapWay.entrySet()) {
                    Attributes attributes = pair.getValue();
                    assert attributes != null;
                    if (attributes.getKey() != null && !attributes.getKey().isEmpty()) {
                        if (attributes.getKey().equalsIgnoreCase(AppConstant.KEY_SURFACE)) {
                            checkSurface = attributes.getValue() != null && !attributes.getValue().isEmpty() && attributes.isValid();
                        }
                        if (attributes.getKey().equalsIgnoreCase(AppConstant.KEY_INCLINE)) {
                            checkIncline = attributes.getValue() != null && !attributes.getValue().isEmpty() && attributes.isValid();
                        }
                        if (attributes.getKey().equalsIgnoreCase(AppConstant.KEY_WIDTH)) {
                            checkWidth = attributes.getValue() != null && !attributes.getValue().isEmpty() && attributes.isValid();
                        }
                    }

                }

                shouldProceed = checkSurface && checkIncline && checkWidth;
            }
        }
        else {
            boolean checkKerbHeight = false;
            if (mHashMapWay != null && mHashMapWay.size() != 0) {
                for (Map.Entry<Integer, Attributes> pair : mHashMapWay.entrySet()) {
                    Attributes attributes = pair.getValue();
                    assert attributes != null;
                    if (attributes.getKey() != null && !attributes.getKey().isEmpty()) {
                        if (attributes.getKey().equalsIgnoreCase(AppConstant.KEY_KERB_HEIGHT)) {
                            checkKerbHeight = attributes.getValue() != null && !attributes.getValue().isEmpty() && attributes.isValid();
                        }

                    }

                }
                shouldProceed = checkKerbHeight;
            }

        }
        return shouldProceed;
    }

    /**
     * Api Call To CREATE NODE
     */
    private void callToCreateNode(NodeReference nodeReference) {
        showLoader();
        double lat = 0;
        double lon = 0;
        if (!nodeReference.getLat().isEmpty() && !nodeReference.getLon().isEmpty()) {
            lat = Double.parseDouble(nodeReference.getLat());
            lon = Double.parseDouble(nodeReference.getLon());
        }
        String requestString;
        if (nodeReference.getAttributes() != null && nodeReference.getAttributes().size() != 0) {
            //Case When node attributes are there
            StringBuilder tags = new StringBuilder();
            tags.append("<tag k=\"" + nodeReference.getAttributes().get(0).getKey() + "\" v=\"" +
                    Utility.covertValueRequiredWhenSend(this, nodeReference.getAttributes().get(0).getKey(), nodeReference.getAttributes().get(0).getValue()) + "\"/>\n");

            requestString = "<osm>\n" +
                    " <node changeset=\"" + mChangeSetID + "\" lat=\"" + lat + "\" lon=\"" + lon + "\" >\n" +
                    tags +
                    " </node>\n" +
                    "</osm>";

        } else {
            //Case When no node attributes are there
            requestString = "<osm>\n" +
                    " <node changeset=\"" + mChangeSetID + "\" lat=\"" + lat + "\" lon=\"" + lon + "\" >\n" +
                    " </node>\n" +
                    "</osm>";
        }

        String URLCreateNode = mApiEndPoint + "node/create";
        Log.e("API", URLCreateNode);
        OauthData oauthData = new OauthData(Verb.PUT, requestString, URLCreateNode);
        asyncTaskOsmApi = new AsyncTaskOsmApi(SettingActivity.this, oauthData, this,
                false, AppConstant.API_TYPE_CREATE_NODE, false);
        try {
            Object result = asyncTaskOsmApi.execute().get();
            //asyncTaskOsmApi.execute("");

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    boolean mIsCalledCreateWay = false;

    /**
     * Api Call To CREATE WAY
     */
    private void callToCreateWay() {
        showLoader();
        String requestString;
        StringBuilder tags = new StringBuilder();
        HashMap<String, String> hashMapTags = new HashMap<>();

        for (Map.Entry<Integer, Attributes> pair : mHashMapWay.entrySet()) {
            Attributes attributes = pair.getValue();
            assert attributes != null;
            if (attributes.getValue() != null && !attributes.getValue().isEmpty()) {
                if (attributes.isValid()) {
                    hashMapTags.put(attributes.getKey(), Utility.covertValueRequiredWhenSend(this, attributes.getKey(), attributes.getValue()));
                }
            }
        }

        for (Map.Entry<Integer, Attributes> pair : mHashMapWay.entrySet()) {
            Attributes attributes = pair.getValue();
            if (attributes != null && attributes.getValue() != null) {
                if (attributes.getKey().equalsIgnoreCase(AppConstant.KEY_SURFACE) && !attributes.getValue().isEmpty() && attributes.isValid()) {
                    hashMapTags.put(AppConstant.KEY_SURFACE,
                            Utility.covertValueRequiredWhenSend(this, attributes.getKey(), attributes.getValue()));
                }
                if (attributes.getKey().equalsIgnoreCase(AppConstant.KEY_INCLINE) && !attributes.getValue().isEmpty() && attributes.isValid()) {
                    hashMapTags.put(AppConstant.KEY_INCLINE,
                            Utility.covertValueRequiredWhenSend(this, attributes.getKey(), attributes.getValue()));
                }
                if (attributes.getKey().equalsIgnoreCase(AppConstant.KEY_WIDTH) && !attributes.getValue().isEmpty() && attributes.isValid()) {
                    hashMapTags.put(AppConstant.KEY_WIDTH,
                            Utility.covertValueRequiredWhenSend(this, attributes.getKey(), attributes.getValue()));
                }

            }
        }

        for (Map.Entry<String, String> pair : hashMapTags.entrySet()) {
            tags.append("<tag k=\"" + pair.getKey() + "\" v=\"" + pair.getValue() + "\"/>\n");

        }

        StringBuilder nodes = new StringBuilder();
        for (int i = 0; i < mListWayData.getNodeReference().size(); i++) {
            if (mListWayData.getNodeReference().get(i).getOSMNodeId().isEmpty()) {
                String valueId = mNodeIdsCreated.get(i);
                nodes.append("<nd ref=\"" + valueId + "\"/>\n");

            } else {
                nodes.append("<nd ref=\"" + mListWayData.getNodeReference().get(i).getOSMNodeId() + "\"/>\n");
            }
        }

        requestString = "<osm>\n" +
                " <way changeset=\"" + mChangeSetID + "\">\n" +
                tags +
                nodes +
                " </way>\n" +
                "</osm>";


        String URLCreateWay = mApiEndPoint + "way/create";
        Log.e("API_way_create", URLCreateWay);

        OauthData oauthData = new OauthData(Verb.PUT, requestString, URLCreateWay);
        asyncTaskOsmApi = new AsyncTaskOsmApi(SettingActivity.this, oauthData, this,
                false, AppConstant.API_TYPE_CREATE_WAY, false);
        if (!mIsCalledCreateWay) {
            asyncTaskOsmApi.execute("");
            mIsCalledCreateWay = true;
        }

    }

    private void checkNodes() {
        if (mListWayData != null) {
            showLoader();
            if (mListWayData.getNodeReference() != null && mListWayData.getNodeReference().size() != 0) {
                for (int i = 0; i < mListWayData.getNodeReference().size(); i++) {
                    mNodeRefIndex = i;
                    boolean mCallForWay = false;
                    if (i == mListWayData.getNodeReference().size() - 1) {
                        mCallForWay = true;
                    }
                    if (mListWayData.getNodeReference().get(i).getOSMNodeId().isEmpty()) {
                        //Create Node
                        showLoader();
                        callToCreateNode(mListWayData.getNodeReference().get(i));
                    }
                    if (mCallForWay) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callToCreateWay();

                            }
                        });
                    }
                }

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_SETTING_TYPE) {
            if (resultCode == RESULT_OK) {
                String dataString = data.getStringExtra(AppConstant.SETTING_ITEM_SELECTED_RECIEVE);
                Attributes attributes = new Attributes();
                attributes.setKey(mHashMapWay.get(mPositionClicked).getKey());
                attributes.setValue(Utility.changeCommaToDot(dataString));
                //attributes.setValid(true);
                mHashMapWay.put(mPositionClicked, attributes);
                mSettingAdapter.setSelectionMap(mHashMapWay, true);
                mSettingAdapter.notifyDataSetChanged();
            }
        }
    }


    /**
     * Get Data from intent and set  values to attributes
     */
    private void getDataFromWay() {
        if (mIsForWAY) {
            Attributes attributesSurfaceType = new Attributes();
            attributesSurfaceType.setKey(AppConstant.KEY_SURFACE);
            mHashMapWay.put(0, attributesSurfaceType);

            Attributes attributesInclineType = new Attributes();
            attributesInclineType.setKey(AppConstant.KEY_INCLINE);
            mHashMapWay.put(2, attributesInclineType);

            Attributes attributesWidthType = new Attributes();
            attributesWidthType.setKey(AppConstant.KEY_WIDTH);
            mHashMapWay.put(3, attributesWidthType);


            for (int i = 0; i < mListWayData.getAttributesList().size(); i++)
                switch (mListWayData.getAttributesList().get(i).getKey()) {

                    case AppConstant.KEY_SURFACE:
                        if (!mISFromOSM) {
                            Attributes attributesSurface = new Attributes();
                            attributesSurface.setKey(mListWayData.getAttributesList().get(i).getKey());
                            attributesSurface.setValue(mListWayData.getAttributesList().get(i).getValue().trim());
                            attributesSurface.setValid(mListWayData.getAttributesList().get(i).isValid());
                            mHashMapWay.put(0, attributesSurface);
                        }
                        if (mISFromOSM && mStringChoosedSideWalk.isEmpty()) {
                            Attributes attributesSurface = new Attributes();
                            attributesSurface.setKey(mListWayData.getAttributesList().get(i).getKey());
                            attributesSurface.setValue(mListWayData.getAttributesList().get(i).getValue().trim());
                            attributesSurface.setValid(mListWayData.getAttributesList().get(i).isValid());
                            mHashMapWay.put(0, attributesSurface);
                        }
                        break;

                    case AppConstant.KEY_HIGHWAY:
                        Attributes attributesHighWay = new Attributes();
                        attributesHighWay.setKey(mListWayData.getAttributesList().get(i).getKey());
                        attributesHighWay.setValue(mListWayData.getAttributesList().get(i).getValue().trim());
                        attributesHighWay.setValid(true);
                        mHashMapWay.put(1, attributesHighWay);

                        break;


                    case AppConstant.KEY_INCLINE:
                        Attributes attributesIncline = new Attributes();
                        attributesIncline.setKey(mListWayData.getAttributesList().get(i).getKey());
                        String value;
                        if (mListWayData.getAttributesList().get(i).getValue() != null && mListWayData.getAttributesList().get(i).getValue().contains("&lt;")) {
                            value = mListWayData.getAttributesList().get(i).getValue().replace("&lt;", "<");
                        } else if (mListWayData.getAttributesList().get(i).getValue() != null && mListWayData.getAttributesList().get(i).getValue().contains("&Lt;")) {
                            value = mListWayData.getAttributesList().get(i).getValue().replace("&Lt;", "<");
                        }
                        else if (mListWayData.getAttributesList().get(i).getValue() != null && mListWayData.getAttributesList().get(i).getValue().contains("&Lt;")) {
                            value = mListWayData.getAttributesList().get(i).getValue().replace("&gt;", ">");
                        }
                        else if (mListWayData.getAttributesList().get(i).getValue() != null && mListWayData.getAttributesList().get(i).getValue().contains("&Lt;")) {
                            value = mListWayData.getAttributesList().get(i).getValue().replace("&Gt;", ">");
                        }
                        else {
                            value = mListWayData.getAttributesList().get(i).getValue();
                        }
                        attributesIncline.setValue(value.trim());
                        attributesIncline.setValid(mListWayData.getAttributesList().get(i).isValid());
                        mHashMapWay.put(2, attributesIncline);

                        break;

                    case AppConstant.KEY_WIDTH:
                        if (!mISFromOSM) {
                            Attributes attributesWidth = new Attributes();
                            attributesWidth.setKey(mListWayData.getAttributesList().get(i).getKey());
                            if (mListWayData.getAttributesList().get(i).getValue().contains(".")) {
                                String stringValue = Utility.trimTWoDecimalPlaces(Double.parseDouble(mListWayData.getAttributesList().get(i).getValue()));
                                attributesWidth.setValue(Utility.convertDToCORCtoD(stringValue).trim());

                            } else {
                                attributesWidth.setValue(mListWayData.getAttributesList().get(i).getValue());

                            }
                            attributesWidth.setValid(mListWayData.getAttributesList().get(i).isValid());
                            mHashMapWay.put(3, attributesWidth);
                        }

                        if (mISFromOSM && mStringChoosedSideWalk.isEmpty()) {
                            Attributes attributesWidth = new Attributes();
                            attributesWidth.setKey(mListWayData.getAttributesList().get(i).getKey());
                            if (mListWayData.getAttributesList().get(i).getValue().contains(".")) {
                                String stringValue = Utility.trimTWoDecimalPlaces(Double.parseDouble(mListWayData.getAttributesList().get(i).getValue()));
                                attributesWidth.setValue(Utility.convertDToCORCtoD(stringValue).trim());

                            } else {
                                attributesWidth.setValue(mListWayData.getAttributesList().get(i).getValue());

                            }
                            attributesWidth.setValid(mListWayData.getAttributesList().get(i).isValid());
                            mHashMapWay.put(3, attributesWidth);
                        }
                        break;

                    case AppConstant.KEY_FOOTWAY:
                        Attributes attributesFootWay = new Attributes();
                        attributesFootWay.setKey(mListWayData.getAttributesList().get(i).getKey());
                        attributesFootWay.setValue(mListWayData.getAttributesList().get(i).getValue().trim());
                        attributesFootWay.setValid(true);
                        mHashMapWay.put(4, attributesFootWay);
                        break;

                    case "sidewalk:left:surface":
                        if (mISFromOSM) {
                            Attributes attributesSurface = new Attributes();
                            attributesSurface.setKey(mListWayData.getAttributesList().get(i).getKey());
                            attributesSurface.setValue(mListWayData.getAttributesList().get(i).getValue().trim());
                            attributesSurface.setValid(mListWayData.getAttributesList().get(i).isValid());
                            mHashMapWay.put(0, attributesSurface);
                        }
                        break;
                    case "sidewalk:right:surface":
                        if (mISFromOSM) {
                            Attributes attributesSurface = new Attributes();
                            attributesSurface.setKey(mListWayData.getAttributesList().get(i).getKey());
                            attributesSurface.setValue(mListWayData.getAttributesList().get(i).getValue().trim());
                            attributesSurface.setValid(mListWayData.getAttributesList().get(i).isValid());
                            mHashMapWay.put(0, attributesSurface);
                        }
                        break;

                    case "sidewalk:both:surface":
                        if (mISFromOSM) {
                            Attributes attributesSurface = new Attributes();
                            attributesSurface.setKey(mListWayData.getAttributesList().get(i).getKey());
                            attributesSurface.setValue(mListWayData.getAttributesList().get(i).getValue().trim());
                            attributesSurface.setValid(mListWayData.getAttributesList().get(i).isValid());
                            mHashMapWay.put(0, attributesSurface);
                        }
                        break;

                    case "sidewalk:left:width":
                        if (mISFromOSM) {
                            Attributes attributesWidth = new Attributes();
                            attributesWidth.setKey(mListWayData.getAttributesList().get(i).getKey());
                            if (mListWayData.getAttributesList().get(i).getValue().contains(".")) {
                                String stringValue = Utility.trimTWoDecimalPlaces(Double.parseDouble(mListWayData.getAttributesList().get(i).getValue()));
                                attributesWidth.setValue(Utility.convertDToCORCtoD(stringValue).trim());

                            } else {
                                attributesWidth.setValue(mListWayData.getAttributesList().get(i).getValue());

                            }
                            attributesWidth.setValid(mListWayData.getAttributesList().get(i).isValid());
                            mHashMapWay.put(3, attributesWidth);
                        }

                        break;
                    case "sidewalk:right:width":
                        if (mISFromOSM) {
                            Attributes attributesWidth = new Attributes();
                            attributesWidth.setKey(mListWayData.getAttributesList().get(i).getKey());
                            if (mListWayData.getAttributesList().get(i).getValue().contains(".")) {
                                String stringValue = Utility.trimTWoDecimalPlaces(Double.parseDouble(mListWayData.getAttributesList().get(i).getValue()));
                                attributesWidth.setValue(Utility.convertDToCORCtoD(stringValue).trim());

                            } else {
                                attributesWidth.setValue(mListWayData.getAttributesList().get(i).getValue());

                            }
                            attributesWidth.setValid(mListWayData.getAttributesList().get(i).isValid());
                            mHashMapWay.put(3, attributesWidth);
                        }

                        break;
                    case "sidewalk:both:width":
                        if (mISFromOSM) {
                            Attributes attributesWidth = new Attributes();
                            attributesWidth.setKey(mListWayData.getAttributesList().get(i).getKey());
                            if (mListWayData.getAttributesList().get(i).getValue().contains(".")) {
                                String stringValue = Utility.trimTWoDecimalPlaces(Double.parseDouble(mListWayData.getAttributesList().get(i).getValue()));
                                attributesWidth.setValue(Utility.convertDToCORCtoD(stringValue).trim());

                            } else {
                                attributesWidth.setValue(mListWayData.getAttributesList().get(i).getValue());
                            }
                            attributesWidth.setValid(mListWayData.getAttributesList().get(i).isValid());
                            mHashMapWay.put(3, attributesWidth);
                        }

                        break;

                    default:
                        break;
                }
        } else {
            for (int i = 0; i < mNodeReference.getAttributes().size(); i++) {
                switch (mNodeReference.getAttributes().get(i).getKey()) {
                    case AppConstant.KEY_KERB_HEIGHT:
                        Attributes attributesKerb = new Attributes();
                        attributesKerb.setKey(mNodeReference.getAttributes().get(i).getKey());
                        attributesKerb.setValue(mNodeReference.getAttributes().get(i).getValue().trim());
                        attributesKerb.setValid(mNodeReference.getAttributes().get(i).isValid());
                        mHashMapWay.put(0, attributesKerb);
                        break;
                    default:
                }
            }

        }

    }

    private void onUpdateNode() {
        showLoader();
        boolean isNodeNeedToUpdate = false;
        if (mIsForWAY) {
            for (int i = 0; i < mListWayData.getNodeReference().size(); i++) {
                if (mListWayData.getNodeReference().get(i).getOSMNodeId().isEmpty()) {
                    isNodeNeedToUpdate = true;
                    RequestNodeInfo requestNodeInfo = new RequestNodeInfo();
                    NodeReference nodeReference = new NodeReference();
                    String nodeOSMID = mNodeIdsCreated.get(i);
                    nodeReference.setOSMNodeId(nodeOSMID);
                    nodeReference.setAPINodeId(mListWayData.getNodeReference().get(i).getAPINodeId());
                    nodeReference.setLat(mListWayData.getNodeReference().get(i).getLat());
                    nodeReference.setLon(mListWayData.getNodeReference().get(i).getLon());
                    nodeReference.setVersion(mListWayData.getNodeReference().get(i).getVersion());

                    mListDataSEND.getNodeReference().get(i).setOSMNodeId(nodeOSMID);

                    List<Attributes> attributesValidateList = new ArrayList<>();
                    Attributes attributesValidate = new Attributes();
                    if (mListWayData.getNodeReference().get(i).getAttributes() != null &&
                            mListWayData.getNodeReference().get(i).getAttributes().size() != 0) {
                        attributesValidate.setKey(mListWayData.getNodeReference().get(i).getAttributes().get(0).getKey());
                        attributesValidate.setValue(Utility.covertValueRequiredWhenSend(this,
                                mListWayData.getNodeReference().get(i).getAttributes().get(0).getKey(),
                                mListWayData.getNodeReference().get(i).getAttributes().get(0).getValue()));
                        attributesValidate.setValid(mListWayData.getNodeReference().get(i).getAttributes().get(0).isValid());
                    }
                    if (mListWayData.getNodeReference().get(i).getAttributes().size() != 0) {
                        attributesValidateList.add(attributesValidate);
                        nodeReference.setAttributes(attributesValidateList);
                    }
                    requestNodeInfo.setNodeReference(nodeReference);
                    if (UserPreferences.getInstance(this) != null && UserPreferences.getInstance(this).getUserDetail() != null) {
                        requestNodeInfo.setModifiedByUser(UserPreferences.getInstance(this).getUserDetail());
                    }
                    mISettingScreenPresenter.onUpdateNode(requestNodeInfo, AppConstant.NODE_UPDATE);
                }

            }
            if (!isNodeNeedToUpdate) {
                hideLoader();
                onUpdateWayOurServer(mListWayData.getVersion());
            }

        } else {
            RequestNodeInfo requestNodeInfo = new RequestNodeInfo();
            NodeReference nodeReference = new NodeReference();
            String nodeOSMID = mNodeIdsCreated.get(0);
            nodeReference.setOSMNodeId(nodeOSMID);
            nodeReference.setAPINodeId(mNodeReference.getAPINodeId());
            nodeReference.setLat(mNodeReference.getLat());
            nodeReference.setLon(mNodeReference.getLon());
            nodeReference.setVersion(mNodeReference.getVersion());
            List<Attributes> attributesValidateList = new ArrayList<>();
            Attributes attributesValidate = new Attributes();
            if (mNodeReference.getAttributes() != null &&
                    mNodeReference.getAttributes().size() != 0) {
                attributesValidate.setKey(mHashMapWay.get(0).getKey());
                if (mHashMapWay.get(0).getValue() != null && !mHashMapWay.get(0).getValue().isEmpty()) {
                    attributesValidate.setValue(Utility.covertValueRequiredWhenSend(this, mHashMapWay.get(0).getKey(), mHashMapWay.get(0).getValue()));
                }
                attributesValidate.setValid(mHashMapWay.get(0).isValid());
            }
            assert mNodeReference.getAttributes() != null;
            if (mNodeReference.getAttributes().size() != 0) {
                attributesValidateList.add(attributesValidate);
                nodeReference.setAttributes(attributesValidateList);
            }
            requestNodeInfo.setNodeReference(nodeReference);
            if (UserPreferences.getInstance(this) != null && UserPreferences.getInstance(this).getUserDetail() != null) {
                requestNodeInfo.setModifiedByUser(UserPreferences.getInstance(this).getUserDetail());
            }
            mISettingScreenPresenter.onUpdateNode(requestNodeInfo, AppConstant.NODE_UPDATE);
        }
    }

    /**
     * API CALL FOR UPDATE DATA
     *
     * @param versionString updated version string
     */
    private void onUpdateWay(String versionString) {
        if (mIsForWAY) {
            onUpdateWayOurServer(versionString);
        } else {
            onUpdateNodeOurServer(versionString);
        }
    }

    private void onUpdateWayOurServer(String updateVersionNumber) {
        showLoader();
        RequestWayInfo requestWayInfo = new RequestWayInfo();
        boolean isValidEntireWay = false;
        int count = 0;
        RequestWayData wayDataValidate = new RequestWayData();
        if (mWayIdNew != null) {
            wayDataValidate.setOSMWayId(mWayIdNew);
            mListDataSEND.setOSMWayId(mWayIdNew);

        } else {
            wayDataValidate.setOSMWayId(mListWayData.getOSMWayId());
            mListDataSEND.setOSMWayId(mListWayData.getOSMWayId());

        }
        wayDataValidate.setAPIWayId(mListWayData.getAPIWayId());
        mListDataSEND.setAPIWayId(mListWayData.getAPIWayId());

        wayDataValidate.setProjectId(mListWayData.getProjectId());
        mListDataSEND.setProjectId(mListWayData.getProjectId());


        for (Map.Entry<Integer, Attributes> pair : mHashMapWay.entrySet()) {
            Attributes attributes = pair.getValue();
            if (attributes != null && attributes.getValue() != null && !attributes.getValue().isEmpty()) {
                if (!attributes.getKey().equalsIgnoreCase(AppConstant.KEY_FOOTWAY) &&
                        !attributes.getKey().equalsIgnoreCase(AppConstant.KEY_HIGHWAY)) {
                    if (!attributes.isValid()) {
                        count = count + 1;
                    }
                }
            }

        }
        if (count == 0) {
            isValidEntireWay = true;
        }
        wayDataValidate.setValid(String.valueOf(isValidEntireWay));
        mListDataSEND.setIsValid(String.valueOf(isValidEntireWay));

        wayDataValidate.setVersion(updateVersionNumber);
        List<AttributesValidate> attributesValidateList = new ArrayList<>();
        List<Attributes> attributesList = new ArrayList<>();
        AttributesValidate attributesValidate;
        Attributes attributes;
        if (mHashMapWay.get(0) != null && !mHashMapWay.get(0).getKey().isEmpty() && mHashMapWay.get(0).getValue() != null && !mHashMapWay.get(0).getValue().isEmpty()) {
            attributesValidate = new AttributesValidate();
            attributesValidate.setKey(AppConstant.KEY_SURFACE);
            attributesValidate.setValue(mHashMapWay.get(0).getValue());
            attributesValidate.setValid(mHashMapWay.get(0).isValid());
            attributesValidateList.add(attributesValidate);

            attributes = new Attributes();
            attributes.setKey(AppConstant.KEY_SURFACE);
            attributes.setValue(mHashMapWay.get(0).getValue());
            attributes.setValid(mHashMapWay.get(0).isValid());
            attributesList.add(attributes);
        }
        if (mHashMapWay.get(1) != null && !mHashMapWay.get(1).getKey().isEmpty() && mHashMapWay.get(1).getValue() != null && !mHashMapWay.get(1).getValue().isEmpty()) {
            attributesValidate = new AttributesValidate();
            attributesValidate.setKey(AppConstant.KEY_HIGHWAY);
            attributesValidate.setValue(mHashMapWay.get(1).getValue());
            attributesValidate.setValid(true);
            attributesValidateList.add(attributesValidate);

            attributes = new Attributes();
            attributes.setKey(AppConstant.KEY_HIGHWAY);
            attributes.setValue(mHashMapWay.get(1).getValue());
            attributes.setValid(true);
            attributesList.add(attributes);
        }
        if (mHashMapWay.get(2) != null && !mHashMapWay.get(2).getKey().isEmpty() && mHashMapWay.get(2).getValue() != null && !mHashMapWay.get(2).getValue().isEmpty()) {
            attributesValidate = new AttributesValidate();
            attributesValidate.setKey(AppConstant.KEY_INCLINE);
            attributesValidate.setValue(mHashMapWay.get(2).getValue());
            attributesValidate.setValid(mHashMapWay.get(2).isValid());
            attributesValidateList.add(attributesValidate);

            attributes = new Attributes();
            attributes.setKey(AppConstant.KEY_INCLINE);
            attributes.setValue(mHashMapWay.get(2).getValue());
            attributes.setValid(mHashMapWay.get(2).isValid());
            attributesList.add(attributes);
        }
        if (mHashMapWay.get(3) != null && !mHashMapWay.get(3).getKey().isEmpty() && mHashMapWay.get(3).getValue() != null && !mHashMapWay.get(3).getValue().isEmpty()) {
            attributesValidate = new AttributesValidate();
            attributesValidate.setKey(AppConstant.KEY_WIDTH);
            attributesValidate.setValue(Utility.covertValueRequiredWhenSend(this, mHashMapWay.get(3).getKey(), mHashMapWay.get(3).getValue()));
            attributesValidate.setValid(mHashMapWay.get(3).isValid());
            attributesValidateList.add(attributesValidate);

            attributes = new Attributes();
            attributes.setKey(AppConstant.KEY_WIDTH);
            attributes.setValue(Utility.covertValueRequiredWhenSend(this, mHashMapWay.get(3).getKey(), mHashMapWay.get(3).getValue()));
            attributes.setValid(mHashMapWay.get(3).isValid());
            attributesList.add(attributes);
        }

        if (mHashMapWay.get(4) != null && !mHashMapWay.get(4).getKey().isEmpty() && mHashMapWay.get(4).getValue() != null && !mHashMapWay.get(4).getValue().isEmpty()) {
            attributesValidate = new AttributesValidate();
            attributesValidate.setKey(AppConstant.KEY_FOOTWAY);
            attributesValidate.setValue(Utility.covertValueRequiredWhenSend(this, mHashMapWay.get(4).getKey(), mHashMapWay.get(4).getValue()));
            attributesValidate.setValid(true);
            attributesValidateList.add(attributesValidate);

            attributes = new Attributes();
            attributes.setKey(AppConstant.KEY_FOOTWAY);
            attributes.setValue(Utility.covertValueRequiredWhenSend(this, mHashMapWay.get(4).getKey(), mHashMapWay.get(4).getValue()));
            attributes.setValid(true);
            attributesList.add(attributes);
        }
        wayDataValidate.setAttributesValidate(attributesValidateList);
        mListDataSEND.setAttributesList(attributesList);
        mListDataSEND.setColor(mListWayData.getColor());
        mListDataSEND.setVersion(mUpdateVersionNumber);

        requestWayInfo.setWayDataValidates(wayDataValidate);
        if (UserPreferences.getInstance(this) != null && UserPreferences.getInstance(this).getUserDetail() != null) {
            requestWayInfo.setModifiedByUser(UserPreferences.getInstance(this).getUserDetail());
        }

        mISettingScreenPresenter.onUpdateWay(requestWayInfo, AppConstant.WAY_UPDATE);
    }

    private void onUpdateNodeOurServer(String updateVersionNumber) {
        showLoader();
        RequestNodeInfo requestNodeInfo = new RequestNodeInfo();
        NodeReference nodeReference = new NodeReference();
        nodeReference.setOSMNodeId(mNodeReference.getOSMNodeId());
        nodeReference.setAPINodeId(mNodeID);
        nodeReference.setLat(mNodeReference.getLat());
        nodeReference.setLon(mNodeReference.getLon());
        nodeReference.setVersion(updateVersionNumber);

        mNodeRefSEND.setOSMNodeId(mNodeReference.getOSMNodeId());
        mNodeRefSEND.setAPINodeId(mNodeID);
        mNodeRefSEND.setLat(mNodeReference.getLat());
        mNodeRefSEND.setLon(mNodeReference.getLon());
        mNodeRefSEND.setVersion(updateVersionNumber);

        List<Attributes> attributesValidateList = new ArrayList<>();
        Attributes attributesValidate = new Attributes();
        if (mHashMapWay.get(0) != null && !mHashMapWay.get(0).getKey().isEmpty() && mHashMapWay.get(0).getValue() != null &&
                !mHashMapWay.get(0).getValue().isEmpty()) {
            attributesValidate.setKey(mHashMapWay.get(0).getKey());
            attributesValidate.setValue(Utility.covertValueRequiredWhenSend(this, mHashMapWay.get(0).getKey(),
                    mHashMapWay.get(0).getValue()));
            attributesValidate.setValid(mHashMapWay.get(0).isValid());
        }
        nodeReference.setAttributes(attributesValidateList);
        mNodeRefSEND.setAttributes(attributesValidateList);
        requestNodeInfo.setNodeReference(nodeReference);
        if (UserPreferences.getInstance(this) != null && UserPreferences.getInstance(this).getUserDetail() != null) {
            requestNodeInfo.setModifiedByUser(UserPreferences.getInstance(this).getUserDetail());
        }
        mISettingScreenPresenter.onUpdateNode(requestNodeInfo, AppConstant.NODE_UPDATE);
    }

    @Override
    public void onUpdateDataReceived(ResponseUpdate responseUpdate, String updateType) {
        if (mWayIdAvailable) {
            if (mIsForWAY) {
                if (responseUpdate.isStatus()) {
                    Toast.makeText(SettingActivity.this, R.string.updated_info, Toast.LENGTH_SHORT).show();
                } else {
                    if (responseUpdate.getError() != null && responseUpdate.getError().get(0) != null &&
                            responseUpdate.getError().get(0).getMessage() != null) {
                        Toast.makeText(this, responseUpdate.getError().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                setResult(RESULT_OK);
                hideLoader();
                finish();
            } else {
                if (responseUpdate.isStatus()) {
                    Toast.makeText(SettingActivity.this, R.string.updated_node_info, Toast.LENGTH_SHORT).show();
                } else {
                    if (responseUpdate.getError() != null && responseUpdate.getError().get(0) != null &&
                            responseUpdate.getError().get(0).getMessage() != null) {
                        Toast.makeText(this, responseUpdate.getError().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                setResult(RESULT_OK);
                hideLoader();
                finish();

            }
        } else {
            if (mIsForWAY) {
                if (responseUpdate.isStatus()) {
                    //mRelativeLayoutProgress.setVisibility(View.GONE);
                    if (updateType.equalsIgnoreCase(AppConstant.WAY_UPDATE)) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("DATA_WAY", mListDataSEND);
                        setResult(RESULT_OK, resultIntent);
                        Toast.makeText(SettingActivity.this, R.string.updated_info, Toast.LENGTH_SHORT).show();
                        finish();
                        hideLoader();
                    } else {
                        mNodeUpdate = mNodeUpdate + 1;
                        if (mNodeUpdate == mNodeIdsCreated.size()) {
                            Toast.makeText(SettingActivity.this, R.string.updated_node_info, Toast.LENGTH_SHORT).show();
                            onUpdateWayOurServer(mListWayData.getVersion());
                        }
                    }

                } else {
                    if (responseUpdate.getError() != null && responseUpdate.getError().get(0) != null &&
                            responseUpdate.getError().get(0).getMessage() != null) {
                        Toast.makeText(this, responseUpdate.getError().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    setResult(RESULT_OK);
                    finish();
                    hideLoader();

                }
            } else {
                if (responseUpdate.isStatus()) {
                    Toast.makeText(SettingActivity.this, R.string.updated_node_info, Toast.LENGTH_SHORT).show();

                } else {
                    if (responseUpdate.getError() != null && responseUpdate.getError().get(0) != null &&
                            responseUpdate.getError().get(0).getMessage() != null) {
                        Toast.makeText(this, responseUpdate.getError().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                Intent resultIntent = new Intent();
                resultIntent.putExtra("DATA_NODE", mNodeRefSEND);
                setResult(RESULT_OK, resultIntent);
                finish();
                hideLoader();

            }
        }
    }


    @Override
    public void onFailure(String error) {
        hideLoader();
        Toast.makeText(SettingActivity.this, getResources().getString(R.string.error_when_entry_not_saved), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoader() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {
                    mRelativeLayoutProgress.setVisibility(View.VISIBLE);
                    showProgress();
                }
            }
        });
    }

    @Override
    public void hideLoader() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                hideProgress();
                mRelativeLayoutProgress.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onSuccessAsyncTask(String responseBody, String API_TYPE) {
        if (responseBody != null) {
            hideLoader();
            if (API_TYPE.equalsIgnoreCase(AppConstant.API_TYPE_CREATE_CHANGE_SET)) {
                mChangeSetID = responseBody;
            }
            if (API_TYPE.equalsIgnoreCase(AppConstant.API_TYPE_UPDATE_WAY_OR_NODE)) {
                mUpdateVersionNumber = responseBody;
                this.runOnUiThread(new Runnable() {
                    public void run() {
                        if (mISFromOSM) {
                            hideLoader();
                            if (mIsForWAY) {
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("DATA_OSM_WAY", mListDataSEND);
                                setResult(RESULT_OK, resultIntent);
                                finish();
                                Toast.makeText(SettingActivity.this, getResources().getString(R.string.updated_info), Toast.LENGTH_SHORT).show();
                            } else {
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("DATA_OSM_NODE", mNodeReference);
                                setResult(RESULT_OK, resultIntent);
                                finish();
                                Toast.makeText(SettingActivity.this, getResources().getString(R.string.updated_node_info), Toast.LENGTH_SHORT).show();

                            }
                        } else {
                            onUpdateWay(mUpdateVersionNumber);
                        }
                    }
                });
            }
            if (API_TYPE.equalsIgnoreCase(AppConstant.API_TYPE_CREATE_NODE)) {
                Log.e("ResponseNode:", responseBody);
                showLoader();
                if (mIsForWAY) {
                    mNodeIdsCreated.put(mNodeRefIndex, responseBody);
                }
                if (!mIsForWAY) {
                    mNodeIdsCreated.put(0, responseBody);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onUpdateNode();
                        }
                    });
                }
            }
            if (API_TYPE.equalsIgnoreCase(AppConstant.API_TYPE_CREATE_WAY)) {
                mWayIdNew = responseBody;
                Log.e("ResponseWay:", responseBody);
                //Update Node on Our server first than Way
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onUpdateNode();
                    }
                });

            }
        }

    }

    @Override
    public void onFailureAsyncTask(final String errorBody) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                hideLoader();
                Toast.makeText(SettingActivity.this, getResources().getString(R.string.error_when_entry_not_saved), Toast.LENGTH_SHORT).show();

            }
        });

    }


    @Override
    public void onSuccessAsyncTaskForGetWay(String responseBody) {
        if (responseBody != null) {
            hideLoader();
            if (mIsForWAY) {
                JSONObject jsonObject = Utility.convertXMLtoJSON(responseBody);
                try {
                    JSONObject jsonObjectOSM = jsonObject.getJSONObject("osm");
                    JSONObject jsonObjectWAY = jsonObjectOSM.getJSONObject("way");
                    mVersionNumber = jsonObjectWAY.optString("version");
                    mJSONObjectOSM = jsonObjectOSM;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                JSONObject jsonObject = Utility.convertXMLtoJSON(responseBody);
                try {
                    JSONObject jsonObjectOSM = jsonObject.getJSONObject("osm");
                    JSONObject jsonObjectWAY = jsonObjectOSM.getJSONObject("node");
                    mVersionNumber = jsonObjectWAY.optString("version");
                    mJSONObjectOSM = jsonObjectOSM;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }else {
            hideLoader();
        }
        this.runOnUiThread(new Runnable() {
            public void run() {
                callToUpdateWayDataOnOSMServer();

            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (asyncTaskOsmApi != null) {
            asyncTaskOsmApi.dismissDialog();
        }
    }

    @Override
    public void OnIconEditViewOnClick(View v, int position) {
        Intent intent = new Intent(this, SettingDetailActivity.class);
        if (mIsForWAY) {
            switch (position) {
                case 0:
                    intent.putExtra(AppConstant.POSITION_SETTING, prepareListDataWay().get(0).getKeyPosition());
                    mPositionClicked = prepareListDataWay().get(0).getKeyPosition();
                    intent.putExtra(AppConstant.SETTING_ITEM_SELECTED_SEND, prepareListDataWay().get(0).getKeyString());
                    intent.putExtra(AppConstant.IS_FOR_WAY, true);
                    startActivityForResult(intent, OPEN_SETTING_TYPE);
                    break;
                case 2:
                    intent.putExtra(AppConstant.POSITION_SETTING, prepareListDataWay().get(1).getKeyPosition());
                    mPositionClicked = prepareListDataWay().get(1).getKeyPosition();
                    intent.putExtra(AppConstant.SETTING_ITEM_SELECTED_SEND, prepareListDataWay().get(1).getKeyString());
                    intent.putExtra(AppConstant.IS_FOR_WAY, true);
                    if(mHashMapWay!=null && mHashMapWay.get(2)!=null && mHashMapWay.get(2).getValue()!=null) {
                        intent.putExtra(AppConstant.VALUE_FOR_EDITOR, mHashMapWay.get(2).getValue());
                    }
                    startActivityForResult(intent, OPEN_SETTING_TYPE);
                    break;
                case 3:
                    intent.putExtra(AppConstant.POSITION_SETTING, prepareListDataWay().get(2).getKeyPosition());
                    mPositionClicked = prepareListDataWay().get(2).getKeyPosition();
                    intent.putExtra(AppConstant.SETTING_ITEM_SELECTED_SEND, prepareListDataWay().get(2).getKeyString());
                    intent.putExtra(AppConstant.IS_FOR_WAY, true);
                    if(mHashMapWay!=null && mHashMapWay.get(3)!=null && mHashMapWay.get(3).getValue()!=null) {
                        intent.putExtra(AppConstant.VALUE_FOR_EDITOR, mHashMapWay.get(3).getValue());
                    }
                    startActivityForResult(intent, OPEN_SETTING_TYPE);
                    break;
               /* case 3:
                    intent.putExtra(AppConstant.POSITION_SETTING, position);
                    mPositionClicked = position;
                    intent.putExtra(AppConstant.SETTING_ITEM_SELECTED_SEND, prepareListDataWay().get(position).getKeyString());
                    intent.putExtra(AppConstant.IS_FOR_WAY, true);
                    startActivityForResult(intent, OPEN_SETTING_TYPE);
                    break;*/

            }
        } else {
            switch (position) {
                case 0:
                    intent.putExtra(AppConstant.POSITION_SETTING, position);
                    mPositionClicked = position;
                    intent.putExtra(AppConstant.SETTING_ITEM_SELECTED_SEND, prepareListDataNode().get(0).getKeyString());
                    intent.putExtra(AppConstant.IS_FOR_WAY, false);
                    startActivityForResult(intent, OPEN_SETTING_TYPE);
                    break;
            }
        }
    }

    @Override
    public void OnIconCheckBoxOnClick(View v, int position, boolean isChecked, Attributes
            attributes) {
        switch (position) {
            case 0:
                changeCheckBox(v, isChecked, position, attributes);
                break;
            case 1:
                changeCheckBox(v, isChecked, position, attributes);
                break;
            case 2:
                changeCheckBox(v, isChecked, position, attributes);
                break;
            case 3:
                changeCheckBox(v, isChecked, position, attributes);
                break;

        }
    }

    /**
     * Change Check Box click
     *
     * @param isChecked weather true or false
     * @param v         view
     */
    private void changeCheckBox(View v, boolean isChecked, int positionClicked, Attributes
            attributes) {
        if (isChecked) {
            ((CheckBox) v).setText(getResources().getString(R.string.verified));
            ((CheckBox) v).setTextColor(getResources().getColor(R.color.colorPrimary));
            attributes.setValid(true);
            mHashMapWay.put(positionClicked, attributes);
        } else {
            ((CheckBox) v).setText(getResources().getString(R.string.not_verify));
            ((CheckBox) v).setTextColor(getResources().getColor(R.color.colorBlack));
            attributes.setValid(false);
            mHashMapWay.put(positionClicked, attributes);
        }
    }


}
