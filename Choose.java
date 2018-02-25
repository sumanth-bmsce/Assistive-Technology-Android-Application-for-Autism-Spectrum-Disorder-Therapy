package com.samarth261.asd;

import android.animation.ValueAnimator;
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

import static com.samarth261.asd.MyUtilities.OUTLINE_GAME_MODE;
import static com.samarth261.asd.MyUtilities.OUTLINE_GAME_MODE_DND;
import static com.samarth261.asd.MyUtilities.OUTLINE_GAME_MODE_PINCH;
import static com.samarth261.asd.MyUtilities.SETTINGS_SHARED_PREFERENCES;

public class Choose extends AppCompatActivity {
    //String categoryPath = "/storage/sdcard0/ASD/Category";
    //String resourcePath = "/storage/sdcard0/ASD/Others/ActivityWise/Choose";
    String categoryPath = MyUtilities.ASD_FOLDER_PATH+"ASD/Category";
    String resourcePath = MyUtilities.ASD_FOLDER_PATH+"ASD/Others/ActivityWise/Choose";
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
        setContentView(R.layout.activity_choose);
        File category = new File(categoryPath);
        final String list[] = category.list();
        rl = ((RelativeLayout) findViewById(R.id.relativeLayout2));
        rl.setBackground(Drawable.createFromPath(resourcePath + "/Background2.jpg"));
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
        final ScrollView sv = (ScrollView) findViewById(R.id.sv);
        sv.setBackgroundColor(0x00ffffff);
        final RelativeLayout svrl = (RelativeLayout) findViewById(R.id.svrl);
        svrl.setBackgroundColor(0x00ffffff);
        for (int i = 0; i < list.length; i++) {
            final int index = i;

            ImageButton img = new ImageButton(this);
            img.setBackground(Drawable.createFromPath(categoryPath + "/" + list[index] + "/Thumb.png"));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dimes.getSide(), dimes.getSide());
            int l = dimes.getDist() + ((index % 2 == 1) ? dimes.getDist() + dimes.getSide() : 0);
            int t = dimes.getDist() + (index / 2) * (dimes.getSide() + dimes.getDist());
            params.setMargins(l, dimes.getLayoutHeight(), 0, 0);
            customAnimationy(img, t, l, 250 * index + 250);
            svrl.addView(img, params);
            img.setClickable(true);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itHasEnoughImages(list[index]) || getIntent().getIntExtra("Level", -1) == 2 || getIntent().getIntExtra("Level", -1) == 1) {
                        Intent intent = new Intent();
                        intent.putExtra("Category", list[index]);
                        switch (getIntent().getIntExtra("Level", -1)) {
                            case 2:
                                intent.setClass(getApplicationContext(), Learn.class);
                                break;
                            case 3:
                                intent.setClass(getApplicationContext(), Match.class);
                                break;
                            case 4:
                                SharedPreferences sharedPreferences = getSharedPreferences(SETTINGS_SHARED_PREFERENCES, MODE_PRIVATE);
                                String mode = sharedPreferences.getString(OUTLINE_GAME_MODE, OUTLINE_GAME_MODE_DND);
                                if (mode.equals(OUTLINE_GAME_MODE_PINCH)) {
                                    intent.setClass(getApplicationContext(), PinchAndFly.class);
                                } else if (mode.equals(OUTLINE_GAME_MODE_DND)) {
                                    intent.setClass(getApplicationContext(), dragAndDropOutline.class);
                                }
                                break;
                            case 1:
                                intent.setClass(getApplicationContext(), FillImage.class);
                        }

                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Insufficient Images!\nAdd more", Toast.LENGTH_LONG);
                    }
                }
            });


        }

    }

    boolean itHasEnoughImages(String selectedCategory) {
        if (getIntent().getIntExtra("Level", -1) == 3) {
            File selectedCategoryFile = new File(categoryPath + "/" + selectedCategory + "/Items");
            Log.d("myemergency", selectedCategoryFile.getPath());
            if (selectedCategoryFile.list().length >= 3) return true;
        } else if (getIntent().getIntExtra("Level", -1) == 4) {
            File selectedCategoryFile = new File(categoryPath + "/" + selectedCategory + "/Items");
            if (selectedCategoryFile.list().length >= 3) {
                String[] listOfSubCategories = selectedCategoryFile.list();
                //File subCategoryFile = null;
                int counter = 0;
                for (int i = 0; i < listOfSubCategories.length; i++) {
                    //subCategoryFile = new File(categoryPath + "/" + selectedCategory + "/Items/" + listOfSubCategories[i]);
                    if (MyUtilities.numberOfFilesMatching(categoryPath + "/" + selectedCategory + "/Items/" + listOfSubCategories[i], "img[0-9]+_outline\\.png") > 0) {
                        Log.d("mycategoryies", MyUtilities.listOfFilesMatching(categoryPath + "/" + selectedCategory + "/Items/" + listOfSubCategories[i], "img[0-9]+_outline\\.png").toString());
                        counter++;
                    }
                }
                if (counter >= 3) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }//priority 100 support for case 4 checking if the files are full
        return false;
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
