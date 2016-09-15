package com.noahbutler.soundsq.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.noahbutler.soundsq.Network.Sender;
import com.noahbutler.soundsq.QueueIDGenerator;
import com.noahbutler.soundsq.R;
import com.noahbutler.soundsq.SoundPlayer.SoundQueue;

/**
 * A placeholder fragment containing a simple view.
 */
public class LaunchActivityFragment extends Fragment {

    private ImageButton startNewQueueButton;
    private ImageButton helpButton;
    private ImageButton loadButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View masterView = inflater.inflate(R.layout.fragment_launch_streamlined, container, false);

        SoundQueue.ID = QueueIDGenerator.generate();
        SoundQueue.hasQueuedSounds(true);

        Sender sender = new Sender();
        sender.execute(Sender.RUN_NEW_QID, SoundQueue.ID);
        getFragmentManager().beginTransaction().replace(R.id.main_content_area, new QueueFragment()).commit();

        return masterView;

    }
}
