package com.disablerouting.setting;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.api.ApiEndPoint;
import com.disablerouting.base.BaseActivityImpl;
import com.disablerouting.common.AppConstant;
import com.disablerouting.curd_operations.WayDataPreference;
import com.disablerouting.curd_operations.manager.UpdateWayManager;
import com.disablerouting.curd_operations.manager.ValidateWayManager;
import com.disablerouting.curd_operations.model.*;
import com.disablerouting.login.AsyncTaskOsmApi;
import com.disablerouting.login.IAysncTaskOsm;
import com.disablerouting.login.OauthData;
import com.disablerouting.setting.presenter.ISettingScreenPresenter;
import com.disablerouting.setting.presenter.SettingScreenPresenter;
import com.disablerouting.setting.setting_detail.SettingDetailActivity;
import com.disablerouting.utils.Utility;
import com.github.scribejava.core.model.Verb;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class SettingActivity extends BaseActivityImpl implements SettingAdapterListener, ISettingView,
        IAysncTaskOsm {

    @BindView(R.id.rcv_setting)
    RecyclerView mRecyclerView;

    final int OPEN_SETTING_TYPE = 200;
    private SettingAdapter mSettingAdapter;
    private ListWayData mListWayData;

    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, Attributes> mHashMapWay = new HashMap<>();
    private ISettingScreenPresenter mISettingScreenPresenter;
    private String mChangeSetID = "";
    private String mVersionNumber;
    private String mUpdateVersionNumber;
    private String mWayID;
    private String mURLNodeSet = ApiEndPoint.SANDBOX_BASE_URL_OSM + "node/create";

    private AsyncTaskOsmApi asyncTaskOsmApi;
    private int mPositionClicked = -1;
    private List<NodeReference> mNodeList;
    private String mValueFootWay = "";
    private String mValueHighWay = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        if (Utility.isOnline(this)) {
            callToGetChangeSet();
        }
        mISettingScreenPresenter = new SettingScreenPresenter(this, new UpdateWayManager(), new ValidateWayManager());
        if (getIntent().hasExtra(AppConstant.WAY_DATA)) {
            mListWayData = getIntent().getParcelableExtra(AppConstant.WAY_DATA);
            if (mListWayData != null) {
                mWayID = mListWayData.getId();
                mNodeList = mListWayData.getNodeReference();
                getDataFromWay();
                setUpRecyclerView();
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
        asyncTaskOsmApi = new AsyncTaskOsmApi(SettingActivity.this, oauthData, this, false, AppConstant.API_TYPE_CREATE_CHANGE_SET);
        asyncTaskOsmApi.execute("");
    }

    /**
     * Api Call To GET WAY VERSION NUMBER
     */
    private void callToGetWay() {
        String URLWayGet = ApiEndPoint.SANDBOX_BASE_URL_OSM + "way/" + mWayID;
        OauthData oauthData = new OauthData(Verb.GET, "", URLWayGet);
        asyncTaskOsmApi = new AsyncTaskOsmApi(SettingActivity.this, oauthData, this, true, "");
        asyncTaskOsmApi.execute("");
    }

    /**
     * Api Call To UPDATE WAY DATA ON OSM SERVER
     */
    private void callToUpdateWayDataOnServer() {
        StringBuilder tags = new StringBuilder();
        for (Map.Entry<Integer, Attributes> pair : mHashMapWay.entrySet()) {
            Attributes attributes = pair.getValue();
            assert attributes != null;
            if (attributes.getKey() != null && attributes.getKey().equalsIgnoreCase(AppConstant.KEY_INCLINE) || attributes.getKey().equalsIgnoreCase(AppConstant.KEY_WIDTH)) {
                if (!attributes.getKey().equalsIgnoreCase(AppConstant.KEY_INCLINE)) {
                    tags.append("<tag k=\"" + attributes.getKey() + "\" v=\"" + Utility.changeCommaToDot(attributes.getValue()) + "\"/>\n");
                } else {
                    tags.append("<tag k=\"" + attributes.getKey() + "\" v=\"" + attributes.getValue().replace(">", "") + "\"/>\n");
                }
            }
        }

        // List<RequestNode> nodesList = new ArrayList<>();
        StringBuilder nodes = new StringBuilder();
        for (int i = 0; i < mNodeList.size(); i++) {
            nodes.append("<nd ref=\"" + mNodeList.get(i).getId() + "\"/>\n");
        }
        /*Way way= new Way(mWayID,mChangeSetID,mVersionNumber,tagList,nodesList);
        requestCreateChangeSet.setWay(way);*/
        String requestString = "<osm>\n" +
                " <way  id=\"" + mWayID + "\" changeset=\"" + mChangeSetID + "\" version=\"" + mVersionNumber + "\" >\n" +
                nodes +
                tags +
                " </way>\n" +
                "</osm>";
        String URLWayPUT = ApiEndPoint.SANDBOX_BASE_URL_OSM + "way/" + mWayID;
        OauthData oauthData = new OauthData(Verb.PUT, requestString, URLWayPUT);
        asyncTaskOsmApi = new AsyncTaskOsmApi(SettingActivity.this, oauthData, this, false, AppConstant.API_TYPE_CREATE_PUT_WAY);
        asyncTaskOsmApi.execute("");
    }

    /**
     * Setup recycler view
     */
    private void setUpRecyclerView() {
        mSettingAdapter = new SettingAdapter(this, prepareListData(), this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mSettingAdapter);
        if (mHashMapWay != null) {
            mSettingAdapter.setSelectionMap(mHashMapWay, false);
            mSettingAdapter.notifyDataSetChanged();

        }
    }

    /**
     * Prepare items of attributes
     *
     * @return list
     */
    private ArrayList<String> prepareListData() {
        ArrayList<String> stringArrayList = new ArrayList<>();
        stringArrayList.add(getString(R.string.surface_type));
        // stringArrayList.add(getString(R.string.maximum_sloped));
        stringArrayList.add(getString(R.string.maximum_incline));
        stringArrayList.add(getString(R.string.sidewalk_width));
        return stringArrayList;
    }


    @OnClick(R.id.img_back)
    public void onBackClick() {
        finish();
    }

    @OnClick(R.id.btn_finish)
    public void onFinishClick() {
        if (mListWayData != null) {
            boolean isValid = Boolean.parseBoolean(mListWayData.getIsValid());
            if (!isValid) {
                if (Utility.isOnline(this)) {
                    callToGetWay();
                }
            } else {
                finish();
            }
        } else {
            finish();
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
        Attributes attributesFootway = new Attributes();
        attributesFootway.setKey(AppConstant.KEY_FOOTWAY);
        mHashMapWay.put(0, attributesFootway);

        Attributes attributesHighway = new Attributes();
        attributesHighway.setKey(AppConstant.KEY_HIGHWAY);
        mHashMapWay.put(1, attributesHighway);

        for (int i = 0; i < mListWayData.getAttributesList().size(); i++) {
            switch (mListWayData.getAttributesList().get(i).getKey()) {
                case AppConstant.KEY_INCLINE:
                    Attributes attributesIncline = new Attributes();
                    attributesIncline.setKey(mListWayData.getAttributesList().get(i).getKey());
                    String value;
                    if (mListWayData.getAttributesList().get(i).getValue() != null && mListWayData.getAttributesList().get(i).getValue().contains("&lt")) {
                        value = mListWayData.getAttributesList().get(i).getValue().replace("&lt;", ">");
                    } else if (mListWayData.getAttributesList().get(i).getValue() != null && mListWayData.getAttributesList().get(i).getValue().contains("Up to")) {
                        value = mListWayData.getAttributesList().get(i).getValue().replace("Up to", "Bis zu");
                    } else {
                        value = mListWayData.getAttributesList().get(i).getValue();
                    }
                    attributesIncline.setValue(value);
                    attributesIncline.setValid(mListWayData.getAttributesList().get(i).isValid());
                    mHashMapWay.put(1, attributesIncline);
                    break;

                case AppConstant.KEY_FOOTWAY:
                    mValueFootWay = mListWayData.getAttributesList().get(i).getValue();
                    break;

                case AppConstant.KEY_HIGHWAY:
                    mValueHighWay = mListWayData.getAttributesList().get(i).getValue();
                    break;

                case AppConstant.KEY_WIDTH:
                    Attributes attributesWidth = new Attributes();
                    attributesWidth.setKey(mListWayData.getAttributesList().get(i).getKey());
                    if (!mListWayData.getAttributesList().get(i).getValue().contains(",")) {
                        String stringValue = Utility.trimTWoDecimalPlaces(Double.parseDouble(mListWayData.getAttributesList().get(i).getValue()));
                        attributesWidth.setValue(Utility.changeDotToComma(stringValue));
                    } else {
                        attributesWidth.setValue(mListWayData.getAttributesList().get(i).getValue());

                    }
                    attributesWidth.setValid(mListWayData.getAttributesList().get(i).isValid());
                    mHashMapWay.put(3, attributesWidth);
                    break;

                default:

            }

        }

    }

    /**
     * API CALL FOR UPDATE DATA
     *
     * @param versionString version updated
     */
    private void onUpdateWay(String versionString) {
        RequestWayInfo requestWayInfo = new RequestWayInfo();
        RequestWayData wayDataValidate = new RequestWayData();
        wayDataValidate.setId(mListWayData.getId());
        wayDataValidate.setProjectId(mListWayData.getProjectId());
        wayDataValidate.setValid(mListWayData.getIsValid());
        wayDataValidate.setVersion(versionString);
        List<AttributesValidate> attributesValidateList = new ArrayList<>();
        AttributesValidate attributesValidate = null;
        if (!mValueFootWay.isEmpty()) {
            if (mHashMapWay.get(0) != null && !mHashMapWay.get(0).getKey().isEmpty()) {
                attributesValidate = new AttributesValidate();
                attributesValidate.setKey(AppConstant.KEY_FOOTWAY);
                if (mValueFootWay != null) {
                    attributesValidate.setValue(mValueFootWay);
                }
                if (mHashMapWay.get(0) != null && mHashMapWay.get(0).getKey() != null) {
                    attributesValidate.setValid(false);
                }
                attributesValidateList.add(attributesValidate);
            }
        }
        if (!mValueHighWay.isEmpty()) {
            if (mHashMapWay.get(1) != null && !mHashMapWay.get(1).getKey().isEmpty()) {
                attributesValidate = new AttributesValidate();
                attributesValidate.setKey(AppConstant.KEY_HIGHWAY);
                if (mValueHighWay != null) {
                    attributesValidate.setValue(mValueHighWay);
                }
                attributesValidate.setValid(false);
                attributesValidateList.add(attributesValidate);
            }
        }
        if (mHashMapWay.get(2) != null && !mHashMapWay.get(2).getKey().isEmpty()) {
            attributesValidate = new AttributesValidate();
            attributesValidate.setKey(AppConstant.KEY_INCLINE);
            if (mHashMapWay.get(2).getValue() != null && mHashMapWay.get(2).getValue().contains(">")) {
                attributesValidate.setValue(mHashMapWay.get(2).getValue().replace(">", ""));

            } else {
                attributesValidate.setValue(mHashMapWay.get(2).getValue());
            }
            attributesValidate.setValid(mHashMapWay.get(2).isValid());
            attributesValidateList.add(attributesValidate);
        }
        if (mHashMapWay.get(3) != null && !mHashMapWay.get(3).getKey().isEmpty()) {
            attributesValidate = new AttributesValidate();
            attributesValidate.setKey(AppConstant.KEY_WIDTH);
            attributesValidate.setValue(Utility.changeCommaToDot(mHashMapWay.get(3).getValue()));
            attributesValidate.setValid(mHashMapWay.get(3).isValid());
            attributesValidateList.add(attributesValidate);
        }
        wayDataValidate.setAttributesValidate(attributesValidateList);
        requestWayInfo.setWayDataValidates(wayDataValidate);
        requestWayInfo.setModifiedByUser("shubham.sahgal@daffodilsw.com");
        mISettingScreenPresenter.onUpdate(requestWayInfo);
    }

    @Override
    public void onUpdateDataReceived(ResponseUpdate responseUpdate) {
        if (responseUpdate.isStatus()) {
            boolean isAllValid = true;
            List<ListWayData> listWayDataList = WayDataPreference.getInstance(this).getNotValidatedWayData();
            ArrayList<ListWayData> listNotValidated = null;
            for (int i = 0; i < listWayDataList.size(); i++) {
                if (mListWayData.getId().equals(listWayDataList.get(i).getId())) {
                    List<Attributes> attributesList = listWayDataList.get(i).getAttributesList();
                    for (int j = 0; j < attributesList.size(); j++) {
                        for (Map.Entry<Integer, Attributes> pair : mHashMapWay.entrySet()) {
                            Attributes attributesUpdated = pair.getValue();
                            if (attributesList.get(j).getKey().equalsIgnoreCase(attributesUpdated.getKey())) {
                                attributesList.get(j).setKey(attributesUpdated.getKey());
                                attributesList.get(j).setValue(attributesUpdated.getValue());
                                attributesList.get(j).setValid(attributesUpdated.isValid());
                            }
                        }
                        if (!attributesList.get(j).isValid()) {
                            isAllValid = false;
                        }
                    }
                    listWayDataList.get(i).setAttributesList(attributesList);
                    if (!isAllValid) {
                        listWayDataList.get(i).setIsValid("false");
                        listNotValidated = new ArrayList<ListWayData>(listWayDataList);

                    } else {
                        listWayDataList.get(i).setIsValid("true");
                        //Remove from not validated data and put in validated list
                        List<ListWayData> listWayDataValidated = WayDataPreference.getInstance(this).getValidateWayData();
                        ArrayList<ListWayData> listValidated = new ArrayList<ListWayData>(listWayDataValidated);

                        listValidated.add(listWayDataList.get(i));
                        WayDataPreference.getInstance(this).saveValidateWayData(listValidated);
                        listNotValidated = new ArrayList<ListWayData>(listWayDataList);
                        listNotValidated.remove(i);
                        break;
                    }
                }
            }
            WayDataPreference.getInstance(this).saveNotValidatedWayData(listNotValidated);
            setResult(RESULT_OK);
            Toast.makeText(SettingActivity.this, R.string.updated_info, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            if (responseUpdate.getError() != null && responseUpdate.getError().get(0) != null &&
                    responseUpdate.getError().get(0).getMessage() != null) {
                Toast.makeText(this, responseUpdate.getError().get(0).getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }


    @Override
    public void onFailure(String error) {
        Toast.makeText(SettingActivity.this, error, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void showLoader() {
        showProgress();
    }

    @Override
    public void hideLoader() {
        hideProgress();

    }

    @Override
    public void onSuccessAsyncTask(String responseBody, String API_TYPE) {
        if (responseBody != null) {
            if (API_TYPE.equalsIgnoreCase(AppConstant.API_TYPE_CREATE_CHANGE_SET)) {
                mChangeSetID = responseBody;
            }
            if (API_TYPE.equalsIgnoreCase(AppConstant.API_TYPE_CREATE_PUT_WAY)) {
                mUpdateVersionNumber = responseBody;
                this.runOnUiThread(new Runnable() {
                    public void run() {
                        onUpdateWay(mUpdateVersionNumber);
                    }
                });
            }
        }

    }

    @Override
    public void onFailureAsyncTask(final String errorBody) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(SettingActivity.this, errorBody, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSuccessAsyncTaskForGetWay(String responseBody) {
        if (responseBody != null) {
            JSONObject jsonObject = Utility.convertXMLtoJSON(responseBody);
            try {
                JSONObject jsonObjectOSM = jsonObject.getJSONObject("osm");
                JSONObject jsonObjectWAY = jsonObjectOSM.getJSONObject("way");
                mVersionNumber = jsonObjectWAY.optString("version");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        this.runOnUiThread(new Runnable() {
            public void run() {
                callToUpdateWayDataOnServer();

            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        asyncTaskOsmApi.dismissDialog();

    }

    @Override
    public void OnIconEditViewOnClick(View v, int position) {
        Intent intent = new Intent(this, SettingDetailActivity.class);
        switch (position) {
            case 0:
                intent.putExtra(AppConstant.POSITION_SETTING, position);
                mPositionClicked = position;
                intent.putExtra(AppConstant.SETTING_ITEM_SELECTED_SEND, prepareListData().get(position));
                startActivityForResult(intent, OPEN_SETTING_TYPE);
                break;
            case 1:
                intent.putExtra(AppConstant.POSITION_SETTING, position);
                mPositionClicked = position;
                intent.putExtra(AppConstant.SETTING_ITEM_SELECTED_SEND, prepareListData().get(position));
                startActivityForResult(intent, OPEN_SETTING_TYPE);
                break;
            case 2:
                intent.putExtra(AppConstant.POSITION_SETTING, position);
                mPositionClicked = position;
                intent.putExtra(AppConstant.SETTING_ITEM_SELECTED_SEND, prepareListData().get(position));
                startActivityForResult(intent, OPEN_SETTING_TYPE);
                break;
            case 3:
                intent.putExtra(AppConstant.POSITION_SETTING, position);
                mPositionClicked = position;
                intent.putExtra(AppConstant.SETTING_ITEM_SELECTED_SEND, prepareListData().get(position));
                startActivityForResult(intent, OPEN_SETTING_TYPE);
                break;

        }
    }

    @Override
    public void OnIconCheckBoxOnClick(View v, int position, boolean isChecked, Attributes attributes) {
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
    private void changeCheckBox(View v, boolean isChecked, int positionClicked, Attributes attributes) {
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
