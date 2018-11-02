package com.disablerouting.home;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.base.BaseActivityImpl;
import com.disablerouting.common.AppConstant;
import com.disablerouting.curd_operations.WayDataPreference;
import com.disablerouting.curd_operations.manager.ListGetWayManager;
import com.disablerouting.curd_operations.model.*;
import com.disablerouting.home.presenter.HomeScreenPresenter;
import com.disablerouting.home.presenter.IHomeScreenPresenter;
import com.disablerouting.home.presenter.IHomeView;
import com.disablerouting.login.LoginActivity;
import com.disablerouting.login.UserPreferences;
import com.disablerouting.osm_activity.manager.OSMManager;
import com.disablerouting.osm_activity.model.GetOsmData;
import com.disablerouting.route_planner.view.RoutePlannerActivity;
import com.disablerouting.sidemenu.view.ISideMenuFragmentCallback;
import com.disablerouting.utils.PermissionUtils;
import com.disablerouting.utils.Utility;

import java.io.IOException;
import java.util.*;

public class HomeActivity extends BaseActivityImpl implements ISideMenuFragmentCallback, IHomeView {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.frame_drawer)
    FrameLayout navigationDrawerLayout;

    @BindView(R.id.navigation_btn)
    ImageButton mImageButtonNavigationMenu;

    private IHomeScreenPresenter mIHomeScreenPresenter;
    private boolean slideState = false;
    private List<ListWayData> mWayListValidatedData = new ArrayList<>();
    private List<ListWayData> mWayListNotValidatedData = new ArrayList<>();
    private List<NodeReference> mNodeListValidatedData = new ArrayList<>();
    private List<NodeReference> mNodeListNotValidatedData = new ArrayList<>();

    private List<ListWayData> mWayListValidatedDataOSM = new ArrayList<>();
    private List<ListWayData> mWayListNotValidatedDataOSM = new ArrayList<>();
    private List<NodeReference> mNodeListValidatedDataOSM = new ArrayList<>();
    private List<NodeReference> mNodeListNotValidatedDataOSM = new ArrayList<>();

    final String[] locationPermissions = new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_NETWORK_STATE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        mIHomeScreenPresenter = new HomeScreenPresenter(this, new ListGetWayManager(), new OSMManager(), this);
        addNavigationMenu(navigationDrawerLayout, this);
        addListener();
        checkLocationStatus();
        getWayListData();
    }


    /**
     * Check location services status
     */
    protected void checkLocationStatus() {
        if (!PermissionUtils.isPermissionAllowed(this, android.Manifest.permission_group.LOCATION)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this, locationPermissions, AppConstant.REQUEST_CODE);
            }
        }
    }

    /**
     * Add Listener for drawer
     */
    private void addListener() {
        mDrawerLayout.addDrawerListener(new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                slideState = false;
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                slideState = true;
                mSideMenuFragment.notifyList();
            }
        });
    }


    @OnClick(R.id.navigation_btn)
    public void clickNavigation() {
        if (slideState) {
            mDrawerLayout.openDrawer(Gravity.START);
        } else {
            mDrawerLayout.openDrawer(Gravity.START);
        }
    }

    @Override
    public void onBackPressed() {
        if (slideState) {
            mDrawerLayout.closeDrawer(Gravity.START);
            return;
        }
        super.onBackPressed();
    }

    @OnClick(R.id.btn_route_planner)
    void redirectRoutePlanner() {
        Intent intent = new Intent(this, RoutePlannerActivity.class);
        startActivityForResult(intent, AppConstant.REQUEST_CODE_SCREEN);
    }

    @OnClick(R.id.btn_suggestion)
    void redirectSuggestions() {
        redirectToSuggestionScreen();
        //showSuggestionDialog();
    }

    @OnClick(R.id.btn_osm)
    void redirectOSM() {
        redirectToOSMScreen();
       /* if (UserPreferences.getInstance(this).getAccessToken() == null) {
            Toast.makeText(this, "Login Required", Toast.LENGTH_SHORT).show();
        } else {
            mIHomeScreenPresenter.getOSMData();
        }*/
    }

    /**
     * Result when user give permission or not
     *
     * @param requestCode  Request code
     * @param permissions  Type of permission
     * @param grantResults Results of permission
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case AppConstant.REQUEST_CODE: {
                for (String permission : permissions) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                        finish();
                        break;
                    } else {
                        if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                            Log.e("allowed", permission);
                        } else {
                            openSettingDialog();
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * SHo setting dialog for location
     */
    protected void openSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.txt_dialog_message_enable_location);
        builder.setMessage(R.string.message_gps);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startSettingActivity();
            }
        });
        Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }

    /**
     * Open setting activity
     */
    protected void startSettingActivity() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, AppConstant.SETTING_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstant.SETTING_REQUEST_CODE) {
            checkLocationStatus();
        }
        if (requestCode == AppConstant.REQUEST_CODE_SCREEN) {
            if (resultCode == Activity.RESULT_OK) {
                mSideMenuFragment.onLogin();
            }
        }
        if (requestCode == AppConstant.REQUEST_CODE_LOGIN) {
            if (resultCode == Activity.RESULT_OK) {
                mSideMenuFragment.onLogin();
                if (UserPreferences.getInstance(this).isUserLoggedIn()) {
                    getWayListData();
                }
            }
        }
    }

    private void showSuggestionDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        View customView = layoutInflater.inflate(R.layout.suggestion_pop_up, null);
        Button btnYes = (Button) customView.findViewById(R.id.btn_yes);
        Button btnNo = (Button) customView.findViewById(R.id.btn_no);
        builder.setView(customView);

        final Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                redirectToSuggestionScreen();
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    private void redirectToSuggestionScreen() {
        if (UserPreferences.getInstance(this).getAccessToken() == null) {
            Intent intentLogin = new Intent(this, LoginActivity.class);
            startActivityForResult(intentLogin, AppConstant.REQUEST_CODE_LOGIN);
        } else {
            Intent intent = new Intent(this, RoutePlannerActivity.class);
            intent.putExtra("FromSuggestion", true);
            startActivity(intent);
        }
    }

    private void redirectToOSMScreen() {
        if (UserPreferences.getInstance(this).getAccessToken() == null) {
            Intent intentLogin = new Intent(this, LoginActivity.class);
            startActivityForResult(intentLogin, AppConstant.REQUEST_CODE_LOGIN);
        } else {
            Intent intent = new Intent(this, RoutePlannerActivity.class);
            intent.putExtra("FromOSM", true);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(int close) {
        if (slideState) {
            mDrawerLayout.closeDrawer(Gravity.START);
        }
        super.onClick(close);
    }

    /**
     * API CALL TO GET LIST OF WAY DATA
     */
    private void getWayListData() {
        mIHomeScreenPresenter.getListWays();
        //Once when completed
        mIHomeScreenPresenter.getOSMData();

    }

    @Override
    public void onListWayReceived(ResponseListWay responseWay) {
        if (responseWay != null) {
            if (responseWay.isStatus()) {
                createListData(responseWay, false);
            } else {
                if (responseWay.getError() != null && responseWay.getError().get(0) != null &&
                        responseWay.getError().get(0).getMessage() != null) {
                    Toast.makeText(this, responseWay.getError().get(0).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onOSMDataReceived(String responseBody) {
        if (responseBody != null) {
            GetOsmData getOsmData = null;
            try {
                getOsmData = Utility.convertDataIntoModel(responseBody);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Log.e("Nodes", String.valueOf(getOsmData.getOSM().getNode().size()));
            assert getOsmData != null;
            Log.e("Ways", String.valueOf(getOsmData.getOSM().getWays().size()));

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
                    Attributes attributes = null;
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
                    nodeReferenceList.add(nodeReference);
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
                    listWayDataListCreated.add(listWayData);
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

    public void createListData(ResponseListWay responseWay, boolean isForOsm) {
        if (!isForOsm) {
            mWayListValidatedData.clear();
            mWayListNotValidatedData.clear();
            mNodeListValidatedData.clear();
            mNodeListNotValidatedData.clear();

            for (int i = 0; i < responseWay.getWayData().size(); i++) {
                boolean isValidWay = Boolean.parseBoolean(responseWay.getWayData().get(i).getIsValid());
                if (isValidWay) {
                    mWayListValidatedData.add(responseWay.getWayData().get(i));
                } else {
                    mWayListNotValidatedData.add(responseWay.getWayData().get(i));
                }
                for (int j = 0; j < responseWay.getWayData().get(i).getNodeReference().size(); j++) {
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

        }
        if (isForOsm) {
            mWayListValidatedDataOSM.clear();
            mWayListNotValidatedDataOSM.clear();
            mNodeListValidatedDataOSM.clear();
            mNodeListNotValidatedDataOSM.clear();

            for (int i = 0; i < responseWay.getWayData().size(); i++) {

                boolean isValidWay = Boolean.parseBoolean(responseWay.getWayData().get(i).getIsValid());
                boolean isHaveSeparateGeometry = false;
                boolean isHaveKeyHighway = false;
                boolean isHaveKeyFootWay = false;
                boolean isSideWalkPartOfWay = false;
                boolean isSideWalkPartOfWayNOKey = false;
                for (int k = 0; k < responseWay.getWayData().get(i).getAttributesList().size(); k++) {

                    String key = responseWay.getWayData().get(i).getAttributesList().get(k).getKey();
                    String value = responseWay.getWayData().get(i).getAttributesList().get(k).getValue();
                    if (key.equalsIgnoreCase(AppConstant.KEY_HIGHWAY) && value.equalsIgnoreCase(AppConstant.KEY_FOOTWAY)) {
                        isHaveKeyHighway = true;
                    }
                    if (key.equalsIgnoreCase(AppConstant.KEY_FOOTWAY) && value.equalsIgnoreCase(AppConstant.KEY_SIDEWALK)) {
                        isHaveKeyFootWay = true;
                    }
                    if (isHaveKeyHighway && isHaveKeyFootWay) {
                        isHaveSeparateGeometry = true;
                    }
                    if (key.equalsIgnoreCase(AppConstant.KEY_SIDEWALK)) {
                        isSideWalkPartOfWay = true;
                    }
                    if (key.equalsIgnoreCase(AppConstant.KEY_SIDEWALK) && value.equalsIgnoreCase("NO")) {
                        isSideWalkPartOfWayNOKey = true;
                    }

                }
                if ((isHaveSeparateGeometry || isSideWalkPartOfWay) && !isSideWalkPartOfWayNOKey) {
                    if (isValidWay) {
                        mWayListValidatedDataOSM.add(responseWay.getWayData().get(i));
                    } else {
                        mWayListNotValidatedDataOSM.add(responseWay.getWayData().get(i));
                    }
                }
                for (int j = 0; j < responseWay.getWayData().get(i).getNodeReference().size(); j++) {
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
            Date currentTime = Calendar.getInstance().getTime();
            Log.e("Time End", String.valueOf(currentTime));

            // redirectToOSMScreen();
        }
    }

    @Override
    public void onFailure(String error) {
        Toast.makeText(HomeActivity.this, R.string.unable_to_get_data, Toast.LENGTH_SHORT).show();
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
    public void onDestroy() {
        super.onDestroy();
        hideLoader();
    }
}
