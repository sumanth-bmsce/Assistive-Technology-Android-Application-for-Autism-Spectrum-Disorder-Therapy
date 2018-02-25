package com.samarth261.asd;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class select_progress_mode extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_progress_mode);

        findViewById(R.id.progress_mode_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProgressActivity.class);
                intent.putExtra("mode", ProgressActivity.MODE_ALL);
                startActivity(intent);
            }
        });
        /*findViewById(R.id.progress_mode_weekly_learn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProgressActivity.class);
                intent.putExtra("mode", ProgressActivity.MODE_WEEKLY);
                intent.putExtra("game", "learn");
                startActivity(intent);
            }
        });*/
        /*findViewById(R.id.progress_mode_weekly_match).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProgressActivity.class);
                intent.putExtra("mode", ProgressActivity.MODE_WEEKLY);
                intent.putExtra("game", "match");
                startActivity(intent);
            }
        });
        findViewById(R.id.progress_mode_weekly_outline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProgressActivity.class);
                intent.putExtra("mode", ProgressActivity.MODE_WEEKLY);
                intent.putExtra("game", "outline");
                startActivity(intent);
            }
        });
        findViewById(R.id.progress_mode_weekwise).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProgressActivity.class);
                intent.putExtra("mode", ProgressActivity.MODE_OVERALL_WEEKWISE);
                startActivity(intent);
            }
        });
        String s = "Progress report for ";
        String userId = getSharedPreferences(Config.SHARED_PREF_NAME, MODE_PRIVATE).getString(Config.USERNAME_SHARED_PREF, "");
        s += userId;
        ((TextView) findViewById(R.id.select_progress_textview1)).setText(s);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Config.GRADE_URL + "?user_id=" + userId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //if (response.length() < 10)//limiting the response size to 10 characters
                    ((TextView) findViewById(R.id.select_progress_activity_gradetv)).setText("Grade : " + response);
                Log.d("my_grade_test", "came back");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("my_grade_test", "volley arreor");
            }
        });
        requestQueue.add(stringRequest);*/
    }
}
