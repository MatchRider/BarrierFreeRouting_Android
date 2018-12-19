package com.disablerouting.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;
import com.disablerouting.R;
import com.disablerouting.api.ErrorResponse;
import com.disablerouting.common.AppConstant;
import com.disablerouting.common.MessageEvent;
import com.disablerouting.curd_operations.WayDataPreference;
import com.disablerouting.curd_operations.manager.IListGetWayResponseReceiver;
import com.disablerouting.curd_operations.manager.ListGetWayManager;
import com.disablerouting.curd_operations.model.*;
import com.disablerouting.osm_activity.manager.OSMManager;
import com.disablerouting.osm_activity.model.GetOsmData;
import com.disablerouting.osm_activity.presenter.IOSMResponseReceiver;
import com.disablerouting.utils.Utility;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class OsmDataService extends IntentService implements IOSMResponseReceiver , IListGetWayResponseReceiver {

    public static boolean isSyncInProgress = false;
    public boolean isOSMDataSynced = false;
    public boolean isLISTDatSynced = false;

    private List<ListWayData> mWayListValidatedData = new ArrayList<>();
    private List<ListWayData> mWayListNotValidatedData = new ArrayList<>();
    private List<NodeReference> mNodeListValidatedData = new ArrayList<>();
    private List<NodeReference> mNodeListNotValidatedData = new ArrayList<>();

    private List<ListWayData> mWayListValidatedDataOSM = new ArrayList<>();
    private List<ListWayData> mWayListNotValidatedDataOSM = new ArrayList<>();
    private List<NodeReference> mNodeListValidatedDataOSM = new ArrayList<>();
    private List<NodeReference> mNodeListNotValidatedDataOSM = new ArrayList<>();
    public String stringType;
    private boolean mIsShownList=false;
    private boolean mIsShownOSM=false;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public OsmDataService(String name) {
        super(name);
    }

    public OsmDataService() {
        super("");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        assert intent != null;
        stringType = intent.getStringExtra(AppConstant.RUN_API);
        switch (stringType){
            case AppConstant.RUN_BOTH:
                callListAPI();
                callOSMAPI();
                isOSMDataSynced = true;
                isLISTDatSynced = true;
                isSyncInProgress = true;
                break;
            case AppConstant.RUN_LIST:
                callListAPI();
                isLISTDatSynced = true;
                isSyncInProgress = true;
                break;
            case AppConstant.RUN_OSM:
                callOSMAPI();
                isOSMDataSynced = true;
                isSyncInProgress = true;
                break;
        }

    }

    public void callOSMAPI(){
        OSMManager osmManager = new OSMManager();
        osmManager.getOSMData(this, this);
    }
    public void callListAPI(){
        ListGetWayManager listGetWayManager = new ListGetWayManager();
        listGetWayManager.getListWay(this);
    }

    @Override
    public void onSuccessOSM(final String responseBody) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (responseBody != null) {
                    GetOsmData getOsmData = null;
                    try {
                        getOsmData = Utility.convertDataIntoModel(responseBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    assert getOsmData != null;
                    List<NodeReference> nodeReferenceList = new ArrayList<>();
                    NodeReference nodeReference;
                    if (getOsmData.getOSM() != null && getOsmData.getOSM().getNode() != null) {
                        for (int i = 0; i < getOsmData.getOSM().getNode().size(); i++) {
                            nodeReference = new NodeReference();
                            nodeReference.setOSMNodeId(getOsmData.getOSM().getNode().get(i).getID());
                            nodeReference.setLat(getOsmData.getOSM().getNode().get(i).getLatitude());
                            nodeReference.setLon(getOsmData.getOSM().getNode().get(i).getLongitude());
                            nodeReference.setVersion(getOsmData.getOSM().getNode().get(i).getVersion());
                            nodeReference.setIsForData(AppConstant.OSM_DATA);
                            List<Attributes> attributesList = new ArrayList<>();
                            Attributes attributes;
                            if (getOsmData.getOSM().getNode().get(i).getTag() != null &&
                                    getOsmData.getOSM().getNode().get(i).getTag().size() != 0) {
                                for (int k = 0; k < getOsmData.getOSM().getNode().get(i).getTag().size(); k++) {
                                    attributes = new Attributes();
                                    attributes.setKey(getOsmData.getOSM().getNode().get(i).getTag().get(k).getK());
                                    attributes.setValue(getOsmData.getOSM().getNode().get(i).getTag().get(k).getV());
                                    attributes.setValid(false);
                                    attributesList.add(attributes);
                                    nodeReference.setAttributes(attributesList);
                                }
                            }
                            if(!nodeReference.getOSMNodeId().equalsIgnoreCase("4315552310")) {
                                nodeReferenceList.add(nodeReference);
                            }
                        }
                    }

                    List<ListWayData> listWayDataListCreated = new ArrayList<>();
                    ListWayData listWayData;

                    if (getOsmData.getOSM() != null && getOsmData.getOSM().getWays() != null) {
                        for (int i = 0; i < getOsmData.getOSM().getWays().size(); i++) {
                            listWayData = new ListWayData();

                            listWayData.setOSMWayId(getOsmData.getOSM().getWays().get(i).getID());
                            listWayData.setVersion(getOsmData.getOSM().getWays().get(i).getVersion());
                            listWayData.setIsValid("false");
                            listWayData.setColor(Utility.randomColor());
                            listWayData.setIsForData(AppConstant.OSM_DATA);
                            ParcelableArrayList stringListCoordinates;

                            List<NodeReference> nodeReferencesWay = new ArrayList<>();
                            List<ParcelableArrayList> coordinatesList = new LinkedList<>();

                            for (int j = 0; getOsmData.getOSM().getWays().get(i).getNdList() != null &&
                                    getOsmData.getOSM().getWays().get(i).getNdList().size() != 0 &&
                                    j < getOsmData.getOSM().getWays().get(i).getNdList().size(); j++) {

                                for (int k = 0; k < nodeReferenceList.size(); k++) {
                                    if (getOsmData.getOSM().getWays().get(i).getNdList().get(j).getRef()
                                            .equalsIgnoreCase(nodeReferenceList.get(k).getOSMNodeId())) {

                                        nodeReferencesWay.add(nodeReferenceList.get(k));
                                        stringListCoordinates = new ParcelableArrayList();
                                        stringListCoordinates.add(0, nodeReferenceList.get(k).getLat());
                                        stringListCoordinates.add(1, nodeReferenceList.get(k).getLon());
                                        coordinatesList.add(stringListCoordinates);
                                        break;

                                    }
                                }

                            }
                            listWayData.setCoordinates(coordinatesList);
                            listWayData.setNodeReference(nodeReferencesWay);

                            List<Attributes> attributesArrayListWay = new ArrayList<>();
                            for (int j = 0; getOsmData.getOSM().getWays().get(i).getTagList() != null &&
                                    getOsmData.getOSM().getWays().get(i).getTagList().size() != 0 &&
                                    j < getOsmData.getOSM().getWays().get(i).getTagList().size(); j++) {
                                Attributes attributesWay = new Attributes();

                                attributesWay.setKey(getOsmData.getOSM().getWays().get(i).getTagList().get(j).getK());
                                attributesWay.setValue(getOsmData.getOSM().getWays().get(i).getTagList().get(j).getV());
                                attributesWay.setValid(false);
                                attributesArrayListWay.add(attributesWay);
                            }
                            listWayData.setAttributesList(attributesArrayListWay);
                            if(!listWayData.getOSMWayId().equalsIgnoreCase("4305292042")) {
                                listWayDataListCreated.add(listWayData);
                            }
                        }
                    }

                    Log.e("List", String.valueOf(listWayDataListCreated.size()));
                    ResponseListWay responseListWay = new ResponseListWay();
                    responseListWay.setWayData(listWayDataListCreated);
                    if (listWayDataListCreated.size() > 0) {
                        responseListWay.setStatus(true);
                    } else {
                        responseListWay.setStatus(false);
                    }
                    createListData(responseListWay, true);
                }

            }
        });

    }

    public void createListData(ResponseListWay responseWay, boolean isForOsm) {
        if (!isForOsm) {
            mWayListValidatedData.clear();
            mWayListNotValidatedData.clear();
            mNodeListValidatedData.clear();
            mNodeListNotValidatedData.clear();

            for (int i = 0; i < responseWay.getWayData().size(); i++) {
                responseWay.getWayData().get(i).setmIndex(i);
                boolean isValidWay = Boolean.parseBoolean(responseWay.getWayData().get(i).getIsValid());
                if (isValidWay) {
                    mWayListValidatedData.add(responseWay.getWayData().get(i));
                } else {
                    mWayListNotValidatedData.add(responseWay.getWayData().get(i));
                }
                for (int j = 0; j < responseWay.getWayData().get(i).getNodeReference().size(); j++) {
                    responseWay.getWayData().get(i).getNodeReference().get(j).setmIndex(j);
                    if (responseWay.getWayData().get(i).getNodeReference().get(j).getAttributes() != null) {
                        for (int k = 0; k < responseWay.getWayData().get(i).getNodeReference().get(j).getAttributes().size(); k++) {
                            if (!responseWay.getWayData().get(i).getNodeReference().get(j).getAttributes().get(k).isValid()) {
                                if (!Utility.isListContainId(mNodeListNotValidatedData, responseWay.getWayData().get(i).getNodeReference()
                                        .get(j).getAPINodeId())) {
                                    mNodeListNotValidatedData.add(responseWay.getWayData().get(i).getNodeReference().get(j));

                                }

                            } else {
                                if (!Utility.isListContainId(mNodeListValidatedData, responseWay.getWayData().get(i).getNodeReference()
                                        .get(j).getAPINodeId())) {
                                    mNodeListValidatedData.add(responseWay.getWayData().get(i).getNodeReference().get(j));
                                }
                            }
                        }
                    }
                }
            }
            if (WayDataPreference.getInstance(this) != null) {
                WayDataPreference.getInstance(this).saveValidateWayData(mWayListValidatedData);
                WayDataPreference.getInstance(this).saveNotValidatedWayData(mWayListNotValidatedData);
                WayDataPreference.getInstance(this).saveValidateDataNode(mNodeListValidatedData);
                WayDataPreference.getInstance(this).saveNotValidateDataNode(mNodeListNotValidatedData);

            }
            isLISTDatSynced =false;
        }
        if (isForOsm) {
            mWayListValidatedDataOSM.clear();
            mWayListNotValidatedDataOSM.clear();
            mNodeListValidatedDataOSM.clear();
            mNodeListNotValidatedDataOSM.clear();

            for (int i = 0; i < responseWay.getWayData().size(); i++) {
                responseWay.getWayData().get(i).setmIndex(i);
                boolean isValidWay = Boolean.parseBoolean(responseWay.getWayData().get(i).getIsValid());
                if (isValidWay) {
                    mWayListValidatedDataOSM.add(responseWay.getWayData().get(i));
                } else {
                    mWayListNotValidatedDataOSM.add(responseWay.getWayData().get(i));
                }

                for (int j = 0; j < responseWay.getWayData().get(i).getNodeReference().size(); j++) {
                    responseWay.getWayData().get(i).getNodeReference().get(j).setmIndex(j);
                    if (responseWay.getWayData().get(i).getNodeReference().get(j).getAttributes() != null) {

                        for (int k = 0; k < responseWay.getWayData().get(i).getNodeReference().get(j).getAttributes().size(); k++) {
                            if (!responseWay.getWayData().get(i).getNodeReference().get(j).getAttributes().get(k).isValid()) {
                                mNodeListNotValidatedDataOSM.add(responseWay.getWayData().get(i).getNodeReference().get(j));

                            } else {
                                mNodeListValidatedDataOSM.add(responseWay.getWayData().get(i).getNodeReference().get(j));

                            }
                        }
                    }
                }
            }
            if (WayDataPreference.getInstance(this) != null) {
                WayDataPreference.getInstance(this).saveValidateWayDataOSM(mWayListValidatedDataOSM);
                WayDataPreference.getInstance(this).saveNotValidatedWayDataOSM(mWayListNotValidatedDataOSM);
                WayDataPreference.getInstance(this).saveValidateDataNodeOSM(mNodeListValidatedDataOSM);
                WayDataPreference.getInstance(this).saveNotValidateDataNodeOSM(mNodeListNotValidatedDataOSM);

            }
            isOSMDataSynced =false;
        }

        switch (stringType){
            case AppConstant.RUN_BOTH:
                if(!isLISTDatSynced && !mIsShownList){
                    EventBus.getDefault().post(new MessageEvent("LIST_DATA"));
                    mIsShownList=true;
                }
                if(!isOSMDataSynced && !mIsShownOSM){
                    EventBus.getDefault().post(new MessageEvent("OSM_DATA"));
                    mIsShownOSM=true;

                }
                if((!isLISTDatSynced && !isOSMDataSynced)){
                    isSyncInProgress=false;
                }
                break;
            case AppConstant.RUN_LIST:
                isSyncInProgress = (isLISTDatSynced);
                EventBus.getDefault().post(new MessageEvent("LIST_DATA"));
                break;
            case AppConstant.RUN_OSM:
                isSyncInProgress = (isOSMDataSynced);
                EventBus.getDefault().post(new MessageEvent("OSM_DATA"));
                break;
        }

    }

    @Override
    public void onFailureOSM(@NonNull ErrorResponse errorResponse) {
        Toast.makeText(this, R.string.unable_to_get_data, Toast.LENGTH_SHORT).show();
        isOSMDataSynced = false;
        isSyncInProgress =false;
    }

    @Override
    public void onSuccessGetList(final ResponseListWay responseWay) {
        if (responseWay != null) {
            if (responseWay.isStatus()) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        createListData(responseWay, false);
                    }
                });
            } else {
                if (responseWay.getError() != null && responseWay.getError().get(0) != null &&
                        responseWay.getError().get(0).getMessage() != null) {
                    Toast.makeText(this, responseWay.getError().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onFailureGetList(@NonNull ErrorResponse errorResponse) {
        Toast.makeText(this, R.string.unable_to_get_data, Toast.LENGTH_SHORT).show();
        isLISTDatSynced = false;
        isSyncInProgress =false;
    }
}
