package com.samarth261.asd;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by Samarth on 04-09-2016.
 */

public class TouchMask extends View {
    public TouchMask(Context context) {
        super(context);
        pinchInitiatedFlag = false;
        donotRespond = false;
        pinchDistance = 50;
    }

    public TouchMask(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        pinchInitiatedFlag = false;
        donotRespond = false;
        pinchDistance = 50;
    }

    public TouchMask(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        pinchInitiatedFlag = false;
        donotRespond = false;
        pinchDistance = 50;
    }

    public interface Layer {
        void onPinch(Point mean, long a, long b, long c, long d);
    }


    Layer layer;// this is the layer on which the pinch will be sent

    double initialDistance;
    boolean pinchInitiatedFlag;
    private boolean donotRespond;
    Point mean;
    int pinchDistance = 0;
    long tfpStart, tfpEnd, tftfStart, tftfEnd;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            pinchInitiatedFlag = false;
            Log.d("pinch", "action up");
            //donotRespond = false;
            return false;
        }
        if (donotRespond) {
            //Toast.makeText(getContext(), "should't respond", Toast.LENGTH_LONG).show();
            return false;
        } else if (event.getPointerCount() == 2) {
            if (pinchInitiatedFlag) {
                double currentDistance = Math.sqrt(Math.pow(event.getX(0) - event.getX(1), 2.0) + Math.pow(event.getY(0) - event.getY(1), 2.0));
                Log.d("pinch", "initial distance=" + initialDistance + " current distance=" + currentDistance);
                if (currentDistance < initialDistance - pinchDistance) {
                    tfpEnd = Calendar.getInstance().getTimeInMillis();
                    mean.set((int) (event.getX(0) + event.getX(1)) / 2, (int) (event.getY(0) + event.getY(1)) / 2);
                    layer.onPinch(mean, tftfStart, tftfEnd, tfpStart, tfpEnd);
                    Log.d("pinch", "called fx");
                    pinchInitiatedFlag = false;
                    return false;
                }
            } else {
                tftfEnd = Calendar.getInstance().getTimeInMillis();
                tfpStart = tftfEnd;
                pinchInitiatedFlag = true;
                initialDistance = Math.sqrt(Math.pow(event.getX(0) - event.getX(1), 2.0) + Math.pow(event.getY(0) - event.getY(1), 2.0));
                mean = new Point((int) (event.getX(0) + event.getX(1)) / 2, (int) (event.getY(0) + event.getY(1)) / 2);
                Log.d("pinch", "initial distance " + initialDistance);
                return true;
            }
        } else if (event.getPointerCount() > 2) {
            pinchInitiatedFlag = false;
            return false;
        } else if (event.getPointerCount() == 1) {
            Log.d("pinch", event.getX() + "   " + event.getY());
            if (event.getAction() == event.ACTION_DOWN)
                tftfStart = Calendar.getInstance().getTimeInMillis();
        }
        return true;
    }

    public void setLayerForMask(Layer l) {
        layer = l;
    }

    public void setPinchDistance(int d) {
        pinchDistance = d;
    }

    public int getPinchDistance() {
        return pinchDistance;
    }

    public void stopResponding() {
        //Toast.makeText(getContext(), "not responding", Toast.LENGTH_LONG).show();
        donotRespond = true;
    }

    public void startResponding() {
        //Toast.makeText(getContext(), "is responding", Toast.LENGTH_LONG).show();
        donotRespond = false;
    }
}
