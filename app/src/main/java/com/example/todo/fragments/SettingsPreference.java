package com.example.todo.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.todo.R;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

public class SettingsPreference extends PreferenceFragment {
    private Preference colorPickerPreference;

    private final static String TAG = "SettingsPreference";

    private Context context;
    private SharedPreferences sharedPreferences;

    public SettingsPreference(){

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.user_settings);

        colorPickerPreference = findPreference("color_picker_key");
        final ColorPicker colorPicker = new ColorPicker(getActivity(), 0, 0, 0, 0);
        sharedPreferences = context.getSharedPreferences("UserSettings_colorPicker", 0);
        colorPicker.setAlpha(sharedPreferences.getInt("color_a", 0));
        colorPicker.setRed(sharedPreferences.getInt("color_r", 0));
        colorPicker.setGreen(sharedPreferences.getInt("color_g", 0));
        colorPicker.setBlue(sharedPreferences.getInt("color_b", 0));
        colorPickerPreference.setOnPreferenceClickListener(preference -> {
            colorPicker.setTitle("Select color");
            colorPicker.show();

            colorPicker.setCallback(color -> {
                SharedPreferences.Editor editor = sharedPreferences.edit();

                int colorA = Color.alpha(color);
                int colorR = Color.red(color);
                int colorG = Color.green(color);
                int colorB = Color.blue(color);

                editor.putInt("color_a", colorA);
                editor.putInt("color_r", colorR);
                editor.putInt("color_g", colorG);
                editor.putInt("color_b", colorB);
                editor.apply();

                colorPicker.cancel();
            });

            return true;
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
