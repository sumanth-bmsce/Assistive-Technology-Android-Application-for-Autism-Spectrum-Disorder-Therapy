package com.samarth261.asd;

import android.view.animation.Interpolator;

/**
 * Created by Samarth on 11-07-2016.
 * this interpolator is designed such that the interpolation value is zero till the fraction reaches startFraction
 * and starts increasing when input is equal to startFraction .
 * The interpolation value increase to from 0 to 1 linearly
 */

public class SustainAndDim implements Interpolator {
    private float startFraction;

    @Override
    public float getInterpolation(float input) {
        double ans;
        if (input < startFraction)
            ans = 0f;
        else
            ans = (input-startFraction)/(1-startFraction);
        return (float) ans;
    }

    SustainAndDim(float a) {
        startFraction = a;
    }
}
