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

    public static void setContext(Context c) {
        context = c;
    }


    /*  */
    public static void playNextSound() {
        SoundQueue.nextSong();
        //TODO: Make sure there is an actual sound to play
        if (SoundQueue.getCurrentIndex() == SoundQueue.size())
        soundPlayer = new SoundPlayer(context);
        soundPlayer.execute(SoundQueue.getCurrentSound());
    }

    public static void playCurrentSound() {
        SoundQueue.isPlayingSound(true);
        soundPlayer = new SoundPlayer(context);
        soundPlayer.execute(SoundQueue.getCurrentSound());
    }

    public static void playPreviousSound() {
        SoundQueue.prevSong();
        soundPlayer = new SoundPlayer(context);
        SoundQueue.isPlayingSound(true);
        soundPlayer.execute(SoundQueue.getCurrentSound());
    }

    public static void playSelectedSound(int index) {
        //TODO: create signal to stop the sound first
        soundPlayer = null;
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

    /* Request to Server Methods */
    public static void requestSoundData(String streamUrl) {
         retrieveSoundPackage(streamUrl);
    }

    private static void retrieveSoundPackage(String streamUrl) {
        Log.d("SOUND PACKAGE", "retrieving sound package");
        Sender sender = new Sender();
        sender.execute(Sender.GET_SOUND_INFO_PACKAGE, SoundQueue.ID, streamUrl);
    }
}
