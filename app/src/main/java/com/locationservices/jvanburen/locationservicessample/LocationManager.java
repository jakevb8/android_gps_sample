package com.locationservices.jvanburen.locationservicessample;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jvanburen on 1/8/2015.
 */
public class LocationManager implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static LocationManager _instance;

    protected static final String TAG = "LocationManager";
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private Context _context;
    private GoogleApiClient _googleApiClient;
    private LocationRequest _locationRequest;
    private Location _currentLocation;
    private Boolean _requestingLocationUpdates;
    private String _lastUpdateTime;

    private ArrayList<GpsListener> _gpsListeners = new ArrayList<>();

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
        _googleApiClient.connect();
    }

    public void onApplicationTerminate() {
        _gpsListeners.clear();
        if (_googleApiClient != null &&
                _googleApiClient.isConnected()) {
            _googleApiClient.disconnect();
        }
    }

    public void onActivityResume() {
        if (_googleApiClient.isConnected() && !_requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    public void onActivityPause() {
        _gpsListeners.clear();
        stopLocationUpdates();
    }

    private synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        _googleApiClient = new GoogleApiClient.Builder(_context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    private void createLocationRequest() {
        _locationRequest = new LocationRequest();
        _locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        _locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        _locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                _googleApiClient, _locationRequest, this);
        _requestingLocationUpdates = true;
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(_googleApiClient, this);
        _requestingLocationUpdates = false;
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");
        startLocationUpdates();
        _currentLocation = LocationServices.FusedLocationApi.getLastLocation(_googleApiClient);
        _lastUpdateTime = DateFormat.getTimeInstance().format(new Date());
    }

    @Override
    public void onLocationChanged(Location location) {
        _currentLocation = location;
        _lastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        for(GpsListener gpsListener: _gpsListeners) {
            gpsListener.onGpsChanged(location);
        }

        Toast.makeText(_context, _context.getResources().getString(R.string.location_updated_message),
                Toast.LENGTH_SHORT).show();
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

    public interface GpsListener{
        public void onGpsChanged(Location newLocation);
    }
}
