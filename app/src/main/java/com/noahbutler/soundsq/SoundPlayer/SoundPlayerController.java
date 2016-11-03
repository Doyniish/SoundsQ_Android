package com.noahbutler.soundsq.SoundPlayer;

import android.content.Context;
import android.util.Log;

import com.noahbutler.soundsq.Fragments.MainFragmentLogic.StateController.StateControllerMessage;
import com.noahbutler.soundsq.Network.Sender;

/**
 * Created by Noah on 7/25/2016.
 */
public class SoundPlayerController {

    public static final String TAG = "SoundPlayerController";

    private static SoundPlayer soundPlayer;
    private static Context context;

    private static void setContext(Context c) {
        context = c;
    }

    public static void createController(Context c) {
        setContext(c);
    }

    /*  */
    public static void playNextSound() {
        SoundQueue.nextSong();
        Log.e(TAG, "Current Index: " + SoundQueue.getCurrentIndex() + ", Current size: " + SoundQueue.size());
        Log.e(TAG, "Play next: " + (SoundQueue.getCurrentIndex() < SoundQueue.size()));
        if (SoundQueue.getCurrentIndex() < SoundQueue.size()) {
            SoundQueue.isPlayingSound(true);

            //update ui
            SoundQueue.getCurrentSoundPackage().isPlaying = true;
            new StateControllerMessage().updateQueueView();
            Log.e(TAG, "next sound is going to play, index: " + SoundQueue.getCurrentIndex());

            soundPlayer = new SoundPlayer(context);
            soundPlayer.execute(SoundQueue.getCurrentSound());
        }
    }

    public static void playCurrentSound() {
        SoundQueue.isPlayingSound(true);
        soundPlayer = new SoundPlayer(context);
        soundPlayer.execute(SoundQueue.getCurrentSound());
    }

    public static void pauseCurrentSound() {
        SoundQueue.isPlayingSound(false);
        if(soundPlayer != null) {
            soundPlayer.pause();
        }
    }

    public static void resumeCurrentSound() {
        SoundQueue.isPlayingSound(true);
        if(soundPlayer != null) {
            soundPlayer.resume();
        }
    }

    public static void playPreviousSound() {
        SoundQueue.prevSong();
        soundPlayer = new SoundPlayer(context);
        SoundQueue.isPlayingSound(true);
        soundPlayer.execute(SoundQueue.getCurrentSound());
    }

    public static void playSelectedSound(int index) {
        soundPlayer.stopPlaying();
        SoundQueue.setIndex(index);
        soundPlayer = new SoundPlayer(context);
        SoundQueue.isPlayingSound(true);
        soundPlayer.execute(SoundQueue.getCurrentSound());
    }

    /* SoundPlayer Requests */
    public static void soundPlayerFinished() {
        SoundQueue.isPlayingSound(false);
        SoundQueue.getCurrentSoundPackage().isPlaying = false;
        Log.e(TAG, "next sound requested, just played index: " + SoundQueue.getCurrentIndex());
        playNextSound();
    }

    public static void close() {
        if(soundPlayer != null) {
            soundPlayer.stopPlaying();
        }
    }
}
