package com.samarth261.asd;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import static com.samarth261.asd.MyUtilities.OUTLINE_GAME_MODE;
import static com.samarth261.asd.MyUtilities.OUTLINE_GAME_MODE_DND;
import static com.samarth261.asd.MyUtilities.OUTLINE_GAME_MODE_PINCH;
import static com.samarth261.asd.MyUtilities.SETTINGS_SHARED_PREFERENCES;

public class SettingsActivity extends AppCompatActivity {
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        SharedPreferences sharedPreferences = getSharedPreferences(SETTINGS_SHARED_PREFERENCES, MODE_PRIVATE);
        try {
            String mode = sharedPreferences.getString(OUTLINE_GAME_MODE, "");
            if (mode.equals(OUTLINE_GAME_MODE_PINCH)) {
                ((RadioButton) findViewById(R.id.settingsPinch)).setChecked(true);
            } else if (mode.equals(OUTLINE_GAME_MODE_DND)) {
                ((RadioButton) findViewById(R.id.settingDnd)).setChecked(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        editor = sharedPreferences.edit();
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.settingRadioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.settingsPinch:
                        editor.putString(OUTLINE_GAME_MODE, OUTLINE_GAME_MODE_PINCH);
                        editor.commit();
                        Log.d("settings", "pinch");
                        break;
                    case R.id.settingDnd:
                        editor.putString(OUTLINE_GAME_MODE, OUTLINE_GAME_MODE_DND);
                        editor.commit();
                        Log.d("settings", "dnd");
                        break;
                    default:
                        break;
                }
            }
        });
    }
}
