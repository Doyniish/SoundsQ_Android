package com.noahbutler.soundsq.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.noahbutler.soundsq.Constants;
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
            String sound_link = sharedText.substring(sharedText.lastIndexOf("http"));
            /* remove https and replace with http */
            sound_link = sound_link.substring(5);
            sound_link = "http" + sound_link;
            showAddQueueUI(sound_link);

            /**
            Log.d("SOUND", "link: " + sound_link);

             check for existing queue id
            String cached_queue_id = null;

            cached_queue_id = checkQueueLink();
            /* check for errors or if it actually exists
            if(cached_queue_id != null) {

                /* if the queue has been deleted by the queue owner
                if(cached_queue_id.contentEquals(Constants.DELETED_QUEUE)) {
                    Toast.makeText(getBaseContext(), "Sorry, but your saved queue has been deleted...", Toast.LENGTH_LONG).show();
                    showAddQueueUI(sound_link);

                }else{ // we have the go ahead to send the sound to the queue cached by the user.

                    /* create our sender and send it to the server
                    Sender sender = new Sender();
                    sender.execute(Sender.SEND_SOUND, cached_queue_id, sound_link);

                }

            }else{ //no cached queue id
                Toast.makeText(getBaseContext(), "Join a SoundQ session!", Toast.LENGTH_LONG).show();
                showAddQueueUI(sound_link);
            }

            */
        }
    }

    /**
     * This method is designed to check for the file that saves the current queue id that the user
     * is linked to, checks if it is still a queue and if both check off, then the song is send to
     * the queue id that this method returns
     * @return
     */
    private String checkQueueLink() {
        /* check for queue id saved on phone */

        StringBuilder stringBuilder = new StringBuilder();
        /* open cache file */
        File file = new File(getBaseContext().getFilesDir(), Constants.CACHE_FILE);
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String raw;
            while((raw = bufferedReader.readLine()) != null) {
                stringBuilder.append(raw);
                Log.d("FILE", raw);
            }
            bufferedReader.close();
        }catch(IOException e){
            Log.d("FILE_READ_ERROR", e.getMessage());
        }

        /* look to see if anything in the file was read in */
        Log.e("R", stringBuilder.toString());
        if(stringBuilder.toString().contentEquals("")) {
            return null;
        }else{//check the saved queue id on the database
            Sender sender = new Sender();
            try {
                if(sender.execute(Sender.CHECK_QUEUE, stringBuilder.toString()).get()) {//queue is active, send song to that list
                    return stringBuilder.toString();
                }else{ // queue is inactive/not in the database
                    return Constants.DELETED_QUEUE;
                }
            }catch(InterruptedException | ExecutionException e) {
                Log.d("ERROR", e.getMessage());
            }

            return stringBuilder.toString();
        }
    }

    private void showAddQueueUI(final String sound_link) {
        Button joinQueue;
        final EditText enterQueueID;

        setContentView(R.layout.activity_share);

        joinQueue = (Button)findViewById(R.id.join_queue_button);
        enterQueueID = (EditText)findViewById(R.id.enter_queue_id_join);

        joinQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sender sender = new Sender();
                if(!enterQueueID.getText().toString().equals("")) {
                    try {
                        if (sender.execute(Sender.SEND_SOUND, enterQueueID.getText().toString(), sound_link).get()) {
                            Toast.makeText(getBaseContext(), "Sound has been sent!", Toast.LENGTH_LONG).show();
                            //add queue id to cache file for later use.
                            IO.writeQueueID(getBaseContext().getFilesDir(), enterQueueID.getText().toString());
                            finish();
                        }else{
                            enterQueueID.clearComposingText();
                            Toast.makeText(getBaseContext(), "Doesn't look like the enter ID exists, try again please!", Toast.LENGTH_LONG).show();
                        }
                    }catch(InterruptedException | ExecutionException e) {
                        Log.d("ERROR", e.getMessage());
                    }
                }else{

                }
            }
        });

    }
}
