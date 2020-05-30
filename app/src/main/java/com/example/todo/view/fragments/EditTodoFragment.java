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
import com.example.todo.utils.formats.StringFormater;
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

    public EditTodoFragment() {

    }

    EditTodoFragment(String title) {
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
        }
    }

    private void loadData() {
        TodoAdapter todoAdapter = new TodoAdapter(getContext(), title);
        dataHelper = todoAdapter.loadAllData();

        for (int i = 0; i < dataHelper.size(); i++)
            createElementsWithData(i);

        tmpPosition = dataHelper.size();
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
        taskEditText.setTag("t_" + position);
        doneCheckBox.setTag("d_" + position);
        taskEditText.setText(dataHelper.get(position).getTask().replace("'", ""));
        doneCheckBox.setChecked(dataHelper.get(position).getDone() == 1);

        linearLayout.addView(doneCheckBox);
        linearLayout.addView(taskEditText);

        box.addView(linearLayout);
    }

    private void createElement() {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        doneCheckBox = new CheckBox(context);
        taskEditText = new EditText(context);
        taskEditText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        taskEditText.setBackgroundColor(Color.WHITE);
        taskEditText.setTextSize(20);
        taskEditText.requestFocus();
        taskEditText.setMaxLines(1);

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

        ArrayList<EditTodoHelper> dataHelper = new ArrayList<>();
        EditTodoHelper editTodoHelper;

        for (int i = 0; i < tmpPosition; i++) {
            editText = rootView.findViewWithTag("t_" + i);
            checkBox = rootView.findViewWithTag("d_" + i);

            editTodoHelper = new EditTodoHelper(editText.getText().toString(), checkBox.isChecked() ? 1 : 0, TagsHelper.getTag(), getCurrentDateString());

            dataHelper.add(editTodoHelper);

        }

        TodoAdapter todoAdapter = new TodoAdapter(getContext());
        todoAdapter.editTodo(new StringFormater(titleEditText.getText().toString()).formatTitle(), dataHelper);

        mainActivity.closeFragment(this, new TodoFragment());
    }

    private String getCurrentDateString() {
        Calendar calendar = Calendar.getInstance();
        return SimpleDateFormat.getDateTimeInstance().format(calendar.getTime());
    }
}
