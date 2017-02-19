package com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views.QueueBall.Buttons;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views.QueueBall.BallLogic;
import com.noahbutler.soundsq.R;

/**
 * Created by Gildaroth on 2/13/2017.
 */

public class MiddleButton extends MasterButton {

    private ImageView queueBallImage;


    public MiddleButton(View masterView, Context context) {
        this.button = (Button)masterView.findViewById(R.id.queue_ball_logic_button);
        this.context = context;

        queueBallImage = (ImageView)masterView.findViewById(R.id.queue_ball_image);
        Glide.with(this.context).load(R.drawable.queue_ball).into(queueBallImage);
    }

    @Override
    public void onClick(BallLogic ballLogic) {
        Log.e(TAG, "Clicking Queue Ball, Current State: " + BallLogic.CURRENT_STATE);
        if(BallLogic.CURRENT_STATE == BallLogic.STATE_QUEUE_BALL) {
            //only if displaying red ball, show options.
            Log.e(TAG, "Show Ball w/Option");
            ballLogic.clickCallBack(BallLogic.STATE_QUEUE_BALL_OPTIONS);
        }
    }

    public void image(String image) {
        if(image.contains("delete")) {
            Glide.with(this.context).load(R.drawable.queue_ball_confirm).into(queueBallImage);

        }else if(image.contains("loading")) {
            Glide.with(this.context).load(R.drawable.queue_ball_loading).into(queueBallImage);

        }else if(image.contains("options")) {
            Glide.with(this.context).load(R.drawable.queue_ball_options).into(queueBallImage);

        }else if(image.contains("normal")) {
            Glide.with(this.context).load(R.drawable.queue_ball).into(queueBallImage);
        }
    }

    public void show(boolean s) {
        super.show(s);
        if(s) {
            queueBallImage.setVisibility(View.VISIBLE);
        }else {
            queueBallImage.setVisibility(View.INVISIBLE);
        }
    }
}
