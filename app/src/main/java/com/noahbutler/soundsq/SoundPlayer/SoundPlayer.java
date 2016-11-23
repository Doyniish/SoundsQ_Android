package com.noahbutler.soundsq.SoundPlayer;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;


import java.io.IOException;

/**
 * Created by NoahButler on 1/3/16.
 *
 * Sound Player is only created and controlled by
 * the Sound Player controller.
 *
 * It will started/paused/played/stopped
 * by the Sound Player Controller.
 *
 * It will request the next song to be played once
 * the current sound has been completed
 */

public class SoundPlayer extends AsyncTask<String, Void, Boolean> {


    /*************/
    /* DEBUG TAG */
    private static final String TAG = "SOUND_PLAYER";


    /*****************************************************/
    /* The object responsible for playing the stream url */
    private static MediaPlayer mediaPlayer;


    /**************************/
    /* Local Helper Variables */
    private PowerManager.WakeLock wakeLock;
    private Context context;

    public SoundPlayer(Context context) {
        this.context = context;
        mediaPlayer = new MediaPlayer();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        Boolean prepared;

        try {
            mediaPlayer.setDataSource(params[0]);
            Log.d(TAG, "Data Source: " + params[0]);

            createMediaPlayerListeners();
            mediaPlayer.prepare();

            /* set our state to prepared */
            prepared = true;

        } catch (IllegalArgumentException e) {
            Log.d(TAG, "Illegal Argument: " + e.getMessage());
            prepared = false;
            e.printStackTrace();
        } catch (IOException e) {
            prepared = false;
            e.printStackTrace();
        }

        return prepared;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        /* Continue play when the screen is off */
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        wakeLock.acquire();

    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        wakeLock.release();
    }

    /***************************************/
    /** The following are helper methods. **/
    /***************************************/

    /**
     * This method is used to create our
     * listeners for the Media Player
     */
    private void createMediaPlayerListeners() {
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                completePlay();
                requestNextPlay();
            }
        });

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                startPlay();
            }
        });
    }

    /**
     * Command the Media Player to start playing the stream.
     */
    private void startPlay() {
        mediaPlayer.start();
        Log.e(TAG, "Playing index: " + SoundQueue.getCurrentIndex());
        //TODO: update views
        //messenger.updateViews();
    }

    /**
     * Called when the stream is finished.
     * Clears out the media player.
     */
    private void completePlay() {
        mediaPlayer.stop();
        mediaPlayer.reset();
    }

    /**
     * Since a new Sound Player object is created
     * for each new stream url, we will request the
     * Sound Player Controller to play the next stream url.
     */
    private void requestNextPlay() {
        SoundPlayerController.soundPlayerFinished();
    }

    /**
     * Used when an outside source, such as the
     * notification media controller, stops the app.
     */
    public void stopPlaying() {
        mediaPlayer.stop();
        mediaPlayer.reset();
    }

    /**
     * Used when an outside source pauses the stream.
     */
    public void pause() {
        mediaPlayer.pause();
    }

    /**
     * Used when an outside source continues the stream.
     */
    public void resume() {
        mediaPlayer.start();
    }
}