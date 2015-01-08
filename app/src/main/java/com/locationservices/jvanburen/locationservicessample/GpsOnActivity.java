package com.locationservices.jvanburen.locationservicessample;

import android.app.Activity;

/**
 * Created by jvanburen on 1/8/2015.
 */
public class GpsOnActivity extends Activity {
    @Override
    protected void onResume() {
        super.onResume();
        LocationManager.getInstance().onActivityResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationManager.getInstance().onActivityPause();
    }
}
