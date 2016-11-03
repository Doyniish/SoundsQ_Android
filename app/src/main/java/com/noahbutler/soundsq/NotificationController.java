package com.noahbutler.soundsq;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.noahbutler.soundsq.Activities.LaunchActivity;
import com.noahbutler.soundsq.R;


/**
 * Created by Noah Butler on 7/24/2016.
 */
public class NotificationController extends Notification {

    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_REWIND = "action_rewind";
    public static final String ACTION_FAST_FORWARD = "action_fast_foward";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PREVIOUS = "action_previous";
    public static final String ACTION_STOP = "action_stop";

    private Context context;
    private NotificationManager notificationManager;

    public NotificationController(Context context) {
        super();

        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.Builder builder = new Notification.Builder(context);
        @SuppressWarnings("deprecation")
        Notification notification = builder.getNotification();
        notification.when = System.currentTimeMillis();
        notification.tickerText = "Test Ticker Text";
        notification.icon = R.drawable.icon_top_bar;

        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification_control_view);

        //set the button listeners
        setListeners(contentView);

        notification.contentView = contentView;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        CharSequence contentTitle = "From Shortcuts";
        notificationManager.notify(548853, notification);
    }

    private void setListeners(RemoteViews view) {
        //pause listener
        Intent radio=new Intent(context, LaunchActivity.class);
        radio.putExtra("DO", "pause");
        PendingIntent pause = PendingIntent.getActivity(context, 0, radio, 0);
        view.setOnClickPendingIntent(R.id.status_bar_play, pause);

        //volume listener
        Intent volume=new Intent(context, LaunchActivity.class);
        volume.putExtra("DO", "volume");
        PendingIntent pVolume = PendingIntent.getActivity(context, 1, volume, 0);
        //view.setOnClickPendingIntent(R.id., pVolume);

        //reboot listener
        Intent reboot=new Intent(context, LaunchActivity.class);
        reboot.putExtra("DO", "reboot");
        PendingIntent pReboot = PendingIntent.getActivity(context, 5, reboot, 0);
        //view.setOnClickPendingIntent(R.id.reboot, pReboot);

        //top listener
        Intent top=new Intent(context, LaunchActivity.class);
        top.putExtra("DO", "top");
        PendingIntent pTop = PendingIntent.getActivity(context, 3, top, 0);
        //view.setOnClickPendingIntent(R.id.top, pTop);

        //app listener
        //Intent app=new Intent(context, com.example.demo.HelperActivity.class);
        //app.putExtra("DO", "app");
        //PendingIntent pApp = PendingIntent.getActivity(context, 4, app, 0);
        //view.setOnClickPendingIntent(R.id.btn1, pApp);
    }
}
