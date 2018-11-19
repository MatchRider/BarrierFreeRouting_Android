package com.disablerouting.base;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.disablerouting.R;
import com.disablerouting.network.NetworkChangeReceiver;
import com.disablerouting.sidemenu.view.ISideMenuFragmentCallback;
import com.disablerouting.sidemenu.view.SideMenuFragment;
import com.disablerouting.widget.DRLoader;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.enums.SnackbarType;

@SuppressLint("Registered")
public class BaseActivityImpl extends AppCompatActivity implements UIBase, NetworkChangeReceiver.ConnectionChangeListener,
        ISideMenuFragmentCallback {

    private NetworkChangeReceiver mNetworkChangeReceiver = new NetworkChangeReceiver();
    private BroadcastReceiver mNetworkReceiver;
    private DRLoader mLoader;
    protected SideMenuFragment mSideMenuFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNetworkReceiver = new NetworkChangeReceiver();
        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));


    }

    @Override
    public void showProgress() {
        if (mLoader == null) {
            mLoader = new DRLoader(this);
            Window window = mLoader.getWindow();
            if (window != null) {
                window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            }
            mLoader.setCancelable(false);
        }

        if (mLoader != null && !mLoader.isShowing()) {
            mLoader.show();
        }
    }

    @Override
    public void hideProgress() {
        if (mLoader != null && mLoader.isShowing()) {
            mLoader.dismiss();
            mLoader = null;
        }
    }

    @Override
    public void showSnackBar(@StringRes int message, AppCompatActivity context) {
        if (message != 0) {
            Snackbar snackbar = Snackbar.with(getApplicationContext()).type(SnackbarType.MULTI_LINE).text(message);
            TextView textView = (TextView) snackbar.getRootView().findViewById(android.support.design.R.id.snackbar_text);
            textView.setMaxLines(5);
            snackbar.show(context);

        }
    }

    @Override
    public void showSnackBar(String message, AppCompatActivity context) {
        if (!message.equals("")) {
            Snackbar snackbar = Snackbar.with(getApplicationContext()).type(SnackbarType.MULTI_LINE).text(message);
            TextView textView = (TextView) snackbar.getRootView().findViewById(android.support.design.R.id.snackbar_text);
            textView.setMaxLines(5);
            snackbar.show(context);
        }
    }

    /**
     * @see UIBase
     */
    @Override
    public <T> void launchActivity(Activity _context, Class<T> cls) {
        if (_context != null) {
            Intent intent = new Intent(_context, cls);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_right_out);
            startActivity(intent);
        }
    }

    /**
     * @see UIBase
     */
    @Override
    public <T> void launchActivity(Intent intent) {
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_right_out);
        startActivity(intent);
    }


    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            Toast.makeText(this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mNetworkChangeReceiver.setConnectionListener(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(mNetworkChangeReceiver, intentFilter);
    }


    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(mNetworkChangeReceiver);
    }

    /**
     * Add side menu to your activity
     *
     * @param frameLayout take layout of frame
     */
    protected void addNavigationMenu(FrameLayout frameLayout, ISideMenuFragmentCallback listener) {
        mSideMenuFragment = SideMenuFragment.newInstance();
        mSideMenuFragment.setClickListener(listener);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(frameLayout.getId(), mSideMenuFragment);
        transaction.commit();
    }

    @Override
    public void onClick(int close) {
        switch (close) {
            case R.string.close_drawer:

                break;
        }
    }

    protected void addFragment(@IdRes int containerViewId,
                               @NonNull Fragment fragment,
                               @NonNull String fragmentTag) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(containerViewId, fragment, fragmentTag)
                .disallowAddToBackStack()
                .commit();
    }

    protected void replaceFragment(@IdRes int containerViewId,
                                   @NonNull Fragment fragment,
                                   @NonNull String fragmentTag,
                                   @Nullable String backStackStateName) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(containerViewId, fragment, fragmentTag)
                .addToBackStack(backStackStateName)
                .commit();
    }


}
