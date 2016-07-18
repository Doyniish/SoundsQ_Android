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

    //private ProgressDialog progress;
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

    @Override
    protected Boolean doInBackground(String... params) {
        messenger = new Messenger();
        Boolean prepared;
        try {
            /* set our data source for our media player to the current stream url sent */
            mediaPlayer.setDataSource(params[0]);
            Log.d(LOG, "stream_url: " + params[0]);

            /* create our on completion listener which will signal different parts of the
               application to start to play the next sound.
             */
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    SoundQueue.isPlayingSound(false);
                    // signal the UI thread to play the next sound.
                    if(SoundQueue.size() > (SoundQueue.getCurrentIndex() + 1)) {
                        SoundQueue.nextSong();
                        Log.d(LOG, "playing next sound: Current Index: " + SoundQueue.getCurrentIndex());
                        SoundQueue.isPlayingSound(true);
                        Log.d(LOG, "playing next sound: URL: " + SoundQueue.getCurrentSound());
                        messenger.playSound(SoundQueue.getCurrentSound());
                    }

                }
            });

            /* create our on prepared listener which will signal
               the media player to start playing from the given
               stream url.
             */
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                    /* current stream should now be ready to play.
                       start playing it.
                     */
                    mediaPlayer.start();
                    SoundQueue.isPlayingSound(true);
                    Log.d(LOG, "playing has started");

                    /* signal the UI thread to update
                       the queue list to display which
                       sound is currently playing
                     */
                    messenger.updateViews();
                }
            });

            /* prepare the current media player
               with it's given stream url.
             */
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

        /* return the state of prepared */
        return prepared;
    }

    @Override
    protected void onPostExecute(Boolean result) {

        super.onPostExecute(result);
        mWakeLock.release();
        Log.d(LOG, "//" + result);
    }

    public SoundPlayer(Context context) {
        this.context = context;
        mediaPlayer = new MediaPlayer();
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        mWakeLock.acquire();
        Log.d(LOG, "media player is buffering");

    }
}