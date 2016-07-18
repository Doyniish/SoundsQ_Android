package com.noahbutler.soundsq;

import com.noahbutler.soundsq.ThreadUtils.Messenger;

import java.util.ArrayList;

/**
 * Created by NoahButler on 6/30/16.
 */
public class SoundQueue {

    public static String ID = null;
    private static boolean HAS_QUEUE = false;

    private static ArrayList<String> queue;
    private static int currentSound;

    /**
     * Boolean that holds the state: the current application is playing a sound or not.
     */
    private static boolean PLAYING_SOUND;
    /**
     * Boolean that holds the state: the current application has queued sounds or not.
     */
    private static boolean QUEUED_SOUNDS;

    private static Messenger messenger = new Messenger();
    public static void addSound(String url) {
        queue.add(url);
        //check to see if we need to play this sound
        if (queue.size() == 1) { // first sound added
            messenger.playSound(getCurrentSound());
        }else if(!isPlayingSound()) {//we are not playing any songs
            nextSong();
            messenger.playSound(getCurrentSound());
        }
    }

    public static void createQueue() {
        queue = new ArrayList<>();
    }

    public static int size() {
        return queue.size();
    }

    public static void nextSong() {
        currentSound++;
    }

    public static void prevSong() {
        currentSound--;
    }

    public static void topSong() {
        currentSound = 0;
    }

    public static int getCurrentIndex() {
        return currentSound;
    }

    public static String getCurrentSound() {
        return queue.get(currentSound);
    }

    public static String getLatestSound() {
        return queue.get(queue.size() - 1);
    }

    public static void isPlayingSound(boolean is) {
        PLAYING_SOUND = is;
    }

    public static boolean isPlayingSound() {
        return PLAYING_SOUND;
    }

    public static void hasQueuedSounds(boolean has) {
        QUEUED_SOUNDS = has;
    }

    public static boolean hasQueuedSounds() {
        return QUEUED_SOUNDS;
    }

}
