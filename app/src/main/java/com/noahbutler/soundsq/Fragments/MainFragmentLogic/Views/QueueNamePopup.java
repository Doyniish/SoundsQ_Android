package com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.noahbutler.soundsq.Network.Sender;
import com.noahbutler.soundsq.R;
import com.noahbutler.soundsq.SoundPlayer.SoundQueue;

/**
 * Created by gildaroth on 10/3/16.
 */

public class QueueNamePopup extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View view = inflater.inflate(R.layout.dialog_queue_name, null);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText name = (EditText)view.findViewById(R.id.queue_name_entry);
                        String entry = name.getText().toString();
                        SoundQueue.NAME = entry;
                        Sender.createExecute(Sender.SEND_NAME, SoundQueue.NAME);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO: canceled, don't set queue name
                    }
                });
        return builder.create();
    }
}
