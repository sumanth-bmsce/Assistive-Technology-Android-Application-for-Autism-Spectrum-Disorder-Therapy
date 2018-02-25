package com.samarth261.asd;

import android.animation.ValueAnimator;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class MainActivity extends AppCompatActivity {
    String parentFilePath = MyUtilities.ASD_FOLDER_PATH+"ASD/Others/ActivityWise/MainActivity";
    RelativeLayout rl;
    Dimes dimes;

    public class Dimes {
        private int screenWidth;
        private int screenHeight;
        private int imgHeigth;//will be calculated
        private int imgWidth;//considered to be half of the view width
        private int distanceFromTop;//in this case it is considered to be same as the height of the img
        private int distanceFromLeft;//considered to be 1/4 th os the parent layout
        private int distanceBetweenImgs;//in this case this is taken to be 1/3 of the img height

        public Dimes(int width, int height) {
            screenHeight = height;
            screenWidth = width;
            distanceFromLeft = (int) (screenWidth / 6.0);
            imgWidth = (int) (screenWidth * (2 / 3.0));
            imgHeigth = (int) ((3 / 14.0) * screenHeight);
            distanceFromTop = (int) (imgHeigth / 2.0);
            distanceBetweenImgs = (int) (imgHeigth / 3.0);
            //Toast.makeText(getApplicationContext(),screenWidth+"x"+screenHeight,Toast.LENGTH_LONG).show();
        }

        public int getImgHeigth() {
            return imgHeigth;
        }

        public int getImgWidth() {
            return imgWidth;
        }

        public int getDistanceBetweenImgs() {
            return distanceBetweenImgs;
        }

        public int getDistanceFromLeft() {
            return distanceFromLeft;
        }

        public int getDistanceFromTop() {
            return distanceFromTop;
        }

        public int getScreenWidth() {
            return screenWidth;
        }

        public int getScreenHeight() {
            return screenHeight;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("main", Context.MODE_PRIVATE);
        setContentView(R.layout.initial_activity);
        //Log.d("emergency",sharedPreferences.toString());
        File f = new File(MyUtilities.ASD_FOLDER_PATH+"ASD/");
        if (/*sharedPreferences.contains("firstTime") == false && */f.exists() == false) {
            //Log.d("emergency","inside");
            //
            final DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            //Uri uri=Uri.parse("https://drive.google.com/uc?export=download&id=0B4PTdzap-Y06V3NMeHVlZmFkLWs");
            Uri uri = Uri.parse("https://drive.google.com/uc?export=download&id=0B4PTdzap-Y06aHQyRzAzdFRmZms");
            final DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "ASD.zip");
            request.setDescription("Essential files are being downloaded");
            request.setTitle("Downloading");
            View.OnClickListener myListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Toast.makeText(getApplicationContext(), "downloading", Toast.LENGTH_LONG).show();
                        downloadManager.enqueue(request);
                    } catch (Exception e) {
                        Log.e("error", e.getMessage());
                    }
                }
            };
            findViewById(R.id.downloadBtn).setOnClickListener(myListener);
            /*try {
                downloadManager.enqueue(request);
            } catch (Exception e) {
                Log.e("error", e.getMessage());
            }*/
            BroadcastReceiver receiver = new BroadcastReceiver() {
                String destinationParent = "/sdcard/";

                @Override
                public void onReceive(Context context, Intent intent) {
                    Toast.makeText(context, "unzipping", Toast.LENGTH_LONG).show();
                    File folder = new File(destinationParent + "/ASD/");
                    folder.mkdirs();
                    File zip = new File("/sdcard/" + Environment.DIRECTORY_DOWNLOADS + "/ASD.zip");
                    unZipAndStore(zip, folder);
                }

                public void unZipAndStore(File zip, File folder) {
                    ZipFile zipFile = null;
                    ZipEntry zipEntry = null;
                    InputStream is = null;
                    FileOutputStream fos = null;
                    byte b[] = new byte[1000];
                    try {
                        zipFile = new ZipFile(zip);
                    } catch (Exception e) {
                    }
                    Enumeration entries = zipFile.entries();
                    while (entries.hasMoreElements()) {
                        zipEntry = zipFile.getEntry(((ZipEntry) entries.nextElement()).getName());
                        if (zipEntry.isDirectory()) {
                            File tempFile = new File(folder.getPath() + "/" + zipEntry.getName());
                            tempFile.mkdirs();
                        } else {
                            try {
                                is = zipFile.getInputStream(zipEntry);
                            } catch (Exception e) {
                                Log.d("myerror", e.toString());
                            }
                            try {
                                fos = new FileOutputStream(new File(destinationParent + "ASD/" + zipEntry.getName()));
                                Log.d("filepath", fos.toString() + " -- " + destinationParent + "ASD/" + zipEntry.getName());
                            } catch (Exception e) {
                                Log.d("myerror", e.toString());
                            }
                            int n = 0;
                            try {
                                while ((n = is.read(b)) > 0) {
                                    fos.write(b, 0, n);
                                }
                            } catch (Exception e) {
                                Log.d("myerror", e.toString());
                            }
                        }
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
            registerReceiver(receiver, intentFilter);
            //********************************************************
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("firstTime", false);
            editor.commit();
            //finish();
        } else {
            setContentView(R.layout.activity_main);
            rl = (RelativeLayout) findViewById(R.id.MainActivityRelativeLayout);
            //rl.setBackground(Drawable.createFromPath(parentFilePath + "/img.jpg"));
            findViewById(R.id.MainActivityRelativeLayoutroot).setBackground(Drawable.createFromPath(parentFilePath + "/img.jpg"));
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onResumecustom();
                    // Toast.makeText(getApplicationContext(),"asdfg",Toast.LENGTH_LONG).show();
                }

            }, 250);
        }
    }


    protected void onResumecustom() {
        rl.removeAllViews();
        dimes = new Dimes(rl.getWidth(), rl.getHeight());
        //Toast.makeText(getApplicationContext(),""+getSupportActionBar().getHeight(),Toast.LENGTH_LONG).show();
        for (int i = 1; i <= 4; i++) {
            ImageButton img = new ImageButton(getApplicationContext());
            img.setBackground(Drawable.createFromPath(parentFilePath + "/Level" + i + ".png"));
            //Log.d("16june",parentFilePath + "/Level" + i + ".png");
            img.setClickable(true);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dimes.getImgWidth(), dimes.getImgHeigth());
            int final_y = (i - 1) * (dimes.getImgHeigth() + dimes.getDistanceBetweenImgs()) + dimes.getDistanceFromTop();
            params.setMargins(dimes.getDistanceFromLeft(), dimes.getScreenHeight(), 0, 0);
            rl.addView(img, params);
            img.setTag("" + i);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("Level", Integer.parseInt((String) v.getTag()));
                    intent.setClass(getApplicationContext(), Choose.class);
                    startActivity(intent);
                }
            });
            final String tempMessage;
            final String tempTitle;
            switch (i) {
                case 2:
                    tempTitle = "Level 2";
                    tempMessage = "In this level kids can learn the names of various objects they see around them";
                    break;
                case 3:
                    tempTitle = "Level 3";
                    tempMessage = "In this level kids get to select the right option from 3 choices";
                    break;
                case 4:
                    tempTitle = "Level 4";
                    tempMessage = "In this level kids get to match objects based on the outline";
                    break;
                case 1:
                    tempTitle = "Level 1";
                    tempMessage = "In the level kids get to fill in silhouettes of objects";
                    break;
                default:
                    tempTitle = "Error";
                    tempMessage = "Some unknown error occurred ";
            }
            img.setOnLongClickListener(new View.OnLongClickListener() {
                                           @Override
                                           public boolean onLongClick(View v) {
                                               MyDialogue myDialogue = new MyDialogue(tempTitle, tempMessage);
                                               myDialogue.show(getSupportFragmentManager(), tempTitle);
                                               return false;
                                           }
                                       }
            );
            customAnimation(img, final_y, -i * (150), i * 500);
            customVibrateAnimation(img, -i * (150 - 500));
        }


    }

    void customAnimation(final ImageButton img, int y, int extraTime, int startDelay) {
        final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) (img.getLayoutParams());
        final ValueAnimator obj = new ValueAnimator();
        obj.setStartDelay(startDelay);
        obj.setDuration(1500 + extraTime);//+extraTime will actually result in reduction of time
        obj.setIntValues(dimes.getScreenHeight(), y);
        obj.setInterpolator(new CustomInterpolator());

        obj.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                params.setMargins(params.leftMargin, Integer.parseInt((obj.getAnimatedValue()).toString()), 0, 0);
                img.setLayoutParams(params);
            }
        });
        obj.start();
    }

    void customVibrateAnimation(final ImageButton img, int extraTime) {
        img.setPivotX(dimes.getImgWidth() / 2);
        img.setPivotY(dimes.getImgHeigth() / 2);
        final ValueAnimator obj = new ValueAnimator();
        obj.setStartDelay(1500 + extraTime);
        obj.setDuration(250);
        obj.setFloatValues(0, 4);
        obj.setInterpolator(new CustomVibrateInterpolator());
        obj.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                img.setRotation(Float.parseFloat((obj.getAnimatedValue()).toString()));
            }
        });

        obj.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_activity_menu, menu);
        menu.getItem(0).setActionView(new Switch(this));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.mainActivitMenuAddImage:
                intent = new Intent();
                intent.setClass(this, AddNewImageActivity.class);
                startActivity(intent);
                return true;
            case R.id.mainActivityMenuProgress:
                intent = new Intent();
                intent.putExtra("mode", ProgressActivity.MODE_ALL);
                intent.setClass(this, ProgressActivity.class);
                startActivity(intent);
                return true;
            case R.id.mainActivitMenuLogout:
                logout();
                return true;
            case R.id.mainActivitMenuSettings:
                intent = new Intent();
                intent.setClass(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

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
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
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
    public void onBackPressed() {
        finishAffinity();
        finish();
    }
}
