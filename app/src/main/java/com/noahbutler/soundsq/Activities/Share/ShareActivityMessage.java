package com.noahbutler.soundsq.Activities.Share;

import android.os.Bundle;
import android.os.Message;

import org.json.JSONObject;

/**
 * Created by gildaroth on 10/3/16.
 */

public class ShareActivityMessage {

    Bundle bundle;
    Message message;

    public ShareActivityMessage() {
        bundle = new Bundle();
        message = new Message();
    }

    public void localQueuesLoaded(JSONObject localQueuesJSON) {
        bundle.putInt(ShareActivity.UPDATE_KEY, ShareActivity.LOCAL_QUEUES_LOADED);
        bundle.putString(ShareActivity.LOCAL_LIST, localQueuesJSON.toString());
        message.setData(bundle);
        ShareActivity.updateStream.sendMessage(message);
    }
}
