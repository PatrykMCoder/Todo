package com.example.todo.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.todo.MainActivity;
import com.example.todo.R;
import com.example.todo.utils.setteings.Settings;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {
    private Context context;
    private MainActivity mainActivity;

    public SettingsFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        mainActivity = (MainActivity)context;
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
        mainActivity.closeFragment(this, new TodoFragment(context));

    }
}
