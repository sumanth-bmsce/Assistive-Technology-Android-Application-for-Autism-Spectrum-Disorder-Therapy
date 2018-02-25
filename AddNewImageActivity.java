package com.samarth261.asd;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class AddNewImageActivity extends AppCompatActivity implements MyNewImageDialog.MyDialogListener {

    Button addNewImageDialogButton;
    DialogFragment myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_image);
        addNewImageDialogButton = (Button) findViewById(R.id.addNewImageDialogButton);
        addNewImageDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDialog();
            }
        });
    }

    public void startDialog() {
        myDialog = new MyNewImageDialog("/sdcard/ASD/Category");
        myDialog.show(getSupportFragmentManager(), "get_image");
    }

    @Override
    public void sendDataFromMyDialog(String category, String subCategory, boolean newCategory, boolean newSubcategory, Bitmap bmp, Bitmap thumb) {
        //Toast.makeText(this, category + "\n" + subCategory + "\n" + ((newCategory) ? "new" : "old") + "\n" + ((newSubcategory) ? "new" : "old"), Toast.LENGTH_LONG).show();
        int num;
        FileOutputStream fos = null;
        if (newSubcategory)
            num = 1;
        else {
            num = MyUtilities.numberOfFilesMatching("/sdcard/ASD/Category/" + category + "/Items/" + subCategory, "img[0-9]+\\.png");
            num++;
        }
        if (newCategory) {
            File temp = new File("/sdcard/ASD/Category/" + category);
            temp.mkdir();
            try {
                fos = new FileOutputStream("/sdcard/ASD/Category/" + category + "/Thumb.png");
                thumb.compress(Bitmap.CompressFormat.PNG, 100, fos);
                Toast.makeText(this, fos.toString(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, "Saving Error", Toast.LENGTH_LONG).show();
            } finally {
                try {
                    fos.close();
                } catch (Exception e) {
                    Toast.makeText(this, "Saving Error", Toast.LENGTH_LONG).show();
                }
            }
        }
        if (newSubcategory) {
            File temp = new File("/sdcard/ASD/Category/" + category+"/Items/"+subCategory);
            temp.mkdirs();
            try {
                fos = new FileOutputStream("/sdcard/ASD/Category/" + category + "/Items/" + subCategory + "/Thumb.png");
                bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            } catch (Exception e) {
                Toast.makeText(this, "Saving Error", Toast.LENGTH_LONG).show();
            } finally {
                try {
                    fos.close();
                } catch (Exception e) {
                    Toast.makeText(this, "Saving Error", Toast.LENGTH_LONG).show();
                }
            }
        }


        try {
            fos = new FileOutputStream("/sdcard/ASD/Category/" + category + "/Items/" + subCategory + "/img" + num + ".png");
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            Toast.makeText(this, "Saving Error", Toast.LENGTH_LONG).show();
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
                Toast.makeText(this, "Saving Error", Toast.LENGTH_LONG).show();
            }
        }


    }

}
