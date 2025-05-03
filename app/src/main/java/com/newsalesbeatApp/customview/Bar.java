package com.newsalesbeatApp.customview;


import androidx.annotation.NonNull;
import androidx.annotation.Size;

import static com.newsalesbeatApp.customview.Preconditions.checkNotNull;

public class Bar extends ChartEntry {

    private static final String TAG = "chart.model.Bar";

    private boolean mHasGradientColor;

    private int[] mGradientColors;

    private float[] mGradientPositions;


    public Bar(String label, float value) {

        super(label, value);

        isVisible = true;
        mHasGradientColor = false;
    }


    /*
     * -------------
     * Getters
     * -------------
     */

    /**
     * If bar has gradient fill color defined.
     *
     * @return true if gradient fill property defined.
     */
    public boolean hasGradientColor() {

        return mHasGradientColor;
    }


    /**
     * Retrieve set of colors defining the gradient of bar's fill.
     * Gradient fill property must have been previously defined.
     *
     * @return Gradient colors array.
     */
    public int[] getGradientColors() {

        return mGradientColors;
    }


    /**
     * Retrieve set of positions to define the gradient of bar's fill.
     * Gradient fill property must have been previously defined.
     *
     * @return Gradient positions.
     */
    public float[] getGradientPositions() {

        return mGradientPositions;
    }


    /*
     * -------------
     * Setters
     * -------------
     */

    /**
     * Set gradient colors to the fill of the {@link com.db.chart.model.Bar}.
     *
     * @param colors    The colors to be distributed among gradient
     * @param positions Position/order from which the colors will be place
     * @return {@link com.db.chart.model.Bar} self-reference.
     */
    public Bar setGradientColor(@NonNull @Size(min = 1) int colors[], float[] positions) {

        if (colors.length == 0)
            throw new IllegalArgumentException("Colors list cannot be empty");

        mHasGradientColor = true;
        mGradientColors = checkNotNull(colors);
        mGradientPositions = positions;
        return this;
    }

}