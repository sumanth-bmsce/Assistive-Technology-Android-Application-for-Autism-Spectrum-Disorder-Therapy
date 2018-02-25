package com.samarth261.asd;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;

public class Learn extends AppCompatActivity {

    String categoryPath = MyUtilities.ASD_FOLDER_PATH+"ASD/Category";
    RelativeLayout rl;
    Dimes dimes;

    public class Dimes {
        private int layoutHeight;
        private int layoutWidth;
        private float side;
        private float dist;//distance between the imgviews and also the distance from the margins top dow left
        float s, a;//where is s represents sides and a the dist

        public Dimes(int w, int h) {
            layoutWidth = w;
            layoutHeight = h;
            s = 20;
            a = 3;
            side = (layoutWidth / (2 + (3 * a / s)));
            dist = (layoutWidth - 2 * side) / 3;
        }

        public int getLayoutWidth() {
            return layoutWidth;
        }

        public int getLayoutHeight() {
            return layoutHeight;
        }

        public int getDist() {
            return (int) dist;
        }

        public int getSide() {
            return (int) side;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);
        rl = ((RelativeLayout) findViewById(R.id.ActivityLearnRelativeLayout));
        categoryPath += "/" + getIntent().getStringExtra("Category") + "/Items";
        File categoryFolder = new File(categoryPath);
        final String list[] = categoryFolder.list();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                putThem(list);
            }
        }, 250);
    }

    void putThem(final String list[]) {
        dimes = new Dimes(rl.getWidth(), rl.getHeight());
        final ScrollView sv = (ScrollView) findViewById(R.id.ActivityLearnScrollView);
        sv.setBackgroundColor(0x00ffffff);
        final RelativeLayout svrl = (RelativeLayout) findViewById(R.id.ActivityLearnScrollViewRelativeLayout);
        svrl.setBackgroundColor(0x00ffffff);
        for (int i = 0; i < list.length; i++) {
            final int index = i;

            ImageButton img = new ImageButton(this);
            img.setBackground(Drawable.createFromPath(categoryPath + "/" + list[index] + "/Thumb.png"));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dimes.getSide(), dimes.getSide());
            int l = dimes.getDist() + ((index % 2 == 1) ? dimes.getDist() + dimes.getSide() : 0);
            int t = dimes.getDist() + (index / 2) * (dimes.getSide() + dimes.getDist());
            params.setMargins(l, dimes.getLayoutHeight(), 0, 0);
            customAnimationy(img, t, l, 250 * index);
            svrl.addView(img, params);
            img.setClickable(true);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("SubCategory", list[index]);
                    intent.putExtra("Category", getIntent().getStringExtra("Category"));
                    intent.setClass(getApplicationContext(), SubLearn.class);
                    startActivity(intent);
                    //Toast.makeText(getApplicationContext(),list[index],Toast.LENGTH_LONG).show();
                }
            });


        }
    }

    void customAnimationy(final ImageButton img, int y, final int l, int startDelay) {
        final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dimes.getSide(), dimes.getSide());
        final ValueAnimator obj = new ValueAnimator();
        obj.setStartDelay(startDelay);
        obj.setDuration(1500);
        obj.setIntValues(dimes.getLayoutHeight(), y);
        obj.setInterpolator(new CustomInterpolator());
        obj.start();
        obj.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int calculatedl;
                calculatedl = (int) (l * obj.getAnimatedFraction());
                params.setMargins(calculatedl, Integer.parseInt((obj.getAnimatedValue()).toString()), 0, 0);
                img.setLayoutParams(params);
            }
        });
    }


}
