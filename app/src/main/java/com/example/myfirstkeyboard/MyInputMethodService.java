package com.example.myfirstkeyboard;

import android.content.Context;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.*;


public class MyInputMethodService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    private KeyboardView keyboardView;
    private Keyboard keyboard;

    private boolean caps = false;

    private boolean pressFlag = false;

    private List<Keyboard.Key> currentWord = new ArrayList<>();

    @Override
    public View onCreateInputView() {
        keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard_view, null);
        keyboard = new Keyboard(this, R.xml.keys_layout);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(this);
        return keyboardView;
    }

    @Override
    public void onPress(int primaryCode) {

        Keyboard currentKeyboard = keyboard;
        List<Keyboard.Key> keys = currentKeyboard.getKeys();
        keyboardView.invalidateKey(primaryCode);

        if(primaryCode == -6){
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showInputMethodPicker();
        }

        for(int i = 0; i < keys.size() - 1; i++ )
        {
            Keyboard.Key currentKey = keys.get(i);

            //If your Key contains more than one code, then you will have to check if the codes array contains the primary code
            if(currentKey.codes[0] == primaryCode)
            {
                CharSequence cs = "SPACE";
                if (!currentKey.label.toString().equals(cs.toString())) {
                    currentWord.add(currentKey);
                    System.out.println(currentKey.label);
                }
                else {

                    if(!currentWord.isEmpty()){
                            KeyMotion keyMotionMethods = new KeyMotion();
                            for (int countRandom = 0; countRandom < 1; countRandom++){
                                keyMotionMethods.newWord(currentWord,keyboard);
                        }
                        currentWord.clear();
                        System.out.println("Word finished");
                        keyboardView.invalidateAllKeys();
                    }
                }

                break; // leave the loop once you find your match
            }
        }

    }

    @Override
    public void onRelease(int i) {

    }

    public boolean spaceLongPress() {
        System.out.println("HI");
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showInputMethodPicker();
        return true;
    }



    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection inputConnection = getCurrentInputConnection();
        if (inputConnection != null) {
            switch(primaryCode) {
                case Keyboard.KEYCODE_DELETE :
                    CharSequence selectedText = inputConnection.getSelectedText(0);

                    if (TextUtils.isEmpty(selectedText)) {
                        inputConnection.deleteSurroundingText(1, 0);
                    } else {
                        inputConnection.commitText("", 1);
                    }
                case Keyboard.KEYCODE_SHIFT:
                    caps = !caps;
                    keyboard.setShifted(caps);
                    keyboardView.invalidateAllKeys();
                    break;
                case Keyboard.KEYCODE_DONE:
                    inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));

                    break;
                default :
                    char code = (char) primaryCode;
                    if(Character.isLetter(code) && caps){
                        code = Character.toUpperCase(code);
                    }
                    inputConnection.commitText(String.valueOf(code), 1);

            }
        }

    }


    @Override
    public void onText(CharSequence charSequence) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }
}