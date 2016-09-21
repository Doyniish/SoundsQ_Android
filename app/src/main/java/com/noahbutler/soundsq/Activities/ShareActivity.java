package com.noahbutler.soundsq.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.noahbutler.soundsq.Constants;
import com.noahbutler.soundsq.Fragments.AutoSendDialogFragment;
import com.noahbutler.soundsq.IO.IO;
import com.noahbutler.soundsq.Network.Sender;
import com.noahbutler.soundsq.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by NoahButler on 12/27/15.
 */
public class ShareActivity extends Activity {

    EditText enterQueueID;
    String soundLink;
    String readQueueID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);


        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();



        if(Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendSound(intent); // Handle text being sent
            }
        }
    }

    private void handleSendSound(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);

        if (sharedText != null) {
            soundLink = sharedText.substring(sharedText.lastIndexOf("http"));
            cleanLink();

            /* Display QR Code Scanner and Queue ID TextInput */
            displayAddQueueID();

            //check for existing queue id
            readQueueID = checkQueueFile();

            /* check for errors or if it actually exists */
            if(readQueueID != null) {
                displayAutoSend();
            }else{ //no cached queue id
                Toast.makeText(getBaseContext(), "Join a SoundQ session!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void cleanLink() {
        /* remove https and replace with http */
        soundLink = soundLink.substring(5);
        soundLink = "http" + soundLink;
    }

    private String checkQueueFile() {
        /* check for queue id saved on phone */
        String readQueueID = IO.readQueueID(getBaseContext().getFilesDir());
        /* look to see if anything in the file was read in */
        Log.e("R", readQueueID);
        if(!readQueueID.contentEquals("")) {
            return readQueueID;
        }
        return null;
    }

    private void displayAutoSend() {
        AutoSendDialogFragment autoSendDialogFragment = new AutoSendDialogFragment();

        //don't forget to set params
        autoSendDialogFragment.readQueueID = readQueueID;
        autoSendDialogFragment.soundLink = soundLink;

        autoSendDialogFragment.show(getFragmentManager(), "");
    }

    private void displayAddQueueID() {
        //TODO: display QR Code Scanner

        enterQueueID = (EditText)findViewById(R.id.enter_queue_id_join);
        enterQueueID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutoSendChecker autoSendChecker = new AutoSendChecker();
                autoSendChecker.start();
            }
        });
    }

    public static void failedShare() {
        //TODO: display failed share alert
    }

    class AutoSendChecker extends Thread {

        @Override
        public void run() {
            super.run();

            //wait for the user to enter in a valid id
            while(enterQueueID.getText().length() < Constants.QUEUE_ID_LENGTH) {}

            //auto send
            Sender.createExecute(Sender.SEND_SOUND, enterQueueID.getText().toString(), soundLink);
            Toast.makeText(getBaseContext(), "Sound has been sent!", Toast.LENGTH_LONG).show();

            //add queue id to cache file for later use.
            IO.writeQueueID(getBaseContext().getFilesDir(), enterQueueID.getText().toString());

            //return to SoundCloud
            finish();
        }
    }
}
