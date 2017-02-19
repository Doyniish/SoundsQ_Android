package com.noahbutler.soundsq.SoundPlayer;

import android.util.Log;

import com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views.QueueBall.BallLogic;
import com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views.QueueBall.Signals.BallSignal;
import com.noahbutler.soundsq.IO.IO;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Gildaroth on 2/18/2017.
 */

public class SoundQueueState {

    public static String TAG = "SoundQueueState";

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

    /**
     * Loads JSON String of Queue Data from a temp file.
     * @param directory
     */
    public static void loadState(File directory) {
        Log.v(TAG, "\n\n Loading queue saved state... \n\n");
        JSONObject savedJSON = IO.readSavedState(directory);
        readJSONString(savedJSON);
        SoundQueue.CREATED = true;
    }

    /**
     * User is leaving the app, save the our state as json.
     * @return
     */
    public static String createJSONString() {
        JSONObject savedJSON = new JSONObject();
        try {

            //serialize queue
            JSONObject savedQueueJSON = new JSONObject();

            for (int i = 0; i < SoundQueue.queue.size(); i++) {
                savedQueueJSON.put("" + i, SoundQueue.queue.get(i));
            }

            savedQueueJSON.put("size", SoundQueue.queue.size());
            savedJSON.put("queue", savedQueueJSON);
            Log.e(TAG, "serialized queue...");

            //serialize sound packages
            JSONObject savedSoundPackagesJSON = new JSONObject();

            for(int i = 0; i < SoundQueue.queue_packages.size(); i++) {

                JSONObject savedSoundPackageJSON = new JSONObject();
                SoundPackage current = SoundQueue.queue_packages.get(i);

                savedSoundPackageJSON.put("sound_image", current.soundImage);
                savedSoundPackageJSON.put("title", current.title);
                savedSoundPackageJSON.put("artist_name", current.artistName);
                savedSoundPackageJSON.put("sound_url", current.sound_url);
                savedSoundPackageJSON.put("is_playing", current.isPlaying);

                //add to container JSON object
                savedSoundPackagesJSON.put("" + i, savedSoundPackageJSON);
            }
            savedSoundPackagesJSON.put("size", SoundQueue.queue_packages.size());
            savedJSON.put("packages", savedSoundPackagesJSON);
            //serial name and id
            savedJSON.put("name", SoundQueue.NAME);
            savedJSON.put("queue_id", SoundQueue.ID);

            return savedJSON.toString();
        }catch(JSONException e) {
        }
        return null;

    }

    /**
     *
     * @param savedJSON
     */
    public static void readJSONString(JSONObject savedJSON) {
        try {
            Log.v(TAG, "\n\nreading json string from saved state..\n\n");

            //deserialize queue
            JSONObject savedQueueJSON = savedJSON.getJSONObject("queue");

            Log.e(TAG, "\n\n" + savedJSON.toString()+ "\n\n");

            int queueSize = savedQueueJSON.getInt("size");
            SoundQueue.queue = new ArrayList<>();
            for (int i = 0; i < queueSize; i++) {
                SoundQueue.queue.add(savedQueueJSON.getString("" + i));
            }

            //deserialize sound packages
            JSONObject savedSoundPackagesJSON = savedJSON.getJSONObject("packages");
            int soundPackageSize = savedSoundPackagesJSON.getInt("size");
            SoundQueue.queue_packages = new ArrayList<>();
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
                SoundQueue.queue_packages.add(current);
            }

            //deserialize name and id
            SoundQueue.NAME = savedJSON.getString("name");
            SoundQueue.ID = savedJSON.getString("queue_id");
            Log.v(TAG, "\n\nFinished Loading SoundQueue String...\n\n");
        }catch(JSONException e) {
            Log.e(TAG, "error reading state");
        }
    }
}

