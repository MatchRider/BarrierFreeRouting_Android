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
import com.disablerouting.application.AppData;
import com.disablerouting.base.BaseActivityImpl;
import com.disablerouting.capture_option.model.Node;
import com.disablerouting.capture_option.model.RequestCreateNode;
import com.disablerouting.common.AppConstant;
import com.disablerouting.curd_operations.manager.UpdateWayManager;
import com.disablerouting.curd_operations.manager.ValidateWayManager;
import com.disablerouting.curd_operations.model.*;
import com.disablerouting.feedback.model.RequestCreateChangeSet;
import com.disablerouting.feedback.model.RequestTag;
import com.disablerouting.login.AsyncTaskOsmApi;
import com.disablerouting.login.IAysncTaskOsm;
import com.disablerouting.login.OauthData;
import com.disablerouting.route_planner.model.WayCustomModel;
import com.disablerouting.setting.presenter.ISettingScreenPresenter;
import com.disablerouting.setting.presenter.SettingScreenPresenter;
import com.disablerouting.setting.setting_detail.SettingDetailActivity;
import com.github.scribejava.core.model.Verb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SettingActivity extends BaseActivityImpl implements SettingAdapterListener, ISettingView,
        IAysncTaskOsm {

    @BindView(R.id.rcv_setting)
    RecyclerView mRecyclerView;

    final int OPEN_SETTING_TYPE = 200;
    private SettingAdapter mSettingAdapter;
    private WayCustomModel mWayCustomModel;

    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, Attributes> mHashMapWay = new HashMap<>();
    private ISettingScreenPresenter mISettingScreenPresenter;

    private String mURLChangeSet = ApiEndPoint.SANDBOX_BASE_URL_OSM + "changeset/create";
    private String mURLNodeSet = ApiEndPoint.SANDBOX_BASE_URL_OSM + "node/create";
    private AsyncTaskOsmApi asyncTaskOsmApi;
    private String mChangeSetID;
    private List<RequestTag> mRequestTagList = new ArrayList<>();
    private int mPositionClicked = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        mISettingScreenPresenter = new SettingScreenPresenter(this, new UpdateWayManager(), new ValidateWayManager());
        if (getIntent().hasExtra(AppConstant.WAY_DATA)) {
            mWayCustomModel = getIntent().getParcelableExtra(AppConstant.WAY_DATA);
            if (mWayCustomModel != null) {
                getDataFromWay();
            }
        }

        callToGetChangeSet();
        setUpRecyclerView();

    }

    /**
     * Api Call To Create Change Set
     */
    private void callToGetChangeSet() {
        RequestCreateChangeSet requestCreateChangeSet = new RequestCreateChangeSet();
        List<RequestTag> list = new ArrayList<>();
        RequestTag requestTag = new RequestTag("created_by", "JOSM 1.61");
        list.add(requestTag);
        requestTag = new RequestTag("comment", "Just adding some streetnames");
        list.add(requestTag);
        requestCreateChangeSet.setRequestTag(list);

        String string = "<osm><changeset><tag k=\"created_by\" v=\"JOSM 1.61\"/><tag k=\"comment\" v=\"Just adding some streetnames\"/></changeset></osm>";
        OauthData oauthData = new OauthData(Verb.PUT, string, mURLChangeSet);
        asyncTaskOsmApi = new AsyncTaskOsmApi(SettingActivity.this, oauthData, this);
        asyncTaskOsmApi.execute("");
    }

    private void callToSetChangeSet() {
        if (mChangeSetID != null) {
            RequestCreateNode requestCreateNode = new RequestCreateNode();
            String latitude = String.valueOf(AppData.getNewInstance().getCurrentLoc().latitude);
            String longitude = String.valueOf(AppData.getNewInstance().getCurrentLoc().longitude);
            Node node = new Node(mChangeSetID, latitude, longitude);

            RequestTag requestTag = new RequestTag("note", "Just a node");
            mRequestTagList.add(requestTag);
            node.setRequestTagList(mRequestTagList);
            requestCreateNode.setNode(node);

            String stringBuilder = "<osm><node changeset=" + "\"" + String.valueOf(mChangeSetID) + "\"" + " " +
                    "lat=" + "\"" + String.valueOf(latitude) + "\"" + " " +
                    "lon=" + "\"" + String.valueOf(longitude) + "\"" + ">" +
                    "<tag k=\"note\" v=\"Just a node\"/></node></osm>";


            String string = "<osm>\n" +
                    " <node changeset=\"112100\" lat=\"28.584220243018713\" lon=\"77.13020324707031\">\n" +
                    "   <tag k=\"note\" v=\"Just a node\"/>\n" +
                    " </node>\n" +
                    "</osm>";

            OauthData oauthData = new OauthData(Verb.PUT, stringBuilder, mURLNodeSet);
            new AsyncTaskOsmApi(SettingActivity.this, oauthData, this).execute("");
        }
    }

    /**
     * Setup recycler view
     */
    private void setUpRecyclerView() {
        mSettingAdapter = new SettingAdapter(this, prepareListData(), this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mSettingAdapter);
        if (mHashMapWay != null) {
            mSettingAdapter.setSelectionMap(mHashMapWay,false);
            mSettingAdapter.notifyDataSetChanged();

        }
    }

    private ArrayList<String> prepareListData() {
        ArrayList<String> stringArrayList = new ArrayList<>();
        stringArrayList.add(getString(R.string.surface_type));
        stringArrayList.add(getString(R.string.maximum_sloped));
        stringArrayList.add(getString(R.string.maximum_incline));
        stringArrayList.add(getString(R.string.sidewalk_width));
        return stringArrayList;
    }


    @OnClick(R.id.img_back)
    public void onBackClick() {
        finish();
    }

    @OnClick(R.id.btn_done)
    public void onDoneClick() {
        if (mWayCustomModel != null) {
            onUpdateWay();
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
                attributes.setValid(true);
                mHashMapWay.put(mPositionClicked, attributes);
                mSettingAdapter.setSelectionMap(mHashMapWay,true);
                mSettingAdapter.notifyDataSetChanged();
            }
        }
    }


    private void getDataFromWay() {
        for (int i = 0; i < mWayCustomModel.getAttributesList().size(); i++) {
            switch (mWayCustomModel.getAttributesList().get(i).getKey()) {
                case "incline":
                    mHashMapWay.put(2, mWayCustomModel.getAttributesList().get(i));
                    break;

                case "footway":
                    mHashMapWay.put(0, mWayCustomModel.getAttributesList().get(i));
                    break;

                case "highway":
                    mHashMapWay.put(1, mWayCustomModel.getAttributesList().get(i));
                    break;

                case "width":
                    mHashMapWay.put(3, mWayCustomModel.getAttributesList().get(i));
                    break;

                default:

            }
        }
    }


    private void onUpdateWay() {
        RequestWayInfo requestWayInfo = new RequestWayInfo();
        RequestWayData wayDataValidate = new RequestWayData();
        wayDataValidate.setId(mWayCustomModel.getId());
        wayDataValidate.setProjectId(mWayCustomModel.getProjectId());
        wayDataValidate.setValid(mWayCustomModel.getStatus());
        List<AttributesValidate> attributesValidateList = new ArrayList<>();
        AttributesValidate attributesValidate = null;
        if (mHashMapWay.get(0) != null && !mHashMapWay.get(0).getKey().isEmpty()) {
            attributesValidate = new AttributesValidate();
            attributesValidate.setKey("footway");
            attributesValidate.setValue(mHashMapWay.get(0).getValue());
            attributesValidate.setValid(mHashMapWay.get(0).isValid());
            attributesValidateList.add(attributesValidate);
        }
        if (mHashMapWay.get(1) != null && !mHashMapWay.get(1).getKey().isEmpty()) {
            attributesValidate = new AttributesValidate();
            attributesValidate.setKey("highway");
            attributesValidate.setValue(mHashMapWay.get(1).getValue());
            attributesValidate.setValid(mHashMapWay.get(1).isValid());
            attributesValidateList.add(attributesValidate);
        }
        if (mHashMapWay.get(2) != null && !mHashMapWay.get(2).getKey().isEmpty()) {
            attributesValidate = new AttributesValidate();
            attributesValidate.setKey("incline");
            attributesValidate.setValue(mHashMapWay.get(2).getValue());
            attributesValidate.setValid(mHashMapWay.get(2).isValid());
            attributesValidateList.add(attributesValidate);
        }
        if (mHashMapWay.get(3) != null && !mHashMapWay.get(3).getKey().isEmpty()) {
            attributesValidate = new AttributesValidate();
            attributesValidate.setKey("width");
            attributesValidate.setValue(mHashMapWay.get(3).getValue());
            attributesValidate.setValid(mHashMapWay.get(3).isValid());
            attributesValidateList.add(attributesValidate);
        }
        wayDataValidate.setAttributesValidate(attributesValidateList);
        requestWayInfo.setWayDataValidates(wayDataValidate);
        mISettingScreenPresenter.onUpdate(requestWayInfo);
    }


    private void onValidateWay() {
        RequestWayInfo requestWayInfo = new RequestWayInfo();
        RequestWayData wayDataValidate = new RequestWayData();
        wayDataValidate.setId(mWayCustomModel.getId());
        wayDataValidate.setProjectId(mWayCustomModel.getProjectId());
        wayDataValidate.setValid(mWayCustomModel.getStatus());
        List<AttributesValidate> attributesValidateList = new ArrayList<>();
        AttributesValidate attributesValidate = null;
        if (mHashMapWay.get(0) != null && !mHashMapWay.get(0).getKey().isEmpty()) {
            attributesValidate = new AttributesValidate();
            attributesValidate.setKey("footway");
            attributesValidate.setValue(mHashMapWay.get(0).getValue());
            attributesValidate.setValid(mHashMapWay.get(0).isValid());
            attributesValidateList.add(attributesValidate);
        }
        if (mHashMapWay.get(1) != null && !mHashMapWay.get(1).getKey().isEmpty()) {
            attributesValidate = new AttributesValidate();
            attributesValidate.setKey("highway");
            attributesValidate.setValue(mHashMapWay.get(1).getValue());
            attributesValidate.setValid(mHashMapWay.get(1).isValid());
            attributesValidateList.add(attributesValidate);
        }
        if (mHashMapWay.get(2) != null && !mHashMapWay.get(2).getKey().isEmpty()) {
            attributesValidate = new AttributesValidate();
            attributesValidate.setKey("incline");
            attributesValidate.setValue(mHashMapWay.get(2).getValue());
            attributesValidate.setValid(mHashMapWay.get(2).isValid());
            attributesValidateList.add(attributesValidate);
        }
        if (mHashMapWay.get(3) != null && !mHashMapWay.get(3).getKey().isEmpty()) {
            attributesValidate = new AttributesValidate();
            attributesValidate.setKey("width");
            attributesValidate.setValue(mHashMapWay.get(3).getValue());
            attributesValidate.setValid(mHashMapWay.get(3).isValid());
            attributesValidateList.add(attributesValidate);
        }
        wayDataValidate.setAttributesValidate(attributesValidateList);
        requestWayInfo.setWayDataValidates(wayDataValidate);
        mISettingScreenPresenter.onValidate(requestWayInfo);
    }

    @Override
    public void onUpdateDataReceived(ResponseUpdate responseUpdate) {
        Toast.makeText(SettingActivity.this, R.string.updated_info, Toast.LENGTH_SHORT).show();

        onValidateWay();
    }

    @Override
    public void onValidateDataReceived(ResponseWay responseUpdate) {
        Toast.makeText(SettingActivity.this, R.string.validated_way_info, Toast.LENGTH_SHORT).show();
        finish();

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
    public void onSuccessAsyncTask(String responseBody) {
        if (responseBody != null) {
            mChangeSetID = responseBody;
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
    public void OnIconCheckBoxOnClick(View v, int position, boolean isChecked) {
        switch (position) {
            case 0:
                changeCheckBox(isChecked,v);
                break;
            case 1:
                changeCheckBox(isChecked,v);
                break;
            case 2:
                changeCheckBox(isChecked,v);
                break;
            case 3:
                changeCheckBox(isChecked,v);
                break;

        }
    }

    private void changeCheckBox(boolean isChecked, View v){
        if(isChecked){
            ((CheckBox)v).setText(getResources().getString(R.string.verified));
            ((CheckBox)v).setTextColor(getResources().getColor(R.color.colorPrimary));

        }else {
            ((CheckBox)v).setText(getResources().getString(R.string.not_verify));
            ((CheckBox)v).setTextColor(getResources().getColor(R.color.colorTextGray));
        }
    }
}
