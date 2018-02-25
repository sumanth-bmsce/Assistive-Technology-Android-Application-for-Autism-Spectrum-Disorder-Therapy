package com.samarth261.asd;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;

public class ProgressActivity extends AppCompatActivity {

    /*
    Following are a list of constants that will be used
     */

    public static String MODE_ALL = "mode_all";
    public static String MODE_WEEKLY = "mode_weekly";
    public static String MODE_OVERALL_WEEKWISE = "mode_overall_weekwise";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        /* there will be two modes:
            i) all
            ii) particular
                a)learn
                b)match
                c)outline
         */
        String mode = intent.getExtras().getString("mode", "");
        Log.d("my_mode", mode);
        if (mode.equals(MODE_ALL)) {
            mode_all();

        }
        if (mode.equals(MODE_WEEKLY)) {
            String game = intent.getExtras().getString("game", "");
            Log.d("my_debug_weekly_102", game);
            mode_weekly(game);
        }
        if (mode.equals(MODE_OVERALL_WEEKWISE)) {
            mode_entireProgressWeekWise();
        }

    }

    private void mode_all() {
        setContentView(R.layout.activity_progress);
        ((TextView) findViewById(R.id.progress_activity_user_name)).setText((CharSequence)
                getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE).getString(Config.USERNAME_SHARED_PREF, "Not Available")
        );
        final GraphView lineGraph1 = (GraphView) findViewById(R.id.line_graph1);
        final GraphView lineGraph2 = (GraphView) findViewById(R.id.line_graph2);
        final GraphView lineGraph3 = (GraphView) findViewById(R.id.line_graph3);
        final GraphView lineGraph4 = (GraphView) findViewById(R.id.line_graph4);
        lineGraph1.setTitleTextSize(40);
        lineGraph2.setTitleTextSize(40);
        lineGraph3.setTitleTextSize(40);
        lineGraph4.setTitleTextSize(40);
        lineGraph1.getGridLabelRenderer().setHorizontalAxisTitle("Trials Number");
        lineGraph1.getGridLabelRenderer().setVerticalAxisTitle("Time in Seconds");
        lineGraph1.getGridLabelRenderer().setHorizontalAxisTitleTextSize(30);
        lineGraph1.getGridLabelRenderer().setVerticalAxisTitleTextSize(30);
        lineGraph2.getGridLabelRenderer().setHorizontalAxisTitle("Trial number");
        lineGraph2.getGridLabelRenderer().setVerticalAxisTitle("Time in seconds");
        lineGraph2.getGridLabelRenderer().setHorizontalAxisTitleTextSize(30);
        lineGraph2.getGridLabelRenderer().setVerticalAxisTitleTextSize(30);
        lineGraph3.getGridLabelRenderer().setHorizontalAxisTitle("Trial number");
        lineGraph3.getGridLabelRenderer().setVerticalAxisTitle("Time in seconds");
        lineGraph3.getGridLabelRenderer().setHorizontalAxisTitleTextSize(30);
        lineGraph3.getGridLabelRenderer().setVerticalAxisTitleTextSize(30);
        lineGraph4.getGridLabelRenderer().setHorizontalAxisTitle("Trial number");
        lineGraph4.getGridLabelRenderer().setVerticalAxisTitle("Time in seconds");
        lineGraph4.getGridLabelRenderer().setHorizontalAxisTitleTextSize(30);
        lineGraph4.getGridLabelRenderer().setVerticalAxisTitleTextSize(30);

        //DataPoint dataPoints[] = null;
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String user_id = sharedPreferences.getString(Config.USERNAME_SHARED_PREF, "");
        //networking

        RequestQueue queue = Volley.newRequestQueue(this);
        final String url = MyUtilities.WEBSITE_NAME + "progress/get_progress.php?user_id=" + user_id + "&skill=";
        //Log.d("my_test_103", url + "attention_time");

