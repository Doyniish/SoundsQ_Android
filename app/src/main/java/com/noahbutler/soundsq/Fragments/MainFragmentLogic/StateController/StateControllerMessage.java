package com.noahbutler.soundsq.Fragments.MainFragmentLogic.StateController;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

/**
 * Created by gildaroth on 9/28/16.
 */

public class StateControllerMessage {

    private Bundle bundle;
    private Message message;

    public static final String S_Key = "sound_url";
    public static final String A_Key = "album_art";
    public static final String Reg_Key = "register";

    public StateControllerMessage() {
        bundle = new Bundle();
        message = new Message();
    }

    public void spectatorLoaded() {
        bundle.putInt(StateController.UPDATE_KEY, StateController.SPEC_QUEUE_LOADED);

        message.setData(bundle);
        StateController.updateStream.sendMessage(message);
    }

    public void ownerLoaded() {
        bundle.putInt(StateController.UPDATE_KEY, StateController.OWNER_QUEUE_LOADED);

        message.setData(bundle);
        StateController.updateStream.sendMessage(message);
    }

    public void queueCreated() {
        bundle.putInt(StateController.UPDATE_KEY, StateController.QUEUE_CREATED);

        message.setData(bundle);
        StateController.updateStream.sendMessage(message);
    }

    public void soundCloudRegView(String register_url){
        bundle.putInt(StateController.UPDATE_KEY, StateController.SC_REGISTER);
        bundle.putString(Reg_Key, register_url);

        message.setData(bundle);
        StateController.updateStream.sendMessage(message);
    }

    public void updateQueueView(String fileLocation, String soundUrl) {
        bundle.putInt(StateController.UPDATE_KEY, StateController.UPDATE_VIEW);
        bundle.putString(A_Key, fileLocation);
        bundle.putString(S_Key, soundUrl);

        message.setData(bundle);
        StateController.updateStream.sendMessage(message);
    }

    public void updateQueueView() {
        bundle.putInt(StateController.UPDATE_KEY, StateController.PLAY_UPDATE);

        message.setData(bundle);
        StateController.updateStream.sendMessage(message);
    }

}
