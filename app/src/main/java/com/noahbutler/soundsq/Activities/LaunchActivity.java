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
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.noahbutler.soundsq.Constants;
import com.noahbutler.soundsq.Fragments.QueueBallFragment;
import com.noahbutler.soundsq.Network.GCM.RegistrationIntentService;
import com.noahbutler.soundsq.R;
import com.noahbutler.soundsq.SoundPlayer.SoundPlayerController;
import com.noahbutler.soundsq.SoundPlayer.SoundQueue;
import com.noahbutler.soundsq.ThreadUtils.MessageHandler;

public class LaunchActivity extends Activity {

    BroadcastReceiver mRegistrationBroadcastReceiver;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        /* Sound Player Controller Creator */
        SoundPlayerController.createController(getBaseContext());

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

        /* hand of to Queue Ball Fragment */
        getFragmentManager().beginTransaction().replace(R.id.main_content_area, new QueueBallFragment()).commit();
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
                Toast.makeText(getBaseContext(), "Play Services required...closing now", Toast.LENGTH_LONG);
                Log.i("LaunchActivity", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
