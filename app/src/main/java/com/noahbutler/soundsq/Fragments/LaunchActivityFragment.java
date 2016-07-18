package com.noahbutler.soundsq.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.noahbutler.soundsq.Constants;
import com.noahbutler.soundsq.Network.GCM.RegistrationIntentService;
import com.noahbutler.soundsq.Network.Sender;
import com.noahbutler.soundsq.QueueIDGenerator;
import com.noahbutler.soundsq.R;
import com.noahbutler.soundsq.SoundQueue;

import java.util.Random;
import java.util.concurrent.ExecutionException;

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
        View masterView = inflater.inflate(R.layout.fragment_launch_production, container, false);

        startNewQueueButton = (ImageButton)masterView.findViewById(R.id.start_new_queue);

        startNewQueueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundQueue.ID = QueueIDGenerator.generate();
                SoundQueue.hasQueuedSounds(true);

                Sender sender = new Sender();
                sender.execute(Sender.RUN_NEW_QID, SoundQueue.ID);
                getFragmentManager().beginTransaction().replace(R.id.main_content_area, new QueueFragment()).commit();

            }
        });

        helpButton = (ImageButton)masterView.findViewById(R.id.help_button);

        loadButton = (ImageButton)masterView.findViewById(R.id.load_button);


        return masterView;

    }
}
