package com.samarth261.asd;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.LinearGradient;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Handler;

/**
 * Created by Samarth on 25-07-2016.
 */

public class MyUtilities {

    public static String SHARED_PREFERENCES_DIMENSIONS = "dimensions";
    public static String DIMENSIONS_WIDTH = "dimensions_width";
    public static String DIMENSIONS_HEIGHT = "dimensions_height";
    public static String WEBSITE_NAME = "http://ashadeepautismapp.esy.es/ashadeepapp/";
    public static String SETTINGS_SHARED_PREFERENCES = "settings_preferences";
    public static String OUTLINE_GAME_MODE = "outline_game_mode";
    public static String OUTLINE_GAME_MODE_PINCH = "pinch";
    public static String OUTLINE_GAME_MODE_DND = "dnd";
    //public static String WEBSITE_NAME = "http://192.168.1.101:6390/ashadeep/";
    public static String PINCH = "Pinch";
    public static String ASD_FOLDER_PATH = Environment.getExternalStorageDirectory().getPath()+"/";
    //public static String ASD_FOLDER_PATH = "/sdcard/";

    public static int numberOfFilesMatching(String filePath, String regularExpression) {
        int n = 0;
        File file = new File(filePath);
        String templist[] = file.list();
        for (int i = 0; i < templist.length; i++) {
            if (templist[i].matches(regularExpression)) {
                n++;
            }
        }
        return n;
    }

    public static String[] listOfFilesMatching(String filePath, String regularExpression) {
        File file = new File(filePath);
        String[] templist = file.list();
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < templist.length; i++) {
            if (templist[i].matches(regularExpression)) {
                list.add(templist[i]);
            }
        }

