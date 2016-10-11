package com.noahbutler.soundsq.Fragments.MainFragmentLogic;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.noahbutler.soundsq.Fragments.MainFragmentLogic.StateController.StateController;
import com.noahbutler.soundsq.R;

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

        /* initiate our menu, customized in state controller */
        getActivity().setActionBar((Toolbar) masterView.findViewById(R.id.toolbar));

        /* Takes over and controls all flow */
        stateController = new StateController(masterView, getActivity());


        return masterView;
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
