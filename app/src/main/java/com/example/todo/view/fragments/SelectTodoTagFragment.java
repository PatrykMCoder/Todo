package com.example.todo.view.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.todo.R;
import com.example.todo.helpers.TagsHelper;

import java.util.ArrayList;

public class SelectTodoTagFragment extends DialogFragment {
    private Context context;
    private ArrayList<String> tags;
    private ArrayAdapter adapterSelect;

    private Spinner selectTags;

    SelectTodoTagFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void loadTags() {
        adapterSelect = ArrayAdapter.createFromResource(context, R.array.tags, R.layout.support_simple_spinner_dropdown_item);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        loadTags();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.dialog_select_tag_todo, null);

        selectTags = view.findViewById(R.id.tags_select);
        selectTags.setAdapter(adapterSelect);

        builder.setView(view);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TagsHelper.setTag(selectTags.getSelectedItem().toString());
            }
        });

        return builder.create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
