package com.noahbutler.soundsq.Network.GCM;

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

import com.google.android.gms.gcm.GcmListenerService;
import com.noahbutler.soundsq.Activities.LaunchActivity;
import com.noahbutler.soundsq.Constants;
import com.noahbutler.soundsq.Network.SoundPackageDownloader;
import com.noahbutler.soundsq.R;
import com.noahbutler.soundsq.SoundPlayer.SoundPackage;
import com.noahbutler.soundsq.SoundPlayer.SoundPlayer;
import com.noahbutler.soundsq.SoundPlayer.SoundQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by NoahButler on 9/18/15.
 */
public class DownStreamReceiver extends GcmListenerService {

    public static SoundPlayer soundPlayer;

    private static final String TAG = "DownStreamReceiver";
    private static final String SP_KEY = "sound_package";
    private static final String A_KEY = "artist";
    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.d(TAG, "...RECEIVED...");
        receivedSound(data);
    }

    /**
     * This method is called when our application receives a downstream
     * message from our server in regards to a new sound being added to
     * the queue.
     *
     * @param data
     */
    private void receivedSound(Bundle data) {
        Log.d(TAG,data.toString());

        /* (1) retrieve the raw strings from the data sent from the server */
        String raw_sound_package = data.getString(SP_KEY);
        String raw_artist        = data.getString(A_KEY);
        SoundPackageDownloader soundPackageDownloader = new SoundPackageDownloader(getBaseContext());

        HashMap<String, String> decodedPackage = decodePackage(raw_sound_package, raw_artist);

        /* (4) create our filename from our sound url */
        String filename = decodedPackage.get("sound_url").substring(decodedPackage.get("sound_url").lastIndexOf("/")+1);

        /* (5) download the file */
        soundPackageDownloader.execute(SoundPackageDownloader.GET_SOUND_IMAGE, decodedPackage.get("album_art"), filename);

        /* (6) create a new sound package to hold the data */
        SoundPackage soundPackage = new SoundPackage();
        soundPackage.sound_url = decodedPackage.get("sound_url");
        soundPackage.artistName = decodedPackage.get("username");
        soundPackage.soundName = decodedPackage.get("title");

        /* (7) add the SoundPackage object to the list */
        SoundQueue.addSoundPackage(soundPackage);
        Log.d("SOUND PACKAGE", "Sound package added");

        SoundQueue.addSound(decodedPackage.get("stream_url"));
        Log.d(TAG, "finished receiving sound...");
    }

    /**
     * This method is a helper method used to break
     * down the given strings of json encoded information
     * into a list of strings.
     *
     * decoded[0]: url of sound image
     * decoded[1]: sound cloud url of sound, used in file name for sound image
     * decoded[2]: artist name
     * decoded[3]: sound title
     *
     * @param packageString
     * @param artistPackageString
     * @return decoded (String[])
     */
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
