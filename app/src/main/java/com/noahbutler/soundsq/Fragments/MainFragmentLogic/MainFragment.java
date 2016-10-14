package com.noahbutler.soundsq.Fragments.MainFragmentLogic;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.noahbutler.soundsq.Activities.LaunchActivity;
import com.noahbutler.soundsq.Fragments.MainFragmentLogic.StateController.StateController;
import com.noahbutler.soundsq.R;

/**
 * Used to determine if the app needs to load or start a queue.
 */
public class MainFragment extends Fragment {

    /*************/
    /* DEBUG TAG */
    private static final String TAG = "QUEUE BALL FRAG";


    /* Orientation */
    public static int CURRENT_LAYOUT = 0;


    /*******************/
    /* Local Variables */
    View masterView;
    StateController stateController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //check the orientation of the phone and display the correct configuration for the UI
        this.setLayout(inflater, container);
        //check to see if we need to recreate anything
        this.checkRotate(savedInstanceState);

        /* initiate our menu, customized in state controller */
        getActivity().setActionBar((Toolbar) masterView.findViewById(R.id.toolbar));

        return masterView;
    }

    private void checkRotate(Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            if (savedInstanceState.getInt(LaunchActivity.R_Key) == LaunchActivity.ROTATED) { //
                //signaled that we must check the objects created in this fragment.
                if (stateController == null) {
                    stateController = new StateController(masterView, getActivity(), LaunchActivity.ROTATED);
                } else {
                    stateController.checkRotate();
                }
            }
        } else {
            /* Takes over and controls all flow */
            stateController = new StateController(masterView, getActivity(), 0); // 0 for new
        }
    }

    private void setLayout(LayoutInflater inflater, ViewGroup container) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //TODO: make view for landscape.
            masterView = inflater.inflate(R.layout.fragment_queueball, container, false);
            CURRENT_LAYOUT = Configuration.ORIENTATION_LANDSCAPE;
        }else {
            masterView = inflater.inflate(R.layout.fragment_queueball, container, false);
            CURRENT_LAYOUT = Configuration.ORIENTATION_PORTRAIT;
        }
    }

    public void setMenuView(View menuView) {
        stateController.setMenuView(menuView);
    }

    @Override
    public void onStart() {
        super.onStart();
        stateController.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        stateController.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        stateController.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        stateController.onStop();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        stateController.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        stateController.onActivityResult(requestCode, resultCode, data);
    }
}