// Request a string response from the provided URL.
        StringRequest stringRequest1 = new StringRequest(Request.Method.GET, url + "attention_time",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e("final_graph",url + "attention_time");
                            Calendar calendar = Calendar.getInstance();
                            String values[] = response.split(" ");
                            DataPoint dataPoints[] = new DataPoint[values.length / 2];
                            for (int i = 0; i < values.length-1; i += 2) {
                                calendar.setTimeInMillis(Long.parseLong(values[i]) * 1000);
                                dataPoints[i / 2] = new DataPoint((i) / 2, Integer.parseInt(values[i + 1]));
                            }
                            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);
                            lineGraph1.addSeries(series);
                            lineGraph1.setTitle("Attention Time");
                            //lineGraph1.getViewport().setMaxX(20);
                            lineGraph1.getViewport().setScalable(true);
                        } catch (Exception e) {
                            Log.d("my_error", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("my_error_102", error.getMessage());
            }
        });
        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url + "object_recognition_time",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            lineGraph2.setTitle("Object Recognition Time");
                            String values[] = response.split(" ");
                            DataPoint dataPoints[] = new DataPoint[values.length / 2];

                            for (int i = 0; i < values.length-1; i += 2) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(Long.parseLong(values[i]) * 1000);
                                dataPoints[i / 2] = new DataPoint(i / 2, Integer.parseInt(values[i + 1]));
                                Log.d("my_test_date", "y:" + calendar.get(Calendar.YEAR) + "m:" + calendar.get(Calendar.MONTH) + "d:" + calendar.get(Calendar.DAY_OF_MONTH));
                            }
                            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);
                            lineGraph2.addSeries(series);
                            lineGraph2.setTitle("Object Recognition Time");
                            lineGraph2.getViewport().setScalable(true);
                            //lineGraph2.getViewport().setMaxX(20);
                        } catch (Exception e) {
                            Log.d("my_error", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("my_error_102", error.getMessage());
            }
        });
        StringRequest stringRequest3 = new StringRequest(Request.Method.GET, url + "motor_delay",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String values[] = response.split(" ");
                            DataPoint dataPoints[] = new DataPoint[values.length / 2];
                            for (int i = 0; i < values.length-1; i += 2) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(Long.parseLong(values[i]) * 1000);
                                dataPoints[i / 2] = new DataPoint(i / 2, Integer.parseInt(values[i + 1]));
                                Log.d("my_test_date", "y:" + calendar.get(Calendar.YEAR) + "m:" + calendar.get(Calendar.MONTH) + "d:" + calendar.get(Calendar.DAY_OF_MONTH)+"  "+Integer.parseInt(values[i + 1]));
                            }
                            lineGraph3.setTitle("Motor Delay");
                            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);
                            lineGraph3.addSeries(series);
                            //lineGraph3.getViewport().setMaxX(20);
                            lineGraph3.getViewport().setScalable(true);
                        } catch (Exception e) {
                            Log.d("my_error", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("my_error_102", error.getMessage());
            }
        });
        StringRequest stringRequest4 = new StringRequest(Request.Method.GET, url + "grasping_time",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String values[] = response.split(" ");
                            DataPoint dataPoints[] = new DataPoint[values.length / 2];
                            for (int i = 0; i < values.length-1; i += 2) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(Long.parseLong(values[i]) * 1000);
                                dataPoints[i / 2] = new DataPoint(i / 2, Integer.parseInt(values[i + 1]));
                                Log.d("my_test_date", "y:" + calendar.get(Calendar.YEAR) + "m:" + calendar.get(Calendar.MONTH) + "d:" + calendar.get(Calendar.DAY_OF_MONTH));
                            }
                            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);
                            lineGraph4.addSeries(series);
                            lineGraph4.setTitle("Grasping Time");
                            //lineGraph3.getViewport().setMaxX(20);
                            lineGraph4.getViewport().setScalable(true);
                        } catch (Exception e) {
                            Log.d("my_error", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("my_error_102", error.getMessage());
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest1);
        queue.add(stringRequest2);
        queue.add(stringRequest3);
        queue.add(stringRequest4);
        //end
    }

    private void mode_weekly(final String game) {
        setContentView(R.layout.progress_activity_weekly);
        ((TextView) findViewById(R.id.progress_activity_weekly_user_name)).setText((CharSequence)
                getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE).getString(Config.USERNAME_SHARED_PREF, "Not Available")
        );
        final LinearLayout rootView = (LinearLayout) findViewById(R.id.progress_activity_weekly_root_view);
        RequestQueue queue = Volley.newRequestQueue(this);
        String user_id = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE).getString(Config.USERNAME_SHARED_PREF, "");
        String url = MyUtilities.WEBSITE_NAME + "get_progress.php?user_id=" + user_id + "&game=" + game;
        Log.d("my_test_weekly_100", url);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Calendar calendar = Calendar.getInstance();
                            //Log.d("my_test_102", response);
                            String values[] = response.split(" ");
                            Calendar startWeek = Calendar.getInstance();
                            startWeek.setTimeInMillis(Long.parseLong(values[0]) * 1000);

                            makeThisMonday(startWeek);

                            Calendar endWeek = Calendar.getInstance();
                            endWeek.setTimeInMillis(Long.parseLong(values[values.length - 2]) * 1000);

                            makeThisMonday(endWeek);
                            /*Log.d("my_weekly_end_date", endWeek.get(Calendar.WEEK_OF_YEAR) + "");
                            Log.d("my_test_date", "y:" + endWeek.get(Calendar.YEAR) +
                                    "m:" + endWeek.get(Calendar.MONTH) +
                                    "d:" + endWeek.get(Calendar.DAY_OF_MONTH));*/
                            int numberOfWeeks = 0;
                            try {
                                numberOfWeeks = (int) (
                                        ((endWeek.getTimeInMillis()) - (startWeek.getTimeInMillis())) / (7 * 24 * 60 * 60 * 1000)
                                ) + 1;
                            } catch (Exception e) {
                                Log.d("my_error_date", e.getMessage());
                            }
                            Log.d("my_numberOfWeeks", numberOfWeeks + "");
                            DataPoint allWeekDataPoints[][] = new DataPoint[numberOfWeeks][values.length];
                            int counter[] = new int[numberOfWeeks];

                            for (int i = 0; i < numberOfWeeks; i++)
                                counter[i] = 0;

                            for (int i = 0; i < values.length; i += 2) {
                                //Log.d("my_weekly_201", "yes");
                                calendar.setTimeInMillis(Long.parseLong(values[i]) * 1000);
                                makeThisMonday(calendar);
                                int weekIndex = (int) (
                                        ((calendar.getTimeInMillis()) - (startWeek.getTimeInMillis())) / (7 * 24 * 60 * 60 * 1000)
                                );
                                //calendar.get(Calendar.WEEK_OF_YEAR) - startWeek.get(Calendar.WEEK_OF_YEAR);
                                allWeekDataPoints
                                        [weekIndex]
                                        [counter[weekIndex]] =
                                        new DataPoint(counter[weekIndex],
                                                Long.parseLong(values[i + 1]));
                                counter[weekIndex]++;
                                //Log.d("my_test_date", "y:" + calendar.get(Calendar.YEAR) + "m:" + calendar.get(Calendar.MONTH) + "d:" + calendar.get(Calendar.DAY_OF_MONTH));
                            }
                            for (int i = 0; i < numberOfWeeks; i++) {

                                GraphView gv = new GraphView(getApplicationContext());
                                int numberOfDataPoints = 0;
                                for (int j = 0; j < allWeekDataPoints[i].length; j++) {
                                    if (allWeekDataPoints[i][j] != null)
                                        numberOfDataPoints++;
                                }
                                DataPoint dataPoints[] = new DataPoint[numberOfDataPoints];
                                for (int j = 0; j < dataPoints.length; j++) {
                                    dataPoints[j] = allWeekDataPoints[i][j];
                                }
                                LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);
                                gv.getViewport().setMinY(0);
                                gv.getGridLabelRenderer().setVerticalAxisTitleTextSize(30);
                                gv.getGridLabelRenderer().setHorizontalAxisTitleTextSize(30);
                                gv.getGridLabelRenderer().setVerticalAxisTitleColor(0xff000000);
                                gv.getGridLabelRenderer().setHorizontalAxisTitleColor(0xff000000);
                                gv.getGridLabelRenderer().setVerticalAxisTitle("Time in Seconds");
                                gv.getGridLabelRenderer().setHorizontalAxisTitle("Trial Number");
                                if (game.equals("outline") || game.equals("match")) {
                                    gv.getViewport().setMaxY(800);
                                    gv.getViewport().setYAxisBoundsManual(true);
                                    gv.getGridLabelRenderer().setVerticalAxisTitle("Score");
                                    gv.getGridLabelRenderer().setHorizontalAxisTitle("Trial Number");
                                }
                                calendar.setTimeInMillis(startWeek.getTimeInMillis() + (i * 7 * 24 * 60 * 60 * 1000));
                                String title = titleAsFromTo(calendar);
                                gv.addSeries(series);
                                gv.getGridLabelRenderer().setGridColor(0xff000000);
                                gv.getGridLabelRenderer().setHorizontalLabelsColor(0xff000000);
                                gv.getGridLabelRenderer().setVerticalLabelsColor(0xff000000);
                                gv.getViewport().setMaxX(dataPoints.length);
                                gv.getViewport().setXAxisBoundsManual(true);
                                gv.setTitle(title);
                                gv.setTitleColor(0xff000000);
                                gv.setTitleTextSize(40);
                                gv.getViewport().setScalable(true);
                                //priority 1 need to make it scrollable
                                try {
                                    rootView.addView(gv, LinearLayout.LayoutParams.MATCH_PARENT, 500);
                                } catch (Exception e) {
                                    Log.d("my_weekly_300", e.toString());
                                }
                            }
                        } catch (Exception e) {
                            Log.d("my_error", e.getMessage());
                        }
                    }
                }

                , new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("my_error_102", error.getMessage());
            }
        }

        );
        queue.add(stringRequest);
    }

    private void mode_entireProgressWeekWise() {
        setContentView(R.layout.activity_progress);
        ((TextView) findViewById(R.id.progress_activity_user_name)).setText((CharSequence)
                getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE).getString(Config.USERNAME_SHARED_PREF, "Not Available")
        );
        String legend = (String) ((TextView) findViewById(R.id.progress_activity_legend)).getText();
        legend += "\n4)Week score is calculated by averaging scores for all attempts over the entire week.";
        ((TextView) findViewById(R.id.progress_activity_legend)).setText(legend);
        //final GraphView lineGraph1 = (GraphView) findViewById(R.id.line_graph1);
        final GraphView lineGraph2 = (GraphView) findViewById(R.id.line_graph2);
        final GraphView lineGraph3 = (GraphView) findViewById(R.id.line_graph3);
        //lineGraph1.setTitleTextSize(40);
        lineGraph2.setTitleTextSize(40);
        lineGraph3.setTitleTextSize(40);
        //lineGraph1.getGridLabelRenderer().setHorizontalAxisTitle("Trials Number");
        //lineGraph1.getGridLabelRenderer().setVerticalAxisTitle("Time in Seconds");
        //lineGraph1.getGridLabelRenderer().setHorizontalAxisTitleTextSize(30);
        //lineGraph1.getGridLabelRenderer().setVerticalAxisTitleTextSize(30);
        lineGraph2.getGridLabelRenderer().setHorizontalAxisTitle("Week number");
        lineGraph2.getGridLabelRenderer().setVerticalAxisTitle("Score");
        lineGraph2.getGridLabelRenderer().setHorizontalAxisTitleTextSize(30);
        lineGraph2.getGridLabelRenderer().setVerticalAxisTitleTextSize(30);
        lineGraph3.getGridLabelRenderer().setHorizontalAxisTitle("Week number");
        lineGraph3.getGridLabelRenderer().setVerticalAxisTitle("Score");
        lineGraph3.getGridLabelRenderer().setHorizontalAxisTitleTextSize(30);
        lineGraph3.getGridLabelRenderer().setVerticalAxisTitleTextSize(30);


        //displaying the user name
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String user_id = sharedPreferences.getString(Config.USERNAME_SHARED_PREF, "");

        //networking
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = MyUtilities.WEBSITE_NAME + "get_progress_weekwise.php?user_id=" + user_id + "&game=";

        // Request a string response from the provided URL.

        //this for the learn game
        /*StringRequest stringRequest1 = new StringRequest(Request.Method.GET, url + "learn",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String values[] = response.split(" ");
                            DataPoint dataPoints[] = new DataPoint[values.length / 2];
                            for (int i = 0; i < values.length; i += 2) {
                                dataPoints[i / 2] = new DataPoint((i) / 2, Integer.parseInt(values[i + 1]));
                            }
                            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);
                            lineGraph1.addSeries(series);
                            lineGraph1.setTitle("Learn");
                            lineGraph1.getViewport().setScalable(true);
                            lineGraph1.getViewport().setMinY(0);
                            lineGraph1.getViewport().setYAxisBoundsManual(true);
                        } catch (Exception e) {
                            Log.d("my_error", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("my_error_102", error.getMessage());
            }
        });*/
        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url + "match",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            lineGraph2.setTitle("Match");
                            String values[] = response.split(" ");
                            DataPoint dataPoints[] = new DataPoint[values.length / 2];

                            for (int i = 0; i < values.length; i += 2) {
                                dataPoints[i / 2] = new DataPoint(i / 2, Integer.parseInt(values[i + 1]));
                            }
                            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);
                            lineGraph2.addSeries(series);
                            lineGraph2.getViewport().setScalable(true);
                            lineGraph2.getViewport().setMinY(0);
                            lineGraph2.getViewport().setMaxY(800);
                            lineGraph2.getViewport().setYAxisBoundsManual(true);
                        } catch (Exception e) {
                            Log.d("my_error", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("my_error_102", error.getMessage());
            }
        });
        StringRequest stringRequest3 = new StringRequest(Request.Method.GET, url + "outline",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String values[] = response.split(" ");
                            DataPoint dataPoints[] = new DataPoint[values.length / 2];
                            for (int i = 0; i < values.length; i += 2) {
                                dataPoints[i / 2] = new DataPoint(i / 2, Integer.parseInt(values[i + 1]));
                            }
                            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);
                            lineGraph3.addSeries(series);
                            lineGraph3.setTitle("Matching Outline");
                            lineGraph3.getViewport().setScalable(true);
                            lineGraph3.getViewport().setMinY(0);
                            lineGraph3.getViewport().setMaxY(800);
                            lineGraph3.getViewport().setYAxisBoundsManual(true);
                        } catch (Exception e) {
                            Log.d("my_error", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("my_error_102", error.getMessage());
            }
        });

