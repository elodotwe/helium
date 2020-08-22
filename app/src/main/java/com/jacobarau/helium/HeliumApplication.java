package com.jacobarau.helium;

import android.app.Application;

public class HeliumApplication extends Application {
    public static Wiring wiring;

    @Override
    public void onCreate() {
        super.onCreate();
        HeliumApplication.wiring = new Wiring(getApplicationContext());
    }
}
