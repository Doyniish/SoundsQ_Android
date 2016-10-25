package com.noahbutler.soundsq.Fragments.MainFragmentLogic.StateController;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.noahbutler.soundsq.Activities.LaunchActivity;
import com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views.QueueBall;
import com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views.QueueView;
import com.noahbutler.soundsq.GPS.GPSReceiver;
import com.noahbutler.soundsq.IO.IO;
import com.noahbutler.soundsq.Network.Sender;
import com.noahbutler.soundsq.R;
import com.noahbutler.soundsq.SoundPlayer.SoundQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by gildaroth on 9/28/16.
 */

public class StateController {

    /*************/
    /* DEBUG TAG */
    private static final String TAG = "StateControl";


    /*********************************************/
    /* used to determine which state to start in */
    private static final int FRESH_START = 0;
    private static final int LOAD_SPECTATOR = 1;
    private static final int LOAD_OWNER = 2;


    /**********************************************/
    /* used to receive updates from other threads */
    public static Handler updateStream;
    /* Keys for message type */
    public static final int SPEC_QUEUE_LOADED = 0;
    public static final int OWNER_QUEUE_LOADED = 1;
    public static final int QUEUE_CREATED = 2;
    public static final int QUEUE_DELETED = 3;
    public static final int UPDATE_VIEW = 5;
    public static final int SC_REGISTER = 6;
    public static final String UPDATE_KEY = "update";


    /******************/
    /* Parent Objects */
    private View masterView;
    private Activity activity;


    /**********************/
    /* Classes controlled */
    private GPSReceiver gpsReceiver;
    private QueueBall queueBall;
    private QueueView queueView;


