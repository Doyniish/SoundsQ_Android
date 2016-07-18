package com.noahbutler.soundsq.Network.GCM;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.noahbutler.soundsq.Activities.LaunchActivity;
import com.noahbutler.soundsq.Constants;
import com.noahbutler.soundsq.Network.Sender;
import com.noahbutler.soundsq.Network.SoundPackageDownloader;
import com.noahbutler.soundsq.R;
import com.noahbutler.soundsq.SoundPackage;
import com.noahbutler.soundsq.SoundPlayer.SoundPlayer;
import com.noahbutler.soundsq.SoundQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by NoahButler on 9/18/15.
 */
public class DownStreamReceiver extends GcmListenerService {

    public static SoundPlayer soundPlayer;

    private static final String TAG = "DownStreamReceiver";
    private static final String[] keys = {
            "sound_url",
            "sound_stream_url",
            "info_package"
    };

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.d(TAG, "...RECEIVED...");
        if(data.containsKey(keys[0])) {
            receivedSoundList(data);
        }else if(data.containsKey(keys[1])){
            receivedSoundStreamURL(data);
        }else if(data.containsKey(keys[2])) {
            receivedSoundPackage(data);
        }
    }

    /**
     * This method is called when our application receives a downstream
     * message from our server in regards to a new sound being added to
     * the queue.
     *
     * @param data
     */
    private void receivedSoundList(Bundle data) {

        String sound_sent = data.getString(keys[0]);
        SoundQueue.addSound(sound_sent);
        retrieveSoundPackage();

        //sendNotification("A new song has been added to your queue!");
        Log.d(TAG, "finished receiving sound....");
    }

    /**
     * This method is called when our application receives a downstream
     * message from our server in regards to a stream url request from
     * this device.
     *
     * (1) The server sends the stream url under the key of keys[1]
     * (sound_stream_url)
     *
     * (2) We then create a new SoundPlayer object to handle
     * the playing of this sound stream url.
     *
     * (3) The SoundPlayer object is then fed the stream url
     * and handles playing that sound.
     *
     * @param data
     */
    private void receivedSoundStreamURL(Bundle data) {
        /* (1) grab the stream url from the data package */
        String sound_stream_url = data.getString(keys[1]);
        /* (2) create the SoundPlayer object */
        soundPlayer = new SoundPlayer(getBaseContext());
        /* (3) feed the stream url to the SoundPlayer */
        soundPlayer.execute(sound_stream_url);
    }

    /**
     * This method is called when our application receives a downstream
     * message from our server in regards to a Sound Package request from
     * this device.
     *
     * (1) The server sends two strings under the keys: keys[2] (info_package)
     * and user_package
     *
     * (2) We then create a SoundPackageDownloader object which will be
     * responsible for downloading the image of the sound
     *
     * (3) We then send the two strings through our package decoder
     * method which is defined before this method.
     *
     * (4) We then get our filename name from the decoded string array
     * that will be used to store the image on the device
     *
     * (5) After that, we signal our SoundPackageDownloader object to
     * start the download process of the image url specified in our
     * array of decoded strings.
     *
     * (6) We then create our SoundPackage object and assign the different
     * fields to the correct values we have decoded.
     *
     * (7) Lastly, we add that soundPackage to the list of SoundPackages
     * currently apart of the current queue.
     *
     * @param data
     */
    private void receivedSoundPackage(Bundle data) {

        /* (1) retrieve the raw strings from the data sent from the server */
        String raw_sound_package = data.getString(keys[2]);
        String raw_user_package  = data.getString("user_package");

        /* (2) create our SoundPackageDownloader object responsible for downloading the image
           of the sound.
         */
        SoundPackageDownloader soundPackageDownloader = new SoundPackageDownloader(getBaseContext());

        /* (3) decode the package */
        String[] decodedPackage = decodePackage(raw_sound_package, raw_user_package);

        /* (4) create our filename from our sound url */
        String filename = decodedPackage[1].substring(decodedPackage[1].lastIndexOf("/")+1);

        /* (5) download the file */
        soundPackageDownloader.execute(SoundPackageDownloader.GET_SOUND_IMAGE, decodedPackage[0], filename);

        /* (6) create a new sound package to hold the data */
        SoundPackage soundPackage = new SoundPackage();
        soundPackage.sound_url = decodedPackage[1];
        soundPackage.artistName = decodedPackage[2];
        soundPackage.soundName = decodedPackage[3];

        /* (7) add the SoundPackage object to the list */
        Constants.QUEUE_SOUND_PACKAGES.add(soundPackage);
        Log.d("SOUND PACKAGE", "Sound package added");
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
     * @param userPackageString
     * @return decoded (String[])
     */
    private String[] decodePackage(String packageString, String userPackageString) {
        String[] decoded = new String[4];
        try {
            JSONObject jsonPackage = new JSONObject(packageString);
            JSONObject jsonUserPackage = new JSONObject(userPackageString);
            decoded[0] = jsonPackage.getString("album_art");
            decoded[1] = jsonPackage.getString("sound_url");
            decoded[2] = jsonUserPackage.getString("username");
            decoded[3] = jsonPackage.getString("title");

        }catch(JSONException e) {
            Log.e("JSON_ERROR", e.getMessage());
        }

        return decoded;
    }

    private void retrieveSoundPackage() {
        Log.d("SOUND PACKAGE", "retrieving sound package");
        Sender sender = new Sender();
        sender.execute(Sender.GET_SOUND_INFO_PACKAGE, SoundQueue.ID, SoundQueue.getLatestSound());
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
