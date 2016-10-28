package com.lakeel.altla.sample.wifibatterydrain;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

final class WifiScanner {

    private static final String TAG = WifiScanner.class.getSimpleName();

    private final Context mContext;

    private WifiManager mWifiManager;

    private Timer mTimer = new Timer(true);

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Scanned.");
        }
    };

    WifiScanner(@NonNull Context context) {
        mContext = context;
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        mContext.registerReceiver(mBroadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    void start(int interval) {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mWifiManager.startScan();
            }
        }, interval, interval);
    }

    void stop() {
        mTimer.cancel();
        mContext.unregisterReceiver(mBroadcastReceiver);
    }
}
