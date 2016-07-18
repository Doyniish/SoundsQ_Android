package com.noahbutler.soundsq;

import android.content.Context;

import java.io.File;


/**
 * Created by NoahButler on 1/5/16.
 *
 * Class used to store downloaded info about a specific sound
 */
public class SoundPackage {

    /**
     * file location of the sound's image.
     * This is set to null until the SoundPackageDownloader
     * receives the address of the image from our servers,
     * and then downloads the image. It is then set with
     * the sendFileLocation().
     */
    public String soundImage;
    /**
     * Name of the sound. Set when our server response with
     * the sound data from sound cloud's api
     */
    public String soundName;
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

    /**
     * Method that is called when the correct
     * image for this sound has been downloaded.
     *
     * The SoundPackageDownloader sends a signal via the Messenger
     * and the MessageHandler. The messageHandler finds the correct SoundPackage
     * object and then calls this method with the image file location.
     *
     * Look to MessageHandler for more information.
     * @param fileLocation
     */
    public void sendFileLocation(String fileLocation) {
        soundImage = fileLocation;
    }

}
