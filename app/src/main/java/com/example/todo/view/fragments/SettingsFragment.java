package com.example.todo.view.fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.example.todo.MainActivity;
import com.example.todo.R;

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
