package com.noahbutler.soundsq.Fragments.MainFragmentLogic.StateController;

import android.util.Log;

/**
 * Created by gildaroth on 10/25/16.
 */

public class SaveState {

    public static final String TAG = "SaveState";

    public static boolean RESUMING = false;

    public static void onPause() {
        Log.e(TAG, "\n\nSave state OnPause...\n\n");
        RESUMING = true;
    }
}
