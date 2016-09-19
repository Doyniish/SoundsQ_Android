package com.noahbutler.soundsq.SoundPlayer;

import com.noahbutler.soundsq.SoundPlayer.SoundPlayer;
import com.noahbutler.soundsq.SoundPlayer.SoundPlayerController;
import com.noahbutler.soundsq.ThreadUtils.Messenger;

import java.util.ArrayList;

/**
 * Created by NoahButler on 6/30/16.
 */
public class SoundQueue {

    public static String ID = null;
    public static String NAME = null;
    private static boolean HAS_QUEUE = false;
    public static boolean PLAY = false;

    private static ArrayList<String> queue;
    public static ArrayList<SoundPackage> queue_packages;
    private static int currentSound;

    /**
     * Boolean that holds the state: the current application is playing a sound or not.
     */
    private static boolean PLAYING_SOUND;
    /**
     * Boolean that holds the state: the current application has queued sounds or not.
     */
    private static boolean QUEUED_SOUNDS;

    public static void addSound(String streamUrl) {
        queue.add(streamUrl);
        sendToSoundPlayerController(streamUrl);
    }

    public static void addSoundPackage(SoundPackage soundPackage) {
        queue_packages.add(soundPackage);
    }

    public static void createQueue() {
        queue = new ArrayList<>();
        queue_packages = new ArrayList<>();
    }

    private static void sendToSoundPlayerController(String streamUrl) {
        //run our checks to see if we need to play this sound right now
        if ((firstSongCheck() || soundPlayingCheck()) && PLAY) {
            SoundPlayerController.playCurrentSound();
        }
    }

    private static boolean firstSongCheck() {
        return (queue.size() == 1);
    }

    private static boolean soundPlayingCheck() {
        return !isPlayingSound(); //should not play if this is true
    }

    public static int size() {
        return queue.size() - 1;
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

    public static void setIndex(int index) {
        currentSound = index;
    }

    public static int getCurrentIndex() {
        return currentSound;
    }

    public static String getCurrentSound() {
        return queue.get(currentSound);
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
