package com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views.QueueBall;

import android.view.View;

import com.noahbutler.soundsq.R;

/**
 * Created by Gildaroth on 2/14/2017.
 */

public class SpectrumHandler {

    Spectrum spectrum;

    public SpectrumHandler(View masterView) {
        spectrum = (Spectrum)masterView.findViewById(R.id.spectrum_view);

    }

    public void display(boolean d) {
        if(d) {
            spectrum.setVisibility(View.VISIBLE);
        }else {
            spectrum.setVisibility(View.INVISIBLE);
        }

    }
}
