package com.noahbutler.soundsq.Fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.noahbutler.soundsq.Network.Sender;

/**
 * Created by gildaroth on 9/20/16.
 */
public class AutoSendDialogFragment extends DialogFragment {

    String sendStr = "Send";
    String cancelStr = "Cancel";

    public String readQueueID;
    public String soundLink;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_fire_missiles)
                .setPositiveButton(sendStr, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Sender.createExecute(Sender.SEND_SOUND, readQueueID, soundLink);
                    }
                })
                .setNegativeButton(cancelStr, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
