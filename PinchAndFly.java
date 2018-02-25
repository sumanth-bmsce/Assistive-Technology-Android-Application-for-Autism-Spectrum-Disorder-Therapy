package com.samarth261.asd;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import static com.samarth261.asd.MyUtilities.PINCH;

public class PinchAndFly extends AppCompatActivity implements TouchMask.Layer {

    String path = MyUtilities.ASD_FOLDER_PATH+"ASD/Category";
    String list[];
    int ans;
    String drawableResources[];
    int indices[][];
    ImageView img1, img2, img3;
    ImageView imgq;
    TextToSpeech tts;
    RelativeLayout.LayoutParams img1_params, img2_params, img3_params, imgq_params;
    RelativeLayout rl;
    CrossMarkDisplayer wrongImage1, wrongImage2;//this displays the cross mark on the screen
    Vibrator vibrator;
    long vibPattern[] = {0, 100, 50, 100, 50, 100, 50, 100, 50, 100, 50, 100, 50, 100};
    MediaPlayer applause;
    long scoreStartTime;
    long scoreEndTime;
    int currentScore;

    int numberOfQuestionsAsked;
    int numberOfQuestionsAnsweredRightAtFirstGo;
    boolean firstPinch;

    long tftfStart, tftfEnd, tfpStart, tfpEnd, gtStart, gtEnd;
    String userName;
    Dimes dimes;

    TouchMask touchMask = null;

    String gameMode = PINCH;

    public class Dimes {
        private int layoutWidth;
        private int layoutHeight;
        private float side;
        private float dist;//distance between the imgviews and also the distance from the margins top dow left
        private float img1LM, img1TM, img2LM, img2TM, img3LM, img3TM, imgqLM, imgqTM;
        float s, a;//where is s represents sides and a the dist
        private float activeAreaFraction = .50f;

        public Dimes(int w, int h) {
            double factor = h * 540.0 / (w * 838.0);//f is the factor to convert any screen size to 16:9 , i don't know why i am doing this ;-;, i could have done with different buckets
            int leftCorrectionFactor = (int) ((w - w * factor) / 2.0);//leftCorrectionFactor is the extra length to be added(deleted) from left margin, ;-;  could have used different buckets
            layoutWidth = w;
            layoutWidth = (int) (layoutWidth * factor);
            layoutHeight = h;
            s = 20;
            a = 3;
            side = (layoutWidth / (2 + (3 * a / s)));
            dist = (layoutWidth - 2 * side) / 3;
            imgqLM = layoutWidth / 2 - side / 2 + leftCorrectionFactor;
            imgqTM = dist;
            img1LM = dist + leftCorrectionFactor;
            img1TM = layoutHeight - 2 * side - 2 * dist;
            img2LM = side + 2 * dist + leftCorrectionFactor;
            img2TM = img1TM;
            img3LM = imgqLM ;
            img3TM = layoutHeight - side - dist;
        }

        public int getLayoutWidth() {
            return layoutWidth;
        }

        public int getLayoutHeight() {
            return layoutHeight;
        }

        public int get1LM() {
            return (int) img1LM;
        }

        public int get1TM() {
            return (int) img1TM;
        }

        public int getDist() {
            return (int) dist;
        }

        public int get2LM() {
            return (int) img2LM;
        }

        public int get2TM() {
            return (int) img2TM;
        }

        public int get3LM() {
            return (int) img3LM;
        }

        public int get3TM() {
            return (int) img3TM;
        }

        public int getqLM() {
            return (int) imgqLM;
        }

        public int getqTM() {
            return (int) imgqTM;
        }

        public int getSide() {
            return (int) side;
        }

