package com.locationservices.jvanburen.locationservicessample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by jvanburen on 1/20/2015.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.map_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //LocationManager.getInstance().onActivityResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //LocationManager.getInstance().onActivityPause();
    }
}
