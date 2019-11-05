package com.example.myfirstkeyboard;

import android.inputmethodservice.Keyboard;
import java.util.*;
import java.util.List;

public class KeyMotion {

    private final static int IN_WORD_MOVEMENT_X = 10;
    private final static int IN_WORD_MOVEMENT_Y = 2;

    private final static int OUT_WORD_MOVEMENT_X = 10;
    private final static int OUT_WORD_MOVEMENT_Y = 10;


    private final static int REPEAT_MOTION_CYCLES = 6;


    private class AreaOfKeys {

        boolean[][] keyIsAtPosition;
        int[][] keyIdAtPosition; // Key number on keyboard

        private AreaOfKeys (Keyboard keyboard){
            keyIsAtPosition = new boolean[keyboard.getMinWidth()+1][keyboard.getHeight()];
            keyIdAtPosition = new int[keyboard.getMinWidth()+1][keyboard.getHeight()];
            setArea(keyboard);
        }

        private void setArea(Keyboard keyboard){

            List<Keyboard.Key> keys = keyboard.getKeys();

            for (int k = 0; k < keys.size(); k++) {
                Keyboard.Key key = keys.get(k);
                for (int x = key.x; x < key.x + key.width; x++){
                    for (int y = key.y; y < key.y + key.height; y++){
                        this.keyIsAtPosition[x][y] = true;
                        this.keyIdAtPosition[x][y] = k;
                    }
                }
            }

        }

        public boolean getKeyIsAtPositionArea (int x, int y) {
            return this.keyIsAtPosition[x][y];
        }

        public int getKeyIdAtPosition (int x, int y) {
            return this.keyIdAtPosition[x][y];
        }

        public boolean[][] keyIsAtPositionArea () {
            return this.keyIsAtPosition;
        }

        public int[][] keyIdAtPosition () {
            return this.keyIdAtPosition;
        }

    }


    public void newWord(List<Keyboard.Key> word, Keyboard keyboard)  {

        // If key is at a pixel: true, else: false
        AreaOfKeys area = new AreaOfKeys(keyboard);

        // Letters      'a' to  'z'
        // are codes[i] 97  to  122
        // and set to    0  to   25
        // in lettersInWord:
        boolean[] lettersInWord = new boolean[26];

        List<Keyboard.Key> wordWithoutMultiples = new ArrayList<>();

        // Get letters of word:
        for (int k = 0; k < word.size(); k++) {

            Keyboard.Key key = word.get(k);

            // Check for same letter in word:
            if (key.codes[0] >= 97 && key.codes[0] <= 122) {
                if (!lettersInWord[key.codes[0] - 97]) {
                    wordWithoutMultiples.add(key);
                }
                lettersInWord[key.codes[0] - 97] = true;
            }

        }

        // Move all keys of the keyboard:
        moveAllKeysOnKeyboard(keyboard, lettersInWord, calculateCenterOfMass(wordWithoutMultiples), area);

    }

    private int[] calculateCenterOfMass (List<Keyboard.Key> wordWithoutMultiples){

        int centerOfMassX = 0;
        int centerOfMassY = 0;

        for (int k = 0; k < wordWithoutMultiples.size(); k++) {
            Keyboard.Key key = wordWithoutMultiples.get(k);
            centerOfMassX += key.x;
            centerOfMassY += key.y;
        }

        int[] centerOfMass = new int[2];

        if (wordWithoutMultiples.size() != 0) {
            centerOfMass[0] = centerOfMassX / wordWithoutMultiples.size();
            centerOfMass[1] = centerOfMassY / wordWithoutMultiples.size();
        }

        return centerOfMass;
    }


    private boolean isInWord(boolean[] lettersInWord, int letterCode){
        return letterCode >= 97 && letterCode <= 122 && lettersInWord[letterCode-97];
    }


    /// Move all keys of the keyboard:
    private void moveAllKeysOnKeyboard (Keyboard keyboard, boolean[] lettersInWord,int[] centerOfMass, AreaOfKeys areaOfKeys) {

        List<Keyboard.Key> keys = keyboard.getKeys();

        for (int k = 0; k < keys.size(); k++) {

            Keyboard.Key key = keys.get(k);


            class Motion {

                private Keyboard.Key key;
                private boolean[][] forbiddenArea;

                public Motion(Keyboard.Key key, AreaOfKeys area){
                    this.key = key;
                    this.forbiddenArea = area.keyIsAtPosition;
                }

                private void inWord (int[] centerOfMass) {
                    for(int s = 0; s < IN_WORD_MOVEMENT_X; s++) {
                        int xStep = (int) (Math.signum(centerOfMass[0] - key.x));
                        int newXPosition = key.x + xStep;
                        this.xDirection(newXPosition);
                    }
                    for(int s = 0; s < IN_WORD_MOVEMENT_Y; s++){
                        int yStep = (int) (Math.signum(centerOfMass[1] - key.y));
                        int newYPosition = key.y + yStep;
                        this.yDirection(newYPosition);
                    }
                }

                private void outWord () {
                    // Move only alphabetic characters:
                    if (key.codes[0] >= 97 && key.codes[0] <= 122) {
                        for(int s = 0; s < OUT_WORD_MOVEMENT_X; s++) {
                            int xDirectionPixel = (int) (Math.floor(Math.random() * 2) * 2 - 1);
                            int newXPosition = key.x + xDirectionPixel;
                            this.xDirection(newXPosition);
                        }
                        for(int s = 0; s < OUT_WORD_MOVEMENT_Y; s++){
                            int yDirectionPixel = (int) (Math.floor(Math.random() * 2) * 2 - 1);
                            int newYPosition = key.y + yDirectionPixel;
                            this.yDirection(newYPosition);
                        }
                    }
                }

                private void xDirection (int newXPosition){
                    if(key.x < newXPosition){
                        if (!forbiddenArea[newXPosition + key.width][key.y] && !forbiddenArea[newXPosition + key.width][key.y + key.height]){
                            key.x = newXPosition;
                        }
                    }
                    else {
                        if (!forbiddenArea[newXPosition][key.y] && !forbiddenArea[newXPosition][key.y + key.height]){
                            key.x = newXPosition;
                        }
                    }
                }

                private void yDirection (int newYPosition){
                    if (key.y < newYPosition) {
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
            }


            Motion keyMotion = new Motion(key,areaOfKeys);

            for (int c = 0; c < REPEAT_MOTION_CYCLES; c++) {

                try {
                    if (isInWord(lettersInWord, key.codes[0])) {
                        keyMotion.inWord(centerOfMass);
                    } else {
                        keyMotion.outWord();
                    }
                } catch (java.lang.ArrayIndexOutOfBoundsException e){
                    System.out.println("WARNING! Key '" + key.label + "' at boundary.");
                }

                // Update area of keys after each motion:
                areaOfKeys.setArea(keyboard);

            }

        }

    }

}