        public int whichImageIsClicked(Point p) {
            int actDistFromImg = (int) ((side - side * activeAreaFraction) / 2);// this is the distance between the image border and the active area
            //check if the pinch was on image1
            // img1LM<p.x<img1LM+side
            if (img1LM + actDistFromImg < p.x && p.x < img1LM + side - actDistFromImg) {
                if (img1TM + actDistFromImg < p.y && p.y < img1TM + side - actDistFromImg) {
                    return 1;
                }
            } else if (img2LM + actDistFromImg < p.x && p.x < img2LM + side - actDistFromImg) {
                if (img2TM + actDistFromImg < p.y && p.y < img2TM + side - actDistFromImg) {
                    return 2;
                }
            } else if (img3LM + actDistFromImg < p.x && p.x < img3LM + side - actDistFromImg) {
                if (img3TM + actDistFromImg < p.y && p.y < img3TM + side - actDistFromImg) {
                    return 3;
                }
            }
            return 0;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinch_and_fly);
        rl = (RelativeLayout) findViewById(R.id.PAF_RelativeLayout);
        numberOfQuestionsAnsweredRightAtFirstGo = 0;
        numberOfQuestionsAsked = 0;
        applause = MediaPlayer.create(this, R.raw.applause_3s);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mres();
            }

        }, 250);

        touchMask = (TouchMask) findViewById(R.id.touchMask);
        if (gameMode.equals(PINCH)) {
            touchMask.setLayerForMask(this);
            touchMask.bringToFront();
            touchMask.setPinchDistance(100);
        }
        userName = getSharedPreferences(Config.SHARED_PREF_NAME, MODE_PRIVATE).getString(Config.USERNAME_SHARED_PREF, "");
    }

    public void mres() {
        dimes = new Dimes(rl.getWidth(), rl.getHeight());
        String category = getIntent().getStringExtra("Category");
        path += "/" + category + "/Items";
        //now the path variable will be pointing inside the Items folder of that particular Category
        //Toast.makeText(getApplicationContext(), path, Toast.LENGTH_LONG).show();
        File f = new File(path);
        list = f.list();
        wrongImage1 = new CrossMarkDisplayer(this);
        wrongImage2 = new CrossMarkDisplayer(this);
        //*****************************************************************************************
        // vibrator
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        //*****************************
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                //tts.speak(status+"",tts.QUEUE_ADD,null,null);
            }
        });

        imgq = (ImageView) findViewById(R.id.PAF_imgq);
        img1 = (ImageView) findViewById(R.id.PAF_img1);
        img1.setTag(R.string.IMAGE_VIEW_TAG_KEY, 0);
        img2 = (ImageView) findViewById(R.id.PAF_img2);
        img2.setTag(R.string.IMAGE_VIEW_TAG_KEY, 1);
        img3 = (ImageView) findViewById(R.id.PAF_img3);
        img3.setTag(R.string.IMAGE_VIEW_TAG_KEY, 2);

        //setting params ***************************************************************************
        img1_params = new RelativeLayout.LayoutParams(dimes.getSide(), dimes.getSide());
        img2_params = new RelativeLayout.LayoutParams(dimes.getSide(), dimes.getSide());
        img3_params = new RelativeLayout.LayoutParams(dimes.getSide(), dimes.getSide());
        imgq_params = new RelativeLayout.LayoutParams(dimes.getSide(), dimes.getSide());
        img1_params.setMargins(dimes.get1LM(), dimes.get1TM(), 0, 0);
        img2_params.setMargins(dimes.get2LM(), dimes.get2TM(), 0, 0);
        img3_params.setMargins(dimes.get3LM(), dimes.get3TM(), 0, 0);
        imgq_params.setMargins(dimes.getqLM(), dimes.getqTM(), 0, 0);
        //******************************************************************************************

        imgq.setLayoutParams(imgq_params);
        img1.setLayoutParams(img1_params);
        img1.setLayoutParams(img2_params);
        img1.setLayoutParams(img3_params);

        /*img1.setPinchDistance(dimes.getSide() / 3);
        img1.setPinchDistance(dimes.getSide() / 3);
        img2.setPinchDistance(dimes.getSide() / 3);*/
        if (gameMode.equals(PINCH))
            touchMask.startResponding();
        currentScore = 0;
        paint();
    }

    /**
     * img1.setOnTouchListener(new View.OnTouchListener() {
     *
     * @Override public boolean onTouch(View v, MotionEvent event) {
     * boolean b = ((Pinchable) v).isPinched(event);
     * //Toast.makeText(getApplicationContext(),""+((Pinchable)v).dis+"\n"+((Pinchable)v).d,Toast.LENGTH_SHORT).show();
     * if (b) {
     * //Toast.makeText(getApplicationContext(),"pinching",Toast.LENGTH_SHORT).show();
     * clicked((Pinchable) v);
     * return false;
     * }
     * return true;
     * }
     * });
     * <p>
     * img1.setOnTouchListener(new View.OnTouchListener() {
     * @Override public boolean onTouch(View v, MotionEvent event) {
     * boolean b = ((Pinchable) v).isPinched(event);
     * if (b) {
     * clicked((Pinchable) v);
     * return false;
     * }
     * return true;
     * }
     * });
     * <p>
     * img2.setOnTouchListener(new View.OnTouchListener() {
     * @Override public boolean onTouch(View v, MotionEvent event) {
     * boolean b = ((Pinchable) v).isPinched(event);
     * if (b) {
     * clicked((Pinchable) v);
     * return false;
     * }
     * return true;
     * }
     * });
     */

    void paint() {
        firstPinch = true;
        numberOfQuestionsAsked++;
        indices = new int[3][2];
        set3random(indices);

        img1.setLayoutParams(img1_params);
        img2.setLayoutParams(img2_params);
        img3.setLayoutParams(img3_params);

        img1.setBackground(Drawable.createFromPath(path + "/" + list[indices[0][0]] + "/img" + indices[0][1] + ".png"));
        img2.setBackground(Drawable.createFromPath(path + "/" + list[indices[1][0]] + "/img" + indices[1][1] + ".png"));
        img3.setBackground(Drawable.createFromPath(path + "/" + list[indices[2][0]] + "/img" + indices[2][1] + ".png"));
        ans = (int) (Math.random() * 3);
        imgq.setBackground(Drawable.createFromPath(path + "/" + list[indices[ans][0]] + "/img" + indices[ans][1] + "_outline.png"));
        scoreStartTime = Calendar.getInstance().getTimeInMillis();
        touchMask.startResponding();
        gtStart = Calendar.getInstance().getTimeInMillis();

        img1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ClipData.Item item = new ClipData.Item(v.getTag() + "");
                ClipData data = new ClipData(new ClipDescription("dragged image", new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}), item);
                v.startDrag(data, new View.DragShadowBuilder(v), null, 0);
                v.setAlpha(0);
                return false;
            }
        });
    }

    /*void clicked(Pinchable v) {
        if (ans == v.getindex())
            complement(v);
        else
            tellItsWrong(v);
    }*/

    void clicked(int v, String url) {
        //v ranges from 0 to 2
        /*
            0 for img1
            1 for img1
            2 for img2
        */
        if (ans == v) {
            gtEnd = Calendar.getInstance().getTimeInMillis();
            url += "&gt=" + (gtEnd - gtStart);
            MyUtilities.runHttpGETRequest(this, url);
            switch (v) {
                case 0://img 1 is pinched
                    complement(img1);
                    break;
                case 1://img 2 is pinched
                    complement(img2);
                    break;
                case 2://img 2 is pinched
                    complement(img3);
                    break;
            }
        } else {
            url += "&gt=null";
            MyUtilities.runHttpGETRequest(this, url);
            switch (v) {
                case 0://img 1 is pinched
                    tellItsWrong(img1);
                    break;
                case 1://img 2 is pinched
                    tellItsWrong(img2);
                    break;
                case 2://img 2 is pinched
                    tellItsWrong(img3);
                    break;
            }

        }
    }

    void set3random(int a[][]) {

        int n = list.length;//n is the number of subcategories
        //the first column of 'a' represents the subcategory number the second index represents the image number inside the sub category
        a[0][0] = (int) (Math.random() * n);
        do {
            a[1][0] = (int) (Math.random() * n);
        } while (a[0][0] == a[1][0]);
        do {
            a[2][0] = (int) (Math.random() * n);
        } while (a[2][0] == a[0][0] || a[2][0] == a[1][0]);

        //n will be reused from here onwards
        n = MyUtilities.numberOfFilesMatching(path + "/" + list[a[0][0]], "img[0-9]+_outline\\.png");
        a[0][1] = (int) (Math.random() * n) + 1;
        n = MyUtilities.numberOfFilesMatching(path + "/" + list[a[1][0]], "img[0-9]+_outline\\.png");
        a[1][1] = (int) (Math.random() * n) + 1;
        n = MyUtilities.numberOfFilesMatching(path + "/" + list[a[2][0]], "img[0-9]+_outline\\.png");
        a[2][1] = (int) (Math.random() * n) + 1;
    }

    void complement(final ImageView v) {
        applause.start();
        touchMask.stopResponding();
        if (firstPinch)
            numberOfQuestionsAnsweredRightAtFirstGo++;
        vibrator.vibrate(vibPattern, -1);
        //removing the cross marks if any
        try {
            wrongImage1.removeCross();
        } catch (Exception e) {

        }
        try {
            wrongImage2.removeCross();
        } catch (Exception e) {

        }
        //******************************
        if (Build.VERSION.SDK_INT >= 21)
            tts.speak("correct", TextToSpeech.QUEUE_FLUSH, null, null);
        else
            tts.speak("correct", TextToSpeech.QUEUE_FLUSH, null);
        /*img1.setPinchable(false);
        img1.setPinchable(false);
        img2.setPinchable(false);*/

        //Below is the flying part of that particular view v

        final int initialMarginTop = ((RelativeLayout.LayoutParams) (v.getLayoutParams())).topMargin;
        final ValueAnimator obj = new ValueAnimator();
        obj.setStartDelay(100);
        obj.setIntValues(((RelativeLayout.LayoutParams) (v.getLayoutParams())).leftMargin, imgq_params.leftMargin);
        obj.setDuration(2000);
        obj.setInterpolator(new AccelerateDecelerateInterpolator());
        obj.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(imgq_params);
                int left, top;
                left = Integer.parseInt(obj.getAnimatedValue().toString());
                top = (initialMarginTop) - (int) ((initialMarginTop - dimes.getDist()) * Math.pow((obj.getAnimatedFraction()), 2));
                params.setMargins(left, top, 0, 0);
                v.setLayoutParams(params);
            }
        });
        obj.start();

        //This is for scoring....
        scoreEndTime = Calendar.getInstance().getTimeInMillis();
        long timeDiffInS = (scoreEndTime - scoreStartTime) / 1000;
        if (timeDiffInS < 10) {
            currentScore += 100;
        } else if (timeDiffInS < 20) {
            currentScore += 100 - (9 * (timeDiffInS - 10));
        } else {
            currentScore += 10;
        }
        //end
        //***********************************************************************************************************************
        //calling paint again is what happens below
        if (numberOfQuestionsAsked < 5) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    paint();
                }
            }, 4100 + 500);
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showStarAndExit();
                }
            }, 4100 + 500);
        }
    }


    void tellItsWrong(ImageView v) {
        firstPinch = false;
        //Vibrate the phone
        vibrator.vibrate(500);
        shake(v);
        if (Build.VERSION.SDK_INT >= 21)
            tts.speak("wrong", TextToSpeech.QUEUE_FLUSH, null, null);
        else
            tts.speak("wrong", TextToSpeech.QUEUE_FLUSH, null);
        if (wrongImage1.abovePinchableIndex == (int) v.getTag(R.string.IMAGE_VIEW_TAG_KEY) || wrongImage2.abovePinchableIndex == (int) v.getTag(R.string.IMAGE_VIEW_TAG_KEY)) {
            if (wrongImage1.abovePinchableIndex == (int) v.getTag(R.string.IMAGE_VIEW_TAG_KEY)) {
                wrongImage1.displayCross(v);
            } else {
                wrongImage2.displayCross(v);
            }
        } else if (wrongImage1.isAlive == 0) {
            wrongImage1.displayCross(v);
        } else if (wrongImage2.isAlive == 0) {
            wrongImage2.displayCross(v);
        }
    }

    //**************************************************************************************************************************
    public class CrossMarkDisplayer extends ImageView {
        ObjectAnimator obj;
        int isAlive;//this variable is 1 if the cross is still alive else its zero
        int abovePinchableIndex;

        CrossMarkDisplayer(Context context) {
            super(context);
            this.setBackground(Drawable.createFromPath(MyUtilities.ASD_FOLDER_PATH+"ASD/Others/ActivityWise/PinchAndFlyActivity/img_wrong.png"));
            this.setAlpha(0f);
            isAlive = 0;
            abovePinchableIndex = -1;
        }

        public void displayCross(ImageView v) {
            rl.removeView(this);
            try {
                obj.cancel();
            } catch (Exception e) {

            }
            obj = null;
            abovePinchableIndex = (int) v.getTag(R.string.IMAGE_VIEW_TAG_KEY);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) (v.getLayoutParams());
            rl.addView(this, params);
            isAlive = 1;//because the cross is being displayed
            this.setAlpha(1f);
            fadeAway();
        }

        public void removeCross() {
            obj.cancel();
            obj = null;
            rl.removeView(this);
            isAlive = 0;
            abovePinchableIndex = -1;
        }

        public void fadeAway() {
            obj = ObjectAnimator.ofFloat(this, "Alpha", 1f, 0f);
            obj.setDuration(3000);
            obj.setInterpolator(new SustainAndDim(.5f));
            obj.start();
            final CrossMarkDisplayer temp = this;
            obj.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    rl.removeView(temp);
                    isAlive = 0;
                    abovePinchableIndex = -1;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    rl.removeView(temp);
                    isAlive = 0;
                    abovePinchableIndex = -1;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }

    }

    public void shake(ImageView v) {
        ObjectAnimator obj = ObjectAnimator.ofFloat(v, "X", v.getX(), v.getX() + 5);
        obj.setDuration(500);
        obj.setInterpolator(new CustomVibrateInterpolator(9 * (float) Math.PI));
        obj.start();
    }

    public void onPinch(Point p, long tfs, long tfe, long tps, long tpe) {
        tftfStart = tfs;
        tftfEnd = tfe;
        tfpStart = tps;
        tfpEnd = tpe;
        String url = MyUtilities.WEBSITE_NAME + "log_activity/log_outline.php?user_name=" + userName +
                "&tftf=" + (tftfEnd - tftfStart) +
                "&tfp=" + (tfpEnd - tfpStart);
        int imgIndex = 0;
        if ((imgIndex = dimes.whichImageIsClicked(p)) != 0) {
            clicked(imgIndex - 1, url);
            /*
             because
             imageIndex ranges from 1 to 3
             but we want from  0 to 2;
             */
        }
        Log.d("for the pinchmask", imgIndex + "");
    }

    public void showStarAndExit() {
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString(Config.USERNAME_SHARED_PREF, "");
        try {
            //MyUtilities.sendDataToServer(this, userName, "outline", currentScore + numberOfQuestionsAnsweredRightAtFirstGo * 50);
        } catch (Exception e) {
            Log.d("my_error_0.2", e.getMessage());
        }
        MyUtilities.showStarsAndExit(this, rl, numberOfQuestionsAnsweredRightAtFirstGo);
        if (numberOfQuestionsAnsweredRightAtFirstGo > 3)
            MediaPlayer.create(this, R.raw.cheers_4s).start();
        else
            MediaPlayer.create(this, R.raw.applause_3s).start();
        /*Runnable runnable = new Runnable() {
            @Override
            public void run() {
                PinchAndFly.this.finish();
            }
        };
        runnable.run();*/
    }
}
