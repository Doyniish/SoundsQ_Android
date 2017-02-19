package com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views.QueueBall.Buttons;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views.QueueBall.BallLogic;

/**
 * Created by Gildaroth on 2/13/2017.
 */

public class MasterButton implements ButtonState {

    protected String TAG = "BUTTON";

    public Button button;
    Context context;

    @Override
    public void transparent() {
        button.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public void show(boolean s) {
        if(s) {
            enable();
        }else {
            disable();
        }
    }

    @Override
    public void onClick(BallLogic ballLogic) {}

    @Override
    public void image(String special) {}

    private void enable() {
        button.setVisibility(View.VISIBLE);
        button.setClickable(true);
    }

    private void disable() {
        button.setClickable(false);
        button.setVisibility(View.INVISIBLE);
    }
}
