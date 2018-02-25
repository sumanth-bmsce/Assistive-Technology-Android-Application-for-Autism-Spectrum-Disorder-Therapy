package com.samarth261.asd;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
//import android.widget.Toast;

import java.util.ArrayList;

import static com.samarth261.asd.MyUtilities.ASD_FOLDER_PATH;
import static com.samarth261.asd.MyUtilities.DIMENSIONS_HEIGHT;
import static com.samarth261.asd.MyUtilities.DIMENSIONS_WIDTH;
import static com.samarth261.asd.MyUtilities.SHARED_PREFERENCES_DIMENSIONS;
import static com.samarth261.asd.MyUtilities.listOfFilesMatchingAlongWithPath;
import static com.samarth261.asd.MyUtilities.listOfFilesMatchingAlongWithPathAL;
import static com.samarth261.asd.MyUtilities.showStarsAndExit;

public class dragAndDropOutline extends AppCompatActivity {


    private String userName;
    private String fullCategoryPath = null;
    private Dimes dimes;
    ImageView imgq, img0, img1, img2;
    private int correctChoice; // values 0,1,2
    RelativeLayout root;
    private View.OnTouchListener onTouchListener;
    private android.os.Vibrator vibrator;
    private TextToSpeech tts;
    long vibPattern[] = {0, 100, 50, 100, 50, 100, 50, 100, 50, 100, 50, 100, 50, 100};
    private View.OnDragListener dragListener;
    private MediaPlayer applause;

    int numberOfQuestionsAsked = 0;
    int numberOfQuestionsAnsweredRightAtFirstGo = 0;
    boolean firsrtPinch = false;

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

