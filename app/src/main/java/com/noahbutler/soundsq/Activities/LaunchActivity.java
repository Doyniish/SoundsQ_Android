package com.noahbutler.soundsq.Activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.noahbutler.soundsq.Constants;
import com.noahbutler.soundsq.Fragments.LaunchActivityFragment;
import com.noahbutler.soundsq.Fragments.QueueFragment;
import com.noahbutler.soundsq.IO.IO;
import com.noahbutler.soundsq.Network.GCM.RegistrationIntentService;
import com.noahbutler.soundsq.Network.Sender;
import com.noahbutler.soundsq.R;
import com.noahbutler.soundsq.SoundPlayer.SoundPlayerController;
import com.noahbutler.soundsq.SoundPlayer.SoundQueue;
import com.noahbutler.soundsq.ThreadUtils.MessageHandler;

import java.io.File;


public class LaunchActivity extends Activity {

    BroadcastReceiver mRegistrationBroadcastReceiver;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private String inQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        /* Sound Player Controller Creator */
        SoundPlayerController.setContext(getBaseContext());

        /* check to see if we need to display queue that is running */
        if(savedInstanceState != null && savedInstanceState.containsKey("show_queue")) {
            if(savedInstanceState.getBoolean("show_queue")) { // app needs to go directly to the queue that is playing
                getFragmentManager().beginTransaction().replace(R.id.main_content_area, new QueueFragment()).commit();
            }
        }

        /* create our thread handler */
        Constants.handler = new MessageHandler();

        /* get our GCM token */
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                Log.e("LaunchActivity", "Received Broadcast");
            }
        };

        register();

        //check if user has already sent songs to a queue. If so, load that queue
        inQueue = checkInQueue(getBaseContext().getFilesDir());
        if(inQueue.contentEquals("error")) {
        } else if(inQueue.contentEquals("")) {
            //queue is originates from this phone, play it
            SoundQueue.PLAY = true;
            SoundQueue.createQueue();
        } else {
            //TODO: display that we are loading the queue
            //queue is just being viewed from this phone, just display it.
            SoundQueue.PLAY = false;
            Sender sender = new Sender();
            sender.execute(Sender.REQUEST_QUEUE, inQueue);
        }


        /* hand of to Launch Activity Fragment */
        getFragmentManager().beginTransaction().replace(R.id.main_content_area, new LaunchActivityFragment()).commit();
        /* Test queue fragment */
        //getFragmentManager().beginTransaction().replace(R.id.main_content_area, new QueueFragment()).commit();
    }

    private void register() {
        /* Start IntentService to register this application with GCM. */
        if (checkPlayServices()) {
            Log.e("LaunchActivity", "starting reg intent");
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
            Log.e("LaunchActivity", "handing off");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter("registrationComplete"));
    }

    @Override
    protected void onPause() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("show_queue", SoundQueue.hasQueuedSounds());
        onSaveInstanceState(bundle);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_launch, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i("LaunchActivity", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private String checkInQueue(File directory) {
        return IO.readQueueID(directory);
    }
}
