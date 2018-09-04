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
import com.disablerouting.curd_operations.manager.ListGetWayManager;
import com.disablerouting.curd_operations.model.DataHolder;
import com.disablerouting.curd_operations.model.ListWayData;
import com.disablerouting.curd_operations.model.ResponseListWay;
import com.disablerouting.home.presenter.HomeScreenPresenter;
import com.disablerouting.home.presenter.IHomeScreenPresenter;
import com.disablerouting.home.presenter.IHomeView;
import com.disablerouting.login.LoginActivity;
import com.disablerouting.login.UserPreferences;
import com.disablerouting.route_planner.view.RoutePlannerActivity;
import com.disablerouting.sidemenu.view.ISideMenuFragmentCallback;
import com.disablerouting.utils.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivityImpl  implements ISideMenuFragmentCallback, IHomeView {

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

    final String[] locationPermissions = new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_NETWORK_STATE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        mIHomeScreenPresenter= new HomeScreenPresenter(this,new ListGetWayManager());
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
        Intent intent= new Intent(this, RoutePlannerActivity.class);
        startActivityForResult(intent,AppConstant.REQUEST_CODE_SCREEN);
    }

    @OnClick(R.id.btn_suggestion)
    void redirectSuggestions() {
        showSuggestionDialog();
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
        if(requestCode== AppConstant.REQUEST_CODE_SCREEN){
            if (resultCode == Activity.RESULT_OK) {
                mSideMenuFragment.onLogin();
            }
        }
        if (requestCode == AppConstant.REQUEST_CODE_LOGIN) {
            if (resultCode == Activity.RESULT_OK) {
                mSideMenuFragment.onLogin();
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
    
    private void redirectToSuggestionScreen(){
        if (UserPreferences.getInstance(this).getAccessToken() == null) {
            Intent intentLogin = new Intent(this, LoginActivity.class);
            startActivityForResult(intentLogin, AppConstant.REQUEST_CODE_LOGIN);
        }else {
            Intent intent = new Intent(this, RoutePlannerActivity.class);
            intent.putExtra("FromSuggestion", true);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(int close) {
        if(slideState) {
            mDrawerLayout.closeDrawer(Gravity.START);
        }
        super.onClick(close);
    }

    private void getWayListData(){
        mIHomeScreenPresenter.getListWays();
    }

    @Override
    public void onListWayReceived(ResponseListWay responseWay) {
        if(responseWay!=null) {
            for (int i = 0; i < responseWay.getWayData().size(); i++) {
                boolean isValidWay= Boolean.parseBoolean(responseWay.getWayData().get(i).getIsValid());
                if (isValidWay) {
                    mWayListValidatedData.add(responseWay.getWayData().get(i));
                } else {
                    mWayListNotValidatedData.add(responseWay.getWayData().get(i));
                }
            }
        }
        DataHolder.setDataValidate(mWayListValidatedData);
        DataHolder.setDataNotValidate(mWayListNotValidatedData);

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
}
