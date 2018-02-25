package com.samarth261.asd;

import android.view.animation.Interpolator;

/**
 * Created by Samarth on 01-07-2016.
 */

public class CustomVibrateInterpolator implements Interpolator {

    double a;
    CustomVibrateInterpolator(float i)
    {
        a=i;
    }

    CustomVibrateInterpolator()
    {
        a=58.1;
    }
    @Override
    public float getInterpolation(float x) {
        double ans;
        ans=(0.5)*((Math.sin(a*x))+(Math.sin((a+Math.PI)*x)));
        return (float)ans;
    }
}
