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
import androidx.lifecycle.ViewModelProvider;

import com.pmprogramms.todo.API.retrofit.API;
import com.pmprogramms.todo.API.retrofit.Client;
import com.pmprogramms.todo.API.retrofit.customTags.JsonHelperTag;
import com.pmprogramms.todo.API.retrofit.customTags.TagsData;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.databinding.DialogSelectTagTodoBinding;
import com.pmprogramms.todo.helpers.view.TagsHelper;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.utils.text.Messages;
import com.pmprogramms.todo.viewmodel.TodoNoteViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectTodoTagDialog extends DialogFragment {
    private Context context;
    private TodoNoteViewModel todoNoteViewModel;
    private Spinner selectTags;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void loadTags() {
        String userToken = new UserData(requireContext()).getUserToken();

        todoNoteViewModel.getAllTags(userToken).observe(this, tagsData -> {
            setAdapterSelect(tagsData.data);
        });
    }

    private void setAdapterSelect(ArrayList<TagsData> data) {
        ArrayAdapter<String> adapterSelect = new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item,
                data.stream().map(tag -> tag.tag_name).collect(Collectors.toList()));

        selectTags.setAdapter(adapterSelect);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        todoNoteViewModel = new ViewModelProvider(this).get(TodoNoteViewModel.class);
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

        builder.setTitle("Add custom tag")
                .setView(input)
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                    show(fragmentManager, "set custom tag");
                })
                .setPositiveButton("Save", (dialog, which) -> {
                    String userToken = new UserData(context).getUserToken();
                    String tag = input.getText().toString();
                    if (!tag.equals("")) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("tag_name", tag);
                        todoNoteViewModel.createCustomTag(map, userToken);
                    } else {
                        Toast.makeText(context, "Tag can't be empty", Toast.LENGTH_SHORT).show();
                    }
                    show(fragmentManager, "set custom tag");
                });
        builder.create().show();
    }
}
