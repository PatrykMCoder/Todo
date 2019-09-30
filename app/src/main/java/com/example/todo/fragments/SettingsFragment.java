package com.example.todo.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.example.todo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {
    private Context context;


    public SettingsFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.containerFragment, new SettingsPreference())
                .commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
