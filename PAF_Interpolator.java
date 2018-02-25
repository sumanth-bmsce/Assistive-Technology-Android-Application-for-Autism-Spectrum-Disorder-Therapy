package com.samarth261.asd;

import android.view.animation.Interpolator;

/**
 * Created by Samarth on 08-07-2016.
 */

public class PAF_Interpolator implements Interpolator {
    @Override
    public float getInterpolation(float input) {
        return input*input;
    }
}
