package com.noahbutler.soundsq.Fragments;

import android.app.Fragment;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.noahbutler.soundsq.Activities.LaunchActivity;
import com.noahbutler.soundsq.Constants;
import com.noahbutler.soundsq.GPS.GPSReceiver;
import com.noahbutler.soundsq.IO.IO;
import com.noahbutler.soundsq.Network.Sender;
import com.noahbutler.soundsq.R;
import com.noahbutler.soundsq.SoundPlayer.SoundPlayerController;
import com.noahbutler.soundsq.SoundPlayer.SoundQueue;

import java.io.File;

/**
 * Used to determine if the app needs to load or start a queue.
 */
public class QueueBallFragment extends Fragment {
    ImageView queueBallImage;

    ImageButton queueBallSelectTop,
            queueBallSelectBottom,
            queueBallSelectLeft,
            queueBallSelectRight,
            queueBallLogic;

    LinearLayout queueIDDisplayLayout;
    ImageView qrCodeImageView;
    TextView queueIDDisplay;

    View masterView;

    private String inQueue;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        masterView = inflater.inflate(R.layout.fragment_queueball, container, false);
        //TODO: animation queue ball to loading sign

        inQueue = checkInQueue(getActivity().getBaseContext().getFilesDir());
        if(inQueue.contentEquals("error")) {
            Log.d("ERROR READING", "error reading file to check for queue id/LaunchActivity");
            //file read didn't work, just start new queue for now

        } else if(inQueue.contentEquals("")) {
            //queue originates from this phone, play it

            /* Initiate GPS functionality */
            gpsReceiver = new GPSReceiver();
            gpsReceiver.initialize(false); //initialized from playing phone

            SoundQueue.PLAY = true;
            SoundQueue.createQueue();
            //TODO: Display after we know the creation was successful
            setupQueueBallOverlay();
            setupQueueView();

        } else {
            //queue is just being viewed from this phone, just display it.
            SoundQueue.PLAY = false;
            setupQueueView();
            Sender.createExecute(Sender.REQUEST_QUEUE, inQueue);

        }

        return masterView;
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
        queueBallLogic = (ImageButton)masterView.findViewById(R.id.queue_ball_logic_button);
        //setup show queue id button
        queueBallSelectBottom = (ImageButton)masterView.findViewById(R.id.queue_ball_select_bottom);
        //setup previous button
        queueBallSelectLeft = (ImageButton)masterView.findViewById(R.id.queue_ball_select_left);
        //setup next button
        queueBallSelectRight = (ImageButton)masterView.findViewById(R.id.queue_ball_select_right);
        //setup close queue button
        queueBallSelectTop = (ImageButton)masterView.findViewById(R.id.queue_ball_select_top);

        /* Create Listeners */

        queueBallLogic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showQueueBallOptions();
                hideQueueBallOptions();
                return false;
            }
        });

        queueBallLogic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: show tutorial button
            }
        });

        queueBallSelectBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayQueueID();
            }
        });

        queueBallSelectLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundPlayerController.playPreviousSound();
            }
        });

        queueBallSelectRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundPlayerController.playNextSound();
            }
        });

        queueBallSelectTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundQueue.close();
            }
        });

        /* Setup Queue QR code and ID display */

        //setup layout
        queueIDDisplayLayout = (LinearLayout)masterView.findViewById(R.id.queue_id_display_layout);
        //queueIDDisplayLayout.setPadding(0, 300, 0, 0);
        //queueIDDisplayLayout.setAnimation();

        //setup ID display
        queueIDDisplay = (TextView) masterView.findViewById(R.id.queue_id_display);
        queueIDDisplay.setVisibility(View.INVISIBLE);

        //setup QR code display
        qrCodeImageView = (ImageView) masterView.findViewById(R.id.qr_code_view);
        //TODO: add image of qr code

    }

    private void setupQueueView() {
        /* setup queue view */
        Constants.queueListView = (ListView) masterView.findViewById(R.id.queueView);

        /* create our queue adapter */
        Constants.queueListAdapter = new QueueListAdapter((LaunchActivity) this.getActivity());
        Constants.queueListView.setAdapter(Constants.queueListAdapter);
    }

    private void displayQueueID() {
        //TODO: animate queue id layout to move up from bottom of screen
        if(queueIDDisplay.getVisibility() == View.VISIBLE) {
            queueIDDisplay.setVisibility(View.INVISIBLE);
        }else {
            queueIDDisplay.setText(SoundQueue.ID);
            queueIDDisplay.setVisibility(View.VISIBLE);
            Log.e("QUEUE_ID", SoundQueue.ID);
        }
    }

    private String checkInQueue(File directory) {
        return IO.readQueueID(directory);
    }

    private void showQueueBallOptions() {
        //TODO: Set image back to expanded
        //queueBallImage.setImageDrawable();

        //set options clickable
        setOptionsClickable(true);
    }

    private void hideQueueBallOptions() {
        OptionsDisplayTimer optionsDisplayTimer = new OptionsDisplayTimer();
        //wait 5 seconds and then removes options and makes them non-clickable
        optionsDisplayTimer.start();
    }

    private void setOptionsClickable(boolean clickable) {
        queueBallSelectBottom.setClickable(clickable);
        queueBallSelectTop.setClickable(clickable);
        queueBallSelectLeft.setClickable(clickable);
        queueBallSelectRight.setClickable(clickable);
    }

    class OptionsDisplayTimer extends Thread {

        @Override
        public void run() {
            super.run();
            try {
                wait(5000);

                //TODO: Set image back to original state
                //queueBallImage.setImageDrawable();

                //set options to not be clickable
                setOptionsClickable(false);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
