package com.noahbutler.soundsq.ThreadUtils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.noahbutler.soundsq.Constants;
import com.noahbutler.soundsq.SoundPlayer.SoundPlayerController;

/**
 * Created by NoahButler on 1/6/16.
 */
public class MessageHandler extends Handler {

    /**
     * Method that handles incoming messages from threads
     * that are being sent to the UI thread.
     * @param msg
     */
    @Override
    public void handleMessage(Message msg) {
        if(msg.getData().containsKey(Messenger.keys[0])) { // update views when a new sound has arrived
            signal_updateQueue();

        }else if(msg.getData().containsKey(Messenger.keys[1])) { // sound art has finished downloading
            signal_soundPackageDownloaded(msg);

        }else if(msg.getData().containsKey(Messenger.keys[2])) { // next song needs to be played
            //url sent to signal_playSound(String url)
            signal_requestToController(msg.getData().getString(Messenger.keys[2]));
        }
    }

    /**
     * Associated with Messenger.keys[0]
     * This is signaled by the DownStreamReceiver.
     *
     * It is signaled when the app needs to update the queue view because
     * a new sound has been added to the list
     */
    private void signal_updateQueue() {
        Constants.queueListView.invalidateViews();
        Constants.queueListAdapter.notifyDataSetChanged();
    }

    /**
     * Associated with Messenger.keys[1]
     * This is signaled by a SoundPackageDownloader object.
     *
     * It is signaled when the app needs to update a current sound on the queue
     * with the sound image (after it has been downloaded by the the SoundPackageDownloader).
     *
     * The image's (which has been cached on the device) location is sent
     * in the message as a string with the key: Messenger.keys[1].
     *
     * The method looks for the SoundPackage object associated with the image
     * (which is saved as the name of the song) and then sets the sound package image string
     * as the given one.
     *
     * The method then tell the queue view to update so that it now knows to display the given image.
     *
     * @param msg
     */
    private void signal_soundPackageDownloaded(Message msg) {
        String rawMsg = msg.getData().getString(Messenger.keys[1]);
        Log.e("HANDLER", "raw message: " + rawMsg);
            /* grab the correct sound package */
        for(int i = 0; i < Constants.QUEUE_SOUND_PACKAGES.size(); i++) {
            if(Constants.QUEUE_SOUND_PACKAGES.get(i).sound_url.contains(rawMsg.substring(0, rawMsg.lastIndexOf(".")))) {
                    /* update the sound package's sound image file to the downloaded one */
                Constants.QUEUE_SOUND_PACKAGES.get(i).sendFileLocation(rawMsg);
            }
        }
            /* update our views to now display the sound image */
        Constants.queueListView.invalidateViews();
        Constants.queueListAdapter.notifyDataSetChanged();
    }

    /**
     * Associated with Messenger.keys[2]
     * This is signaled by a SoundPlayer object when a sound is finished playing
     */
    private void signal_requestToController(String url) {
        SoundPlayerController.playNextSound();
    }
}
