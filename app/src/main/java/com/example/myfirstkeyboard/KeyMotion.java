package com.example.myfirstkeyboard;

import android.inputmethodservice.Keyboard;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.*;

public class KeyMotion {

    public void newWord(List<Keyboard.Key> word, Keyboard keyboard) {

        List<Keyboard.Key> keys = keyboard.getKeys();

        for (int k = 0; k < word.size(); k++) {
            Keyboard.Key key = word.get(k);
            System.out.println("key: " + key.label);
            int[] indexOfNearestKeys = keyboard.getNearestKeys(key.x, key.y);
            for (int n = 0; n < indexOfNearestKeys.length; n++) {
                System.out.println("nearest key index: " + indexOfNearestKeys[n]);
                for (int i = 0; i < keys.size(); i++) {
                    Keyboard.Key comparisonKey = keys.get(i);
                    System.out.println("check code of index " + i + " and looking for " + indexOfNearestKeys[n]);
                    if (i == indexOfNearestKeys[n]) {
                        if(comparisonKey.codes[0] == key.codes[0])
                            break;
                        System.out.println("Squared distance from " + key.label + " to " + comparisonKey.label + "  = " + key.squaredDistanceFrom(comparisonKey.x, comparisonKey.y));
                        break;
                    }
                }
            }
        }

        Keyboard.Key currentKey = word.get(0);

        currentKey.x = currentKey.x + 20;
        currentKey.y = currentKey.y + 20;


    }

}
