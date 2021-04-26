package com.pmprogramms.todo.view.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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

import com.pmprogramms.todo.API.retrofit.API;
import com.pmprogramms.todo.API.retrofit.Client;
import com.pmprogramms.todo.API.retrofit.customTags.JsonHelperTag;
import com.pmprogramms.todo.API.retrofit.customTags.TagsData;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.helpers.view.TagsHelper;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.utils.text.Messages;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectTodoTagDialog extends DialogFragment {
    private Context context;
    private ArrayList<String> tags;
    private ArrayAdapter<String> adapterSelect;

    private Spinner selectTags;

    public SelectTodoTagDialog() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void loadTags() {
        API api = Client.getInstance().create(API.class);
        Call<JsonHelperTag> call = api.loadCustomTags(new UserData(context).getUserToken());
        call.enqueue(new Callback<JsonHelperTag>() {
            @Override
            public void onResponse(Call<JsonHelperTag> call, Response<JsonHelperTag> response) {
                if (!response.isSuccessful()) {
                    new Messages(context).showMessage(response.message());
                }
                JsonHelperTag responseBody = response.body();
                if (responseBody != null) {
                    tags = new ArrayList<>();
                    for (TagsData tag : responseBody.data) {
                        tags.add(tag.tag_name);
                    }
                    adapterSelect = new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, tags);
                    selectTags.setAdapter(adapterSelect);
                } else {
                    new Messages(context).showMessage("No tags");
                }
            }

            @Override
            public void onFailure(Call<JsonHelperTag> call, Throwable t) {
                new Messages(context).showMessage(t.getMessage());
            }
        });
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        loadTags();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.dialog_select_tag_todo, null);

        selectTags = view.findViewById(R.id.tags_select);

        builder.setView(view)
                .setPositiveButton("OK", (dialog, which) -> TagsHelper.setTag(selectTags.getSelectedItem().toString()))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setNeutralButton("Add custom", (dialog, which) -> addCustomTag(getFragmentManager()));

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
        builder
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                    show(fragmentManager, "set custom tag");
                })
                .setPositiveButton("Save", (dialog, which) -> {
                    String tag = input.getText().toString();
                    if (!tag.equals("")) {

                        HashMap<String, String> map = new HashMap<>();
                        map.put("tag_name", tag);

                        API api = Client.getInstance().create(API.class);
                        Call<Void> call = api.createCustomTag(map, new UserData(context).getUserToken());
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (!response.isSuccessful()) {
                                    new Messages(context).showMessage(response.message());
                                }
                                new Messages(context).showMessage("Set now your tag 😄");
                                show(fragmentManager, "set custom tag");
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                new Messages(context).showMessage("Cannot create tag, try again");
                            }
                        });
                    } else {
                        Toast.makeText(context, "Tag can't be empty", Toast.LENGTH_SHORT).show();
                        show(fragmentManager, "set custom tag");
                    }
                });

        builder.create().show();
    }
}
