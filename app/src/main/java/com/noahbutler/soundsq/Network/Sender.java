package com.noahbutler.soundsq.Network;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.noahbutler.soundsq.Constants;
import com.noahbutler.soundsq.SoundPlayer.SoundQueue;
import com.noahbutler.soundsq.ThreadUtils.Messenger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Created by NoahButler on 9/6/15.
 */
public class Sender extends AsyncTask<String, Integer, Boolean> {

    public static final String SEND_TOKEN = "send_token";
    public static final String RUN_NEW_QID = "run_new_qid";
    public static final String SEND_SOUND = "send_sound";
    public static final String CHECK_QUEUE = "check_queue";
    public static final String REQUEST_QUEUE = "request_queue";
    public static final String CLOSE_QUEUE = "close_queue";
    public static final String SENDER_GPS = "sender_gps";//looking for queues gps
    public static final String QUEUE_GPS = "queue_gps"; //queue owner gps
    //104.236.237.151
    private static final String SEND_TOKEN_URL = "http://104.236.237.151/send/token/";
    private static final String SEND_NEW_QID_URL = "http://104.236.237.151/new/queue/";
    private static final String SEND_SOUND_URL   = "http://104.236.237.151/sound/send/";
    private static final String CHECK_QUEUE_URL = "http://104.236.237.151/queue/exists/";
    private static final String REQUEST_QUEUE_URL = "http://104.236.237.151/request/queue/";
    private static final String CLOSE_QUEUE_URL = "http://104.236.237.151/close/queue/";
    private static final String SENDER_GPS_URL = "http://104.236.237.151/gps/sender/";
    private static final String QUEUE_GPS_URL = "http://104.236.237.151/gps/queue/";

    private String queue_id_key = "queue_id";
    private String singleSound_key = "sound_url";
    private String user_token_key = "user_token";
    private String lat_key = "lat_coord";
    private String long_key = "long_coord";

    private Activity activty;

    public static void createExecute(String...strings) {
        Sender sender = new Sender();
        sender.execute(strings);
    }

    @Override
    protected Boolean doInBackground(String...strings) {

        switch(strings[0]) {
            case SEND_TOKEN:
                return sendToken(strings);
            case RUN_NEW_QID:
                return sendNewQID(strings);
            case SEND_SOUND:
                return sendSound(strings);
            case CHECK_QUEUE:
                return checkQueue(strings);
            case REQUEST_QUEUE:
                return requestQueue(strings);
            case CLOSE_QUEUE:
                return closeQueue(strings);
            case SENDER_GPS:
                return senderGPS(strings);
            case QUEUE_GPS:
                return queueGPS(strings);
            default:
                Log.e("ERROR", "NOT A METHOD IN STRINGS[0]");
                return false;
        }
    }

    private boolean sendToken(String...strings) {
        String[] keys = new String[2];

        keys[0] = queue_id_key;
        keys[1] = user_token_key;

        String[] values = new String[2];
        values[0] = SoundQueue.ID; //queue id for the new queue
        values[1] = Constants.token; //the current phone android FCM token

        NetworkGate networkGate = new NetworkGate(SEND_TOKEN_URL);
        return networkGate.post(keys, values);
    }

    private boolean sendNewQID(String...strings) {
        String[] keys = new String[2];

        keys[0] = queue_id_key;
        keys[1] = user_token_key;

        String[] values = new String[2];
        values[0] = strings[1]; //queue id for the new queue
        values[1] = Constants.token; //the current phones android FCM token

        NetworkGate networkGate = new NetworkGate(SEND_NEW_QID_URL);
        return networkGate.post(keys, values);
    }

    private boolean closeQueue(String...strings) {
        String[] keys = new String[1];
        String[] values = new String[1];

        keys[0] = queue_id_key;
        values[0] = SoundQueue.ID;

        NetworkGate networkGate = new NetworkGate(CLOSE_QUEUE_URL);
        return networkGate.post(keys, values);
    }

