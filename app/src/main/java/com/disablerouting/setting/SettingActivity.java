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
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.api.ApiEndPoint;
import com.disablerouting.application.AppData;
import com.disablerouting.base.BaseActivityImpl;
import com.disablerouting.common.AppConstant;
import com.disablerouting.curd_operations.manager.ListGetWayManager;
import com.disablerouting.curd_operations.manager.UpdateWayManager;
import com.disablerouting.curd_operations.manager.ValidateWayManager;
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

    final int OPEN_SETTING_TYPE = 200;
    private SettingAdapter mSettingAdapter;
    private ListWayData mListWayData;
    private NodeReference mNodeReference;

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
    private List<NodeReference> mNodeList;
    private boolean mIsForWAY = false;
    private boolean isValidFORCall;
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, String> mNodeIdsCreated = new HashMap<>();
    private String mWayIdNew = null;
    private Integer mNodeRefIndex = -1;
    private boolean mCallForWay = false;
    private boolean mWayIdAvailable = false;
    private Handler mHandler = new Handler();
    private Integer mNodeUpdate = 0;
    private boolean mISFromOSM = false;


    private List<ListWayData> mWayListValidatedData = new ArrayList<>();
    private List<ListWayData> mWayListNotValidatedData = new ArrayList<>();
    private List<NodeReference> mNodeListValidatedData = new ArrayList<>();
    private List<NodeReference> mNodeListNotValidatedData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        if (Utility.isOnline(this)) {
            callToGetChangeSet();
        }
        mISettingScreenPresenter = new SettingScreenPresenter(this, new UpdateWayManager(),
                new ValidateWayManager(), new ListGetWayManager());
        if (getIntent().hasExtra(AppConstant.WAY_DATA)) {
            mIsForWAY = getIntent().getBooleanExtra(AppConstant.IS_FOR_WAY, false);
            if (mIsForWAY) {
                mListWayData = getIntent().getParcelableExtra(AppConstant.WAY_DATA);
                if (mListWayData != null) {
                    mWayID = mListWayData.getOSMWayId();
                    mNodeList = mListWayData.getNodeReference();
                    if (mListWayData.getIsForData() != null) {
                        mISFromOSM = !mListWayData.getIsForData().isEmpty() && mListWayData.getIsForData().equalsIgnoreCase(AppConstant.OSM_DATA);
                    }
                    getDataFromWay();
                    setUpRecyclerView();
                }
            } else {
                mNodeReference = getIntent().getParcelableExtra(AppConstant.WAY_DATA);
                if (mNodeReference != null) {
                    isValidFORCall = mNodeReference.getAttributes().get(0).isValid();
                    mNodeID = mNodeReference.getOSMNodeId();
                    if (mNodeReference.getIsForData() != null) {
                        mISFromOSM = !mNodeReference.getIsForData().isEmpty() && mNodeReference.getIsForData().equalsIgnoreCase(AppConstant.OSM_DATA);
                    }
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
        String string = "<osm><changeset><tag k=\"created_by\" v=\"JOSM 1.61\"/><tag k=\"comment\" v=\"Just adding some streetnames\"/></changeset></osm>";
        String URLChangeSet = ApiEndPoint.SANDBOX_BASE_URL_OSM + "changeset/create";
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
        String URLNodeGet = ApiEndPoint.SANDBOX_BASE_URL_OSM + "node/" + mNodeID;
        OauthData oauthData = new OauthData(Verb.GET, "", URLNodeGet);
        asyncTaskOsmApi = new AsyncTaskOsmApi(SettingActivity.this, oauthData,
                this, true, "", false);
        asyncTaskOsmApi.execute("");
        Log.e("API", URLNodeGet);

    }

    private void GetWayVersion() {
        String URLWayGet = ApiEndPoint.SANDBOX_BASE_URL_OSM + "way/" + mWayID;
        OauthData oauthData = new OauthData(Verb.GET, "", URLWayGet);
        asyncTaskOsmApi = new AsyncTaskOsmApi(SettingActivity.this, oauthData, this,
                true, "", false);
        asyncTaskOsmApi.execute("");
        Log.e("API", URLWayGet);


    }


    /**
     * Api Call To UPDATE WAY DATA ON OSM SERVER
     */
    private void callToUpdateWayDataOnOSMServer() {
        if (mIsForWAY) {
            onUpdateWAYonOSMServer();
        } else {
            onUpdateNODEonOSMServer();
        }
    }

    private void onUpdateNODEonOSMServer() {
        StringBuilder tags = new StringBuilder();
        for (Map.Entry<Integer, Attributes> pair : mHashMapWay.entrySet()) {
            Attributes attributes = pair.getValue();
            if (attributes != null && attributes.getValue() != null) {
                tags.append("<tag k=\"" + attributes.getKey() + "\" v=\"" + Utility.covertValueRequired(attributes.getValue()) + "\"/>\n");
            }

        }
        StringBuilder nodes = new StringBuilder();
        if(mNodeReference.getOSMNodeId() !=null && !mNodeReference.getOSMNodeId().isEmpty()) {
            nodes.append("<nd ref=\"" + mNodeReference.getOSMNodeId() + "\"/>\n");
        }
        String requestString = "<osm>\n" +
                " <node  id=\"" + mNodeID + "\" changeset=\"" + mChangeSetID + "\" version=\"" + mVersionNumber + "\"  lat=\"" + mNodeReference.getLat() + "\" lon=\"" + mNodeReference.getLon() + "\" >\n" +
                nodes +
                tags +
                " </node>\n" +
                "</osm>";

        String URLNodePUT = ApiEndPoint.SANDBOX_BASE_URL_OSM + "node/" + mNodeID;
        OauthData oauthData = new OauthData(Verb.PUT, requestString, URLNodePUT);
        asyncTaskOsmApi = new AsyncTaskOsmApi(SettingActivity.this, oauthData,
                this, false, AppConstant.API_TYPE_UPDATE_WAY_OR_NODE, false);
        asyncTaskOsmApi.execute("");
        Log.e("API", URLNodePUT);

    }

    private void onUpdateWAYonOSMServer() {
        StringBuilder tags = new StringBuilder();
        for (Map.Entry<Integer, Attributes> pair : mHashMapWay.entrySet()) {
            Attributes attributes = pair.getValue();
            assert attributes != null;
            if (attributes.getValue() != null) {
                tags.append("<tag k=\"" + attributes.getKey() + "\" v=\"" + Utility.covertValueRequired(attributes.getValue()) + "\"/>\n");
            }
            /*if(mISFromOSM){
                tags.append("<tag k=\"" + attributes.getKey() + "\" v=\"" + Utility.covertValueRequired(attributes.getValue()) + "\"/>\n");
            }else {
                if (attributes.getKey() != null && attributes.getKey().equalsIgnoreCase(AppConstant.KEY_INCLINE) ||
                        attributes.getKey() != null && attributes.getKey().equalsIgnoreCase(AppConstant.KEY_WIDTH)) {
                    tags.append("<tag k=\"" + attributes.getKey() + "\" v=\"" + Utility.covertValueRequired(attributes.getValue()) + "\"/>\n");

                }
            }*/
        }

        StringBuilder nodes = new StringBuilder();
        for (int i = 0; i < mNodeList.size(); i++) {
            if(mNodeList.get(i).getOSMNodeId() !=null && !mNodeList.get(i).getOSMNodeId().isEmpty())
            nodes.append("<nd ref=\"" + mNodeList.get(i).getOSMNodeId() + "\"/>\n");
        }
        String requestString = "<osm>\n" +
                " <way  id=\"" + mWayID + "\" changeset=\"" + mChangeSetID + "\" version=\"" + mVersionNumber + "\" >\n" +
                nodes +
                tags +
                " </way>\n" +
                "</osm>";
        String URLWayPUT = ApiEndPoint.SANDBOX_BASE_URL_OSM + "way/" + mWayID;
        OauthData oauthData = new OauthData(Verb.PUT, requestString, URLWayPUT);
        asyncTaskOsmApi = new AsyncTaskOsmApi(SettingActivity.this, oauthData,
                this, false, AppConstant.API_TYPE_UPDATE_WAY_OR_NODE, false);
        asyncTaskOsmApi.execute("");
        Log.e("API", URLWayPUT);

    }

    /**
     * Setup recycler view
     */
    private void setUpRecyclerView() {
        if (mIsForWAY) {
            mSettingAdapter = new SettingAdapter(this, prepareListDataWay(), this, mISFromOSM);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.setAdapter(mSettingAdapter);
            if (mHashMapWay != null) {
                mSettingAdapter.setSelectionMap(mHashMapWay, false);
                mSettingAdapter.notifyDataSetChanged();

            }
        } else {
            mSettingAdapter = new SettingAdapter(this, prepareListDataNode(), this, mISFromOSM);
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
        settingModel = new SettingModel(3, getString(R.string.sidewalk_width));
        modelArrayList.add(settingModel);
        return modelArrayList;
    }

    private ArrayList<SettingModel> prepareListDataNode() {
        ArrayList<SettingModel> modelArrayList = new ArrayList<>();
        SettingModel settingModel;
        settingModel = new SettingModel(0, getString(R.string.maximum_sloped));
        modelArrayList.add(settingModel);
        return modelArrayList;

    }

    @OnClick(R.id.img_back)
    public void onBackClick() {
        finish();
    }

    @OnClick(R.id.btn_finish)
    public void onFinishClick() {
        if (mIsForWAY) {
            if (mListWayData != null) {
                boolean isValid = Boolean.parseBoolean(mListWayData.getIsValid());
                if (!isValid) {
                    if (Utility.isOnline(this)) {
                        showLoader();
                        if (!mListWayData.getOSMWayId().isEmpty()) {
                            callToGetVersions();
                        } else {
                            mWayIdAvailable = false;
                            checkNodes();
                        }
                    }
                } else {
                    finish();
                }
            }
        } else {
            if (!isValidFORCall) {
                if (Utility.isOnline(this)) {
                    showLoader();
                    if (!mNodeReference.getOSMNodeId().isEmpty()) {
                        callToGetVersions();
                    } else {
                        mWayIdAvailable = false;
                        callToCreateNode(mNodeReference);
                    }
                }
            } else {
                finish();
            }
        }

    }

    /**
     * Api Call To CREATE NODE
     */
    private void callToCreateNode(NodeReference nodeReference) {
        if (AppData.getNewInstance() != null && AppData.getNewInstance().getCurrentLoc() != null) {
            double lat = AppData.getNewInstance().getCurrentLoc().latitude;
            double lon = AppData.getNewInstance().getCurrentLoc().longitude;
            String requestString;
            if (nodeReference.getAttributes() != null && nodeReference.getAttributes().size() != 0) {
                //Case When node attributes are there
                StringBuilder tags = new StringBuilder();
                tags.append("<tag k=\"" + nodeReference.getAttributes().get(0).getKey() + "\" v=\"" +
                        nodeReference.getAttributes().get(0).getValue() + "\"/>\n");

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

            String URLCreateNode = ApiEndPoint.SANDBOX_BASE_URL_OSM + "node/create";
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
    }

    /**
     * Api Call To CREATE WAY
     */
    private void callToCreateWay() {
        String requestString;
        StringBuilder tags = new StringBuilder();
        for (Attributes attributes : mListWayData.getAttributesList()) {
            String attributesKey = attributes.getKey();
            String attributesValue = attributes.getValue();
            if (attributes.getKey() != null && attributes.getValue() != null) {
                tags.append("<tag k=\"" + attributesKey + "\" v=\"" + attributesValue + "\"/>\n");
            }
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


        String URLCreateWay = ApiEndPoint.SANDBOX_BASE_URL_OSM + "way/create";
        Log.e("API", URLCreateWay);

        OauthData oauthData = new OauthData(Verb.PUT, requestString, URLCreateWay);
        asyncTaskOsmApi = new AsyncTaskOsmApi(SettingActivity.this, oauthData, this,
                false, AppConstant.API_TYPE_CREATE_WAY, false);
        asyncTaskOsmApi.execute("");

    }

    private void checkNodes() {
        if (mListWayData != null) {
            if (mListWayData.getNodeReference() != null && mListWayData.getNodeReference().size() != 0) {
                for (int i = 0; i < mListWayData.getNodeReference().size(); i++) {
                    mNodeRefIndex = i;
                    mCallForWay = i == mListWayData.getNodeReference().size() - 1;
                    if (mListWayData.getNodeReference().get(i).getOSMNodeId().isEmpty()) {
                        //Create Node
                        callToCreateNode(mListWayData.getNodeReference().get(i));
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
                attributes.setValue(dataString);
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

            for (int i = 0; i < mListWayData.getAttributesList().size(); i++) {
                switch (mListWayData.getAttributesList().get(i).getKey()) {

                    case AppConstant.KEY_SURFACE:
                        Attributes attributesSurface = new Attributes();
                        attributesSurface.setKey(mListWayData.getAttributesList().get(i).getKey());
                        attributesSurface.setValue(mListWayData.getAttributesList().get(i).getValue());
                        attributesSurface.setValid(mListWayData.getAttributesList().get(i).isValid());
                        mHashMapWay.put(0, attributesSurface);
                        break;

                    case AppConstant.KEY_HIGHWAY:
                        Attributes attributesHighWay = new Attributes();
                        attributesHighWay.setKey(mListWayData.getAttributesList().get(i).getKey());
                        attributesHighWay.setValue(mListWayData.getAttributesList().get(i).getValue());
                        attributesHighWay.setValid(mListWayData.getAttributesList().get(i).isValid());
                        mHashMapWay.put(1, attributesHighWay);
                        break;


                    case AppConstant.KEY_INCLINE:
                        Attributes attributesIncline = new Attributes();
                        attributesIncline.setKey(mListWayData.getAttributesList().get(i).getKey());
                        String value;
                        if (mListWayData.getAttributesList().get(i).getValue() != null && mListWayData.getAttributesList().get(i).getValue().contains("&lt;")) {
                            value = mListWayData.getAttributesList().get(i).getValue().replace("&lt;", ">");
                        } else if (mListWayData.getAttributesList().get(i).getValue() != null && mListWayData.getAttributesList().get(i).getValue().contains("&Lt;")) {
                            value = mListWayData.getAttributesList().get(i).getValue().replace("&Lt;", ">");
                        } else if (mListWayData.getAttributesList().get(i).getValue() != null && mListWayData.getAttributesList().get(i).getValue().contains("Up to")) {
                            value = mListWayData.getAttributesList().get(i).getValue().replace("Up to", "Bis zu");
                        } else {
                            value = mListWayData.getAttributesList().get(i).getValue();
                        }
                        attributesIncline.setValue(value);
                        attributesIncline.setValid(mListWayData.getAttributesList().get(i).isValid());
                        mHashMapWay.put(2, attributesIncline);
                        break;

                    case AppConstant.KEY_WIDTH:
                        Attributes attributesWidth = new Attributes();
                        attributesWidth.setKey(mListWayData.getAttributesList().get(i).getKey());
                        if (mListWayData.getAttributesList().get(i).getValue().contains(".")) {
                            String stringValue = Utility.trimTWoDecimalPlaces(Double.parseDouble(mListWayData.getAttributesList().get(i).getValue()));
                            attributesWidth.setValue(Utility.changeDotToComma(stringValue));
                        } else {
                            attributesWidth.setValue(mListWayData.getAttributesList().get(i).getValue());

                        }
                        attributesWidth.setValid(mListWayData.getAttributesList().get(i).isValid());
                        mHashMapWay.put(3, attributesWidth);
                        break;

                    case AppConstant.KEY_FOOTWAY:
                        Attributes attributesFootWay = new Attributes();
                        attributesFootWay.setKey(mListWayData.getAttributesList().get(i).getKey());
                        attributesFootWay.setValue(mListWayData.getAttributesList().get(i).getValue());
                        attributesFootWay.setValid(mListWayData.getAttributesList().get(i).isValid());
                        mHashMapWay.put(4, attributesFootWay);
                        break;
                    default:
                }

            }
        } else {
            for (int i = 0; i < mNodeReference.getAttributes().size(); i++) {
                switch (mNodeReference.getAttributes().get(i).getKey()) {
                    case AppConstant.KEY_KERB_HEIGHT:
                        Attributes attributesKerb = new Attributes();
                        attributesKerb.setKey(mNodeReference.getAttributes().get(i).getKey());
                        attributesKerb.setValue(Utility.changeMeterToCm(mNodeReference.getAttributes().get(i).getValue()));
                        attributesKerb.setValid(mNodeReference.getAttributes().get(i).isValid());
                        mHashMapWay.put(0, attributesKerb);
                        break;
                    default:
                }
            }

        }

    }

    private void onUpdateNode() {
        if (mIsForWAY) {
            for (int i = 0; i < mListWayData.getNodeReference().size(); i++) {
                if (mListWayData.getNodeReference().get(i).getOSMNodeId().isEmpty()) {
                    RequestNodeInfo requestNodeInfo = new RequestNodeInfo();
                    NodeReference nodeReference = new NodeReference();
                    String nodeOSMID = mNodeIdsCreated.get(i);
                    nodeReference.setOSMNodeId(nodeOSMID);
                    nodeReference.setAPINodeId(mListWayData.getNodeReference().get(i).getAPINodeId());
                    nodeReference.setLat(mListWayData.getNodeReference().get(i).getLat());
                    nodeReference.setLon(mListWayData.getNodeReference().get(i).getLon());
                    nodeReference.setVersion(mListWayData.getNodeReference().get(i).getVersion());
                    List<Attributes> attributesValidateList = new ArrayList<>();
                    Attributes attributesValidate = new Attributes();
                    if (mListWayData.getNodeReference().get(i).getAttributes() != null &&
                            mListWayData.getNodeReference().get(i).getAttributes().size() != 0) {
                        attributesValidate.setKey(mListWayData.getNodeReference().get(i).getAttributes().get(0).getKey());
                        attributesValidate.setValue(mListWayData.getNodeReference().get(i).getAttributes().get(0).getValue());
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
        } else {
            RequestNodeInfo requestNodeInfo = new RequestNodeInfo();
            NodeReference nodeReference = new NodeReference();
            String nodeOSMID = mNodeIdsCreated.get(0);
            nodeReference.setOSMNodeId(nodeOSMID);
            nodeReference.setAPINodeId(mNodeReference.getAPINodeId());
            nodeReference.setLat(mNodeReference.getLat());
            nodeReference.setLon(mNodeReference.getLat());
            nodeReference.setVersion(mNodeReference.getLon());
            List<Attributes> attributesValidateList = new ArrayList<>();
            Attributes attributesValidate = new Attributes();
            if (mNodeReference.getAttributes() != null &&
                    mNodeReference.getAttributes().size() != 0) {
                attributesValidate.setKey(mHashMapWay.get(0).getKey());
                attributesValidate.setValue(mHashMapWay.get(0).getValue());
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
        RequestWayInfo requestWayInfo = new RequestWayInfo();
        RequestWayData wayDataValidate = new RequestWayData();
        if (mWayIdNew != null) {
            wayDataValidate.setOSMWayId(mWayIdNew);

        } else {
            wayDataValidate.setOSMWayId(mListWayData.getOSMWayId());
        }
        wayDataValidate.setAPIWayId(mListWayData.getAPIWayId());
        wayDataValidate.setProjectId(mListWayData.getProjectId());
        wayDataValidate.setValid(mListWayData.getIsValid());
        wayDataValidate.setVersion(updateVersionNumber);
        List<AttributesValidate> attributesValidateList = new ArrayList<>();
        AttributesValidate attributesValidate;
        if (mHashMapWay.get(0) != null && !mHashMapWay.get(0).getKey().isEmpty() && mHashMapWay.get(0).getValue() != null) {
            attributesValidate = new AttributesValidate();
            attributesValidate.setKey(AppConstant.KEY_SURFACE);
            attributesValidate.setValue(Utility.covertValueRequired(mHashMapWay.get(0).getValue()));
            attributesValidate.setValid(mHashMapWay.get(0).isValid());
            attributesValidateList.add(attributesValidate);
        }
        if (mHashMapWay.get(1) != null && !mHashMapWay.get(1).getKey().isEmpty() && mHashMapWay.get(1).getValue() != null) {
            attributesValidate = new AttributesValidate();
            attributesValidate.setKey(AppConstant.KEY_HIGHWAY);
            attributesValidate.setValue(Utility.covertValueRequired(mHashMapWay.get(1).getValue()));
            attributesValidate.setValid(mHashMapWay.get(1).isValid());
            attributesValidateList.add(attributesValidate);
        }
        if (mHashMapWay.get(2) != null && !mHashMapWay.get(2).getKey().isEmpty() && mHashMapWay.get(2).getValue() != null) {
            attributesValidate = new AttributesValidate();
            attributesValidate.setKey(AppConstant.KEY_INCLINE);
            attributesValidate.setValue(Utility.covertValueRequired(mHashMapWay.get(2).getValue()));
            attributesValidate.setValid(mHashMapWay.get(2).isValid());
            attributesValidateList.add(attributesValidate);
        }
        if (mHashMapWay.get(3) != null && !mHashMapWay.get(3).getKey().isEmpty() && mHashMapWay.get(3).getValue() != null) {
            attributesValidate = new AttributesValidate();
            attributesValidate.setKey(AppConstant.KEY_WIDTH);
            attributesValidate.setValue(Utility.covertValueRequired(mHashMapWay.get(3).getValue()));
            attributesValidate.setValid(mHashMapWay.get(3).isValid());
            attributesValidateList.add(attributesValidate);
        }

        if (mHashMapWay.get(4) != null && !mHashMapWay.get(4).getKey().isEmpty() && mHashMapWay.get(4).getValue() != null) {
            attributesValidate = new AttributesValidate();
            attributesValidate.setKey(AppConstant.KEY_FOOTWAY);
            attributesValidate.setValue(Utility.covertValueRequired(mHashMapWay.get(4).getValue()));
            attributesValidate.setValid(mHashMapWay.get(4).isValid());
            attributesValidateList.add(attributesValidate);
        }
        wayDataValidate.setAttributesValidate(attributesValidateList);
        requestWayInfo.setWayDataValidates(wayDataValidate);
        if (UserPreferences.getInstance(this) != null && UserPreferences.getInstance(this).getUserDetail() != null) {
            requestWayInfo.setModifiedByUser(UserPreferences.getInstance(this).getUserDetail());
        }
        mISettingScreenPresenter.onUpdateWay(requestWayInfo, AppConstant.WAY_UPDATE);
    }

    private void onUpdateNodeOurServer(String updateVersionNumber) {
        RequestNodeInfo requestNodeInfo = new RequestNodeInfo();
        NodeReference nodeReference = new NodeReference();
        nodeReference.setOSMNodeId(mNodeReference.getOSMNodeId());
        nodeReference.setAPINodeId(mNodeID);
        nodeReference.setLat(mNodeReference.getLat());
        nodeReference.setLon(mNodeReference.getLon());
        nodeReference.setVersion(updateVersionNumber);
        List<Attributes> attributesValidateList = new ArrayList<>();
        Attributes attributesValidate = new Attributes();
        if (mHashMapWay.get(0) != null && !mHashMapWay.get(0).getKey().isEmpty()) {
            attributesValidate.setKey(mHashMapWay.get(0).getKey());
            String value = Utility.covertValueRequired(mHashMapWay.get(0).getValue());
            value = Utility.changeCmToMeter(value);
            attributesValidate.setValue(value);
            attributesValidate.setValid(mHashMapWay.get(0).isValid());
        }
        nodeReference.setAttributes(attributesValidateList);
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
                mRelativeLayoutProgress.setVisibility(View.GONE);
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
                mRelativeLayoutProgress.setVisibility(View.GONE);
                finish();

            }
        } else {
            if (mIsForWAY) {
                if (responseUpdate.isStatus()) {
                    mRelativeLayoutProgress.setVisibility(View.GONE);
                    if (updateType.equalsIgnoreCase(AppConstant.WAY_UPDATE)) {
                        setResult(RESULT_OK);
                        Toast.makeText(SettingActivity.this, R.string.updated_info, Toast.LENGTH_SHORT).show();
                        finish();
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
                    mRelativeLayoutProgress.setVisibility(View.GONE);
                    finish();
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
                setResult(RESULT_OK);
                mRelativeLayoutProgress.setVisibility(View.GONE);
                finish();
            }
        }
    }

    @Override
    public void onListDataSuccess(ResponseListWay responseWay) {
        if (responseWay != null) {
        }
    }

    @Override
    public void onFailureListData(String error) {
        mRelativeLayoutProgress.setVisibility(View.GONE);
        Toast.makeText(SettingActivity.this, error, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onFailure(String error) {
        mRelativeLayoutProgress.setVisibility(View.GONE);
        hideLoader();
        Toast.makeText(SettingActivity.this, getResources().getString(R.string.error_when_entry_not_saved), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoader() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {
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
            }
        });

    }

    @Override
    public void onSuccessAsyncTask(String responseBody, String API_TYPE) {
        if (responseBody != null) {
            if (API_TYPE.equalsIgnoreCase(AppConstant.API_TYPE_CREATE_CHANGE_SET)) {
                mChangeSetID = responseBody;
            }
            if (API_TYPE.equalsIgnoreCase(AppConstant.API_TYPE_UPDATE_WAY_OR_NODE)) {
                mUpdateVersionNumber = responseBody;
                this.runOnUiThread(new Runnable() {
                    public void run() {
                        if (mISFromOSM) {
                            if (mIsForWAY) {
                                Toast.makeText(SettingActivity.this, getResources().getString(R.string.updated_info), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SettingActivity.this, getResources().getString(R.string.updated_node_info), Toast.LENGTH_SHORT).show();

                            }
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            onUpdateWay(mUpdateVersionNumber);
                        }
                    }
                });
            }
            if (API_TYPE.equalsIgnoreCase(AppConstant.API_TYPE_CREATE_NODE)) {
                Log.e("ResponseNode:", responseBody);
                if (mIsForWAY) {
                    mNodeIdsCreated.put(mNodeRefIndex, responseBody);
                    if (mCallForWay) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callToCreateWay();

                            }
                        });
                    }
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
            if (mIsForWAY) {
                JSONObject jsonObject = Utility.convertXMLtoJSON(responseBody);
                try {
                    JSONObject jsonObjectOSM = jsonObject.getJSONObject("osm");
                    JSONObject jsonObjectWAY = jsonObjectOSM.getJSONObject("way");
                    mVersionNumber = jsonObjectWAY.optString("version");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                JSONObject jsonObject = Utility.convertXMLtoJSON(responseBody);
                try {
                    JSONObject jsonObjectOSM = jsonObject.getJSONObject("osm");
                    JSONObject jsonObjectWAY = jsonObjectOSM.getJSONObject("node");
                    mVersionNumber = jsonObjectWAY.optString("version");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
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
                    intent.putExtra(AppConstant.POSITION_SETTING, prepareListDataWay().get(position).getKeyPosition());
                    mPositionClicked = prepareListDataWay().get(position).getKeyPosition();
                    intent.putExtra(AppConstant.SETTING_ITEM_SELECTED_SEND, prepareListDataWay().get(position).getKeyString());
                    intent.putExtra(AppConstant.IS_FOR_WAY, true);
                    startActivityForResult(intent, OPEN_SETTING_TYPE);
                    break;
                case 1:
                    intent.putExtra(AppConstant.POSITION_SETTING, prepareListDataWay().get(position).getKeyPosition());
                    mPositionClicked = prepareListDataWay().get(position).getKeyPosition();
                    intent.putExtra(AppConstant.SETTING_ITEM_SELECTED_SEND, prepareListDataWay().get(position).getKeyString());
                    intent.putExtra(AppConstant.IS_FOR_WAY, true);
                    startActivityForResult(intent, OPEN_SETTING_TYPE);
                    break;
                case 2:
                    intent.putExtra(AppConstant.POSITION_SETTING, prepareListDataWay().get(position).getKeyPosition());
                    mPositionClicked = prepareListDataWay().get(position).getKeyPosition();
                    intent.putExtra(AppConstant.SETTING_ITEM_SELECTED_SEND, prepareListDataWay().get(position).getKeyString());
                    intent.putExtra(AppConstant.IS_FOR_WAY, true);
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

    /**
     * API call to get List data.
     */
    private void getListData() {
        mRelativeLayoutProgress.setVisibility(View.VISIBLE);
        mISettingScreenPresenter.getLisData();

    }
}
