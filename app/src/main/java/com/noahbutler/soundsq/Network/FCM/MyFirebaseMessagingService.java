package com.noahbutler.soundsq.Network.FCM;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.noahbutler.soundsq.Activities.Share.ShareActivityMessage;
import com.noahbutler.soundsq.Fragments.MainFragmentLogic.StateController.StateControllerMessage;
import com.noahbutler.soundsq.Fragments.MainFragmentLogic.StateController.UserState;
import com.noahbutler.soundsq.SoundPlayer.SoundPackage;
import com.noahbutler.soundsq.SoundPlayer.SoundQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gildaroth on 9/23/16.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "DownStreamReceiver";
    private static final String SP_KEY = "sound_package";
    private static final String A_KEY = "artist";
    private static final String R_Key = "requested_queue";
    private static final String L_KEY = "local_queue";
    private static final String SC_Reg_Key = "sound_cloud_register";

    Map<String, String> data;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        data = remoteMessage.getData();
        Log.d(TAG, "...RECEIVED...");
        Log.d(TAG, "" + data.toString());
        Log.d(TAG, "" + data.keySet());

        if(data.keySet().contains(SP_KEY)) {
            receivedSound(data);
        }else if(data.keySet().contains(R_Key)) {
            receivedQueue(data);
        }else if(data.keySet().contains(L_KEY)) {
            receivedLocalQueues(data);
        }else if(data.keySet().contains(SC_Reg_Key)) {
            receivedRegisterURL(data);
        }
    }

    /**
     * This method is called when our application receives a downstream
     * message from our server in regards to a new sound being added to
     * the queue.
     *
     * @param data
     */
    private void receivedSound(Map<String, String> data) {
        Log.d(TAG,data.toString());

        /* (1) retrieve the raw strings from the data sent from the server */
        String raw_sound_package = data.get(SP_KEY);
        String raw_artist        = data.get(A_KEY);

        HashMap<String, String> decodedPackage = decodePackage(raw_sound_package, raw_artist);
        queueSound(decodedPackage);

    }

    private void receivedQueue(Map<String, String> data) {
        //Log.e(TAG, "DATA: " + data.toString());

        SoundQueue.createQueue(false);

        SoundQueue.ID = data.get("queue_id");
        SoundQueue.NAME = data.get("name");

        if(!data.get("size").contentEquals("0")) {
            try {
                JSONArray queue = new JSONArray(data.get("requested_queue"));
                for (int i = 0; i < queue.length(); i++) {
                    JSONObject jsonObject = queue.getJSONObject(i);
                    HashMap<String, String> decoded = new HashMap<>();
                    decoded.put("album_art", jsonObject.getString("album_art"));
                    decoded.put("stream_url", jsonObject.getString("stream_url"));
                    decoded.put("sound_url", jsonObject.getString("sound_url"));
                    decoded.put("artist", jsonObject.getString("artist"));
                    decoded.put("title", jsonObject.getString("title"));
                    queueSound(decoded);



                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //update the ui
        StateControllerMessage message = new StateControllerMessage();
        if(UserState.STATE == UserState.OWNER) {
            message.ownerLoaded();
        }else {
            message.spectatorLoaded();
        }

    }

    private void receivedLocalQueues(Map<String, String> data) {
        try {
            JSONObject localQueuesJSON = new JSONObject(data.get(L_KEY));
            ShareActivityMessage message = new ShareActivityMessage();

            message.localQueuesLoaded(localQueuesJSON);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void receivedRegisterURL(Map<String, String> data) {
        StateControllerMessage message = new StateControllerMessage();
        message.soundCloudRegView(data.get(SC_Reg_Key));
    }

    private HashMap<String, String> decodePackage(String packageString, String artistPackageString) {
        HashMap<String, String> decoded = new HashMap<>();
        try {
            JSONObject soundPackage = new JSONObject(packageString);
            JSONObject userPackage = new JSONObject(artistPackageString);

            decoded.put("album_art", soundPackage.getString("album_art"));
            decoded.put("stream_url", soundPackage.getString("stream_url"));
            decoded.put("sound_url", soundPackage.getString("sound_url"));
            decoded.put("artist", userPackage.getString("username"));
            decoded.put("title", soundPackage.getString("title"));

        }catch(JSONException e) {
            Log.e("JSON_ERROR", e.getMessage());
        }

        return decoded;
    }

    private void queueSound(HashMap<String, String> decodedPackage) {

        /* create a new sound package to hold the data */
        SoundPackage soundPackage = SoundPackage.createSoundPackage(decodedPackage);

        /* add the SoundPackage object to the list */
        SoundQueue.addSoundPackage(soundPackage);
        SoundQueue.addSound(decodedPackage.get("stream_url"));

        // notify the view that we have a new sound to display
        StateControllerMessage message = new StateControllerMessage();
        message.updateList_NewSong();
    }
}
