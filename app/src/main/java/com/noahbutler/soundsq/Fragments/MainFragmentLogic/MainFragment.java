package com.noahbutler.soundsq.Fragments.MainFragmentLogic;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.noahbutler.soundsq.Fragments.MainFragmentLogic.StateController.StateController;
import com.noahbutler.soundsq.R;

import org.json.JSONException;

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
        masterView = inflater.inflate(R.layout.fragment_queueball, container, false);
        /* Takes over and controls all flow */
        stateController = new StateController(masterView, getActivity());


        return masterView;
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
}
