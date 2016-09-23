package com.noahbutler.soundsq.Fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.noahbutler.soundsq.Network.Sender;

import java.util.HashMap;

/**
 * Created by gildaroth on 9/20/16.
 */
public class LocalQueuesFragment extends DialogFragment {

    public String soundLink;

    /* <Name, Queue id> */
    public static HashMap<String, String> localQueueList;

    private String[] keys;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

         keys = (String[])localQueueList.keySet().toArray();

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Local Queues")
                .setItems(keys, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String queue = localQueueList.get(keys[which]);

                        Sender.createExecute(Sender.SEND_SOUND, queue, soundLink);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
