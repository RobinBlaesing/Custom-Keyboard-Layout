package com.example.myfirstkeyboard;

import android.inputmethodservice.Keyboard;
import java.util.*;
import java.util.List;

public class KeyMotion {

    public void newWord(List<Keyboard.Key> word, Keyboard keyboard)  {

        boolean[][] forbiddenArea = setForbiddenArea(keyboard);
        Keyboard.Key[][] keyArea = setKeyInArea(keyboard);

        // 'a' to 'z' are codes[i] 97 to 122
        // are set to 0 to 25
        boolean[] lettersInWord = new boolean[26];

        // All keys of keyboard:
        List<Keyboard.Key> keys = keyboard.getKeys();

        int[] codes = new int[word.size()];
        List<Keyboard.Key> wordWithoutMultiples = new ArrayList<>();

        // Analyze word:
        for (int k = 0; k < word.size(); k++) {

            Keyboard.Key key = word.get(k);
            codes[k] = key.codes[0];

            // Check for same letter in word:
            if (codes[k] >= 97 && codes[k] <= 122) {
                if (!lettersInWord[codes[k] - 97]) {
                    wordWithoutMultiples.add(key);
                }
                lettersInWord[codes[k] - 97] = true;
            }

        }

        // Center of mass

        int centerOfMassX = 0;
        int centerOfMassY = 0;

        for (int k = 0; k < wordWithoutMultiples.size(); k++) {
            Keyboard.Key key = wordWithoutMultiples.get(k);
            centerOfMassX += key.x;
            centerOfMassY += key.y;
        }

        if (wordWithoutMultiples.size() != 0) {
            centerOfMassX = centerOfMassX / wordWithoutMultiples.size();
            centerOfMassY = centerOfMassY / wordWithoutMultiples.size();
        }

        // Move keys:

        for (int k = 0; k < keys.size(); k++) {

            Keyboard.Key key = keys.get(k);

            move(key,lettersInWord,centerOfMassX,centerOfMassY,forbiddenArea,keyArea,keyboard);

            forbiddenArea = setForbiddenArea(keyboard);
            keyArea = setKeyInArea(keyboard);

        }

    }


    private boolean isInWord(boolean[] lettersInWord, int letterCode){
        return letterCode >= 97 && letterCode <= 122 && lettersInWord[letterCode-97];
    }


    private boolean[][] setForbiddenArea (Keyboard keyboard){

        boolean[][] forbiddenArea = new boolean[keyboard.getMinWidth()+1][keyboard.getHeight()];

        List<Keyboard.Key> keys = keyboard.getKeys();

        for (int k = 0; k < keys.size(); k++) {
            Keyboard.Key key = keys.get(k);
            for (int x = key.x; x < key.x + key.width; x++){
                for (int y = key.y; y < key.y + key.height; y++){
                    forbiddenArea[x][y] = true;
                }
            }
        }

        return forbiddenArea;

    }


    private Keyboard.Key[][] setKeyInArea (Keyboard keyboard){

        Keyboard.Key[][] keyArea = new Keyboard.Key[keyboard.getMinWidth()+1][keyboard.getHeight()];

        List<Keyboard.Key> keys = keyboard.getKeys();

        for (int k = 0; k < keys.size(); k++) {
            Keyboard.Key key = keys.get(k);
            for (int x = key.x; x < key.x + key.width; x++){
                for (int y = key.y; y < key.y + key.height; y++){
                    keyArea[x][y] = key;
                }
            }
        }

        return keyArea;

    }


