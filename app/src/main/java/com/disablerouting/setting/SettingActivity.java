package com.disablerouting.setting;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.disablerouting.setting.presenter.ISettingScreenPresenter;
import com.disablerouting.setting.presenter.SettingScreenPresenter;
import com.github.scribejava.core.model.Verb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SettingActivity extends BaseActivityImpl implements SettingAdapterListener , ISettingView ,
        IAysncTaskOsm {

    @BindView(R.id.rcv_setting)
    RecyclerView mRecyclerView;

    final int OPEN_SETTING_TYPE = 200;
    private SettingAdapter mSettingAdapter;
    private ResponseWay mResponseWayData;
    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, String> mHashMapWay = new HashMap<>();
    private ISettingScreenPresenter mISettingScreenPresenter;

    private String mURLChangeSet= ApiEndPoint.SANDBOX_BASE_URL_OSM+"changeset/create";
    private String mURLNodeSet= ApiEndPoint.SANDBOX_BASE_URL_OSM+"node/create";
    private AsyncTaskOsmApi asyncTaskOsmApi;
    private String mChangeSetID;
    List<RequestTag> mRequestTagList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        mISettingScreenPresenter= new SettingScreenPresenter(this,new UpdateWayManager(), new ValidateWayManager());
        if(getIntent().hasExtra(AppConstant.WAY_DATA)){
            mResponseWayData= getIntent().getParcelableExtra(AppConstant.WAY_DATA);
            ResponseWay responseWay= new ResponseWay();
            responseWay.setWayData(mResponseWayData.getWayData());
            if(mResponseWayData!=null) {
                getDataFromWay();

               /* Gson gson = new GsonBuilder().create();
                String json = gson.toJson(responseWay);
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    String jsonString= jsonObj.toString();
                    JsonToXml jsonToXml = new JsonToXml.Builder(jsonString)
                            .forceAttribute("/WayData")
                            .build();
                    Log.e("XML",jsonToXml.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }*/

            }
        }

        callToGetChangeSet();
        setUpRecyclerView();

    }

    /**
     * Api Call To Create Change Set
     */
    private void callToGetChangeSet(){
        RequestCreateChangeSet requestCreateChangeSet= new RequestCreateChangeSet();
        List<RequestTag> list = new ArrayList<>();
        RequestTag requestTag = new RequestTag("created_by","JOSM 1.61");
        list.add(requestTag);
        requestTag = new RequestTag("comment","Just adding some streetnames");
        list.add(requestTag);
        requestCreateChangeSet.setRequestTag(list);

        String string="<osm><changeset><tag k=\"created_by\" v=\"JOSM 1.61\"/><tag k=\"comment\" v=\"Just adding some streetnames\"/></changeset></osm>";
        OauthData oauthData= new OauthData(Verb.PUT,string,mURLChangeSet);
        asyncTaskOsmApi= new AsyncTaskOsmApi(SettingActivity.this,oauthData,this);
        asyncTaskOsmApi.execute("");
    }

    private void callToSetChangeSet(){
        if(mChangeSetID!=null) {
            RequestCreateNode requestCreateNode = new RequestCreateNode();
            String latitude = String.valueOf(AppData.getNewInstance().getCurrentLoc().latitude);
            String longitude = String.valueOf(AppData.getNewInstance().getCurrentLoc().longitude);
            Node node = new Node(mChangeSetID, latitude, longitude);

            RequestTag requestTag = new RequestTag("note", "Just a node");
            mRequestTagList.add(requestTag);
            node.setRequestTagList(mRequestTagList);
            requestCreateNode.setNode(node);

            String stringBuilder = "<osm><node changeset=" + "\""+String.valueOf(mChangeSetID) +"\""+ " "+
                    "lat=" + "\""+String.valueOf(latitude)+"\"" +" "+
                    "lon=" +"\""+ String.valueOf(longitude) +"\""+ ">" +
                    "<tag k=\"note\" v=\"Just a node\"/></node></osm>";


            String string="<osm>\n" +
                    " <node changeset=\"112100\" lat=\"28.584220243018713\" lon=\"77.13020324707031\">\n" +
                    "   <tag k=\"note\" v=\"Just a node\"/>\n" +
                    " </node>\n" +
                    "</osm>";

            OauthData oauthData= new OauthData(Verb.PUT, stringBuilder,mURLNodeSet);
            new AsyncTaskOsmApi(SettingActivity.this,oauthData,this).execute("");
        }
    }
    /**
     * Setup recycler view
     */
    private void setUpRecyclerView() {
        mSettingAdapter = new SettingAdapter(this,prepareListData(), this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mSettingAdapter);
        if(mHashMapWay !=null){
            mSettingAdapter.setSelectionMap(mHashMapWay);
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
    public void onDoneClick(){
        if(mResponseWayData!=null){
            onUpdateWay();
        }else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_SETTING_TYPE) {
            if (resultCode == RESULT_OK) {
                String dataString = data.getStringExtra(AppConstant.SETTING_ITEM_SELECTED_RECIEVE);
                int positionClicked = -1;
                mHashMapWay.put(positionClicked, dataString);
                mSettingAdapter.setSelectionMap(mHashMapWay);
                mSettingAdapter.notifyDataSetChanged();
            }
        }
    }


    private void getDataFromWay(){
        for (int i=0;i<mResponseWayData.getWayData().size();i++){
            for (int j=0;j<mResponseWayData.getWayData().get(i).getAttributesList().size();j++){
                switch (mResponseWayData.getWayData().get(i).getAttributesList()
                        .get(j).getKey()){

                    case "incline":
                        mHashMapWay.put(2,mResponseWayData.getWayData().get(i).getAttributesList().get(j).getValue());

                        break;

                    case "footway":
                        mHashMapWay.put(0,mResponseWayData.getWayData().get(i).getAttributesList().get(j).getValue());
                        break;

                    case "highway":
                        mHashMapWay.put(1,mResponseWayData.getWayData().get(i).getAttributesList().get(j).getValue());
                        break;

                    case "width":
                        mHashMapWay.put(3,mResponseWayData.getWayData().get(i).getAttributesList().get(j).getValue());
                        break;

                    default:

                }
            }
        }
    }



    private void onUpdateWay(){
        WayDataValidate wayDataValidate= new WayDataValidate();
        wayDataValidate.setId(mResponseWayData.getWayData().get(0).getId());
        AttributesValidate attributesValidate= new AttributesValidate();
        if(mHashMapWay.get(0)!=null && !mHashMapWay.get(0).isEmpty()) {
            attributesValidate.setFootWay(mHashMapWay.get(0));
        }
        if(mHashMapWay.get(1)!=null && !mHashMapWay.get(1).isEmpty()) {
            attributesValidate.setHighWay(mHashMapWay.get(1));
        }

        if(mHashMapWay.get(2)!=null && !mHashMapWay.get(2).isEmpty()) {
            attributesValidate.setIncline(mHashMapWay.get(2));
        }

        if(mHashMapWay.get(3)!=null && !mHashMapWay.get(3).isEmpty()) {
            attributesValidate.setHWidth(mHashMapWay.get(3));
        }
        wayDataValidate.setAttributesValidate(attributesValidate);
        List<WayDataValidate> listRequestValidate= new ArrayList<>();
        listRequestValidate.add(wayDataValidate);
        RequestValidate requestValidate= new RequestValidate();
        requestValidate.setWayDataValidates(listRequestValidate);
        mISettingScreenPresenter.onUpdate(requestValidate);
    }



    private void onValidateWay(){
        WayDataValidate wayDataValidate= new WayDataValidate();
        wayDataValidate.setId(mResponseWayData.getWayData().get(0).getId());
        AttributesValidate attributesValidate= new AttributesValidate();
        if(mHashMapWay.get(0)!=null && !mHashMapWay.get(0).isEmpty()) {
            attributesValidate.setFootWay(mHashMapWay.get(0));
        }
        if(mHashMapWay.get(1)!=null && !mHashMapWay.get(1).isEmpty()) {
            attributesValidate.setHighWay(mHashMapWay.get(1));
        }

        if(mHashMapWay.get(2)!=null && !mHashMapWay.get(2).isEmpty()) {
            attributesValidate.setIncline(mHashMapWay.get(2));
        }

        if(mHashMapWay.get(3)!=null && !mHashMapWay.get(3).isEmpty()) {
            attributesValidate.setHWidth(mHashMapWay.get(3));
        }
        wayDataValidate.setAttributesValidate(attributesValidate);
        List<WayDataValidate> listRequestValidate= new ArrayList<>();
        listRequestValidate.add(wayDataValidate);
        RequestValidate requestValidate= new RequestValidate();
        requestValidate.setWayDataValidates(listRequestValidate);
        mISettingScreenPresenter.onValidate(requestValidate);
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
        if(responseBody!=null) {
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
}
