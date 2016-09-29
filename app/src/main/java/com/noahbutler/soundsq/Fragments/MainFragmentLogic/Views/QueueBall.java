package com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.noahbutler.soundsq.R;
import com.noahbutler.soundsq.SoundPlayer.SoundPackage;
import com.noahbutler.soundsq.SoundPlayer.SoundQueue;

/**
 * Created by gildaroth on 9/28/16.
 *
 * QueueBall holds the views and
 * logic for the Queue Ball
 *
 * Views:
 * Queue Ball Image
 * Top, Bottom, left, Right option Buttons
 *
 * Image States:
 * Queue Ball
 * Queue Ball w/Options
 * Queue Ball Loading View
 *
 *
 */

public class QueueBall {


    /*************/
    /* DEBUG TAG */
    private static final String TAG = "QUEUE BALL";


    /**************/
    /* State Keys */
    public static final int STATE_QUEUE_BALL = 0;
    public static final int STATE_QUEUE_BALL_OPTIONS = 1;
    public static final int STATE_LOADING = 2;
    public static final int STATE_DELETE = 3;
    public static final int STATE_INVISIBLE = 4;


    /******************/
    /* Parent Objects */
    private View masterView;
    private Activity activity;


    /*********/
    /* Views */
    private TextView descriptionView;
    private Button queueBallSelectTop, queueBallSelectBottom, queueBallSelectLeft, queueBallSelectRight;
    private Button queueBallLogic;
    private ImageView queueBallImage;

    public QueueBall(View masterView, Activity activity) {
        this.masterView = masterView;
        this.activity = activity;

    }

    public void instantiate() {

        queueBallImage = (ImageView)masterView.findViewById(R.id.queue_ball_image);
        queueBallLogic = (Button)masterView.findViewById(R.id.queue_ball_logic_button);
        queueBallSelectBottom = (Button)masterView.findViewById(R.id.queue_ball_select_bottom);
        queueBallSelectLeft = (Button)masterView.findViewById(R.id.queue_ball_select_left);
        queueBallSelectRight = (Button)masterView.findViewById(R.id.queue_ball_select_right);
        queueBallSelectTop = (Button)masterView.findViewById(R.id.queue_ball_select_top);

        descriptionView = (TextView)masterView.findViewById(R.id.description);

        /* only want to display the image, not the logic buttons */
        setButtonsTransparent();

        /*
           Create our Listeners
         */

        queueBallLogic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clicked Queue Ball...");
                setState(STATE_QUEUE_BALL_OPTIONS);
            }
        });

        queueBallSelectBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Display Help Fragment
                setState(STATE_QUEUE_BALL);
            }
        });

        queueBallSelectLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: go to sound cloud
                setState(STATE_QUEUE_BALL);
            }
        });

        queueBallSelectRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete queue
                setState(STATE_DELETE);
            }
        });

        queueBallSelectTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setState(STATE_INVISIBLE);
            }
        });
    }

    public void setState(int state) {
        switch (state) {
            case STATE_QUEUE_BALL:
                displayQueueBall();
                break;
            case STATE_QUEUE_BALL_OPTIONS:
                displayQueueBallOptions();
                break;
            case STATE_LOADING:
                displayLoadingQueueBall();
                break;
            case STATE_DELETE:
                deleteQueue();
                break;
            case STATE_INVISIBLE:
                closeQueueBall();
        }
    }

    /**
     * Main Display Methods
     */

    private void displayQueueBall() {
        setBallVisibility(true);
        //TODO: replace with loaded in image
        //queueBallImage.setImageDrawable(queueBallImage);
        setBallClickable(true);
        setOptionsClickable(false);
    }

    private void displayQueueBallOptions() {
        //TODO: replace with loaded image
        //queueBallImage.setImageDrawable(queueBallOptionsImage);
        setBallClickable(false);
        setOptionsClickable(true);
    }

    private void displayLoadingQueueBall() {
        //TODO: display image as loading queue ball
        //queueBallImage.setImageDrawable(queueBallLoadingImage);
        setBallClickable(false);
        setOptionsClickable(false);
    }

    private void deleteQueue() {
        //TODO: remove queue_id and boolean from local file.
        //TODO: send message to server that queue needs to be deleted
    }

    private void closeQueueBall() {
        setBallVisibility(false);
        setDescriptionVisibility(false);
        setBallClickable(false);
        setOptionsClickable(false);
    }

    /**
     * Queue Ball Logic Methods
     */

    private void setBallVisibility(boolean visibility) {
        if(visibility) {
            queueBallImage.setVisibility(View.VISIBLE);
        }else{
            queueBallImage.setVisibility(View.INVISIBLE);
        }
    }

    private void setBallClickable(boolean clickable) {
        queueBallLogic.setClickable(clickable);
    }

    /**
     * Options Logic Methods
     */

    private void setButtonsTransparent() {
        queueBallLogic.setBackgroundColor(Color.TRANSPARENT);
        queueBallSelectBottom.setBackgroundColor(Color.TRANSPARENT);
        queueBallSelectLeft.setBackgroundColor(Color.TRANSPARENT);
        queueBallSelectRight.setBackgroundColor(Color.TRANSPARENT);
        queueBallSelectTop.setBackgroundColor(Color.TRANSPARENT);
    }

    private void setOptionsClickable(boolean clickable) {
        queueBallSelectBottom.setClickable(clickable);
        queueBallSelectTop.setClickable(clickable);
        queueBallSelectLeft.setClickable(clickable);
        queueBallSelectRight.setClickable(clickable);
    }

    /**
     * Sound Name Logic Methods
     */

    public void setDescription() {
        SoundPackage current = SoundQueue.getCurrentSoundPackage();
        String description = current.title + " - by " + current.artistName;
        this.descriptionView.setText(description);
    }

    public void setDescriptionVisibility(boolean visibility) {
        if(visibility) {
            descriptionView.setVisibility(View.VISIBLE);
        }else{
            descriptionView.setVisibility(View.INVISIBLE);
        }

    }
}
