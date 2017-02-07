package com.noahbutler.soundsq.Fragments.MainFragmentLogic;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.noahbutler.soundsq.Activities.LaunchActivity;
import com.noahbutler.soundsq.Fragments.MainFragmentLogic.StateController.StateController;
import com.noahbutler.soundsq.R;
import com.noahbutler.soundsq.SoundPlayer.SoundQueue;

/**
 * Used to determine if the app needs to load or start a queue.
 */
public class MainFragment extends Fragment {

    /*************/
    /* DEBUG TAG */
    private static final String TAG = "QUEUE BALL FRAG";


    /*******************/
    /* Local Variables */
    View masterView;
    StateController stateController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //check the orientation of the phone and display the correct configuration for the UI
        if(savedInstanceState == null) {
            masterView = inflater.inflate(R.layout.fragment_queueball, container, false);
            stateController = new StateController(masterView, getActivity());

        /* initiate our menu, customized in state controller */
            getActivity().setActionBar((Toolbar) masterView.findViewById(R.id.toolbar));
        }else {
            Log.e(TAG, "\n\nSaved instance state loading...\n\n");
            if(stateController == null) {
                stateController = new StateController(masterView, getActivity());
            }
            stateController.onSavedInstanceRestored(savedInstanceState);
        }
        return masterView;
    }


    public void setMenuView(View menuView) {
        stateController.setMenuView(menuView);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        stateController.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        stateController.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            stateController.onResume(getActivity().getFilesDir());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stateController.onPause(getActivity().getFilesDir());
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


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return stateController.onKeyDown(keyCode, event);
    }
}
