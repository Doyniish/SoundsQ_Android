package com.noahbutler.soundsq.SoundPlayer;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.HashMap;


/**
 * Created by NoahButler on 1/5/16.
 *
 * Class used to store downloaded info about a specific sound
 */
public class SoundPackage {

    /**
     * Stores the url for glide. Glide will then handle showing the image.
     */
    public String soundImage;
    /**
     * Title of the sound. Set when our server response with
     * the sound data from sound cloud's api
     */
    public String title;
    /**
     * Name of the artist. Set when our server response with
     * the sound data from sound cloud's api
     */
    public String artistName;
    /**
     * Sound cloud url associated with this sound.
     * used as a key for linking the SoundPackage object
     * with the correct item in the list view of the queue.
     */
    public String sound_url;
    /**
     * Boolean to hold the state: this sound is currently playing or not.
     */
    public boolean isPlaying;

    /**
     * Constructor that defaults the sound image location string
     * and isPlaying state to false. These are set by other parts
     * of the application.
     */
    public SoundPackage() {
        soundImage = null;
        isPlaying = false;
    }

    public static SoundPackage createSoundPackage(HashMap<String, String> data) {
        SoundPackage soundPackage = new SoundPackage();
        soundPackage.sound_url = data.get("sound_url");
        soundPackage.artistName = data.get("artist");
        soundPackage.title = data.get("title");
        soundPackage.soundImage = (data.get("album_art"));

        return soundPackage;
    }

}
