package com.example.todo.view.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.todo.R;
import com.example.todo.helpers.LoadTagsHelper;
import com.example.todo.helpers.TagsHelper;

import java.util.ArrayList;
import java.util.Arrays;

public class SelectTodoTagFragment extends DialogFragment {
    private Context context;
    private ArrayList<String> tags;
    private ArrayAdapter<String> adapterSelect;

    private Spinner selectTags;

    SelectTodoTagFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void loadTags() {
        ArrayList<String> tags = new LoadTagsHelper(context).loadTags();

        adapterSelect = new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, tags);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        loadTags();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.dialog_select_tag_todo, null);

        selectTags = view.findViewById(R.id.tags_select);
        selectTags.setAdapter(adapterSelect);

        builder.setView(view)

                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TagsHelper.setTag(selectTags.getSelectedItem().toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNeutralButton("Add custom", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addCustomTag(getFragmentManager());
                    }
                });

        return builder.create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void addCustomTag(FragmentManager fragmentManager) {
        EditText input = new EditText(context);
        input.setSingleLine();
        input.setHint("Tag");

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Add custom tag");
        builder.setView(input);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String tag = input.getText().toString();
                        SharedPreferences sharedPreferences = context.getSharedPreferences("custom_tags", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putString(tag.replace(" ", "_"), tag);

                        editor.apply();
                        Toast.makeText(context, "Set now your tag 😄", Toast.LENGTH_LONG).show();

                        show(fragmentManager, "set custom tag");
                    }
                });

        builder.create().show();
    }
}
