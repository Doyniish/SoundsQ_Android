package com.noahbutler.soundsq;

import java.util.Random;

/**
 * Created by NoahButler on 1/5/16.
 */
public class QueueIDGenerator {

    private static char[] letters = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','w','x','y','z','1','2','3','4','5','6','7','8','9','0'};

    public static String generate() {
        StringBuilder stringBuilder = new StringBuilder();
        Random r = new Random();
        for(int i = 0; i < 5; i++) {
            char c = letters[r.nextInt(letters.length)];
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

}
