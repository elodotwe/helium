package com.jacobarau.helium;

import android.app.Application;

public class HeliumApplication extends Application {
    public static HeliumApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }
}
