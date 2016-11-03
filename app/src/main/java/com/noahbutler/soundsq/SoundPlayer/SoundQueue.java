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

    private static ArrayList<String> queue;
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

    /**
     * Writes JSON String of Queue Data to a temp file
     * @param directory
     */
    public static void saveState(File directory) {
        Log.v(TAG, "\n\nSaving queue state...\n\n");

        //create json string of the entire SoundQueue
        String savedJSON = createJSONString();

        //write to file
        IO.writeSaveState(directory, savedJSON);
    }

    private static String createJSONString() {
        JSONObject savedJSON = new JSONObject();
        try {

            //serialize queue
            JSONObject savedQueueJSON = new JSONObject();
            for (int i = 0; i < queue.size(); i++) {
                savedQueueJSON.put("" + i, queue.get(i));
            }
            savedQueueJSON.put("size", queue.size());
            savedJSON.put("queue", savedQueueJSON);
            Log.e(TAG, "serialized queue...");
            //serialize sound packages
            JSONObject savedSoundPackagesJSON = new JSONObject();
            for(int i = 0; i < queue_packages.size(); i++) {

                JSONObject savedSoundPackageJSON = new JSONObject();
                SoundPackage current = queue_packages.get(i);

                savedSoundPackageJSON.put("sound_image", current.soundImage);
                savedSoundPackageJSON.put("title", current.title);
                savedSoundPackageJSON.put("artist_name", current.artistName);
                savedSoundPackageJSON.put("sound_url", current.sound_url);
                savedSoundPackageJSON.put("is_playing", current.isPlaying);

                //add to container JSON object
                savedSoundPackagesJSON.put("" + i, savedSoundPackageJSON);
            }
            savedSoundPackagesJSON.put("size", queue_packages.size());
            savedJSON.put("packages", savedSoundPackagesJSON);
            Log.e(TAG, "serialized sound packages...");
            //serial name and id
            savedJSON.put("name", NAME);
            savedJSON.put("queue_id", ID);

            Log.e(TAG, "\n\n Finished saving queue state: " + savedJSON.toString());
            return savedJSON.toString();
        }catch(JSONException e) {
            Log.e(TAG, "error saving state");
        }
        return null;
    }

    public static void loadState(File directory) {
        Log.v(TAG, "\n\n Loading queue saved state... \n\n");
        JSONObject savedJSON = IO.readSavedState(directory);
        readJSONString(savedJSON);
        SoundQueue.CREATED = true;
    }

    public static void readJSONString(JSONObject savedJSON) {
        try {
            Log.v(TAG, "\n\nreading json string from saved state..\n\n");

            //deserialize queue
            JSONObject savedQueueJSON = savedJSON.getJSONObject("queue");

            Log.e(TAG, "\n\n" + savedJSON.toString()+ "\n\n");

            int queueSize = savedQueueJSON.getInt("size");
            queue = new ArrayList<>();
            for (int i = 0; i < queueSize; i++) {
                queue.add(savedQueueJSON.getString("" + i));
            }

            //deserialize sound packages
            JSONObject savedSoundPackagesJSON = savedJSON.getJSONObject("packages");
            int soundPackageSize = savedSoundPackagesJSON.getInt("size");
            queue_packages = new ArrayList<>();
            for(int i = 0; i < soundPackageSize; i++) {
                Log.v(TAG, "index: " + i + "");

                JSONObject savedSoundPackageJSON = savedSoundPackagesJSON.getJSONObject("" + i);
                SoundPackage current = new SoundPackage();

                Log.e(TAG, "Sound Package JSON String: " + savedSoundPackageJSON.toString() + "\n\n");
                current.soundImage = savedSoundPackageJSON.getString("sound_image");
                current.title = savedSoundPackageJSON.getString("title");
                current.artistName = savedSoundPackageJSON.getString("artist_name");
                current.sound_url = savedSoundPackageJSON.getString("sound_url");
                current.isPlaying = savedSoundPackageJSON.getBoolean("is_playing");

                //add to sound package array
                queue_packages.add(current);
            }

            //deserialize name and id
            NAME = savedJSON.getString("name");
            ID = savedJSON.getString("queue_id");
            Log.v(TAG, "\n\nFinished Loading SoundQueue String...\n\n");
        }catch(JSONException e) {
            Log.e(TAG, "error reading state");
        }
    }

    public static void saveInstanceState(Bundle savedInstanceState) {
        //save queue to bundle as
        String savedInstance = createJSONString();
        savedInstanceState.putString("saved_state", savedInstance);
        Log.v(TAG, "\n\nFinished saving instance in bundle...\n\n");
    }

    public static void onSavedInstanceRestored(Bundle savedInstanceState) {
        try {
            readJSONString( new JSONObject(savedInstanceState.getString("saved_state")));
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
