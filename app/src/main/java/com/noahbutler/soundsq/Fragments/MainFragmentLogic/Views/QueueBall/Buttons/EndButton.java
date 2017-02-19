package com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views.QueueBall.Buttons;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views.QueueBall.BallLogic;
import com.noahbutler.soundsq.IO.IO;
import com.noahbutler.soundsq.R;
import com.noahbutler.soundsq.SoundPlayer.SoundQueue;

/**
 * Created by Gildaroth on 2/13/2017.
 */

public class EndButton extends MasterButton {

    private ImageView deleteYes, deleteNo;
    private BallLogic ballLogic;

    public EndButton(View masterView, Context context) {
        this.button = (Button)masterView.findViewById(R.id.queue_ball_select_right);
        this.context = context;

        createOptions(masterView);
    }

    public void onClick(BallLogic ballLogic) {
        if(BallLogic.CURRENT_STATE == BallLogic.STATE_QUEUE_BALL_OPTIONS) {
            this.ballLogic = ballLogic;
            ballLogic.clickCallBack(BallLogic.STATE_DELETE);
        }
    }

    public void image(String image) {
        if(image.contains("delete")) {
            displayOptions(true);
        }
    }

    private void displayOptions(boolean d) {
        this.deleteYes.setClickable(d);
        this.deleteNo.setClickable(d);

        if(d) {
            //set yes and no option to visible
            this.deleteYes.setVisibility(View.VISIBLE);
            this.deleteNo.setVisibility(View.VISIBLE);
        }else {
            //set yes and no option to invisible
            this.deleteYes.setVisibility(View.INVISIBLE);
            this.deleteNo.setVisibility(View.INVISIBLE);

        }
    }

    private void createOptions(View masterView) {
        this.deleteYes = (ImageView)masterView.findViewById(R.id.check_delete_yes);
        this.deleteNo = (ImageView)masterView.findViewById(R.id.check_delete_no);

        Glide.with(this.context).load(R.drawable.yes).into(this.deleteYes);
        Glide.with(this.context).load(R.drawable.no).into(this.deleteNo);

        //don't display them until they are needed
        displayOptions(false);

        this.deleteYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IO.deleteQueueID(EndButton.this.context.getFilesDir());
                SoundQueue.close();
            }
        });
 
        this.deleteNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayOptions(false);
                ballLogic.clickCallBack(BallLogic.STATE_QUEUE_BALL_OPTIONS);
            }
        });
    }
}
