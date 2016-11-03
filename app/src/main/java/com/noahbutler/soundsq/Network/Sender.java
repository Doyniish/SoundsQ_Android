package com.noahbutler.soundsq.Network;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.noahbutler.soundsq.Constants;
import com.noahbutler.soundsq.Fragments.MainFragmentLogic.StateController.UserState;
import com.noahbutler.soundsq.SoundPlayer.SoundQueue;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by NoahButler on 9/6/15.
 */
public class Sender extends AsyncTask<String, Integer, Boolean> {

    public static final String TAG = "Sender";

    public static final String SEND_TOKEN = "send_token";
    public static final String NEW_QUEUE = "run_new_qid";
    public static final String SEND_NAME = "send_name";
    public static final String SEND_SOUND = "send_sound";
    public static final String CHECK_QUEUE = "check_queue";
    public static final String REQUEST_QUEUE = "request_queue";
    public static final String CLOSE_QUEUE = "close_queue";
    public static final String LIKED_SOUND = "liked_sound";
    public static final String SENDER_GPS = "sender_gps";//looking for queues gps
    public static final String QUEUE_GPS = "queue_gps"; //queue owner gps
    //104.236.237.151
    private static final String SEND_TOKEN_URL = "http://104.236.237.151/send/token/";
    private static final String SEND_NEW_QID_URL = "http://104.236.237.151/new/queue/";
    private static final String SEND_NAME_URL = "http://104.236.237.151/send/name/";
    private static final String SEND_SOUND_URL   = "http://104.236.237.151/send/sound/";
    private static final String CHECK_QUEUE_URL = "http://104.236.237.151/queue/exists/";
    private static final String REQUEST_QUEUE_URL = "http://104.236.237.151/request/queue/";
    private static final String CLOSE_QUEUE_URL = "http://104.236.237.151/close/queue/";
    private static final String LIKED_SOUND_URL = "http://104.236.237.151/liked/sound/";
    private static final String SENDER_GPS_URL = "http://104.236.237.151/gps/sender/";
    private static final String QUEUE_GPS_URL = "http://104.236.237.151/gps/queue/";

    private static String Q_Key = "queue_id";
    private static String N_Key = "queue_name";
    private static String S_Key = "sound_url";
    private static String T_Key = "user_token";
    private static String Lat_Key = "latitude";
    private static String Long_Key = "longitude";
    private static String User_Key = "username";
    private static String Pass_Key = "password";

    /* used to determine if the user was deleting.
       must wait until onPostExecute, to close the app.
     */
    private static boolean DELETING = false;

    public static void createExecute(String...strings) {
        Sender sender = new Sender();
        sender.execute(strings);
    }

    protected void onPostExecute(Boolean result) {
        if(DELETING) { //close the app after we have deleted it from the server
            int pid = android.os.Process.myPid();
            android.os.Process.killProcess(pid);
        }
    }

    @Override
    protected Boolean doInBackground(String...strings) {

        switch(strings[0]) {
            case SEND_TOKEN:
                return sendToken(strings);
            case NEW_QUEUE:
                return newQueue(strings);
            case SEND_NAME:
                return sendName(strings);
            case SEND_SOUND:
                return sendSound(strings);
            case CHECK_QUEUE:
                return checkQueue(strings);
            case REQUEST_QUEUE:
                return requestQueue(strings);
            case CLOSE_QUEUE:
                return closeQueue(strings);
            case LIKED_SOUND:
                return likedSound(strings);
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

        keys[0] = Q_Key;
        keys[1] = T_Key;

        String[] values = new String[2];
        values[0] = SoundQueue.ID; //queue id for the new queue
        values[1] = Constants.token; //the current phone android FCM token

        NetworkGate networkGate = new NetworkGate(SEND_TOKEN_URL);
        return networkGate.post(keys, values);
    }

    private boolean newQueue(String...strings) {
        String[] keys = new String[4];

        keys[0] = Q_Key;
        keys[1] = T_Key;
        keys[2] = Lat_Key;
        keys[3] = Long_Key;

        String[] values = new String[4];
        values[0] = SoundQueue.ID;
        values[1] = Constants.token; //the current phones android FCM token
        values[2] = strings[1]; //latitude
        values[3] = strings[2]; //longitude


        NetworkGate networkGate = new NetworkGate(SEND_NEW_QID_URL);
        return networkGate.post(keys, values);
    }

    private boolean sendName(String...strings) {
        String[] keys = new String[2];

        keys[0] = Q_Key;
        keys[1] = N_Key;

        String[] values = new String[2];
        values[0] = SoundQueue.ID;
        values[1] = strings[1];

        NetworkGate networkGate = new NetworkGate(SEND_NAME_URL);
        return networkGate.post(keys, values);
    }

    private boolean likedSound(String...strings) {
        String[] keys = new String[2];
        keys[0] = S_Key;
        keys[1] = T_Key;

        String[] values = new String[2];
        values[0] = strings[1];
        values[1] = Constants.token;

        NetworkGate networkGate = new NetworkGate(LIKED_SOUND_URL);
        return networkGate.post(keys, values);
    }

    private boolean closeQueue(String...strings) {
        String[] keys = new String[1];
        String[] values = new String[1];

        keys[0] = Q_Key;
        values[0] = SoundQueue.ID;

        DELETING = true;

        NetworkGate networkGate = new NetworkGate(CLOSE_QUEUE_URL);
        return networkGate.post(keys, values);
    }

    private boolean requestQueue(String...strings) {
        String[] keys = new String[2];

        keys[0] = Q_Key;
        keys[1] = T_Key;

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
        keys[0] = Q_Key;
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

        keys[0] = Q_Key;
        keys[1] = S_Key;

        //Send the information to the server
        NetworkGate networkGate = new NetworkGate(SEND_SOUND_URL);
        return networkGate.post(keys, values);
    }

    private boolean senderGPS(String...strings) {
        String[] keys = new String[3];
        String[] values = new String[3];

        keys[0] = "to";
        keys[1] = Lat_Key;
        keys[2] = Long_Key;

        values[0] = Constants.token;
        values[1] = strings[1];
        values[2] = strings[2];

        NetworkGate networkGate = new NetworkGate(SENDER_GPS_URL);
        return networkGate.post(keys, values);
    }

    private boolean queueGPS(String...strings) {
        String[] keys = new String[3];
        String[] values = new String[3];

        keys[0] = Q_Key;
        keys[1] = Lat_Key;
        keys[2] = Long_Key;

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
            OutputStream out = urlConnection.getOutputStream();
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
