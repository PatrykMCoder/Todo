package com.example.todo.view.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
    private ArrayList<GetDataHelper> dataHelper;
    private FloatingActionButton saveTodoButton;

    private View rootView;

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
        saveTodoButton.setOnClickListener(this );
        titleEditText.setText(title.replace("_", " "));



        loadData();


        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_todo:
                updateTodo();
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

        Log.d(TAG, "createElements: " + taskEditText.getTag());

        box.addView(linearLayout);
    }

    private void updateTodo() {
        ArrayList<EditTodoHelper> helper = new ArrayList<>();
        EditTodoHelper editTodoHelper;
        EditText editText;
        CheckBox checkBox;
        String title = titleEditText.getText().toString().replace(" ", "_");
        for (int i = 0; i < dataHelper.size(); i++) {
            editText = rootView.findViewWithTag("t_" + i);
            checkBox =rootView.findViewWithTag("d_" + i);
            Log.d(TAG, "updateTodo: " + editText.getTag());
            String task = editText.getText().toString();
            int done = (checkBox.isChecked() ? 1 : 0);

            editTodoHelper = new EditTodoHelper(task, done, "", getCurrentDateString());
            helper.add(editTodoHelper);
        }

        TodoAdapter todoAdapter = new TodoAdapter(getContext());
        todoAdapter.openDB();
        todoAdapter.editTodo(title, helper);
        todoAdapter.closeDB();

        mainActivity.closeFragment(this, new TodoFragment());
    }

    private String getCurrentDateString() {
        String date = "";
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM hh:mm");
        date = simpleDateFormat.format(calendar.getTime());
        return date;
    }
}
