package com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views;

import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.noahbutler.soundsq.Activities.LaunchActivity;
import com.noahbutler.soundsq.R;
import com.noahbutler.soundsq.SoundPlayer.SoundPlayerController;

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


    /******************/
    /* Parent Objects */
    private View masterView;
    private Activity activity;


    /*********/
    /* Views */
    private ListView queueListView;
    private QueueListAdapter queueListAdapter;
    private ImageButton pausePlayButton;

    /*********/
    /* State */
    private int pausePlayButtonState;

    private static final int PAUSED = 0;
    private static final int PLAYING = 1;

    public QueueView(View masterView, Activity activity) {
        this.masterView = masterView;
        this.activity = activity;
    }

    public void instantiate() {

        /* setup pause/play button */
        pausePlayButton = (ImageButton) masterView.findViewById(R.id.pause_play_button);
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
    }

    private void displayPauseButton() {
        //pausePlayButton.setImageDrawable(pauseImage);
    }

    private void displayPlayButton() {
        //pausePlayButton.setImageDrawable(playImage);
    }
}
