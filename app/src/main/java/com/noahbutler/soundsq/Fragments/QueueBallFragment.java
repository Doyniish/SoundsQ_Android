package com.noahbutler.soundsq.Fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Space;
import android.widget.TextView;

import com.noahbutler.soundsq.Activities.LaunchActivity;
import com.noahbutler.soundsq.Constants;
import com.noahbutler.soundsq.GPS.GPSReceiver;
import com.noahbutler.soundsq.IO.IO;
import com.noahbutler.soundsq.Network.Sender;
import com.noahbutler.soundsq.R;
import com.noahbutler.soundsq.SoundPlayer.SoundPlayerController;
import com.noahbutler.soundsq.SoundPlayer.SoundQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Used to determine if the app needs to load or start a queue.
 */
public class QueueBallFragment extends Fragment {
    ImageView queueBallImage;

    Button queueBallSelectTop,
            queueBallSelectBottom,
            queueBallSelectLeft,
            queueBallSelectRight,
            queueBallLogic;

    View masterView;

    private JSONObject checkFile;
    private GPSReceiver gpsReceiver;
    private static final String TAG = "QUEUE BALL FRAG";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        masterView = inflater.inflate(R.layout.fragment_queueball, container, false);
        Log.d(TAG, "Starting Fragment...");
        //TODO: animation queue ball to loading sign

        try {
            initialize_soundsQ();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return masterView;
    }

    @Override
    public void onResume() {
        super.onResume();
        gpsReceiver.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        gpsReceiver.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        gpsReceiver.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * Setup for app
     */

    private void initialize_soundsQ() throws JSONException {
        checkFile = checkInQueue(getActivity().getBaseContext().getFilesDir());

        if(checkFile.has(IO.N_Key)) {
            //nothing has been saved yet, start a new queue.

            /* Initiate Location functionality */
            initializeLocation();

            /* Start the app in Owner View. */
            createOwnerView();

        } else if(checkFile.has(IO.Q_Key)){
            //queue was saved from a previous session or user is viewing this queue
            if(checkFile.getBoolean(IO.B_Key)) {
                //user is the owner of this queue
                //TODO: we need to save all data about the queue I believe.
            }else {
                //queue is just being viewed from this phone, just display it.
                createSpectatorView();
            }
        }
    }

    private void createSpectatorView() throws JSONException {
        SoundQueue.PLAY = false; //Don't try to play songs
        setupQueueView();
        Sender.createExecute(Sender.REQUEST_QUEUE, checkFile.getString(IO.Q_Key));
        //TODO: Display that we are loading the queue
    }

    private void createOwnerView() {
        SoundQueue.PLAY = true; //Play songs
        SoundQueue.createQueue();
        //TODO: Display after we know the creation was successful
        setupQueueBallOverlay();
        setupQueueView();
    }

    private void initializeLocation() {
        gpsReceiver = new GPSReceiver();
        gpsReceiver.initialize(getActivity(), false); //initialized from playing phone
    }

    private JSONObject checkInQueue(File directory) {
        return IO.readQueueID(directory);
    }

    /**
     * Queue was loaded successfully
     */
    public static void loadingSuccess() {

    }

    /**
     * Let the user know the requested queue no longer exists
     */
    public static void failedRequest() {

    }

    private void setupQueueBallOverlay() {
        /* Setup ball */
        //create image
        queueBallImage = (ImageView)masterView.findViewById(R.id.queue_ball_image);
        //setup ball button
        queueBallLogic = (Button)masterView.findViewById(R.id.queue_ball_logic_button);
        //setup show queue id button
        queueBallSelectBottom = (Button)masterView.findViewById(R.id.queue_ball_select_bottom);
        //setup previous button
        queueBallSelectLeft = (Button)masterView.findViewById(R.id.queue_ball_select_left);
        //setup next button
        queueBallSelectRight = (Button)masterView.findViewById(R.id.queue_ball_select_right);
        //setup close queue button
        queueBallSelectTop = (Button)masterView.findViewById(R.id.queue_ball_select_top);

        setButtonsTransparent();

        /* Create Listeners */

        queueBallLogic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clicked Queue Ball...");
                showQueueBallOptions();
            }
        });

        queueBallSelectBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideQueueBallOptions();
            }
        });

        queueBallSelectLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideQueueBallOptions();
            }
        });

        queueBallSelectRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideQueueBallOptions();
            }
        });

        queueBallSelectTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideQueueBallOptions();
            }
        });

    }

    private void hideQueueBall() {
        queueBallImage.setVisibility(View.INVISIBLE);
    }

    private void setButtonsTransparent() {
        queueBallLogic.setBackgroundColor(Color.TRANSPARENT);
        queueBallSelectBottom.setBackgroundColor(Color.TRANSPARENT);
        queueBallSelectLeft.setBackgroundColor(Color.TRANSPARENT);
        queueBallSelectRight.setBackgroundColor(Color.TRANSPARENT);
        queueBallSelectTop.setBackgroundColor(Color.TRANSPARENT);
    }

    private void setupQueueView() {
        /* setup queue view */
        Constants.queueListView = (ListView) masterView.findViewById(R.id.queueView);

        /* create our queue adapter */
        Constants.queueListAdapter = new QueueListAdapter((LaunchActivity) this.getActivity());
        Constants.queueListView.setAdapter(Constants.queueListAdapter);
    }

    private void showQueueBallOptions() {
        queueBallImage.setImageDrawable(getResources().getDrawable(R.drawable.queue_ball_options, null));
        setOptionsClickable(true);
    }

    private void hideQueueBallOptions() {
        queueBallImage.setImageDrawable(getResources().getDrawable(R.drawable.queue_ball, null));
        setOptionsClickable(false);
    }

    private void setOptionsClickable(boolean clickable) {
        queueBallSelectBottom.setClickable(clickable);
        queueBallSelectTop.setClickable(clickable);
        queueBallSelectLeft.setClickable(clickable);
        queueBallSelectRight.setClickable(clickable);
    }
}
