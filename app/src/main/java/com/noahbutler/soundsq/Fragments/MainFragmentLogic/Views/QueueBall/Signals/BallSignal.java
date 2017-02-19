package com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views.QueueBall.Signals;

import android.content.Context;
import android.view.View;

import com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views.QueueBall.BallLogic;

/**
 * Created by Gildaroth on 2/13/2017.
 * This class takes input from the app
 * and passes it on to the BallLogic through Listeners
 */

public class BallSignal {

    public static final int SIGNAL_STATE = 321;
    public static final int SIGNAL_CREATE = 432;

    private BallLogic ballLogic;

    public BallSignal(View masterView, Context context) {
        this.ballLogic = new BallLogic(masterView, context);
    }

    /**
     * Used for state signalling
     * @param type
     * @param state
     */
    public void signal(int type, int state) {
        switch(type) {
            case SIGNAL_CREATE:
                break;
            case SIGNAL_STATE:
                this.ballLogic.onStateChange(state);
                break;
        }
    }

    /**
     * Used for create.
     * @param type
     */
    public void signal(int type) {
        switch(type) {
            case SIGNAL_CREATE:
                this.ballLogic.onCreate();
                break;
            case SIGNAL_STATE:
                break;
        }
    }
}
