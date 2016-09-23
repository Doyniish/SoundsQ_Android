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
import com.noahbutler.soundsq.Constants;
import com.noahbutler.soundsq.Fragments.LocalQueuesFragment;
import com.noahbutler.soundsq.GPS.GPSReceiver;
import com.noahbutler.soundsq.IO.IO;
import com.noahbutler.soundsq.Network.GCM.RegistrationIntentService;
import com.noahbutler.soundsq.Network.Sender;
import com.noahbutler.soundsq.R;

/**
 * Created by NoahButler on 12/27/15.
 */
public class ShareActivity extends Activity {

    EditText enterQueueID;
    static String soundLink;
    String readQueueID;
    static FragmentManager fragmentManager;

    BroadcastReceiver mRegistrationBroadcastReceiver;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        /* get our GCM token */
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                Log.e("LaunchActivity", "Received Broadcast");
            }
        };

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

    public void register() {
        /* Start IntentService to register this application with GCM. */
        if (checkPlayServices()) {
            Log.e("LaunchActivity", "starting reg intent");
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
            Log.e("LaunchActivity", "handing off");
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

    private String checkQueueFile() {
        /* check for queue id saved on phone */
        String readQueueID = IO.readQueueID(getBaseContext().getFilesDir());
        /* look to see if anything in the file was read in */
        Log.e("R", readQueueID);
        if(!readQueueID.contentEquals("")) {
            return readQueueID;
        }
        return null;
    }

    private void displayAddQueueID() {
        //TODO: display QR Code Scanner

        enterQueueID = (EditText)findViewById(R.id.enter_queue_id_join);
        enterQueueID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutoSendChecker autoSendChecker = new AutoSendChecker();
                autoSendChecker.start();
            }
        });
    }

    public static void failedShare() {
        //TODO: display failed share alert
    }

    class AutoSendChecker extends Thread {

        @Override
        public void run() {
            super.run();

            //wait for the user to enter in a valid id
            while(enterQueueID.getText().length() < Constants.QUEUE_ID_LENGTH) {}

            //auto send
            Sender.createExecute(Sender.SEND_SOUND, enterQueueID.getText().toString(), soundLink);
            Toast.makeText(getBaseContext(), "Sound has been sent!", Toast.LENGTH_LONG).show();

            //add queue id to cache file for later use.
            IO.writeQueueID(getBaseContext().getFilesDir(), enterQueueID.getText().toString());

            //return to SoundCloud
            finish();
        }
    }
}
