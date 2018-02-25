package com.samarth261.asd;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by Geek on 27-03-2017.
 */

public class AcceptingLinearLayout extends LinearLayout {
    Character nextCharacter = null;
    int nextCharsIndex;
    String correctString = null;
    AcceptingLinearLayoutListener acceptingLinearLayoutListener;

    public AcceptingLinearLayout(Context context) {
        super(context);
    }

    public AcceptingLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AcceptingLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    void setCorrectString(String s) {
        correctString = s;
        nextCharacter = correctString.charAt(0);
        nextCharsIndex = 0;
    }

    interface AcceptingLinearLayoutListener {
        public void onRearrangeFinish();
    }

    public void setAcceptingListener(AcceptingLinearLayoutListener listener) {
        this.acceptingLinearLayoutListener = listener;
    }

    @Override
    public void addView(View v) {
        super.addView(v);
        nextCharsIndex++;
        if (nextCharsIndex < correctString.length()) {
            nextCharacter = correctString.charAt(nextCharsIndex);
        } else {
            for (int i = 0; i < this.getChildCount(); i++) {
                this.getChildAt(i).setOnTouchListener(null);
            }
            acceptingLinearLayoutListener.onRearrangeFinish();
            nextCharacter = null;
        }
    }
}
