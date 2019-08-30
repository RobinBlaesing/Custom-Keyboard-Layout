package com.example.myfirstkeyboard;

import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import java.util.*;
import java.util.List;

public class KeyMotion {

    public void newWord(List<Keyboard.Key> word, Keyboard keyboard, KeyboardView keyboardView)  {

        List<Keyboard.Key> keys = keyboard.getKeys();

        int[] codes = new int[word.size()];
        List<Keyboard.Key> wordWithoutMultiples = new ArrayList<>();

        int centerOfMassX = 0;
        int centerOfMassY = 0;
        for (int k = 0; k < word.size(); k++) {

            Keyboard.Key key = word.get(k);

            codes[k] = key.codes[0];

            // Check if key already used:

            for (int code: codes){
                wordWithoutMultiples.add(key);
                if (key.codes[0] == code){
                    wordWithoutMultiples.remove(wordWithoutMultiples.size()-1);
                }
            }

        }

        // Center of mass

        for (int k = 0; k < wordWithoutMultiples.size(); k++) {
            Keyboard.Key key = wordWithoutMultiples.get(k);
            centerOfMassX += key.x;
            centerOfMassY += key.y;
        }

        if (wordWithoutMultiples.size() != 0) {
            centerOfMassX = centerOfMassX / wordWithoutMultiples.size();
            centerOfMassY = centerOfMassY / wordWithoutMultiples.size();
        }

        for (int k = 0; k < wordWithoutMultiples.size(); k++) {
            Keyboard.Key key = wordWithoutMultiples.get(k);
            key.x += (int) (Math.signum(centerOfMassX - key.x))*20;
            key.y += (int) (Math.signum(centerOfMassY - key.y))*20;
        }

    }

}