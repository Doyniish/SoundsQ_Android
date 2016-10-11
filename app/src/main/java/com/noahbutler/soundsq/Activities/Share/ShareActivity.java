package com.noahbutler.soundsq.Activities.Share;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.noahbutler.soundsq.Activities.LaunchActivity;
import com.noahbutler.soundsq.Constants;
import com.noahbutler.soundsq.GPS.GPSReceiver;
import com.noahbutler.soundsq.IO.IO;
import com.noahbutler.soundsq.Network.FCM.FCMInitiate;
import com.noahbutler.soundsq.Network.Sender;
import com.noahbutler.soundsq.R;
import com.noahbutler.soundsq.SoundPlayer.SoundQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;


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
    private ListView localQueueList;
    private LocalQueueListAdapter localQueueListAdapter;
    private GPSReceiver gpsReceiver;
    private String saveQueueID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        /* show loading ball */
        showLoading(true);

        /* get our FCM token */
        FCMInitiate fcmInitiate = new FCMInitiate(this);
        fcmInitiate.register();

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
        gpsReceiver = new GPSReceiver();
        gpsReceiver.initialize(this, true); //sends GPS to find local queues
        /* local queues sent to DownStreamReceiver from server */
    }

    private void handleSendSound(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        soundLink = sharedText.substring(sharedText.lastIndexOf("http"));
        /* remove https and replace with http */
        soundLink = soundLink.substring(5);
        soundLink = "http" + soundLink;

        //check to see if the user came from SoundsQ
        if(checkIsOwner()) {
            Sender.createExecute(Sender.SEND_SOUND, saveQueueID, soundLink);
            //send them back to the app
            Intent intent1 = new Intent(this, LaunchActivity.class);
            startActivity(intent1);
            ShareActivity.this.finish();
        }
        /* Wait for user to select local queue */
    }

    private void showLoading(boolean show) {
        if(show) {
            loadingBall = (ImageView)findViewById(R.id.share_loading_ball);
            loadingBall.setVisibility(View.VISIBLE);
        }else{
            loadingBall.setVisibility(View.INVISIBLE);
        }
    }

    private void showList(String list) {
        try {
            JSONObject jsonObject = new JSONObject(list);

            //convert to HashMap for easier data retrieval
            Iterator<String> keys = jsonObject.keys();
            HashMap<String, String> localQueues = new HashMap<>();

            while(keys.hasNext()) {
                String currentKey = keys.next();
                localQueues.put(currentKey, jsonObject.getString(currentKey));

            }

            //loading is completed
            showLoading(false);

            //display list of local queues
            constructLocalQueueList(localQueues);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void constructLocalQueueList(final HashMap<String, String> localQueues) {
        final String[] localQueueData = localQueues.keySet().toArray(new String[localQueues.keySet().size()]);

        localQueueList = (ListView)findViewById(R.id.local_queue_list);
        localQueueListAdapter = new LocalQueueListAdapter(this, localQueueData);
        localQueueList.setAdapter(localQueueListAdapter);

        localQueueList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Get queue selected by using the list of keys.
                String queue_id = localQueues.get(localQueueData[position]);

                IO.writeQueueID(getBaseContext().getFilesDir(), queue_id, false); //false: not owner

                Sender.createExecute(Sender.SEND_SOUND, queue_id, soundLink);
                ShareActivity.this.finish();
            }
        });

    }

    private boolean checkIsOwner() {
        JSONObject saveFileJSON = IO.readQueueID(getBaseContext().getFilesDir());

        if(saveFileJSON.has(IO.N_Key)) { //no file, fresh start
            return false;
        }else if(saveFileJSON.has(IO.Q_Key)) { //Check for saved Queue ID
            try {
                saveQueueID = saveFileJSON.getString(IO.Q_Key);
                //Check to see if we are an Owner
                if(saveFileJSON.getBoolean(IO.B_Key)) { //true: Owner of Queue
                    return true;
                }else{ //false: Spectator of Queue
                    return false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }else{//file is empty, Fresh Start
            return false;
        }

    }

    /** GPS Methods **/

    public void onStart() {
        super.onStart();
        gpsReceiver.onStart();
    }

    public void onResume() {
        super.onResume();
        gpsReceiver.onResume();
    }

    public void onPause() {
        super.onPause();
        gpsReceiver.onPause();
    }

    public void onStop() {
        super.onStop();
        gpsReceiver.onStop();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        gpsReceiver.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        gpsReceiver.onActivityResult(requestCode, resultCode, data);
    }

}
