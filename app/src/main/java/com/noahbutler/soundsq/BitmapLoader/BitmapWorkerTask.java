package com.noahbutler.soundsq.BitmapLoader;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by gildaroth on 10/4/16.
 */

public class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {


    /***********************/
    /* Reference Variables */
    private final WeakReference<ImageView> imageViewWeakReference;
    private final WeakReference<Resources> resourcesWeakReference;


    /*********************/
    /* Public Variables */
    public int id = 0;


    public BitmapWorkerTask(Resources res, ImageView imageView) {
        resourcesWeakReference = new WeakReference<>(res);
        imageViewWeakReference = new WeakReference<>(imageView);
    }

    @Override
    protected Bitmap doInBackground(Integer... params) {
        id = params[0];
        return BitmapLoader.decodeSampledBitmap(resourcesWeakReference.get(), params[0], params[1], params[2]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if(isCancelled()) {
            bitmap = null;
        }

        if(imageViewWeakReference != null && bitmap != null) {
            final ImageView imageView = imageViewWeakReference.get();
            final BitmapWorkerTask bitmapWorkerTask = AsyncDrawable.getBitmapWorkerTask(imageView);
            if(this == bitmapWorkerTask && imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
