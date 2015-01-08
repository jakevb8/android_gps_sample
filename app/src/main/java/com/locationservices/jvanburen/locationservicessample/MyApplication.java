package com.locationservices.jvanburen.locationservicessample;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Created by jvanburen on 1/8/2015.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LocationManager.getInstance().onApplicationCreate(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        LocationManager.getInstance().onApplicationTerminate();
    }
}
