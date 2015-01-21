package com.locationservices.jvanburen.locationservicessample;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity {
    protected static final String TAG = "location-updates-sample";
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    protected Location mCurrentLocation;

    // UI Widgets.
    protected TextView mLastUpdateTimeTextView;
    protected TextView mLatitudeTextView;
    protected TextView mLongitudeTextView;

    protected Boolean mRequestingLocationUpdates;

    protected String mLastUpdateTime;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        Button getLocationButton = (Button) findViewById(R.id.get_location_button);

        mLatitudeTextView = (TextView) findViewById(R.id.latitude_text);
        mLongitudeTextView = (TextView) findViewById(R.id.longitude_text);
        mLastUpdateTimeTextView = (TextView) findViewById(R.id.last_update_time_text);

        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";

        findViewById(R.id.gps_on_activity_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, GpsOnActivity.class);
                startActivity(intent);
            }
        });
        final Button gpsListenerButton = (Button)findViewById(R.id.gps_listener_button);
        gpsListenerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gpsListenerButton.getText().toString().equalsIgnoreCase("Activate Listener")) {
                    LocationManager.getInstance().addGpsListener(_gpsListener);
                    gpsListenerButton.setText("Deactivate Listener");
                } else {
                    LocationManager.getInstance().removeGpsListener(_gpsListener);
                    gpsListenerButton.setText("Activate Listener");
                }
            }
        });
        findViewById(R.id.no_gps_activity_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, GpsOffActivity.class);
                startActivity(intent);
            }
        });
        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentLocation = LocationManager.getInstance().getLastLocation();
                updateCurrentLocaiton();
            }
        });
    }

    private LocationManager.GpsListener _gpsListener = new LocationManager.GpsListener() {
        @Override
        public void onGpsChanged(Location newLocation) {
            mCurrentLocation = newLocation;
            updateCurrentLocaiton();
        }
    };

    private void updateCurrentLocaiton() {
        if (mCurrentLocation != null) {
            mLastUpdateTime = LocationManager.getInstance().getLastUpdateTime();
            updateUI();
            mMap.clear();
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
        }
    }

    private void updateUI() {
        if (mCurrentLocation != null) {
            mLatitudeTextView.setText(String.valueOf(mCurrentLocation.getLatitude()));
            mLongitudeTextView.setText(String.valueOf(mCurrentLocation.getLongitude()));
            mLastUpdateTimeTextView.setText(mLastUpdateTime);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        //LocationManager.getInstance().onActivityResume();
        updateCurrentLocaiton();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //LocationManager.getInstance().onActivityPause();
        LocationManager.getInstance().removeGpsListener(_gpsListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

//    @Override
//    public void onLocationChanged(Location location) {
//        mCurrentLocation = location;
//        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
//        updateUI();
//        Toast.makeText(this, getResources().getString(R.string.location_updated_message),
//                Toast.LENGTH_SHORT).show();
//    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }
}
