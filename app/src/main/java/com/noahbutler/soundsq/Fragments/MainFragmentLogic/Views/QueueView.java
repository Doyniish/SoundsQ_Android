package com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.noahbutler.soundsq.Activities.LaunchActivity;
import com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views.SoundCloudLogin.RegisterClient;
import com.noahbutler.soundsq.R;
import com.noahbutler.soundsq.SoundPlayer.SoundPlayerController;
import com.noahbutler.soundsq.SoundPlayer.SoundQueue;
import com.noahbutler.soundsq.SoundPlayer.SoundQueueState;

import java.io.File;

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
    private Context context;


    /*********/
    /* Views */
    private ListView queueListView;
    private QueueListAdapter queueListAdapter;
    private ImageView pausePlayButton;
    private WebView registerView;

    /*********/
    /* SoundQueueState */
    private int pausePlayButtonState = 1;
    private static final int PAUSED = 0;
    private static final int PLAYING = 1;

    /* Image Size */
    private static final int WIDTH = 100;
    private static final int HEIGHT = 100;

    /* Custom web view to keep users in the app while registering */
    private RegisterClient registerClient;

    public QueueView(Context context, View masterView) {
        this.masterView = masterView;
        this.context = context;
        this.registerClient = new RegisterClient(context);
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
        queueListAdapter = new QueueListAdapter(context);
        queueListView.setAdapter(queueListAdapter);

        //web view to display the registration url for SoundCloud connection.
        registerView = (WebView)masterView.findViewById(R.id.register_popup);
        registerView.getSettings().setJavaScriptEnabled(true);
        registerView.getSettings().setLoadWithOverviewMode(true);
        registerView.getSettings().setUseWideViewPort(true);
        registerView.setWebViewClient(registerClient);
        registerView.setVisibility(View.INVISIBLE);

        //initially display pause
        displayPauseButton();
    }

    private void displayPauseButton() {
        Glide.with(this.context).load(R.drawable.pause).into(pausePlayButton);
    }

    private void displayPlayButton() {
        Glide.with(this.context).load(R.drawable.play).into(pausePlayButton);
    }

    public void displayRegisterPopUp(final String register_url) {
        //Log.e(TAG, "Displaying register popup");

        //let our web client know it should display this url in the app.
        registerClient.setUrl(register_url);
        //now that the web view knows to not move away from our app when loading this view. Display
        //the registration url.
        registerView.setVisibility(View.VISIBLE);
        registerView.loadUrl(register_url);
    }

    public void update() {
        //Log.e(TAG, "UPDATE NOTIFY");
        //Log.e(TAG, "Package size: " + SoundQueue.queue_packages.size());
        queueListAdapter.notifyDataSetChanged();
        //queueListAdapter.notifyDataSetInvalidated();
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        SoundQueue.saveInstanceState(savedInstanceState);
    }

    public void onSavedInstanceRestored(Bundle savedInstanceState) {
        SoundQueue.onSavedInstanceRestored(savedInstanceState);
    }

    public void onPause(File directory) {
        //Log.v(TAG, "\n\nQueueView OnPause...\n\n");
        SoundQueueState.saveState(directory);
    }

    public void onResume(File directory) {
        //load saved data
        //Log.v(TAG, "\n\nQueueView OnPause...\n\n");
        SoundQueueState.loadState(directory);
        //update view
        update();
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.registerView.canGoBack()) {
            this.registerView.goBack();
            return true;
        }

        return false;
    }

}
