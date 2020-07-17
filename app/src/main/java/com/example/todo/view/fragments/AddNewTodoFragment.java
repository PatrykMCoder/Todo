package com.example.todo.view.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.todo.MainActivity;
import com.example.todo.R;
import com.example.todo.helpers.todohelper.CreateTodoHelper;
import com.example.todo.helpers.view.HideAppBarHelper;
import com.example.todo.helpers.todohelper.tags.TagsHelper;
import com.example.todo.API.MongoDBClient;
import com.example.todo.API.jsonhelper.JSONHelperSaveTodo;
import com.example.todo.API.taskstate.TaskState;
import com.example.todo.utils.Messages;
import com.example.todo.view.dialogs.SelectTodoTagDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AddNewTodoFragment extends Fragment implements View.OnClickListener {

    private final static String TAG = "AddNewTodoFragment";
    private String userID;
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
    private boolean done;
    private int createdElement = 0;
    private View rootView;

    private ProgressDialog progressDialog;

    private CreateTodoHelper createTodoHelper;

    private LinearLayout linearLayout;
    private ArrayList<JSONHelperSaveTodo> saveTodoData;

    public AddNewTodoFragment() {
    }

    public AddNewTodoFragment(String userID) {
        this.userID = userID;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
        mainActivity = (MainActivity) context;

        new HideAppBarHelper(mainActivity).hideBar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_add_new_todo, container, false);

        newTitleEditText = rootView.findViewById(R.id.new_title_todo);
        saveTodoButton = rootView.findViewById(R.id.save_todo);
        setTagButton = rootView.findViewById(R.id.set_tag);
        box = rootView.findViewById(R.id.box);
        linearLayout = rootView.findViewById(R.id.box_new_item);

        saveTodoButton.setOnClickListener(this);
        setTagButton.setOnClickListener(this);
        linearLayout.setOnClickListener(this);
        createElements();
        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_todo: {
                saveTodo();
                break;
            }
            case R.id.set_tag: {
                DialogFragment dialogFragment = new SelectTodoTagDialog();
                dialogFragment.show(mainActivity.getSupportFragmentManager(), "set tag todo");
                break;
            }
            case R.id.box_new_item: {
                createElements();
                break;
            }
        }
    }

    private void createElements() {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);


        checkBoxDone = new CheckBox(context);
        newTaskEditText = new EditText(context);
        newTaskEditText.setId(createdElement);
        newTaskEditText.setHint("Enter task");
        newTaskEditText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        newTaskEditText.setBackgroundColor(Color.WHITE);
        newTaskEditText.setTextSize(20);
        newTaskEditText.setMaxLines(1);
        newTaskEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        newTaskEditText.setTextColor(Color.BLACK);
        newTaskEditText.requestFocus();

        checkBoxDone.setTag("c_" + createdElement);
        newTaskEditText.setTag("t_" + createdElement);

        linearLayout.addView(checkBoxDone);
        linearLayout.addView(newTaskEditText);

        box.addView(linearLayout);
        createdElement++;

    }

    private String getCurrentDateString() {
        Calendar calendar = Calendar.getInstance();
        return SimpleDateFormat.getDateTimeInstance().format(calendar.getTime());
    }

    private void saveTodo() {
        progressDialog = ProgressDialog.show(context, "Save...", "Please wait..");
        saveTodoData = new ArrayList<>();
        title = newTitleEditText.getText().toString().trim();
        tag = TagsHelper.getTag();
        if (!title.isEmpty()) {
            for (int i = 0; i < createdElement; i++) {
                newTaskEditText = rootView.findViewWithTag("t_" + i);
                checkBoxDone = rootView.findViewWithTag("c_" + i);
                task = newTaskEditText.getText().toString();
                done = checkBoxDone.isChecked();

                JSONHelperSaveTodo helper = new JSONHelperSaveTodo(task, done);
                saveTodoData.add(helper);
            }

            SaveTodoAsync saveTodoAsync = new SaveTodoAsync();
            saveTodoAsync.execute();
        } else {
            progressDialog.dismiss();
            Snackbar.make(rootView, "Title can not be empty", Snackbar.LENGTH_SHORT).show();
        }
    }

    class SaveTodoAsync extends AsyncTask<String, String, TaskState> {

        @Override
        protected TaskState doInBackground(String... strings) {
            MongoDBClient mongoDBClient = new MongoDBClient();
            int code = mongoDBClient.createNewTodo(userID, title, saveTodoData, tag);

            if (code == 200 || code == 201)
                return TaskState.DONE;

            return TaskState.NOT_DONE;
        }

        @Override
        protected void onPostExecute(TaskState state) {
            super.onPostExecute(state);
            progressDialog.dismiss();

            switch (state) {
                case DONE: {
                    mainActivity.closeFragment(AddNewTodoFragment.this, new TodoFragment());
                    break;
                }
                case NOT_DONE: {
                    new Messages(context).showMessage("Cannot save todo, try again");
                    break;
                }
            }
        }
    }
}
