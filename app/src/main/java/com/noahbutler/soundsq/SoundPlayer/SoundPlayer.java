package com.noahbutler.soundsq.SoundPlayer;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;

import com.noahbutler.soundsq.Constants;
import com.noahbutler.soundsq.SoundQueue;
import com.noahbutler.soundsq.ThreadUtils.Messenger;

import java.io.IOException;

/**
 * Created by NoahButler on 1/3/16.
 */

public class SoundPlayer extends AsyncTask<String, Void, Boolean> {

    private static String LOG = "SOUND_PLAYER";

    //private ProgdressDialog progress;
    /**
     * the object responsible for playing the stream url given to it.
     */
    private static MediaPlayer mediaPlayer;
    /**
     * a messenger object used to signal that we have finished the current song.
     */
    private Messenger messenger;

    private Context context;
    private PowerManager.WakeLock mWakeLock;

    public SoundPlayer(Context context) {
        this.context = context;
        mediaPlayer = new MediaPlayer();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        messenger = new Messenger();
        Boolean prepared;
        try {
            mediaPlayer.setDataSource(params[0]);
            Log.d(LOG, "stream_url: " + params[0]);

            createMediaHandlers();
            mediaPlayer.prepare();
            /* set our state to prepared */
            prepared = true;

        } catch (IllegalArgumentException e) {
            Log.d("IllegarArgument", e.getMessage());
            prepared = false;
            e.printStackTrace();
        } catch (IOException e) {
            prepared = false;
            e.printStackTrace();
        }

        return prepared;
    }

    @Override
    protected void onPostExecute(Boolean result) {

        super.onPostExecute(result);
        mWakeLock.release();
        Log.d(LOG, "//" + result);
    }
    @Override
    protected void onPreExecute() {

        super.onPreExecute();
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        mWakeLock.acquire();
        Log.d(LOG, "media player is buffering");

    }

    private void createMediaHandlers() {
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

    private void startPlay() {
        mediaPlayer.start();
        messenger.updateViews();
    }

    private void completePlay() {
        mediaPlayer.stop();
        mediaPlayer.reset();
    }

    private void requestNextPlay() {
        // signal the UI thread to play the next sound.
        SoundPlayerController.soundPlayerFinished();
    }
}