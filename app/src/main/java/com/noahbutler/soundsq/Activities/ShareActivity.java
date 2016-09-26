package com.noahbutler.soundsq.Activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;
import com.noahbutler.soundsq.Constants;
import com.noahbutler.soundsq.Fragments.LocalQueuesFragment;
import com.noahbutler.soundsq.GPS.GPSReceiver;
import com.noahbutler.soundsq.IO.IO;
import com.noahbutler.soundsq.Network.GCM.RegistrationIntentService;
import com.noahbutler.soundsq.Network.Sender;
import com.noahbutler.soundsq.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by NoahButler on 12/27/15.
 */
public class ShareActivity extends Activity {

    EditText enterQueueID;
    static String soundLink;
    static FragmentManager fragmentManager;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        /* get our FCM token */
        register();

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        //used to display local queues list from DownStreamReceiver
        fragmentManager = getFragmentManager();

        if(Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendSound(intent); // Handle text being sent
            }
        }

        /* Send GPS to find local Queues */
        GPSReceiver gpsReceiver = new GPSReceiver();
        gpsReceiver.initialize(this, true); //send GPS to find local queues
        /* local queues sent to DownStreamReceiver from server */
    }

    private void register() {
        /* Start IntentService to register this application with GCM. */
        if (checkPlayServices()) {
            Log.e("LaunchActivity", "starting reg intent");
            Constants.token = FirebaseInstanceId.getInstance().getToken();
            Log.e("LaunchActivity", "TOKEN: " + Constants.token);
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Toast.makeText(getBaseContext(), "Play Services required...closing now", Toast.LENGTH_LONG);
                Log.i("LaunchActivity", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void handleSendSound(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);

        if (sharedText != null) {
            soundLink = sharedText.substring(sharedText.lastIndexOf("http"));
            cleanLink();

            /* Display QR Code Scanner and Queue ID TextInput */
            //displayAddQueueID();

            //check for existing queue id
            //readQueueID = checkQueueFile();

            /* check for errors or if it actually exists */
            //if(readQueueID != null) {
            //    displayAutoSend();
            //}else { //no cached queue id
            //    Toast.makeText(getBaseContext(), "Join a SoundQ session!", Toast.LENGTH_LONG).show();
            //}
        }
    }

    public static void showList() {
        LocalQueuesFragment localQueuesFragment = new LocalQueuesFragment();
        localQueuesFragment.soundLink = soundLink;
        localQueuesFragment.show(fragmentManager, "");
    }

    private void cleanLink() {
        /* remove https and replace with http */
        soundLink = soundLink.substring(5);
        soundLink = "http" + soundLink;
    }

    private JSONObject checkQueueFile() {
        /* check for queue id saved on phone */
        JSONObject readQueueID = IO.readQueueID(getBaseContext().getFilesDir());
        /* look to see if anything in the file was read in */

        if(!readQueueID.has(IO.Q_Key)) {
            try {
                Log.d("R", readQueueID.getString(IO.Q_Key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return readQueueID;
        }
        return null;
    }

    public static void failedShare() {
        //TODO: display failed share alert
    }
}
