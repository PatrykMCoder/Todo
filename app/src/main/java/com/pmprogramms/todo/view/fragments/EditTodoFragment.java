package com.pmprogramms.todo.view.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pmprogramms.todo.api.retrofit.todo.todo.save.JSONHelperSaveTodo;
import com.pmprogramms.todo.api.retrofit.todo.todo.Todos;
import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.databinding.FragmentEditTodoBinding;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.helpers.input.HideKeyboard;
import com.pmprogramms.todo.api.retrofit.todo.todo.edit.JSONHelperEditTodo;
import com.pmprogramms.todo.utils.text.Messages;
import com.pmprogramms.todo.view.dialogs.SelectTodoTagDialog;
import com.pmprogramms.todo.viewmodel.TodoNoteViewModel;

import java.util.ArrayList;
import java.util.HashMap;


public class EditTodoFragment extends Fragment implements View.OnClickListener {
    private EditText taskEditText;
    private CheckBox doneCheckBox;
    private MainActivity mainActivity;
    private Context context;

    private String userToken, todoID, tag;
    private int color;
    private String newColor;
    private int tmpPosition;

    private FragmentEditTodoBinding fragmentEditTodoBinding;
    private TodoNoteViewModel todoNoteViewModel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        mainActivity = (MainActivity) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentEditTodoBinding = FragmentEditTodoBinding.inflate(inflater);
        todoNoteViewModel = new ViewModelProvider(this).get(TodoNoteViewModel.class);
        loadData(EditTodoFragmentArgs.fromBundle(getArguments()));

        fragmentEditTodoBinding.container.setBackgroundColor(color);
        fragmentEditTodoBinding.colorsMenu.defaultColor.setOnClickListener(this);
        fragmentEditTodoBinding.colorsMenu.pastel1.setOnClickListener(this);
        fragmentEditTodoBinding.colorsMenu.pastel2.setOnClickListener(this);
        fragmentEditTodoBinding.colorsMenu.pastel3.setOnClickListener(this);
        fragmentEditTodoBinding.colorsMenu.pastel4.setOnClickListener(this);
        fragmentEditTodoBinding.colorsMenu.pastel5.setOnClickListener(this);
        fragmentEditTodoBinding.saveTodo.setOnClickListener(this);
        fragmentEditTodoBinding.setTag.setOnClickListener(this);
        fragmentEditTodoBinding.boxNewItem.setOnClickListener(this);

        return fragmentEditTodoBinding.getRoot();
    }

    private void loadData(EditTodoFragmentArgs args) {
        userToken = new UserData(requireContext()).getUserToken();
        todoID = args.getEditTodoFields().getDataTodo()._id;
        tag = args.getEditTodoFields().getDataTodo().tag;
        color = Color.parseColor(args.getEditTodoFields().getDataTodo().color);
        newColor = String.format("#%06X", (0xFFFFFF & color));

        int index = 0;
        for (Todos data : args.getEditTodoFields().getDataTodo().todos) {
            createElementsWithData(data, index);
            index++;
        }
        fragmentEditTodoBinding.titleEdit.setText(args.getEditTodoFields().getDataTodo().title);
        tmpPosition = args.getEditTodoFields().getDataTodo().todos.size();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.save_todo) {
            new HideKeyboard(view, mainActivity).hide();
            updateTodo();
        } else if (id == R.id.box_new_item) {
            createElement();
        } else if (id == R.id.set_tag) {
            DialogFragment dialogFragment = new SelectTodoTagDialog();
            dialogFragment.show(mainActivity.getSupportFragmentManager(), "set tag todo");
        } else if (id == R.id.default_color) {
            newColor = "#ffffff";
        } else if (id == R.id.pastel1) {
            newColor = "#FFFFBA";
        } else if (id == R.id.pastel2) {
            newColor = "#FFDBF8";
        } else if (id == R.id.pastel3) {
            newColor = "#FFE5E7";
        } else if (id == R.id.pastel4) {
            newColor = "#DFEDFA";
        } else if (id == R.id.pastel5) {
            newColor = "#D7D4D7";
        }

        fragmentEditTodoBinding.container.setBackgroundColor(Color.parseColor(newColor));
    }

    private void createElementsWithData(Todos data, int position) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        doneCheckBox = new CheckBox(context);
        taskEditText = new EditText(context);
        taskEditText.setBackgroundColor(Color.TRANSPARENT);
        taskEditText.setHint("Enter task");
        taskEditText.setTextSize(20);
        taskEditText.setMaxLines(1);
        taskEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        taskEditText.setTag("t_" + position);
        doneCheckBox.setTag("d_" + position);
        taskEditText.setText(data.task);
        doneCheckBox.setChecked(data.done);

        taskEditText.setBackgroundColor(Color.TRANSPARENT);

        taskEditText.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                this.createElement();
                return true;
            }
            return false;
        });

        linearLayout.addView(doneCheckBox);
        linearLayout.addView(taskEditText);

        fragmentEditTodoBinding.box.addView(linearLayout);
    }

    private void createElement() {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        doneCheckBox = new CheckBox(context);
        taskEditText = new EditText(context);
        taskEditText.setBackgroundColor(Color.TRANSPARENT);
        taskEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        taskEditText.setTextSize(20);
        taskEditText.setMaxLines(1);
        taskEditText.requestFocus();
        taskEditText.setHint("Enter task");
        taskEditText.setTag("t_" + tmpPosition);
        doneCheckBox.setTag("d_" + tmpPosition);

        taskEditText.setBackgroundColor(Color.TRANSPARENT);

        taskEditText.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                this.createElement();
                return true;
            }
            return false;
        });

        linearLayout.addView(doneCheckBox);
        linearLayout.addView(taskEditText);
        fragmentEditTodoBinding.box.addView(linearLayout);

        tmpPosition++;
    }

    private void updateTodo() {
        ProgressBar progressBar = new ProgressBar(requireContext(), null, android.R.attr.progressBarStyle);
        fragmentEditTodoBinding.getRoot().addView(progressBar);
        progressBar.setVisibility(View.VISIBLE);
        String title = fragmentEditTodoBinding.titleEdit.getText().toString();

        EditText editText;
        CheckBox checkBox;
        JSONHelperEditTodo helper;
        ArrayList<JSONHelperEditTodo> todoListDataHelper = new ArrayList<>();
        for (int i = 0; i < tmpPosition; i++) {
            editText = fragmentEditTodoBinding.getRoot().findViewWithTag("t_" + i);
            checkBox = fragmentEditTodoBinding.getRoot().findViewWithTag("d_" + i);

            if (!editText.getText().toString().trim().isEmpty()) {
                helper = new JSONHelperEditTodo(editText.getText().toString().trim(), checkBox.isChecked());
                todoListDataHelper.add(helper);
            }
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("title", title);
        map.put("tag", tag);
        map.put("color", newColor);
        map.put("todos", new Gson().toJson(todoListDataHelper, new TypeToken<ArrayList<JSONHelperSaveTodo>>() {
        }.getType()));

        todoNoteViewModel.editTodo(todoID, map, userToken).observe(getViewLifecycleOwner(), code -> {
            if (code == 200 || code == 201)
                Navigation.findNavController(fragmentEditTodoBinding.getRoot()).navigate(R.id.todoFragment);
            else
                new Messages(context).showMessage("Something wrong, try again");
        });
        progressBar.setVisibility(View.GONE);
    }
}