        /*public int whichImageIsClicked(Point p) {
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
        }*/
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_and_drop_outline);
        userName = getSharedPreferences(Config.SHARED_PREF_NAME, MODE_PRIVATE).getString(Config.USERNAME_SHARED_PREF, "");
        dimes = new Dimes(getSharedPreferences(SHARED_PREFERENCES_DIMENSIONS, MODE_PRIVATE).getInt(DIMENSIONS_WIDTH, 0),
                getSharedPreferences(SHARED_PREFERENCES_DIMENSIONS, MODE_PRIVATE).getInt(DIMENSIONS_HEIGHT, 0));
        setUp();
        fullCategoryPath = ASD_FOLDER_PATH + "ASD/Category/" + getIntent().getExtras().getString("Category");
        //fullCategoryPath = ASD_FOLDER_PATH + "ASD/Category/" + "Fruits";
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                //tts.speak(status+"",tts.QUEUE_ADD,null,null);
            }
        });
        applause = MediaPlayer.create(this, R.raw.applause_3s);
        applause.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("my_test_1-4-100", "eh");
                paint();
            }
        });
        paint();
    }

    private void setUp() {
        imgq = new ImageView(this);
        img0 = new ImageView(this);
        img1 = new ImageView(this);
        img2 = new ImageView(this);

        //imgq.setBackgroundColor(0xff00ff00);
        //img1.setBackgroundColor(0xfff0ff00);
        //img1.setBackgroundColor(0xff00fff0);
        //img2.setBackgroundColor(0xfff00ff0);

        img0.setTag(R.string.IMAGE_VIEW_TAG_KEY, 0);
        img1.setTag(R.string.IMAGE_VIEW_TAG_KEY, 1);
        img2.setTag(R.string.IMAGE_VIEW_TAG_KEY, 2);

        onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                ClipData.Item item = new ClipData.Item(v.getTag(R.string.IMAGE_VIEW_TAG_KEY).toString());
                ClipData data = new ClipData("image", new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDrag(data, shadowBuilder, null, 0);
                v.setVisibility(View.INVISIBLE);
                return false;
            }
        };
        img0.setOnTouchListener(onTouchListener);
        img1.setOnTouchListener(onTouchListener);
        img2.setOnTouchListener(onTouchListener);

        root = (RelativeLayout) findViewById(R.id.activity_drag_and_drop_outline);
        root.addView(imgq);
        root.addView(img0);
        root.addView(img1);
        root.addView(img2);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imgq.getLayoutParams();
        layoutParams.height = dimes.getSide();
        layoutParams.width = dimes.getSide();
        layoutParams.setMargins(dimes.getqLM(), dimes.getqTM(), 0, 0);

        layoutParams = (RelativeLayout.LayoutParams) img0.getLayoutParams();
        layoutParams.height = dimes.getSide();
        layoutParams.width = dimes.getSide();
        layoutParams.setMargins(dimes.get1LM(), dimes.get1TM(), 0, 0);

        layoutParams = (RelativeLayout.LayoutParams) img1.getLayoutParams();
        layoutParams.height = dimes.getSide();
        layoutParams.width = dimes.getSide();
        layoutParams.setMargins(dimes.get2LM(), dimes.get2TM(), 0, 0);

        layoutParams = (RelativeLayout.LayoutParams) img2.getLayoutParams();
        layoutParams.height = dimes.getSide();
        layoutParams.width = dimes.getSide();
        layoutParams.setMargins(dimes.get3LM(), dimes.get3TM(), 0, 0);

        dragListener = new View.OnDragListener() {
            //boolean dropped = false;

            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DROP:
                        //dropped = true;
                        int draggedIndex = Integer.parseInt(event.getClipData().getItemAt(0).getText().toString());
                        if (draggedIndex == correctChoice) {
                            //Toast.makeText(getApplicationContext(), "correct", Toast.LENGTH_LONG).show();
                            complement();
                        } else {
                            tellWrong();
                        }
                        break;
                    case DragEvent.ACTION_DRAG_STARTED:
                        //dropped = false;
                }
                return true;
            }
        };
        imgq.setOnDragListener(dragListener);
        root.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                if (event.getAction() == DragEvent.ACTION_DROP) {
                    makeThemVisible();
                }
                return true;
            }
        });
    }

    private void makeThemVisible() {//sets all the images to View.VISIBLE
        img0.setVisibility(View.VISIBLE);
        img1.setVisibility(View.VISIBLE);
        img2.setVisibility(View.VISIBLE);
    }

    private void tellWrong() {
        vibrator.vibrate(500);
        try {
            if (Build.VERSION.SDK_INT >= 21)
                tts.speak("wrong", TextToSpeech.QUEUE_FLUSH, null, null);
            else
                tts.speak("wrong", TextToSpeech.QUEUE_FLUSH, null);
        } catch (Exception e) {

        }
        makeThemVisible();
        firsrtPinch = false;
    }

    private void complement() {
        vibrator.vibrate(vibPattern, -1);
        /*try {
            if (Build.VERSION.SDK_INT >= 21)
                tts.speak("correct", TextToSpeech.QUEUE_FLUSH, null, null);
            else
                tts.speak("correct", TextToSpeech.QUEUE_FLUSH, null);
        } catch (Exception e) {
        }*/
        try {
            applause.start();
        } catch (Exception e) {

        }
        if (correctChoice == 0) {
            imgq.setBackground(img0.getDrawable());
            img0.setVisibility(View.INVISIBLE);
        } else if (correctChoice == 1) {
            imgq.setBackground(img1.getDrawable());
            img1.setVisibility(View.INVISIBLE);
        } else if (correctChoice == 2) {
            imgq.setBackground(img2.getDrawable());
            img2.setVisibility(View.INVISIBLE);
        }
        img0.setOnTouchListener(null);
        img1.setOnTouchListener(null);
        img2.setOnTouchListener(null);
        Handler handler = new Handler();
        /*handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                paint();
            }

        }, 3000);*/
        if (firsrtPinch) numberOfQuestionsAnsweredRightAtFirstGo++;
    }


    private void paint() {
        numberOfQuestionsAsked++;
        if (numberOfQuestionsAsked > 5) {
            showStarsAndExit(this, root, numberOfQuestionsAnsweredRightAtFirstGo);
            if (numberOfQuestionsAnsweredRightAtFirstGo > 3)
                MediaPlayer.create(this, R.raw.cheers_4s).start();
            else
                MediaPlayer.create(this, R.raw.applause_3s).start();
            return;
        }
        String images[] = new String[3];
        choose3RandomImages(images);
        img0.setImageDrawable(Drawable.createFromPath(outlineToImage(images[0])));
        img1.setImageDrawable(Drawable.createFromPath(outlineToImage(images[1])));
        img2.setImageDrawable(Drawable.createFromPath(outlineToImage(images[2])));
        correctChoice = (int) (3 * Math.random());
        imgq.setImageDrawable(Drawable.createFromPath(images[correctChoice]));
        img0.setOnTouchListener(onTouchListener);
        img1.setOnTouchListener(onTouchListener);
        img2.setOnTouchListener(onTouchListener);
        imgq.setOnDragListener(dragListener);
        imgq.setBackground(null);
        img0.setVisibility(View.VISIBLE);
        img1.setVisibility(View.VISIBLE);
        img2.setVisibility(View.VISIBLE);
        firsrtPinch = true;
    }

    private void choose3RandomImages(String images[]) {
        String listOfItems[] = listOfFilesMatchingAlongWithPath(fullCategoryPath + "/Items/", "[a-zA-z0-9]+");
        ArrayList<String> listOfImages = new ArrayList<String>();
        for (String s : listOfItems) {
            listOfImages.addAll(listOfFilesMatchingAlongWithPathAL(s, "img[0-9]+_outline.png"));
        }

        int i1, i2, i3;
        i2 = i3 = 0;
        i1 = (int) (Math.random() * listOfImages.size());
        do {
            i2 = (int) (Math.random() * listOfImages.size());
        } while (i2 == i1);
        do {
            i3 = (int) (Math.random() * listOfImages.size());
        } while (i2 == i1 || i3 == i1 || i2 == i3);
        images[0] = listOfImages.get(i1);
        images[1] = listOfImages.get(i2);
        images[2] = listOfImages.get(i3);
    }

    private String outlineToImage(String s) {
        String correct = s.replace("_outline.png", ".png");
        return correct;
    }

}
