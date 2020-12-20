package com.pmprogramms.todo.view.fragments;


import android.app.ProgressDialog;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pmprogramms.todo.API.retrofit.API;
import com.pmprogramms.todo.API.retrofit.Client;
import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.helpers.api.SessionHelper;
import com.pmprogramms.todo.helpers.input.HideKeyboard;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.helpers.view.HideAppBarHelper;
import com.pmprogramms.todo.helpers.view.TagsHelper;
import com.pmprogramms.todo.API.retrofit.todo.todo.save.JSONHelperSaveTodo;
import com.pmprogramms.todo.utils.text.Messages;
import com.pmprogramms.todo.view.dialogs.SelectTodoTagDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pmprogramms.todo.view.dialogs.SessionDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNewTodoFragment extends Fragment implements View.OnClickListener {

    private final static String TAG = "AddNewTodoFragment";
    private String userToken;
    private Context context;
    private EditText newTitleEditText;
    private LinearLayout box;
    private CheckBox checkBoxDone;
    private EditText newTaskEditText;
    private FloatingActionButton saveTodoButton;
    private FloatingActionButton setTagButton;
    private MainActivity mainActivity;
    private String title;
    private String task;
    private String tag;
    private String stringColor = "#ffffff";
    private boolean done;
    private int createdElement = 0;
    private View rootView;
    private RelativeLayout relativeLayout;
    private API api;

    private ProgressDialog progressDialog;

    private LinearLayout linearLayout;
    private ArrayList<JSONHelperSaveTodo> saveTodoData;

    //buttons color
    private View colorsViewLayout;
    private ImageButton defaultColorButton, color1Button, color2Button, color3Button, color4Button, color5Button;

    public AddNewTodoFragment() {
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.context = context;
        mainActivity = (MainActivity) context;

        new HideAppBarHelper(mainActivity).hideBar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        api = Client.getInstance().create(API.class);
        userToken = new UserData(context).getUserToken();

        rootView = inflater.inflate(R.layout.fragment_add_new_todo, container, false);
        relativeLayout = rootView.findViewById(R.id.main);
        colorsViewLayout = rootView.findViewById(R.id.colors_layout);
        defaultColorButton = colorsViewLayout.findViewById(R.id.default_color);
        color1Button = colorsViewLayout.findViewById(R.id.pastel1);
        color2Button = colorsViewLayout.findViewById(R.id.pastel2);
        color3Button = colorsViewLayout.findViewById(R.id.pastel3);
        color4Button = colorsViewLayout.findViewById(R.id.pastel4);
        color5Button = colorsViewLayout.findViewById(R.id.pastel5);

        newTitleEditText = rootView.findViewById(R.id.new_title_todo);
        saveTodoButton = rootView.findViewById(R.id.save_todo);
        setTagButton = rootView.findViewById(R.id.set_tag);
        box = rootView.findViewById(R.id.box);
        linearLayout = rootView.findViewById(R.id.box_new_item);

        saveTodoButton.setOnClickListener(this);
        setTagButton.setOnClickListener(this);
        linearLayout.setOnClickListener(this);

        defaultColorButton.setOnClickListener(this);
        color1Button.setOnClickListener(this);
        color2Button.setOnClickListener(this);
        color3Button.setOnClickListener(this);
        color4Button.setOnClickListener(this);
        color5Button.setOnClickListener(this);

        createElements();
        return rootView;
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

        relativeLayout.setBackgroundColor(Color.parseColor(stringColor));
    }

    private void createElements() {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        checkBoxDone = new CheckBox(context);
        newTaskEditText = new EditText(context);
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

        box.addView(linearLayout);
        createdElement++;
    }

    private void saveTodo() {
        int tmp = createdElement;
        progressDialog = ProgressDialog.show(context, "Save...", "Please wait..");
        saveTodoData = new ArrayList<>();
        title = newTitleEditText.getText().toString().trim();
        tag = TagsHelper.getTag();
        if (!title.isEmpty()) {
            for (int i = 0; i < createdElement; i++) {
                newTaskEditText = rootView.findViewWithTag("t_" + i);
                checkBoxDone = rootView.findViewWithTag("c_" + i);
                task = newTaskEditText.getText().toString().trim();
                done = checkBoxDone.isChecked();

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

                Call<Void> call = api.saveTodo(map, userToken);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        progressDialog.cancel();
                        if (!response.isSuccessful()) {
                            SessionHelper sessionHelper = new SessionHelper();
                            try {
                                assert response.errorBody() != null;
                                if (sessionHelper.checkSession(response.errorBody().string())) {
                                    new Messages(context).showMessage("Something wrong, try again");
                                } else {
                                    new UserData(context).removeUserToken();
                                    DialogFragment dialogFragment = new SessionDialog();
                                    dialogFragment.show(getChildFragmentManager(), "session fragment");
                                }
                            } catch (IOException e) {
                                new Messages(context).showMessage("Something wrong, try again");
                                e.printStackTrace();
                            }
                            return;
                        }
                        mainActivity.closeFragment(AddNewTodoFragment.this, new TodoFragment());
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        progressDialog.cancel();
                        new Messages(context).showMessage("Cannot save todo, try again");
                    }
                });
            } else {
                progressDialog.dismiss();
                new Messages(context).showMessage("Todo list cannot be empty");
            }
        } else {
            progressDialog.dismiss();
            new Messages(context).showMessage("Title cannot be empty");
        }
    }
}
