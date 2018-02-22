package com.disablerouting.sidemenu;

import android.os.Bundle;
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
import com.disablerouting.route_planner.view.RoutePlannerActivity;
import com.disablerouting.sidemenu.view.ISideMenuFragmentCallback;
import com.disablerouting.suggestions.SuggestionsActivity;

public class HomeActivity extends BaseActivityImpl  implements ISideMenuFragmentCallback{

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.frame_drawer)
    FrameLayout navigationDrawerLayout;

    @BindView(R.id.navigation_btn)
    ImageButton mImageButtonNavigationMenu;

    private boolean slideState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        addNavigationMenu(navigationDrawerLayout, this);
        addListener();

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
        launchActivity(this, SuggestionsActivity.class);
    }
}
