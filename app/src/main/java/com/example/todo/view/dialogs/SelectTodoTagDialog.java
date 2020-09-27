package com.example.todo.view.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.todo.API.MongoDBClient;
import com.example.todo.API.jsonhelper.JSONHelperLoadCustomTags;
import com.example.todo.API.taskstate.TaskState;
import com.example.todo.R;
import com.example.todo.helpers.todohelper.tags.LoadTagsHelper;
import com.example.todo.helpers.todohelper.tags.TagsHelper;
import com.example.todo.helpers.user.UserData;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class SelectTodoTagDialog extends DialogFragment {
    private Context context;
    private ArrayList<String> tags;
    private ArrayAdapter<String> adapterSelect;
    private ArrayList<JSONHelperLoadCustomTags> helperLoadCustomTagsArrayList;

    private FragmentManager fragmentManager;

    private Spinner selectTags;

    public SelectTodoTagDialog() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void loadTags() {
        LoadCustomTagsAsync loadCustomTagsAsync = new LoadCustomTagsAsync();
        loadCustomTagsAsync.execute();
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
        this.fragmentManager = fragmentManager;
        EditText input = new EditText(context);
        input.setSingleLine();
        input.setHint("Tag");

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Add custom tag");
        builder.setView(input);
        builder
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                    show(fragmentManager, "set custom tag");
                })
                .setPositiveButton("Save", (dialog, which) -> {
                    String tag = input.getText().toString();
                    if (!tag.equals("")) {
                        CreateCustomTagAsync createCustomTagAsync = new CreateCustomTagAsync();
                        createCustomTagAsync.execute(new UserData(context).getUserID(), tag);
                    } else {
                        Toast.makeText(context, "Tag can't be empty", Toast.LENGTH_SHORT).show();
                        show(fragmentManager, "set custom tag");
                    }
                });

        builder.create().show();
    }

    class LoadCustomTagsAsync extends AsyncTask<String, String, TaskState> {

        @Override
        protected TaskState doInBackground(String... strings) {
            MongoDBClient mongoDBClient = new MongoDBClient();
            String data = mongoDBClient.loadUserTags(new UserData(context).getUserID());
            if (data != null) {
                Gson gson = new Gson();
                helperLoadCustomTagsArrayList = gson.fromJson(data, new TypeToken<ArrayList<JSONHelperLoadCustomTags>>() {
                }.getType());
                return TaskState.DONE;
            }
            return TaskState.NOT_DONE;
        }

        @Override
        protected void onPostExecute(TaskState taskState) {
            super.onPostExecute(taskState);
            switch (taskState) {
                case DONE: {
                    if (helperLoadCustomTagsArrayList != null) {
                        tags = new ArrayList<>();
                        for (JSONHelperLoadCustomTags tag : helperLoadCustomTagsArrayList) {
                            tags.add(tag.tag_name);
                        }
                        adapterSelect = new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, tags);
                        selectTags.setAdapter(adapterSelect);
                        break;
                    }
                }
                case NOT_DONE: {
                    break;
                }
            }
        }
    }

    class CreateCustomTagAsync extends AsyncTask<String, String, TaskState> {
        @Override
        protected TaskState doInBackground(String... strings) {
            String userID = strings[0];
            String tag = strings[1];
            MongoDBClient mongoDBClient = new MongoDBClient();
            int code = mongoDBClient.createUserCustomTag(userID, tag);

            if (code == 200 || code == 201) {
                return TaskState.DONE;
            }

            return TaskState.NOT_DONE;
        }

        @Override
        protected void onPostExecute(TaskState taskState) {
            super.onPostExecute(taskState);

            switch (taskState) {
                case DONE: {
                    new Handler().post(() -> {
                        Toast.makeText(context, "Set now your tag 😄", Toast.LENGTH_LONG).show();
                    });
                    show(fragmentManager, "set custom tag");
                    break;
                }
                case NOT_DONE: {
                    new Handler().post(() -> {
                        Toast.makeText(context, "Something wrong. try again", Toast.LENGTH_LONG).show();
                    });
                    break;
                }
            }
        }
    }
}