    public StateController(View masterView, Activity activity) {
        this.masterView = masterView;
        this.activity = activity;

        /* create our thread message receiver */
        updateStream = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {

                switch(msg.getData().getInt(UPDATE_KEY)) {
                    case SPEC_QUEUE_LOADED:
                        signal_LoadedSpectator();
                        break;
                    case OWNER_QUEUE_LOADED:
                        signal_LoadedOwner();
                        break;
                    case QUEUE_CREATED:
                        freshStartCompleted();
                        break;
                    case QUEUE_DELETED:
                        failedRequest();
                        break;
                    case UPDATE_VIEW:
                        String albumArt = msg.getData().getString(StateControllerMessage.A_Key);
                        String soundUrl = msg.getData().getString(StateControllerMessage.S_Key);
                        queueView.addArt(albumArt, soundUrl);
                        queueView.update();
                    case SC_REGISTER:
                        String registerUrl = msg.getData().getString(StateControllerMessage.Reg_Key);
                        displayRegisterPopUp(registerUrl);
                    default:
                        break;
                }
                return true;
            }
        });

        /* start the app */
        displayLoading();
        initializeLocation();
        initializeSoundsQ();
    }

    /** Setup for app **/

    private void initializeSoundsQ() {

        switch(checkFile(activity.getBaseContext().getFilesDir())) {
            case FRESH_START:
                UserState.STATE = UserState.OWNER;
                Log.e(TAG, "FRESH START");
                initializeLocation();
                createOwnerView(); //user needs a new queue
                break;
            case LOAD_OWNER:
                UserState.STATE = UserState.OWNER;
                Log.e(TAG, "LOAD_OWNER");
                //TODO: wait for queue to load from server
                loadOwnerView();
                break;
            case LOAD_SPECTATOR:
                Log.e(TAG, "LOAD_SPECTATOR");
                UserState.STATE = UserState.SPECTATOR;
                //TODO: wait for queue to load from server
                loadSpectatorView();
        }

        /* Showing loading ball until we get a message from the server */
    }

    private void initializeLocation() {
        gpsReceiver = new GPSReceiver();
        gpsReceiver.initialize(activity, false); //initialized from playing phone
    }

    /** Create SoundsQ Views **/

    private void createQueueBall() {
        queueBall = new QueueBall(masterView, activity);
        queueBall.instantiate();
    }

    private void createQueueView() {
        queueView = new QueueView(masterView, activity);
        queueView.instantiate();
    }

    private void displayLoading() {
        if(queueBall == null) {
            createQueueBall();
        }
        queueBall.setState(QueueBall.STATE_LOADING);
    }

    private void createOwnerView() {
        SoundQueue.PLAY = true; //Play songs
        SoundQueue.createQueue();
    }

    /** Load Saved Queue From Server **/

    private void loadSpectatorView() {
        SoundQueue.PLAY = false; //Don't try to play songs
        Sender.createExecute(Sender.REQUEST_QUEUE, SoundQueue.ID);
    }

    private void loadOwnerView() {
        Sender.createExecute(Sender.REQUEST_QUEUE, SoundQueue.ID);
    }

    /** Update Stream Methods **/

    private void signal_LoadedSpectator() {
        createQueueView();
        queueBall.setState(QueueBall.STATE_INVISIBLE);
    }

    private void signal_LoadedOwner() {
        createQueueView();
        queueBall.setState(QueueBall.STATE_INVISIBLE);
    }

    private void freshStartCompleted() {
        IO.writeQueueID(activity.getFilesDir(), SoundQueue.ID, true); //true, is owner
        createQueueView();
        queueBall.setState(QueueBall.STATE_INVISIBLE);
        Toast.makeText(activity.getBaseContext(), "Queue started! Now give it a name!", Toast.LENGTH_LONG).show();
    }

    private void failedRequest() {
        Toast.makeText(activity.getBaseContext(), "Joined queue no longer exists, Starting a fresh one...", Toast.LENGTH_LONG).show();
        createOwnerView();
    }

    private int checkFile(File directory) {

        //Read the saved File
        JSONObject checkFile = IO.readQueueID(directory);

        if(checkFile.has(IO.N_Key)) { //no file, fresh start
            return FRESH_START;
        }else if(checkFile.has(IO.Q_Key)) { //Check for saved Queue ID
            try {
                //Set our Global Queue ID as the saved Queue ID.
                SoundQueue.ID = checkFile.getString(IO.Q_Key);

                //Check to see if we are an Owner
                if(checkFile.getBoolean(IO.B_Key)) { //true: Owner of Queue
                    return LOAD_OWNER;
                }else{ //false: Spectator of Queue
                    return LOAD_SPECTATOR;
                }

            } catch (JSONException e) {
                e.printStackTrace();
                return FRESH_START;
            }
        }else{//file is empty, Fresh Start
            return FRESH_START;
        }
    }

    /** GPS Methods **/

    public void onStart() {
        if(UserState.STATE == UserState.OWNER) {
            gpsReceiver.onStart();
        }
    }

    public void onResume() {
        if(UserState.STATE == UserState.OWNER) {
            gpsReceiver.onResume();
        }
    }

    public void onPause() {
        if(UserState.STATE == UserState.OWNER) {
            gpsReceiver.onPause();
        }
    }

    public void onStop() {
        if(UserState.STATE == UserState.OWNER) {
            gpsReceiver.onStop();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(UserState.STATE == UserState.OWNER) {
            gpsReceiver.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(UserState.STATE == UserState.OWNER) {
            gpsReceiver.onActivityResult(requestCode, resultCode, data);
        }

        //sound cloud login check
        //if(data.hasExtra('code')) {
            Log.e(TAG, "results: " + data.toString());
        //}
    }

    public void setMenuView(View menuView) {

        if (UserState.STATE == UserState.OWNER) {

            ImageView home = (ImageView) menuView.findViewById(R.id.home_button);
            home.setClickable(true);
            home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    queueBall.setState(QueueBall.STATE_QUEUE_BALL);
                }
            });

            final TextView queueNameDisplay = (TextView) menuView.findViewById(R.id.queue_name_display);
            final EditText queueNameEdit = (EditText) menuView.findViewById(R.id.enter_queue_name);

            queueNameEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        // do your stuff here
                        String entry = queueNameEdit.getText().toString();
                        SoundQueue.NAME = entry;
                        Sender.createExecute(Sender.SEND_NAME, SoundQueue.NAME);
                        queueNameEdit.setClickable(false);
                        queueNameEdit.setVisibility(View.INVISIBLE);
                        queueNameDisplay.setText(entry);
                    }
                    return false;
                }
            });

        } else { // Spectator
            ImageView close = (ImageView) menuView.findViewById(R.id.close_button);
            close.setClickable(true);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IO.deleteQueueID(activity.getFilesDir());
                    //TODO: show rating view
                    activity.finish();
                }
            });

            TextView queueNameDisplay = (TextView) menuView.findViewById(R.id.queue_name_display_spec);
        }
    }

    public void displayRegisterPopUp(String register_url) {
        Uri uri = Uri.parse(register_url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        activity.startActivity(intent);
    }
}
