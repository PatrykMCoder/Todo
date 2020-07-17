package com.pmprogramms.todo.view.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.R;
import com.pmprogramms.todo.helpers.view.TagsHelper;
import com.pmprogramms.todo.helpers.input.HideKeyboard;
import com.pmprogramms.todo.API.jsonhelper.JSONHelperEditTodo;
import com.pmprogramms.todo.API.APIClient;
import com.pmprogramms.todo.API.jsonhelper.JSONHelperDataTodo;
import com.pmprogramms.todo.API.taskstate.TaskState;
import com.pmprogramms.todo.utils.text.Messages;
import com.pmprogramms.todo.view.dialogs.SelectTodoTagDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class EditTodoFragment extends Fragment implements View.OnClickListener {

    private ArrayList<JSONHelperDataTodo> arrayData;
    private EditText titleEditText;
    private EditText taskEditText;
    private CheckBox doneCheckBox;
    private LinearLayout box;
    private MainActivity mainActivity;
    private Context context;
    private String title;
    private boolean archive;
    private ArrayList<JSONHelperEditTodo> dataHelper;
    private FloatingActionButton saveTodoButton;
    private FloatingActionButton setTagButton;

    private ProgressDialog progressDialog;

    private View rootView;
    private LinearLayout linearLayout;

    private String userID, todoID, tag;

    private int tmpPosition;

    public EditTodoFragment() {

    }

    public EditTodoFragment(String title, String userID, String todoID, ArrayList<JSONHelperDataTodo> arrayData, String tag, boolean archive) {
        this.title = title;
        this.userID = userID;
        this.todoID = todoID;
        this.arrayData = arrayData;
        this.tag = tag;
        this.archive = archive;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        mainActivity = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_edit_todo, container, false);
        TagsHelper.setTag(null);
        box = rootView.findViewById(R.id.box);
        titleEditText = rootView.findViewById(R.id.title_edit);
        saveTodoButton = rootView.findViewById(R.id.save_todo);
        setTagButton = rootView.findViewById(R.id.set_tag);

        saveTodoButton.setOnClickListener(this);
        setTagButton.setOnClickListener(this);

        titleEditText.setText(title);
        linearLayout = rootView.findViewById(R.id.box_new_item);
        linearLayout.setOnClickListener(this);

        loadData();
        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_todo: {
                new HideKeyboard(view, mainActivity).hide();
                updateTodo();
                break;
            }
            case R.id.box_new_item: {
                createElement();
                break;
            }
            case R.id.set_tag: {
                DialogFragment dialogFragment = new SelectTodoTagDialog();
                dialogFragment.show(mainActivity.getSupportFragmentManager(), "set tag todo");
            }
        }
    }

    private void loadData() {
        for (int i = 0; i < arrayData.size(); i++)
            createElementsWithData(i);

        tmpPosition = arrayData.size();
    }

    private void createElementsWithData(int position) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        doneCheckBox = new CheckBox(context);
        taskEditText = new EditText(context);
        taskEditText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        taskEditText.setBackgroundColor(Color.WHITE);
        taskEditText.setTextSize(20);
        taskEditText.setMaxLines(1);
        taskEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        taskEditText.setTag("t_" + position);
        doneCheckBox.setTag("d_" + position);
        taskEditText.setText(arrayData.get(position).task);
        doneCheckBox.setChecked(arrayData.get(position).done);

        taskEditText.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER){
                this.createElement();
                return true;
            }
            return false;
        });

        linearLayout.addView(doneCheckBox);
        linearLayout.addView(taskEditText);

        box.addView(linearLayout);
    }

    private void createElement() {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        doneCheckBox = new CheckBox(context);
        taskEditText = new EditText(context);
        taskEditText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        taskEditText.setBackgroundColor(Color.WHITE);
        taskEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        taskEditText.setTextSize(20);
        taskEditText.setMaxLines(1);
        taskEditText.requestFocus();
        taskEditText.setHint("Enter task");
        taskEditText.setTag("t_" + tmpPosition);
        doneCheckBox.setTag("d_" + tmpPosition);

        taskEditText.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER){
                this.createElement();
                return true;
            }
            return false;
        });

        linearLayout.addView(doneCheckBox);
        linearLayout.addView(taskEditText);
        box.addView(linearLayout);

        tmpPosition++;
    }

    private void updateTodo() {
        new HideKeyboard(rootView, mainActivity).hide();
        progressDialog = ProgressDialog.show(context, "Update...", "Please wait...");
        title = titleEditText.getText().toString();
        EditText editText;
        CheckBox checkBox;
        JSONHelperEditTodo helper;
        dataHelper = new ArrayList<>();
        for (int i = 0; i < tmpPosition; i++) {
            editText = rootView.findViewWithTag("t_" + i);
            checkBox = rootView.findViewWithTag("d_" + i);
            helper = new JSONHelperEditTodo(editText.getText().toString(), checkBox.isChecked());

            dataHelper.add(helper);
        }

        EditTodoAsync editTodoAsync = new EditTodoAsync();
        editTodoAsync.execute(title);
    }

    class EditTodoAsync extends AsyncTask<String, String, TaskState> {

        @Override
        protected TaskState doInBackground(String... strings) {
            title = strings[0];
            APIClient APIClient = new APIClient();
            if (TagsHelper.getTag() != null)
                tag = TagsHelper.getTag();

            int code = APIClient.editTodo(userID, todoID, title, dataHelper, tag);
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
                    mainActivity.closeFragment(EditTodoFragment.this, new TodoDetailsFragment(userID, todoID, title, archive));
                    TagsHelper.setTag("");
                    break;
                }
                case NOT_DONE: {
                    new Messages(context).showMessage("Cannot update todo, try again");
                    break;
                }
            }
        }
    }
}
