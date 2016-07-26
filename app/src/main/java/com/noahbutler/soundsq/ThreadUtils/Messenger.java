package com.noahbutler.soundsq.ThreadUtils;

import android.os.Bundle;
import android.os.Message;

import com.noahbutler.soundsq.Constants;

/**
 * Created by NoahButler on 1/3/16.
 */
public class Messenger {

    /**
     * Objects that are used to communicate between
     * two threads.
     */
    private Message message;
    private Bundle bundle;

    /**
     * Our keys data bank,
     * allows us to know which message is coming through
     * the pipe from one thread to another.
     */
    public static String[] keys = {
            "update",
            "sound_image_location",
            "play_sound"
    };

    /**
     * Method that creates the necessary objects
     * needed to convey messages from thread to thread
     */
    private void init() {
        message = new Message();
        bundle = new Bundle();
    }

    /**
     * Gateway method,
     * Allows async tasks to affect objects belonging to the
     * UI thread. Look at MessageHandler for more details
     *
     * Associated with keys[0]
     */
    public void updateViews() {
        init();
        String string = "";
        bundle.putString(keys[0], string);
        message.setData(bundle);
        Constants.handler.sendMessage(message);
    }

    /**
     * Gateway method,
     * Allows async tasks to affect objects belonging to the
     * UI thread. Look at MessageHandler for more details
     *
     * Associated with keys[1]
     */
    public void sendSoundImageLocation(String fileLocation) {
        init();
        bundle.putString(keys[1], fileLocation);
        message.setData(bundle);
        Constants.handler.sendMessage(message);
    }

    /**
     * Gateway method,
     * Allows async tasks to affect objects belonging to the
     * UI thread. Look at MessageHandler for more details
     *
     * Associated with keys[2]
     */
    public void requestNextPlay() {
        init();
        //send url from queue list
        bundle.putString(keys[2], "next_sound_please");
        message.setData(bundle);
        Constants.handler.sendMessage(message);
    }

}
