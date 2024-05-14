package com.ugwebstudio.plasticwastemanagementapp.classes;

import android.app.Application;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Scheduler.schedulePickupCheck(this);
    }
}
