package com.disablerouting;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.disablerouting.Network.NetworkChangeReceiver;

@SuppressLint("Registered")
public class BaseActivityImpl extends AppCompatActivity implements NetworkChangeReceiver.ConnectionChangeListener {

    private NetworkChangeReceiver mNetworkChangeReceiver = new NetworkChangeReceiver();

    private BroadcastReceiver mNetworkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNetworkReceiver = new NetworkChangeReceiver();
        registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

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
        if(!isConnected) {
            Toast.makeText(this, getResources().getString(R.string.no_internet),Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        mNetworkChangeReceiver.setConnectionListener(this);
        IntentFilter intentFilter= new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(mNetworkChangeReceiver,intentFilter);
    }


    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(mNetworkChangeReceiver);
    }
}
