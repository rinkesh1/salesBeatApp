package com.newsalesbeatApp.customview;

/**
 * Created by Dhirendra Thakur on 09-12-2017.
 */

import android.graphics.Color;

/**
 * Created by sbaiget on 11/04/2014.
 */
public class Utils {

    public static int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }
}