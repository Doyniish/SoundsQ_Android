package com.noahbutler.soundsq.Activities.TestSpectrum;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.noahbutler.soundsq.R;
import com.noahbutler.soundsq.SoundPlayer.SoundPlayer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by gildaroth on 11/2/16.
 */

public class TestSpectrumFragment extends Fragment {

    private static final String TAG = "TEST-SPECTRUM";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View masterView = inflater.inflate(R.layout.fragment_test_spectrum, container, false);
        String constStreamUrl = "https://cf-media.sndcdn.com/0PIzU7mswlfp.128.mp3?Policy=eyJTdGF0ZW1lbnQiOlt7IlJlc291cmNlIjoiKjovL2NmLW1lZGlhLnNuZGNkbi5jb20vMFBJelU3bXN3bGZwLjEyOC5tcDMiLCJDb25kaXRpb24iOnsiRGF0ZUxlc3NUaGFuIjp7IkFXUzpFcG9jaFRpbWUiOjE0NzgyMDYzMTN9fX1dfQ__&Signature=RUO0FgxyJuUNsG5-nKGt8jORIJyOECzrV6YgKylvhYP83EIpWzx-jw~Z4ZXLFmoLTUp~9vXAPa1696cvDIjQrm1EUhGDudfpiAshXeEyYleby2fYor9KU3HW30Z39NMNSW4MRlVjVbbJkN7wz~QaXRjOG0sJLZAw2yoPJ3aZQv4pYDnfxvcdmdLocuSdmMtM6t9FbjisxlJqi23RSTtoXcymuYA~U46xIVvfc~ciHjNl0NOE8ZSxC3xDB0~3nWExSeOa49zp-Ce31vBSr7K94RL9g08kCNTavVSGSHl9SpeiSXzZkR6lk8hCCDR8BwJLhkTgvlj5yZr1Z91tGKI96w__&Key-Pair-Id=APKAJAGZ7VMH2PFPW6UQ";

        AudioStreamer audioStreamer = new AudioStreamer();
        audioStreamer.execute(constStreamUrl);

        return masterView;
    }
}
