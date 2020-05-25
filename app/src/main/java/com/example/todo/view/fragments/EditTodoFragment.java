package com.example.todo.view.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.example.todo.MainActivity;
import com.example.todo.R;
import com.example.todo.database.TodoAdapter;
import com.example.todo.helpers.EditTodoHelper;
import com.example.todo.helpers.GetDataHelper;
import com.example.todo.helpers.TagsHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class EditTodoFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "EditTODO";
    private int id;
    private EditText titleEditText;
    private EditText taskEditText;
    private CheckBox doneCheckBox;
    private LinearLayout box;
    private MainActivity mainActivity;
    private Context context;
    private String title, task;
    private int done;
    private ArrayList<GetDataHelper> dataHelper;
    private FloatingActionButton saveTodoButton;

    private View rootView;
    private LinearLayout l;

    private int tmpPosition;
    private int oldSize;

    private ArrayList<EditTodoHelper> editTodoHelpers;
    private ArrayList<EditTodoHelper> editTodoHelpers2;

    public EditTodoFragment() {

    }

    public EditTodoFragment(String title) {
        this.title = title;
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
        box = rootView.findViewById(R.id.box);
        titleEditText = rootView.findViewById(R.id.title_edit);
        saveTodoButton = rootView.findViewById(R.id.save_todo);
        saveTodoButton.setOnClickListener(this);
        titleEditText.setText(title.replace("_", " "));
        l = rootView.findViewById(R.id.box_new_item);
        l.setOnClickListener(this);

        loadData();
        editTodoHelpers = new ArrayList<>();
        copyDataToOtherArray();
        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_todo: {
                updateTodo();
                break;
            }
            case R.id.box_new_item: {
                createElement();
                break;
            }
            default:
                break;
        }
    }

    private void loadData() {
        TodoAdapter todoAdapter = new TodoAdapter(getContext());
        todoAdapter.openDB();
        dataHelper = todoAdapter.loadAllData(title);
        todoAdapter.closeDB();

        for (int i = 0; i < dataHelper.size(); i++)
            createElements(i);

        tmpPosition = dataHelper.size();
        oldSize = dataHelper.size();
    }

    private void createElements(int position) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        doneCheckBox = new CheckBox(context);
        taskEditText = new EditText(context);
        taskEditText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        taskEditText.setBackgroundColor(Color.WHITE);
        taskEditText.setTextSize(20);
        taskEditText.setTag("t_" + position);
        doneCheckBox.setTag("d_" + position);
        taskEditText.setText(dataHelper.get(position).getTask().replace("'", ""));
        doneCheckBox.setChecked(dataHelper.get(position).getDone() == 1);

        linearLayout.addView(doneCheckBox);
        linearLayout.addView(taskEditText);

        box.addView(linearLayout);
    }

    private void createElement() {
        if (tmpPosition != editTodoHelpers.size()) {
            task = taskEditText.getText().toString();
            done = doneCheckBox.isChecked() ? 1 : 0;

            EditTodoHelper editTodoHelper = new EditTodoHelper(task, done, getTag(), getCurrentDateString());
            editTodoHelpers.add(editTodoHelper);
        }

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        doneCheckBox = new CheckBox(context);
        taskEditText = new EditText(context);
        taskEditText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        taskEditText.setBackgroundColor(Color.WHITE);
        taskEditText.setTextSize(20);

        taskEditText.setTag("t_" + tmpPosition);
        doneCheckBox.setTag("d_" + tmpPosition);

        linearLayout.addView(doneCheckBox);
        linearLayout.addView(taskEditText);
        box.addView(linearLayout);

        tmpPosition++;
    }

    private void updateTodo() {
        EditText editText;
        CheckBox checkBox;

        task = taskEditText.getText().toString();
        done = doneCheckBox.isChecked() ? 1 : 0;
        EditTodoHelper editTodoHelper = new EditTodoHelper(task, done, getTag(), getCurrentDateString());
        editTodoHelpers.add(editTodoHelper);

        editTodoHelpers2 = new ArrayList<>();

        for (int i = 0; i < editTodoHelpers.size(); i++) {
            editText = rootView.findViewWithTag("t_" + i);
            checkBox = rootView.findViewWithTag("d_" + i);

            editTodoHelper = new EditTodoHelper(editText.getText().toString(), checkBox.isChecked() ? 1 : 0, TagsHelper.getTag(), getCurrentDateString());

            editTodoHelpers2.add(editTodoHelper);

        }

        String title = titleEditText.getText().toString().replace(" ", "_");
        TodoAdapter todoAdapter = new TodoAdapter(getContext());
        todoAdapter.openDB();
        todoAdapter.editTodo(title, editTodoHelpers2, oldSize);
        todoAdapter.closeDB();

        mainActivity.closeFragment(this, new TodoFragment());
    }

    private void copyDataToOtherArray() {
        for (int i = 0; i < dataHelper.size(); i++) {
            String task = dataHelper.get(i).getTask();
            int done = dataHelper.get(i).getDone();
            String tag = dataHelper.get(i).getTag();
            EditTodoHelper editTodoHelper = new EditTodoHelper(task, done, tag, getCurrentDateString());
            editTodoHelpers.add(editTodoHelper);
        }
    }

    private void deleteDuplicate() {
        // TODO: 24/05/2020 fix it

    }

    private String getCurrentDateString() {
        String date = "";
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM hh:mm");
        date = simpleDateFormat.format(calendar.getTime());
        return date;
    }
}
