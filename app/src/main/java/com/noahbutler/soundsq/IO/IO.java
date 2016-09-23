package com.noahbutler.soundsq.IO;

import android.util.Log;

import com.noahbutler.soundsq.Constants;

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

    public static void writeQueueID(File directory, String queueID) {
        write(directory, Constants.CACHE_FILE, queueID);
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

    public static String readQueueID(File directory) {
        return read(directory, Constants.CACHE_FILE);
    }

    private static String read(File directory, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();

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
            return "nofile";
        }

        /* look to see if anything in the file was read in */
        Log.e("R", stringBuilder.toString());
        if(stringBuilder.toString().contentEquals("")) {
            return null;
        }else {
            return stringBuilder.toString();
        }

    }

}
