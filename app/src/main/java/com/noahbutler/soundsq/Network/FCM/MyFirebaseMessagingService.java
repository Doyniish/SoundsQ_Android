package com.noahbutler.soundsq.Network.FCM;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.noahbutler.soundsq.Activities.LaunchActivity;
import com.noahbutler.soundsq.Activities.ShareActivity;
import com.noahbutler.soundsq.Fragments.LocalQueuesFragment;
import com.noahbutler.soundsq.Network.SoundPackageDownloader;
import com.noahbutler.soundsq.QRCode.QRCodeDecompiler;
import com.noahbutler.soundsq.R;
import com.noahbutler.soundsq.SoundPlayer.SoundPackage;
import com.noahbutler.soundsq.SoundPlayer.SoundQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by gildaroth on 9/23/16.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "DownStreamReceiver";
    private static final String SP_KEY = "sound_package";
    private static final String A_KEY = "artist";
    private static final String S_KEY = "size";
    private static final String QR_KEY = "qr_code";
    private static final String L_KEY = "local_queue";

    Map<String, String> data;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        data = remoteMessage.getData();
        Log.d(TAG, "...RECEIVED...");
        if(data.keySet().contains(SP_KEY)) {
            receivedSound(data);
        }else if(data.keySet().contains(S_KEY)) {
            receivedQueue(data);
        }else if(data.keySet().contains(L_KEY)) {
            receivedLocalQueues(data);
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
        String size_string = data.get(S_KEY);
        int size = Integer.getInteger(size_string);

        for(int i = 0; i < size; i++) {
            String currentSoundData = data.get(String.valueOf(i));
            try {
                JSONObject jsonObject = new JSONObject(currentSoundData);
                HashMap<String, String> sound_data = decodePackage(jsonObject.getString(SP_KEY),jsonObject.getString(A_KEY));
                queueSound(sound_data);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    private void receivedLocalQueues(Map<String, String> data) {
        try {
            JSONObject jsonObject = new JSONObject(data.get(L_KEY));
            Iterator<String> keys = jsonObject.keys();

            //create list of key strings
            while(keys.hasNext()) {
                LocalQueuesFragment.localQueueList.put(keys.next(), jsonObject.getString(keys.next()));
            }

            //notify ShareActivity that it can display the list.
            ShareActivity.showList();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, String> decodePackage(String packageString, String artistPackageString) {
        HashMap<String, String> decoded = new HashMap<>();
        try {
            JSONObject soundPackage = new JSONObject(packageString);
            JSONObject userPackage = new JSONObject(artistPackageString);

            decoded.put("album_art", soundPackage.getString("album_art"));
            decoded.put("stream_url", soundPackage.getString("stream_url"));
            decoded.put("sound_url", soundPackage.getString("sound_url"));
            decoded.put("username", userPackage.getString("username"));
            decoded.put("title", soundPackage.getString("title"));

        }catch(JSONException e) {
            Log.e("JSON_ERROR", e.getMessage());
        }

        return decoded;
    }

    private void queueSound(HashMap<String, String> decodedPackage) {
        SoundPackageDownloader soundPackageDownloader = new SoundPackageDownloader(getBaseContext());
        /* create our filename from our sound url */
        String filename = decodedPackage.get("sound_url").substring(decodedPackage.get("sound_url").lastIndexOf("/")+1);
        /* download the file */
        soundPackageDownloader.execute(SoundPackageDownloader.GET_SOUND_IMAGE, decodedPackage.get("album_art"), filename);
        /* create a new sound package to hold the data */
        SoundPackage soundPackage = SoundPackage.createSoundPackage(decodedPackage);
        /* add the SoundPackage object to the list */
        SoundQueue.addSoundPackage(soundPackage);
        SoundQueue.addSound(decodedPackage.get("stream_url"));
    }

    private void sendNotification(String message) {
        Intent intent = new Intent(this, LaunchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark_focused)
                .setContentTitle("SoundsQ")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setLights(Color.CYAN, 1000, 1000)
                .setColor(Color.CYAN);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}
