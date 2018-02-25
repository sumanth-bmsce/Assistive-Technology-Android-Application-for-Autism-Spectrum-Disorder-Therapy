package com.samarth261.asd;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

/**
 * Created by Samarth on 17-07-2016.
 */

public class MyDialogue extends DialogFragment {
    String Title;
    String Message;

    public MyDialogue() {
        Title = getArguments().getString("Title");
        Message = getArguments().getString("Message");
    }

    public MyDialogue(String t, String m) {
        Title = t;
        Message = m;
        Bundle b = new Bundle();
        b.putString("Title", t);
        b.putString("Message", m);
        setArguments(b);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder myBuilder = new AlertDialog.Builder(getContext());
        myBuilder.setTitle(Title);
        myBuilder.setMessage(Message);
        myBuilder.setPositiveButton("OK", null);
        return myBuilder.create();
    }
}
