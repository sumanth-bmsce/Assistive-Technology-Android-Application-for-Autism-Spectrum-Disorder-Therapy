package com.samarth261.asd;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;

public class Match extends AppCompatActivity implements TouchMask.Layer {


    String path = MyUtilities.ASD_FOLDER_PATH+"ASD/Category";
    String list[];//list of all the subCategories in the selected category
    int ans;//just tells which button is the right answer rages from 0 to 2
    String drawableResources[];
    int indices[][];//is a set of indices that store the subcategory number and the image to choose from among the many inside each subcategory
    ImageView img1, img2, img3;
    ImageView imgq, imgComp;
    TextToSpeech tts;
    RelativeLayout rl;//this is the relative layout
    CrossMarkDisplayer wrongImage1, wrongImage2;//this displays the cross mark on the screen
    Vibrator vibrator;
    long vibPattern[] = {0, 100, 50, 100, 50, 100, 50, 100, 50, 100, 50, 100, 50, 100};
    int numberOfQuestionsAsked;
    int numberOfQuestionAnsweredRightAtFirstGo;
    boolean firstPinch;
    MediaPlayer applause;
    long scoreStartTime;
    long scoreEndTime;
    int currentScore;
    Dimes dimes = null;
    TouchMask touchMask = null;
    long ortStart, tftfStart, tfpStart, ortEnd, tfpEnd, tftfEnd;

    TextView instructions;

    String userName = null;

    ValueAnimator backgroundAnimator = null;

    public class Dimes {
        private int layoutWidth;
        private int layoutHeight;
        private float side;
        private float border;//distance between the imgviews and also the distance from the margins top down left
        private float img1LM, img1TM, img2LM, img2TM, img3LM, img3TM, imgqLM, imgqTM;
        float s, a;//where is s represents sides and a the dist
        private float activeAreaFraction = 0.50f;
        //activeAreaFraction should always be in the range [0,1]

