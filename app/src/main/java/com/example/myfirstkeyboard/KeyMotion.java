package com.example.myfirstkeyboard;

import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.*;

import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.*;

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

            for (int i = 0; i < codes.length; i++){
                wordWithoutMultiples.add(key);
                if (key.codes[0] == codes[i]){
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
