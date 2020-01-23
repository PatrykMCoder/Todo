package com.example.todo.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.todo.R;
import com.example.todo.utils.setteings.Settings;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

import java.util.ArrayList;
import java.util.prefs.Preferences;

public class SettingsPreference extends PreferenceFragment {

    private Preference colorPickerPreference;
    private ListPreference sortTodoPreference;
    private CheckBoxPreference securityFingerprintPreference;

    private Settings settings;

    private final static String TAG = "SettingsPreference";

    private Context context;
    private SharedPreferences sharedPreferences;

    public SettingsPreference(){

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        settings = new Settings(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.user_settings);

        ArrayList<Integer> colors = settings.loadBackgroundColor();

        colorPickerPreference = findPreference("color_picker_key");
        sortTodoPreference = (ListPreference) getPreferenceManager().findPreference("sort_todo_key");
        securityFingerprintPreference = (CheckBoxPreference)  getPreferenceManager().findPreference("fingerprint_security_app");

        securityFingerprintPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                Log.d(TAG, "onPreferenceChange: object: " + o);
                settings.setSecurityFingerprint(o);
                return true;
            }
        });

        final ColorPicker colorPicker = new ColorPicker(getActivity(), 0, 0, 0, 0);

        colorPicker.setAlpha(colors.get(0));
        colorPicker.setRed(colors.get(1));
        colorPicker.setGreen(colors.get(2));
        colorPicker.setBlue(colors.get(3));

        colorPickerPreference.setOnPreferenceClickListener(preference -> {
            colorPicker.show();

            colorPicker.setCallback(color -> {
                int colorA = Color.alpha(color);
                int colorR = Color.red(color);
                int colorG = Color.green(color);
                int colorB = Color.blue(color);

                settings.saveBackgroundColor(colorA, colorR, colorG, colorB);
                Toast.makeText(context, settings.message(), Toast.LENGTH_SHORT).show();
                colorPicker.cancel();
            });

            return true;
        });

        sortTodoPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            Log.d(TAG, "onPreferenceChange: sortTodoPreference: " + sortTodoPreference.getValue());
            Log.d(TAG, "onPreferenceChange: new value: " + newValue);
            settings.saveSortTodo(newValue.toString());

            if(newValue.equals("Date reaming")){
                Toast.makeText(context, "This can bug :C I'm still working about this :)", Toast.LENGTH_SHORT).show();
            }

            return true;
        });

    }
}
