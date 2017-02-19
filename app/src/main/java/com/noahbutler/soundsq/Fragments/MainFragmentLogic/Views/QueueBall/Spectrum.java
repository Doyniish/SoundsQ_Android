package com.noahbutler.soundsq.Fragments.MainFragmentLogic.Views.QueueBall;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.noahbutler.soundsq.SoundPlayer.SoundQueue;

import java.util.Random;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Created by gildaroth on 12/28/16.
 */

public class Spectrum extends View {

    /*************/
    /* DEBUG TAG */
    private static final String TAG = "SPECTRUM";

    float mX;
    float mY;
    int radius;
    Paint paint;
    Point[] dots;
    boolean reset;
    boolean draw;

    /* Receives message from spectrum thread to update view */
    private static Handler handler;


    public Spectrum(Context context) {
        super(context);
        init();
    }

    public Spectrum(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if(msg.getData().containsKey("update")) {
                    invalidate();
                }
                return false;
            }
        });

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        WindowManager w = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
        mX = w.getDefaultDisplay().getWidth()/2;
        mY = convertDpToPixel(150 + 70, this.getContext());
        radius = 450;
        reset = false;

        //init point array
        dots = new Point[36];
        for(int i = 0; i < 36; i++) {
            dots[i] = new Point();
        }
        plot();
        update();
    }

    /**
     * plot calculates a new position for each dot.
     * A max of 30 and a min of 5 is applied in either direction
     */
    private void plot() {
        Random r = new Random();
        for(int i = 0; i < 36; i++) {
            int randX = r.nextInt(200) - 100;
            int randY = r.nextInt(200) - 100;
            int angle = i * 10;

            if (reset) {
                dots[i].x = (int) (mX + radius * cos(angle * Math.PI / 180));
                dots[i].y = (int) (mY + radius * sin(angle * Math.PI / 180));
            } else {
                dots[i].x = (int) (mX + radius * cos(angle * Math.PI / 180)) + randX;
                dots[i].y = (int) (mY + radius * sin(angle * Math.PI / 180)) + randY;
            }
        }

        if(reset) {
            reset = false;
        }else{
            reset = true;
        }
    }

    /**
     * Update redraws the spectrum every 1 seconds to a new view
     */
    public void update() {

        //TODO: only do if showing spectrum
        new Thread(new Runnable() {


            long current = System.currentTimeMillis();
            long last = System.currentTimeMillis();

            @Override
            public void run() {

                while(true) {
                    if(SoundQueue.isPlayingSound()) {
                        draw = true;

                        if (current - last > 160) {
                            Message message = new Message();
                            Bundle bundle = new Bundle();

                            bundle.putInt("update", 1);
                            message.setData(bundle);

                            plot();

                            Spectrum.handler.sendMessage(message);
                            last = current;
                        } else {
                            current = System.currentTimeMillis();
                        }
                    }else {
                        reset = true;
                        Message message = new Message();
                        Bundle bundle = new Bundle();

                        bundle.putInt("update", 1);
                        message.setData(bundle);

                        plot();
                        Spectrum.handler.sendMessage(message);

                    }
                }
            }
        }).start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        String check = "";
        //Log.e(TAG, Boolean.toString(draw));
        if(draw) {
            //Log.e(TAG, "DRAWING SPECTRUM");
            for (int i = 0; i < 36; i++) {
                check += "{" + i + ": [" + dots[i].x + ", " + dots[i].y + "]} ";
                canvas.drawCircle(dots[i].x, dots[i].y, 15, paint);
                if (i == 35) {
                    canvas.drawLine(dots[i].x, dots[i].y, dots[0].x, dots[0].y, paint);
                } else {
                    canvas.drawLine(dots[i].x, dots[i].y, dots[i + 1].x, dots[i + 1].y, paint);
                }
            }
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            draw = false;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            draw = true;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }


}
