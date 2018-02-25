package com.samarth261.asd;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Samarth on 24-07-2016.
 */

public class MyNewImageDialog extends DialogFragment implements AdapterView.OnItemSelectedListener {

    public MyNewImageDialog() {
    }

    public MyNewImageDialog(String a) {
        Bundle b = new Bundle();
        b.putString("path", a);
        setArguments(b);
    }

    public interface MyDialogListener {
        public void sendDataFromMyDialog(String category, String subCategory, boolean newCategory, boolean newSubcategory, Bitmap bmp, Bitmap thumb);
    }

    final int CLICK_A_PICTURE_CODE = 100;
    final int CROP_THE_PICTURE_CODE = 200;
    final int NEW_PICTURE_VIEW=1;
    final int NEW_THUMB_VIEW=2;

    String path;
    String selectedCategoryPath;
    String categoryList[], subCategoryList[];
    Spinner categorySpinner, subCategorySpinner;
    View v;
    ArrayAdapter spinnerCategoryAdapter, spinnerSubCategoryAdapter;
    File categoryFolder, ItemsFolder;
    EditText categoryEditText, subCategoryEditText;
    LinearLayout categoryLinearLayout, subCategoryLinearLayout, imageLayout;
    ImageButton newPicture, newThumb;
    Activity parentActivity;
    Uri fileUri;
    Bitmap bmp, thmb;
    boolean newCategory, newSubCategory;
    int viewNumber;


    @Override
    public void onAttach(Activity a) {
        super.onAttach(a);
        parentActivity = a;
    }

    @Override
    public Dialog onCreateDialog(Bundle SavedInstanceState) {
        path = getArguments().getString("path");
        newCategory = false;
        newSubCategory = false;
        categoryFolder = new File(path);
        categoryList = categoryFolder.list();
        categoryList = addEle(categoryList, "New Category");
        AlertDialog.Builder myDialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        v = inflater.inflate(R.layout.my_new_image_dialog_layout, null);
        categorySpinner = (Spinner) v.findViewById(R.id.dialogSpinner1);
        subCategorySpinner = (Spinner) v.findViewById(R.id.dialogSpinner2);
        categoryLinearLayout = (LinearLayout) v.findViewById(R.id.dialogLinearLayout1);
        subCategoryLinearLayout = (LinearLayout) v.findViewById(R.id.dialogLinearLayout2);
        newPicture = (ImageButton) v.findViewById(R.id.dialogImage);
        imageLayout = (LinearLayout) v.findViewById(R.id.dialogImagesLinearLayout);
        newPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPicture(NEW_PICTURE_VIEW);
            }
        });
        spinnerCategoryAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, categoryList);
        categorySpinner.setAdapter(spinnerCategoryAdapter);
        categorySpinner.setOnItemSelectedListener(this);
        subCategorySpinner.setOnItemSelectedListener(this);
        myDialogBuilder.setTitle("ADD NEW IMAGE");
        myDialogBuilder.setView(v);
        myDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendData();
            }
        });
        AlertDialog alertDialog = myDialogBuilder.create();
        return alertDialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        Dialog dialog = getDialog();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public void onItemSelected(AdapterView adapterView, View v, int pos, long id) {
        // parentActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        if (adapterView.getId() == categorySpinner.getId()) {
            Toast.makeText(getContext(), ((TextView) v).getText(), Toast.LENGTH_LONG).show();
            try {
                categoryLinearLayout.removeView(categoryEditText);
            } catch (Exception e) {

            }
            try {
                subCategoryLinearLayout.removeView(subCategoryEditText);
            } catch (Exception e) {
                Toast.makeText(getContext(), "couldn't remove", Toast.LENGTH_LONG).show();
            }
            if (((TextView) v).getText() != "New Category") {
                newCategory = false;
                selectedCategoryPath = path + "/" + ((TextView) v).getText() + "/Items";
                ItemsFolder = new File(selectedCategoryPath);
                subCategoryList = ItemsFolder.list();
                subCategoryList = addEle(subCategoryList, "New Sub-Category");
                try {
                    imageLayout.removeView(newThumb);
                } catch (Exception e) {

                }

            } else {
                newCategory = true;
                subCategoryList = new String[0];
                subCategoryList = addEle(subCategoryList, "New Sub-Category");
                categoryEditText = new EditText(getContext());
                categoryEditText.setHint("New Categor's Name");
                categoryLinearLayout.addView(categoryEditText, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                newThumb = new ImageButton(getContext());
                newThumb.setImageResource(R.drawable.add_thumb_here);
                newThumb.setScaleType(newPicture.getScaleType());
                imageLayout.addView(newThumb, new LinearLayout.LayoutParams(newPicture.getLayoutParams()));
                newThumb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getPicture(NEW_THUMB_VIEW);
                    }
                });
            }
            spinnerSubCategoryAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, subCategoryList);
            subCategorySpinner.setAdapter(spinnerSubCategoryAdapter);
        }
        if (adapterView.getId() == subCategorySpinner.getId()) {
            if (((TextView) v).getText() == "New Sub-Category") {
                newSubCategory = true;
                subCategoryEditText = new EditText(getContext());
                subCategoryEditText.setHint("New Sub-Categor's Name");
                subCategoryLinearLayout.addView(subCategoryEditText, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            } else {
                newSubCategory = false;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public String[] addEle(String old[], String newEle) {
        String temp[] = new String[old.length + 1];
        for (int i = 0; i < old.length; i++)
            temp[i] = old[i];
        temp[temp.length - 1] = newEle;
        return temp;
    }


    public void getPicture(int viewNum) {
        viewNumber = viewNum;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = Uri.fromFile(new File(MyUtilities.ASD_FOLDER_PATH+"ASD/Others/ActivityWise/AddNewImageActivity", "NewImage.png"));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CLICK_A_PICTURE_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CLICK_A_PICTURE_CODE) {
            if (parentActivity.RESULT_OK == resultCode) {
                Toast.makeText(getContext(), "saved at", Toast.LENGTH_LONG).show();
                cropMethod();
            }
        } else if (requestCode == CROP_THE_PICTURE_CODE) {
            if (parentActivity.RESULT_OK == resultCode) {
                Bundle temp = data.getExtras();
                if (viewNumber==NEW_THUMB_VIEW) {
                    thmb = temp.getParcelable("data");
                    newThumb.setImageBitmap(thmb);
                } else {
                    bmp = temp.getParcelable("data");
                    newPicture.setImageBitmap(bmp);
                }
                Toast.makeText(getContext(), "croped", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void cropMethod() {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(Uri.fromFile(new File(MyUtilities.ASD_FOLDER_PATH+"ASD/Others/ActivityWise/AddNewImageActivity", "NewImage.png")), "image/*");
        cropIntent.putExtra("crop", "true");
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("outputX", 300);
        cropIntent.putExtra("outputY", 300);
        cropIntent.putExtra("return-data", true);
        startActivityForResult(cropIntent, CROP_THE_PICTURE_CODE);
    }

    public void sendData() {
        String catName, subCatName;
        if (newCategory)
            catName = categoryEditText.getText().toString();
        else
            catName = categorySpinner.getSelectedItem().toString();
        if (newSubCategory)
            subCatName = subCategoryEditText.getText().toString();
        else
            subCatName = subCategorySpinner.getSelectedItem().toString();
        ((MyDialogListener) parentActivity).sendDataFromMyDialog(catName, subCatName, newCategory, newSubCategory, bmp, thmb);
    }

}
