package com.pmprogramms.todo.view.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
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

import com.pmprogramms.todo.API.APIClient;
import com.pmprogramms.todo.API.jsonhelper.JSONHelperCustomTags;
import com.pmprogramms.todo.API.taskstate.TaskState;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.helpers.view.TagsHelper;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.utils.text.Messages;

import java.util.ArrayList;

public class SelectTodoTagDialog extends DialogFragment {
    private Context context;
    private ArrayList<String> tags;
    private ArrayAdapter<String> adapterSelect;
    private ArrayList<JSONHelperCustomTags> helperLoadCustomTagsArrayList;

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
            APIClient APIClient = new APIClient();
            helperLoadCustomTagsArrayList = APIClient.loadUserTags(new UserData(context).getUserID());
            if (helperLoadCustomTagsArrayList != null)
                return TaskState.DONE;
            else
                return TaskState.NOT_DONE;
        }

        @Override
        protected void onPostExecute(TaskState taskState) {
            super.onPostExecute(taskState);
            switch (taskState) {
                case DONE: {
                    tags = new ArrayList<>();
                    for (JSONHelperCustomTags tag : helperLoadCustomTagsArrayList) {
                        tags.add(tag.tag_name);
                    }
                    adapterSelect = new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, tags);
                    selectTags.setAdapter(adapterSelect);
                    break;
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
            APIClient APIClient = new APIClient();
            int code = APIClient.createUserCustomTag(userID, tag);

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
                    new Messages(context).showMessage("Set now your tag 😄");
                    show(fragmentManager, "set custom tag");
                    break;
                }
                case NOT_DONE: {
                    new Messages(context).showMessage("Cannot create tag, try again");
                    break;
                }
            }
        }
    }
}
