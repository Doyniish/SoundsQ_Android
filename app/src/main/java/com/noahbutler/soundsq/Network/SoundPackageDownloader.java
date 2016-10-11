package com.noahbutler.soundsq.Network;

import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;

import com.noahbutler.soundsq.Fragments.MainFragmentLogic.StateController.StateControllerMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by NoahButler on 1/5/16.
 * This class downloads the images to be displayed on each sound package
 */
public class SoundPackageDownloader extends AsyncTask<String, String, Boolean> {
    /**
     * key used
     */
    public static final String GET_SOUND_IMAGE = "get_sound_image";
    private Context context;
    private PowerManager.WakeLock mWakeLock;
    private String fileLocation;
    private String soundUrl;

    public SoundPackageDownloader(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(String... params) {

        InputStream in = null;
        OutputStream out = null;

        HttpURLConnection connection = null;

        try {
            URL url = new URL(params[1]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return false;
            }
            fileLocation = params[2] + ".jpg";
            soundUrl = params[3];
            Log.e("ART_DOWNLOADER", "file: " + fileLocation);

            in = connection.getInputStream();
            out = context.openFileOutput(fileLocation, Context.MODE_PRIVATE);

            byte data[] = new byte[1000000];
            int count;
            while((count = in.read(data))!= -1) {

                out.write(data, 0, count);
            }
        }catch(IOException e) {
            Log.e("IMAGE_DOWNLOADER", e.getMessage());
        }finally {
            try {
                if(out != null) {
                    out.close();
                }
            }catch(IOException e) {
                Log.e("IO", e.getMessage());
            }
        }
        return true;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        mWakeLock.acquire();
    }


    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        mWakeLock.release();
        StateControllerMessage message = new StateControllerMessage();
        message.updateQueueView(fileLocation, soundUrl);
    }
}
