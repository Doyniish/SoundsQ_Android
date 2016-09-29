package com.noahbutler.soundsq.Network;

import com.noahbutler.soundsq.Fragments.MainFragmentLogic.StateController.StateController;
import com.noahbutler.soundsq.Fragments.MainFragmentLogic.StateController.StateControllerMessage;
import com.noahbutler.soundsq.SoundPlayer.SoundQueue;
import com.noahbutler.soundsq.ThreadUtils.Messenger;

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

    private static final int QUEUE_LOCATION_SET = 100;
    private static final int QUEUE_ID_USED = 302;
    private static final int QUEUE_CREATED = 201;
    private static final int QUEUE_LOADED = 200;
    private static final int QUEUE_DELETED_SUCCESS = 205;
    private static final int SHARE_ACT_QUEUE_REMOVED = 204;
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

        switch(responseCode) {
            case QUEUE_LOCATION_SET:
                break;
            case QUEUE_ID_USED:
                SoundQueue.createQueue();
                break;
            case QUEUE_CREATED:
                //TODO: queue created successfully,

                break;
            case QUEUE_LOADED:
                StateController.queueFound();
                break;
            case QUEUE_DELETED_SUCCESS:
                StateController.queueDeletedSuccess();
                break;
            case SHARE_ACT_QUEUE_REMOVED:
                messenger.queueNotExists(Messenger.notExists[0]);
                break;
            case LAUNCH_ACT_QUEUE_REMOVED:
                StateController.queueRemoved();
                break;
            default:
                break;
        }
    }
}
