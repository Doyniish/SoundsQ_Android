package com.noahbutler.soundsq;

/**
 * Created by NoahButler on 9/18/15.
 */
public class Constants {
    /**
     * File that contains a cached queue id.
     */
    public static final String CACHE_FILE = "soundQ_file_cache.sq";
    /**
     * Amount of characters in a QUEUE ID
     */
    public static final int QUEUE_ID_LENGTH = 10;
    /**
     * Token of the current phone, given by FBM (Fire Base Messaging). Used to send
     * down stream messages from our server to the correct phone.
     */
    public static String token = null;

}
