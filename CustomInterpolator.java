package com.samarth261.asd;

import android.view.animation.Interpolator;

/**
 * Created by Samarth on 29-06-2016.
 */

public class CustomInterpolator implements Interpolator {
    @Override
    public float getInterpolation(float input) {
        double a,b;
        a=-1.3;
        b=-(a*a+1)/(2*a);
        double ans=(a*input+b)*(a*input+b)-(b)*(b);
        ans=-ans;
        return (float)ans;
    }
}
