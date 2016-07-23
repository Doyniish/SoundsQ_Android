package com.noahbutler.soundsq.Notification;

/**
 * Created by NoahButler on 7/12/16.
 */
public class NotificationData {
    private String current_soundName;
    private String current_artistName;

    public NotificationData(String current_artistName, String current_soundName) {
        this.current_artistName = current_artistName;
        this.current_soundName = current_soundName;
    }

    public String getCurrent_soundName() {
        return current_soundName;
    }

    public String getCurrent_artistName() {
        return current_artistName;
    }

}
