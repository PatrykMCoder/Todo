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
import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.databinding.FragmentAddNewTodoBinding;
import com.pmprogramms.todo.helpers.input.HideKeyboard;
import com.pmprogramms.todo.helpers.view.HideAppBarHelper;
import com.pmprogramms.todo.helpers.view.TagsHelper;
import com.pmprogramms.todo.api.retrofit.todo.todo.save.JSONHelperSaveTodo;
import com.pmprogramms.todo.utils.text.Messages;
import com.pmprogramms.todo.view.dialogs.SelectTodoTagDialog;
import com.pmprogramms.todo.viewmodel.TodoNoteViewModel;


import java.util.ArrayList;
import java.util.HashMap;

public class AddNewTodoFragment extends Fragment implements View.OnClickListener {

    private String userToken;

    private EditText newTaskEditText;
    private MainActivity mainActivity;
    private String stringColor = "#ffffff";
    private int createdElement = 0;

    private FragmentAddNewTodoBinding fragmentAddNewTodoBinding;

    private ProgressBar progressBar;

    private TodoNoteViewModel todoNoteViewModel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;

        new HideAppBarHelper(mainActivity).hideBar();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        userToken = AddNewTodoFragmentArgs.fromBundle(getArguments()).getUserToken();
        fragmentAddNewTodoBinding = FragmentAddNewTodoBinding.inflate(inflater);

        todoNoteViewModel = new ViewModelProvider(this).get(TodoNoteViewModel.class);

        fragmentAddNewTodoBinding.saveTodo.setOnClickListener(this);
        fragmentAddNewTodoBinding.setTag.setOnClickListener(this);
        fragmentAddNewTodoBinding.boxNewItem.setOnClickListener(this);

        fragmentAddNewTodoBinding.colorsMenu.defaultColor.setOnClickListener(this);
        fragmentAddNewTodoBinding.colorsMenu.pastel1.setOnClickListener(this);
        fragmentAddNewTodoBinding.colorsMenu.pastel2.setOnClickListener(this);
        fragmentAddNewTodoBinding.colorsMenu.pastel3.setOnClickListener(this);
        fragmentAddNewTodoBinding.colorsMenu.pastel4.setOnClickListener(this);
        fragmentAddNewTodoBinding.colorsMenu.pastel5.setOnClickListener(this);

        createElements();
        return fragmentAddNewTodoBinding.getRoot();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.save_todo) {
            new HideKeyboard(view, mainActivity).hide();
            saveTodo();
        } else if (id == R.id.set_tag) {
            DialogFragment dialogFragment = new SelectTodoTagDialog();
            dialogFragment.show(mainActivity.getSupportFragmentManager(), "set tag todo");
        } else if (id == R.id.box_new_item) {
            createElements();
        } else if (id == R.id.default_color) {
            stringColor = "#ffffff";
        } else if (id == R.id.pastel1) {
            stringColor = "#FFFFBA";
        } else if (id == R.id.pastel2) {
            stringColor = "#FFDBF8";
        } else if (id == R.id.pastel3) {
            stringColor = "#FFE5E7";
        } else if (id == R.id.pastel4) {
            stringColor = "#DFEDFA";
        } else if (id == R.id.pastel5) {
            stringColor = "#D7D4D7";
        }

        fragmentAddNewTodoBinding.main.setBackgroundColor(Color.parseColor(stringColor));
    }

    private void createElements() {
        LinearLayout linearLayout = new LinearLayout(requireContext());
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        CheckBox checkBoxDone = new CheckBox(requireContext());
        newTaskEditText = new EditText(requireContext());
        newTaskEditText.setHint("Enter task");
        newTaskEditText.setBackgroundColor(Color.TRANSPARENT);
        newTaskEditText.setTextSize(20);
        newTaskEditText.setMaxLines(1);
        newTaskEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        newTaskEditText.setTextColor(Color.BLACK);
        newTaskEditText.requestFocus();
        newTaskEditText.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                this.createElements();
                return true;
            }
            return false;
        });

        checkBoxDone.setTag("c_" + createdElement);
        newTaskEditText.setTag("t_" + createdElement);

        linearLayout.addView(checkBoxDone);
        linearLayout.addView(newTaskEditText);

        fragmentAddNewTodoBinding.box.addView(linearLayout);
        createdElement++;
    }

    private void saveTodo() {
        int tmp = createdElement;
        progressBar = new ProgressBar(requireContext(), null, android.R.attr.progressBarStyle);
        fragmentAddNewTodoBinding.getRoot().addView(progressBar);
        progressBar.setVisibility(View.VISIBLE);
        ArrayList<JSONHelperSaveTodo> saveTodoData = new ArrayList<>();
        String title = fragmentAddNewTodoBinding.newTitleTodo.getText().toString().trim();
        String tag = TagsHelper.getTag();
        if (!title.isEmpty()) {
            for (int i = 0; i < createdElement; i++) {
                newTaskEditText = fragmentAddNewTodoBinding.getRoot().findViewWithTag("t_" + i);
                CheckBox checkBoxDone = fragmentAddNewTodoBinding.getRoot().findViewWithTag("c_" + i);
                String task = newTaskEditText.getText().toString().trim();
                boolean done = checkBoxDone.isChecked();

                if (!task.isEmpty()) {
                    JSONHelperSaveTodo helper = new JSONHelperSaveTodo(task, done);
                    saveTodoData.add(helper);
                } else
                    tmp--;
            }

            if (tmp > 0) {
                HashMap<String, String> map = new HashMap<>();
                map.put("title", title);
                map.put("color", stringColor);
                map.put("todos", new Gson().toJson(saveTodoData, new TypeToken<ArrayList<JSONHelperSaveTodo>>() {
                }.getType()));
                map.put("tag", tag);

                todoNoteViewModel.createTodo(map, userToken).observe(getViewLifecycleOwner(), code -> {
                    progressBar.setVisibility(View.GONE);
                    if (code == 200 || code == 201)
                        Navigation.findNavController(fragmentAddNewTodoBinding.getRoot()).navigate(R.id.todoFragment);
                    else
                        new Messages(requireContext()).showMessage("Something wrong, try again");
                });
            }

        } else {
            progressBar.setVisibility(View.GONE);
            new Messages(requireContext()).showMessage("Title cannot be empty");
        }
    }
}
