package com.noahbutler.soundsq;

import android.widget.ListView;

import com.noahbutler.soundsq.Fragments.QueueListAdapter;
import com.noahbutler.soundsq.SoundPlayer.SoundPackage;
import com.noahbutler.soundsq.ThreadUtils.MessageHandler;
import com.noahbutler.soundsq.ThreadUtils.Messenger;

import java.util.ArrayList;

/**
 * Created by NoahButler on 9/18/15.
 */
public class Constants {
    /**
     * File that contains a cached queue id.
     */
    public static final String CACHE_FILE = "soundQ_queue_id_cache";
    /**
     * Key string used to signal the cached id is no longer in use.
     */
    public static final String DELETED_QUEUE = "queue_deleted";
    /**
     * Token of the current phone, given by GCM (Google Cloud Messaging). Used to send
     * down stream messages from our server to the correct phone.
     */
    public static String token = null;
    /**
     * List of SoundPackage objects each associated with a soundcloud.com url in
     * QUEUE_LIST. The objects hold the name, artist, and image file location of each sound.
     */
    public static ArrayList<SoundPackage> QUEUE_SOUND_PACKAGES = new ArrayList<>();
    /**
     * List view that displays the queue.
     */
    public static ListView queueListView;
    /**
     * List Adapter that creates each element for the list view above.
     */
    public static QueueListAdapter queueListAdapter;
    /**
     * Receives signals from threads on the UI thread.
     * Then, runs the respected code for each type of message.
     */
    public static MessageHandler handler;
    /**
     * Sends signals created on different threads to send to the UI thread.
     */
    public static Messenger messenger = new Messenger();

}
