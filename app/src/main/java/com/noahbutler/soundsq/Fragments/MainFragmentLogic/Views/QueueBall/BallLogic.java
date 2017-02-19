package com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views.QueueBall;

import android.content.Context;
import android.view.View;

import com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views.QueueBall.Buttons.EndButton;
import com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views.QueueBall.Buttons.HideButton;
import com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views.QueueBall.Buttons.MasterButton;
import com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views.QueueBall.Buttons.MiddleButton;
import com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views.QueueBall.Buttons.SoundCloudButton;
import com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views.QueueBall.Buttons.TutorialButton;
import com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views.QueueBall.Listeners.CreateListener;
import com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views.QueueBall.Listeners.StateListener;
import com.noahbutler.soundsq.SoundPlayer.SoundQueue;

/**
 * Created by Gildaroth on 2/13/2017.
 */

public class BallLogic implements StateListener, CreateListener {

    /*************/
    /* DEBUG TAG */
    private static final String TAG = "BALL LOGIC";


    /**************/
    /* SoundQueueState Keys */
    public static int CURRENT_STATE = 0;
    public static final int STATE_QUEUE_BALL = 132;
    public static final int STATE_QUEUE_BALL_OPTIONS = 23241;
    public static final int STATE_LOADING = 34234;
    public static final int STATE_DELETE = 44234;
    public static final int STATE_INVISIBLE = 543241;


    /******************/
    /* Parent Objects */
    private View masterView;
    private Context context;

    /*********/
    /* Views */



    private SpectrumHandler spectrumHandler;

    /* Queue Ball Settings */
    private static final int WIDTH = 100;
    private static final int HEIGHT = 100;
    private static final int TOP = 0;
    private static final int LEFT = 1;
    private static final int MIDDLE = 2;
    private static final int RIGHT = 3;
    private static final int BOTTOM = 4;

    private MasterButton buttons[];


    public BallLogic(View masterView, Context context) {
        this.masterView = masterView;
        this.context = context;
    }

    @Override
    public void onCreate() {
        //TODO: create Track Name Overlay
        spectrumHandler = new SpectrumHandler(masterView);

        buttons = new MasterButton[5];
        buttons[TOP] = new HideButton(masterView, context);
        buttons[LEFT] = new SoundCloudButton(masterView, context);
        buttons[MIDDLE] = new MiddleButton(masterView, context);
        buttons[RIGHT] = new EndButton(masterView, context);
        buttons[BOTTOM] = new TutorialButton(masterView, context);

        //spectrum = new Spectrum(activity.getBaseContext());

        for(int i = 0; i < buttons.length; i++) {
            //set each button's bg to transparent so the user only sees our image
            buttons[i].transparent();
            //Create the listeners for each object
            buttons[i].button.setOnClickListener(createButtonListener(i, this));
        }
    }

    private View.OnClickListener createButtonListener(final int i, final BallLogic ballLogic) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttons[i].onClick(ballLogic);
            }
        };
    }

    @Override
    public void onStateChange(int STATE) {
        //set our current state to the requested one
        CURRENT_STATE = STATE;

        //switch our state to the requested one
        switch(CURRENT_STATE) {
            case STATE_QUEUE_BALL:
                //show only red ball
                state_queueBall();
                break;

            case STATE_QUEUE_BALL_OPTIONS:
                //show red ball w/options
                state_queueBallOptions();
                break;

            case STATE_LOADING:
                //show grey ball with loading text
                state_loading();
                break;

            case STATE_DELETE:
                //show delete options w/grey ball
                state_delete();
                break;

            case STATE_INVISIBLE:
                //turn everything off
                state_invisible();
                break;
        }
    }

    /****************************/
    /** SoundQueueState Changing Methods **/
    /****************************/

    private void state_queueBall() {
        //show just the ball w/spec if music is play
        if(SoundQueue.isPlayingSound()) {
            spectrumHandler.display(true);
        }
        allOff();
        buttons[MIDDLE].image("normal_display");
        buttons[MIDDLE].show(true);
    }

    private void state_queueBallOptions() {
        allOn();
        spectrumHandler.display(false);
        buttons[MIDDLE].image("options_display");
    }

    private void state_loading() {
        allOff();
        buttons[MIDDLE].show(true);
        buttons[MIDDLE].image("loading_display");
    }

    private void state_invisible() {

        allOff();
    }

    private void state_delete() {
        allOff();
        buttons[MIDDLE].show(true);
        //show grey
        buttons[MIDDLE].image("delete_display");
        //show yes and no buttons
        buttons[RIGHT].image("delete_display");

    }

    /********************/
    /** On/Off Switch **/
    /*******************/

    private void allOn() {
        for(int i = 0; i < buttons.length; i++) {
            buttons[i].show(true);
        }
    }

    private void allOff() {
        spectrumHandler.display(false);
        for(int i = 0; i < buttons.length; i++) {
            buttons[i].show(false);
        }
    }

    /****************************/
    /** Button Click Call Back **/
    /****************************/
    public void clickCallBack(int state) {
        onStateChange(state);
    }

}
