package com.noahbutler.soundsq.SoundPlayer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.noahbutler.soundsq.IO.IO;
import com.noahbutler.soundsq.Network.Sender;
import com.noahbutler.soundsq.QueueIDGenerator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by NoahButler on 6/30/16.
 */
public class SoundQueue {

    private static final String TAG = "SoundQueue";

    public static String ID = null;
    public static String NAME = null;

    public static boolean PLAY = false;
    public static boolean CREATED = false;
    public static boolean LOADED = false;

    protected static ArrayList<String> queue;
    public static ArrayList<SoundPackage> queue_packages;
    private static int currentSound;

    /**
     * Boolean that holds the state: the current application is playing a sound or not.
     */
    private static boolean PLAYING_SOUND;

    public static void addSound(String streamUrl) {
        queue.add(streamUrl);
        sendToSoundPlayerController();
    }

    public static void addSoundPackage(SoundPackage soundPackage) {
        queue_packages.add(soundPackage);
    }

    public static void createQueue(boolean newQueueID) {
        /* instantiate SoundsQ Data */
        queue = new ArrayList<>();
        queue_packages = new ArrayList<>();

        if(newQueueID) {
            /* Create a Random ID */
            genQueueID();
        }
    }

    private static void sendToSoundPlayerController() {
        //run our checks to see if we need to play this sound right now
        Log.e(TAG, "First Song Check: " + firstSongCheck());
        Log.e(TAG, "Sound Playing Check: " + isPlayingSound());
        Log.e(TAG, "PLAY Check: " + PLAY);

        if (firstSongCheck() && !isPlayingSound() && PLAY) {
            SoundPlayerController.playCurrentSound();
        }
    }

    private static boolean firstSongCheck() {
        return (queue.size() == 1);
    }


    public static String getSoundUrl(int index) {
        SoundPackage soundPackage = queue_packages.get(index);
        return soundPackage.sound_url;
    }

    public static int size() {
        return queue.size();
    }

    public static void nextSong() {
        currentSound++;
    }

    public static void prevSong() {
        currentSound--;
    }

    public static void topSong() {
        currentSound = 0;
    }

    public static void setIndex(int index) {
        currentSound = index;
    }

    public static int getCurrentIndex() {
        return currentSound;
    }

    public static String getCurrentSound() {
        return queue.get(currentSound);
    }

    public static SoundPackage getCurrentSoundPackage() {
        if(queue_packages != null) {
            if(queue_packages.size() != 0) {
                return queue_packages.get(currentSound);
            }
        }

        return null;
    }

    public static void isPlayingSound(boolean is) {
        PLAYING_SOUND = is;
    }

    public static boolean isPlayingSound() {
        return PLAYING_SOUND;
    }

    public static boolean hasQueuedSounds() {
        if(queue != null) {
            if(queue.size() > 0) {
                return true;
            }
        }
        return false;
    }

    public static void genQueueID() {
        SoundQueue.ID = QueueIDGenerator.generate();
    }

    public static void saveInstanceState(Bundle savedInstanceState) {
        //save queue to bundle as
        String savedInstance = SoundQueueState.createJSONString();
        savedInstanceState.putString("saved_state", savedInstance);
    }

    public static void onSavedInstanceRestored(Bundle savedInstanceState) {
        try {
            SoundQueueState.readJSONString( new JSONObject(savedInstanceState.getString("saved_state")));
            SoundQueue.CREATED = true;
            Log.v(TAG, "\n\nFinished onSavedInstanceRestored \n\n");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void close() {
        Log.e(TAG, "Closing...");
        SoundPlayerController.close();
        Sender.createExecute(Sender.CLOSE_QUEUE);
    }
}