    private void move(Keyboard.Key key, boolean[] lettersInWord, int moveTowardsX, int moveTowardsY, boolean[][] forbiddenArea, Keyboard.Key[][] keyArea, Keyboard keyboard) {

        if(isInWord(lettersInWord,key.codes[0])){

            int inWordMovementX = 5;
            int inWordMovementY = 1;

            // Note: Check each corner in motion direction!

            int xMotionDirection = (int) (Math.signum(moveTowardsX - key.x))*inWordMovementX;
            int newXPosition = key.x + xMotionDirection;
            if(xMotionDirection > 0){
                if (!forbiddenArea[newXPosition + key.width][key.y] && !forbiddenArea[newXPosition + key.width][key.y + key.height]){
                    key.x = newXPosition;
                }
            }
            else {
                if (!forbiddenArea[newXPosition][key.y] && !forbiddenArea[newXPosition][key.y + key.height]){
                    key.x = newXPosition;
                }
            }

            int yMotionDirection = (int) (Math.signum(moveTowardsY - key.y)) * inWordMovementY;
            int newYPosition = key.y + yMotionDirection;
            if (yMotionDirection > 0) {
                if (!forbiddenArea[key.x][newYPosition + key.height] && !forbiddenArea[key.x + key.width][newYPosition + key.height]){
                    key.y = newYPosition;
                }
            }
            else {
                if (!forbiddenArea[key.x][newYPosition] && !forbiddenArea[key.x + key.width][newYPosition]){
                    key.y = newYPosition;
                }
            }

        }
        else {

            // Key not in word

            if (key.codes[0] >= 97 && key.codes[0] <= 122) {

                int outWordMovementX = 2;
                int outWordMovementY = 6;

                // Random motion for all other keys:

                int xMotionDirection = (int) (Math.floor(Math.random() * 2) * 2 - 1) * outWordMovementX;
                int newXPosition = key.x + xMotionDirection;
                if (xMotionDirection > 0) {
                    if (newXPosition + key.width < keyboard.getMinWidth()) {
                        if (!forbiddenArea[newXPosition + key.width][key.y] && !forbiddenArea[newXPosition + key.width][key.y + key.height]) {
                            key.x = newXPosition;
                        }
                    }
                } else {
                    if (newXPosition > 0) {
                        if (!forbiddenArea[newXPosition][key.y] && !forbiddenArea[newXPosition][key.y + key.height]) {
                            key.x = newXPosition;
                        }
                    }
                }
                // Try to move into other direction:
                if (key.x != newXPosition) {
                    xMotionDirection = -xMotionDirection;
                    newXPosition = key.x + xMotionDirection;
                    if (xMotionDirection > 0) {
                        if (newXPosition + key.width < keyboard.getMinWidth()) {
                            if (!forbiddenArea[newXPosition + key.width][key.y] && !forbiddenArea[newXPosition + key.width][key.y + key.height]) {
                                key.x = newXPosition;
                            }
                        }
                    } else {
                        if (newXPosition > 0) {
                            if (!forbiddenArea[newXPosition][key.y] && !forbiddenArea[newXPosition][key.y + key.height]) {
                                key.x = newXPosition;
                            }
                        }
                    }
                }


                int yMotionDirection = (int) (Math.floor(Math.random() * 2) * 2 - 1) * outWordMovementY;
                int newYPosition = key.y + yMotionDirection;
                if (yMotionDirection > 0) {
                    if (newYPosition + key.height < keyboard.getHeight()) {
                        if (!forbiddenArea[key.x][newYPosition + key.height] && !forbiddenArea[key.x + key.width][newYPosition + key.height]) {
                            key.y = newYPosition;
                        }
                    }
                } else {
                    if (newYPosition > 0) {
                        if (!forbiddenArea[key.x][newYPosition] && !forbiddenArea[key.x + key.width][newYPosition]) {
                            key.y = newYPosition;
                        }
                    }
                }
                // Try to move into other direction:
                if (key.y != newYPosition) {
                    yMotionDirection = -yMotionDirection;
                    newYPosition = key.y + yMotionDirection;
                    if (yMotionDirection > 0) {
                        if (newYPosition + key.height < keyboard.getHeight()) {
                            if (!forbiddenArea[key.x][newYPosition + key.height] && !forbiddenArea[key.x + key.width][newYPosition + key.height]) {
                                key.y = newYPosition;
                            }
                        }
                    } else {
                        if (newYPosition > 0) {
                            if (!forbiddenArea[key.x][newYPosition] && !forbiddenArea[key.x + key.width][newYPosition]) {
                                key.y = newYPosition;
                            }
                        }
                    }
                }

            }
        }
    }

}