package com.noahbutler.soundsq.Fragments.MainFragmentLogic.StateController;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views.QueueBall;
import com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views.QueueView;
import com.noahbutler.soundsq.GPS.GPSReceiver;
import com.noahbutler.soundsq.IO.IO;
import com.noahbutler.soundsq.Network.Sender;
import com.noahbutler.soundsq.SoundPlayer.SoundQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by gildaroth on 9/28/16.
 */

public class StateController {

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

        updateStream = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {

                switch(msg.getData().getInt(UPDATE_KEY)) {
                    case SPEC_QUEUE_LOADED:
                        loadedSpectator();
                        break;
                    case OWNER_QUEUE_LOADED:
                        loadedOwner();
                        break;
                    case QUEUE_CREATED:
                        freshStartCompleted();
                        break;
                    case QUEUE_DELETED:
                        failedRequest();
                    default:
                        break;
                }

                return true;
            }
        });

        /* start the app */
        displayLoading();
        initializeSoundsQ();
    }

    private void createQueueBall() {
        queueBall = new QueueBall(masterView, activity);
        queueBall.instantiate();
    }

    private void createQueueView() {
        queueView = new QueueView(masterView, activity);
        queueView.instantiate();
    }

    private void displayLoading() {
        createQueueBall();
        queueBall.setState(QueueBall.STATE_LOADING);
    }

    /** Setup for app **/

    private void initializeSoundsQ() {

        switch(checkFile(activity.getBaseContext().getFilesDir())) {
            case FRESH_START:
                initializeLocation();
                createOwnerView(); //user needs a new queue
                break;
            case LOAD_OWNER:
                initializeLocation();
                //TODO: wait for queue to load from server
                loadOwnerView();
                break;
            case LOAD_SPECTATOR:
                //TODO: wait for queue to load from server
                loadSpectatorView();
        }

        /* Showing loading ball until we get a message from the server */
    }

    private void initializeLocation() {
        gpsReceiver = new GPSReceiver();
        gpsReceiver.initialize(activity, false); //initialized from playing phone
    }

    private void loadSpectatorView() {
        SoundQueue.PLAY = false; //Don't try to play songs
        Sender.createExecute(Sender.REQUEST_QUEUE, SoundQueue.ID);
    }

    private void loadOwnerView() {
        Sender.createExecute(Sender.REQUEST_QUEUE, SoundQueue.ID);
    }

    private void createOwnerView() {
        SoundQueue.createQueue();
        SoundQueue.PLAY = true; //Play songs
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

    /** Update Stream Methods **/

    public void loadedSpectator() {
        createQueueView();
        //TODO: display exit button on toolbar
        queueBall.setState(QueueBall.STATE_INVISIBLE);
    }

    public void loadedOwner() {
        createQueueView();
        //TODO: display normal toolbar, with created name in queue name field
        queueBall.setState(QueueBall.STATE_INVISIBLE);
    }

    public void freshStartCompleted() {
        createQueueView();
        //TODO: display normal toolbar, with "create name" in queue name field
        queueBall.setState(QueueBall.STATE_INVISIBLE);

    }

    public void failedRequest() {
        Toast.makeText(activity.getBaseContext(), "Joined queue no longer exists, Starting a fresh one...", Toast.LENGTH_LONG).show();
        createOwnerView();
    }

    /** GPS Methods **/

    public void onStart() {
        gpsReceiver.onStart();
    }

    public void onResume() {
        gpsReceiver.onResume();
    }

    public void onPause() {
        gpsReceiver.onPause();
    }

    public void onStop() {
        gpsReceiver.onStop();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        gpsReceiver.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
