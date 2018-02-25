package com.samarth261.asd;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by Samarth on 30-06-2016.
 */

public class Pinchable extends ImageView {

    double dis;
    double d;
    int flag;
    public boolean isReleased;
    int myindex;
    private boolean isPinchable;
    int pinchDistance;

    public Pinchable(Context context) {
        super(context);
        flag = 0;
        isPinchable = true;
        isReleased = true;
    }

    public Pinchable(Context context, AttributeSet attrs) {
        super(context, attrs);
        flag = 0;
        isPinchable = true;
        isReleased = true;
    }


    public boolean isPinched(MotionEvent event) {
        if (isPinchable == false)
            return false;
        if (isReleased) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                flag = 0;
                return false;
            } else if (event.getPointerCount() == 2 && flag == 0) {
                {
                    flag = 1;
                    dis = (Math.sqrt(Math.pow(event.getX(0) - event.getX(1), 2) + Math.pow(event.getY(0) - event.getY(1), 2)));
                }
            } else if (event.getPointerCount() == 2 && flag == 1) {
                d = (Math.sqrt(Math.pow(event.getX(0) - event.getX(1), 2) + Math.pow(event.getY(0) - event.getY(1), 2)));
                //Toast.makeText(getContext(), dis - d + "", Toast.LENGTH_LONG).show();
                if (dis - d > pinchDistance) {
                    flag = 0;
                    isReleased = true;
                    return true;
                }
                return false;
            } else {
                dis = 0;
                flag = 0;
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            isReleased = true;
            dis = 0;
            flag = 0;
        }
        return false;
    }

    public void setindex(int i) {
        myindex = i;
    }

    public int getindex() {
        return myindex;
    }

    public void setPinchable(boolean a) {
        isPinchable = a;
    }

    public void setPinchDistance(int a) {
        pinchDistance = a;
    }

    public void setIsReleased(boolean a) {
        isReleased = a;
    }
}
