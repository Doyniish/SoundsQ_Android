package com.noahbutler.soundsq.Activities.TestSpectrum;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by gildaroth on 11/29/16.
 */

public class AudioStreamer extends AsyncTask<String, Integer, Boolean> {

    private static final String TAG = "TEST-SPECTRUM";

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            BufferedInputStream in = new BufferedInputStream(new URL(params[0]).openStream());


        }catch(MalformedURLException e) {
            Log.e(TAG, "URL IS FUCKED");
        } catch (IOException e) {
            Log.e(TAG, "reading is not working.");
        }
        return true;
    }
}