// Add the request to the RequestQueue.
        //queue.add(stringRequest1);
        queue.add(stringRequest2);
        queue.add(stringRequest3);

        //end
    }

    String titleAsFromTo(Calendar calendar) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(calendar.getTimeInMillis());
        String from = "";
        String to = "";
        makeThisMonday(c);
        from += c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + (c.get(Calendar.YEAR));
        makeThisSunday(c);
        to += c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + (c.get(Calendar.YEAR));
        return from + " - " + to;
    }

    static void makeThisMonday(Calendar cal) {
        System.out.println("day of weeek: " + cal.get(Calendar.DAY_OF_WEEK));
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek >= 2)
            cal.setTimeInMillis(cal.getTimeInMillis() - (dayOfWeek - 2) * 24 * 60 * 60 * 1000);
        else
            cal.setTimeInMillis(cal.getTimeInMillis() - (6) * 24 * 60 * 60 * 1000);
    }

    static void makeThisSunday(Calendar cal) {
        System.out.println("day of weeek: " + cal.get(Calendar.DAY_OF_WEEK));
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek >= 2)
            cal.setTimeInMillis(cal.getTimeInMillis() + (7 - dayOfWeek + 1) * 24 * 60 * 60 * 1000);
        else
            cal.setTimeInMillis(cal.getTimeInMillis() + (0) * 24 * 60 * 60 * 1000);
    }
}
