package com.noahbutler.soundsq.Network;

import android.app.DownloadManager;
import android.os.AsyncTask;
import android.util.Log;

import com.noahbutler.soundsq.Constants;

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

/**
 * Created by NoahButler on 9/6/15.
 */
public class Sender extends AsyncTask<String, Integer, Boolean> {

    public static final String RUN_NEW_QID = "run_new_qid";
    public static final String SEND_SOUND = "send_sound";
    public static final String CHECK_QUEUE = "check_queue";
    public static final String REQUEST_QUEUE = "request_queue";
    //104.236.237.151
    private static final String SEND_NEW_QID_URL = "http://104.236.237.151/new/queue/";
    private static final String SEND_SOUND_URL   = "http://104.236.237.151/sound/send/";
    private static final String CHECK_QUEUE_URL = "http://104.236.237.151/queue/exists/";
    private static final String REQUEST_QUEUE_URL = "http://104.236.237.151/request/queue/";

    private String queue_id_key = "queue_id";
    private String singleSound_key = "sound_url";
    private String user_token_key = "user_token";


    @Override
    protected Boolean doInBackground(String...strings) {

        switch(strings[0]) {
            case RUN_NEW_QID:
                return sendNewQID(strings);
            case SEND_SOUND:
                return sendSound(strings);
            case CHECK_QUEUE:
                return checkQueue(strings);
            case REQUEST_QUEUE:
                return requestQueue(strings);
            default:
                Log.e("ERROR", "NOT A METHOD IN STRINGS[0]");
                return false;
        }
    }

    private boolean sendNewQID(String...strings) {
        String[] keys = new String[2];

        keys[0] = queue_id_key;
        keys[1] = user_token_key;

        String[] values = new String[2];
        values[0] = strings[1]; //queue id for the new queue
        values[1] = Constants.token; //the current phones android GCM token

        //Send the information to the server
        Log.d("SEND NEW QID", SEND_NEW_QID_URL);
        NetworkGate networkGate = new NetworkGate(SEND_NEW_QID_URL);
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

    /**
     * Method used to check if saved queue is still an active queue
     * @param strings
     * @return
     */
    private boolean checkQueue(String...strings) {
        String[] keys = new String[1];
        String[] values = new String[1];
        keys[0] = queue_id_key;
        values[0] = strings[1];

        NetworkGate networkGate = new NetworkGate(CHECK_QUEUE_URL);
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
    class NetworkGate {

        URL url;
        HttpURLConnection urlConnection;
        String sendingToURL;

        public NetworkGate(String sendingToURL) {
            this.sendingToURL = sendingToURL;
        }

        public boolean post(String[] keys, String[] values) {

            try {

                StringBuilder stringBuilder = new StringBuilder();

                url = new URL(sendingToURL);
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Connection", "Keep-Alive");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                for(int i = 0; i < keys.length; i++) {

                    stringBuilder.append(keys[i]);
                    stringBuilder.append("=");
                    stringBuilder.append(values[i]);

                    if(i == keys.length - 1) { //last ending is different
                    }else{
                        stringBuilder.append(";");
                    }
                }

                Log.d("DATA SENT", stringBuilder.toString());

                //create the output stream for the post data
                BufferedOutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                out.write(stringBuilder.toString().getBytes());
                out.flush();

                String responseMsg = urlConnection.getResponseMessage();
                int responseCode = urlConnection.getResponseCode();

                Log.d("RESPONSE CODE", "" + responseCode);
                Log.d("RESPONSE MESSAGE", responseMsg);

                /* check the response for error */
                if(responseCode == 500) {
                    return false;
                }else{
                    return true;
                }

            }catch(IOException e) {
                Log.e("ERROR","something fucked up in the post method of the NetworkGate class");
                Log.e("ERROR", e.toString());
                return false;
            }
        }
    }

}
