package com.noahbutler.soundsq.Network.FCM;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.noahbutler.soundsq.Constants;
import com.noahbutler.soundsq.Network.Sender;

/**
 * Created by gildaroth on 9/23/16.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "FIRE BASE ID";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        Constants.token = refreshedToken;
        Sender.createExecute(Sender.SEND_TOKEN);
    }

}
