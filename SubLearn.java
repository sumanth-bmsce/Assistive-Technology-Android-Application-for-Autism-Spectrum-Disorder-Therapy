package com.samarth261.asd;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.Calendar;

public class SubLearn extends AppCompatActivity {

    String path = MyUtilities.ASD_FOLDER_PATH+"ASD/Category";
    ImageView img;
    TextView t;
    TextToSpeech tts;
    ImageButton bn;
    RelativeLayout rl;
    String subCategory;
    long scoreStartTime;
    long scoreEndTime;

    int RIGHT = +1;
    int LEFT = -1;
    MyUtilities.MyRandomIntGenerator newRandom;
    String imagesPathList[];

    String userName;
    //Calendar start = null, end = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scoreStartTime = Calendar.getInstance().getTimeInMillis();

        setContentView(R.layout.activity_sub_learn);
        rl = (RelativeLayout) findViewById(R.id.SubLearnRelativeLayout);
        img = (ImageView) findViewById(R.id.SubLearnImageView);
        t = (TextView) findViewById(R.id.SubLearnTextView);
        bn = (ImageButton) findViewById(R.id.SubLearnButton);
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    //tts.setLanguage(Locale.UK);
                }
            }
        });
        tts.setSpeechRate(.8f);
        subCategory = getIntent().getStringExtra("SubCategory");
        String category = getIntent().getStringExtra("Category");
        path += "/" + category + "/Items/" + subCategory;
        File f = new File(path);
        String[] templist = f.list();
        t.setText(subCategory);

        imagesPathList = MyUtilities.listOfFilesMatchingAlongWithPath(path, "img[0-9]+\\.png");
        newRandom = new MyUtilities.MyRandomIntGenerator(0, imagesPathList.length - 1);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mres();
            }
        }, 250);
        //start = Calendar.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        userName = sharedPreferences.getString(Config.USERNAME_SHARED_PREF, "");
    }

    public void mres() {
        //RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) (rl.getWidth() * (4 / 5.0)), (int) (rl.getWidth() * (4 / 5.0)));
        img.getLayoutParams().height = img.getWidth();
        Log.d("mymessage", img.getLayoutParams().height + " ");
        img.setBackground(Drawable.createFromPath(imagesPathList[newRandom.nextInt()]));
        bn.setBackground(Drawable.createFromPath(MyUtilities.ASD_FOLDER_PATH+"ASD/Others/ActivityWise/SubLearnActivity/Next.png"));
        if (Build.VERSION.SDK_INT >= 21)
            tts.speak(subCategory, TextToSpeech.QUEUE_FLUSH, null, null);
        else
            tts.speak(subCategory, TextToSpeech.QUEUE_FLUSH, null);
        bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 21)
                    tts.speak(subCategory, TextToSpeech.QUEUE_FLUSH, null, null);
                else
                    tts.speak(subCategory, TextToSpeech.QUEUE_FLUSH, null);
                //animateAndSet(img, newRandom.nextInt());

            }
        });

        View.OnTouchListener swipeListener = new SwipeListener();
        img.setOnTouchListener(swipeListener);
    }

    public void animateAndSet(final ImageView v, final int i, final SwipeListener swipeListener, int direction) {
        /*if (Build.VERSION.SDK_INT >= 21)
            tts.speak(subCategory, TextToSpeech.QUEUE_FLUSH, null, null);
        else
            tts.speak(subCategory, TextToSpeech.QUEUE_FLUSH, null);*/

        ObjectAnimator obj1 = ObjectAnimator.ofFloat(v, "X", direction * (v.getX()), -(v.getWidth()) * direction);
        final ObjectAnimator obj2 = ObjectAnimator.ofFloat(v, "X", (rl.getWidth()) * direction, v.getX() * direction);
        obj1.setDuration(650);
        obj1.setInterpolator(new AnticipateInterpolator(3));
        obj1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                v.setBackground(Drawable.createFromPath(imagesPathList[i]));
                obj2.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        obj2.setDuration(650);
        obj2.setStartDelay(200);
        obj2.setInterpolator(new OvershootInterpolator(3));
        obj2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                bn.setClickable(true);
                swipeListener.active = true;
                if (Build.VERSION.SDK_INT >= 21)
                    tts.speak(subCategory, TextToSpeech.QUEUE_FLUSH, null, null);
                else
                    tts.speak(subCategory, TextToSpeech.QUEUE_FLUSH, null);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        obj1.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        scoreEndTime = Calendar.getInstance().getTimeInMillis();
        try {
            //MyUtilities.sendDataToServer(this, userName, "learn", (int) (scoreEndTime - scoreStartTime) / 1000);
            String url = MyUtilities.WEBSITE_NAME + "/log_activity/log_learn.php?user_name=" + userName + "&at=" + (scoreEndTime - scoreStartTime) + "&tfs=null";
            MyUtilities.runHttpGETRequest(this, url);
        } catch (Exception e) {
            Log.d("my_error_0.1", e.getMessage());
        }
    }

    class SwipeListener implements View.OnTouchListener {
        private VelocityTracker mVelocityTracker = null;
        boolean active = true;
        long start = 0, end = 0;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int index = event.getActionIndex();
            int action = event.getActionMasked();
            int pointerId = event.getPointerId(index);
            if (!active) return false;
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    start = Calendar.getInstance().getTimeInMillis();
                    if (mVelocityTracker == null) {
                        // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                        mVelocityTracker = VelocityTracker.obtain();
                    } else {
                        // Reset the velocity tracker back to its initial state.
                        mVelocityTracker.clear();
                    }
                    // Add a user's movement to the tracker.
                    mVelocityTracker.addMovement(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    mVelocityTracker.addMovement(event);
                    // When you want to determine the velocity, call
                    // computeCurrentVelocity(). Then call getXVelocity()
                    // and getYVelocity() to retrieve the velocity for each pointer ID.
                    mVelocityTracker.computeCurrentVelocity(1000);//pixel per second
                    // Log velocity of pixels per second
                    // Best practice to use VelocityTrackerCompat where possible.
                    Log.d("my_swipe", "X velocity: " +
                            VelocityTrackerCompat.getXVelocity(mVelocityTracker,
                                    pointerId));
                    Log.d("my_swipe", "Y velocity: " +
                            VelocityTrackerCompat.getYVelocity(mVelocityTracker,
                                    pointerId));
                    if (VelocityTrackerCompat.getXVelocity(mVelocityTracker, pointerId) > 3000) {
                        end = Calendar.getInstance().getTimeInMillis();
                        String url = MyUtilities.WEBSITE_NAME + "/log_activity/log_learn.php?user_name=" + userName + "&tfs=" + (end - start) + "&at=null";
                        MyUtilities.runHttpGETRequest(SubLearn.this, url);
                        active = false;
                        animateAndSet(img, newRandom.nextInt(), this, RIGHT);
                    }
                    if (VelocityTrackerCompat.getXVelocity(mVelocityTracker, pointerId) < -3000) {
                        end = Calendar.getInstance().getTimeInMillis();
                        String url = MyUtilities.WEBSITE_NAME + "/log_activity/log_learn.php?user_name=" + userName + "&tfs=" + (end - start) + "&at=null";
                        MyUtilities.runHttpGETRequest(SubLearn.this, url);
                        active = false;
                        animateAndSet(img, newRandom.nextInt(), this, LEFT);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    //mVelocityTracker.recycle();
                    Log.d("my_swipe", "okay");
                    break;
                case MotionEvent.ACTION_CANCEL:
                    // Return a VelocityTracker object back to be re-used by others.
                    mVelocityTracker.recycle();
                    break;
            }
            return true;
        }
    }

    //@Override
    public void onBackPrssed() {
        Log.d("database", ".db test..");
        //end = Calendar.getInstance();
        //String url = MyUtilities.WEBSITE_NAME + "log_activity/log_learn.php?at=" + (end.getTimeInMillis() - start.getTimeInMillis());
        //MyUtilities.runHttpGETRequest(this, url);
        //super.onDestroy();
    }
}
