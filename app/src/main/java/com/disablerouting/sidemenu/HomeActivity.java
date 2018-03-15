package com.disablerouting.sidemenu;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.disablerouting.R;
import com.disablerouting.base.BaseActivityImpl;
import com.disablerouting.common.AppConstant;
import com.disablerouting.feedback.view.FeedbackActivity;
import com.disablerouting.route_planner.view.RoutePlannerActivity;
import com.disablerouting.sidemenu.view.ISideMenuFragmentCallback;
import com.disablerouting.utils.PermissionUtils;

public class HomeActivity extends BaseActivityImpl  implements ISideMenuFragmentCallback{

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.frame_drawer)
    FrameLayout navigationDrawerLayout;

    @BindView(R.id.navigation_btn)
    ImageButton mImageButtonNavigationMenu;


    private boolean slideState = false;

    final String[] locationPermissions = new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_NETWORK_STATE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        addNavigationMenu(navigationDrawerLayout, this);
        addListener();
        checkLocationStatus();

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
    public void clickNavigation(){
        if(slideState){
            mDrawerLayout.openDrawer(Gravity.START);
        }else{
            mDrawerLayout.openDrawer(Gravity.START);
        }
    }

    @Override
    public void onBackPressed() {
        if(slideState){
            mDrawerLayout.closeDrawer(Gravity.START);
            return;
        }
        super.onBackPressed();
    }

    @OnClick(R.id.btn_route_planner)
    void redirectRoutePlanner(){
        launchActivity(this, RoutePlannerActivity.class);
    }

    @OnClick(R.id.btn_suggestion)
    void redirectSuggestions(){
        launchActivity(this, FeedbackActivity.class);
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
                        } else {
                            finish();
                            break;
                        }
                    }
                }
            }
        }
    }
}
