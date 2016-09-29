package com.noahbutler.soundsq.Fragments.MainFragmentLogic.StateController;

import android.os.Bundle;
import android.os.Message;

/**
 * Created by gildaroth on 9/28/16.
 */

public class StateControllerMessage {

    Bundle bundle;
    Message message;

    public void init() {
        bundle = new Bundle();
        message = new Message();
    }

    public void spectatorLoaded() {
        init();
        bundle.putInt(StateController.UPDATE_KEY, StateController.SPEC_QUEUE_LOADED);
        message.setData(bundle);
        StateController.updateStream.sendMessage(message);
    }

}
