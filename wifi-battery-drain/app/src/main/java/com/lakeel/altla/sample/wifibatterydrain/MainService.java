package com.lakeel.altla.sample.wifibatterydrain;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public final class MainService extends Service {

    // 15 sec
    public static final int DEFAULT_INTERVAL = 15000;

    private static final String TAG = MainService.class.getSimpleName();

    private static final String INTENT_EXTRA_INT_INTERVAL = "interval";

    private static final int NOTIFICATION_ID = 1;

    private WifiScanner mWifiScanner;

    @NonNull
    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, MainService.class);
    }

    @NonNull
    public static Intent createIntent(@NonNull Context context, int interval) {
        Intent intent = createIntent(context);
        intent.putExtra(MainService.INTENT_EXTRA_INT_INTERVAL, interval);
        return intent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        mWifiScanner = new WifiScanner(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        int interval = intent.getIntExtra(INTENT_EXTRA_INT_INTERVAL, DEFAULT_INTERVAL);
        Log.d(TAG, "Interval = " + interval);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, MainActivity.createStartIntent(this), 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_warning_white_24dp)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_text))
                .setContentIntent(pendingIntent)
                .build();

        startForeground(NOTIFICATION_ID, notification);
        mWifiScanner.start(interval);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        mWifiScanner.stop();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return null;
    }
}
