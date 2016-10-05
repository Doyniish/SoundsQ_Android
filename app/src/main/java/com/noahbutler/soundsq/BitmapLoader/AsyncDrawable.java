package com.noahbutler.soundsq.BitmapLoader;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by gildaroth on 10/4/16.
 */

public class AsyncDrawable extends BitmapDrawable {
    private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskWeakReference;

    public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
        super(res, bitmap);
        bitmapWorkerTaskWeakReference = new WeakReference<>(bitmapWorkerTask);
    }

    private BitmapWorkerTask getBitmapWorkerTask() {
        return bitmapWorkerTaskWeakReference.get();
    }

    public static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if(imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if(drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    public static void loadBitmap(Resources res, int id, int reqWidth, int reqHeight, ImageView imageView) {
        if(cancelPotentialWork(id, imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(res, imageView);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(res, null, task);

            imageView.setImageDrawable(asyncDrawable);
            task.execute(id, reqWidth, reqHeight);
        }
    }

    private static boolean cancelPotentialWork(int id, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if(bitmapWorkerTask != null) {
            final int bitmapID = bitmapWorkerTask.id;

            if(bitmapID == 0 || bitmapID != id) {
                bitmapWorkerTask.cancel(true);
            } else {
                //The same work is already in progress
                return false;
            }
        }
        //No task associated with the ImageView, or an existing task was cancelled
        return true;
    }
}

