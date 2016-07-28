package com.noahbutler.soundsq;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.noahbutler.soundsq.R;


/**
 * Created by Noah Butler on 7/24/2016.
 */
public class UIController {

    private Context context;
    private Bitmap soundGraphic;
    Intent prevPendingIntent;
    Intent pausePendingIntent;
    Intent nextPendingIntent;

    public UIController(Context context) {
        this.context = context;

//        Notification notification = new Notification.Builder(context)
//        // Show controls on lock screen even when user hides sensitive content.
//        .setVisibility(Notification.VISIBILITY_PUBLIC)
//        .setSmallIcon(R.drawable.ic_stat_player)
//        // Add media control buttons that invoke intents in your media service
//        .addAction(R.drawable.ic_prev,"Previous", prevPendingIntent) // #0
//        .addAction(R.drawable.ic_pause, "Pause", pausePendingIntent)  // #1
//        .addAction(R.drawable.ic_next, "Next", nextPendingIntent)     // #2
//        // Apply the media style template
//        .setStyle(new Notification.MediaStyle()
//                .setShowActionsInCompactView(1 /* #1: pause button */)
//                .setMediaSession(mMediaSession.getSessionToken())
//                .setContentTitle("Wonderful music")
//                .setContentText("My Awesome Band")
//                .setLargeIcon()
//                .build());
    }
}
