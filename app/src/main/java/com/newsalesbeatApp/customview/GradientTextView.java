package com.newsalesbeatApp.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Dhirendra Thakur on 23-12-2017.
 */

@SuppressLint("AppCompatCustomView")
public class GradientTextView extends TextView {
    public GradientTextView(Context context) {
        super(context, null, -1);
    }

    public GradientTextView(Context context,
                            AttributeSet attrs) {
        super(context, attrs, -1);
    }

    public GradientTextView(Context context,
                            AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean changed,
                            int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            getPaint().setShader(new LinearGradient(
                    0, 0, 0, getHeight(),
                    Color.WHITE, Color.BLACK,
                    Shader.TileMode.CLAMP));
        }
    }
}