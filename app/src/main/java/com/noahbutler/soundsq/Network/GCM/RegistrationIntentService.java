package com.noahbutler.soundsq.Network.GCM;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.noahbutler.soundsq.Constants;
import com.noahbutler.soundsq.R;

/**
 * Created by NoahButler on 9/17/15.
 */
public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";


    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e("Intent", "starting intent");

        try {

            InstanceID instanceID = InstanceID.getInstance(this);
            Log.e("token", "attempting to receive token");
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderID), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.d(TAG, "GCM Registration Token: " + token);

            setRegistrationToken(token);


        }catch(Exception e) {
            Log.e("error", "token failed");
            Log.e("error", "message: " + e.getMessage());
        }
    }

    private void setRegistrationToken(String token) {
        Constants.token = token;
    }

}
