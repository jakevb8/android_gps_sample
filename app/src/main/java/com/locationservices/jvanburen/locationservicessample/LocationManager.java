package com.locationservices.jvanburen.locationservicessample;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jvanburen on 1/8/2015.
 */
public class LocationManager implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static final long FORGROUND_UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long BACKGROUND_UPDATE_INTERVAL_IN_MILLISECONDS = 30000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            FORGROUND_UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    protected static final String TAG = "LocationManager";
    private static LocationManager _instance;
    private Context _context;
    private GoogleApiClient _googleApiClient;
    private LocationRequest _locationRequest;
    private static Location _currentLocation;
    private Boolean _requestingLocationUpdates;
    private static String _lastUpdateTime;
    private PendingIntent _locationReceiverPendingIntent;

    private static ArrayList<GpsListener> _gpsListeners = new ArrayList<>();

    private LocationManager() {
    }

    public static LocationManager getInstance() {
        if (_instance == null) {
            _instance = new LocationManager();
        }
        return _instance;
    }

    public Location getLastLocation() {
        return _currentLocation;
    }

    public String getLastUpdateTime() {
        return _lastUpdateTime;
    }

    public void addGpsListener(GpsListener gpsListener) {
        _gpsListeners.add(gpsListener);
    }

    public void removeGpsListener(GpsListener gpsListener) {
        _gpsListeners.remove(gpsListener);
    }

    public void onApplicationCreate(Context context) {
        _context = context;
        buildGoogleApiClient();
        Log.d(TAG, "onApplicationCreate");
    }

    public void onApplicationTerminate() {
        _gpsListeners.clear();
        if (_googleApiClient != null &&
                _googleApiClient.isConnected()) {
            _googleApiClient.disconnect();
        }
        stopLocationUpdates();
        Log.d(TAG, "onApplicationTerminate");
    }

    public void onActivityResume() {
        createLocationRequest(FORGROUND_UPDATE_INTERVAL_IN_MILLISECONDS);
       _googleApiClient.connect();
    }

    public void onActivityPause() {
        createLocationRequest(BACKGROUND_UPDATE_INTERVAL_IN_MILLISECONDS);
        _googleApiClient.connect();
    }

    private synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        _googleApiClient = new GoogleApiClient.Builder(_context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void createLocationRequest(long updateTime) {
        _locationRequest = new LocationRequest();
        _locationRequest.setInterval(updateTime);
        _locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        _locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        Intent intent = new Intent(_context.getResources().getString(R.string.location_received_broadcast));
        _locationReceiverPendingIntent = PendingIntent.getBroadcast(_context, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                _googleApiClient, _locationRequest, _locationReceiverPendingIntent);
        _requestingLocationUpdates = true;
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(_googleApiClient, _locationReceiverPendingIntent);
        _requestingLocationUpdates = false;
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");
        if(_locationReceiverPendingIntent != null) {
            stopLocationUpdates();
        }
        startLocationUpdates();
        _currentLocation = LocationServices.FusedLocationApi.getLastLocation(_googleApiClient);
        _lastUpdateTime = DateFormat.getTimeInstance().format(new Date());

        if (_googleApiClient.isConnected() && !_requestingLocationUpdates) {
            startLocationUpdates();
        }
        _googleApiClient.disconnect();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
        _googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    public void onDisconnected() {
        Log.i(TAG, "Disconnected");
    }

    public interface GpsListener {
        public void onGpsChanged(Location newLocation);
    }

    public static class LocationReceiver extends BroadcastReceiver {

        public LocationReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Location location = (Location) intent.getExtras().get(FusedLocationProviderApi.KEY_LOCATION_CHANGED);
                _currentLocation = location;
                _lastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                for (GpsListener gpsListener : _gpsListeners) {
                    gpsListener.onGpsChanged(location);
                }

                Log.d(TAG, location.toString());
                Toast.makeText(context, context.getResources().getString(R.string.location_updated_message),
                        Toast.LENGTH_SHORT).show();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