    private boolean requestQueue(String...strings) {
        String[] keys = new String[2];

        keys[0] = queue_id_key;
        keys[1] = user_token_key;

        String[] values = new String[2];

        values[0] = strings[1];
        values[1] = Constants.token;

        Log.d("Request Queue", "requesting queue...");
        NetworkGate networkGate = new NetworkGate(REQUEST_QUEUE_URL);
        return networkGate.post(keys, values);
    }

    private boolean checkQueue(String...strings) {
        String[] keys = new String[1];
        String[] values = new String[1];
        keys[0] = queue_id_key;
        values[0] = strings[1];

        NetworkGate networkGate = new NetworkGate(CHECK_QUEUE_URL);
        return networkGate.post(keys, values);
    }

    private boolean sendSound(String...strings) {

        String[] keys = new String[strings.length - 1];
        String[] values = new String[strings.length - 1];
        //queue id that the person is trying to send the sound to.
        values[0] = strings[1];

        //Sound the person is trying to send
        values[1] = strings[2];

        keys[0] = queue_id_key;
        keys[1] = singleSound_key;

        //Send the information to the server
        NetworkGate networkGate = new NetworkGate(SEND_SOUND_URL);
        return networkGate.post(keys, values);
    }

    private boolean senderGPS(String...strings) {
        String[] keys = new String[3];
        String[] values = new String[3];

        keys[0] = "to";
        keys[1] = lat_key;
        keys[2] = long_key;

        values[0] = Constants.token;
        values[1] = strings[1];
        values[2] = strings[2];

        NetworkGate networkGate = new NetworkGate(SENDER_GPS_URL);
        return networkGate.post(keys, values);
    }

    private boolean queueGPS(String...strings) {
        String[] keys = new String[3];
        String[] values = new String[3];

        keys[0] = queue_id_key;
        keys[1] = lat_key;
        keys[2] = long_key;

        values[0] = SoundQueue.ID;
        values[1] = strings[1];
        values[2] = strings[2];

        NetworkGate networkGate = new NetworkGate(QUEUE_GPS_URL);
        return networkGate.post(keys, values);
    }

    /**
     * Class that is use to house the different network HTTP requests
     * quick are housed in their own methods.
     *
     * Each time a request is wanting to be made the methods call this class
     * will always make a new instance of this class
     *
     */
    private class NetworkGate {

        URL url;
        HttpURLConnection urlConnection;
        String sendingToURL;

        StringBuilder stringBuilder;

        NetworkGate(String sendingToURL) {
            this.sendingToURL = sendingToURL;
            this.stringBuilder = new StringBuilder();
        }

        private void createURLConnection() throws IOException {
            url = new URL(sendingToURL);
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        }

        private void createDataPackage(String[] keys, String[] values) {
            for(int i = 0; i < keys.length; i++) {

                stringBuilder.append(keys[i]);
                stringBuilder.append("=");
                stringBuilder.append(values[i]);

                if(i == keys.length - 1) { //last ending is different
                }else{
                    stringBuilder.append(";");
                }
            }
        }

        private void sendData() throws IOException {
            //create the output stream for the post data
            BufferedOutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            out.write(stringBuilder.toString().getBytes());
            out.flush();
            Log.d("DATA SENT", stringBuilder.toString());
        }

        private void respond() throws IOException {
            String responseMsg = urlConnection.getResponseMessage();
            int responseCode = urlConnection.getResponseCode();

            Log.d("RESPONSE CODE", "" + responseCode);
            Log.d("RESPONSE MESSAGE", responseMsg);

                /* respond to Server */
            Response.startResponse(responseCode);
        }

        private void error(IOException e) {
            Log.e("ERROR","something fucked up in the post method of the NetworkGate class");
            Log.e("ERROR", e.toString());
        }

        public boolean post(String[] keys, String[] values) {

            try {

                createURLConnection();
                createDataPackage(keys, values);
                sendData();
                respond();

                return true;
            }catch(IOException e) {
                error(e);
                return false;
            }
        }
    }

}
