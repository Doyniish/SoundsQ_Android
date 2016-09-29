package com.noahbutler.soundsq.Activities.Share;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;
import com.noahbutler.soundsq.Constants;
import com.noahbutler.soundsq.GPS.GPSReceiver;
import com.noahbutler.soundsq.IO.IO;
import com.noahbutler.soundsq.Network.Sender;
import com.noahbutler.soundsq.R;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by NoahButler on 12/27/15.
 *
 * Share Activity is displayed when
 * a user shares a Sound from SoundCloud
 *
 */
public class ShareActivity extends Activity {

    /*************/
    /* DEBUG TAG */
    private static final String TAG = "Share_Activity";


    /**************************/
    /* Play Service Variables */
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;


    /**********************************************/
    /* used to receive updates from other threads */
    public static Handler updateStream;
    /* Keys for message type */
    public static final int LOCAL_QUEUES_LOADED = 0;
    public static final String UPDATE_KEY = "update";
    public static final String LOCAL_LIST = "local_list";

    /*******************/
    /* Local Variables */
    private String soundLink;
    private ImageView loadingBall;
    private TextView loadingText;
    private ListView localQueueList;
    private LocalQueueListAdapter localQueueListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        /* show loading ball */
        showLoading(true);

        /* get our FCM token */
        register();

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if(Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendSound(intent); // Clean up link to our liking.
            }
        }

        updateStream = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch(msg.getData().getInt(UPDATE_KEY)) {
                    case LOCAL_QUEUES_LOADED:
                        //populate list
                        showList(msg.getData().getString(LOCAL_LIST));
                        break;
                }
                return false;
            }
        });

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
        soundLink = sharedText.substring(sharedText.lastIndexOf("http"));
        /* remove https and replace with http */
        soundLink = soundLink.substring(5);
        soundLink = "http" + soundLink;
        /* Wait for user to select local queue */
    }

    private void showLoading(boolean show) {
        if(show) {
            loadingBall = (ImageView)findViewById(R.id.share_loading_ball);
            loadingBall.setVisibility(View.VISIBLE);
            loadingText = (TextView)findViewById(R.id.share_loading_text);
        }else{
            loadingBall.setVisibility(View.INVISIBLE);
            loadingText.setVisibility(View.INVISIBLE);
        }
    }

    private void showList(String list) {
        try {
            JSONObject jsonObject = new JSONObject(list);
            //TODO: decode json

            //Display our list
            showLoading(false);
            //TODO: make sure we are displaying queue names and not ids
            String[] localQueues = (String[])list.keySet().toArray();
            constructLocalQueueList(localQueues);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void constructLocalQueueList(final String[] localQueues) {
        localQueueList = (ListView)findViewById(R.id.local_queue_list);
        localQueueList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String queue_id = localQueues[position];
                //Save queue_id that they are send so that they can view it in the app
                IO.writeQueueID(getBaseContext().getFilesDir(), queue_id, false); //false: not owner
                Sender.createExecute(Sender.SEND_SOUND, queue_id, soundLink);
            }
        });

        localQueueListAdapter = new LocalQueueListAdapter(this, localQueues);
        localQueueList.setAdapter(localQueueListAdapter);
    }
}
