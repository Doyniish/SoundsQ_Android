package com.noahbutler.soundsq.Activities;

import android.app.Activity;
import android.os.Bundle;

import com.noahbutler.soundsq.Fragments.TestSpectrumFragment;
import com.noahbutler.soundsq.R;

/**
 * Created by gildaroth on 11/3/16.
 */

public class TestSpectrumActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        getFragmentManager().beginTransaction().replace(R.id.main_content_area, new TestSpectrumFragment()).commit();
    }
}
