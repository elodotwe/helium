package com.jacobarau.helium;

import android.app.Application;
import android.os.Handler;

import com.jacobarau.helium.data.JData;
import com.jacobarau.helium.data.JDataListener;

public class HeliumApplication extends Application {
    public static HeliumApplication application;

    private Handler handler;
    public static JData<String, JDataListener<String>> data;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;

        handler = new Handler();
        data = new JData<>("foo");
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                data.setValue("bar " + System.currentTimeMillis());
                handler.postDelayed(this, 2000);
            }
        };
        handler.postDelayed(runnable, 5000);
    }
}