        public Dimes(int w, int h) {
            double factor = h * 540.0 / (w * 900.0);//f is the factor to convert any screen size to 16:9 , i don't know why i am doing this ;-;, i could have done with different buckets
            //f is the factor that has to be multiplied with the width of the screen so that the screen size becomes 16:9
            int leftCorrectionFactor = (int) ((w - w * factor) / 2.0);//leftCorrectionFactor is the extra length to be added(deleted) from left margin, ;-;  could have used different buckets
            //basically how much should be subtracted from the width of the screen so that the screeen becomes 16:9
            //NOTE: w*factor is the new width
            //NOTE: divide by 2 is to provide the space from both left and right edges
            layoutWidth = w;
            layoutHeight = h;
            layoutWidth = (int) (layoutWidth * factor);//changing the width to a virtual width ;-;, .... could have used different buckets
            s = 20;
            a = 3;
            // the ration s:a is what is happening
            side = (layoutWidth / (2 + (3 * a / s)));
            border = (layoutWidth - 2 * side) / 3;
            imgqLM = layoutWidth / 2 - side / 2 + leftCorrectionFactor;
            imgqTM = border;
            img1LM = border + leftCorrectionFactor;
            img1TM = layoutHeight - 2 * side - 2 * border;
            img2LM = side + 2 * border + leftCorrectionFactor;
            img2TM = img1TM;
            img3LM = imgqLM ;
            img3TM = layoutHeight - side - border;
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

        public int getBorder() {
            return (int) border;
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
        setContentView(R.layout.activity_match);
        rl = (RelativeLayout) findViewById(R.id.match_activity_rl);
        numberOfQuestionsAsked = 0;
        numberOfQuestionAnsweredRightAtFirstGo = 0;
        /*View v = findViewById(R.id.justAView);
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });*/
        applause = MediaPlayer.create(this, R.raw.applause_3s);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mres();
            }

        }, 250);
        touchMask = (TouchMask) findViewById(R.id.touchMask);
        touchMask.setLayerForMask(this);
        touchMask.bringToFront();
        touchMask.setPinchDistance(100);
        touchMask.startResponding();
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        userName = sharedPreferences.getString(Config.USERNAME_SHARED_PREF, "");
        instructions = new TextView(this);
        instructions.setText("Pinch the matching image");
        instructions.setTextSize(getResources().getDimension(R.dimen.matchActivityInstructionSize));
        instructions.setTextColor(0xff000000);
        setUp();
    }

    private void setUp() {
        backgroundAnimator = new ValueAnimator();
        final int maxAlphaForBackgroud = 50;
        backgroundAnimator.setIntValues(0, maxAlphaForBackgroud);
        backgroundAnimator.setDuration(2000);
        backgroundAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        backgroundAnimator.setRepeatCount(ValueAnimator.INFINITE);
        backgroundAnimator.setRepeatMode(ValueAnimator.REVERSE);
        backgroundAnimator.addPauseListener(new Animator.AnimatorPauseListener() {
            @Override
            public void onAnimationPause(Animator animation) {
                img1.setBackgroundColor(0x00ffffff);
                img2.setBackgroundColor(0x00ffffff);
                img3.setBackgroundColor(0x00ffffff);
            }

            @Override
            public void onAnimationResume(Animator animation) {

            }
        });
        backgroundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                img1.setBackgroundColor(Color.argb(
                        (Integer) ((ValueAnimator) animation).getAnimatedValue(), 255, 255, 0
                ));
                img2.setBackgroundColor(Color.argb(
                        ((Integer) ((ValueAnimator) animation).getAnimatedValue()), 255, 255, 0
                ));
                img3.setBackgroundColor(Color.argb(
                        ((Integer) ((ValueAnimator) animation).getAnimatedValue()), 255, 255, 0
                ));
            }
        });
    }

    public void mres() {
        String category = getIntent().getStringExtra("Category");
        path += "/" + category + "/Items";
        //now the path variable will be pointing inside the Items folder of that particular Category
        File f = new File(path);
        list = f.list();
        wrongImage1 = new CrossMarkDisplayer(this);
        wrongImage2 = new CrossMarkDisplayer(this);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        //***************************************************************************************
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // tts.speak(status+"",tts.QUEUE_ADD,null,null);
            }
        });
        img1 = (ImageView) findViewById(R.id.imageButton1);
        img1.setTag(R.string.IMAGE_VIEW_TAG_KEY, 0);
        img2 = (ImageView) findViewById(R.id.imageButton2);
        img2.setTag(R.string.IMAGE_VIEW_TAG_KEY, 1);
        img3 = (ImageView) findViewById(R.id.imageButton3);
        img3.setTag(R.string.IMAGE_VIEW_TAG_KEY, 2);
        imgq = (ImageView) findViewById(R.id.imageView);
        imgComp = (ImageView) findViewById(R.id.complementImageView);

        //Toast.makeText(getApplicationContext(),""+rl.getWidth()+"x"+rl.getHeight(),Toast.LENGTH_LONG).show();
        dimes = new Dimes(rl.getWidth(), rl.getHeight());
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(dimes.getSide(), dimes.getSide());
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(dimes.getSide(), dimes.getSide());
        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(dimes.getSide(), dimes.getSide());
        RelativeLayout.LayoutParams paramsq = new RelativeLayout.LayoutParams(dimes.getSide(), dimes.getSide());
        params1.setMargins(dimes.get1LM(), dimes.get1TM(), 0, 0);
        img1.setLayoutParams(params1);
        params2.setMargins(dimes.get2LM(), dimes.get2TM(), 0, 0);
        img2.setLayoutParams(params2);
        params3.setMargins(dimes.get3LM(), dimes.get3TM(), 0, 0);
        img3.setLayoutParams(params3);
        paramsq.setMargins(dimes.getqLM(), dimes.getqTM(), 0, 0);
        imgq.setLayoutParams(paramsq);
        //img1.setPinchDistance(dimes.getSide() / 3);
        //img1.setPinchDistance(dimes.getSide() / 3);
        //img2.setPinchDistance(dimes.getSide() / 3);
        rl.addView(instructions, -1, -2);
        instructions.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) instructions.getLayoutParams();
        layoutParams.setMargins(0, dimes.getqTM() + dimes.getSide() + 10, 0, 0);
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
        instructions.setVisibility(View.VISIBLE);
        firstPinch = true;
        numberOfQuestionsAsked++;
        indices = new int[3][2];
        set3random(indices);
        //img1.setPinchable(true);
        //img1.setPinchable(true);
        //img2.setPinchable(true);
        imgComp.setBackground(null);

        img1.setImageDrawable(Drawable.createFromPath(path + "/" + list[indices[0][0]] + "/img" + indices[0][1] + ".png"));
        //Toast.makeText(getApplicationContext(), path + "/" + list[indices[0][0]] + "/img" + indices[0][1] + ".png", Toast.LENGTH_LONG).show();
        img2.setImageDrawable(Drawable.createFromPath(path + "/" + list[indices[1][0]] + "/img" + indices[1][1] + ".png"));
        img3.setImageDrawable(Drawable.createFromPath(path + "/" + list[indices[2][0]] + "/img" + indices[2][1] + ".png"));
        ans = (int) (Math.random() * 3);
        //ans=0;
        imgq.setBackground(Drawable.createFromPath(path + "/" + list[indices[ans][0]] + "/img" + indices[ans][1] + ".png"));
        scoreStartTime = Calendar.getInstance().getTimeInMillis();
        touchMask.startResponding();
        ortStart = Calendar.getInstance().getTimeInMillis();
        backgroundAnimator.start();
    }

    /**
     * void clicked(Pinchable v) {
     * if (ans == v.getindex())
     * complement();
     * else
     * tellItsWrong(v);
     * }
     */

    void clicked(int v, String url) {

        //v ranges from 0 to 2
        /*
            0 for img1
            1 for img1
            2 for img2
        */
        backgroundAnimator.pause();
        if (ans == v) {
            ortEnd = Calendar.getInstance().getTimeInMillis();
            url += "&ort=" + (ortEnd - ortStart);
            Log.d("database100", url);
            try {
                MyUtilities.runHttpGETRequest(this, url);
            } catch (Exception e) {

            }
            complement();
        } else {
            url += "&ort=null";
            Log.d("database100", url);
            try {
                MyUtilities.runHttpGETRequest(this, url);
            } catch (Exception e) {

            }
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
        n = MyUtilities.numberOfFilesMatching(path + "/" + list[a[0][0]], "img[0-9]+\\.png");
        a[0][1] = (int) (Math.random() * n) + 1;
        n = MyUtilities.numberOfFilesMatching(path + "/" + list[a[1][0]], "img[0-9]+\\.png");
        a[1][1] = (int) (Math.random() * n) + 1;
        n = MyUtilities.numberOfFilesMatching(path + "/" + list[a[2][0]], "img[0-9]+\\.png");
        a[2][1] = (int) (Math.random() * n) + 1;
    }

    void complement() {
        instructions.setVisibility(View.INVISIBLE);
        //backgroundAnimator.pause();
        applause.start();
        touchMask.stopResponding();
        if (firstPinch)
            numberOfQuestionAnsweredRightAtFirstGo++;
        //removing the cross marks if any
        vibrator.vibrate(vibPattern, -1);
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
        //img1.setPinchable(false);
        //img1.setPinchable(false);
        //img2.setPinchable(false);

        img1.setImageDrawable(null);
        img2.setImageDrawable(null);
        img3.setImageDrawable(null);
        imgq.setBackground(null);
        imgComp.setBackgroundResource(R.drawable.compliment_animation);
        AnimationDrawable compleAnimation = (AnimationDrawable) imgComp.getBackground();
        compleAnimation.start();

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
        //calling paint again is what happens below
        if (numberOfQuestionsAsked < 5) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    paint();
                }
            }, 4000);
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showStarsAndExit();
                }
            }, 4000);
        }
    }

    void tellItsWrong(ImageView v) {
        firstPinch = false;
        vibrator.vibrate(500);
        shake(v);
        if (Build.VERSION.SDK_INT >= 21)
            tts.speak("wrong", TextToSpeech.QUEUE_FLUSH, null, null);
        else
            tts.speak("wrong", TextToSpeech.QUEUE_FLUSH, null);
        if (wrongImage1.aboveImageIndex == (int) v.getTag(R.string.IMAGE_VIEW_TAG_KEY) || wrongImage2.aboveImageIndex == (int) v.getTag(R.string.IMAGE_VIEW_TAG_KEY)) {
            if (wrongImage1.aboveImageIndex == (int) v.getTag(R.string.IMAGE_VIEW_TAG_KEY)) {
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

    public class CrossMarkDisplayer extends ImageView {
        ObjectAnimator obj;
        int isAlive;//this variable is 1 if the cross is still alive else its zero
        int aboveImageIndex;

        CrossMarkDisplayer(Context context) {
            super(context);
            this.setBackground(Drawable.createFromPath(MyUtilities.ASD_FOLDER_PATH+"ASD/Others/ActivityWise/MatchActivity/img_wrong.png"));
            this.setAlpha(0f);
            isAlive = 0;
            aboveImageIndex = -1;
        }

        public void displayCross(ImageView v) {
            rl.removeView(this);
            try {
                obj.cancel();
            } catch (Exception e) {

            }
            obj = null;
            aboveImageIndex = (int) v.getTag(R.string.IMAGE_VIEW_TAG_KEY);
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
            aboveImageIndex = -1;
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
                    aboveImageIndex = -1;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    //Toast.makeText(getApplicationContext(), "stopped", Toast.LENGTH_LONG).show();
                    rl.removeView(temp);
                    isAlive = 0;
                    aboveImageIndex = -1;
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

    /**
     * public void showStarsAndExit() {
     * double side, border, dist2;
     * int s, a;
     * s = 10;
     * a = 1;
     * side = (rl.getWidth() / (3.0 + 4 * (a * 1.0 / s)));
     * border = (rl.getWidth() - 3.0 * side) / (4);
     * dist2 = ((rl.getWidth() - 2 * side - border) / 2.0);
     * RelativeLayout showStarRelativeLayout = new RelativeLayout(this);
     * showStarRelativeLayout.setBackgroundColor(0x00ffffff);
     * showStarRelativeLayout.setGravity(Gravity.CENTER_VERTICAL);
     * ImageView starsOutline[] = new ImageView[5];
     * ImageView stars[] = new ImageView[numberOfQuestionAnsweredRightAtFirstGo];
     * for (int i = 0; i < 5; i++) {
     * starsOutline[i] = new ImageView(this);
     * starsOutline[i].setId(starsOutline[i].hashCode());
     * starsOutline[i].setImageResource(R.drawable.star_outline);
     * RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) side, (int) side);
     * switch (i) {
     * case 0:
     * params.leftMargin = (int) border;
     * break;
     * case 1:
     * params.leftMargin = (int) (border * 2 + side);
     * break;
     * case 2:
     * params.leftMargin = (int) (border * 3 + 2 * side);
     * break;
     * case 3:
     * params.leftMargin = (int) (dist2);
     * params.addRule(RelativeLayout.BELOW, starsOutline[0].getId());
     * params.topMargin = (int) border;
     * break;
     * case 4:
     * params.leftMargin = (int) (dist2 + side + border);
     * params.addRule(RelativeLayout.BELOW, starsOutline[0].getId());
     * params.topMargin = (int) border;
     * break;
     * }
     * showStarRelativeLayout.addView(starsOutline[i], params);
     * <p>
     * }
     * rl.removeAllViews();
     * Toast.makeText(this, side + "\n" + border, Toast.LENGTH_LONG).show();
     * rl.addView(showStarRelativeLayout, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
     * for (int i = 0; i < numberOfQuestionAnsweredRightAtFirstGo; i++) {
     * stars[i]=new ImageView(this);
     * stars[i].setImageResource(R.drawable.star);
     * RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(starsOutline[i].getLayoutParams());
     * params.leftMargin=((RelativeLayout.LayoutParams)(starsOutline[i].getLayoutParams())).leftMargin;
     * if(i>2){
     * params.addRule(RelativeLayout.BELOW, starsOutline[0].getId());
     * params.topMargin=(int)border;
     * }
     * stars[i].setAlpha(0f);
     * showStarRelativeLayout.addView(stars[i],params);
     * ObjectAnimator obj=ObjectAnimator.ofFloat(stars[i],"Alpha",0,1);
     * obj.setInterpolator(new AccelerateInterpolator());
     * obj.setDuration(500);
     * obj.setStartDelay(i*500+500);
     * obj.start();
     * }
     * }
     */

    public void onPinch(Point p, long tfs, long tfe, long tps, long tpe) {
        tftfStart = tfs;
        tftfEnd = tfe;
        tfpStart = tps;
        tfpEnd = tpe;
        String url = MyUtilities.WEBSITE_NAME + "log_activity/log_match.php?user_name=" + userName +
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

    public void showStarsAndExit() {
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString(Config.USERNAME_SHARED_PREF, "");
        try {
            //MyUtilities.sendDataToServer(this, userName, "match", currentScore + numberOfQuestionAnsweredRightAtFirstGo * 50);
        } catch (Exception e) {
            Log.d("my_error_0.1", e.getMessage());
        }
        MyUtilities.showStarsAndExit(this, rl, numberOfQuestionAnsweredRightAtFirstGo);
        if (numberOfQuestionAnsweredRightAtFirstGo > 3)
            MediaPlayer.create(this, R.raw.cheers_4s).start();
        else
            MediaPlayer.create(this, R.raw.applause_3s).start();
    }
}
