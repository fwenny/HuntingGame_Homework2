package com.guy.class22b_and_1;

import android.app.Activity;
import android.os.Handler;

import java.util.Random;

// includes helper methods to help the app
public class HelperMethods {
    // generate random numbers
    public static final Random RANDOM = new Random();

    // timer
    public static final Handler TIMER = new Handler();

    // delay of timer
    public static final int DELAY = 1000;

    // board rows and cols
    public static final int ROWS = 8;
    public static final int COLS = 5;

    // how much time is reduced when meat is picked up
    public static final int MEAT_VALUE = -4;
}
