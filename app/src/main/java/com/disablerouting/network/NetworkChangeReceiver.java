package com.disablerouting.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private ConnectionChangeListener mConnectionChangeListener;

    public void setConnectionListener(ConnectionChangeListener mConnectionChangeListener) {
        this.mConnectionChangeListener = mConnectionChangeListener;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (intent.getExtras() != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            assert cm != null;
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            if (mConnectionChangeListener != null) {
                mConnectionChangeListener.onNetworkConnectionChanged(isConnected);
            }
        }
    }

    public interface ConnectionChangeListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }


}