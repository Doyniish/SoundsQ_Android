package com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views.QueueBall;

import android.view.View;
import android.widget.TextView;

import com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views.QueueBall.Buttons.ButtonState;
import com.noahbutler.soundsq.SoundPlayer.SoundQueue;

/**
 * Created by Gildaroth on 2/13/2017.
 */

public class TrackNameOverlay implements ButtonState {

    private TextView descriptionView;

    @Override
    public void transparent() {

    }

    @Override
    public void show(boolean s) {
        if(s && SoundQueue.isPlayingSound()) {
            descriptionView.setVisibility(View.VISIBLE);
        }else{
            descriptionView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(BallLogic ballLogic) {

    }

    @Override
    public void image(String image) {

    }
}
