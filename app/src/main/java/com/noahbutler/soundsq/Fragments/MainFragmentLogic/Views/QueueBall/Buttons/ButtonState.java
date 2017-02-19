package com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views.QueueBall.Buttons;


import com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views.QueueBall.BallLogic;

/**
 * Created by Gildaroth on 2/13/2017.
 */

public interface ButtonState {

    /* used to turn off the default bg of a normal button */
    void transparent();

    /* used to turn the button on or off */
    void show(boolean s);

    /* used to run button's onClick functionality ballLogic passed for callback */
    void onClick(BallLogic ballLogic);

    /* used to show delete and loading */
    void image(String image);
}
