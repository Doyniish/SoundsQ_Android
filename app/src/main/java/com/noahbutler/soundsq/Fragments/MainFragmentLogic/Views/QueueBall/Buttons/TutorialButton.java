package com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views.QueueBall.Buttons;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.noahbutler.soundsq.R;

/**
 * Created by Gildaroth on 2/13/2017.
 */

public class TutorialButton extends MasterButton {

    public TutorialButton(View masterView, Context context) {
        this.button = (Button)masterView.findViewById(R.id.queue_ball_select_bottom);
        this.context = context;
    }
}
