package com.noahbutler.soundsq.IO;

import android.util.Log;

import com.noahbutler.soundsq.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by gildaroth on 9/19/16.
 */
public class IO {


    /********/
    /* Keys */
    public static final String Q_Key = "queue_id";
    public static final String B_Key = "is_owner";
    public static final String N_Key = "no_file";


    public static void writeQueueID(File directory, String queueID, boolean isOwner) {

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put(Q_Key, queueID);
            jsonObject.put(B_Key, isOwner);
            write(directory, Constants.CACHE_FILE, jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject readQueueID(File directory) {
        return read(directory, Constants.CACHE_FILE);
    }

    public static boolean deleteQueueID(File directory) {
        return delete(directory, Constants.CACHE_FILE);
    }

    private static boolean delete(File directory, String fileName) {
        File file = new File(directory, fileName);
        return file.delete();
    }

    private static void write(File directory, String fileName, String data) {
        File file = new File(directory, fileName);
        BufferedWriter bufferedWriter;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
        }catch(IOException e) {
            Log.d("WRITING_ERROR", e.getMessage());
        }

    }

    private static JSONObject read(File directory, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        JSONObject response;
        File file = new File(directory, fileName);
        BufferedReader bufferedReader = null;

        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String raw;
            while((raw = bufferedReader.readLine()) != null) {
                stringBuilder.append(raw);
                Log.d("FILE", raw);
            }
            bufferedReader.close();
        }catch(IOException e){

            Log.d("FILE_READ_ERROR", e.getMessage());
            response = new JSONObject();

            try {
                response.put(N_Key, "");
                return response;
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        /* construct our JSON object */
        try {
            response = new JSONObject(stringBuilder.toString());
            return response;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
