package com.noahbutler.soundsq.Network.FCM;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;
import com.noahbutler.soundsq.Constants;

/**
 * Created by gildaroth on 10/3/16.
 *
 * FCM Initiate handles all functionality
 * in regards to Firebase Cloud Messaging
 * start up and id retrieval.
 */

public class FCMInitiate {

    /*************/
    /* DEBUG TAG */
    private static final String TAG = "FCMInitiate";

    /********************/
    /* static variables */
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;


    /*******************/
    /* Local variables */
    Activity activity;


    public FCMInitiate(Activity activity) {
        this.activity = activity;
    }

    public void register() {
        if (checkPlayServices()) {
            Constants.token = FirebaseInstanceId.getInstance().getToken();
            Log.e(TAG, "FCM TOKEN: " + Constants.token);
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Toast.makeText(activity.getBaseContext(), "Play Services required...closing now", Toast.LENGTH_LONG);
                Log.i("LaunchActivity", "This device is not supported.");
                activity.finish();
            }
            return false;
        }
        return true;
    }

}
