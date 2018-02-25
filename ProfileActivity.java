package com.samarth261.asd;

/**
 * Created by Samarth on 25-01-2017.
 */

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    //Textview to show currently logged in user
    private TextView textView;
    String path = MyUtilities.ASD_FOLDER_PATH+"";
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Initializing textview
        textView = (TextView) findViewById(R.id.textView);
        img = (ImageView) findViewById(R.id.imageView3);

        //Fetching email from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(Config.USERNAME_SHARED_PREF, "Not Available");
        //getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE).getString(Config.USERNAME_SHARED_PREF,"Not Available");
        //Showing the current logged in email to textview
        textView.setText("Current User: " + username);
        img.setBackground(Drawable.createFromPath(path + "/" + username + ".jpg"));

    }

    //Logout function
    private void logout() {
        //Creating an alert dialog to confirm logout
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to logout?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        //Getting out sharedpreferences
                        SharedPreferences preferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                        //Getting editor
                        SharedPreferences.Editor editor = preferences.edit();

                        //Puting the value false for loggedin
                        editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, false);

                        //Putting blank value to email
                        editor.putString(Config.USERNAME_SHARED_PREF, "");

                        //Saving the sharedpreferences
                        editor.commit();

                        //Starting login activity
                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                });

        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        //Showing the alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Adding our menu to toolbar
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.profile_menu_logout) {
            //calling logout method when the logout button is clicked
            logout();
        } else if (id == R.id.profile_menu_progress) {
            //Intent intent = new Intent(this, select_progress_mode.class);
            //intent.putExtra("mode" , "all");
            //startActivity(intent);
            Intent intent = new Intent(getApplicationContext(), ProgressActivity.class);
            intent.putExtra("mode", ProgressActivity.MODE_ALL);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        finish();
    }

    public void continueToApp(View v) {
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.profileActivityRoot);
        SharedPreferences sharedPreferences = getSharedPreferences(MyUtilities.SHARED_PREFERENCES_DIMENSIONS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(MyUtilities.DIMENSIONS_WIDTH, rl.getWidth());
        editor.putInt(MyUtilities.DIMENSIONS_HEIGHT, rl.getHeight());
        editor.commit();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}