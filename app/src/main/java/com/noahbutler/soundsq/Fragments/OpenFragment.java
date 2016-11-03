package com.noahbutler.soundsq.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.noahbutler.soundsq.Activities.LaunchActivity;
import com.noahbutler.soundsq.Fragments.MainFragmentLogic.MainFragment;
import com.noahbutler.soundsq.R;

/**
 * Created by gildaroth on 10/25/16.
 */

public class OpenFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View masterView = inflater.inflate(R.layout.fragment_open, container, false);

        new DisplayThread().start();

        return masterView;

    }

    class DisplayThread extends Thread {

        @Override
        public void run() {
            super.run();
            try {
                sleep(3000);
                LaunchActivity activity = (LaunchActivity)getActivity();
                activity.showMainFragment();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