        return (list.toArray(new String[0]));
    }

    public static String[] listOfFilesMatchingAlongWithPath(String filePath, String regularExpression) {
        File file = new File(filePath);
        String[] templist = file.list();
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < templist.length; i++) {
            if (templist[i].matches(regularExpression)) {
                list.add(filePath + File.separator + templist[i]);
            }
        }
        return (list.toArray(new String[0]));//new array and all is created because Object[] can't be cast to String directly
    }

    public static ArrayList<String> listOfFilesMatchingAlongWithPathAL(String filePath, String regularExpression) {
        File file = new File(filePath);
        String[] templist = file.list();
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < templist.length; i++) {
            if (templist[i].matches(regularExpression)) {
                list.add(filePath + File.separator + templist[i]);
            }
        }
        return list;//new array and all is created because Object[] can't be cast to String directly
    }

    public static class MyRandomIntGenerator {
        int from;
        int to;
        int randomNumber;

        public MyRandomIntGenerator(int fromIncluded, int toIncluded) {
            this.from = fromIncluded;
            this.to = toIncluded;
            if (from > to) {
                from = toIncluded;
                to = fromIncluded;
            }
            randomNumber = getRandomNumber();
        }

        public int nextInt() {
            int temp;
            do {
                temp = getRandomNumber();
            } while (temp == randomNumber && this.from != this.to);
            randomNumber = temp;
            return temp;
        }

        private int getRandomNumber() {
            return from + (int) (Math.random() * (to - from + 1));
        }
    }

    public static void showStarsAndExit(final AppCompatActivity activity, RelativeLayout rl, int numberOfQuestionAnsweredRightAtFirstGo) {
        try {
            double side, dist, dist2;
            int s, a;
            s = 10;
            a = 1;
            side = (rl.getWidth() / (3.0 + 4 * (a * 1.0 / s)));
            dist = (rl.getWidth() - 3.0 * side) / (4);
            dist2 = ((rl.getWidth() - 2 * side - dist) / 2.0);
            RelativeLayout showStarRelativeLayout = new RelativeLayout(activity);
            showStarRelativeLayout.setBackgroundColor(0x00ffffff);
            showStarRelativeLayout.setGravity(Gravity.CENTER_VERTICAL);
            ImageView starsOutline[] = new ImageView[5];
            ImageView stars[] = new ImageView[numberOfQuestionAnsweredRightAtFirstGo];
            for (int i = 0; i < 5; i++) {
                starsOutline[i] = new ImageView(activity);
                starsOutline[i].setId(starsOutline[i].hashCode());
                starsOutline[i].setImageResource(R.drawable.star_outline);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) side, (int) side);
                switch (i) {
                    case 0:
                        params.leftMargin = (int) dist;
                        break;
                    case 1:
                        params.leftMargin = (int) (dist * 2 + side);
                        break;
                    case 2:
                        params.leftMargin = (int) (dist * 3 + 2 * side);
                        break;
                    case 3:
                        params.leftMargin = (int) (dist2);
                        params.addRule(RelativeLayout.BELOW, starsOutline[0].getId());
                        params.topMargin = (int) dist;
                        break;
                    case 4:
                        params.leftMargin = (int) (dist2 + side + dist);
                        params.addRule(RelativeLayout.BELOW, starsOutline[0].getId());
                        params.topMargin = (int) dist;
                        break;
                }
                showStarRelativeLayout.addView(starsOutline[i], params);

            }
            rl.removeAllViews();
            //Toast.makeText(activity, side + "\n" + dist, Toast.LENGTH_LONG).show();
            rl.addView(showStarRelativeLayout, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            for (int i = 0; i < numberOfQuestionAnsweredRightAtFirstGo; i++) {
                stars[i] = new ImageView(activity);
                stars[i].setImageResource(R.drawable.star);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(starsOutline[i].getLayoutParams());
                params.leftMargin = ((RelativeLayout.LayoutParams) (starsOutline[i].getLayoutParams())).leftMargin;
                if (i > 2) {
                    params.addRule(RelativeLayout.BELOW, starsOutline[0].getId());
                    params.topMargin = (int) dist;
                }
                stars[i].setAlpha(0f);
                showStarRelativeLayout.addView(stars[i], params);
                ObjectAnimator obj = ObjectAnimator.ofFloat(stars[i], "Alpha", 0, 1);
                obj.setInterpolator(new AccelerateInterpolator());
                obj.setDuration(500);
                obj.setStartDelay(i * 500 + 500);
                obj.start();
                if (i == numberOfQuestionAnsweredRightAtFirstGo - 1) {
                    obj.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                        /*android.os.Handler handler = new android.os.Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                activity.finish();
                            }
                        },1000);*/
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                }
            }
        } catch (Exception e) {
            Log.d("stars_not_working1001", e.getMessage());
        }
    }

    /**
     * public static boolean sendDataToServer(final String user_name ,final String gameName ,final int score){
     * <p>
     * Thread thread = new Thread(new Runnable() {
     *
     * @Override public void run() {
     * try{
     * URL url = new URL("http://ashadeepautismapp.esy.es/ashadeepapp/log_new_game_score.php?user_id="+user_name+"&game="+gameName+"&score="+score);
     * Log.d("my_test" , url.toString());
     * HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
     * try{
     * InputStream reply = urlConnection.getInputStream();
     * BufferedReader br = new BufferedReader(new InputStreamReader(reply));
     * String replyText = br.readLine();
     * if(replyText == "OKAY"){
     * Log.d("my_test" , "OKAY");
     * }else{
     * Log.d("my_test_notokay",replyText);
     * }
     * }catch(Exception e){
     * Log.d("my_error" , e.getMessage());
     * }
     * }catch (Exception e){
     * Log.d("my_error_10",e.getMessage());
     * }
     * }
     * });
     * thread.run();
     * return false;
     * }
     */

    public static boolean sendDataToServer(final Context context, final String user_name, final String gameName, final int score) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = WEBSITE_NAME + "log_new_game_score.php?user_id=" + user_name + "&game=" + gameName + "&score=" + score;

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("my_test", response);
                        // Display the first 500 characters of the response string.
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("my_error_101", error.getMessage());
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
        return true;
    }

    public static boolean runHttpGETRequest(final Context context, final String url) {
        RequestQueue queue = Volley.newRequestQueue(context);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("my_error_101", error.getMessage());
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        return true;
    }
}
