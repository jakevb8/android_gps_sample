package com.locationservices.jvanburen.locationservicessample;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Created by jvanburen on 1/8/2015.
 */
public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks{

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
        LocationManager.getInstance().onApplicationCreate(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        LocationManager.getInstance().onApplicationTerminate();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        LocationManager.getInstance().onActivityResume();
    }

    @Override
    public void onActivityPaused(Activity activity) {
        LocationManager.getInstance().onActivityPause();
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
