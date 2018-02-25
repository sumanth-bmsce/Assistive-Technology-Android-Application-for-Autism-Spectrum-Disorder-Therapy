package com.samarth261.asd;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;

public class FillImage extends AppCompatActivity implements AcceptingLinearLayout.AcceptingLinearLayoutListener {
    String mCategory = "";
    String mItem = "";
    int mImageWidth = 0;
    int mImageHeight = 0;
    TextToSpeech tts = null;
    String spelling = null;
    ValueAnimator topBarBackgroundAnimator = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //todo support for sending to database
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_image);
        final ImageView iv;
        final ImageView ov;
        iv = (ImageView) findViewById(R.id.image_view);
        ov = (ImageView) findViewById(R.id.outline_view);

        Intent intent = getIntent();
        mCategory = intent.getExtras().getString("Category", "Nothing");


        String listOfItems[] = MyUtilities.listOfFilesMatching(
                MyUtilities.ASD_FOLDER_PATH+"ASD/Category/" + mCategory + "/Items/", ".+"
        );
        spelling = listOfItems[(int) (Math.random() * listOfItems.length)];
        final String item = MyUtilities.ASD_FOLDER_PATH+"ASD/Category/" + mCategory + "/Items/" + spelling;
        String listOfImages[] = MyUtilities.listOfFilesMatching(
                item, "img[0-9]+_outline.png"
        );
        final String selectedImage = listOfImages[(int) (Math.random() * listOfImages.length)];

        SharedPreferences sharedPreferences = getSharedPreferences(MyUtilities.SHARED_PREFERENCES_DIMENSIONS, Context.MODE_PRIVATE);
        mImageWidth = (int) (sharedPreferences.getInt(MyUtilities.DIMENSIONS_WIDTH, 0) / 100 * 75);
        mImageHeight = mImageWidth;

        ov.getLayoutParams().width = mImageWidth;
        ov.getLayoutParams().height = mImageHeight;
        iv.getLayoutParams().width = mImageWidth;
        iv.getLayoutParams().height = mImageHeight;


        ov.setImageDrawable(Drawable.createFromPath(item + File.separator + selectedImage));
        iv.setOnTouchListener(new MyStrokeListener(
                item + File.separator + (selectedImage.split("_")[0]) + ".png",
                new ImageParams(mImageHeight, mImageWidth, 0, 0), iv));

        /*findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextOutline();
            }
        });*/

        String templist[] = item.split("/");
        mItem = templist[templist.length - 1];
        //Log.d("my_test", mItem);

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    //tts.setLanguage(Locale.UK);
                }
            }
        });
        tts.setSpeechRate(.8f);
        /*findViewById(R.id.update_radius).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int temp = Integer.parseInt(((EditText) findViewById(R.id.brush_radius)).getText().toString());
                    if (temp >= 1 && temp <= 50)
                        EnvironmentVariables.radius = temp;
                } catch (Exception e) {

                }
            }
        });*/
        ((AcceptingLinearLayout) findViewById(R.id.topLinearLayout)).setAcceptingListener(this);
        initBackgroundAnimator();
    }

    private void initBackgroundAnimator() {
        topBarBackgroundAnimator = new ValueAnimator();
        topBarBackgroundAnimator.setIntValues(0, 155);
        topBarBackgroundAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        topBarBackgroundAnimator.setDuration(1000);
        topBarBackgroundAnimator.setRepeatMode(ValueAnimator.REVERSE);
        topBarBackgroundAnimator.setRepeatCount(ValueAnimator.INFINITE);
        topBarBackgroundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            AcceptingLinearLayout topBar = (AcceptingLinearLayout) findViewById(R.id.topLinearLayout);

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                topBar.setBackgroundColor(Color.argb((int) (animation.getAnimatedValue()), 255, 255, 0));
            }
        });
        topBarBackgroundAnimator.addPauseListener(new Animator.AnimatorPauseListener() {
            @Override
            public void onAnimationPause(Animator animation) {
                ((AcceptingLinearLayout) findViewById(R.id.topLinearLayout)).setBackgroundColor(0x00ffffff);
            }

            @Override
            public void onAnimationResume(Animator animation) {

            }
        });
    }

    public void nextOutline() {
        topBarBackgroundAnimator.pause();
        String listOfFruits[] = MyUtilities.listOfFilesMatching(
                MyUtilities.ASD_FOLDER_PATH+"ASD/Category/" + mCategory + "/Items/", ".+"
        );
        spelling = listOfFruits[(int) (Math.random() * listOfFruits.length)];
        final String fruit = MyUtilities.ASD_FOLDER_PATH+"ASD/Category/" + mCategory + "/Items/" + spelling;
        String listOfImages[] = MyUtilities.listOfFilesMatching(
                fruit, "img[0-9]+_outline.png"
        );
        final String selectedImage = listOfImages[(int) (Math.random() * listOfImages.length)];
        //Log.d("my_test", selectedImage);
        final ImageView iv = (ImageView) findViewById(R.id.image_view);
        final ImageView ov = ((ImageView) findViewById(R.id.outline_view));
        final RelativeLayout.LayoutParams imgParams = (RelativeLayout.LayoutParams) iv.getLayoutParams();
        iv.setOnTouchListener(null);
        ValueAnimator valueAnimatorGrow = new ValueAnimator();
        final ValueAnimator valueAnimatorShrink = new ValueAnimator();
        final ValueAnimator valueAnimatorShake = new ValueAnimator();
        valueAnimatorGrow.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimatorGrow.setIntValues(mImageWidth, mImageWidth + 100);
        valueAnimatorGrow.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                imgParams.width = (int) (animation.getAnimatedValue());
                imgParams.height = (int) (animation.getAnimatedValue());
                iv.setLayoutParams(imgParams);
                ov.setLayoutParams(imgParams);
            }
        });
        valueAnimatorGrow.setDuration(1000);
        valueAnimatorGrow.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (Build.VERSION.SDK_INT >= 21)
                    tts.speak(mItem, TextToSpeech.QUEUE_FLUSH, null, null);
                else
                    tts.speak(mItem, TextToSpeech.QUEUE_FLUSH, null);
                valueAnimatorShake.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        valueAnimatorShake.setFloatValues(0, 5);
        valueAnimatorShake.setInterpolator(new Interpolator() {
            @Override
            public float getInterpolation(float input) {
                float res = (float) Math.sin(8 * Math.PI * input);
                return res;
            }
        });
        valueAnimatorShake.setDuration(1000);
        valueAnimatorShake.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                valueAnimatorShrink.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimatorShake.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                iv.setRotation((float) animation.getAnimatedValue());
                ov.setRotation((float) animation.getAnimatedValue());
            }
        });


        valueAnimatorShrink.setIntValues(mImageWidth + 100, mImageWidth);
        valueAnimatorShrink.setDuration(1000);
        valueAnimatorShrink.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimatorShrink.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                imgParams.width = (int) (animation.getAnimatedValue());
                imgParams.height = (int) (animation.getAnimatedValue());
                iv.setLayoutParams(imgParams);
                ov.setLayoutParams(imgParams);
            }
        });
        valueAnimatorShrink.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ov.setImageDrawable(Drawable.createFromPath(fruit + File.separator + selectedImage));
                iv.setOnTouchListener(new MyStrokeListener(
                        fruit + File.separator + (selectedImage.split("_")[0]) + ".png",
                        new ImageParams(mImageWidth, mImageHeight, 0, 0), iv));
                String templist[] = fruit.split("/");
                mItem = templist[templist.length - 1];
                ((AcceptingLinearLayout) findViewById(R.id.topLinearLayout)).removeAllViews();
                //Log.d("my_test", mItem);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimatorGrow.start();
    }

    @Override
    public void onRearrangeFinish() {
        ((LinearLayout) findViewById(R.id.bottomLinearLayout)).removeAllViews();
        nextOutline();
    }

    class MyStrokeListener implements View.OnTouchListener {
        private Bitmap mSource;
        private ImageParams mImageParams;
        private ImageView mImageView;
        private double oldX;
        private double oldY;
        private int mRadius;
        private Bitmap bm;
        private int totalNonTransparentPixels;
        private int mSkipPixels = 1;

        MyStrokeListener() {
            mSource = null;
            mImageParams = null;
            bm = null;
        }

        MyStrokeListener(String sourcePath, ImageParams imageParams, ImageView imageView) {
            mSource = BitmapFactory.decodeFile(sourcePath);
            mImageParams = imageParams;
            mImageParams.bitmapWidth = mSource.getWidth();
            mImageParams.bitmapHeight = mSource.getHeight();
            mRadius = (int) (mSource.getWidth() / 100 * 5);
            mSkipPixels = (int) (mRadius * 0.50);
            bm = Bitmap.createBitmap(imageParams.bitmapWidth, imageParams.bitmapHeight, Bitmap.Config.ARGB_8888);
            for (int i = 0; i < bm.getHeight(); i++) {
                for (int j = 0; j < bm.getWidth(); j++) {
                    bm.setPixel(j, i, 0x00000000);
                }
            }
            totalNonTransparentPixels = numberOfNonTransparent(mSource);

            mImageView = imageView;
            mImageView.setImageBitmap(bm);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            try {
                double x, y;
                x = event.getX();
                y = event.getY();
                x = x / mImageParams.viewWidth * mImageParams.bitmapWidth;
                y = y / mImageParams.viewHeight * mImageParams.bitmapHeight;
                int flag = 1;
                if (x >= mImageParams.bitmapHeight - 1 || x <= 1) flag = 0;
                if (y >= mImageParams.bitmapWidth - 1 || y <= 1) flag = 0;
                //Log.d("my_paint_coord", (int) event.getX() + " " + (int) event.getY());
                if (event.getAction() == MotionEvent.ACTION_MOVE && flag == 1) {
                    double xFactor, yFactor;
                    double temp = Math.max(Math.abs(oldX - x), Math.abs(oldY - y));
                    xFactor = (x - oldX) * 1.0 / temp;
                    yFactor = (y - oldY) * 1.0 / temp;
                    for (int i = 1; i <= temp; i += mSkipPixels)
                        //    bm.setPixel((int) x, (int) y, mSource.getPixel((int) x, (int) y));
                        paintCircle((int) (oldX + xFactor * i), (int) (oldY + yFactor * i));
                    //bm.setPixel((int) (oldX + xFactor * i), (int) (oldY + yFactor * i), (int) color);
                    mImageView.setImageBitmap(bm);
                    oldX = x;
                    oldY = y;
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //Log.d("my_paint", "down at " + x + " " + y);
                    mRadius = mRadius;
                    oldX = x;
                    oldY = y;
                    paintCircle((int) oldX, (int) oldY);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    //Log.d("my_paint", "up at " + x + " " + y);
                    //oldX = oldY = 0;
                    int np = numberOfNonTransparent(bm);
                    double percent = (np * 1.0 / totalNonTransparentPixels * 100);
                    //Toast.makeText(getApplicationContext(), percent + "%", Toast.LENGTH_LONG).show();
                    if (percent > 95) {
                        spellingCheck();
                    }

                }
            } catch (Exception e) {
                //Log.d("my_error_100", e.getMessage());
            }
            return true;
        }

        public void paintCircle(int x, int y) {

            for (int i = -mRadius; i <= mRadius; i++)
                for (int j = -mRadius; j < mRadius; j++) {
                    //Log.d("my_log_brush", i + "  " + j);
                    if (i * i + j * j < mRadius * mRadius) {
                        try {
                            bm.setPixel(x + i, y + j, mSource.getPixel(x + i, y + j));
                        } catch (Exception e) {
                            //Log.d("my_paint_error", e.getMessage());
                        }
                    }
                }
        }

        public void setBrushRadius(int x) {
            mRadius = x;
        }

        public int numberOfNonTransparent(Bitmap bm) {
            int width = bm.getWidth();
            int height = bm.getHeight();
            int color = 0;
            int transpirancy = 0;
            int counter = 0;
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    color = bm.getPixel(j, i);
                    //Log.d("my_color" , color+"");
                    transpirancy = color >>> 24;
                    if (transpirancy > 0xa0) {
                        counter++;
                    }
                }
            }
            Log.d("my_counter_size:", bm.getWidth() + " x " + bm.getHeight());
            Log.d("my_counter:", counter + "");
            return counter;
        }
    }

    public void spellingCheck() {
        Log.d("16june",findViewById(R.id.bottomLinearLayout).getMeasuredHeight()+"");
        findViewById(R.id.image_view).setOnTouchListener(null);
        populateTheBottomBar(spelling.toUpperCase());
        makeTheTopBarReceive();
        topBarBackgroundAnimator.start();
    }

    public void populateTheBottomBar(String s) {
        //s = s.toUpperCase();
        LinearLayout bottomLayout = (LinearLayout) findViewById(R.id.bottomLinearLayout);
        AcceptingLinearLayout topLayout = (AcceptingLinearLayout) findViewById(R.id.topLinearLayout);
        topLayout.setCorrectString(s);
        s = mixup(s);
        int widthOfScreen = (findViewById(R.id.bottomLinearLayout)).getWidth();
        for (int i = 0; i < s.length(); i++) {
            final char letter = s.charAt(i);
            TextView textView = new TextView(this);
            textView.setText(letter + "");
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(widthOfScreen / s.length(), -1);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(getResources().getDimension(R.dimen.fillImageActivityTextSize));
            textView.setLayoutParams(layoutParams);
            textView.setBackgroundColor(0x88884444);
            textView.setId(View.generateViewId());
            textView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    ClipData.Item id = new ClipData.Item(v.getId() + "");
                    ClipData.Item character = new ClipData.Item(letter + "");
                    Log.e("myerror102", v.getId() + "");
                    ClipData dragData = new ClipData(letter + "", new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, id);
                    dragData.addItem(character);
                    View.DragShadowBuilder myShadow = new View.DragShadowBuilder(v);
                    v.setAlpha(0);
                    v.startDrag(dragData, myShadow, null, 0);
                    return false;
                }
            });
            bottomLayout.addView(textView);
        }
    }

    public void makeTheTopBarReceive() {
        AcceptingLinearLayout topLayout = (AcceptingLinearLayout) findViewById(R.id.topLinearLayout);
        topLayout.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                Log.e("mytest100", "receiving");
                if (DragEvent.ACTION_DROP == event.getAction()) {
                    int id = Integer.parseInt((String) event.getClipData().getItemAt(0).getText());
                    TextView textView = (TextView) FillImage.this.findViewById(id);
                    textView.setAlpha(1);
                    if (event.getClipData().getItemAt(1).getText().charAt(0) == ((AcceptingLinearLayout) v).nextCharacter) {
                        try {
                            ((LinearLayout) textView.getParent()).removeView(textView);
                            ((AcceptingLinearLayout) v).addView(textView);
                            textView.setOnTouchListener(null);
                        } catch (Exception e) {
                            Log.e("mytest101", e.getMessage());
                        }
                    }
                } else if (DragEvent.ACTION_DRAG_ENDED == event.getAction()) {
                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.bottomLinearLayout);
                    for (int i = 0; i < linearLayout.getChildCount(); i++) {
                        TextView textView = (TextView) linearLayout.getChildAt(i);
                        textView.setAlpha(1);
                    }
                }
                return true;
            }
        });
    }

    public String mixup(String string) {
        char s[] = string.toCharArray();
        for (int i = 0; i < s.length; i++) {
            int i1 = (int) (Math.random() * s.length);
            int i2 = (int) (Math.random() * s.length);
            char t = s[i2];
            s[i2] = s[i1];
            s[i1] = t;
        }
        if (new String(s).equals(string) && string.length() > 2)
            return mixup(new String(s));
        return new String(s);
    }
}


class ImageParams {
    public int viewWidth;
    public int viewHeight;
    public int bitmapWidth;
    public int bitmapHeight;

    ImageParams() {
        viewHeight = viewWidth = bitmapWidth = bitmapHeight = 0;
    }

    ImageParams(int vw, int vh, int bw, int bh) {
        viewWidth = vw;
        viewHeight = vh;
        bitmapWidth = bw;
        bitmapHeight = bh;
    }
}
