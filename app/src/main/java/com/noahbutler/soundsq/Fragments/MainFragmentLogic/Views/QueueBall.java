package com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.noahbutler.soundsq.BitmapLoader.AsyncDrawable;
import com.noahbutler.soundsq.IO.IO;
import com.noahbutler.soundsq.Network.Sender;
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
    private int CURRENT_STATE = 0;
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
    private ImageView queueBallImage, deleteYes, deleteNo;


    /* Queue Ball Settings */
    private static final int WIDTH = 100;
    private static final int HEIGHT = 100;

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

        deleteYes = (ImageView)masterView.findViewById(R.id.check_delete_yes);
        deleteNo = (ImageView)masterView.findViewById(R.id.check_delete_no);
        hideDeleteCheck();

        descriptionView = (TextView)masterView.findViewById(R.id.description);

        /* only want to display the image, not the logic buttons */
        setButtonsTransparent();
        setBallClickable(false);
        setOptionsClickable(false);

        /*
           Create our Listeners
         */

        queueBallLogic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CURRENT_STATE == STATE_QUEUE_BALL_OPTIONS) {
                    setState(STATE_QUEUE_BALL);
                }else{
                    CURRENT_STATE = STATE_QUEUE_BALL_OPTIONS;
                    setState(STATE_QUEUE_BALL_OPTIONS);
                }
            }
        });

        queueBallSelectBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Display Help Fragment
                setState(STATE_INVISIBLE);
            }
        });

        queueBallSelectLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: go to sound cloud
                setState(STATE_INVISIBLE);
            }
        });

        queueBallSelectRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setState(STATE_DELETE);
            }
        });

        queueBallSelectTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setState(STATE_INVISIBLE);
            }
        });

        deleteYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IO.deleteQueueID(activity.getBaseContext().getFilesDir());
                SoundQueue.close();
                hideDeleteCheck();
            }
        });

        deleteNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideDeleteCheck();
            }
        });
    }

    public void setState(int state) {
        switch (state) {
            case STATE_QUEUE_BALL:
                CURRENT_STATE = STATE_QUEUE_BALL;
                displayQueueBall();
                break;
            case STATE_QUEUE_BALL_OPTIONS:
                displayQueueBallOptions();
                break;
            case STATE_LOADING:
                CURRENT_STATE = STATE_LOADING;
                displayLoadingQueueBall();
                break;
            case STATE_DELETE:
                displayDeleteCheck();
                break;
            case STATE_INVISIBLE:
                CURRENT_STATE = STATE_INVISIBLE;
                closeQueueBall();
        }
    }

    /**
     * Main Display Methods
     */

    private void displayQueueBall() {
        setBallVisibility(true);
        if(!(CURRENT_STATE == STATE_LOADING)) {
            AsyncDrawable.loadBitmap(activity.getResources(), R.drawable.queue_ball, WIDTH, HEIGHT, queueBallImage);
            setDescription();
            setDescriptionVisibility(true);
            setBallClickable(true);
            setBallVisibility(true);
            setOptionsClickable(false);
        }
    }

    private void displayQueueBallOptions() {
        AsyncDrawable.loadBitmap(activity.getResources(), R.drawable.queue_ball_options, WIDTH, HEIGHT, queueBallImage);
        setBallClickable(false);
        setOptionsClickable(true);
    }

    private void displayLoadingQueueBall() {
        AsyncDrawable.loadBitmap(activity.getResources(), R.drawable.queue_ball_loading, WIDTH, HEIGHT, queueBallImage);
        setBallClickable(false);
        setDescriptionVisibility(false);
        setOptionsClickable(false);
    }

    private void displayDeleteCheck() {
        deleteNo.setVisibility(View.VISIBLE);
        deleteYes.setVisibility(View.VISIBLE);
        AsyncDrawable.loadBitmap(activity.getResources(), R.drawable.queue_ball_confirm, WIDTH, HEIGHT, queueBallImage);
        AsyncDrawable.loadBitmap(activity.getResources(), R.drawable.yes, 50, 50, deleteYes);
        AsyncDrawable.loadBitmap(activity.getResources(), R.drawable.no, 50, 50, deleteNo);
        deleteYes.setClickable(true);
        deleteNo.setClickable(true);
    }

    private void hideDeleteCheck() {
        AsyncDrawable.loadBitmap(activity.getResources(), R.drawable.queue_ball, WIDTH, HEIGHT, queueBallImage);
        deleteYes.setVisibility(View.INVISIBLE);
        deleteNo.setVisibility(View.INVISIBLE);
        deleteYes.setClickable(false);
        deleteNo.setClickable(false);
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
        if(SoundQueue.getCurrentSoundPackage() != null) {
            SoundPackage current = SoundQueue.getCurrentSoundPackage();
            String description = current.title + " - by " + current.artistName;
            this.descriptionView.setText(description);
        }
    }

    public void setDescriptionVisibility(boolean visibility) {
        if(visibility && SoundQueue.isPlayingSound()) {
            descriptionView.setVisibility(View.VISIBLE);
        }else{
            descriptionView.setVisibility(View.INVISIBLE);
        }

    }
}
