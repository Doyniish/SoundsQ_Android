package com.noahbutler.soundsq.Fragments.MainFragmentLogic.StateController;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
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
    public static final int PLAY_UPDATE = 4;
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
    private TextView queueNameDisplay;
    private EditText queueNameEdit;

    public StateController(View masterView, final Activity activity) {
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
                        SoundQueue.saveState(activity.getFilesDir());
                        String albumArt = msg.getData().getString(StateControllerMessage.A_Key);
                        String soundUrl = msg.getData().getString(StateControllerMessage.S_Key);
                        queueView.addArt(albumArt, soundUrl);
                        queueView.update();
                        break;
                    case PLAY_UPDATE:
                        queueView.update();
                        break;
                    case SC_REGISTER:
                        String registerUrl = msg.getData().getString(StateControllerMessage.Reg_Key);
                        displayRegisterPopUp(registerUrl);
                        break;
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
        //IO.deleteQueueID(activity.getFilesDir());
        if(!SaveState.RESUMING) {
            switch (checkFile(activity.getBaseContext().getFilesDir())) {
                case FRESH_START:
                    UserState.STATE = UserState.OWNER;
                    Log.e(TAG, "FRESH START");
                    SoundQueue.PLAY = true;
                    initializeLocation();
                    createOwnerView(); //user needs a new queue
                    break;
                case LOAD_OWNER:
                    UserState.STATE = UserState.OWNER;
                    SoundQueue.PLAY = true;
                    Log.e(TAG, "LOAD_OWNER");
                    displayLoading();
                    loadOwnerView();
                    break;
                case LOAD_SPECTATOR:
                    Log.e(TAG, "LOAD_SPECTATOR");
                    UserState.STATE = UserState.SPECTATOR;
                    //TODO: wait for queue to load from server
                    loadSpectatorView();
            }
        }
        /* Showing loading ball until we get a message from the server */
    }

    private void initializeLocation() {
        if (!SaveState.RESUMING) {
            gpsReceiver = new GPSReceiver();
            gpsReceiver.initialize(activity, false); //initialized from playing phone
        }
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
        Log.e(TAG, "displaying loading queue ball from state controller");
        queueBall.setState(QueueBall.STATE_LOADING);
    }

    private void createOwnerView() {
        SoundQueue.PLAY = true; //Play songs
        SoundQueue.createQueue(true);
    }

    /** Load Saved Queue From Server **/

    private void loadSpectatorView() {
        SoundQueue.PLAY = false; //Don't try to play songs
        SoundQueue.CREATED = true;
        Sender.createExecute(Sender.REQUEST_QUEUE, SoundQueue.ID);
    }

    private void loadOwnerView() {
        SoundQueue.CREATED = true;
        Sender.createExecute(Sender.REQUEST_QUEUE, SoundQueue.ID);
    }

    /** Update Stream Methods **/

    private void signal_LoadedSpectator() {
        createQueueView();
        queueBall.setState(QueueBall.STATE_INVISIBLE);
        setQueueNameDisplayText();
    }

    private void signal_LoadedOwner() {
        createQueueView();
        Log.e(TAG, "Loaded Owner...");
        queueBall.setState(QueueBall.STATE_INVISIBLE);
        setQueueNameDisplayText();
        Toast.makeText(activity.getBaseContext(), "Your queue has been loaded...", Toast.LENGTH_LONG).show();
    }

    private void freshStartCompleted() {
        Log.e(TAG, "writing out queue id...");
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
            if (gpsReceiver != null) {
                gpsReceiver.onStart();
            }
        }
    }

    public void onResume(File directory) {
        displayLoading();
        if(UserState.STATE == UserState.OWNER) {
            gpsReceiver.onResume();
        }
        Log.v(TAG, "\n\nState Controller OnResume...\n\n");
        createQueueBall();
        if(queueView != null) {
            queueView.onResume(directory);
        }
    }

    public void onPause(File directory) {
        displayLoading();
        Log.v(TAG, "\n\nState Controller onPause...\n\n");
        if(UserState.STATE == UserState.OWNER) {
            gpsReceiver.onPause();
        }
        SaveState.onPause();
        if(queueView != null) {
            queueView.onPause(directory);
        }
    }

    public void onStop() {
        if(UserState.STATE == UserState.OWNER) {
            gpsReceiver.onStop();
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        if(queueView != null) {
            queueView.onSaveInstanceState(savedInstanceState);
        }
    }

    public void onSavedInstanceRestored(Bundle savedInstanceState) {
        if(queueView != null) {
            queueView.onSavedInstanceRestored(savedInstanceState);
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
            queueNameEdit = (EditText) menuView.findViewById(R.id.enter_queue_name);

            //check to see if we already have a name
            if (SoundQueue.NAME == null || SoundQueue.NAME.contentEquals("null")) {

                ImageView home = (ImageView) menuView.findViewById(R.id.home_button);
                home.setClickable(true);
                home.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        queueBall.setState(QueueBall.STATE_QUEUE_BALL);
                    }
                });

                queueNameDisplay = (TextView) menuView.findViewById(R.id.queue_name_display);


                queueNameEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {

                            String entry = queueNameEdit.getText().toString();
                            SoundQueue.NAME = entry;
                            Sender.createExecute(Sender.SEND_NAME, SoundQueue.NAME);
                            queueNameEdit.setClickable(false);
                            queueNameEdit.setVisibility(View.INVISIBLE);
                            queueNameDisplay.setText(entry);

                            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(queueNameEdit.getWindowToken(), 0);

                        }else if(actionId == EditorInfo.IME_ACTION_NEXT) {

                            String entry = queueNameEdit.getText().toString();
                            SoundQueue.NAME = entry;
                            Sender.createExecute(Sender.SEND_NAME, SoundQueue.NAME);
                            queueNameEdit.setClickable(false);
                            queueNameEdit.setVisibility(View.INVISIBLE);
                            queueNameDisplay.setText(entry);

                            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(queueNameEdit.getWindowToken(), 0);
                        }
                        return false;
                    }
                });
            }else{
                setQueueNameDisplayText();
            }

        } else { // Spectator
            ImageView close = (ImageView) menuView.findViewById(R.id.close_button);
            close.setClickable(true);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IO.deleteQueueID(activity.getFilesDir());
                    //TODO: show rating view
                    int pid = android.os.Process.myPid();
                    android.os.Process.killProcess(pid);
                }
            });

            queueNameDisplay = (TextView) menuView.findViewById(R.id.queue_name_display_spec);
            setQueueNameDisplayText();
        }
    }

    public void setQueueNameDisplayText() {
        if(SoundQueue.NAME != null && !SoundQueue.NAME.contentEquals("null")) {
            if(UserState.STATE == UserState.SPECTATOR) {
                queueNameDisplay.setText(SoundQueue.ID);
            }else {
                queueNameEdit.setClickable(false);
                queueNameEdit.setVisibility(View.INVISIBLE);
                queueNameDisplay.setText(SoundQueue.NAME);
                queueNameDisplay.invalidate();
            }
        }
    }

    public void displayRegisterPopUp(String register_url) {
        queueView.displayRegisterPopUp(register_url);
    }

    /**
     * Used by Registration View: catches back key clicks
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return queueView.onKeyDown(keyCode, event);
    }
}
