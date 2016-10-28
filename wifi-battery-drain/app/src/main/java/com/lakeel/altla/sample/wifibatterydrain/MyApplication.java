package com.lakeel.altla.sample.wifibatterydrain;

import com.squareup.leakcanary.LeakCanary;

import android.app.Application;

public final class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // LeakCanary
        LeakCanary.install(this);
    }
}
