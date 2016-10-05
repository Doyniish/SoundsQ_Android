package com.noahbutler.soundsq.Fragments.MainFragmentLogic.StateController;

import android.os.Bundle;
import android.os.Message;

/**
 * Created by gildaroth on 9/28/16.
 */

public class StateControllerMessage {

    private Bundle bundle;
    private Message message;

    public StateControllerMessage() {
        bundle = new Bundle();
        message = new Message();
    }

    public void spectatorLoaded() {
        bundle.putInt(StateController.UPDATE_KEY, StateController.SPEC_QUEUE_LOADED);
        message.setData(bundle);
        StateController.updateStream.sendMessage(message);
    }

    public void freshStartCompleted() {
        bundle.putInt(StateController.UPDATE_KEY, StateController.QUEUE_CREATED);
        message.setData(bundle);
        StateController.updateStream.sendMessage(message);
    }

}
