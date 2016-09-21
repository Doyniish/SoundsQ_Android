package com.noahbutler.soundsq.SoundPlayer;

import android.content.Context;
import android.util.Log;

import com.noahbutler.soundsq.Network.Sender;

/**
 * Created by Noah on 7/25/2016.
 */
public class SoundPlayerController {

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
        if (SoundQueue.getCurrentIndex() < SoundQueue.size()) {
            SoundQueue.isPlayingSound(true);
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
        soundPlayer.pause();
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
        playNextSound();
    }
}
