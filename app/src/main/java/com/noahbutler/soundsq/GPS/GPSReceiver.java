package com.noahbutler.soundsq.GPS;

import android.Manifest;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.noahbutler.soundsq.Network.Sender;

/**
 * Created by gildaroth on 9/21/16.
 */
public class GPSReceiver implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private Activity activity;

    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 1;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private boolean isSharing;
    public static final String TAG = GPSReceiver.class.getSimpleName();

    public void initialize(Activity activity, boolean isSharing) {
        Log.d(TAG, "Initializing GPS");
        googleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(120 * 1000)
                .setFastestInterval(60 * 1000);

        this.activity = activity;
        this.isSharing = isSharing;

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
                    PERMISSION_ACCESS_COARSE_LOCATION);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            } else {
                handleNewLocation(location);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // All good!
                } else {
                    Toast.makeText(activity, "Need your location!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(activity, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    public void onResume() {
        googleApiClient.connect();
    }

    public void onPause() {
        if(googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        String lat = String.valueOf(location.getLatitude());
        String lon = String.valueOf(location.getLongitude());

        if(isSharing) {
            Log.d(TAG, "Looking for local queues...");
            Sender.createExecute(Sender.SENDER_GPS, lat, lon);
        }else {
            Log.d(TAG, "Sending queue\'s gps");
            Sender.createExecute(Sender.QUEUE_GPS, lat, lon);
        }

    }
}
