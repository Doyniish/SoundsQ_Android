package com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.noahbutler.soundsq.Activities.LaunchActivity;
import com.noahbutler.soundsq.BitmapLoader.AsyncDrawable;
import com.noahbutler.soundsq.R;
import com.noahbutler.soundsq.SoundPlayer.SoundPackage;
import com.noahbutler.soundsq.SoundPlayer.SoundPlayerController;
import com.noahbutler.soundsq.SoundPlayer.SoundQueue;

/**
 * Created by gildaroth on 9/28/16.
 *
 * QueueView holds all views associated
 * with displaying the queue.
 *
 * Views:
 * QueueList
 * QueueListAdapter
 */

public class QueueView {


    /*************/
    /* DEBUG TAG */
    private static final String TAG = "QueueView";


    /******************/
    /* Parent Objects */
    private View masterView;
    private Activity activity;


    /*********/
    /* Views */
    private ListView queueListView;
    private QueueListAdapter queueListAdapter;
    private ImageView pausePlayButton;

    /*********/
    /* State */
    private int pausePlayButtonState = 1;
    private static final int PAUSED = 0;
    private static final int PLAYING = 1;

    /* Image Size */
    private static final int WIDTH = 100;
    private static final int HEIGHT = 100;

    public QueueView(View masterView, Activity activity) {
        this.masterView = masterView;
        this.activity = activity;
    }

    public void instantiate() {

        /* setup pause/play button */
        pausePlayButton = (ImageView) masterView.findViewById(R.id.pause_play_button);
        pausePlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(pausePlayButtonState) {
                    case PAUSED:
                        //Play
                        SoundPlayerController.resumeCurrentSound();
                        displayPauseButton();
                        pausePlayButtonState = PLAYING;
                        break;
                    case PLAYING:
                        SoundPlayerController.pauseCurrentSound();
                        displayPlayButton();
                        pausePlayButtonState = PAUSED;
                }
            }
        });

        /* setup queue view */
        queueListView = (ListView) masterView.findViewById(R.id.queueView);
        /* create our queue adapter */
        queueListAdapter = new QueueListAdapter((LaunchActivity) activity);
        queueListView.setAdapter(queueListAdapter);

        //initially display pause
        displayPauseButton();
    }

    private void displayPauseButton() {
        AsyncDrawable.loadBitmap(activity.getResources(), R.drawable.pause, WIDTH, HEIGHT, pausePlayButton);
    }

    private void displayPlayButton() {
        AsyncDrawable.loadBitmap(activity.getResources(), R.drawable.play, WIDTH, HEIGHT, pausePlayButton);
    }

    public void update() {
        Log.e(TAG, "UPDATE NOTIFY");
        Log.e(TAG, "Package size: " + SoundQueue.queue_packages.size());
        queueListAdapter = new QueueListAdapter((LaunchActivity) activity);
        queueListView.setAdapter(queueListAdapter);
    }

    public void addArt(String fileLocation, String sound_url) {
        for(int i = 0; i < SoundQueue.queue_packages.size(); i++) {
            if (SoundQueue.queue_packages.get(i).sound_url.contentEquals(sound_url)) {
                SoundQueue.queue_packages.get(i).sendFileLocation(fileLocation);
            }
        }
    }
}
