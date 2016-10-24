package com.noahbutler.soundsq.Network;

import android.util.Log;

import com.noahbutler.soundsq.Fragments.MainFragmentLogic.StateController.StateController;
import com.noahbutler.soundsq.Fragments.MainFragmentLogic.StateController.StateControllerMessage;
import com.noahbutler.soundsq.GPS.GPSReceiver;
import com.noahbutler.soundsq.SoundPlayer.SoundQueue;

/**
 * Created by gildaroth on 9/28/16.
 *
 * This class handles response codes
 * from the server.
 *
 * The Sender class receives a response
 * code from the server and then instantiates
 * this class and passes the response code so
 * the app may respond to the server correctly
 *
 */

public class Response {

    private static final int QUEUE_ID_USED = 302;
    private static final int QUEUE_CREATED = 201;
    private static final int QUEUE_DELETED_SUCCESS = 205;
    private static final int LAUNCH_ACT_QUEUE_REMOVED = 404;

    private int responseCode;

    public static void startResponse(int responseCode) {
        Response response = new Response(responseCode);
        response.respond();
    }

    private Response(int responseCode) {
        this.responseCode = responseCode;
    }

    private void respond() {

        StateControllerMessage message = new StateControllerMessage();
        Log.e("Response", "HERE: " + responseCode + " " + QUEUE_ID_USED);
        switch(responseCode) {
            case QUEUE_ID_USED:
                SoundQueue.CREATED = false;
                SoundQueue.createQueue();
                Sender.createExecute(Sender.NEW_QUEUE, GPSReceiver.latitude, GPSReceiver.longitude);
                break;
            case QUEUE_CREATED:
                message.queueCreated();
                break;
            case QUEUE_DELETED_SUCCESS:
                //StateController.queueDeletedSuccess();
                break;
            case LAUNCH_ACT_QUEUE_REMOVED:
                //StateController.queueRemoved();
                break;
            default:
                break;
        }
    }
}
